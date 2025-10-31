package kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.tool.evaluation;

import java.util.List;

import org.apache.ibatis.type.Alias;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DmAttachments;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DmEvaluation;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisPageable;
import lombok.Data;

public interface EvaluationMybatisParam {
	
	@Data
	@Alias("evaluationListInput")
	public class EvaluationListInput extends MybatisPageable {
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
	class EvaluationInsertInput {		
		DmEvaluation dmEvaluation;
		List<DmAttachments> delFileList;
	}
	
	@Data
	@Alias("evaluationDetailInput")
	class EvaluationDetailInput {
		String dsgnPhaseNo;
		String dsgnNo;
	}

}
