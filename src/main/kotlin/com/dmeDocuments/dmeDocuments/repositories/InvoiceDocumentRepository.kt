package com.dmeDocuments.dmeDocuments.repositories

import com.dmeDocuments.dmeDocuments.dataclasses.InvoiceDocument
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface InvoiceDocumentRepository:CrudRepository<InvoiceDocument,String> {
    @Query("select docref, index9 as assessmentid, index11 as invoiceid  from IndexInfo where index9=:assessmentId")
    fun getDocrefByAssessmentId(assessmentId:String):List<InvoiceDocument>

    @Query("select  docref, index9 as assessmentid, index11 as invoiceid from IndexInfo where index11=:invoiceId")
    fun getDocrefByInvoiceId(invoiceId:String):List<InvoiceDocument>
}