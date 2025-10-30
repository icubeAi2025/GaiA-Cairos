package kr.co.ideait.platform.gaiacairos.core.persistence.vo.progress;

import org.apache.ibatis.type.Alias;

import lombok.Data;

public interface ProgressstatusMybatisParam {

	@Data
	@Alias("activityOutPut")
	public class ActivityOutput {
		Integer seqNum;
		String wbsCd;
		String wbsNm;
		String activityId;
		String activityNm;
		String planBgnDate;
		String planEndDate;
		String actualBgnDate;
		String actualEndDate;
	}

	@Data
	@Alias("processRateOutPut")
	public class ProcessRateOutPut {
		Integer seqNum;
		String unitCnstType;
		String cnsttyCd;
		String cnsttyNm;
		String cntCost;
		String prevPlanCum;
		String prevActualCum;
		String thisPlanAmt;
		String actualAmt;
		String thisPlanCum;
		String thisActualCum;
		String nextPlanAmt;
	}
}
