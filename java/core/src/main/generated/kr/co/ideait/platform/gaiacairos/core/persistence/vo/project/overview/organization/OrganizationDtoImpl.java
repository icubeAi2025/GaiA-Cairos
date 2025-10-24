package kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.overview.organization;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnAttachments;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnContractOrg;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:18+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class OrganizationDtoImpl implements OrganizationDto {

    @Override
    public Organization fromCnContractOrgOutput(OrganizationMybatisParam.OrganizationListOutput organizationListOutput) {
        if ( organizationListOutput == null ) {
            return null;
        }

        Organization organization = new Organization();

        organization.setCntrctNo( organizationListOutput.getCntrctNo() );
        organization.setCntrctOrgId( organizationListOutput.getCntrctOrgId() );
        organization.setCnsttyCd( organizationListOutput.getCnsttyCd() );
        organization.setCorpNo( organizationListOutput.getCorpNo() );
        organization.setBsnsmnNo( organizationListOutput.getBsnsmnNo() );
        organization.setCorpNm( organizationListOutput.getCorpNm() );
        organization.setOfclType( organizationListOutput.getOfclType() );
        organization.setOfclId( organizationListOutput.getOfclId() );
        organization.setOfclNm( organizationListOutput.getOfclNm() );
        organization.setTelNo( organizationListOutput.getTelNo() );
        organization.setEmail( organizationListOutput.getEmail() );
        organization.setPstn( organizationListOutput.getPstn() );
        organization.setUseYn( organizationListOutput.getUseYn() );
        organization.setDltYn( organizationListOutput.getDltYn() );
        organization.setCnsttyCdNmKrn( organizationListOutput.getCnsttyCdNmKrn() );
        organization.setOfclTypeNmKrn( organizationListOutput.getOfclTypeNmKrn() );

        return organization;
    }

    @Override
    public Organization toOrganization(CnContractOrg cnContractOrg) {
        if ( cnContractOrg == null ) {
            return null;
        }

        Organization organization = new Organization();

        organization.setCntrctNo( cnContractOrg.getCntrctNo() );
        organization.setCntrctOrgId( cnContractOrg.getCntrctOrgId() );
        organization.setCnsttyCd( cnContractOrg.getCnsttyCd() );
        organization.setCorpNo( cnContractOrg.getCorpNo() );
        organization.setBsnsmnNo( cnContractOrg.getBsnsmnNo() );
        organization.setCorpNm( cnContractOrg.getCorpNm() );
        organization.setOfclType( cnContractOrg.getOfclType() );
        organization.setOfclId( cnContractOrg.getOfclId() );
        organization.setOfclNm( cnContractOrg.getOfclNm() );
        organization.setTelNo( cnContractOrg.getTelNo() );
        organization.setEmail( cnContractOrg.getEmail() );
        organization.setPstn( cnContractOrg.getPstn() );
        organization.setUseYn( cnContractOrg.getUseYn() );
        organization.setDltYn( cnContractOrg.getDltYn() );

        return organization;
    }

    @Override
    public CreateOrganization toCreateOrganization(CnContractOrg cnContractOrg) {
        if ( cnContractOrg == null ) {
            return null;
        }

        CreateOrganization createOrganization = new CreateOrganization();

        if ( cnContractOrg.getCntrctNo() != null ) {
            createOrganization.setCntrctNo( Integer.parseInt( cnContractOrg.getCntrctNo() ) );
        }
        createOrganization.setCntrctOrgId( cnContractOrg.getCntrctOrgId() );
        createOrganization.setCnsttyCd( cnContractOrg.getCnsttyCd() );
        createOrganization.setCorpNo( cnContractOrg.getCorpNo() );
        createOrganization.setBsnsmnNo( cnContractOrg.getBsnsmnNo() );
        createOrganization.setCorpNm( cnContractOrg.getCorpNm() );
        createOrganization.setOfclType( cnContractOrg.getOfclType() );
        createOrganization.setOfclId( cnContractOrg.getOfclId() );
        createOrganization.setOfclNm( cnContractOrg.getOfclNm() );
        createOrganization.setTelNo( cnContractOrg.getTelNo() );
        createOrganization.setEmail( cnContractOrg.getEmail() );
        createOrganization.setPstn( cnContractOrg.getPstn() );
        createOrganization.setUseYn( cnContractOrg.getUseYn() );

        return createOrganization;
    }

    @Override
    public Organization fromOrganization(CnContractOrg cnContractOrg) {
        if ( cnContractOrg == null ) {
            return null;
        }

        Organization organization = new Organization();

        organization.setCntrctNo( cnContractOrg.getCntrctNo() );
        organization.setCntrctOrgId( cnContractOrg.getCntrctOrgId() );
        organization.setCnsttyCd( cnContractOrg.getCnsttyCd() );
        organization.setCorpNo( cnContractOrg.getCorpNo() );
        organization.setBsnsmnNo( cnContractOrg.getBsnsmnNo() );
        organization.setCorpNm( cnContractOrg.getCorpNm() );
        organization.setOfclType( cnContractOrg.getOfclType() );
        organization.setOfclId( cnContractOrg.getOfclId() );
        organization.setOfclNm( cnContractOrg.getOfclNm() );
        organization.setTelNo( cnContractOrg.getTelNo() );
        organization.setEmail( cnContractOrg.getEmail() );
        organization.setPstn( cnContractOrg.getPstn() );
        organization.setUseYn( cnContractOrg.getUseYn() );
        organization.setDltYn( cnContractOrg.getDltYn() );

        return organization;
    }

    @Override
    public List<OrgAttachMent> toOrgAttachments(List<CnAttachments> cnAttachments) {
        if ( cnAttachments == null ) {
            return null;
        }

        List<OrgAttachMent> list = new ArrayList<OrgAttachMent>( cnAttachments.size() );
        for ( CnAttachments cnAttachments1 : cnAttachments ) {
            list.add( cnAttachmentsToOrgAttachMent( cnAttachments1 ) );
        }

        return list;
    }

    protected OrgAttachMent cnAttachmentsToOrgAttachMent(CnAttachments cnAttachments) {
        if ( cnAttachments == null ) {
            return null;
        }

        OrgAttachMent orgAttachMent = new OrgAttachMent();

        if ( cnAttachments.getFileNo() != null ) {
            orgAttachMent.setFileNo( cnAttachments.getFileNo() );
        }
        if ( cnAttachments.getSno() != null ) {
            orgAttachMent.setSno( cnAttachments.getSno() );
        }
        orgAttachMent.setFileNm( cnAttachments.getFileNm() );
        orgAttachMent.setFileDiskNm( cnAttachments.getFileDiskNm() );
        orgAttachMent.setFileDiskPath( cnAttachments.getFileDiskPath() );
        if ( cnAttachments.getFileSize() != null ) {
            orgAttachMent.setFileSize( cnAttachments.getFileSize() );
        }
        if ( cnAttachments.getFileHitNum() != null ) {
            orgAttachMent.setFileHitNum( cnAttachments.getFileHitNum() );
        }
        orgAttachMent.setDltYn( cnAttachments.getDltYn() );

        return orgAttachMent;
    }
}
