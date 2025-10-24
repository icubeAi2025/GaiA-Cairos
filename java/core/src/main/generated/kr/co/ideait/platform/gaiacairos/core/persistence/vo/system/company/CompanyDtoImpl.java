package kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.company;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmCompany;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:17+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class CompanyDtoImpl implements CompanyDto {

    @Override
    public List<Company> fromSmCompanyList(List<SmCompany> smCompanyList) {
        if ( smCompanyList == null ) {
            return null;
        }

        List<Company> list = new ArrayList<Company>( smCompanyList.size() );
        for ( SmCompany smCompany : smCompanyList ) {
            list.add( fromSmCompany( smCompany ) );
        }

        return list;
    }

    @Override
    public List<Company> fromSmCompanyListOutput(List<CompanyMybatisParam.CompanyOutput> smCompanyListOutput) {
        if ( smCompanyListOutput == null ) {
            return null;
        }

        List<Company> list = new ArrayList<Company>( smCompanyListOutput.size() );
        for ( CompanyMybatisParam.CompanyOutput companyOutput : smCompanyListOutput ) {
            list.add( fromSmCompanyOutput( companyOutput ) );
        }

        return list;
    }

    @Override
    public Company fromSmCompanyOutput(CompanyMybatisParam.CompanyOutput smComapanyOutput) {
        if ( smComapanyOutput == null ) {
            return null;
        }

        Company company = new Company();

        company.setCorpNo( smComapanyOutput.getCorpNo() );
        company.setCompGrpCd( smComapanyOutput.getCompGrpCd() );
        company.setCmnCdNmKrn( smComapanyOutput.getCmnCdNmKrn() );
        company.setCmnCdNmEng( smComapanyOutput.getCmnCdNmEng() );
        company.setCompGrpCdNm( smComapanyOutput.getCompGrpCdNm() );
        company.setCompNm( smComapanyOutput.getCompNm() );
        company.setBsnsmnNo( smComapanyOutput.getBsnsmnNo() );
        company.setCorpCeo( smComapanyOutput.getCorpCeo() );
        company.setCompDscrpt( smComapanyOutput.getCompDscrpt() );
        company.setPstnNm( smComapanyOutput.getPstnNm() );
        company.setMngNm( smComapanyOutput.getMngNm() );
        company.setCompTelno( smComapanyOutput.getCompTelno() );
        company.setCompFaxno( smComapanyOutput.getCompFaxno() );
        company.setCompAdrs( smComapanyOutput.getCompAdrs() );
        company.setUseYn( smComapanyOutput.getUseYn() );
        company.setChgDt( smComapanyOutput.getChgDt() );

        return company;
    }

    @Override
    public Company fromSmCompany(SmCompany smComapany) {
        if ( smComapany == null ) {
            return null;
        }

        Company company = new Company();

        company.setCorpNo( smComapany.getCorpNo() );
        company.setCompGrpCd( smComapany.getCompGrpCd() );
        company.setCompNm( smComapany.getCompNm() );
        company.setBsnsmnNo( smComapany.getBsnsmnNo() );
        company.setCorpCeo( smComapany.getCorpCeo() );
        company.setCompDscrpt( smComapany.getCompDscrpt() );
        company.setPstnNm( smComapany.getPstnNm() );
        company.setMngNm( smComapany.getMngNm() );
        company.setCompTelno( smComapany.getCompTelno() );
        company.setCompFaxno( smComapany.getCompFaxno() );
        company.setCompAdrs( smComapany.getCompAdrs() );
        company.setUseYn( smComapany.getUseYn() );
        if ( smComapany.getChgDt() != null ) {
            company.setChgDt( DateTimeFormatter.ISO_LOCAL_DATE_TIME.format( smComapany.getChgDt() ) );
        }

        return company;
    }

    @Override
    public IdeaCompany fromIdeaCompanyOutput(CompanyMybatisParam.IdeaCompanyOutput output) {
        if ( output == null ) {
            return null;
        }

        IdeaCompany ideaCompany = new IdeaCompany();

        ideaCompany.setPageable( output.getPageable() );
        ideaCompany.setCorpNo( output.getCorpNo() );
        ideaCompany.setBizno( output.getBizno() );
        ideaCompany.setCorprtNo( output.getCorprtNo() );
        ideaCompany.setCeoNm( output.getCeoNm() );
        ideaCompany.setCompNm( output.getCompNm() );
        ideaCompany.setCompTelno( output.getCompTelno() );
        ideaCompany.setZipcd( output.getZipcd() );
        ideaCompany.setBaseAdrs( output.getBaseAdrs() );
        ideaCompany.setDtlAdrs( output.getDtlAdrs() );
        ideaCompany.setUntyatchFileNo( output.getUntyatchFileNo() );
        ideaCompany.setCertiCd( output.getCertiCd() );
        ideaCompany.setCorpDivCd( output.getCorpDivCd() );
        ideaCompany.setEletxbilAdrs( output.getEletxbilAdrs() );
        ideaCompany.setEletxbilIndtpNm( output.getEletxbilIndtpNm() );
        ideaCompany.setEletxbilBzcndNm( output.getEletxbilBzcndNm() );
        ideaCompany.setEletxbilBizno( output.getEletxbilBizno() );
        ideaCompany.setEletxbilCompNm( output.getEletxbilCompNm() );
        ideaCompany.setEletxbilCeoNm( output.getEletxbilCeoNm() );
        ideaCompany.setEletxbilEmail1( output.getEletxbilEmail1() );
        ideaCompany.setCertiDate( output.getCertiDate() );
        ideaCompany.setCertEndDate( output.getCertEndDate() );
        ideaCompany.setSaasUseCnrlDivCd( output.getSaasUseCnrlDivCd() );
        ideaCompany.setRgstrId( output.getRgstrId() );
        ideaCompany.setRgstDt( output.getRgstDt() );
        ideaCompany.setChgId( output.getChgId() );
        ideaCompany.setChgDt( output.getChgDt() );

        return ideaCompany;
    }

    @Override
    public PcesCompany fromPcesCompanyOutput(CompanyMybatisParam.PcesCompanyOutput output) {
        if ( output == null ) {
            return null;
        }

        PcesCompany pcesCompany = new PcesCompany();

        pcesCompany.setPageable( output.getPageable() );
        pcesCompany.setCorpNo( output.getCorpNo() );
        pcesCompany.setInsttCd( output.getInsttCd() );
        pcesCompany.setInsttNm( output.getInsttNm() );
        pcesCompany.setBizno( output.getBizno() );
        pcesCompany.setCeoNm( output.getCeoNm() );
        pcesCompany.setTelNo( output.getTelNo() );
        pcesCompany.setAdrs( output.getAdrs() );
        pcesCompany.setIp( output.getIp() );
        pcesCompany.setCorpDivCd( output.getCorpDivCd() );
        pcesCompany.setOrderOrgId( output.getOrderOrgId() );
        pcesCompany.setRgstrId( output.getRgstrId() );
        pcesCompany.setRgstDt( output.getRgstDt() );
        pcesCompany.setChgId( output.getChgId() );
        pcesCompany.setChgDt( output.getChgDt() );
        pcesCompany.setCompNm( output.getCompNm() );

        return pcesCompany;
    }
}
