package kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.overview.organization;

import org.apache.ibatis.type.Alias;

import lombok.Data;

public interface OrganizationMybatisParam {

    @Data
    @Alias("organizationListInput")
    public class OrganizationListInput {
        String cntrctNo;
        String cmnGrpCdMajorCnstty;
        String cmnGrpCdOfcl;
    }

    @Data
    @Alias("organizationListOutput")
    public class OrganizationListOutput {
        String cntrctNo; // 계약번호
        Integer cntrctOrgId; // 계약조직도ID
        String cnsttyCd; // 공종코드
        String corpNo; // 업체번호
        String bsnsmnNo; // 사업자등록번호
        String corpNm; // 업체명
        String ofclType; // 담당구분
        String ofclId; // 담당자ID
        String ofclNm; // 담당자명
        String telNo; // 휴대전화번호
        String email; // 이메일
        String pstn; // 직책
        String useYn; // 사용여부
        String dltYn;
        String cnsttyCdNmKrn;
        String ofclTypeNmKrn;
    }

    @Data
    @Alias("organizationInput")
    public class OrganizationInput {
        String cntrctNo;
        Integer cntrctOrgId;

        String cmnGrpCdMajorCnstty;
        String cmnGrpCdOfcl;
    }

    @Data
    @Alias("organizationOutput")
    public class OrganizationOutput {
        String cnsttyCd; // 공종코드
        String corpNo; // 업체번호
        String bsnsmnNo; // 사업자등록번호
        String corpNm; // 업체명
        String ofclType; // 담당구분
        String ofclId; // 담당자ID
        String ofclNm; // 담당자명
        String telNo; // 휴대전화번호
        String email; // 이메일
        String pstn; // 직책
        String useYn; // 사용여부
        String dltYn;
        String cnsttyCdNm;
        String ofclTypeNm;
    }

}
