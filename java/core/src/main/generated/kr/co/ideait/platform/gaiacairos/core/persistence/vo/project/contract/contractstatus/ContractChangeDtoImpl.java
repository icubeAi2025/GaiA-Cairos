package kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.contract.contractstatus;

import javax.annotation.processing.Generated;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnContractChange;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:18+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class ContractChangeDtoImpl implements ContractChangeDto {

    @Override
    public ContractChangeList toChangeList(ContractstatusMybatisParam.ContractchangeOutputList contractchangeOutput) {
        if ( contractchangeOutput == null ) {
            return null;
        }

        ContractChangeList contractChangeList = new ContractChangeList();

        contractChangeList.setCntrctChgId( contractchangeOutput.getCntrctChgId() );
        contractChangeList.setCntrctNo( contractchangeOutput.getCntrctNo() );
        contractChangeList.setCntrctChgNo( contractchangeOutput.getCntrctChgNo() );
        contractChangeList.setCntrctChgDate( contractchangeOutput.getCntrctChgDate() );
        contractChangeList.setCntrctAmt( contractchangeOutput.getCntrctAmt() );
        contractChangeList.setCbgnDateRange( contractchangeOutput.getCbgnDateRange() );
        contractChangeList.setCntrctChgTypeNmKrn( contractchangeOutput.getCntrctChgTypeNmKrn() );
        contractChangeList.setCntrctChgType( contractchangeOutput.getCntrctChgType() );
        contractChangeList.setRmrk( contractchangeOutput.getRmrk() );
        contractChangeList.setLastChgYn( contractchangeOutput.getLastChgYn() );
        if ( contractchangeOutput.getCntrctDivCd() != null ) {
            contractChangeList.setCntrctDivCd( Long.parseLong( contractchangeOutput.getCntrctDivCd() ) );
        }
        contractChangeList.setChgThisCbgnDate( contractchangeOutput.getChgThisCbgnDate() );

        return contractChangeList;
    }

    @Override
    public ContractChange toChange(ContractstatusMybatisParam.ContractchangeOutput contractchangeOutput) {
        if ( contractchangeOutput == null ) {
            return null;
        }

        ContractChange contractChange = new ContractChange();

        contractChange.setCntrctNo( contractchangeOutput.getCntrctNo() );
        contractChange.setCntrctNm( contractchangeOutput.getCntrctNm() );
        contractChange.setMajorCnsttyCd( contractchangeOutput.getMajorCnsttyCd() );
        contractChange.setMajorCnsttyNm( contractchangeOutput.getMajorCnsttyNm() );
        contractChange.setMngCntrctNo( contractchangeOutput.getMngCntrctNo() );
        contractChange.setCorpNm( contractchangeOutput.getCorpNm() );
        contractChange.setCbgnDate( contractchangeOutput.getCbgnDate() );
        contractChange.setCntrctCost( contractchangeOutput.getCntrctCost() );
        contractChange.setCntrctPhase( contractchangeOutput.getCntrctPhase() );
        contractChange.setCntrctChgNo( contractchangeOutput.getCntrctChgNo() );
        contractChange.setChgApprDate( contractchangeOutput.getChgApprDate() );
        contractChange.setCntrctChgDate( contractchangeOutput.getCntrctChgDate() );
        contractChange.setCntrctAmt( contractchangeOutput.getCntrctAmt() );
        contractChange.setChgCbgnDate( contractchangeOutput.getChgCbgnDate() );
        contractChange.setCntrctChgType( contractchangeOutput.getCntrctChgType() );
        contractChange.setCntrctChgTypeNm( contractchangeOutput.getCntrctChgTypeNm() );
        contractChange.setChgConPrd( contractchangeOutput.getChgConPrd() );
        contractChange.setRmrk( contractchangeOutput.getRmrk() );
        contractChange.setCntrctAmtBefore( contractchangeOutput.getCntrctAmtBefore() );
        contractChange.setLastChgYn( contractchangeOutput.getLastChgYn() );
        contractChange.setCntrctDivCd( contractchangeOutput.getCntrctDivCd() );
        contractChange.setChgThisCbgnDate( contractchangeOutput.getChgThisCbgnDate() );
        contractChange.setChgThisConPrd( contractchangeOutput.getChgThisConPrd() );
        contractChange.setThisCntrctCost( contractchangeOutput.getThisCntrctCost() );
        contractChange.setThisCntrctAmt( contractchangeOutput.getThisCntrctAmt() );
        contractChange.setThisCntrctAmtBefore( contractchangeOutput.getThisCntrctAmtBefore() );

        return contractChange;
    }

    @Override
    public ContractChangeAdd toChangeAdd(ContractstatusMybatisParam.ContractchangeAddOutput contractchangeAddOutput) {
        if ( contractchangeAddOutput == null ) {
            return null;
        }

        ContractChangeAdd contractChangeAdd = new ContractChangeAdd();

        contractChangeAdd.setCntrctNo( contractchangeAddOutput.getCntrctNo() );
        contractChangeAdd.setCntrctNm( contractchangeAddOutput.getCntrctNm() );
        contractChangeAdd.setMajorCnsttyNm( contractchangeAddOutput.getMajorCnsttyNm() );
        contractChangeAdd.setMngCntrctNo( contractchangeAddOutput.getMngCntrctNo() );
        contractChangeAdd.setCorpNm( contractchangeAddOutput.getCorpNm() );
        contractChangeAdd.setCbgnDate( contractchangeAddOutput.getCbgnDate() );
        contractChangeAdd.setCntrctCost( contractchangeAddOutput.getCntrctCost() );
        contractChangeAdd.setCntrctChgNo( contractchangeAddOutput.getCntrctChgNo() );
        contractChangeAdd.setCntrctPhase( contractchangeAddOutput.getCntrctPhase() );
        contractChangeAdd.setCntrctAmtBefore( contractchangeAddOutput.getCntrctAmtBefore() );
        contractChangeAdd.setThisCntrctCost( contractchangeAddOutput.getThisCntrctCost() );
        contractChangeAdd.setThisCbgnDate( contractchangeAddOutput.getThisCbgnDate() );
        contractChangeAdd.setCntrctDivCd( contractchangeAddOutput.getCntrctDivCd() );
        contractChangeAdd.setThisCntrctAmtBefore( contractchangeAddOutput.getThisCntrctAmtBefore() );

        return contractChangeAdd;
    }

    @Override
    public ContractChange toContractChange(CnContractChange cnChange) {
        if ( cnChange == null ) {
            return null;
        }

        ContractChange contractChange = new ContractChange();

        contractChange.setCntrctNo( cnChange.getCntrctNo() );
        if ( cnChange.getCntrctPhase() != null ) {
            contractChange.setCntrctPhase( String.valueOf( cnChange.getCntrctPhase() ) );
        }
        contractChange.setCntrctChgNo( cnChange.getCntrctChgNo() );
        contractChange.setChgApprDate( cnChange.getChgApprDate() );
        contractChange.setCntrctChgDate( cnChange.getCntrctChgDate() );
        if ( cnChange.getCntrctAmt() != null ) {
            contractChange.setCntrctAmt( cnChange.getCntrctAmt().longValue() );
        }
        contractChange.setChgCbgnDate( cnChange.getChgCbgnDate() );
        contractChange.setCntrctChgType( cnChange.getCntrctChgType() );
        if ( cnChange.getChgConPrd() != null ) {
            contractChange.setChgConPrd( cnChange.getChgConPrd().longValue() );
        }
        contractChange.setRmrk( cnChange.getRmrk() );
        contractChange.setLastChgYn( cnChange.getLastChgYn() );
        contractChange.setChgThisCbgnDate( cnChange.getChgThisCbgnDate() );
        if ( cnChange.getChgThisConPrd() != null ) {
            contractChange.setChgThisConPrd( cnChange.getChgThisConPrd().longValue() );
        }
        if ( cnChange.getThisCntrctAmt() != null ) {
            contractChange.setThisCntrctAmt( cnChange.getThisCntrctAmt().longValue() );
        }

        return contractChange;
    }
}
