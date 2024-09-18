package com.dmeDocuments.dmeDocuments.dataclasses
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("invoicedocument")
data class InvoiceDocument (
    @Column("invoiceid")
    val invoiceId:String?,
    @Column("assessmentid")
    val assessmentId:String?,
    @Column("docref")
    val docref:String,
    @Column("receivedDate")
    val receivedDate:String
)