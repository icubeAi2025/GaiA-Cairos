package kr.co.ideait.platform.gaiacairos.core.persistence.vo.progress.wbs.revision;

import org.apache.ibatis.type.Alias;

import lombok.Data;

public interface RevisionMybatisParam {

	@Data
	@Alias("revisionListInput")
	public class RevisionListInput {
		String pjtNo;
		String cntrctNo;
		String searchText;
	}
	
	@Data
	@Alias("revisionListOutput")
	public class RevisionListOutput {
		String cntrctChgId;
		String cntrctChgNo;
		String revisionId;
		String epsId;
		String epsNm;
		String p6ProjectId;
		String p6ProjectNm;
		String lastRevisionYn;
		String rmrk;
		String rgstDt;
		String chgDt;

		// 20250714 추가
		String cntrctPhase;		// 계약 차수
	}
	
	
	@Data
	@Alias("deleteRevisionInput")
	public class DeleteRevisionInput {
		String cntrctChgId;
		String revisionId;
		String dltId;
	}

	@Data
	@Alias("prevRevision")
	public class PrevRevision {
		String cntrctChgId;
		String cntrctChgNo;
		String revisionId;
		String cntrctPhase;		// 계약 차수
		Integer revRank;		// 리비젼 순위
	}

}
