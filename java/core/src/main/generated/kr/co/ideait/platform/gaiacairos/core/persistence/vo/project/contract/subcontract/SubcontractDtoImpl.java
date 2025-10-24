package kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.contract.subcontract;

import javax.annotation.processing.Generated;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnSubcontract;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnSubcontractChange;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:18+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class SubcontractDtoImpl implements SubcontractDto {

    @Override
    public Subcontract fromCnSubcontractOutput(SubcontractMybatisParam.SubcontractListOutput subcontractListOutput) {
        if ( subcontractListOutput == null ) {
            return null;
        }

        Subcontract subcontract = new Subcontract();

        subcontract.setCntrctNo( subcontractListOutput.getCntrctNo() );
        subcontract.setScontrctCorpId( subcontractListOutput.getScontrctCorpId() );
        subcontract.setScontrctCntrctNo( subcontractListOutput.getScontrctCntrctNo() );
        subcontract.setScontrctCntrctNm( subcontractListOutput.getScontrctCntrctNm() );
        subcontract.setGcontrctCorpNo( subcontractListOutput.getGcontrctCorpNo() );
        subcontract.setScontrctCorpNo( subcontractListOutput.getScontrctCorpNo() );
        subcontract.setScontrctCorpBsnsmnNo( subcontractListOutput.getScontrctCorpBsnsmnNo() );
        subcontract.setScontrctCorpNm( subcontractListOutput.getScontrctCorpNm() );
        subcontract.setScontrctCcnsttyCd( subcontractListOutput.getScontrctCcnsttyCd() );
        subcontract.setScontrctIndstrytyCd( subcontractListOutput.getScontrctIndstrytyCd() );
        subcontract.setCntrctDate( subcontractListOutput.getCntrctDate() );
        subcontract.setCntrctBgnDate( subcontractListOutput.getCntrctBgnDate() );
        subcontract.setCntrctEndDate( subcontractListOutput.getCntrctEndDate() );
        subcontract.setScontrctCntrctAmt( subcontractListOutput.getScontrctCntrctAmt() );
        subcontract.setRmrk( subcontractListOutput.getRmrk() );
        subcontract.setBsnsmnNo( subcontractListOutput.getBsnsmnNo() );
        subcontract.setCorpNm( subcontractListOutput.getCorpNm() );
        subcontract.setScontrctIndstrytyCdKrn( subcontractListOutput.getScontrctIndstrytyCdKrn() );
        subcontract.setCntrctNm( subcontractListOutput.getCntrctNm() );
        subcontract.setCnsttyCdKrn( subcontractListOutput.getCnsttyCdKrn() );
        subcontract.setCntrctChgNo( subcontractListOutput.getCntrctChgNo() );

        return subcontract;
    }

    @Override
    public Subcontract fromCnSubcontract(CnSubcontract cnSubcontract) {
        if ( cnSubcontract == null ) {
            return null;
        }

        Subcontract subcontract = new Subcontract();

        subcontract.setCntrctNo( cnSubcontract.getCntrctNo() );
        subcontract.setScontrctCorpId( cnSubcontract.getScontrctCorpId() );
        subcontract.setScontrctCntrctNo( cnSubcontract.getScontrctCntrctNo() );
        subcontract.setScontrctCntrctNm( cnSubcontract.getScontrctCntrctNm() );
        subcontract.setGcontrctCorpNo( cnSubcontract.getGcontrctCorpNo() );
        subcontract.setScontrctCorpNo( cnSubcontract.getScontrctCorpNo() );
        subcontract.setScontrctCorpBsnsmnNo( cnSubcontract.getScontrctCorpBsnsmnNo() );
        subcontract.setScontrctCorpNm( cnSubcontract.getScontrctCorpNm() );
        subcontract.setScontrctCcnsttyCd( cnSubcontract.getScontrctCcnsttyCd() );
        subcontract.setScontrctIndstrytyCd( cnSubcontract.getScontrctIndstrytyCd() );
        subcontract.setCntrctDate( cnSubcontract.getCntrctDate() );
        subcontract.setCntrctBgnDate( cnSubcontract.getCntrctBgnDate() );
        subcontract.setCntrctEndDate( cnSubcontract.getCntrctEndDate() );
        subcontract.setScontrctCntrctAmt( cnSubcontract.getScontrctCntrctAmt() );
        subcontract.setRmrk( cnSubcontract.getRmrk() );

        return subcontract;
    }

    @Override
    public SubcontractChange fromCnSubcontractChangeOutput(SubcontractMybatisParam.SubcontractChangeListOutput subcontractChangeListOutput) {
        if ( subcontractChangeListOutput == null ) {
            return null;
        }

        SubcontractChange subcontractChange = new SubcontractChange();

        subcontractChange.setCntrctNo( subcontractChangeListOutput.getCntrctNo() );
        subcontractChange.setScontrctCorpId( subcontractChangeListOutput.getScontrctCorpId() );
        subcontractChange.setCntrctChgId( subcontractChangeListOutput.getCntrctChgId() );
        subcontractChange.setCntrctChgNo( subcontractChangeListOutput.getCntrctChgNo() );
        subcontractChange.setCntrctChgType( subcontractChangeListOutput.getCntrctChgType() );
        subcontractChange.setChgApprDate( subcontractChangeListOutput.getChgApprDate() );
        subcontractChange.setCntrctChgDate( subcontractChangeListOutput.getCntrctChgDate() );
        subcontractChange.setChgCbgnDate( subcontractChangeListOutput.getChgCbgnDate() );
        subcontractChange.setChgConPrd( subcontractChangeListOutput.getChgConPrd() );
        subcontractChange.setCntrctAmt( subcontractChangeListOutput.getCntrctAmt() );
        subcontractChange.setDfrcmpnstRate( subcontractChangeListOutput.getDfrcmpnstRate() );
        subcontractChange.setVatRate( subcontractChangeListOutput.getVatRate() );
        subcontractChange.setRmrk( subcontractChangeListOutput.getRmrk() );
        subcontractChange.setDltYn( subcontractChangeListOutput.getDltYn() );
        subcontractChange.setCntrctBgnDate( subcontractChangeListOutput.getCntrctBgnDate() );
        subcontractChange.setCntrctChgTypeKrn( subcontractChangeListOutput.getCntrctChgTypeKrn() );

        return subcontractChange;
    }

    @Override
    public SubcontractChange fromCnSubcontractChange(CnSubcontractChange cnSubcontractChange) {
        if ( cnSubcontractChange == null ) {
            return null;
        }

        SubcontractChange subcontractChange = new SubcontractChange();

        subcontractChange.setCntrctNo( cnSubcontractChange.getCntrctNo() );
        subcontractChange.setScontrctCorpId( cnSubcontractChange.getScontrctCorpId() );
        subcontractChange.setCntrctChgId( cnSubcontractChange.getCntrctChgId() );
        subcontractChange.setCntrctChgNo( cnSubcontractChange.getCntrctChgNo() );
        subcontractChange.setCntrctChgType( cnSubcontractChange.getCntrctChgType() );
        subcontractChange.setChgApprDate( cnSubcontractChange.getChgApprDate() );
        subcontractChange.setCntrctChgDate( cnSubcontractChange.getCntrctChgDate() );
        subcontractChange.setChgCbgnDate( cnSubcontractChange.getChgCbgnDate() );
        subcontractChange.setChgConPrd( cnSubcontractChange.getChgConPrd() );
        subcontractChange.setCntrctAmt( cnSubcontractChange.getCntrctAmt() );
        subcontractChange.setDfrcmpnstRate( cnSubcontractChange.getDfrcmpnstRate() );
        subcontractChange.setVatRate( cnSubcontractChange.getVatRate() );
        subcontractChange.setRmrk( cnSubcontractChange.getRmrk() );
        subcontractChange.setDltYn( cnSubcontractChange.getDltYn() );

        return subcontractChange;
    }
}
