package kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.tool.setting;

import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DtDeficiencyPhase;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DtDeficientySchedule;
import lombok.Data;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;

import java.util.List;

@Mapper(componentModel = ComponentModel.SPRING)
public interface SettingForm {

	List<SettingMybatisParam.DisplayOrderMoveInput> toDisplayOrderMoveInput(List<DisplayOrderMove> displayOrderMove);

	SettingMybatisParam.DashboardListInput toDashboardListInput(DashboardList dashboardList);

	// 결함 단계 추가
	@Data
	class DeficientyPhaseInsert {
		@Description(name = "결함단계 객체", description = "", type = Description.TYPE.FIELD)
		DtDeficiencyPhase dtDeficiencyPhase;

		@Description(name = "결함단계 일정 리스트", description = "", type = Description.TYPE.FIELD)
		List<DtDeficientySchedule> scheduleArr;
	}

	// 결함 단계 상세조회
	@Data
	class DeficientyPhaseDetail {
		@Description(name = "계약번호", description = "", type = Description.TYPE.FIELD)
		String cntrctNo;

		@Description(name = "결함단계 번호", description = "", type = Description.TYPE.FIELD)
		String dfccyPhaseNo;
	}

	// 결함 단계 삭제
	@Data
	class DeficientyPhaseDeleteList {
		@Description(name = "계약번호", description = "", type = Description.TYPE.FIELD)
		String cntrctNo;

		@Description(name = "삭제할 결함 단계 리스트", description = "", type = Description.TYPE.FIELD)
		List<String> delPhaseList;
	}

	// 결함 단계 순서 변경
	@Data
	class DisplayOrderMove {
		@Description(name = "결함단계 번호", description = "", type = Description.TYPE.FIELD)
		String dfccyPhaseNo;

		@Description(name = "결함단계 순서", description = "", type = Description.TYPE.FIELD)
		Short dsplyOrdr;
	}

	// 대시보드용
	@Data
	class DashboardList {
		@Description(name = "현재 페이지", description = "", type = Description.TYPE.FIELD)
		int page;

		@Description(name = "페이지 당 보여줄 개수", description = "", type = Description.TYPE.FIELD)
		int size;

		@Description(name = "계약번호", description = "", type = Description.TYPE.FIELD)
		String cntrctNo;

		@Description(name = "사용자 ID", description = "", type = Description.TYPE.FIELD)
		String usrId;
	}
}
