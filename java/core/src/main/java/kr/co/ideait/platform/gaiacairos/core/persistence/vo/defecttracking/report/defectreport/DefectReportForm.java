package kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.report.defectreport;

import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;

import java.util.List;

@Mapper(componentModel = ComponentModel.SPRING)
public interface DefectReportForm {

	DefectReportMybatisParam.DefectReportListInput toDefectReportListInput(DefectReportList defectReportList);
	
	@Data
	class DefectReportList {
		@Description(name = "계약번호", description = "", type = Description.TYPE.FIELD)
		String cntrctNo;

		@Description(name = "결함코드", description = "", type = Description.TYPE.FIELD)
		String dfccyCd;

		@Description(name = "결함단계 리스트", description = "", type = Description.TYPE.FIELD)
		List<String> dfccyPhaseNoList;

		@Description(name = "결함 작성자 리스트", description = "", type = Description.TYPE.FIELD)
		List<String> rgstrIdList;

		@Description(name = "검색어", description = "", type = Description.TYPE.FIELD)
		String keyword;

		@Description(name = "결함 작성자", description = "", type = Description.TYPE.FIELD)
		String rgstrNm;

		@Description(name = "내 의견 여부", description = "", type = Description.TYPE.FIELD)
		String myRplyYn;

		@Description(name = "결함ID 검색 시작 값", description = "", type = Description.TYPE.FIELD)
        String startDfccyNo;

		@Description(name = "결함ID 검색 종료 값", description = "", type = Description.TYPE.FIELD)
        String endDfccyNo;

		@Description(name = "액티비티 명", description = "", type = Description.TYPE.FIELD)
        String activityNm;

		@Description(name = "답변 결과 상태", description = "", type = Description.TYPE.FIELD)
        String rplyStatus;

		@Description(name = "답변 결과 코드", description = "", type = Description.TYPE.FIELD)
        String rplyCd;

		@Description(name = "QA 결과 상태", description = "", type = Description.TYPE.FIELD)
        String qaStatus;

		@Description(name = "QA 결과 코드", description = "", type = Description.TYPE.FIELD)
        String qaCd;

		@Description(name = "관리관 결과 상태", description = "", type = Description.TYPE.FIELD)
        String spvsStatus;

		@Description(name = "관리관 결과 코드", description = "", type = Description.TYPE.FIELD)
        String spvsCd;

		@Description(name = "종결 결과 코드", description = "", type = Description.TYPE.FIELD)
        String edCd;

		@Description(name = "입력기간 시작일", description = "", type = Description.TYPE.FIELD)
        String startRgstDt;

		@Description(name = "입력기간 종료일", description = "", type = Description.TYPE.FIELD)
        String endRgstDt;

		@Description(name = "생명/보건/안전 관련 여부", description = "", type = Description.TYPE.FIELD)
        String crtcIsueYn;

		@Description(name = "첨부파일 여부", description = "", type = Description.TYPE.FIELD)
        String atachYn;

		@Description(name = "언어", description = "", type = Description.TYPE.FIELD)
        String lang;

		@Description(name = "중요 결함 여부", description = "", type = Description.TYPE.FIELD)
		String priorityCheck;
	}
	
	@Data
	class DefectReportDetail {
		@Description(name = "계약번호", description = "", type = Description.TYPE.FIELD)
		String cntrctNo;

		@Description(name = "결함번호", description = "", type = Description.TYPE.FIELD)
		String dfccyNo;
	}
}
