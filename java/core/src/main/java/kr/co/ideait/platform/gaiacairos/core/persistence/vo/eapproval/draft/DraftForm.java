package kr.co.ideait.platform.gaiacairos.core.persistence.vo.eapproval.draft;

import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.ApLine;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.CommonForm;
import lombok.Data;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;

import java.util.List;

@Mapper(componentModel = ComponentModel.SPRING)
public interface DraftForm {

	ApLine toApLine(DraftApLine draftApLine);
	
	DraftMybatisParam.SearchAppLine toDraftLineSearch (DrafLineSearch drafLineSearch);
	
	List<ApLine> toApLineList(List<DraftApLine> apLineList);
	

	@Data
	class DashoardForm extends CommonForm{
		@Description(name = "현재 페이지", description = "", type = Description.TYPE.FIELD)
		int page;

		@Description(name = "페이지 당 보여줄 개수", description = "", type = Description.TYPE.FIELD)
		int size;

		@Description(name = "결재 문서별 조회 조건", description = "", type = Description.TYPE.FIELD)
		String data;

		@Description(name = "프로젝트 번호", description = "", type = Description.TYPE.FIELD)
		String pjtNo;

		@Description(name = "계약 번호", description = "", type = Description.TYPE.FIELD)
		String cntrctNo;
	}

	
	/**
     * 전자결재 기안문 작성 메인 검색 폼
     */
	@Data
    class DraftFormList {
		@Description(name = "상위 문서 ID", description = "", type = Description.TYPE.FIELD)
        int upFrmNo;

		@Description(name = "프로젝트 번호", description = "", type = Description.TYPE.FIELD)
        String pjtNo;

		@Description(name = "계약 번호", description = "", type = Description.TYPE.FIELD)
        String cntrctNo;

		@Description(name = "프로젝트 타입", description = "", type = Description.TYPE.FIELD)
        String pjtType;

		@Description(name = "검색어", description = "", type = Description.TYPE.FIELD)
        String searchText;

		@Description(name = "기안문 체크박스", description = "", type = Description.TYPE.FIELD)
        String searchCheckBox;

		@Description(name = "사용자ID", description = "", type = Description.TYPE.FIELD)
        String usrId;
    }

	
	/**
     * 전자결재 서식 검색 폼
     */
	@Data
    class DrafLineSearch {
		@Description(name = "프로젝트 번호", description = "", type = Description.TYPE.FIELD)
		String pjtNo;

		@Description(name = "계약 번호", description = "", type = Description.TYPE.FIELD)
        String cntrctNo;

		@Description(name = "프로젝트 타입", description = "", type = Description.TYPE.FIELD)
        String pjtType;

		@Description(name = "검색어", description = "", type = Description.TYPE.FIELD)
        String searchText;

		@Description(name = "공유 구분", description = "", type = Description.TYPE.FIELD)
        String apCnrsRng;

		@Description(name = "부서 타입", description = "", type = Description.TYPE.FIELD)
        String deptType;
    }

	
	/**
     * 기안문 결재라인 상신 & 임시저장 등록 폼
     */
	@Data
    class DraftApLine {
		@Description(name = "결재라인 번호", description = "", type = Description.TYPE.FIELD)
		int apNo;

		@Description(name = "결재라인 ID", description = "", type = Description.TYPE.FIELD)
	    String apId;

		@Description(name = "결재문서 번호", description = "", type = Description.TYPE.FIELD)
	    int apDocNo;

		@Description(name = "결재문서 ID", description = "", type = Description.TYPE.FIELD)
	    String apDocId;

		@Description(name = "결재 순번", description = "", type = Description.TYPE.FIELD)
	    int apOrder;

		@Description(name = "결재 구분", description = "", type = Description.TYPE.FIELD)
	    String apDiv;

		@Description(name = "결재 상태", description = "", type = Description.TYPE.FIELD)
	    String apStats;

		@Description(name = "결재자ID", description = "", type = Description.TYPE.FIELD)
	    String apUsrId;

		@Description(name = "결재자 로그인ID", description = "", type = Description.TYPE.FIELD)
	    String apLoginId;

		@Description(name = "결재자 의견", description = "", type = Description.TYPE.FIELD)
	    String apUsrOpnin;

		@Description(name = "등록자ID", description = "", type = Description.TYPE.FIELD)
	    String rgstrId;

		@Description(name = "등록일", description = "", type = Description.TYPE.FIELD)
	    String rgstrDt;

		@Description(name = "수정자ID", description = "", type = Description.TYPE.FIELD)
	    String chgId;

		@Description(name = "수정일", description = "", type = Description.TYPE.FIELD)
	    String chgDt;
    }

}
