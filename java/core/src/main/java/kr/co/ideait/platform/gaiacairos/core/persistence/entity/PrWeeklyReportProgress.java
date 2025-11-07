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
@IdClass(PrWeeklyReportProgressId.class)
public class PrWeeklyReportProgress extends AbstractRudIdTime {
	@Id
	@Description(name = "계약변경ID", description = "", type = Description.TYPE.FIELD)
	String cntrctChgId;
	
	@Id
	@Column(columnDefinition = "NUMERIC")
	@Description(name = "주간보고ID", description = "", type = Description.TYPE.FIELD)
	Long weeklyReportId;
	
	@Id
	@Description(name = "주간공종ID", description = "", type = Description.TYPE.FIELD)
	Integer weeklyCnsttyId;
	
	@Description(name = "공종코드", description = "", type = Description.TYPE.FIELD)
	String cnsttyCd;
	
	@Description(name = "공종명", description = "", type = Description.TYPE.FIELD)
	String cnsttyNm;
	
	@Column(columnDefinition = "NUMERIC")
	@Description(name = "누적계획보할율", description = "", type = Description.TYPE.FIELD)
	Double acmltPlanBohalRate;
	
	@Column(columnDefinition = "NUMERIC")
	@Description(name = "누적실적보할율", description = "", type = Description.TYPE.FIELD)
	Double acmltArsltBohalRate;
	
	@Column(columnDefinition = "NUMERIC")
	@Description(name = "금주계획보할율", description = "", type = Description.TYPE.FIELD)
    Double thismthPlanBohalRate;
	
    @Column(columnDefinition = "NUMERIC")
    @Description(name = "금주실적보할율", description = "", type = Description.TYPE.FIELD)
    Double thismthArsltBohalRate;
    
    @Column(columnDefinition = "NUMERIC")
    @Description(name = "전주계획보할율", description = "", type = Description.TYPE.FIELD)
    Double lsmthPlanBohalRate;
    
    @Column(columnDefinition = "NUMERIC")
    @Description(name = "전주실적보할율", description = "", type = Description.TYPE.FIELD)
    Double lsmthArsltBohalRate;
    
    @Description(name = "비고", description = "", type = Description.TYPE.FIELD)
	String rmk;
    
    @Description(name = "삭제여부", description = "", type = Description.TYPE.FIELD)
	String dltYn;
	
	@Column(columnDefinition = "NUMERIC")
	@Description(name = "계약금액", description = "", type = Description.TYPE.FIELD)
	Double cntrctAm;
	
	@Column(columnDefinition = "NUMERIC")
	@Description(name = "계약보할", description = "", type = Description.TYPE.FIELD)
	Double cntrctBohalRate;
}
