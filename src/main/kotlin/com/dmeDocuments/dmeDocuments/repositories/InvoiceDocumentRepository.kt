package com.dmeDocuments.dmeDocuments.repositories

import com.dmeDocuments.dmeDocuments.dataclasses.InvoiceDocument
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface InvoiceDocumentRepository:CrudRepository<InvoiceDocument,String> {
    @Query("with assessments as (select a.AssessmentId,i.invoiceid from claimassessment a join claimtreatment t on a.assessmentid=t.assessmentid join claimtreatmentinvoice i on t.treatmentid=i.treatmentid)\n" +
            "\n" +
            "select docref, index9 as assessmentid, index11 as invoiceid,CONVERT(DATE, index4, 112) as receivedDate  from IndexInfo i\n" +
            "join assessments a on cast(a.assessmentid as Nvarchar(20))=index9  and cast(a.InvoiceId as Nvarchar(20))=index11 where index9=:assessmentId")
    fun getDocrefByAssessmentId(assessmentId:String):List<InvoiceDocument>

    @Query("with assessments as (select a.AssessmentId,i.invoiceid from claimassessment a join claimtreatment t on a.assessmentid=t.assessmentid join claimtreatmentinvoice i on t.treatmentid=i.treatmentid)\n" +
            "\n" +
            "select docref, index9 as assessmentid, index11 as invoiceid, CONVERT(DATE, index4, 112) as receivedDate  from IndexInfo i\n" +
            "join assessments a on cast(a.assessmentid as Nvarchar(20))=index9  and cast(a.InvoiceId as Nvarchar(20))=index11 where index11=:invoiceId")
    fun getDocrefByInvoiceId(invoiceId:String):List<InvoiceDocument>
}