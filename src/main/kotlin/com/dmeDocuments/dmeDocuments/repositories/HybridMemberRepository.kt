package com.dmeDocuments.dmeDocuments.repositories

import com.dmeDocuments.dmeDocuments.dataclasses.HybridMember
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface HybridMemberRepository:CrudRepository<HybridMember,String> {
    @Query("select e.entityId,CONCAT(title,' ',FirstName, ' ',Surname) as memberName,info as otherNo \n" +
            "from EntityRoleInfo i JOIN entity e on i.EntityId=e.EntityId where infoid=1365 and e.entityId=:entityId;\n")
    fun getHybridMemberNo(entityId:Int):List<HybridMember>
}