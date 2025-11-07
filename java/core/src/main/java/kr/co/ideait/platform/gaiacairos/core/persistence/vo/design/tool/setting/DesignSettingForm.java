package kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.tool.setting;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DmDesignPhase;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DmDesignSchedule;
import lombok.Data;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;

import java.util.List;

@Mapper(componentModel = ComponentModel.SPRING)
public interface DesignSettingForm {

	DesignSettingMybatisParam.DesignPhaseDetailInput toDesignPhaseDetailInput(DesignPhaseDetail designPhaseDetail);

	List<DesignSettingMybatisParam.DesignDisplayOrderMoveInput> toDesignDisplayOrderMoveInput(List<DesignDisplayOrderMove> displayOrderMove);

	@Data
	class DesignPhaseInsert {
		DmDesignPhase dmDesignPhase;
		List<DmDesignSchedule> scheduleArr;
	}

	@Data
	class DesignPhaseDetail {
		String cntrctNo;
		String dsgnPhaseNo;
	}

	@Data
	class DesignPhaseDeleteList {
		String cntrctNo;
		List<String> delPhaseList;
	}

	@Data
	class DesignDisplayOrderMove {
		String dsgnPhaseNo;
		Short dsplyOrdr;
	}

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
