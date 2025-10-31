package kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.contract.contractstatus;

import javax.annotation.processing.Generated;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnContractChange;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:17+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class ContractChangeFormImpl implements ContractChangeForm {

    @Override
    public ContractstatusMybatisParam.ContractchangeListInput toContractchangeListInput(ContractChangeListGet changeListGet) {
        if ( changeListGet == null ) {
            return null;
        }

        ContractstatusMybatisParam.ContractchangeListInput contractchangeListInput = new ContractstatusMybatisParam.ContractchangeListInput();

        contractchangeListInput.setCntrctNo( changeListGet.getCntrctNo() );

        return contractchangeListInput;
    }

    @Override
    public CnContractChange toContractChange(ChangeCreate change) {
        if ( change == null ) {
            return null;
        }

        CnContractChange cnContractChange = new CnContractChange();

        cnContractChange.setCntrctNo( change.getCntrctNo() );
        cnContractChange.setCntrctChgNo( change.getCntrctChgNo() );
        cnContractChange.setCntrctChgType( change.getCntrctChgType() );
        cnContractChange.setChgApprDate( change.getChgApprDate() );
        cnContractChange.setCntrctChgDate( change.getCntrctChgDate() );
        cnContractChange.setChgCbgnDate( change.getChgCbgnDate() );
        cnContractChange.setChgConPrd( change.getChgConPrd() );
        cnContractChange.setCntrctAmt( change.getCntrctAmt() );
        cnContractChange.setChgThisCbgnDate( change.getChgThisCbgnDate() );
        cnContractChange.setChgThisConPrd( change.getChgThisConPrd() );
        cnContractChange.setThisCntrctAmt( change.getThisCntrctAmt() );
        cnContractChange.setRmrk( change.getRmrk() );
        cnContractChange.setLastChgYn( change.getLastChgYn() );
        if ( change.getCntrctPhase() != null ) {
            cnContractChange.setCntrctPhase( Integer.parseInt( change.getCntrctPhase() ) );
        }

        return cnContractChange;
    }

    @Override
    public void updateContractChange(ChangeUpdate change, CnContractChange cnChange) {
        if ( change == null ) {
            return;
        }

        cnChange.setCntrctNo( change.getCntrctNo() );
        cnChange.setCntrctChgNo( change.getCntrctChgNo() );
        cnChange.setCntrctChgType( change.getCntrctChgType() );
        cnChange.setChgApprDate( change.getChgApprDate() );
        cnChange.setCntrctChgDate( change.getCntrctChgDate() );
        cnChange.setChgCbgnDate( change.getChgCbgnDate() );
        cnChange.setChgConPrd( change.getChgConPrd() );
        cnChange.setCntrctAmt( change.getCntrctAmt() );
        cnChange.setChgThisCbgnDate( change.getChgThisCbgnDate() );
        cnChange.setChgThisConPrd( change.getChgThisConPrd() );
        cnChange.setThisCntrctAmt( change.getThisCntrctAmt() );
        cnChange.setDfrcmpnstRate( change.getDfrcmpnstRate() );
        cnChange.setVatRate( change.getVatRate() );
        cnChange.setRmrk( change.getRmrk() );
        cnChange.setLastChgYn( change.getLastChgYn() );
        if ( change.getCntrctPhase() != null ) {
            cnChange.setCntrctPhase( change.getCntrctPhase().intValue() );
        }
        else {
            cnChange.setCntrctPhase( null );
        }
    }
}
