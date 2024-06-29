package com.dmeDocuments.dmeDocuments.dataclasses

import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("documentpath")
data class DocumentPath(
    @Column("docref")
    val docRef:String,
    @Column("path")
    val path:String
)
