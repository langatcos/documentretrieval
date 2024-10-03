package com.dmeDocuments.dmeDocuments.controllers




import com.dmeDocuments.dmeDocuments.services.PdfDecompressorService
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

@RestController
@RequestMapping("/api/pdf")
class PdfController(
    private val pdfDecompressorService: PdfDecompressorService
) {

    @PostMapping("/decompress", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun decompressPdf(@RequestParam("file") file: MultipartFile): ResponseEntity<ByteArray> {
        val inputStream = ByteArrayInputStream(file.bytes)
        val outputStream = ByteArrayOutputStream()

        // Call the service to decompress the PDF
        pdfDecompressorService.decompressPdf(inputStream, outputStream)

        // Prepare the decompressed PDF as a response
        val decompressedPdf = outputStream.toByteArray()
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_PDF
            set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=decompressed.pdf")
        }

        return ResponseEntity.ok()
            .headers(headers)
            .body(decompressedPdf)
    }
}
