package kr.co.ideait.platform.gaiacairos.core.persistence.vo.construction;

import org.apache.ibatis.type.Alias;

import lombok.Data;

public interface QualityinspectionMybatisParam {

	@Data
	@Alias("activity")
	public class ActivityOutput {
		String wbsCd;
		String activityId;
		String activityNm;
	}

	@Data
	@Alias("checkList")
	public class CheckListOutput {
		String chklstId;
		Integer chklstSno;
		String chklstDscrpt;
		String chklstBssCd;
		String cnstrtnYn;
		String cnsttyCd1;
		String cnsttyCd2;
		String cqcYn;
		String actnDscrpt;
	}

	@Data
	@Alias("qualityOutPut")
	public class QualityOutPut {
		String qltyIspId; // 품질검측 ID

		String cntrctNo; // 계약번호
		String ispDocNo; // 품질검측 문서 번호
		String cnstrtnId; // 검측 요청자 ID (시공담당자)

		String ispReqDt; // 검측요청일자
		String ispLct; // 위치
		String cnsttyCd; // 상위 공종코드
		String cnsttyCdL1; // 공종코드_1
		String cnsttyCdL2; // 공종코드_2
		String ispPart; // 검측부위
		String ispIssue; // 검측사항
		int atchFileNo; // 첨부파일 번호
		String cqcId; // 검측자 ID
		String rsltDocNo; // 검측결과 문서번호
		String rsltDt; // 검측일자
		String rsltCd; // 검측결과 코드
		String ordeOpnin; // 지시사항
		String apReqId; // 전자결재 요청자 ID
		String apReqDt; // 전자결재 요청 일자
		String apDocId; // 전자결재 문서 ID
		String apprvlId; // 전자결재 승인자 ID
		String apprvlDt; // 전자결재 승인일
		String apprvlStats; // 전자결재 승인상태
		String apOpnin; // 전자결재 의견

		String dltYn; // 삭제여부

		String ispApDocId; // 검측요청 전자결재 문서ID

		String usrNm;
		String paymentResult;
		String cnsttyCdKrn;
	}
}
