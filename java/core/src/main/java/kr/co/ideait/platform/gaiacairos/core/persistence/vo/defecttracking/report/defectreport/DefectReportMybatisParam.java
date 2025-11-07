package kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.report.defectreport;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DtAttachments;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.util.List;

public interface DefectReportMybatisParam {

	@Data
	@Alias("defectReportListInput")
	class DefectReportListInput {
		String cntrctNo;
		String dfccyCd;
		List<String> dfccyPhaseNoList;
		List<String> rgstrIdList;
		String keyword;
		String rgstrNm;
		String myRplyYn;
		String startDfccyNo;
		String endDfccyNo;
        String activityNm;
        String rplyStatus;
        String rplyCd;
        String qaStatus;
        String qaCd;
        String spvsStatus;
        String spvsCd;
        String edCd;
        String startRgstDt;
        String endRgstDt;
        String crtcIsueYn;
        String atachYn;
        String usrId;
        String lang;
        char priorityCheck;
	}
	
	
	@Data
	@Alias("defectReportListOutput")
	class DefectReportListOutput {
        String dfccyPhaseNo;
		String dfccyPhaseNm;
		String dfccyCd;
		String dfccyCdNm;
		String dfccyNo;
		String cntrctNo;
		String title;
        String dfccyCntnts;
        String crtcIsueYn;
        String dfccyLct;
        Integer atchFileNo;
		String rgstrId;
		String rgstrNm;
		String rgstDt;
		String activityIds;
        String rplyStatus;
        String rplyCd;
        String rplyCdNm;
        String rplyRgstrId;
        String rplyRgstrNm;
        String rplyRgstDt;
        String rplyCntnts;
        String rplyOkNm;
        String rplyOkDt;
        String rplyYn;
        Integer rplyAtchFileNo;
        String qaStatus;
        String qaCd;
        String qaCdNm;
        String qaRgstrId;
        String spvsStatus;
        String spvsCd;
        String spvsCdNm;
        String spvsRgstrId;
        String edCd;
        String edCdNm;
        String edRgstrId;
        char priorityCheck;
        
        List<DtAttachments> dfccyFiles;
        List<DtAttachments> replyFiles;
        List<DtAttachments> confirmFiles;
	}
}
