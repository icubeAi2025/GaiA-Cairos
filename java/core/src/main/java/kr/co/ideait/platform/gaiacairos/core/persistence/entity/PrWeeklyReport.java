package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@IdClass(PrWeeklyReportId.class)
public class PrWeeklyReport extends AbstractRudIdTime{
	
	@Id
	@Description(name = "계약변경ID", description = "", type = Description.TYPE.FIELD)
	String cntrctChgId;
	
	@Id
	@Column(columnDefinition = "NUMERIC")
	@Description(name = "주간보고ID", description = "", type = Description.TYPE.FIELD)
	Long weeklyReportId;

	@Description(name = "보고기준일", description = "", type = Description.TYPE.FIELD)
	String reportDate;
	
	@Description(name = "주간보고일자", description = "", type = Description.TYPE.FIELD)
	String weeklyReportDate;
	
	@Column(columnDefinition = "NUMERIC")
	@Description(name = "누적계획보할율", description = "", type = Description.TYPE.FIELD)
	double acmltPlanBohalRate;
	
	@Column(columnDefinition = "NUMERIC")
	@Description(name = "누적실적보할율", description = "", type = Description.TYPE.FIELD)
	double acmltArsltBohalRate;
	
	@Column(columnDefinition = "NUMERIC")
	@Description(name = "금월계획보할율", description = "", type = Description.TYPE.FIELD)
    double thismthPlanBohalRate;
	
    @Column(columnDefinition = "NUMERIC")
    @Description(name = "금월실적보할율", description = "", type = Description.TYPE.FIELD)
    double thismthArsltBohalRate;
    
    @Column(columnDefinition = "NUMERIC")
    @Description(name = "전월계획보할율", description = "", type = Description.TYPE.FIELD)
    double lsmthPlanBohalRate;
    
    @Column(columnDefinition = "NUMERIC")
    @Description(name = "전월실적보할율", description = "", type = Description.TYPE.FIELD)
    double lsmthArsltBohalRate;
    
    @Description(name = "비고내용", description = "", type = Description.TYPE.FIELD)
    String rmrkCntnts;
    
    @Description(name = "승인상태", description = "", type = Description.TYPE.FIELD)
    String apprvlStats;
    
    @Description(name = "승인자ID", description = "", type = Description.TYPE.FIELD)
    String apprvlId;
    
    @Description(name = "승인일자", description = "", type = Description.TYPE.FIELD)
    LocalDateTime apprvlDt;
    
    @Description(name = "전자결재문서ID", description = "", type = Description.TYPE.FIELD)
    String apDocId;
    
    @Description(name = "승인요청자ID", description = "", type = Description.TYPE.FIELD)
    String apprvlReqId;
    
    @Description(name = "승인요청일자", description = "", type = Description.TYPE.FIELD)
    LocalDateTime apprvlReqDt;
    
    @Description(name = "삭제여부", description = "", type = Description.TYPE.FIELD)
    String dltYn;

    // 2025-07-24 추가
    @Description(name = "제목", description = "", type = Description.TYPE.FIELD)
    String title;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Description(name = "금주 시작일자", description = "", type = Description.TYPE.FIELD)
    LocalDate thisWeekStartDay;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Description(name = "금주 종료일자", description = "", type = Description.TYPE.FIELD)
    LocalDate thisWeekEndDay;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Description(name = "차주 시작일자", description = "", type = Description.TYPE.FIELD)
    LocalDate nextWeekStartDay;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Description(name = "차주 종료일자", description = "", type = Description.TYPE.FIELD)
    LocalDate nextWeekEndDay;

    @Description(name = "금주추진사항", description = "", type = Description.TYPE.FIELD)
    String thisWeekPromotion;

    @Description(name = "차주추진계획", description = "", type = Description.TYPE.FIELD)
    String nextWeekPlan;
}
