package com.dmeDocuments.dmeDocuments.repositories
import com.dmeDocuments.dmeDocuments.dataclasses.PreauthInvoice
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PreathInvoiceRepository:CrudRepository<PreauthInvoice,String>{
    @Query("select invoiceReference as preauthNumber, I.invoiceId, treatmentId, C.Description as preauthStatus from claimtreatmentinvoice  I join PreAuthorisedInvoice P ON I.INVOICEID=P.InvoiceId\n" +
            "JOIN CODE C ON P.StatusCodeId=C.CODEID AND CodeSet=755 where invoiceType=5 and invoiceReference=:preauthNumber")
    fun getPreauthInvoiceByPreauthNumber(preauthNumber:String):List<PreauthInvoice>
}
