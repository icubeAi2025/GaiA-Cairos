package kr.co.ideait.platform.gaiacairos.core.persistence.vo.eapproval.approval;

import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.ApLine;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.ApShare;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.CommonForm;
import lombok.Data;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;

import java.util.List;

@Mapper(componentModel = ComponentModel.SPRING)
public interface ApprovalForm {

	ApprovalMybatisParam.ApprovalListInput toApprovalListInput(ApprovalList approvalList);

	ApprovalMybatisParam.ApproveListInput toApproveListInput(ApproveList approveList);

	ApprovalMybatisParam.ApproveOneInput toApproveOneInput(ApproveOne approveOne);


	//상세검색 form
	@Data
	class ApprovalList extends CommonForm {
		@Description(name = "결재 문서별 조회 조건", description = "", type = Description.TYPE.FIELD)
		String data;

		@Description(name = "결재문서 상태", description = "", type = Description.TYPE.FIELD)
		String status;

		@Description(name = "검색어", description = "", type = Description.TYPE.FIELD)
		String keyword;

		@Description(name = "기안자ID", description = "", type = Description.TYPE.FIELD)
		String apUsrId;

		@Description(name = "기안자 로그인ID", description = "", type = Description.TYPE.FIELD)
		String apLoginId;

		@Description(name = "문서 제목", description = "", type = Description.TYPE.FIELD)
		String apDocTitle;

		@Description(name = "문서 내용", description = "", type = Description.TYPE.FIELD)
		String apDocTxt;

		@Description(name = "기안요청기간 시작일", description = "", type = Description.TYPE.FIELD)
		String startAppDt;

		@Description(name = "기안요청기간 종료일", description = "", type = Description.TYPE.FIELD)
		String endAppDt;

		@Description(name = "결재완료기간 시작일", description = "", type = Description.TYPE.FIELD)
		String startCmpltDt;

		@Description(name = "결재완료기간 종료일", description = "", type = Description.TYPE.FIELD)
		String endCmpltDt;

		@Description(name = "프로젝트 번호", description = "", type = Description.TYPE.FIELD)
		String pjtNo;

		@Description(name = "계약 번호", description = "", type = Description.TYPE.FIELD)
		String cntrctNo;

		@Description(name = "프로젝트 타입", description = "", type = Description.TYPE.FIELD)
		String pjtType;

		@Description(name = "선택한 문서구분 리스트", description = "", type = Description.TYPE.FIELD)
		List<String> selectedApType;

		@Description(name = "선택한 문서 상태", description = "", type = Description.TYPE.FIELD)
		String selectedStatus;

		@Description(name = "선택한 서식 종류", description = "", type = Description.TYPE.FIELD)
		Integer selectedForm;
	}


	// 승인할 문서 form
	@Data
	class ApproveDoc {
		@Description(name = "결재문서 번호", description = "", type = Description.TYPE.FIELD)
		Integer apDocNo;

		@Description(name = "결재문서 ID", description = "", type = Description.TYPE.FIELD)
		String apDocId;

		@Description(name = "결재자 ID", description = "", type = Description.TYPE.FIELD)
		String apUsrId;

		@Description(name = "결재상태", description = "", type = Description.TYPE.FIELD)
		String apStats;

		@Description(name = "결재문서 상태", description = "", type = Description.TYPE.FIELD)
		String apDocStats;

		@Description(name = "결재자 의견", description = "", type = Description.TYPE.FIELD)
		String apUsrOpnin;

		@Description(name = "문서구분", description = "", type = Description.TYPE.FIELD)
		String apType;
	}


	// 일괄 승인/반려
	@Data
	class ApproveList {
		@Description(name = "결재 상태", description = "", type = Description.TYPE.FIELD)
		String apStats;
		
		@Description(name = "결재 대상 리스트", description = "", type = Description.TYPE.FIELD)
		List<ApproveDoc> approveDocList;
	}


	// 단건 승인/반려
	@Data
	class ApproveOne {
		@Description(name = "결재 상태", description = "", type = Description.TYPE.FIELD)
		String apStats;

		@Description(name = "공유자 리스트", description = "", type = Description.TYPE.FIELD)
		List<ApShare> apShareList;

		@Description(name = "공유자 삭제리스트", description = "", type = Description.TYPE.FIELD)
		List<ApShare> delShareList;

		@Description(name = "결재선 정보", description = "", type = Description.TYPE.FIELD)
		ApLine apLine;
		
		@Description(name = "계약번호", description = "", type = Description.TYPE.FIELD)
		String cntrctNo;

	}

	
}

