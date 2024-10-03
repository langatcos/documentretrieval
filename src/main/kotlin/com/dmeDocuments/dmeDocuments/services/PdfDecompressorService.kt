package com.dmeDocuments.dmeDocuments.services




import org.apache.pdfbox.contentstream.PDFStreamEngine
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.springframework.stereotype.Service
import java.io.File
import java.io.InputStream
import java.io.OutputStream

@Service
class PdfDecompressorService {

    // Decompress the PDF by processing page content streams
    fun decompressPdf(inputStream: InputStream, outputStream: OutputStream) {
        val document = PDDocument.load(inputStream)

        // Iterate through each page in the document
        for (page in document.pages) {
            decompressPageContent(page)
        }

        // Save the decompressed PDF
        document.save(outputStream)
        document.close()
    }

    fun decompressPdf(inputFile: File, outputFile: File) {
        val document = PDDocument.load(inputFile)

        for (page in document.pages) {
            decompressPageContent(page)
        }

        document.save(outputFile)
        document.close()
    }

    // Process and decompress content streams on the page
    private fun decompressPageContent(page: PDPage) {
        val contentStreamEngine = object : PDFStreamEngine() {}

        // Trigger PDFStreamEngine to process the page content
        contentStreamEngine.processPage(page)
    }
}
