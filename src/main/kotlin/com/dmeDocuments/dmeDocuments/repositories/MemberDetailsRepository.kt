package com.dmeDocuments.dmeDocuments.repositories

import com.dmeDocuments.dmeDocuments.dataclasses.MemberDetails
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface MemberDetailsRepository : CrudRepository<MemberDetails, String> {

    @Query(
        """
        SELECT DISTINCT * 
        FROM beneficiaryDetails 
        WHERE 
            EXISTS (
                SELECT 1 
                FROM STRING_SPLIT(LOWER(beneficiaryName), ' ') AS parts
                WHERE parts.value IN (
                    LOWER(:beneficiaryName), 
                    LOWER(REPLACE(:beneficiaryName, ' ', ''))
                )
            )
            OR (
                LOWER(policyholderName) LIKE LOWER(CONCAT('%', :policyholderName, '%'))
            )
        """
    )
    fun getMemberDetails(
        @Param("beneficiaryName") beneficiaryName: String,
        @Param("policyholderName") policyholderName: String?
    ): List<MemberDetails>
}
