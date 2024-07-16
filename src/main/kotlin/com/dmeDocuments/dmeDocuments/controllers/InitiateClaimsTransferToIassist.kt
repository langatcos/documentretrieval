package com.dmeDocuments.dmeDocuments.controllers
import com.dmeDocuments.dmeDocuments.services.AssessmentService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("claims/")
class InitiateClaimsTransferToIassist {

    @Autowired
    private lateinit var assessmentService: AssessmentService

    @GetMapping("/initiate")
    fun processAssessments(): List<AssessmentService.AssessmentData> {
        return assessmentService.processAssessments()
    }
}