package kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.contract.subcontract;

import javax.annotation.processing.Generated;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnSubcontract;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnSubcontractChange;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:16+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class SubcontractFormImpl implements SubcontractForm {

    @Override
    public CnSubcontract toCreateSubcontract(CreateSubcontract subcontract) {
        if ( subcontract == null ) {
            return null;
        }

        CnSubcontract cnSubcontract = new CnSubcontract();

        cnSubcontract.setCntrctNo( subcontract.getCntrctNo() );
        cnSubcontract.setScontrctCorpId( subcontract.getScontrctCorpId() );
        cnSubcontract.setScontrctCntrctNo( subcontract.getScontrctCntrctNo() );
        cnSubcontract.setScontrctCntrctNm( subcontract.getScontrctCntrctNm() );
        cnSubcontract.setGcontrctCorpNo( subcontract.getGcontrctCorpNo() );
        cnSubcontract.setScontrctCorpNo( subcontract.getScontrctCorpNo() );
        cnSubcontract.setScontrctCorpBsnsmnNo( subcontract.getScontrctCorpBsnsmnNo() );
        cnSubcontract.setScontrctCorpNm( subcontract.getScontrctCorpNm() );
        cnSubcontract.setScontrctCcnsttyCd( subcontract.getScontrctCcnsttyCd() );
        cnSubcontract.setScontrctIndstrytyCd( subcontract.getScontrctIndstrytyCd() );
        cnSubcontract.setCntrctDate( subcontract.getCntrctDate() );
        cnSubcontract.setCntrctBgnDate( subcontract.getCntrctBgnDate() );
        cnSubcontract.setCntrctEndDate( subcontract.getCntrctEndDate() );
        cnSubcontract.setScontrctCntrctAmt( subcontract.getScontrctCntrctAmt() );
        cnSubcontract.setRmrk( subcontract.getRmrk() );
        cnSubcontract.setScontrctCorpAdrs( subcontract.getScontrctCorpAdrs() );
        cnSubcontract.setScontrctTelNo( subcontract.getScontrctTelNo() );
        cnSubcontract.setScontrctFaxNo( subcontract.getScontrctFaxNo() );
        cnSubcontract.setScontrctCorpCeo( subcontract.getScontrctCorpCeo() );

        return cnSubcontract;
    }

    @Override
    public SubcontractMybatisParam.SubcontractListInput toSubcontractListInput(SubcontractListGet subcontractListGet) {
        if ( subcontractListGet == null ) {
            return null;
        }

        SubcontractMybatisParam.SubcontractListInput subcontractListInput = new SubcontractMybatisParam.SubcontractListInput();

        subcontractListInput.setCntrctNo( subcontractListGet.getCntrctNo() );

        return subcontractListInput;
    }

    @Override
    public SubcontractMybatisParam.SubcontractInput toSubcontractInput(String cntrctNo, Long scontrctCorpId) {
        if ( cntrctNo == null && scontrctCorpId == null ) {
            return null;
        }

        SubcontractMybatisParam.SubcontractInput subcontractInput = new SubcontractMybatisParam.SubcontractInput();

        subcontractInput.setCntrctNo( cntrctNo );
        subcontractInput.setScontrctCorpId( scontrctCorpId );

        return subcontractInput;
    }

    @Override
    public void toUpdateSubcontract(UpdateSubcontract subcontract, CnSubcontract cnSubcontract) {
        if ( subcontract == null ) {
            return;
        }

        if ( subcontract.getCntrctNo() != null ) {
            cnSubcontract.setCntrctNo( subcontract.getCntrctNo() );
        }
        if ( subcontract.getScontrctCorpId() != null ) {
            cnSubcontract.setScontrctCorpId( subcontract.getScontrctCorpId() );
        }
        if ( subcontract.getScontrctCntrctNo() != null ) {
            cnSubcontract.setScontrctCntrctNo( subcontract.getScontrctCntrctNo() );
        }
        if ( subcontract.getScontrctCntrctNm() != null ) {
            cnSubcontract.setScontrctCntrctNm( subcontract.getScontrctCntrctNm() );
        }
        if ( subcontract.getGcontrctCorpNo() != null ) {
            cnSubcontract.setGcontrctCorpNo( subcontract.getGcontrctCorpNo() );
        }
        if ( subcontract.getScontrctCorpNo() != null ) {
            cnSubcontract.setScontrctCorpNo( subcontract.getScontrctCorpNo() );
        }
        if ( subcontract.getScontrctCorpBsnsmnNo() != null ) {
            cnSubcontract.setScontrctCorpBsnsmnNo( subcontract.getScontrctCorpBsnsmnNo() );
        }
        if ( subcontract.getScontrctCorpNm() != null ) {
            cnSubcontract.setScontrctCorpNm( subcontract.getScontrctCorpNm() );
        }
        if ( subcontract.getScontrctCcnsttyCd() != null ) {
            cnSubcontract.setScontrctCcnsttyCd( subcontract.getScontrctCcnsttyCd() );
        }
        if ( subcontract.getScontrctIndstrytyCd() != null ) {
            cnSubcontract.setScontrctIndstrytyCd( subcontract.getScontrctIndstrytyCd() );
        }
        if ( subcontract.getCntrctDate() != null ) {
            cnSubcontract.setCntrctDate( subcontract.getCntrctDate() );
        }
        if ( subcontract.getCntrctBgnDate() != null ) {
            cnSubcontract.setCntrctBgnDate( subcontract.getCntrctBgnDate() );
        }
        if ( subcontract.getCntrctEndDate() != null ) {
            cnSubcontract.setCntrctEndDate( subcontract.getCntrctEndDate() );
        }
        if ( subcontract.getScontrctCntrctAmt() != null ) {
            cnSubcontract.setScontrctCntrctAmt( subcontract.getScontrctCntrctAmt() );
        }
        if ( subcontract.getRmrk() != null ) {
            cnSubcontract.setRmrk( subcontract.getRmrk() );
        }
        if ( subcontract.getDltYn() != null ) {
            cnSubcontract.setDltYn( subcontract.getDltYn() );
        }
        if ( subcontract.getScontrctCorpAdrs() != null ) {
            cnSubcontract.setScontrctCorpAdrs( subcontract.getScontrctCorpAdrs() );
        }
        if ( subcontract.getScontrctTelNo() != null ) {
            cnSubcontract.setScontrctTelNo( subcontract.getScontrctTelNo() );
        }
        if ( subcontract.getScontrctFaxNo() != null ) {
            cnSubcontract.setScontrctFaxNo( subcontract.getScontrctFaxNo() );
        }
        if ( subcontract.getScontrctCorpCeo() != null ) {
            cnSubcontract.setScontrctCorpCeo( subcontract.getScontrctCorpCeo() );
        }
    }

    @Override
    public SubcontractMybatisParam.SubcontractChangeListInput toSubcontractChangeListInput(SubcontractChangeListGet subcontractChangeListGet) {
        if ( subcontractChangeListGet == null ) {
            return null;
        }

        SubcontractMybatisParam.SubcontractChangeListInput subcontractChangeListInput = new SubcontractMybatisParam.SubcontractChangeListInput();

        subcontractChangeListInput.setCntrctNo( subcontractChangeListGet.getCntrctNo() );
        subcontractChangeListInput.setScontrctCorpId( subcontractChangeListGet.getScontrctCorpId() );

        return subcontractChangeListInput;
    }

    @Override
    public SubcontractMybatisParam.SubcontractChangeInput toSubcontractChangeInput(String cntrctNo, Long scontrctCorpId, Long cntrctChgId) {
        if ( cntrctNo == null && scontrctCorpId == null && cntrctChgId == null ) {
            return null;
        }

        SubcontractMybatisParam.SubcontractChangeInput subcontractChangeInput = new SubcontractMybatisParam.SubcontractChangeInput();

        subcontractChangeInput.setCntrctNo( cntrctNo );
        subcontractChangeInput.setScontrctCorpId( scontrctCorpId );
        subcontractChangeInput.setCntrctChgId( cntrctChgId );

        return subcontractChangeInput;
    }

    @Override
    public CnSubcontractChange toCreateSubcontractChange(CreateSubcontractChange subcontractChange) {
        if ( subcontractChange == null ) {
            return null;
        }

        CnSubcontractChange cnSubcontractChange = new CnSubcontractChange();

        cnSubcontractChange.setCntrctNo( subcontractChange.getCntrctNo() );
        cnSubcontractChange.setScontrctCorpId( subcontractChange.getScontrctCorpId() );
        cnSubcontractChange.setCntrctChgId( subcontractChange.getCntrctChgId() );
        cnSubcontractChange.setCntrctChgNo( subcontractChange.getCntrctChgNo() );
        cnSubcontractChange.setCntrctChgType( subcontractChange.getCntrctChgType() );
        cnSubcontractChange.setChgApprDate( subcontractChange.getChgApprDate() );
        cnSubcontractChange.setCntrctChgDate( subcontractChange.getCntrctChgDate() );
        cnSubcontractChange.setChgCbgnDate( subcontractChange.getChgCbgnDate() );
        cnSubcontractChange.setChgConPrd( subcontractChange.getChgConPrd() );
        cnSubcontractChange.setCntrctAmt( subcontractChange.getCntrctAmt() );
        cnSubcontractChange.setDfrcmpnstRate( subcontractChange.getDfrcmpnstRate() );
        cnSubcontractChange.setVatRate( subcontractChange.getVatRate() );
        cnSubcontractChange.setRmrk( subcontractChange.getRmrk() );
        cnSubcontractChange.setDltYn( subcontractChange.getDltYn() );

        return cnSubcontractChange;
    }

    @Override
    public void toUpdateSubcontractChange(UpdateSubcontractChange subcontractChange, CnSubcontractChange cnSubcontractChange) {
        if ( subcontractChange == null ) {
            return;
        }

        if ( subcontractChange.getCntrctNo() != null ) {
            cnSubcontractChange.setCntrctNo( subcontractChange.getCntrctNo() );
        }
        if ( subcontractChange.getScontrctCorpId() != null ) {
            cnSubcontractChange.setScontrctCorpId( subcontractChange.getScontrctCorpId() );
        }
        if ( subcontractChange.getCntrctChgId() != null ) {
            cnSubcontractChange.setCntrctChgId( subcontractChange.getCntrctChgId() );
        }
        if ( subcontractChange.getCntrctChgNo() != null ) {
            cnSubcontractChange.setCntrctChgNo( subcontractChange.getCntrctChgNo() );
        }
        if ( subcontractChange.getCntrctChgType() != null ) {
            cnSubcontractChange.setCntrctChgType( subcontractChange.getCntrctChgType() );
        }
        if ( subcontractChange.getChgApprDate() != null ) {
            cnSubcontractChange.setChgApprDate( subcontractChange.getChgApprDate() );
        }
        if ( subcontractChange.getCntrctChgDate() != null ) {
            cnSubcontractChange.setCntrctChgDate( subcontractChange.getCntrctChgDate() );
        }
        if ( subcontractChange.getChgCbgnDate() != null ) {
            cnSubcontractChange.setChgCbgnDate( subcontractChange.getChgCbgnDate() );
        }
        if ( subcontractChange.getChgConPrd() != null ) {
            cnSubcontractChange.setChgConPrd( subcontractChange.getChgConPrd() );
        }
        if ( subcontractChange.getCntrctAmt() != null ) {
            cnSubcontractChange.setCntrctAmt( subcontractChange.getCntrctAmt() );
        }
        if ( subcontractChange.getDfrcmpnstRate() != null ) {
            cnSubcontractChange.setDfrcmpnstRate( subcontractChange.getDfrcmpnstRate() );
        }
        if ( subcontractChange.getVatRate() != null ) {
            cnSubcontractChange.setVatRate( subcontractChange.getVatRate() );
        }
        if ( subcontractChange.getRmrk() != null ) {
            cnSubcontractChange.setRmrk( subcontractChange.getRmrk() );
        }
        if ( subcontractChange.getDltYn() != null ) {
            cnSubcontractChange.setDltYn( subcontractChange.getDltYn() );
        }
    }
}
