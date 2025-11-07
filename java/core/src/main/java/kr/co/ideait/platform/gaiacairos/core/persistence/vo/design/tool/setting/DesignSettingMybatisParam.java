package kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.tool.setting;

import org.apache.ibatis.type.Alias;

import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisPageable;
import lombok.Data;

public interface DesignSettingMybatisParam {

	@Data
	@Alias("designPhaseDetailInput")
	public class DesignPhaseDetailInput {
		String cntrctNo;
		String dsgnPhaseNo;
	}

	@Data
	@Alias("designDisplayOrderMoveInput")
	public class DesignDisplayOrderMoveInput {
		String dsgnPhaseNo;
		Short dsplyOrdr;
	}

	@Data
	@Alias("designDashboardListInput")
	public class DesignDashboardListInput extends MybatisPageable {
		int page;
		int size;
		String cntrctNo;
		String usrId;
	}

}
