package kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.tool.evaluation;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DmAttachments;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:18+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class EvaluationFormImpl implements EvaluationForm {

    @Override
    public EvaluationMybatisParam.EvaluationListInput toEvaluationListInput(EvaluationList evaluationList) {
        if ( evaluationList == null ) {
            return null;
        }

        EvaluationMybatisParam.EvaluationListInput evaluationListInput = new EvaluationMybatisParam.EvaluationListInput();

        evaluationListInput.setPageable( evaluationList.getPageable() );
        evaluationListInput.setDsgnPhaseNo( evaluationList.getDsgnPhaseNo() );
        evaluationListInput.setCntrctNo( evaluationList.getCntrctNo() );
        evaluationListInput.setDsgnPhaseCd( evaluationList.getDsgnPhaseCd() );
        evaluationListInput.setDsgnCd( evaluationList.getDsgnCd() );
        evaluationListInput.setApprerStatus( evaluationList.getApprerStatus() );
        evaluationListInput.setKeyword( evaluationList.getKeyword() );
        evaluationListInput.setRgstrNm( evaluationList.getRgstrNm() );
        evaluationListInput.setMyRplyYn( evaluationList.getMyRplyYn() );
        evaluationListInput.setStartDsgnNo( evaluationList.getStartDsgnNo() );
        evaluationListInput.setEndDsgnNo( evaluationList.getEndDsgnNo() );
        evaluationListInput.setRplyCd( evaluationList.getRplyCd() );
        evaluationListInput.setApprerCd( evaluationList.getApprerCd() );
        evaluationListInput.setStartRgstDt( evaluationList.getStartRgstDt() );
        evaluationListInput.setEndRgstDt( evaluationList.getEndRgstDt() );
        evaluationListInput.setIsuYn( evaluationList.getIsuYn() );
        evaluationListInput.setLesnYn( evaluationList.getLesnYn() );
        evaluationListInput.setAtachYn( evaluationList.getAtachYn() );
        evaluationListInput.setUsrId( evaluationList.getUsrId() );
        evaluationListInput.setLang( evaluationList.getLang() );

        return evaluationListInput;
    }

    @Override
    public EvaluationMybatisParam.EvaluationInsertInput toEvaluationInsertInput(EvaluationInsert evaluationInsert) {
        if ( evaluationInsert == null ) {
            return null;
        }

        EvaluationMybatisParam.EvaluationInsertInput evaluationInsertInput = new EvaluationMybatisParam.EvaluationInsertInput();

        evaluationInsertInput.setDmEvaluation( evaluationInsert.getDmEvaluation() );
        List<DmAttachments> list = evaluationInsert.getDelFileList();
        if ( list != null ) {
            evaluationInsertInput.setDelFileList( new ArrayList<DmAttachments>( list ) );
        }

        return evaluationInsertInput;
    }

    @Override
    public EvaluationMybatisParam.EvaluationDetailInput toEvaluationDetailInput(EvaluationDetail evaluationDetaill) {
        if ( evaluationDetaill == null ) {
            return null;
        }

        EvaluationMybatisParam.EvaluationDetailInput evaluationDetailInput = new EvaluationMybatisParam.EvaluationDetailInput();

        evaluationDetailInput.setDsgnPhaseNo( evaluationDetaill.getDsgnPhaseNo() );
        evaluationDetailInput.setDsgnNo( evaluationDetaill.getDsgnNo() );

        return evaluationDetailInput;
    }
}
