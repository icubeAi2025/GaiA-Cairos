package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@IdClass(PrMonthlyReportAdminId.class)
public class PrMonthlyReportAdmin extends AbstractRudIdTime {
	
	@Id
	@Description(name = "계약변경ID", description = "", type = Description.TYPE.FIELD)
	String cntrctChgId;
	
	@Id
	@Column(columnDefinition = "NUMERIC")
	@Description(name = "월간보고관리자ID", description = "", type = Description.TYPE.FIELD)
	Long monthlyReportAdminId;

	@Description(name = "보고서년월", description = "", type = Description.TYPE.FIELD)
	String reportYm;
	
	@Description(name = "월간보고일자", description = "", type = Description.TYPE.FIELD)
	String monthlyReportDate;
	
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
    
    @Description(name = "승인요청자ID", description = "", type = Description.TYPE.FIELD)
    String apprvlReqId;
    
    @Description(name = "승인요청일자", description = "", type = Description.TYPE.FIELD)
    LocalDateTime apprvlReqDt;
    
    @Description(name = "전자결재문서ID", description = "", type = Description.TYPE.FIELD)
    String apDocId;
    
    @Description(name = "승인자ID", description = "", type = Description.TYPE.FIELD)
    String apprvlId;
    
    @Description(name = "승인일자", description = "", type = Description.TYPE.FIELD)
    LocalDateTime apprvlDt;
    
    @Description(name = "삭제여부", description = "", type = Description.TYPE.FIELD)
    String dltYn;

    // 2025-07-24 추가
    @Description(name = "제목", description = "", type = Description.TYPE.FIELD)
    String title;

    @Description(name = "금월추진사항", description = "", type = Description.TYPE.FIELD)
    String thisMonthPromotion;

    @Description(name = "차월추진계획", description = "", type = Description.TYPE.FIELD)
    String nextMonthPlan;
}
