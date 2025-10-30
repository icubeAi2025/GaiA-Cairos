package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@IdClass(PrMonthlyReportStatusId.class)
@Alias("prMonthlyReportStatus")
public class PrMonthlyReportStatus extends AbstractRudIdTime{

    @Id
	@Description(name = "계약변경ID", description = "", type = Description.TYPE.FIELD)
    String cntrctChgId;

    @Id
	@Column(columnDefinition = "NUMERIC")
	@Description(name = "월간보고ID", description = "", type = Description.TYPE.FIELD)
    Long monthlyReportId;

    @Id
	@Description(name = "공사종류 코드", description = "", type = Description.TYPE.FIELD)
    String cnstrctCd;

    @Description(name = "주요공사 현황", description = "", type = Description.TYPE.FIELD)
    String cnstrctPerf;

    @Description(name = "주요공사 차월 추진사항", description = "", type = Description.TYPE.FIELD)
    String cnstrctPlan;

    @Description(name = "주요공사 비고", description = "", type = Description.TYPE.FIELD)
    String cnstrctNote;

    @Description(name = "현안사항 문제점", description = "", type = Description.TYPE.FIELD)
    String currentIssues;

    @Description(name = "현안사항 추진방안", description = "", type = Description.TYPE.FIELD)
    String currentActionPlan;

    @Description(name = "현안사항 비고", description = "", type = Description.TYPE.FIELD)
    String currentNote;

    @Description(name = "삭제여부", description = "", type = Description.TYPE.FIELD)
    String dltYn;

}
