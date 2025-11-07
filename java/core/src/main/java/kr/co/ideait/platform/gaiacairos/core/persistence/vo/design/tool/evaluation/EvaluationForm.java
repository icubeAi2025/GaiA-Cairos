package kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.tool.evaluation;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DmAttachments;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DmEvaluation;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.tool.evaluation.EvaluationMybatisParam.*;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.CommonForm;
import lombok.Data;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;

import java.util.List;

@Mapper(componentModel = ComponentModel.SPRING)
public interface EvaluationForm {

	EvaluationListInput toEvaluationListInput(EvaluationList evaluationList);
	
	EvaluationInsertInput toEvaluationInsertInput(EvaluationInsert evaluationInsert);
	
	EvaluationDetailInput toEvaluationDetailInput(EvaluationDetail evaluationDetaill);
	
	@Data
	class EvaluationList extends CommonForm {
		int page;
		int size;
		
		String dsgnPhaseNo;
        String cntrctNo;
        String dsgnPhaseCd;
        String dsgnCd;
        String apprerStatus;
        String keyword;
        String rgstrNm;
        String myRplyYn;
        String startDsgnNo;
        String endDsgnNo;
        String rplyCd;
        String apprerCd;
        String startRgstDt;
        String endRgstDt;
        String isuYn;
        String lesnYn;
        String atachYn;
        String usrId;
        String lang;
        
	}
	
	@Data
	class EvaluationInsert {
		DmEvaluation dmEvaluation;
		List<DmAttachments> delFileList;
	}
	
	@Data
	class EvaluationDetail {
		String dsgnPhaseNo;
		String dsgnNo;
	}
	
	@Data
	class ApprerUpdate {
		String cntrctNo;
		String dsgnNo;
		String apprerCd;
	}
	
	@Data
	class EvaluationDeleteAll {
		List<DmEvaluation> delEvaList;
	}
}
