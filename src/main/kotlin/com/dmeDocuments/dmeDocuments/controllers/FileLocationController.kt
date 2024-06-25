package com.dmeDocuments.dmeDocuments.controllers

import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.core.io.InputStreamResource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.nio.file.Paths

@RestController
@CrossOrigin
@RequestMapping("/api/files")
class FileLocationController {
    @Value("\${file.storage.path}")
    private lateinit var basePath: String

    // Endpoint to get file location based on filename
    @GetMapping("/{filename}")
    fun getFileLocation(@PathVariable filename: String): String {
        val basePath = "100000000" // Replace with your actual root holding folder
        val folderPath = calculateFolderPath(filename)
        return "$basePath/$folderPath/$filename"
    }
    @GetMapping("/download/{filename}")
    fun downloadFile(@PathVariable filename: String): ResponseEntity<InputStreamResource> {
            val pdfFilePath = Paths.get(basePath, calculateFolderPath(filename), "$filename.pdf").toString()
            val tiffFilePath = Paths.get(basePath, calculateFolderPath(filename), "$filename.tif").toString()

            try {
                // Try to open PDF file
                val pdfFile = FileInputStream(pdfFilePath)
                val pdfResource = InputStreamResource(pdfFile)

                val pdfHeaders = HttpHeaders()
                pdfHeaders.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=$filename.pdf")

                return ResponseEntity.ok()
                    .headers(pdfHeaders)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfResource)
            } catch (pdfNotFound: FileNotFoundException) {
                // If PDF file not found, try to open TIFF file
                try {
                    val tiffFile = FileInputStream(tiffFilePath)
                    val tiffResource = InputStreamResource(tiffFile)

                    val tiffHeaders = HttpHeaders()
                    tiffHeaders.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=$filename.tif")

                    return ResponseEntity.ok()
                        .headers(tiffHeaders)
                        .contentType(MediaType.parseMediaType("image/tif"))
                        .body(tiffResource)
                } catch (tiffNotFound: FileNotFoundException) {
                    // If neither file type is found, return 404 Not Found
                    return ResponseEntity.notFound().build()
                }
            }
        }

    // Function to calculate folder path based on filename
    private fun calculateFolderPath(filename: String): String {
        val folder1 = filename.substring(0, 5) + "0000" // Example: First six digits + "00000"
        val folder2 = filename.substring(0, 7) + "00" // Example: First seven digits + "00"
        // Example: Directly using the full filename as the folder

        return "$folder1/$folder2"
    }
}