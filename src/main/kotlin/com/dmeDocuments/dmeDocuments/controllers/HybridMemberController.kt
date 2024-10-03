package com.dmeDocuments.dmeDocuments.controllers

import com.dmeDocuments.dmeDocuments.dataclasses.HybridMember
import com.dmeDocuments.dmeDocuments.repositories.HybridMemberRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin
@RequestMapping("api/")
class HybridMemberController (val hybridMemberRepository: HybridMemberRepository) {
    @GetMapping("/getHybridMemberOtherNo/{entityId}")
    fun getHybridMemberNo(@PathVariable entityId:Int):ResponseEntity<List<HybridMember>>{
        val member=hybridMemberRepository.getHybridMemberNo(entityId)
        return if (member.isNotEmpty()){
            ResponseEntity.ok(member)
        }else{
            ResponseEntity.noContent().build()
        }
    }
}