package kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.tool.setting;

import org.apache.ibatis.type.Alias;

import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisPageable;
import lombok.Data;

public interface SettingMybatisParam {

	// 결함 단계 순서 변경
	@Data
	@Alias("displayOrderMoveInput")
	public class DisplayOrderMoveInput {
		String dfccyPhaseNo;
		Short dsplyOrdr;
	}

	// 대시보드 조회
	@Data
	@Alias("dashboardListInput")
	public class DashboardListInput extends MybatisPageable {
		int page;
		int size;
		String cntrctNo;
		String usrId;
	}

}
