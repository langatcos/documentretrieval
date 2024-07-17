package com.dmeDocuments.dmeDocuments.services
import jakarta.annotation.PostConstruct
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import java.util.*
import org.springframework.stereotype.Service
import java.sql.SQLException
import java.util.logging.Logger
import javax.sql.DataSource
import kotlin.concurrent.scheduleAtFixedRate

@Service
class AssessmentService {

    @Value("\${ial.server.ip}")
    private lateinit var ialIpServer: String

    @Value("\${ial.token.username}")
    private lateinit var tokenUsername:String

    @Value("\${ial.token.password}")
    private lateinit var tokenPassword:String



    private val logger: Logger = Logger.getLogger(AssessmentService::class.java.name)
    private val client = OkHttpClient()

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
            .url("http://$ialIpServer/iail/auth")
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
            SELECT DISTINCT TOP 2 a.assessmentId as assessment_id, i.InvoiceId as invoice_id, 
            case when i.AdmissionStatus='Out Patient' then 'opd' else 'ipd' end as claimType 
            FROM ClaimAssessment a 
            JOIN claimtreatment t on a.AssessmentId = t.assessmentid 
            JOIN claimtreatmentinvoice i on i.treatmentid = t.treatmentid 
            JOIN claimtreatmentinvoiceline l on i.invoiceid = l.invoiceid
            JOIN indexinfo inx on index9 = CAST(a.assessmentId as varchar(26)) and index11=CAST(i.InvoiceId as varchar(26)) 
            left Join IassistSubmittedClaims ail on ail.assessmentid=a.assessmentid and ail.InvoiceID =i.invoiceid
            WHERE InvoiceStatus = 'Loaded'
            AND InvoiceEntity IN (116808, 116692, 116748, 116760, 116948, 116858, 116932, 148744)
            AND treatmentDate >= '2023-04-01'
            and ail.assessmentId is null and ail.invoiceid is null
            ORDER BY a.assessmentId ASC 
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
            "invoice_id": ${invoiceIds.map { "\"$it\"" }},
            "claim_type": "$claimType"
        }
    """.trimIndent()
        val body = requestJson.toRequestBody(mediaType)

        logger.info("Sending request with body: $requestJson")

        val request = Request.Builder()
            .url("http://$ialIpServer/iail/initiate")
            .post(body)
            .addHeader("Authorization", "JWT $token")
            .addHeader("Content-Type", "application/json")
            .build()

        try {
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            logger.info("Received response: $responseBody")

            val jsonResponse = JSONObject(responseBody)
            if (jsonResponse.has("success")) {
                logger.info("Success response received for assessmentId $assessmentId")
                insertSubmittedClaim(assessmentId, invoiceIds, connection, requestJson)
            } else {
                throw Exception("Claim initiation failed: $responseBody")
            }

            response.close()
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


