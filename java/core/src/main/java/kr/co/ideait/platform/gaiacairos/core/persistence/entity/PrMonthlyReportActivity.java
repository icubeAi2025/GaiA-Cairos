package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@IdClass(PrMonthlyReportActivityId.class)
public class PrMonthlyReportActivity extends AbstractRudIdTime{
	
	@Id
	@Description(name = "계약변경ID", description = "", type = Description.TYPE.FIELD)
	String cntrctChgId;
	
	@Id
	@Column(columnDefinition = "NUMERIC")
	@Description(name = "월간보고ID", description = "", type = Description.TYPE.FIELD)
	Long monthlyReportId;
	
	@Id
	@Description(name = "월간ActivityID", description = "", type = Description.TYPE.FIELD)
	Integer monthlyActivityId;
	
	@Description(name = "WBS코드", description = "", type = Description.TYPE.FIELD)
	String wbsCd;
	
	@Description(name = "ActivityID", description = "", type = Description.TYPE.FIELD)
	String activityId;
	
	@Description(name = "작업구분", description = "", type = Description.TYPE.FIELD)
	String workDtType;
	
	@Description(name = "계획시작일자", description = "", type = Description.TYPE.FIELD)
	String planBgnDate;
	
	@Description(name = "계획종료일자", description = "", type = Description.TYPE.FIELD)
	String planEndDate;
	
	@Description(name = "실행시작일자", description = "", type = Description.TYPE.FIELD)
	String actualBgnDate;
	
	@Description(name = "실행종료일자", description = "", type = Description.TYPE.FIELD)
	String actualEndDate;
	
	@Column(columnDefinition = "NUMERIC")
	@Description(name = "당월계획율", description = "", type = Description.TYPE.FIELD)
	Double thismthPlanRate;
	
	@Column(columnDefinition = "NUMERIC")
	@Description(name = "당월실행율", description = "", type = Description.TYPE.FIELD)
	Double thismthExeRate;
	
	@Description(name = "진행상태코드", description = "", type = Description.TYPE.FIELD)
	String pstats;
	
	@Description(name = "지연구분", description = "", type = Description.TYPE.FIELD)
	String dlyDiv;
	
	@Description(name = "주공정여부", description = "", type = Description.TYPE.FIELD)
	String majorPrcsYn;
	
	@Description(name = "지연사유", description = "", type = Description.TYPE.FIELD)
	String dlyRsn;
	
	@Description(name = "리비전ID", description = "", type = Description.TYPE.FIELD)
	String revisionId;
	
	@Description(name = "삭제여부", description = "", type = Description.TYPE.FIELD)
	String dltYn;
}
