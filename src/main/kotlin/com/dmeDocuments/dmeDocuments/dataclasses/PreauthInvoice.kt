package com.dmeDocuments.dmeDocuments.dataclasses

import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("preauthinvoice")
data class PreauthInvoice(
    @Column("preauthNumber")
    val preauthNumber:String?,
    @Column("invoiceId")
    val PreauthInvoiceId:String?,
    @Column("treatmentId")
    val treatmentId:String?
)
