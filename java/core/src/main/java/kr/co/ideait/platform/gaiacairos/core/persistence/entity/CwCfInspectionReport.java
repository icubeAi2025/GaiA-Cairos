package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

import java.time.LocalDateTime;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Alias("cwCfInspectionReport")
@IdClass(CwCfInspectionReportId.class)
public class CwCfInspectionReport extends AbstractRudIdTime {

    @Id
    @Description(name = "계약번호", description = "", type = Description.TYPE.FIELD)
    String cntrctNo; // 계약번호

    @Id
    @Column(columnDefinition = "NUMERIC")
    @Description(name = "책임감리일지 일련번호", description = "", type = Description.TYPE.FIELD)
    Long dailyReportId; // 책임감리일지 일련번호

    @Description(name = "보고일자", description = "보고일자 (yyyy-mm-dd)", type = Description.TYPE.FIELD)
    String dailyReportDate; // 보고일자 (yyyy-mm-dd)

    @Description(name = "보고서 번호", description = "", type = Description.TYPE.FIELD)
    String reportNo; // 보고서 번호

    @Description(name = "제목", description = "", type = Description.TYPE.FIELD)
    String title; // 제목

    @Description(name = "책임감리 이름", description = "기존 감리일지에는 없던 추가 컬럼", type = Description.TYPE.FIELD)
    String chiefMgr; // 책임감리 이름 (기존 감리일지에는 없던 추가 컬럼)

    @Description(name = "날씨 상태 요약", description = "", type = Description.TYPE.FIELD)
    String wthrSttus; // 날씨 상태 요약

    @Description(name = "오전 날씨", description = "", type = Description.TYPE.FIELD)
    String amWthr; // 오전 날씨

    @Description(name = "오후 날씨", description = "", type = Description.TYPE.FIELD)
    String pmWthr; // 오후 날씨

    @Column(name = "prcpt_rate", columnDefinition = "NUMERIC")
    @Description(name = "강수량", description = "강수량(mm)", type = Description.TYPE.FIELD)
    Long prcptRate; // 강수량(mm)

    @Column(name = "snow_rate", columnDefinition = "NUMERIC")
    @Description(name = "적설량", description = "적설량(mm)", type = Description.TYPE.FIELD)
    Long snowRate; // 적설량(mm)

    @Description(name = "최저 온도", description = "최저 온도(℃)", type = Description.TYPE.FIELD)
    String dlowstTmprtVal; // 최저 온도(℃)

    @Description(name = "최고 온도", description = "최고 온도(℃)", type = Description.TYPE.FIELD)
    String dtopTmprtVal; // 최고 온도(℃)

    @Column(name = "acmlt_plan_bohal_rate", columnDefinition = "NUMERIC")
    @Description(name = "누적 계획 공정률", description = "누적 계획 공정률(%)", type = Description.TYPE.FIELD)
    Double acmltPlanBohalRate; // 누적 계획 공정률(%)

    @Column(name = "acmlt_arslt_bohal_rate", columnDefinition = "NUMERIC")
    @Description(name = "누적 실적 공정률", description = "누적 실적 공정률(%)", type = Description.TYPE.FIELD)
    Double acmltArsltBohalRate; // 누적 실적 공정률(%)

    @Column(name = "acmlt_process", columnDefinition = "NUMERIC")
    @Description(name = "누적 공정 대비율", description = "누적 공정 대비율(%)", type = Description.TYPE.FIELD)
    Double acmltProcess; // 누적 공정 대비율(%)

    @Column(name = "today_plan_bohal_rate", columnDefinition = "NUMERIC")
    @Description(name = "당일 계획 공정률", description = "당일 계획 공정률(%)", type = Description.TYPE.FIELD)
    Double todayPlanBohalRate; // 당일 계획 공정률(%)

    @Column(name = "today_arslt_bohal_rate", columnDefinition = "NUMERIC")
    @Description(name = "당일 실적 공정률", description = "당일 실적 공정률(%)", type = Description.TYPE.FIELD)
    Double todayArsltBohalRate; // 당일 실적 공정률(%)

    @Column(name = "today_process", columnDefinition = "NUMERIC")
    @Description(name = "당일 공정 대비율", description = "당일 공정 대비율(%)", type = Description.TYPE.FIELD)
    Double todayProcess; // 당일 공정 대비율(%)

    @Column(name = "bfrt_plan_bohal_rate", columnDefinition = "NUMERIC")
    @Description(name = "전일 계획 공정률", description = "전일 계획 공정률(%)", type = Description.TYPE.FIELD)
    Double bfrtPlanBohalRate; // 전일 계획 공정률(%)

    @Column(name = "bfrt_arslt_bohal_rate", columnDefinition = "NUMERIC")
    @Description(name = "전일 실적 공정률", description = "전일 실적 공정률(%)", type = Description.TYPE.FIELD)
    Double bfrtArsltBohalRate; // 전일 실적 공정률(%)

    @Description(name = "주요업무", description = "주요업무 (공종)", type = Description.TYPE.FIELD)
    String majorMatter; // 주요업무 (공종)

    @Description(name = "주요업무의 작업내용", description = "", type = Description.TYPE.FIELD)
    String significantNote; // 주요업무의 작업내용

    @Description(name = "주요업무의 특이사항", description = "", type = Description.TYPE.FIELD)
    String commentResult; // 주요업무의 특이사항

    @Description(name = "특이사항", description = "", type = Description.TYPE.FIELD)
    String rmrkCntnts; // 특이사항

    @Description(name = "승인 상태 코드", description = "", type = Description.TYPE.FIELD)
    String apprvlStats; // 승인 상태 코드

    @Description(name = "결재 문서 ID", description = "", type = Description.TYPE.FIELD)
    String apDocId; // 결재 문서 ID

    @Description(name = "승인자 ID", description = "", type = Description.TYPE.FIELD)
    String apprvlId; // 승인자 ID

    @Description(name = "승인 일시", description = "", type = Description.TYPE.FIELD)
    LocalDateTime apprvlDt; // 승인 일시

    @Description(name = "승인 요청자 ID", description = "", type = Description.TYPE.FIELD)
    String apprvlReqId; // 승인 요청자 ID

    @Description(name = "승인 요청 일시", description = "", type = Description.TYPE.FIELD)
    LocalDateTime apprvlReqDt; // 승인 요청 일시

    @Description(name = "삭제 여부", description = "삭제 여부 (N:정상, Y:삭제)", type = Description.TYPE.FIELD)
    String dltYn; // 삭제 여부 (N:정상, Y:삭제)

    @Description(name = "업무 코드", description = "업무 코드/공종 코드", type = Description.TYPE.FIELD)
    String workCd; // 업무 코드/공종 코드

    @Description(name = "책임 주요업무 수행내용", description = "주요업무 (공종)", type = Description.TYPE.FIELD)
    String cfMajorMatter; // 주요업무 수행내용

    @Description(name = "책임 지시사항", description = "", type = Description.TYPE.FIELD)
    String cfRmrkCntnts; // 지시사항

    @Description(name = "책임 지시사항", description = "", type = Description.TYPE.FIELD)
    String cfTimeRange; // 지시사항

    @Description(name = "문서 ID (PDF 문서 구분 ID)", description = "", type = Description.TYPE.FIELD)
    String docId; // 문서 ID (PDF 문서 구분 ID)
} 