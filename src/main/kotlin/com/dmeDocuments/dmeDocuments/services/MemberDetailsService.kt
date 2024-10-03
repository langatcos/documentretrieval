package com.dmeDocuments.dmeDocuments.services

import com.dmeDocuments.dmeDocuments.dataclasses.MemberDetails
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class MemberDetailsService(private val jdbcTemplate: JdbcTemplate) {

    // Define a RowMapper to convert ResultSet to MemberDetails
    private val rowMapper = RowMapper<MemberDetails> { rs, _ ->
        MemberDetails(
            entityId = rs.getInt("entityId"),
            beneficiaryName = rs.getString("beneficiaryName"),
            policyId = rs.getInt("policyId"),
            effectiveDate = rs.getObject("effectiveDate", LocalDate::class.java),
            policyholderName = rs.getString("policyholderName"),
            product = rs.getString("product")
        )
    }

    fun getMemberDetails(beneficiaryName: String, policyholderName: String?): List<MemberDetails> {
        // Call the stored procedure
        val sql = "{call SearchBeneficiaryDetails (?, ?)}"
        return jdbcTemplate.query(sql, arrayOf(beneficiaryName, policyholderName), rowMapper)
    }
}
