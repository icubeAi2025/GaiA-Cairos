package kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.tool.verification;

import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DtAttachments;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DtDeficiencyConfirm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.tool.verification.VerificationMybatisParam.ConfirmHistoryInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.tool.verification.VerificationMybatisParam.DfccyConfirmDetailInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.tool.verification.VerificationMybatisParam.DfccyConfirmInsertInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.tool.verification.VerificationMybatisParam.DfccyConfirmListInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.CommonForm;
import lombok.Data;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;

import java.util.List;

@Mapper(componentModel = ComponentModel.SPRING)
public interface VerificationForm {

	DfccyConfirmListInput toDfccyConfirmListInput(DfccyConfirmList dfccyConfirmList);

	DfccyConfirmInsertInput toDfccyConfirmInsertInput(DfccyConfirmInsert DfccyConfirmInsert);

	DfccyConfirmDetailInput toDfccyConfirmDetailInput(DfccyConfirmDetail dfccyConfirmDetail);
	
	ConfirmHistoryInput toConfirmHistoryInput(ConfirmHistory confirmHistory);
	
	@Data
	class DfccyConfirmList extends CommonForm {
		@Description(name = "현재 페이지", description = "", type = Description.TYPE.FIELD)
		int page;

		@Description(name = "페이지 당 보여줄 개수", description = "", type = Description.TYPE.FIELD)
		int size;

		@Description(name = "결함단계 번호", description = "", type = Description.TYPE.FIELD)
		String dfccyPhaseNo;

		@Description(name = "계약 번호", description = "", type = Description.TYPE.FIELD)
        String cntrctNo;

		@Description(name = "결함단계 코드", description = "", type = Description.TYPE.FIELD)
        String dfccyPhaseCd;

		@Description(name = "결함 코드", description = "", type = Description.TYPE.FIELD)
        String dfccyCd;

		@Description(name = "확인 상태", description = "", type = Description.TYPE.FIELD)
        String confirmStatus;

		@Description(name = "검색어", description = "", type = Description.TYPE.FIELD)
        String keyword;

		@Description(name = "작성자 이름", description = "", type = Description.TYPE.FIELD)
        String rgstrNm;

		@Description(name = "내 의견 여부", description = "", type = Description.TYPE.FIELD)
        String myRplyYn;

		@Description(name = "결함ID 검색 시작 값", description = "", type = Description.TYPE.FIELD)
        String startDfccyNo;

		@Description(name = "결함ID 검색 종료 값", description = "", type = Description.TYPE.FIELD)
        String endDfccyNo;

		@Description(name = "액티비티 명", description = "", type = Description.TYPE.FIELD)
        String activityNm;

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

		@Description(name = "입력기간 시작일", description = "", type = Description.TYPE.FIELD)
        String startRgstDt;

		@Description(name = "입력기간 종료일", description = "", type = Description.TYPE.FIELD)
        String endRgstDt;

		@Description(name = "생명/보건/안전 관련 여부", description = "", type = Description.TYPE.FIELD)
        String crtcIsueYn;

		@Description(name = "첨부파일 여부", description = "", type = Description.TYPE.FIELD)
        String atachYn;

		@Description(name = "사용자ID", description = "", type = Description.TYPE.FIELD)
        String usrId;

		@Description(name = "언어", description = "", type = Description.TYPE.FIELD)
        String lang;

		@Description(name = "중요 여부", description = "", type = Description.TYPE.FIELD)
		String priorityCheck;
	}
	
	@Data
	class DfccyConfirmInsert {
		@Description(name = "확인 객체", description = "", type = Description.TYPE.FIELD)
		DtDeficiencyConfirm dtDeficiencyConfirm;

		@Description(name = "삭제할 첨부파일 리스트", description = "", type = Description.TYPE.FIELD)
		List<DtAttachments> delFileList;
	}
	
	@Data
	class DfccyConfirmDetail {
		@Description(name = "결함단계 번호", description = "", type = Description.TYPE.FIELD)
		String dfccyPhaseNo;

		@Description(name = "결함 번호", description = "", type = Description.TYPE.FIELD)
		String dfccyNo;
	}
	
	@Data
	class DfccyConfirmDeleteAll {
		@Description(name = "삭제할 확인의견 리스트", description = "", type = Description.TYPE.FIELD)
		List<DtDeficiencyConfirm> delDfccyList;
	}
	
	@Data
	class ConfirmHistory {
		@Description(name = "결함 번호", description = "", type = Description.TYPE.FIELD)
		String dfccyNo;

		@Description(name = "확인 구분", description = "", type = Description.TYPE.FIELD)
		String cnfrmDiv;

		@Description(name = "언어", description = "", type = Description.TYPE.FIELD)
		String lang;
	}
}
