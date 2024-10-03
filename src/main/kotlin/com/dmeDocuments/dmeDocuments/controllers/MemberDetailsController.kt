package com.dmeDocuments.dmeDocuments.controllers



import com.dmeDocuments.dmeDocuments.dataclasses.MemberDetails
import com.dmeDocuments.dmeDocuments.services.MemberDetailsService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin
@RequestMapping("api/")
class MemberDetailsController(private val memberDetailsService: MemberDetailsService) {

    @GetMapping("/getMemberDetails")
    fun getMemberDetails(
        @RequestParam beneficiaryName: String,
        @RequestParam(required = false) policyholderName: String?
    ): ResponseEntity<List<MemberDetails>> {
        // Retrieve member details using the service
        val details = memberDetailsService.getMemberDetails(beneficiaryName, policyholderName)

        return if (details.isNotEmpty()) {
            ResponseEntity.ok(details)
        } else {
            ResponseEntity.noContent().build()
        }
    }
}
