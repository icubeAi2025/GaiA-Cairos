package kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.contract.contractstatus;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnContract;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:17+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class ContractstatusDtoImpl implements ContractstatusDto {

    @Override
    public Contract fromCnContractOutput(ContractstatusMybatisParam.ContractstatusOutput contractstatusOutput) {
        if ( contractstatusOutput == null ) {
            return null;
        }

        Contract contract = new Contract();

        contract.setCntrctNo( contractstatusOutput.getCntrctNo() );
        contract.setPjtNo( contractstatusOutput.getPjtNo() );
        contract.setCntrctNm( contractstatusOutput.getCntrctNm() );
        contract.setMngCntrctNo( contractstatusOutput.getMngCntrctNo() );
        contract.setCntrctType( contractstatusOutput.getCntrctType() );
        contract.setMajorCnsttyCd( contractstatusOutput.getMajorCnsttyCd() );
        contract.setMajorCnsttyNmKrn( contractstatusOutput.getMajorCnsttyNmKrn() );
        contract.setCntrctDate( contractstatusOutput.getCntrctDate() );
        contract.setGrntyDate( contractstatusOutput.getGrntyDate() );
        contract.setCbgnDate( contractstatusOutput.getCbgnDate() );
        contract.setCcmpltDate( contractstatusOutput.getCcmpltDate() );
        if ( contractstatusOutput.getConPrd() != null ) {
            contract.setConPrd( contractstatusOutput.getConPrd() );
        }
        if ( contractstatusOutput.getCntrctCost() != null ) {
            contract.setCntrctCost( contractstatusOutput.getCntrctCost() );
        }
        if ( contractstatusOutput.getGrntyCost() != null ) {
            contract.setGrntyCost( contractstatusOutput.getGrntyCost() );
        }
        if ( contractstatusOutput.getVatRate() != null ) {
            contract.setVatRate( contractstatusOutput.getVatRate() );
        }
        if ( contractstatusOutput.getDfrcmpnstRate() != null ) {
            contract.setDfrcmpnstRate( contractstatusOutput.getDfrcmpnstRate() );
        }
        contract.setBsnsmnNo( contractstatusOutput.getBsnsmnNo() );
        contract.setCorpNm( contractstatusOutput.getCorpNm() );
        contract.setCorpAdrs( contractstatusOutput.getCorpAdrs() );
        contract.setTelNo( contractstatusOutput.getTelNo() );
        contract.setFaxNo( contractstatusOutput.getFaxNo() );
        contract.setCorpCeo( contractstatusOutput.getCorpCeo() );
        contract.setOfclNm( contractstatusOutput.getOfclNm() );
        contract.setOfclId( contractstatusOutput.getOfclId() );
        contract.setLatestCntrctChgDate( contractstatusOutput.getLatestCntrctChgDate() );

        return contract;
    }

    @Override
    public Contract toContract(CnContract contract) {
        if ( contract == null ) {
            return null;
        }

        Contract contract1 = new Contract();

        contract1.setCntrctNo( contract.getCntrctNo() );
        contract1.setPjtNo( contract.getPjtNo() );
        contract1.setCntrctNm( contract.getCntrctNm() );
        contract1.setMngCntrctNo( contract.getMngCntrctNo() );
        contract1.setCntrctType( contract.getCntrctType() );
        contract1.setMajorCnsttyCd( contract.getMajorCnsttyCd() );
        contract1.setCntrctDate( contract.getCntrctDate() );
        contract1.setGrntyDate( contract.getGrntyDate() );
        contract1.setCbgnDate( contract.getCbgnDate() );
        contract1.setCcmpltDate( contract.getCcmpltDate() );
        contract1.setConPrd( contract.getConPrd() );
        contract1.setCntrctCost( contract.getCntrctCost() );
        contract1.setGrntyCost( contract.getGrntyCost() );
        contract1.setVatRate( contract.getVatRate() );
        contract1.setDfrcmpnstRate( contract.getDfrcmpnstRate() );
        contract1.setBsnsmnNo( contract.getBsnsmnNo() );
        contract1.setCorpNm( contract.getCorpNm() );
        contract1.setCorpAdrs( contract.getCorpAdrs() );
        contract1.setCorpNo( contract.getCorpNo() );
        contract1.setTelNo( contract.getTelNo() );
        contract1.setFaxNo( contract.getFaxNo() );
        contract1.setCorpCeo( contract.getCorpCeo() );
        contract1.setOfclNm( contract.getOfclNm() );
        contract1.setOfclId( contract.getOfclId() );
        contract1.setThisCcmpltDate( contract.getThisCcmpltDate() );
        contract1.setThisConPrd( contract.getThisConPrd() );
        contract1.setThisCntrctCost( contract.getThisCntrctCost() );
        contract1.setCntrctDivCd( contract.getCntrctDivCd() );

        return contract1;
    }

    @Override
    public List<Contract> toContractList(List<CnContract> list) {
        if ( list == null ) {
            return null;
        }

        List<Contract> list1 = new ArrayList<Contract>( list.size() );
        for ( CnContract cnContract : list ) {
            list1.add( toContract( cnContract ) );
        }

        return list1;
    }
}
