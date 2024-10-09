package com.dmeDocuments.dmeDocuments.services
import jakarta.annotation.PostConstruct
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import java.util.*
import org.springframework.stereotype.Service
import java.sql.SQLException
import java.util.logging.Logger
import javax.sql.DataSource

import java.util.concurrent.TimeUnit

@Service
class AssessmentService {

    @Value("\${ial.server.ip}")
    private lateinit var ialIpServer: String

    @Value("\${ial.token.username}")
    private lateinit var tokenUsername:String

    @Value("\${ial.token.password}")
    private lateinit var tokenPassword:String



    private val logger: Logger = Logger.getLogger(AssessmentService::class.java.name)
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS) // Set connection timeout
        .writeTimeout(60, TimeUnit.SECONDS)   // Set write timeout
        .readTimeout(60, TimeUnit.SECONDS)    // Set read timeout
        .build()

    @Autowired
    private lateinit var dataSource: DataSource
    @Scheduled(fixedRate = 10 * 60 * 1000) // Run every 2 minutes

    fun processAssessments(): List<AssessmentData> {
     val connection = dataSource.connection
     val assessments = mutableListOf<AssessmentData>()

     try {
         val retrievedAssessments = getAssessments(connection)
         assessments.addAll(retrievedAssessments)
         val assessmentsGroupedById = retrievedAssessments.groupBy { it.assessmentId }

         logger.info("Retrieved assessments: $assessmentsGroupedById")

         val token = retrieveToken()

         for ((assessmentId, invoices) in assessmentsGroupedById) {
             val invoiceIds = invoices.map { it.invoiceId }
             val claimType = invoices.first().claimType
             sendRequest(assessmentId, invoiceIds, claimType, token, connection)
         }
     } catch (e: SQLException) {
         logger.severe("Error retrieving assessments: ${e.message}")
     } finally {
         connection.close()
     }

     return assessments
 }


    fun retrieveToken(): String {
        val mediaType = "application/json".toMediaType()
        val json = """
            {
                "username": "$tokenUsername",
                "password": "$tokenPassword"
            }
        """.trimIndent()
        val body = json.toRequestBody(mediaType)

        val request = Request.Builder()
            .url("https://$ialIpServer/iail/auth")
            .post(body)
            .addHeader("Content-Type", "application/json")
            .build()

        val response = client.newCall(request).execute()
        val responseBody = response.body?.string()
        val token = parseTokenFromResponse(responseBody)



        response.close()


        return token ?: throw IllegalStateException("Token not retrieved")
    }

    fun parseTokenFromResponse(responseBody: String?): String? {
        return try {
            val json = JSONObject(responseBody)
            json.getString("access_token")
        } catch (e: Exception) {
            null
        }
    }


    fun getAssessments(connection: java.sql.Connection): List<AssessmentData> {
        val assessments = mutableListOf<AssessmentData>()
        val query = """
            SELECT * from IassistRetrievedClaims 
        """.trimIndent()

        logger.info("Executing SQL query: $query")

        try {
            val statement = connection.prepareStatement(query)
            val resultSet = statement.executeQuery()

            while (resultSet.next()) {
                val assessmentId = resultSet.getString("assessment_id")
                val invoiceId = resultSet.getString("invoice_id")
                val claimType = resultSet.getString("claimType")
                assessments.add(AssessmentData(assessmentId, invoiceId, claimType))

                logger.info("Fetched row: assessmentId=$assessmentId, invoiceId=$invoiceId, claimType=$claimType")
            }

            resultSet.close()
            statement.close()
        } catch (e: SQLException) {
            logger.severe("SQL error: ${e.message}")
            throw e
        }

        return assessments
    }

    fun sendRequest(assessmentId: String, invoiceIds: List<String>, claimType: String, token: String, connection: java.sql.Connection) {
        val mediaType = "application/json".toMediaType()



        val requestJson = """
        {
            "assessment_id": "$assessmentId",
            "invoice_id": [${invoiceIds.joinToString { "\"$it\"" }}],
            "claim_type": "$claimType"
        }
    """.trimIndent()


        val body = requestJson.toRequestBody(mediaType)

        logger.info("Sending request with body: $requestJson")

        val request = Request.Builder()
            .url("https://$ialIpServer/iail/initiate")
            .post(body)
            .addHeader("Authorization", "JWT $token")
            .addHeader("Content-Type", "application/json")
            .build()
        try {
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            logger.info("Received response: $responseBody")

            if (responseBody.isNullOrEmpty()) {
                logger.severe("Empty response body")
                throw Exception("Empty response from server")
            }

        try {
            val jsonResponse = JSONObject(responseBody)
            val iassistCaseId = jsonResponse.optString("iassist_case_id", "Unknown ID")
            val message = jsonResponse.optString("message", "No message provided")

            logger.info("Parsed iassist_case_id: $iassistCaseId")
            logger.info("Parsed message: $message")

            if (message == "Claim initiated successfully") {
                logger.info("Success response received for assessmentId $assessmentId")
                insertSubmittedClaim(assessmentId, invoiceIds, connection, requestJson)
            } else {
                throw Exception("Claim initiation failed: $message")
            }
        } catch (jsonException: JSONException) {
            logger.severe("Error parsing JSON response: ${jsonException.message}")
            throw Exception("Invalid JSON response format")
        } finally {
            response.close()
        }
    } catch (e: Exception) {
        logger.severe("Error sending request: ${e.message}")
    }
}

    fun insertSubmittedClaim(assessmentId: String, invoiceIds: List<String>, connection: java.sql.Connection, requestJson: String) {
        val insertQuery = "INSERT INTO IassistSubmittedClaims (AssessmentId, InvoiceId, RequestJson, TimeSend) VALUES (?, ?, ?, getDate())"

        try {
            connection.autoCommit = false
            val preparedStatement = connection.prepareStatement(insertQuery)

            // Logging the entire batch
            logger.info("Preparing to insert assessmentId $assessmentId with invoiceIds $invoiceIds and requestJson $requestJson")

            for (invoiceId in invoiceIds) {
                preparedStatement.setString(1, assessmentId)
                preparedStatement.setString(2, invoiceId)
                preparedStatement.setString(3, requestJson)
                preparedStatement.addBatch()

                // Log each entry to be inserted
                logger.info("Adding to batch: assessmentId=$assessmentId, invoiceId=$invoiceId, requestJson=$requestJson")
            }

            val result = preparedStatement.executeBatch()
            connection.commit()
            preparedStatement.close()

            // Log the batch execution result
            logger.info("Batch insert result: ${result.joinToString()}")

            logger.info("Inserted assessmentId $assessmentId and invoiceIds $invoiceIds into IassistSubmittedClaims")
        } catch (e: SQLException) {
            connection.rollback()
            logger.severe("Error inserting into IassistSubmittedClaims: ${e.message}")
            throw e
        } finally {
            connection.autoCommit = true
        }
    }



    data class AssessmentData(val assessmentId: String, val invoiceId: String, val claimType: String)
}


