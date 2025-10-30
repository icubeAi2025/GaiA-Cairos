package kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.tool.dashboard;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DmDesignPhase;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DmDesignSchedule;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.tool.setting.DesignSettingMybatisParam;
import lombok.Data;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;

import java.util.List;

@Mapper(componentModel = ComponentModel.SPRING)
public interface DesignDashboardForm {
	DesignSettingMybatisParam.DesignDashboardListInput toDesignDashboardListInput(DesignDashboardList dashboardList);

	// 대시보드용
	@Data
	class DesignDashboardList {
		int page;
		int size;
		String cntrctNo;
		String usrId;
	}
}
