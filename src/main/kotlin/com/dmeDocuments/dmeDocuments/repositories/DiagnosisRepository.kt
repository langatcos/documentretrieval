package com.dmeDocuments.dmeDocuments.repositories

import com.dmeDocuments.dmeDocuments.dataclasses.Diagnosis
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface DiagnosisRepository:CrudRepository<Diagnosis,String> {
    @Query("select icd10,ConditionDescription,a.Description as AffectedSystem,r.Description as riskGroup\n" +
            "from Conditions c join code a on c.AffectedSystem=a.CodeId \n" +
            "join Code r on r.codeid=c.RiskGroup where r.CodeSet=715\n" +
            "and a.codeset=714  and Archived=0\n" +
            ";\n")
    fun getAllActiveDiagnosis():List<Diagnosis>
    @Query("select icd10,ConditionDescription,a.Description as AffectedSystem,r.Description as riskGroup\n" +
            "from Conditions c join code a on c.AffectedSystem=a.CodeId \n" +
            "join Code r on r.codeid=c.RiskGroup where r.CodeSet=715\n" +
            "and a.codeset=714  and Archived=0 and icd10=:icd10 \n" +
            ";\n")
    fun getAllActiveDiagnosisByIcd10(icd10:String):List<Diagnosis>
}