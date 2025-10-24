package kr.co.ideait.platform.gaiacairos.core.persistence.vo.progress;

import org.apache.ibatis.type.Alias;

import lombok.Data;

public interface ActualtoplanMybatisParam {

	@Data
	@Alias("activityGraphOutPut")
	public class ActivityGraphOutPut {
		String activityId;
		String activityNm;
		String planStart;
		String planFinish;
		String actualStart;
		String actualFinish;
	}
}
