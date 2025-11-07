package kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.contract.contractstatus;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnContractCompany;
import kr.co.ideait.platform.gaiacairos.core.config.GlobalMapperConfig;
import lombok.Data;
import org.mapstruct.Mapper;

@Mapper(config = GlobalMapperConfig.class)
public interface ContractCompanyDto {
    ContractCompany toCompany(ContractstatusMybatisParam.ContractcompanyOutput contractcompanyOutput);

    ContractCompany toContractCompany(CnContractCompany company);

    @Data
    class ContractCompany {
        String cntrctNo; // 계약번호
        Long cntrctId; // 계약도급 Id
        String cntrctNm; // 공사계약명
        String cnsttyCd; // 공종코드
        String cnsttyCdNmKrn;
        String bsnsmnNo; // 사업자등록번호
        String corpNm; // 업체명(도급사 이름)
        String corpNo; // 업체번호(도급사 번호)
        String telNo; // 전화번호(사무실 번호)
        String faxNo; // 팩스번호
        String corpAdrs; // 업체주소
        String corpCeo; // 업체대표자
        String ofclNm; // 담당자명
        Long shreRate; // 지분율
        String rprsYn;  // 대표여부
    }
}
