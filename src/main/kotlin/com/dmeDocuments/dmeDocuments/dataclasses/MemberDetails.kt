package com.dmeDocuments.dmeDocuments.dataclasses

import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("memberdetails")
data class MemberDetails(
    @Column("entityId")
    val entityId:Int?,
    @Column("beneficiaryName")
    val beneficiaryName:String?,

    @Column("policyId")
    val policyId:Int?,
    @Column("effectiveDate")
    val effectiveDate: Any,
    @Column("policyholderName")
    val policyholderName:String?,
    @Column("product")
    val product:String?,

    )
