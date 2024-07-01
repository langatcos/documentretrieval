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
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.nio.file.Paths

//Ommitted as a service as another service that does not need a caluclation of the file location has been created.

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

    // Endpoint to search for files
    @GetMapping("/search-and-download")
    fun searchAndDownload(@RequestParam filename: String): ResponseEntity<InputStreamResource> {
        val searchResults = mutableListOf<File>()
        val baseFolder = File(basePath)

        // Recursive function to search for file within basePath and its subdirectories
        searchFilesRecursively(baseFolder, filename, searchResults)

        // If searchResults is not empty, retrieve the first file found and allow download
        if (searchResults.isNotEmpty()) {
            val file = searchResults[0]

            try {
                val fileInputStream = FileInputStream(file)
                val resource = InputStreamResource(fileInputStream)

                val headers = HttpHeaders()
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=${file.name}")

                return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource)
            } catch (e: FileNotFoundException) {
                return ResponseEntity.notFound().build()
            }
        }

        // If file is not found, return 404 Not Found
        return ResponseEntity.notFound().build()
    }

    // Recursive function to search for files
    private fun searchFilesRecursively(folder: File, searchTerm: String, searchResults: MutableList<File>) {
        folder.listFiles()?.forEach { file ->
            if (file.isDirectory) {
                searchFilesRecursively(file, searchTerm, searchResults)
            } else if (file.name.equals(searchTerm, ignoreCase = true)) {
                searchResults.add(file)
            }
        }
    }
}