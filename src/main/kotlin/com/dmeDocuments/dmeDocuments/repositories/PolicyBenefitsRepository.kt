package com.dmeDocuments.dmeDocuments.repositories


import com.dmeDocuments.dmeDocuments.dataclasses.PolicyBenefits
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface  PolicyBenefitsRepository:CrudRepository<PolicyBenefits,String> {
    @Query("\n" +
            "with benefits as( SELECT\n" +
            "\tPolicyId,\n" +
            "\tEffectiveDate,\n" +
            "\tSystemKey,\n" +
            "\tSUBSTRING(SystemKey,(PATINDEX('%~%',SystemKey)+1),(LEN(SystemKey)-(PATINDEX('%~%',SystemKey)))) BenefitId,\n" +
            "\tSUBSTRING(SystemKey,1,PATINDEX('%~%',SystemKey)-1) BenefitTypeId,\n" +
            "\tNULL Benefit,\n" +
            "\t'0' AppliesTo,\n" +
            "\tCASE\n" +
            "\t\tWHEN ExtRelationshipType = N'Additional Benefit Options' THEN 'BENEFIT'\n" +
            "\t\tWHEN ExtRelationshipType = N'Sub Benefit' THEN 'SUB BENEFIT'\n" +
            "\t\tELSE NULL\n" +
            "\tEND as Type,\n" +
            "\t'0' PrincipalBenefit,\n" +
            "\t0 Division,\n" +
            "\tNULL CoverChoice,\n" +
            "\tNULL DiscountReason\n" +
            "FROM [dbo].PolicyExtRelationship\n" +
            "WHERE ExtRelationshipType IN (N'Additional Benefit Options', N'Sub Benefit') and SUBSTRING(SystemKey,1,PATINDEX('%~%',SystemKey)-1) in ('18')\n" +
            "),\n" +
            "subBenefits as ( SELECT\n" +
            "\tPolicyId,\n" +
            "\tEffectiveDate,\n" +
            "\tSystemKey,\n" +
            "\tSUBSTRING(SystemKey,(PATINDEX('%~%',SystemKey)+1),(LEN(SystemKey)-(PATINDEX('%~%',SystemKey)))) BenefitId,\n" +
            "\tSUBSTRING(SystemKey,1,PATINDEX('%~%',SystemKey)-1) BenefitTypeId,\n" +
            "\tNULL Benefit,\n" +
            "\t'0' AppliesTo,\n" +
            "\tCASE\n" +
            "\t\tWHEN ExtRelationshipType = N'Additional Benefit Options' THEN 'BENEFIT'\n" +
            "\t\tWHEN ExtRelationshipType = N'Sub Benefit' THEN 'SUB BENEFIT'\n" +
            "\t\tELSE NULL\n" +
            "\tEND as Type,\n" +
            "\t'0' PrincipalBenefit,\n" +
            "\t0 Division,\n" +
            "\tNULL CoverChoice,\n" +
            "\tNULL DiscountReason\n" +
            "FROM [dbo].PolicyExtRelationship\n" +
            "WHERE ExtRelationshipType IN (N'Additional Benefit Options', N'Sub Benefit') and SUBSTRING(SystemKey,1,PATINDEX('%~%',SystemKey)-1) in ('143'))\n" +
            "\n" +
            "select distinct  b.policyId,b.EffectiveDate as polEffDate, b.BenefitId,b.BenefitTypeId,c.description as benefitName from benefits b join  component c on c.componentid=b.benefitid and c.componenttypeid=18\n" +
            "join relation rb on b.benefitid=rb.parentcompid and c.componenttypeid=18\n" +
            "join component sbc on sbc.componentid=rb.childCompId and sbc.ComponentTypeID=143\n" +
            "join subBenefits sb on sbc.componentId=sb.benefitid and b.policyid=sb.policyid and b.effectivedate=sb.effectivedate\n" +
            "join relation rbc on  rbc.parentCompId=sb.BenefitId and rbc.parentCompTypeID=143\n" +
            "join component bdl on bdl.componentid=rbc.childCompId and bdl.ComponentTypeID=115\n" +
            "\n" +
            "\n" +
            "where  b.policyid=:policyId and b.effectivedate = :polEffDate\n" +
            "\n" +
            "union All\n" +
            "\n" +
            "select distinct b.policyId,b.polEffDate, b.BenefitId,b.BenefitTypeId,c.description as benefitName from policybenefit b\n" +
            "            join  component c on c.ComponentTypeID=b.benefittypeid and c.ComponentID=b.BenefitId  \n" +
            "            join relation r on r.ParentCompTypeID=c.ComponentTypeID and c.ComponentID=r.ParentCompID\n" +
            "            join policybenefit sb on sb.BenefitId=r.ChildCompID and sb.BenefitTypeId=r.ChildCompTypeID and sb.policyid=b.policyid and sb.PolEffDate=b.PolEffDate\n" +
            "            join component sbc on sb.BenefitId=sbc.ComponentID and sb.BenefitTypeId=sbc.ComponentTypeID \n" +
            "            join relation sbr on sbc. ComponentTypeID=sbr.ParentCompTypeID and sbc.ComponentID=sbr.ParentCompID\n" +
            "            join Component bdl on bdl.ComponentID=sbr.ChildCompID and bdl.ComponentTypeID=sbr.ChildCompTypeID\n" +
            "            where b.BenefitTypeId=18\n" +
            "            and sb.BenefitTypeId=143\n" +
            "            and bdl.ComponentTypeID=115\n" +
            "            and b.InfoId=43 and sb.infoid=43 and\n" +
            "\t\t\tb.policyid=:policyId and b.poleffdate=:polEffDate\n" +
        "union All\n" +
                "\n" +
                "select distinct b.policyId,b.polEffDate, b.BenefitId,b.BenefitTypeId,c.description as benefitName from policybenefit b\n" +
                "            join  component c on c.ComponentTypeID=b.benefittypeid and c.ComponentID=b.BenefitId  \n" +
                "            join relation r on r.ParentCompTypeID=c.ComponentTypeID and c.ComponentID=r.ParentCompID\n" +
                "            join policybenefit sb on sb.BenefitId=r.ChildCompID and sb.BenefitTypeId=r.ChildCompTypeID and sb.policyid=b.policyid and sb.PolEffDate=b.PolEffDate\n" +
                "            join component sbc on sb.BenefitId=sbc.ComponentID and sb.BenefitTypeId=sbc.ComponentTypeID \n" +
                "            join relation sbr on sbc. ComponentTypeID=sbr.ParentCompTypeID and sbc.ComponentID=sbr.ParentCompID\n" +
                "            join Component bdl on bdl.ComponentID=sbr.ChildCompID and bdl.ComponentTypeID=sbr.ChildCompTypeID\n" +
                "            where b.BenefitTypeId=18\n" +
                "            and sb.BenefitTypeId=143\n" +
                "            and bdl.ComponentTypeID=115\n" +
            "            and b.InfoId=0 and sb.infoid=0 and\n" +
                "\t\t\tb.policyid=:policyId and b.poleffdate=:polEffDate\n"
    )
    fun findAllPolicyBenefitsByPolicyIdAndPolEffDate(policyId:Int,polEffDate:LocalDate):List<PolicyBenefits>

    @Query("with benefits as( SELECT\n" +
            "\tPolicyId,\n" +
            "\tEffectiveDate,\n" +
            "\tSystemKey,\n" +
            "\tSUBSTRING(SystemKey,(PATINDEX('%~%',SystemKey)+1),(LEN(SystemKey)-(PATINDEX('%~%',SystemKey)))) BenefitId,\n" +
            "\tSUBSTRING(SystemKey,1,PATINDEX('%~%',SystemKey)-1) BenefitTypeId,\n" +
            "\tNULL Benefit,\n" +
            "\t'0' AppliesTo,\n" +
            "\tCASE\n" +
            "\t\tWHEN ExtRelationshipType = N'Additional Benefit Options' THEN 'BENEFIT'\n" +
            "\t\tWHEN ExtRelationshipType = N'Sub Benefit' THEN 'SUB BENEFIT'\n" +
            "\t\tELSE NULL\n" +
            "\tEND as Type,\n" +
            "\t'0' PrincipalBenefit,\n" +
            "\t0 Division,\n" +
            "\tNULL CoverChoice,\n" +
            "\tNULL DiscountReason\n" +
            "FROM [dbo].PolicyExtRelationship\n" +
            "WHERE ExtRelationshipType IN (N'Additional Benefit Options', N'Sub Benefit') and SUBSTRING(SystemKey,1,PATINDEX('%~%',SystemKey)-1) in ('18')\n" +
            "),\n" +
            "subBenefits as ( SELECT\n" +
            "\tPolicyId,\n" +
            "\tEffectiveDate,\n" +
            "\tSystemKey,\n" +
            "\tSUBSTRING(SystemKey,(PATINDEX('%~%',SystemKey)+1),(LEN(SystemKey)-(PATINDEX('%~%',SystemKey)))) BenefitId,\n" +
            "\tSUBSTRING(SystemKey,1,PATINDEX('%~%',SystemKey)-1) BenefitTypeId,\n" +
            "\tNULL Benefit,\n" +
            "\t'0' AppliesTo,\n" +
            "\tCASE\n" +
            "\t\tWHEN ExtRelationshipType = N'Additional Benefit Options' THEN 'BENEFIT'\n" +
            "\t\tWHEN ExtRelationshipType = N'Sub Benefit' THEN 'SUB BENEFIT'\n" +
            "\t\tELSE NULL\n" +
            "\tEND as Type,\n" +
            "\t'0' PrincipalBenefit,\n" +
            "\t0 Division,\n" +
            "\tNULL CoverChoice,\n" +
            "\tNULL DiscountReason\n" +
            "FROM [dbo].PolicyExtRelationship\n" +
            "WHERE ExtRelationshipType IN (N'Additional Benefit Options', N'Sub Benefit') and SUBSTRING(SystemKey,1,PATINDEX('%~%',SystemKey)-1) in ('143'))\n" +
            "\n" +
            "select distinct b.policyId,b.effectivedate as polEffDate, sb.BenefitId,sb.BenefitTypeId,sbc.description as benefitName from benefits b join  component c on c.componentid=b.benefitid and c.componenttypeid=18\n" +
            "join relation rb on b.benefitid=rb.parentcompid and c.componenttypeid=18\n" +
            "join component sbc on sbc.componentid=rb.childCompId and sbc.ComponentTypeID=143\n" +
            "join subBenefits sb on sbc.componentId=sb.benefitid and b.policyid=sb.policyid and b.effectivedate=sb.effectivedate\n" +
            "join relation rbc on  rbc.parentCompId=sb.BenefitId and rbc.parentCompTypeID=143\n" +
            "join component bdl on bdl.componentid=rbc.childCompId and bdl.ComponentTypeID=115\n" +
            "\n" +
            "\n" +
            "where  b.policyid=:policyId and b.effectivedate = :polEffDate and b.benefitId=:benefitId\n" +
            "\n" +
            "union All \n" +
            "  select distinct b.policyId,b.polEffDate, sb.BenefitId,sb.BenefitTypeId,sbc.description as benefitName from policybenefit b  \n" +
            "join  component c on c.ComponentTypeID=b.benefittypeid and c.ComponentID=b.BenefitId  \n" +
            "join relation r on r.ParentCompTypeID=c.ComponentTypeID and c.ComponentID=r.ParentCompID\n" +
            "join policybenefit sb on sb.BenefitId=r.ChildCompID and sb.BenefitTypeId=r.ChildCompTypeID and sb.policyid=b.policyid and sb.PolEffDate=b.PolEffDate\n" +
            "join component sbc on sb.BenefitId=sbc.ComponentID and sb.BenefitTypeId=sbc.ComponentTypeID \n" +
            "join relation sbr on sbc. ComponentTypeID=sbr.ParentCompTypeID and sbc.ComponentID=sbr.ParentCompID\n" +
            "join Component bdl on bdl.ComponentID=sbr.ChildCompID and bdl.ComponentTypeID=sbr.ChildCompTypeID\n" +
            "where b.BenefitTypeId=18\n" +
            "and sb.BenefitTypeId=143\n" +
            "and bdl.ComponentTypeID=115\n" +
            "and b.InfoId=43 and sb.infoid=43 and b.policyid=:policyId and b.poleffdate=:polEffDate and b.benefitId=:benefitId\n"+
            "union All \n" +
            "  select distinct b.policyId,b.polEffDate, sb.BenefitId,sb.BenefitTypeId,sbc.description as benefitName from policybenefit b  \n" +
            "join  component c on c.ComponentTypeID=b.benefittypeid and c.ComponentID=b.BenefitId  \n" +
            "join relation r on r.ParentCompTypeID=c.ComponentTypeID and c.ComponentID=r.ParentCompID\n" +
            "join policybenefit sb on sb.BenefitId=r.ChildCompID and sb.BenefitTypeId=r.ChildCompTypeID and sb.policyid=b.policyid and sb.PolEffDate=b.PolEffDate\n" +
            "join component sbc on sb.BenefitId=sbc.ComponentID and sb.BenefitTypeId=sbc.ComponentTypeID \n" +
            "join relation sbr on sbc. ComponentTypeID=sbr.ParentCompTypeID and sbc.ComponentID=sbr.ParentCompID\n" +
            "join Component bdl on bdl.ComponentID=sbr.ChildCompID and bdl.ComponentTypeID=sbr.ChildCompTypeID\n" +
            "where b.BenefitTypeId=18\n" +
            "and sb.BenefitTypeId=143\n" +
            "and bdl.ComponentTypeID=115\n" +
            "and b.InfoId=0 and sb.infoid=0 and b.policyid=:policyId and b.poleffdate=:polEffDate and b.benefitId=:benefitId"
    )

    fun findAllPolicySubBenefitsByPolicyIdAndPolEffDateandBenefitId(policyId:Int,polEffDate:LocalDate,benefitId:Int):List<PolicyBenefits>
    @Query(" \n" +
            "with benefits as( SELECT\n" +
            "\tPolicyId,\n" +
            "\tEffectiveDate,\n" +
            "\tSystemKey,\n" +
            "\tSUBSTRING(SystemKey,(PATINDEX('%~%',SystemKey)+1),(LEN(SystemKey)-(PATINDEX('%~%',SystemKey)))) BenefitId,\n" +
            "\tSUBSTRING(SystemKey,1,PATINDEX('%~%',SystemKey)-1) BenefitTypeId,\n" +
            "\tNULL Benefit,\n" +
            "\t'0' AppliesTo,\n" +
            "\tCASE\n" +
            "\t\tWHEN ExtRelationshipType = N'Additional Benefit Options' THEN 'BENEFIT'\n" +
            "\t\tWHEN ExtRelationshipType = N'Sub Benefit' THEN 'SUB BENEFIT'\n" +
            "\t\tELSE NULL\n" +
            "\tEND as Type,\n" +
            "\t'0' PrincipalBenefit,\n" +
            "\t0 Division,\n" +
            "\tNULL CoverChoice,\n" +
            "\tNULL DiscountReason\n" +
            "FROM [dbo].PolicyExtRelationship\n" +
            "WHERE ExtRelationshipType IN (N'Additional Benefit Options', N'Sub Benefit') and SUBSTRING(SystemKey,1,PATINDEX('%~%',SystemKey)-1) in ('18')\n" +
            "),\n" +
            "subBenefits as ( SELECT\n" +
            "\tPolicyId,\n" +
            "\tEffectiveDate,\n" +
            "\tSystemKey,\n" +
            "\tSUBSTRING(SystemKey,(PATINDEX('%~%',SystemKey)+1),(LEN(SystemKey)-(PATINDEX('%~%',SystemKey)))) BenefitId,\n" +
            "\tSUBSTRING(SystemKey,1,PATINDEX('%~%',SystemKey)-1) BenefitTypeId,\n" +
            "\tNULL Benefit,\n" +
            "\t'0' AppliesTo,\n" +
            "\tCASE\n" +
            "\t\tWHEN ExtRelationshipType = N'Additional Benefit Options' THEN 'BENEFIT'\n" +
            "\t\tWHEN ExtRelationshipType = N'Sub Benefit' THEN 'SUB BENEFIT'\n" +
            "\t\tELSE NULL\n" +
            "\tEND as Type,\n" +
            "\t'0' PrincipalBenefit,\n" +
            "\t0 Division,\n" +
            "\tNULL CoverChoice,\n" +
            "\tNULL DiscountReason\n" +
            "FROM [dbo].PolicyExtRelationship\n" +
            "WHERE ExtRelationshipType IN (N'Additional Benefit Options', N'Sub Benefit') and SUBSTRING(SystemKey,1,PATINDEX('%~%',SystemKey)-1) in ('143'))\n" +
            "\n" +
            "select distinct b.policyId,b.effectiveDate as polEffDate, bdl.componentId as BenefitId ,bdl.componentTypeId as benefitTypeId,bdl.description as benefitName from benefits b join  component c on c.componentid=b.benefitid and c.componenttypeid=18\n" +
            "join relation rb on b.benefitid=rb.parentcompid and c.componenttypeid=18\n" +
            "join component sbc on sbc.componentid=rb.childCompId and sbc.ComponentTypeID=143\n" +
            "join subBenefits sb on sbc.componentId=sb.benefitid and b.policyid=sb.policyid and b.effectivedate=sb.effectivedate\n" +
            "join relation rbc on  rbc.parentCompId=sb.BenefitId and rbc.parentCompTypeID=143\n" +
            "join component bdl on bdl.componentid=rbc.childCompId and bdl.ComponentTypeID=115\n" +
            "\n" +
            "\n" +
            "where  b.policyid=:policyId and b.effectivedate = :polEffDate  and b.benefitId=:benefitId and sb.benefitid=:subBenefitId\n" +
            "\n" +
            "union All \n" +
            "  select distinct b.policyId,b.polEffDate, bdl.componentId as BenefitId ,bdl.componentTypeId as benefitTypeId,bdl.description as benefitName from policybenefit b  \n" +
            "join  component c on c.ComponentTypeID=b.benefittypeid and c.ComponentID=b.BenefitId  \n" +
            "join relation r on r.ParentCompTypeID=c.ComponentTypeID and c.ComponentID=r.ParentCompID\n" +
            "join policybenefit sb on sb.BenefitId=r.ChildCompID and sb.BenefitTypeId=r.ChildCompTypeID and sb.policyid=b.policyid and sb.PolEffDate=b.PolEffDate\n" +
            "join component sbc on sb.BenefitId=sbc.ComponentID and sb.BenefitTypeId=sbc.ComponentTypeID \n" +
            "join relation sbr on sbc. ComponentTypeID=sbr.ParentCompTypeID and sbc.ComponentID=sbr.ParentCompID\n" +
            "join Component bdl on bdl.ComponentID=sbr.ChildCompID and bdl.ComponentTypeID=sbr.ChildCompTypeID\n" +
            "where b.BenefitTypeId=18\n" +
            "and sb.BenefitTypeId=143\n" +
            "and bdl.ComponentTypeID=115\n" +
            "and b.InfoId=43 and sb.infoid=43 and b.policyid=:policyId and b.poleffdate=:polEffDate and b.benefitId=:benefitId and sb.benefitid=:subBenefitId\n"+
        "union All \n" +
                "  select distinct b.policyId,b.polEffDate, bdl.componentId as BenefitId ,bdl.componentTypeId as benefitTypeId,bdl.description as benefitName from policybenefit b  \n" +
                "join  component c on c.ComponentTypeID=b.benefittypeid and c.ComponentID=b.BenefitId  \n" +
                "join relation r on r.ParentCompTypeID=c.ComponentTypeID and c.ComponentID=r.ParentCompID\n" +
                "join policybenefit sb on sb.BenefitId=r.ChildCompID and sb.BenefitTypeId=r.ChildCompTypeID and sb.policyid=b.policyid and sb.PolEffDate=b.PolEffDate\n" +
                "join component sbc on sb.BenefitId=sbc.ComponentID and sb.BenefitTypeId=sbc.ComponentTypeID \n" +
                "join relation sbr on sbc. ComponentTypeID=sbr.ParentCompTypeID and sbc.ComponentID=sbr.ParentCompID\n" +
                "join Component bdl on bdl.ComponentID=sbr.ChildCompID and bdl.ComponentTypeID=sbr.ChildCompTypeID\n" +
                "where b.BenefitTypeId=18\n" +
                "and sb.BenefitTypeId=143\n" +
                "and bdl.ComponentTypeID=115\n" +
                "and b.InfoId=0 and sb.infoid=0 and b.policyid=:policyId and b.poleffdate=:polEffDate and b.benefitId=:benefitId and sb.benefitid=:subBenefitId"
    )
    fun findAllPolicySubBenefitsBdlsByPolicyIdAndPolEffDateandBenefitIdandbdlId(policyId:Int,polEffDate:LocalDate,benefitId:Int,subBenefitId:Int):List<PolicyBenefits>
}