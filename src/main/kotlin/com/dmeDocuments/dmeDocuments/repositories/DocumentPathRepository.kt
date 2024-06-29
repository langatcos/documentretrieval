package com.dmeDocuments.dmeDocuments.repositories

import com.dmeDocuments.dmeDocuments.dataclasses.DocumentPath
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface DocumentPathRepository:CrudRepository<DocumentPath,String> {
    @Query("select i.DocRef, Path from indexinfo i join PageInfo p on i.docref=p.DocRef where i.docref=:docRef")
    fun getPathByDocRef(docRef:String):List<DocumentPath>


}