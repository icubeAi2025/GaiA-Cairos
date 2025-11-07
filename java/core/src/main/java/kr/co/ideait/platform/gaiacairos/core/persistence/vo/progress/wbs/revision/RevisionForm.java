package kr.co.ideait.platform.gaiacairos.core.persistence.vo.progress.wbs.revision;


import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.PrRevision;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.CommonForm;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.MappingTarget;

import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = ComponentModel.SPRING)
public interface RevisionForm {

	RevisionMybatisParam.RevisionListInput toRevisionListInput(RevisionList revisionList);
	
	List<RevisionMybatisParam.DeleteRevisionInput> toDeleteRevisionInput(List<RevisionDelete> revisionDeleteList);

	void updatePrRevision(RevisionUpdate revisionUpdate, @MappingTarget PrRevision PrRevision);
	
	
	@Data
    class RevisionList extends CommonForm {
		@Description(name = "프로젝트 번호", description = "", type = Description.TYPE.FIELD)
        String pjtNo;
		@Description(name = "계약 번호", description = "", type = Description.TYPE.FIELD)
        String cntrctNo;
		@Description(name = "검색어", description = "", type = Description.TYPE.FIELD)
        String searchText;
    }
	
	
	@Data
	class RevisionUpdate extends CommonForm {
		@Description(name = "계약변경 ID", description = "", type = Description.TYPE.FIELD)
		String cntrctChgId;
		@Description(name = "리비젼 ID", description = "", type = Description.TYPE.FIELD)
		String revisionId;
		@Description(name = "EPS ID", description = "", type = Description.TYPE.FIELD)
		String epsId;
		@Description(name = "EPS 명", description = "", type = Description.TYPE.FIELD)
		String epsNm;
		@Description(name = "P6 프로젝트 ID", description = "", type = Description.TYPE.FIELD)
        String p6ProjectId;
		@Description(name = "P6 프로젝트 명", description = "", type = Description.TYPE.FIELD)
        String p6ProjectNm;
		@Description(name = "최종 리비젼 여부", description = "", type = Description.TYPE.FIELD)
		String lastRevisionYn;
		@Description(name = "비고", description = "", type = Description.TYPE.FIELD)
		String rmrk;
		@Description(name = "수정일시", description = "", type = Description.TYPE.FIELD)
		LocalDateTime chgDt;
		@Description(name = "수정자 ID", description = "", type = Description.TYPE.FIELD)
		String chgId;

		// 20241219 추가
		@Description(name = "P6 프로젝트 객체 ID", description = "", type = Description.TYPE.FIELD)
		String p6ProjectObjId;
	}
	
	
	@Data
	class RevisionDeleteList extends CommonForm {
		@Description(name = "삭제 리비젼 목록", description = "", type = Description.TYPE.FIELD)
		List<RevisionDelete> delRevisionList;
	}
	
	
	@Data
	class RevisionDelete extends CommonForm {
		@Description(name = "계약변경 ID", description = "", type = Description.TYPE.FIELD)
		String cntrctChgId;
		@Description(name = "리비젼 ID", description = "", type = Description.TYPE.FIELD)
		String revisionId;
		@Description(name = "삭제자 ID", description = "", type = Description.TYPE.FIELD)
		String dltId;
	}

	@Getter
	@Setter
	class WorkTypeRequest {
		@Pattern(regexp = "^(GAPR\\d{4}|WBPR\\d{4})$", message = "The value must start with 'GAPR' followed by 4 digits.")
		@Description(name = "작업 유형", description = "", type = Description.TYPE.FIELD)
		private String workType;
		@Description(name = "EPS 객체 ID", description = "", type = Description.TYPE.FIELD)
		private String epsObjId;
		@Description(name = "P6 프로젝트 ID", description = "", type = Description.TYPE.FIELD)
		private String projId;
		@Description(name = "프로젝트 번호", description = "", type = Description.TYPE.FIELD)
		private String pjtNo;
	}
	
}
