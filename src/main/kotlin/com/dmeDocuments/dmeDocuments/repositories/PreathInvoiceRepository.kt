package com.dmeDocuments.dmeDocuments.repositories

import com.dmeDocuments.dmeDocuments.dataclasses.PreauthInvoice
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PreathInvoiceRepository:CrudRepository<PreauthInvoice,String>{
    @Query("select invoiceReference as preauthNumber, invoiceId, treatmentId from claimtreatmentinvoice where invoiceType=5 and invoiceReference=:preauthNumber")
    fun getPreauthInvoiceByPreauthNumber(preauthNumber:String):List<PreauthInvoice>
}
