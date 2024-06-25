package com.dmeDocuments.dmeDocuments.controllers

import com.dmeDocuments.dmeDocuments.dataclasses.InvoiceDocument
import com.dmeDocuments.dmeDocuments.repositories.InvoiceDocumentRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.swing.text.Document

@RestController
@CrossOrigin
@RequestMapping("/api")
class InvoiceDocumentService (val invoiceDocumentRepository: InvoiceDocumentRepository) {
    @GetMapping("/retrievedocrefByAssessmentId/{assessmentId}")
    fun getDocrefByAssessmentId(@PathVariable assessmentId:String):ResponseEntity<List<InvoiceDocument>>{
        val docref=invoiceDocumentRepository.getDocrefByAssessmentId(assessmentId)
        return if (docref.isNotEmpty()){
            ResponseEntity.ok(docref)
        }else{
            ResponseEntity.noContent().build()
        }
    }
    @GetMapping("/retrievedocrefbyInvoiceId/{invoiceId}")
    fun getDocrefByInvoiceId(@PathVariable invoiceId:String):ResponseEntity<List<InvoiceDocument>>{
        val docref=invoiceDocumentRepository.getDocrefByAssessmentId(invoiceId)
        return if (docref.isNotEmpty()){
            ResponseEntity.ok(docref)
        }else{
            ResponseEntity.noContent().build()
        }
    }

}