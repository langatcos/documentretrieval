package com.dmeDocuments.dmeDocuments.dataclasses

import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("hybridmember")
data class HybridMember(
    @Column("entityId")
    val entityId:Int?,
    @Column("memberName")
    val memberName:String?,
    @Column("otherNo")
    val otherNo:String?,

)
