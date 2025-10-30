package kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.company;

import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmCompany;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisPageable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = ComponentModel.SPRING)
public interface CompanyDto {

    List<Company> fromSmCompanyList(List<SmCompany> smCompanyList);

    List<Company> fromSmCompanyListOutput(List<CompanyMybatisParam.CompanyOutput> smCompanyListOutput);

    Company fromSmCompanyOutput(CompanyMybatisParam.CompanyOutput smComapanyOutput);

    Company fromSmCompany(SmCompany smComapany);

    IdeaCompany fromIdeaCompanyOutput(CompanyMybatisParam.IdeaCompanyOutput output);

    PcesCompany fromPcesCompanyOutput(CompanyMybatisParam.PcesCompanyOutput output);

    @Data
    class Company {
        String corpNo;
        String compGrpCd;
        String cmnCdNmKrn;
        String cmnCdNmEng;
        String compGrpCdNm;
        String compNm;
        String bsnsmnNo;
        String corpCeo;
        String compDscrpt;
        String pstnNm;
        String mngNm;
        String compTelno;
        String compFaxno;
        String compAdrs;
        String useYn;
        // LocalDateTime chgDt;
        String chgDt;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    class IdeaCompany extends CompanyMybatisParam.IdeaCompanyOutput {
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    class PcesCompany extends CompanyMybatisParam.PcesCompanyOutput {
    }
}
