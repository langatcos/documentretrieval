package com.dmeDocuments.dmeDocuments.services



import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.multipart.MaxUploadSizeExceededException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

@ControllerAdvice
class FileUploadExceptionAdvice {

    @ExceptionHandler(MaxUploadSizeExceededException::class)
    fun handleMaxSizeException(e: MaxUploadSizeExceededException): ResponseEntity<String> {
        return ResponseEntity("File too large! Maximum allowed size is exceeded.", HttpStatus.PAYLOAD_TOO_LARGE)
    }
}
