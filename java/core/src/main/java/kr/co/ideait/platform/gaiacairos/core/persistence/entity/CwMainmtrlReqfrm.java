package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class CwMainmtrlReqfrm extends AbstractRudIdTime {
    @Id
    @Description(name = "검수요청서 No", description = "", type = Description.TYPE.FIELD)
    String reqfrmNo;        // 검수요청서 No

    @Description(name = "계약번호", description = "", type = Description.TYPE.FIELD)
    String cntrctNo;         // 계약번호 (FK)

    @Description(name = "문서번호", description = "", type = Description.TYPE.FIELD)
    String docNo;            // 문서번호

    @Description(name = "공종", description = "", type = Description.TYPE.FIELD)
    String cnsttyCd;        // 공종 (콤마로 구분된 다중값)

    @Description(name = "수신업체", description = "", type = Description.TYPE.FIELD)
    String rxcorpNm;       // 수신업체 명

    @Description(name = "품명", description = "", type = Description.TYPE.FIELD)
    String prdnm;          // 품명

    @Description(name = "제조회사명", description = "", type = Description.TYPE.FIELD)
    String makrNm;         // 제조회사명

    @Description(name = "비고", description = "", type = Description.TYPE.FIELD)
    String rmrk;           // 비고

    @Description(name = "첨부파일 번호", description = "", type = Description.TYPE.FIELD)
    Integer atchFileNo;    // 첨부파일 번호

    @Description(name = "요청자 ID", description = "", type = Description.TYPE.FIELD)
    String reqId;       // 요청자 ID

    @Description(name = "검수요청일자", description = "", type = Description.TYPE.FIELD)
    LocalDateTime reqDt;     // 검수요청일자

    @Description(name = "검수결과 여부", description = "", type = Description.TYPE.FIELD)
    String rsltYn;     // 검수결과 여부 (Y/N)

    @Description(name = "검수자 ID", description = "", type = Description.TYPE.FIELD)
    String cmId;       // 검수자 ID

    @Description(name = "검수일자", description = "", type = Description.TYPE.FIELD)
    LocalDateTime cmDt;     // 검수일자

    @Description(name = "검수판정결과", description = "", type = Description.TYPE.FIELD)
    String rsltCd;  // 검수판정결과

    @Description(name = "자재검사의견", description = "", type = Description.TYPE.FIELD)
    String rsltOpnin; // 자재검사의견

    @Description(name = "전자결재 요청자 ID", description = "", type = Description.TYPE.FIELD)
    String apReqId;   // 전자결재 요청자 ID

    @Description(name = "전자결재 요청일자", description = "", type = Description.TYPE.FIELD)
    LocalDateTime apReqDt;   // 전자결재 요청일자

    @Description(name = "검수요청 전자결재 문서 ID", description = "", type = Description.TYPE.FIELD)
    String apDocId;   //검수요청 전자결재 문서ID

    @Description(name = "전자결재 문서 ID", description = "", type = Description.TYPE.FIELD)
    String apprvlId;    // 전자결재 승인자 ID
    
    @Description(name = "전자결재 승인일자", description = "", type = Description.TYPE.FIELD)
    LocalDateTime apprvlDt;    // 전자결재 승인일자

    @Description(name = "전자결재 승인상태", description = "", type = Description.TYPE.FIELD)
    String apprvlStats; // 전자결재 승인상태

    @Description(name = "전자결재 의견", description = "", type = Description.TYPE.FIELD)
    String apOpnin; // 전자결재 의견

    @Description(name = "검수완료일자", description = "", type = Description.TYPE.FIELD)
    String dltYn;      // 삭제여부 (Y/N)

    @Description(name = "문서 ID", description = "", type = Description.TYPE.FIELD)
    String docId;      // 문서ID (통합문서관리 문서ID)
}
