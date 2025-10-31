package kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.contract.contractstatus;

import javax.annotation.processing.Generated;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnContractCompany;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:16+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class ContractCompanyFormImpl implements ContractCompanyForm {

    @Override
    public ContractstatusMybatisParam.ContractcompanyListInput toContractcompanyListInput(ContractCompanyListGet companyListGet) {
        if ( companyListGet == null ) {
            return null;
        }

        ContractstatusMybatisParam.ContractcompanyListInput contractcompanyListInput = new ContractstatusMybatisParam.ContractcompanyListInput();

        contractcompanyListInput.setCntrctNo( companyListGet.getCntrctNo() );

        return contractcompanyListInput;
    }

    @Override
    public CnContractCompany toCnContractCompany(ContractCompany company) {
        if ( company == null ) {
            return null;
        }

        CnContractCompany cnContractCompany = new CnContractCompany();

        cnContractCompany.setCntrctNo( company.getCntrctNo() );
        cnContractCompany.setCnsttyCd( company.getCnsttyCd() );
        cnContractCompany.setCorpNo( company.getCorpNo() );
        cnContractCompany.setBsnsmnNo( company.getBsnsmnNo() );
        cnContractCompany.setCorpNm( company.getCorpNm() );
        cnContractCompany.setCorpAdrs( company.getCorpAdrs() );
        cnContractCompany.setTelNo( company.getTelNo() );
        cnContractCompany.setFaxNo( company.getFaxNo() );
        cnContractCompany.setCorpCeo( company.getCorpCeo() );
        if ( company.getShreRate() != null ) {
            cnContractCompany.setShreRate( company.getShreRate() );
        }
        cnContractCompany.setRprsYn( company.getRprsYn() );
        cnContractCompany.setOfclNm( company.getOfclNm() );
        cnContractCompany.setOfclId( company.getOfclId() );

        return cnContractCompany;
    }

    @Override
    public void updateContractCompany(CompanyUpdate company, CnContractCompany cnComPany) {
        if ( company == null ) {
            return;
        }

        cnComPany.setCntrctNo( company.getCntrctNo() );
        cnComPany.setCntrctId( company.getCntrctId() );
        cnComPany.setCnsttyCd( company.getCnsttyCd() );
        cnComPany.setCorpNo( company.getCorpNo() );
        cnComPany.setBsnsmnNo( company.getBsnsmnNo() );
        cnComPany.setCorpNm( company.getCorpNm() );
        cnComPany.setCorpAdrs( company.getCorpAdrs() );
        cnComPany.setTelNo( company.getTelNo() );
        cnComPany.setFaxNo( company.getFaxNo() );
        cnComPany.setCorpCeo( company.getCorpCeo() );
        if ( company.getShreRate() != null ) {
            cnComPany.setShreRate( company.getShreRate() );
        }
        cnComPany.setRprsYn( company.getRprsYn() );
        cnComPany.setOfclNm( company.getOfclNm() );
        cnComPany.setOfclId( company.getOfclId() );
    }
}
