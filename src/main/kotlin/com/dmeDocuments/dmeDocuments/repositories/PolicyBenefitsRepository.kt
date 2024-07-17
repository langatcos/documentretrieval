package com.dmeDocuments.dmeDocuments.repositories


import com.dmeDocuments.dmeDocuments.dataclasses.PolicyBenefits
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface  PolicyBenefitsRepository:CrudRepository<PolicyBenefits,String> {
    @Query("select distinct b.policyId,b.polEffDate, b.BenefitId,b.BenefitTypeId,c.description as benefitName from policybenefit b  \n" +
            "join  component c on c.ComponentTypeID=b.benefittypeid and c.ComponentID=b.BenefitId  \n" +
            "join relation r on r.ParentCompTypeID=c.ComponentTypeID and c.ComponentID=r.ParentCompID\n" +
            "join policybenefit sb on sb.BenefitId=r.ChildCompID and sb.BenefitTypeId=r.ChildCompTypeID and sb.policyid=b.policyid and sb.PolEffDate=b.PolEffDate\n" +
            "join component sbc on sb.BenefitId=sbc.ComponentID and sb.BenefitTypeId=sbc.ComponentTypeID \n" +
            "join relation sbr on sbc. ComponentTypeID=sbr.ParentCompTypeID and sbc.ComponentID=sbr.ParentCompID\n" +
            "join Component bdl on bdl.ComponentID=sbr.ChildCompID and bdl.ComponentTypeID=sbr.ChildCompTypeID\n" +
            "where b.BenefitTypeId=18\n" +
            "and sb.BenefitTypeId=143\n" +
            "and bdl.ComponentTypeID=115\n" +
            "and b.InfoId=43 and sb.infoid=43 and b.policyid=:policyId and b.poleffdate=:polEffDate")
    fun findAllPolicyBenefitsByPolicyIdAndPolEffDate(policyId:Int,polEffDate:LocalDate):List<PolicyBenefits>

    @Query("select distinct b.policyId,b.polEffDate, sb.BenefitId,sb.BenefitTypeId,sbc.description as benefitName from policybenefit b  \n" +
            "join  component c on c.ComponentTypeID=b.benefittypeid and c.ComponentID=b.BenefitId  \n" +
            "join relation r on r.ParentCompTypeID=c.ComponentTypeID and c.ComponentID=r.ParentCompID\n" +
            "join policybenefit sb on sb.BenefitId=r.ChildCompID and sb.BenefitTypeId=r.ChildCompTypeID and sb.policyid=b.policyid and sb.PolEffDate=b.PolEffDate\n" +
            "join component sbc on sb.BenefitId=sbc.ComponentID and sb.BenefitTypeId=sbc.ComponentTypeID \n" +
            "join relation sbr on sbc. ComponentTypeID=sbr.ParentCompTypeID and sbc.ComponentID=sbr.ParentCompID\n" +
            "join Component bdl on bdl.ComponentID=sbr.ChildCompID and bdl.ComponentTypeID=sbr.ChildCompTypeID\n" +
            "where b.BenefitTypeId=18\n" +
            "and sb.BenefitTypeId=143\n" +
            "and bdl.ComponentTypeID=115\n" +
            "and b.InfoId=43 and sb.infoid=43 and b.policyid=:policyId and b.poleffdate=:polEffDate and b.benefitId=:benefitId")

    fun findAllPolicySubBenefitsByPolicyIdAndPolEffDateandBenefitId(policyId:Int,polEffDate:LocalDate,benefitId:Int):List<PolicyBenefits>
    @Query("select distinct b.policyId,b.polEffDate, bdl.componentId as BenefitId ,bdl.componentTypeId as benefitTypeId,bdl.description as benefitName from policybenefit b  \n" +
            "join  component c on c.ComponentTypeID=b.benefittypeid and c.ComponentID=b.BenefitId  \n" +
            "join relation r on r.ParentCompTypeID=c.ComponentTypeID and c.ComponentID=r.ParentCompID\n" +
            "join policybenefit sb on sb.BenefitId=r.ChildCompID and sb.BenefitTypeId=r.ChildCompTypeID and sb.policyid=b.policyid and sb.PolEffDate=b.PolEffDate\n" +
            "join component sbc on sb.BenefitId=sbc.ComponentID and sb.BenefitTypeId=sbc.ComponentTypeID \n" +
            "join relation sbr on sbc. ComponentTypeID=sbr.ParentCompTypeID and sbc.ComponentID=sbr.ParentCompID\n" +
            "join Component bdl on bdl.ComponentID=sbr.ChildCompID and bdl.ComponentTypeID=sbr.ChildCompTypeID\n" +
            "where b.BenefitTypeId=18\n" +
            "and sb.BenefitTypeId=143\n" +
            "and bdl.ComponentTypeID=115\n" +
            "and b.InfoId=43 and sb.infoid=43 and b.policyid=:policyId and b.poleffdate=:polEffDate and b.benefitId=:benefitId and sb.benefitid=:subBenefitId")
    fun findAllPolicySubBenefitsBdlsByPolicyIdAndPolEffDateandBenefitIdandbdlId(policyId:Int,polEffDate:LocalDate,benefitId:Int,subBenefitId:Int):List<PolicyBenefits>
}