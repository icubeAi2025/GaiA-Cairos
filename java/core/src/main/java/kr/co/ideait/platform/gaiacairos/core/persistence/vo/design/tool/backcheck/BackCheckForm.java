package kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.tool.backcheck;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DmAttachments;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DmBackcheck;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.CommonForm;
import lombok.Data;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;

import java.util.List;

@Mapper(componentModel = ComponentModel.SPRING)
public interface BackCheckForm {

	BackCheckMybatisParam.BackCheckListInput toBackCheckListInput(BackCheckList backCheckList);
	
	@Data
	class BackCheckList extends CommonForm {
		int page;
		int size;
		
		String dsgnPhaseNo;
        String cntrctNo;
        String dsgnPhaseCd;
        String dsgnCd;
        String backchkStatus;
        String keyword;
        String rgstrNm;
        String myRplyYn;
        String startDsgnNo;
        String endDsgnNo;
        String rplyCd;
        String apprerCd;
        String backchkCd;
        String startRgstDt;
        String endRgstDt;
        String isuYn;
        String lesnYn;
        String atachYn;
        String usrId;
        String lang;
	}
	
	
	@Data
	class BackCheckDetail {
		String dsgnPhaseNo;
		String dsgnNo;
	}
	
	@Data
	class BackCheckDeleteAll {
		List<DmBackcheck> delList;
	}
	
	@Data
	class BackchkCdUpdate {
		String cntrctNo;
		String dsgnNo;
		String backchkCd;
	}
	
	@Data
	class BackCheckInsert {
		DmBackcheck dmBackcheck;
		List<DmAttachments> delFileList;
	}
}
