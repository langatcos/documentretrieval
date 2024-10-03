package com.dmeDocuments.dmeDocuments.services

import io.swagger.v3.oas.annotations.Operation
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.XML
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.io.File
import java.util.Base64

//@RestController
//@CrossOrigin
class DocumentService(
    @Value("\${dme.soap.service.url}") private val soapServiceUrl: String
) {
    private val logger = org.slf4j.LoggerFactory.getLogger(DocumentService::class.java)

    @Operation(
        summary = "AddDocument",
        description = "This API calls the Actisure Add Document SOAP API and returns the response as XML"
    )
    @PostMapping("/addDocument", produces = [MediaType.APPLICATION_XML_VALUE])
    fun addDocument(
        @RequestParam filePath:String,

        @RequestParam fileExtension: String

    ): String {

      //  val filePath:String ="C:\\Users\\cosmas.lagat\\Documents\\Occupation List.xlsx"
        logger.info("Received Request to Add Document from file path: $filePath")
        val document = File(filePath).readBytes()


        val documentBase64 = Base64.getEncoder().encodeToString(document)
     //   logger.info("Base 64 Byte Array: $documentBase64")

        val soapRequestXML = """
            <soap:Envelope xmlns:soap="http://www.w3.org/2003/05/soap-envelope" xmlns:ns="http://services.activus.com/dme/messagecontracts/2010/02" xmlns:ns1="http://services.activus.com/dme/messagecontracts/2010/06" xmlns:ns2="http://services.activus.com/dme/datacontracts/2010/06" xmlns:ns3="http://services.activus.com/dme/datacontracts/2010/07">
                <soap:Header>
                    <To xmlns="http://www.w3.org/2005/08/addressing">$soapServiceUrl</To>
                    <a:Action xmlns:a="http://www.w3.org/2005/08/addressing" soap:mustUnderstand="1">http://services.activus.com/dme/servicecontracts/2010/02/ActDMEServiceContract/AddDocument</a:Action>
                </soap:Header>
                <soap:Body>
                    <ns:AddDocumentRequestMessage>
                        <ns1:AddDocumentReq>
                            <ns2:Document>${documentBase64}</ns2:Document>
                            <ns2:FileExtension>$fileExtension</ns2:FileExtension>
                            <ns2:DMEIndexList>
                                    <ns3:Act_DMEIndex>
                                    <!--Optional:-->
                                    <ns3:IndexId>1</ns3:IndexId>
                                    <!--Optional:-->
                                    <ns3:IndexName>POLICY NUMBER</ns3:IndexName>
                                    <!--Optional:-->
                                    <ns3:Data>JPR1112344</ns3:Data>
                                    <!--Optional:-->
                                </ns3:Act_DMEIndex>
                                <ns3:Act_DMEIndex>
                                    <!--Optional:-->
                                    <ns3:IndexId>2</ns3:IndexId>
                                    <!--Optional:-->
                                    <ns3:IndexName>SCHEME NAME</ns3:IndexName>
                                    <!--Optional:-->
                                    <ns3:Data>TEST POLICYHOLDERNAME</ns3:Data>
                                    <!--Optional:-->
                                </ns3:Act_DMEIndex>
                                <ns3:Act_DMEIndex>
                                    <!--Optional:-->
                                    <ns3:IndexId>6</ns3:IndexId>
                                    <!--Optional:-->
                                    <ns3:IndexName>POLICY EFFECTIVEDATE</ns3:IndexName>
                                    <!--Optional:-->
                                    <ns3:Data>2024-09-16</ns3:Data>
                                    <!--Optional:-->
                                </ns3:Act_DMEIndex>
                                <ns3:Act_DMEIndex>
                                    <!--Optional:-->
                                    <ns3:IndexId>4</ns3:IndexId>
                                    <!--Optional:-->
                                    <ns3:IndexName>COMMENT</ns3:IndexName>
                                    <!--Optional:-->
                                    <ns3:Data>Comment Test</ns3:Data>
                                    <!--Optional:-->
                                </ns3:Act_DMEIndex>
                            </ns2:DMEIndexList>
                            <ns2:Cabinet>4</ns2:Cabinet>
                            <ns2:StartQueue>Distribution</ns2:StartQueue>
                            <ns2:Priority>B</ns2:Priority>
                        </ns1:AddDocumentReq>
                    </ns:AddDocumentRequestMessage>
                </soap:Body>
            </soap:Envelope>
        """.trimIndent()

        val client = OkHttpClient()
        val mediaType = "application/soap+xml".toMediaType()
        val requestBody = soapRequestXML.toRequestBody(mediaType)

        val request = Request.Builder()
            .url(soapServiceUrl)
            .post(requestBody)
            .addHeader("Content-Type", "application/soap+xml")
            .build()

        val response = client.newCall(request).execute()
        val soapResponseXML = response.body?.string() ?: ""
        logger.info("Response: $soapResponseXML")
        val xmlStart = soapResponseXML.indexOf("<s:Envelope")
        val xmlEnd = soapResponseXML.indexOf("</s:Envelope>") + "</s:Envelope>".length

        val extractedXML = if (xmlStart != -1 && xmlEnd != -1) {
            soapResponseXML.substring(xmlStart, xmlEnd)
        } else {
            ""
        }

        if (extractedXML.isNotEmpty()) {
            val jsonObject = XML.toJSONObject(extractedXML)
            val jsonObjectString = jsonObject.toString(4)
            val successMsg = jsonObject
                .getJSONObject("s:Envelope")
                .getJSONObject("s:Body")
                .getJSONObject("AddDocumentResponseMessage")
                .getJSONObject("AddDocumentResp")
                .getJSONObject("b:ActServiceResult")
                .getBoolean("c:Success")

            val documentReference = jsonObject
                .getJSONObject("s:Envelope")
                .getJSONObject("s:Body")
                .getJSONObject("AddDocumentResponseMessage")
                .getJSONObject("AddDocumentResp")
                .getInt("b:DocumentReference")

            val resultArray = JSONArray()
            if(successMsg){
                val resultObject = JSONObject()
                resultObject.put("documentReference", documentReference)
                resultObject.put("success", successMsg)
                resultArray.put(resultObject)
            }

        //  logger.info("$resultArray")


            logger.info("Document Reference: $documentReference and Message is $successMsg")
        } else {
            logger.info("No valid XML found in the response.")
        }
        //logger.info("SOAP Response: $cleanedXML")
        return soapResponseXML
    }
}
