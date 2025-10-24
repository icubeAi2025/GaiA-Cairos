package kr.co.ideait.platform.gaiacairos.core.persistence.vo.progress.wbs.activityqdb;

import kr.co.ideait.platform.gaiacairos.core.persistence.model.CommonForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.progress.wbs.activityqdb.ActivityQdbMybatisParam.*;
import lombok.Data;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;

@Mapper(componentModel = ComponentModel.SPRING)
public interface ActivityQdbForm {

	ActivityWbsListInput toActivityWbsListInput(ActivityWbsList activityWbsList);

	ActivityWbsQdbListInput toActivityWbsQdbListInput(ActivityWbsQdbList activityWbsQdbList);
	
	ActivityCbsListInput toActivityCbsListInput(ActivityCbsList activityCbsList);
	
	ActivityCbsQdbListInput toActivityCbsQdbListInput(ActivityCbsQdbList activityCbsQdbList);

	ActivityTreeListInput toActivityTreeListInput (ActivityTreeList activityTreeList);
	
	@Data
	class ActivityWbsList extends CommonForm {
		String cntrctChgId;
		String wbsCd;
		String searchText;
	}

	@Data
	class ActivityWbsQdbList extends CommonForm {
		String cntrctChgId;
		String activityId;
		String searchText;
	}
	
	@Data
	class ActivityCbsList extends CommonForm {
		String cntrctChgId;
		String unitCnstType;
		String cnsttyCd;
		String searchText;
	}

	@Data
	class ActivityCbsQdbList {
		String cntrctChgId;
        String cnsttySn;
        String dtlCnsttySn;
        String searchText;
    }

	@Data
	class ActivityTreeList extends CommonForm {
		String cntrctChgId;
	}

    
}
