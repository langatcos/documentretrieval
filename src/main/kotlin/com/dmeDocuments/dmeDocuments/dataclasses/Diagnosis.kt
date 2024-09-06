package com.dmeDocuments.dmeDocuments.dataclasses

import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("diagnosis")
data class Diagnosis(
    @Column("icd10")
    val icd10:String?,
    @Column("conditionDescription")
    val conditionDescription:String?,
    @Column("AffectedSystem")
    val AffectedSystem:String?,
    @Column("riskGroup")
    val riskGroup:String?,


)
