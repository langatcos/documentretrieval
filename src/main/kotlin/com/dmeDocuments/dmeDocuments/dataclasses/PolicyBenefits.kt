package com.dmeDocuments.dmeDocuments.dataclasses

import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDate

@Table("policybenefits")
data class PolicyBenefits(
    @Column("policyid")
    val policyId:Int,
    @Column("poleffdate")
    val polEffDate:LocalDate,
    @Column("benefitid")
    val benefitId:Int,
    @Column("benefittypeid")
    val benefitTypeID:Int,
    @Column("benefitName")
    val benefitName:String,


)

