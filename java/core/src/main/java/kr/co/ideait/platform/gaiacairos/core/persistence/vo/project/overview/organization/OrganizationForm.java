package kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.overview.organization;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnContractOrg;
import kr.co.ideait.platform.gaiacairos.core.config.GlobalMapperConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(config = GlobalMapperConfig.class)
public interface OrganizationForm {

    // 조회
    OrganizationMybatisParam.OrganizationListInput toOrganizationListInput(OrganizationListGet organizationListGet);
    OrganizationMybatisParam.OrganizationInput toOrganizationInput(OrganizationListGet organizationListGet);

    // 등록
    CnContractOrg toCreateOrganization(CreateOrganization organization);

    // 수정
    void toUpdateOrganization(UpdateOrganization organization, @MappingTarget CnContractOrg cnContractOrg);

    // 조직검색
    @Data
    @EqualsAndHashCode(callSuper = false)
    public class OrganizationListGet {
        String cntrctNo;
        Integer cntrctOrgId;
    }

    // 조직 추가

    @Data
    public class CreateOrganization {
        String cntrctNo; // 계약번호
        Integer cntrctOrgId; // 계약조직도ID
        String cnsttyCd; // 공종코드
        String corpNo; // 업체번호
        String bsnsmnNo; // 사업자등록번호
        @NotBlank(message = "업체명은 필수 입력 값입니다.")
        String corpNm; // 업체명
        String ofclType; // 담당구분
        String ofclId; // 담당자ID
        @NotBlank(message = "담당자명은 필수 입력 값입니다.")
        String ofclNm; // 담당자명
        String telNo; // 휴대전화번호
        String email; // 이메일
        String pstn; // 직책
        @NotBlank(message = "사용여부는 필수 입력 값입니다.")
        @Size(max = 1)
        String useYn; // 사용여부
    }

    // 조직 수정
    @Data
    public class UpdateOrganization {
        String cntrctNo; // 계약번호
        Integer cntrctOrgId; // 계약조직도ID
        String cnsttyCd; // 공종코드
        String corpNo; // 업체번호
        String bsnsmnNo; // 사업자등록번호
        @NotBlank(message = "업체명은 필수 입력 값입니다.")
        String corpNm; // 업체명
        String ofclType; // 담당구분
        String ofclId; // 담당자ID
        @NotBlank(message = "담당자명은 필수 입력 값입니다.")
        String ofclNm; // 담당자명
        String telNo; // 휴대전화번호
        String email; // 이메일
        String pstn; // 직책
        @NotBlank(message = "사용여부는 필수 입력 값입니다.")
        @Size(max = 1)
        String useYn; // 사용여부
    }

    /* 조직 삭제 */
    @Data
    public class OrganizationList {
        List<CnContractOrg> organizationList; // 수정된 부분
    }

    @Data
    public class Organization { // 추가된 부분
        String cntrctNo;
        String cntrctOrgId;
    }
}
