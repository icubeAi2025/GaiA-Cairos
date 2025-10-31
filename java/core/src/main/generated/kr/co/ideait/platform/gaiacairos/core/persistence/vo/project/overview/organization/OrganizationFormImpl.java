package kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.overview.organization;

import javax.annotation.processing.Generated;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnContractOrg;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:16+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class OrganizationFormImpl implements OrganizationForm {

    @Override
    public OrganizationMybatisParam.OrganizationListInput toOrganizationListInput(OrganizationListGet organizationListGet) {
        if ( organizationListGet == null ) {
            return null;
        }

        OrganizationMybatisParam.OrganizationListInput organizationListInput = new OrganizationMybatisParam.OrganizationListInput();

        organizationListInput.setCntrctNo( organizationListGet.getCntrctNo() );

        return organizationListInput;
    }

    @Override
    public OrganizationMybatisParam.OrganizationInput toOrganizationInput(OrganizationListGet organizationListGet) {
        if ( organizationListGet == null ) {
            return null;
        }

        OrganizationMybatisParam.OrganizationInput organizationInput = new OrganizationMybatisParam.OrganizationInput();

        organizationInput.setCntrctNo( organizationListGet.getCntrctNo() );
        organizationInput.setCntrctOrgId( organizationListGet.getCntrctOrgId() );

        return organizationInput;
    }

    @Override
    public CnContractOrg toCreateOrganization(CreateOrganization organization) {
        if ( organization == null ) {
            return null;
        }

        CnContractOrg cnContractOrg = new CnContractOrg();

        cnContractOrg.setCntrctNo( organization.getCntrctNo() );
        cnContractOrg.setCntrctOrgId( organization.getCntrctOrgId() );
        cnContractOrg.setCnsttyCd( organization.getCnsttyCd() );
        cnContractOrg.setCorpNo( organization.getCorpNo() );
        cnContractOrg.setBsnsmnNo( organization.getBsnsmnNo() );
        cnContractOrg.setCorpNm( organization.getCorpNm() );
        cnContractOrg.setOfclType( organization.getOfclType() );
        cnContractOrg.setOfclId( organization.getOfclId() );
        cnContractOrg.setOfclNm( organization.getOfclNm() );
        cnContractOrg.setTelNo( organization.getTelNo() );
        cnContractOrg.setEmail( organization.getEmail() );
        cnContractOrg.setPstn( organization.getPstn() );
        cnContractOrg.setUseYn( organization.getUseYn() );

        return cnContractOrg;
    }

    @Override
    public void toUpdateOrganization(UpdateOrganization organization, CnContractOrg cnContractOrg) {
        if ( organization == null ) {
            return;
        }

        if ( organization.getCntrctNo() != null ) {
            cnContractOrg.setCntrctNo( organization.getCntrctNo() );
        }
        if ( organization.getCntrctOrgId() != null ) {
            cnContractOrg.setCntrctOrgId( organization.getCntrctOrgId() );
        }
        if ( organization.getCnsttyCd() != null ) {
            cnContractOrg.setCnsttyCd( organization.getCnsttyCd() );
        }
        if ( organization.getCorpNo() != null ) {
            cnContractOrg.setCorpNo( organization.getCorpNo() );
        }
        if ( organization.getBsnsmnNo() != null ) {
            cnContractOrg.setBsnsmnNo( organization.getBsnsmnNo() );
        }
        if ( organization.getCorpNm() != null ) {
            cnContractOrg.setCorpNm( organization.getCorpNm() );
        }
        if ( organization.getOfclType() != null ) {
            cnContractOrg.setOfclType( organization.getOfclType() );
        }
        if ( organization.getOfclId() != null ) {
            cnContractOrg.setOfclId( organization.getOfclId() );
        }
        if ( organization.getOfclNm() != null ) {
            cnContractOrg.setOfclNm( organization.getOfclNm() );
        }
        if ( organization.getTelNo() != null ) {
            cnContractOrg.setTelNo( organization.getTelNo() );
        }
        if ( organization.getEmail() != null ) {
            cnContractOrg.setEmail( organization.getEmail() );
        }
        if ( organization.getPstn() != null ) {
            cnContractOrg.setPstn( organization.getPstn() );
        }
        if ( organization.getUseYn() != null ) {
            cnContractOrg.setUseYn( organization.getUseYn() );
        }
    }
}
