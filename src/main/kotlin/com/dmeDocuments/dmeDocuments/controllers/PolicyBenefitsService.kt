package com.dmeDocuments.dmeDocuments.controllers


import com.dmeDocuments.dmeDocuments.dataclasses.PolicyBenefits
import com.dmeDocuments.dmeDocuments.repositories.PolicyBenefitsRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RestController
@CrossOrigin
@RequestMapping("api/data")
class PolicyBenefitsService(val policyBenefitsRepository: PolicyBenefitsRepository) {
    @GetMapping("/policyBenefits/{policyId}/{polEffDate}")
    fun getAllPolicyBenefits(@PathVariable policyId:Int,@PathVariable polEffDate:String):ResponseEntity<List<PolicyBenefits>>{
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val effDate: LocalDate = LocalDate.parse(polEffDate, formatter)
        val benefits=policyBenefitsRepository.findAllPolicyBenefitsByPolicyIdAndPolEffDate(policyId,effDate)
        return if(benefits.isNotEmpty()){
            ResponseEntity.ok(benefits)
        }else{
            ResponseEntity.noContent().build()
        }
    }
    @GetMapping("/policyBenefitSubBenefits/{policyId}/{polEffDate}/{benefitId}")
    fun getAllPolicyBenefitSubBenefits(@PathVariable policyId:Int,@PathVariable polEffDate:String,@PathVariable benefitId:Int):ResponseEntity<List<PolicyBenefits>>{
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val effDate: LocalDate = LocalDate.parse(polEffDate, formatter)
        val benefits=policyBenefitsRepository.findAllPolicySubBenefitsByPolicyIdAndPolEffDateandBenefitId(policyId,effDate,benefitId)
        return if(benefits.isNotEmpty()){
            ResponseEntity.ok(benefits)
        }else{
            ResponseEntity.noContent().build()
        }
    }
    @GetMapping("/policyBenefitSubBenefitsBDLs/{policyId}/{polEffDate}/{benefitId}/{subBenefitId}")
    fun getAllPolicyBenefitSubBenefitsBDle(@PathVariable policyId:Int,@PathVariable polEffDate:String,@PathVariable benefitId:Int,@PathVariable subBenefitId:Int):ResponseEntity<List<PolicyBenefits>> {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val effDate: LocalDate = LocalDate.parse(polEffDate, formatter)
        val benefits = policyBenefitsRepository.findAllPolicySubBenefitsBdlsByPolicyIdAndPolEffDateandBenefitIdandbdlId(
            policyId,
            effDate,
            benefitId,
            subBenefitId
        )
        return if (benefits.isNotEmpty()) {
            ResponseEntity.ok(benefits)
        } else {
            ResponseEntity.noContent().build()
        }
    }
}