package kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.contract.contractstatus;

import javax.annotation.processing.Generated;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnContract;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnContractChange;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnContractCompany;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:17+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class ContractstatusFormImpl implements ContractstatusForm {

    @Override
    public ContractstatusMybatisParam.ContractstatusListInput toContractstatusListInput(ContractListGet contractListGet) {
        if ( contractListGet == null ) {
            return null;
        }

        ContractstatusMybatisParam.ContractstatusListInput contractstatusListInput = new ContractstatusMybatisParam.ContractstatusListInput();

        contractstatusListInput.setPjtNo( contractListGet.getPjtNo() );

        return contractstatusListInput;
    }

    @Override
    public CnContract toCnContract(CreateContract contract) {
        if ( contract == null ) {
            return null;
        }

        CnContract cnContract = new CnContract();

        cnContract.setPjtNo( contract.getPjtNo() );
        cnContract.setMngCntrctNo( contract.getMngCntrctNo() );
        cnContract.setCntrctNm( contract.getCntrctNm() );
        cnContract.setCntrctType( contract.getCntrctType() );
        cnContract.setCntrctDivCd( contract.getCntrctDivCd() );
        cnContract.setMajorCnsttyCd( contract.getMajorCnsttyCd() );
        cnContract.setCntrctDate( contract.getCntrctDate() );
        cnContract.setGrntyDate( contract.getGrntyDate() );
        cnContract.setCbgnDate( contract.getCbgnDate() );
        cnContract.setCcmpltDate( contract.getCcmpltDate() );
        cnContract.setConPrd( contract.getConPrd() );
        cnContract.setThisCcmpltDate( contract.getThisCcmpltDate() );
        cnContract.setThisConPrd( contract.getThisConPrd() );
        cnContract.setCntrctCost( contract.getCntrctCost() );
        cnContract.setThisCntrctCost( contract.getThisCntrctCost() );
        cnContract.setGrntyCost( contract.getGrntyCost() );
        cnContract.setVatRate( contract.getVatRate() );
        cnContract.setDfrcmpnstRate( contract.getDfrcmpnstRate() );
        cnContract.setCorpNo( contract.getCorpNo() );
        cnContract.setBsnsmnNo( contract.getBsnsmnNo() );
        cnContract.setCorpNm( contract.getCorpNm() );
        cnContract.setCorpAdrs( contract.getCorpAdrs() );
        cnContract.setTelNo( contract.getTelNo() );
        cnContract.setFaxNo( contract.getFaxNo() );
        cnContract.setCorpCeo( contract.getCorpCeo() );
        cnContract.setOfclId( contract.getOfclId() );
        cnContract.setOfclNm( contract.getOfclNm() );

        return cnContract;
    }

    @Override
    public CnContractCompany toCnCompany(CreateContract contract) {
        if ( contract == null ) {
            return null;
        }

        CnContractCompany cnContractCompany = new CnContractCompany();

        cnContractCompany.setCorpNo( contract.getCorpNo() );
        cnContractCompany.setBsnsmnNo( contract.getBsnsmnNo() );
        cnContractCompany.setCorpNm( contract.getCorpNm() );
        cnContractCompany.setCorpAdrs( contract.getCorpAdrs() );
        cnContractCompany.setTelNo( contract.getTelNo() );
        cnContractCompany.setFaxNo( contract.getFaxNo() );
        cnContractCompany.setCorpCeo( contract.getCorpCeo() );
        cnContractCompany.setOfclNm( contract.getOfclNm() );
        cnContractCompany.setOfclId( contract.getOfclId() );

        return cnContractCompany;
    }

    @Override
    public CnContractChange toCnChange(CreateContract contract) {
        if ( contract == null ) {
            return null;
        }

        CnContractChange cnContractChange = new CnContractChange();

        cnContractChange.setDfrcmpnstRate( contract.getDfrcmpnstRate() );
        cnContractChange.setVatRate( contract.getVatRate() );

        return cnContractChange;
    }

    @Override
    public void updateContract(ContractUpdate contract, CnContract cnContract) {
        if ( contract == null ) {
            return;
        }

        if ( contract.getMngCntrctNo() != null ) {
            cnContract.setMngCntrctNo( contract.getMngCntrctNo() );
        }
        if ( contract.getCntrctNm() != null ) {
            cnContract.setCntrctNm( contract.getCntrctNm() );
        }
        if ( contract.getCntrctType() != null ) {
            cnContract.setCntrctType( contract.getCntrctType() );
        }
        if ( contract.getCntrctDivCd() != null ) {
            cnContract.setCntrctDivCd( contract.getCntrctDivCd() );
        }
        if ( contract.getMajorCnsttyCd() != null ) {
            cnContract.setMajorCnsttyCd( contract.getMajorCnsttyCd() );
        }
        if ( contract.getCntrctDate() != null ) {
            cnContract.setCntrctDate( contract.getCntrctDate() );
        }
        if ( contract.getGrntyDate() != null ) {
            cnContract.setGrntyDate( contract.getGrntyDate() );
        }
        if ( contract.getCbgnDate() != null ) {
            cnContract.setCbgnDate( contract.getCbgnDate() );
        }
        if ( contract.getCcmpltDate() != null ) {
            cnContract.setCcmpltDate( contract.getCcmpltDate() );
        }
        cnContract.setConPrd( contract.getConPrd() );
        if ( contract.getThisCcmpltDate() != null ) {
            cnContract.setThisCcmpltDate( contract.getThisCcmpltDate() );
        }
        cnContract.setThisConPrd( contract.getThisConPrd() );
        cnContract.setCntrctCost( contract.getCntrctCost() );
        cnContract.setThisCntrctCost( contract.getThisCntrctCost() );
        cnContract.setGrntyCost( contract.getGrntyCost() );
        cnContract.setVatRate( contract.getVatRate() );
        cnContract.setDfrcmpnstRate( contract.getDfrcmpnstRate() );
        if ( contract.getCorpNo() != null ) {
            cnContract.setCorpNo( contract.getCorpNo() );
        }
        if ( contract.getBsnsmnNo() != null ) {
            cnContract.setBsnsmnNo( contract.getBsnsmnNo() );
        }
        if ( contract.getCorpNm() != null ) {
            cnContract.setCorpNm( contract.getCorpNm() );
        }
        if ( contract.getCorpAdrs() != null ) {
            cnContract.setCorpAdrs( contract.getCorpAdrs() );
        }
        if ( contract.getTelNo() != null ) {
            cnContract.setTelNo( contract.getTelNo() );
        }
        if ( contract.getFaxNo() != null ) {
            cnContract.setFaxNo( contract.getFaxNo() );
        }
        if ( contract.getCorpCeo() != null ) {
            cnContract.setCorpCeo( contract.getCorpCeo() );
        }
        if ( contract.getOfclId() != null ) {
            cnContract.setOfclId( contract.getOfclId() );
        }
        if ( contract.getOfclNm() != null ) {
            cnContract.setOfclNm( contract.getOfclNm() );
        }
    }
}
