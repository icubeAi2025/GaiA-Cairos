package kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.contract.contractstatus;

import javax.annotation.processing.Generated;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnContractCompany;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:17+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class ContractCompanyDtoImpl implements ContractCompanyDto {

    @Override
    public ContractCompany toCompany(ContractstatusMybatisParam.ContractcompanyOutput contractcompanyOutput) {
        if ( contractcompanyOutput == null ) {
            return null;
        }

        ContractCompany contractCompany = new ContractCompany();

        contractCompany.setCntrctNo( contractcompanyOutput.getCntrctNo() );
        contractCompany.setCntrctId( contractcompanyOutput.getCntrctId() );
        contractCompany.setCntrctNm( contractcompanyOutput.getCntrctNm() );
        contractCompany.setCnsttyCd( contractcompanyOutput.getCnsttyCd() );
        contractCompany.setCnsttyCdNmKrn( contractcompanyOutput.getCnsttyCdNmKrn() );
        contractCompany.setBsnsmnNo( contractcompanyOutput.getBsnsmnNo() );
        contractCompany.setCorpNm( contractcompanyOutput.getCorpNm() );
        contractCompany.setCorpNo( contractcompanyOutput.getCorpNo() );
        contractCompany.setTelNo( contractcompanyOutput.getTelNo() );
        contractCompany.setFaxNo( contractcompanyOutput.getFaxNo() );
        contractCompany.setCorpAdrs( contractcompanyOutput.getCorpAdrs() );
        contractCompany.setCorpCeo( contractcompanyOutput.getCorpCeo() );
        contractCompany.setOfclNm( contractcompanyOutput.getOfclNm() );
        contractCompany.setShreRate( contractcompanyOutput.getShreRate() );
        contractCompany.setRprsYn( contractcompanyOutput.getRprsYn() );

        return contractCompany;
    }

    @Override
    public ContractCompany toContractCompany(CnContractCompany company) {
        if ( company == null ) {
            return null;
        }

        ContractCompany contractCompany = new ContractCompany();

        contractCompany.setCntrctNo( company.getCntrctNo() );
        contractCompany.setCntrctId( company.getCntrctId() );
        contractCompany.setCnsttyCd( company.getCnsttyCd() );
        contractCompany.setBsnsmnNo( company.getBsnsmnNo() );
        contractCompany.setCorpNm( company.getCorpNm() );
        contractCompany.setCorpNo( company.getCorpNo() );
        contractCompany.setTelNo( company.getTelNo() );
        contractCompany.setFaxNo( company.getFaxNo() );
        contractCompany.setCorpAdrs( company.getCorpAdrs() );
        contractCompany.setCorpCeo( company.getCorpCeo() );
        contractCompany.setOfclNm( company.getOfclNm() );
        contractCompany.setShreRate( (long) company.getShreRate() );
        contractCompany.setRprsYn( company.getRprsYn() );

        return contractCompany;
    }
}
