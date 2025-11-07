package kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.tool.verification;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DtAttachments;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DtDeficiencyConfirm;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisPageable;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.util.List;

public interface VerificationMybatisParam {

	@Data
	@Alias("dfccyConfirmListInput")
	class DfccyConfirmListInput extends MybatisPageable {
		String dfccyPhaseNo;
        String cntrctNo;
        String dfccyPhaseCd;
        String dfccyCd;
        String confirmStatus;
        String keyword;
        String rgstrNm;
        String myRplyYn;
        String startDfccyNo;
        String endDfccyNo;
        String activityNm;
        String rplyCd;
        String qaStatus;
        String qaCd;
        String spvsStatus;
        String spvsCd;
        String startRgstDt;
        String endRgstDt;
        String crtcIsueYn;
        String atachYn;
        String usrId;
        String lang;
		String priorityCheck;
	}
	
	@Data
	class DfccyConfirmInsertInput {
		DtDeficiencyConfirm dtDeficiencyConfirm;
		List<DtAttachments> delFileList;
	}
	
	@Data
	@Alias("dfccyConfirmDetailInput")
	class DfccyConfirmDetailInput {
		String dfccyPhaseNo;
		String dfccyNo;
	}
	
	@Data
	@Alias("confirmHistoryInput")
	class ConfirmHistoryInput {
		String dfccyNo;		
		String cnfrmDiv;
		String lang;
	}

}
