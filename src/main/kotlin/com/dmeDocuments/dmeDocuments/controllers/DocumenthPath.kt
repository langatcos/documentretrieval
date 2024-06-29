package com.dmeDocuments.dmeDocuments.controllers

import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("documentpath")
data class DocumenthPath(
    @Column("docref")
    val docref:String,
    @Column("path")
    val path:String
)
