package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@IdClass(CwInspectionReportId.class)
public class CwInspectionReport extends AbstractRudIdTime {

    @Id
    @Description(name = "계약번호", description = "", type = Description.TYPE.FIELD)
    String cntrctNo;

    @Column(columnDefinition = "NUMERIC")
    @Description(name = "작업보고ID", description = "", type = Description.TYPE.FIELD)
    Long dailyReportId;

    @Description(name = "작업보고일자", description = "", type = Description.TYPE.FIELD)
    String dailyReportDate;

    @Description(name = "보고서 번호", description = "", type = Description.TYPE.FIELD)
    String reportNo;

    @Description(name = "제목", description = "", type = Description.TYPE.FIELD)
    String title;

    @Description(name = "기상현황", description = "", type = Description.TYPE.FIELD)
    String wthrSttus;

    @Description(name = "오전날씨", description = "", type = Description.TYPE.FIELD)
    String amWthr;

    @Description(name = "오후날씨", description = "", type = Description.TYPE.FIELD)
    String pmWthr;

    @Description(name = "강수량", description = "", type = Description.TYPE.FIELD)
    BigDecimal prcptRate;

    @Description(name = "강설량", description = "", type = Description.TYPE.FIELD)
    BigDecimal snowRate;

    @Description(name = "일최저기온", description = "", type = Description.TYPE.FIELD)
    String dlowstTmprtVal;

    @Description(name = "일최고기온", description = "", type = Description.TYPE.FIELD)
    String dtopTmprtVal;

    @Description(name = "누적계획보할율", description = "", type = Description.TYPE.FIELD)
    BigDecimal acmltPlanBohalRate;

    @Description(name = "누적실적보할율", description = "", type = Description.TYPE.FIELD)
    BigDecimal acmltArsltBohalRate;

    @Description(name = "누적대비율", description = "", type = Description.TYPE.FIELD)
    BigDecimal acmltProcess;

    @Description(name = "당일계획보할율", description = "", type = Description.TYPE.FIELD)
    BigDecimal todayPlanBohalRate;

    @Description(name = "당일실적보할율", description = "", type = Description.TYPE.FIELD)
    BigDecimal todayArsltBohalRate;

    @Description(name = "당일대비율", description = "", type = Description.TYPE.FIELD)
    BigDecimal todayProcess;

    @Description(name = "전일계획보할율", description = "", type = Description.TYPE.FIELD)
    BigDecimal bfrtPlanBohalRate;

    @Description(name = "전일실적보할율", description = "", type = Description.TYPE.FIELD)
    BigDecimal bfrtArsltBohalRate;

    @Description(name = "주요안건", description = "", type = Description.TYPE.FIELD)
    String majorMatter;

    @Description(name = "특이사항", description = "", type = Description.TYPE.FIELD)
    String significantNote;

    @Description(name = "지적사항및처리결과", description = "", type = Description.TYPE.FIELD)
    String commentResult;

    @Description(name = "비고내용", description = "", type = Description.TYPE.FIELD)
    String rmrkCntnts;

    @Description(name = "승인상태", description = "", type = Description.TYPE.FIELD)
    String apprvlStats;

    @Description(name = "전자결재문서ID", description = "", type = Description.TYPE.FIELD)
    String apDocId;

    @Description(name = "승인자ID", description = "", type = Description.TYPE.FIELD)
    String apprvlId;

    @Description(name = "승인일자", description = "", type = Description.TYPE.FIELD)
    LocalDateTime apprvlDt;

    @Description(name = "승인요청자ID", description = "", type = Description.TYPE.FIELD)
    String apprvlReqId;

    @Description(name = "승인요청일자", description = "", type = Description.TYPE.FIELD)
    LocalDateTime apprvlReqDt;

    @Description(name = "삭제여부", description = "", type = Description.TYPE.FIELD)
    String dltYn;

    @Description(name = "업무구분 코드", description = "", type = Description.TYPE.FIELD)
    String workCd;

    @Description(name = "문서 번호", description = "", type = Description.TYPE.FIELD)
    String docId;
}

