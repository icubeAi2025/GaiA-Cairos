package kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.contract.contractstatus;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnContractCompany;
import lombok.Data;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = ComponentModel.SPRING)
public interface ContractCompanyForm {

    ContractstatusMybatisParam.ContractcompanyListInput toContractcompanyListInput(ContractCompanyListGet companyListGet);

    CnContractCompany toCnContractCompany(ContractCompany company);

    void updateContractCompany(CompanyUpdate company, @MappingTarget CnContractCompany cnComPany);

    @Data
    class ContractCompanyListGet{
        String cntrctNo;
    }

    @Data
    class ContractCompany {
        String cntrctNo; // 계약번호
        String cntrctNm; // 공사계약명
        String cnsttyCd; // 공종코드
        String bsnsmnNo; // 사업자등록번호
        String corpNo;
        String corpNm; // 업체명(도급사 이름)
        String telNo; // 전화번호(사무실 번호)
        String faxNo; // 팩스번호
        String corpAdrs; // 업체주소
        String corpCeo; // 업체대표자
        Long shreRate; // 지분율
        String ofclNm; // 담당자명
        String ofclId; // 담당자아이디
        String rprsYn;  // 대표 여부
    }

    @Data
    class CompanyUpdate {    
        Long cntrctId;  // 계약도급 ID
        String cntrctNo; // 계약번호
        String cnsttyCd; // 공종코드
        String bsnsmnNo; // 사업자등록번호
        String corpNm; // 업체명(도급사 이름)
        String corpNo;
        String telNo; // 전화번호(사무실 번호)
        String faxNo; // 팩스번호
        String corpAdrs; // 업체주소
        String corpCeo; // 업체대표자
        Long shreRate; // 지분율
        String ofclNm; // 담당자명
        String ofclId; // 담당자아이디
        String rprsYn;  // 대표 여부
    }

    @Data
    class CompanyList {
        List<CnContractCompany> companyList;
        String cntrctNo;
    }
}
