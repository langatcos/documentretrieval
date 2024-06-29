package com.dmeDocuments.dmeDocuments.controllers

import com.dmeDocuments.dmeDocuments.dataclasses.DocumentPath
import com.dmeDocuments.dmeDocuments.repositories.DocumentPathRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin
@RequestMapping("/api/files")
class DocumentPathService (val documentPathRepository: DocumentPathRepository){
    @GetMapping("/dmefileDownload/{docRef}")
    fun getDocpath(@PathVariable docRef:String):ResponseEntity<List<DocumentPath>>{
        val path=documentPathRepository.getPathByDocRef(docRef)
        return if(path.isNotEmpty()){
            ResponseEntity.ok(path)
        }else{
            ResponseEntity.noContent().build()
        }
    }

}