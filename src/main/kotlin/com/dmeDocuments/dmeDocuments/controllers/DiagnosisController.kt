package com.dmeDocuments.dmeDocuments.controllers

import com.dmeDocuments.dmeDocuments.dataclasses.Diagnosis
import com.dmeDocuments.dmeDocuments.repositories.DiagnosisRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class DiagnosisController(val diagnosisRepository: DiagnosisRepository) {
    @GetMapping("/getAllDiagnosis")
    fun getAllDiagnosis():ResponseEntity<List<Diagnosis>>{
        val diagnosis=diagnosisRepository.getAllActiveDiagnosis()
        return if (diagnosis.isNotEmpty()){
            ResponseEntity.ok(diagnosis)
        }else{
            ResponseEntity.noContent().build()
        }
    }
    @GetMapping("/getdiagnosisByICD10/{icd10}")
    fun getAllDiagnosisByIcd10(@PathVariable icd10:String):ResponseEntity<List<Diagnosis>>{
        val diagnosis=diagnosisRepository.getAllActiveDiagnosisByIcd10(icd10)
        return if (diagnosis.isNotEmpty()){
            ResponseEntity.ok(diagnosis)
        }else{
            ResponseEntity.noContent().build()
        }
    }
}