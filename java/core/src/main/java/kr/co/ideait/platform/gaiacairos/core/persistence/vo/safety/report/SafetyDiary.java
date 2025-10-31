package kr.co.ideait.platform.gaiacairos.core.persistence.vo.safety.report;

import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 안전일지
 */
@Data
public class SafetyDiary {
    @Description(name = "안전일지아이디", description = "안전일지 Field", type = Description.TYPE.FIELD)
    String safeDiaryId;
    @Description(name = "계약번호", description = "안전일지 Field", type = Description.TYPE.FIELD)
    String cntrctNo;
    @Description(name = "보고일자", description = "안전일지 Field", type = Description.TYPE.FIELD)
    String repoDt;
    @Description(name = "보고서번호", description = "안전일지 Field", type = Description.TYPE.FIELD)
    String repoNo;
    @Description(name = "제목", description = "안전일지 Field", type = Description.TYPE.FIELD)
    String title;
    @Description(name = "오전날씨", description = "안전일지 Field", type = Description.TYPE.FIELD)
    String forcAm;
    @Description(name = "오후날씨", description = "안전일지 Field", type = Description.TYPE.FIELD)
    String forcPm;
    @Description(name = "최고기온", description = "안전일지 Field", type = Description.TYPE.FIELD)
    String taMax;
    @Description(name = "최저기온", description = "안전일지 Field", type = Description.TYPE.FIELD)
    String taMin;
    @Description(name = "사무직(남)", description = "안전일지 Field", type = Description.TYPE.FIELD)
    Integer officeM;
    @Description(name = "사무직(여)", description = "안전일지 Field", type = Description.TYPE.FIELD)
    Integer officeF;
    @Description(name = "노무직(남)", description = "안전일지 Field", type = Description.TYPE.FIELD)
    Integer laborM;
    @Description(name = "노무직(여)", description = "안전일지 Field", type = Description.TYPE.FIELD)
    Integer laborF;
    @Description(name = "장비인원(남)", description = "안전일지 Field", type = Description.TYPE.FIELD)
    Integer equipM;
    @Description(name = "장비인원(여)", description = "안전일지 Field", type = Description.TYPE.FIELD)
    Integer equipF;
    @Description(name = "인원누계", description = "안전일지 Field", type = Description.TYPE.FIELD)
    Integer cusum;
    @Description(name = "교육구분", description = "안전일지 Field", type = Description.TYPE.FIELD)
    Integer eduDiv;
    @Description(name = "총괄책임자의견", description = "안전일지 Field", type = Description.TYPE.FIELD)
    String gmRevOpin;
    @Description(name = "재해자수_전일", description = "안전일지 Field", type = Description.TYPE.FIELD)
    Integer vicNumYday;
    @Description(name = "재해자수_금일", description = "안전일지 Field", type = Description.TYPE.FIELD)
    Integer vicNumTday;
    @Description(name = "재해자수_누계", description = "안전일지 Field", type = Description.TYPE.FIELD)
    Integer vicNumCu;
    @Description(name = "무재해운동_목표시간", description = "안전일지 Field", type = Description.TYPE.FIELD)
    Integer accFreTargTm;
    @Description(name = "무재해운동_전일시간", description = "안전일지 Field", type = Description.TYPE.FIELD)
    Integer accFreYdayTm;
    @Description(name = "무재해운동_금일시간", description = "안전일지 Field", type = Description.TYPE.FIELD)
    Integer accFreTdayTm;
    @Description(name = "무재해운동_연간누계", description = "안전일지 Field", type = Description.TYPE.FIELD)
    Integer accFreYearCu;
    @Description(name = "전자결재 요청자 ID", description = "안전일지 Field", type = Description.TYPE.FIELD)
    String apReqId;
    @Description(name = "전자결재 요청 일자", description = "안전일지 Field", type = Description.TYPE.FIELD)
    LocalDateTime apReqDt;
    @Description(name = "전자결재 문서 ID", description = "안전일지 Field", type = Description.TYPE.FIELD)
    String apDocId;
    @Description(name = "전자결재 승인자 ID", description = "안전일지 Field", type = Description.TYPE.FIELD)
    String apprvlId;
    @Description(name = "전자결재 승인일", description = "안전일지 Field", type = Description.TYPE.FIELD)
    LocalDateTime apprvlDt;
    @Description(name = "전자결재 승인상태", description = "안전일지 Field", type = Description.TYPE.FIELD)
    String apprvlStats;
    @Description(name = "첨부파일번호", description = "안전일지 Field", type = Description.TYPE.FIELD)
    Integer atchFileNo;
    @Description(name = "삭제여부", description = "안전일지 Field", type = Description.TYPE.FIELD)
    String dltYn;
    @Description(name = "등록자ID", description = "안전일지 Field", type = Description.TYPE.FIELD)
    String rgstrId;
    @Description(name = "등록일", description = "안전일지 Field", type = Description.TYPE.FIELD)
    LocalDateTime rgstDt;
    @Description(name = "수정자ID", description = "안전일지 Field", type = Description.TYPE.FIELD)
    String chgId;
    @Description(name = "수정일", description = "안전일지 Field", type = Description.TYPE.FIELD)
    LocalDateTime chgDt;
    @Description(name = "삭제자ID", description = "안전일지 Field", type = Description.TYPE.FIELD)
    String dltId;
    @Description(name = "삭제일", description = "안전일지 Field", type = Description.TYPE.FIELD)
    LocalDateTime dltDt;
}
