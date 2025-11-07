package kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.overview.organization;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnAttachments;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnContractOrg;
import kr.co.ideait.platform.gaiacairos.core.config.GlobalMapperConfig;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = GlobalMapperConfig.class)
public interface OrganizationDto {
    Organization fromCnContractOrgOutput(OrganizationMybatisParam.OrganizationListOutput organizationListOutput);

    Organization toOrganization(CnContractOrg cnContractOrg);

    CreateOrganization toCreateOrganization(CnContractOrg cnContractOrg);

    Organization fromOrganization(CnContractOrg cnContractOrg);

    List<OrgAttachMent> toOrgAttachments(List<CnAttachments> cnAttachments);

    @Data
    /**
     * InnerOrganizationDto
     */
    public class Organization {
        @Description(name = "계약번호", description = "", type = Description.TYPE.FIELD)
        String cntrctNo; // 계약번호
        @Description(name = "계약조직도ID", description = "", type = Description.TYPE.FIELD)
        Integer cntrctOrgId; // 계약조직도ID
        @Description(name = "공종코드", description = "", type = Description.TYPE.FIELD)
        String cnsttyCd; // 공종코드
        @Description(name = "업체번호", description = "", type = Description.TYPE.FIELD)
        String corpNo; // 업체번호
        @Description(name = "사업자등록번호", description = "", type = Description.TYPE.FIELD)
        String bsnsmnNo; // 사업자등록번호
        @Description(name = "업체명", description = "", type = Description.TYPE.FIELD)
        String corpNm; // 업체명
        @Description(name = "담당구분", description = "", type = Description.TYPE.FIELD)
        String ofclType; // 담당구분
        @Description(name = "담당자ID", description = "", type = Description.TYPE.FIELD)
        String ofclId; // 담당자ID
        @Description(name = "담당자명", description = "", type = Description.TYPE.FIELD)
        String ofclNm; // 담당자명
        @Description(name = "휴대전화번호", description = "", type = Description.TYPE.FIELD)
        String telNo; // 휴대전화번호
        @Description(name = "이메일", description = "", type = Description.TYPE.FIELD)
        String email; // 이메일
        @Description(name = "직책", description = "", type = Description.TYPE.FIELD)
        String pstn; // 직책
        @Description(name = "사용여부", description = "", type = Description.TYPE.FIELD)
        String useYn; // 사용여부
        @Description(name = "삭제여부", description = "", type = Description.TYPE.FIELD)
        String dltYn;
        @Description(name = "공종코드 한글명", description = "", type = Description.TYPE.FIELD)
        String cnsttyCdNmKrn;
        @Description(name = "담당구분 한글명", description = "", type = Description.TYPE.FIELD)
        String ofclTypeNmKrn;
    }

    @Data
    class CreateOrganization {
        @Description(name = "계약번호", description = "", type = Description.TYPE.FIELD)
        Integer cntrctNo; // 계약번호
        @Description(name = "계약조직도ID", description = "", type = Description.TYPE.FIELD)
        Integer cntrctOrgId; // 계약조직도ID
        @Description(name = "공종코드", description = "", type = Description.TYPE.FIELD)
        String cnsttyCd; // 공종코드
        @Description(name = "업체번호", description = "", type = Description.TYPE.FIELD)
        String corpNo; // 업체번호
        @Description(name = "사업자등록번호", description = "", type = Description.TYPE.FIELD)
        String bsnsmnNo; // 사업자등록번호
        @Description(name = "업체명", description = "", type = Description.TYPE.FIELD)
        String corpNm; // 업체명
        @Description(name = "담당구분", description = "", type = Description.TYPE.FIELD)
        String ofclType; // 담당구분
        @Description(name = "담당자ID", description = "", type = Description.TYPE.FIELD)
        String ofclId; // 담당자ID
        @Description(name = "담당자명", description = "", type = Description.TYPE.FIELD)
        String ofclNm; // 담당자명
        @Description(name = "휴대전화번호", description = "", type = Description.TYPE.FIELD)
        String telNo; // 휴대전화번호
        @Description(name = "이메일", description = "", type = Description.TYPE.FIELD)
        String email; // 이메일
        @Description(name = "직책", description = "", type = Description.TYPE.FIELD)
        String pstn; // 직책
        @Description(name = "사용여부", description = "", type = Description.TYPE.FIELD)
        String useYn; // 사용여부
    }

    @Data
    public class OrgAttachMent {
        @Description(name = "파일 번호", description = "", type = Description.TYPE.FIELD)
        int fileNo;
        @Description(name = "순번", description = "", type = Description.TYPE.FIELD)
        int sno;
        @Description(name = "파일 이름", description = "", type = Description.TYPE.FIELD)
        String fileNm;
        @Description(name = "파일 DISK 이름", description = "", type = Description.TYPE.FIELD)
        String fileDiskNm;
        @Description(name = "파일 DISK 경로", description = "", type = Description.TYPE.FIELD)
        String fileDiskPath;
        @Description(name = "파일 사이즈", description = "", type = Description.TYPE.FIELD)
        int fileSize;
        @Description(name = "조회수", description = "", type = Description.TYPE.FIELD)
        int fileHitNum;
        @Description(name = "삭제여부", description = "", type = Description.TYPE.FIELD)
        String dltYn;
    }
}
