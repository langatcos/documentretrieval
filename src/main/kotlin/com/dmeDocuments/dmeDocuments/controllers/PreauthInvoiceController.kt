package com.dmeDocuments.dmeDocuments.controllers

import com.dmeDocuments.dmeDocuments.dataclasses.PreauthInvoice
import com.dmeDocuments.dmeDocuments.repositories.PreathInvoiceRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin
class PreauthInvoiceController(val preauthInvoiceRepository: PreathInvoiceRepository) {
    @GetMapping("getInvoiceIdByPreauthNumber/{preauthNumber}")
    fun getPreauthInvoiceId(@PathVariable preauthNumber:String):ResponseEntity<List<PreauthInvoice>>
    {
        val preauth=preauthInvoiceRepository.getPreauthInvoiceByPreauthNumber(preauthNumber)
        return if (preauth.isNotEmpty()){
            ResponseEntity.ok(preauth)
        }
        else{
            ResponseEntity.noContent().build()
        }
    }

}