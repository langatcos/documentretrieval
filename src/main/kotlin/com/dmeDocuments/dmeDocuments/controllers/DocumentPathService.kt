package com.dmeDocuments.dmeDocuments.controllers

import com.dmeDocuments.dmeDocuments.dataclasses.DocumentPath
import com.dmeDocuments.dmeDocuments.repositories.DocumentPathRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.InputStreamResource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.nio.file.Paths

@RestController
@CrossOrigin
@RequestMapping("/api/files")
class DocumentPathService (val documentPathRepository: DocumentPathRepository){
    private val logger = LoggerFactory.getLogger(DocumentPathService::class.java)

    @Value("\${file.storage.path}")
    private lateinit var basePath: String
    @GetMapping("/dmefilePath/{docRef}")
    fun getDocpath(@PathVariable docRef:String):ResponseEntity<List<DocumentPath>>{
        val path=documentPathRepository.getPathByDocRef(docRef)
        return if(path.isNotEmpty()){
            ResponseEntity.ok(path)
        }else{
            ResponseEntity.noContent().build()
        }
    }
    @GetMapping("/downloadDMEFile/{filename}")
    fun downloadFile(@PathVariable filename: String): ResponseEntity<InputStreamResource> {
        val filePathList = documentPathRepository.getPathByDocRef(filename)
        if (filePathList.isNullOrEmpty()) {
            return ResponseEntity.notFound().build()
        }

        val baseFilePath = basePath + filePathList[0].path

        try {
            val file = FileInputStream(baseFilePath)
            val resource = InputStreamResource(file)

            val headers = HttpHeaders()
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=${Paths.get(baseFilePath).fileName}")
            headers.contentType = determineMediaType(baseFilePath)

            return ResponseEntity.ok()
                .headers(headers)
                .body(resource)
        } catch (e: FileNotFoundException) {
            return ResponseEntity.notFound().build()
        }
    }

    // Function to determine MediaType based on file extension
    private fun determineMediaType(filePath: String): MediaType {
        val extension = filePath.substringAfterLast('.', "")
        return when (extension.toLowerCase()) {
            "pdf" -> MediaType.APPLICATION_PDF
            "tif", "tiff" -> MediaType.parseMediaType("image/tiff")
            "png" -> MediaType.IMAGE_PNG
            "jpg", "jpeg" -> MediaType.IMAGE_JPEG
            "msg" -> MediaType.parseMediaType("application/vnd.ms-outlook")
            "doc" -> MediaType.parseMediaType("application/msword")
            "docx" -> MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
            else -> MediaType.APPLICATION_OCTET_STREAM // Default to binary data if extension is unknown
        }
    }




    // Endpoint to get file path based on docRef from the database



}