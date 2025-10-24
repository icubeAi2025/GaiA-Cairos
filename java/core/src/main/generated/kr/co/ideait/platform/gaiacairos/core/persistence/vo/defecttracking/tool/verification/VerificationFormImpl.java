package kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.tool.verification;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DtAttachments;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:16+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class VerificationFormImpl implements VerificationForm {

    @Override
    public VerificationMybatisParam.DfccyConfirmListInput toDfccyConfirmListInput(DfccyConfirmList dfccyConfirmList) {
        if ( dfccyConfirmList == null ) {
            return null;
        }

        VerificationMybatisParam.DfccyConfirmListInput dfccyConfirmListInput = new VerificationMybatisParam.DfccyConfirmListInput();

        dfccyConfirmListInput.setPageable( dfccyConfirmList.getPageable() );
        dfccyConfirmListInput.setDfccyPhaseNo( dfccyConfirmList.getDfccyPhaseNo() );
        dfccyConfirmListInput.setCntrctNo( dfccyConfirmList.getCntrctNo() );
        dfccyConfirmListInput.setDfccyPhaseCd( dfccyConfirmList.getDfccyPhaseCd() );
        dfccyConfirmListInput.setDfccyCd( dfccyConfirmList.getDfccyCd() );
        dfccyConfirmListInput.setConfirmStatus( dfccyConfirmList.getConfirmStatus() );
        dfccyConfirmListInput.setKeyword( dfccyConfirmList.getKeyword() );
        dfccyConfirmListInput.setRgstrNm( dfccyConfirmList.getRgstrNm() );
        dfccyConfirmListInput.setMyRplyYn( dfccyConfirmList.getMyRplyYn() );
        dfccyConfirmListInput.setStartDfccyNo( dfccyConfirmList.getStartDfccyNo() );
        dfccyConfirmListInput.setEndDfccyNo( dfccyConfirmList.getEndDfccyNo() );
        dfccyConfirmListInput.setActivityNm( dfccyConfirmList.getActivityNm() );
        dfccyConfirmListInput.setRplyCd( dfccyConfirmList.getRplyCd() );
        dfccyConfirmListInput.setQaStatus( dfccyConfirmList.getQaStatus() );
        dfccyConfirmListInput.setQaCd( dfccyConfirmList.getQaCd() );
        dfccyConfirmListInput.setSpvsStatus( dfccyConfirmList.getSpvsStatus() );
        dfccyConfirmListInput.setSpvsCd( dfccyConfirmList.getSpvsCd() );
        dfccyConfirmListInput.setStartRgstDt( dfccyConfirmList.getStartRgstDt() );
        dfccyConfirmListInput.setEndRgstDt( dfccyConfirmList.getEndRgstDt() );
        dfccyConfirmListInput.setCrtcIsueYn( dfccyConfirmList.getCrtcIsueYn() );
        dfccyConfirmListInput.setAtachYn( dfccyConfirmList.getAtachYn() );
        dfccyConfirmListInput.setUsrId( dfccyConfirmList.getUsrId() );
        dfccyConfirmListInput.setLang( dfccyConfirmList.getLang() );
        dfccyConfirmListInput.setPriorityCheck( dfccyConfirmList.getPriorityCheck() );

        return dfccyConfirmListInput;
    }

    @Override
    public VerificationMybatisParam.DfccyConfirmInsertInput toDfccyConfirmInsertInput(DfccyConfirmInsert DfccyConfirmInsert) {
        if ( DfccyConfirmInsert == null ) {
            return null;
        }

        VerificationMybatisParam.DfccyConfirmInsertInput dfccyConfirmInsertInput = new VerificationMybatisParam.DfccyConfirmInsertInput();

        dfccyConfirmInsertInput.setDtDeficiencyConfirm( DfccyConfirmInsert.getDtDeficiencyConfirm() );
        List<DtAttachments> list = DfccyConfirmInsert.getDelFileList();
        if ( list != null ) {
            dfccyConfirmInsertInput.setDelFileList( new ArrayList<DtAttachments>( list ) );
        }

        return dfccyConfirmInsertInput;
    }

    @Override
    public VerificationMybatisParam.DfccyConfirmDetailInput toDfccyConfirmDetailInput(DfccyConfirmDetail dfccyConfirmDetail) {
        if ( dfccyConfirmDetail == null ) {
            return null;
        }

        VerificationMybatisParam.DfccyConfirmDetailInput dfccyConfirmDetailInput = new VerificationMybatisParam.DfccyConfirmDetailInput();

        dfccyConfirmDetailInput.setDfccyPhaseNo( dfccyConfirmDetail.getDfccyPhaseNo() );
        dfccyConfirmDetailInput.setDfccyNo( dfccyConfirmDetail.getDfccyNo() );

        return dfccyConfirmDetailInput;
    }

    @Override
    public VerificationMybatisParam.ConfirmHistoryInput toConfirmHistoryInput(ConfirmHistory confirmHistory) {
        if ( confirmHistory == null ) {
            return null;
        }

        VerificationMybatisParam.ConfirmHistoryInput confirmHistoryInput = new VerificationMybatisParam.ConfirmHistoryInput();

        confirmHistoryInput.setDfccyNo( confirmHistory.getDfccyNo() );
        confirmHistoryInput.setCnfrmDiv( confirmHistory.getCnfrmDiv() );
        confirmHistoryInput.setLang( confirmHistory.getLang() );

        return confirmHistoryInput;
    }
}
