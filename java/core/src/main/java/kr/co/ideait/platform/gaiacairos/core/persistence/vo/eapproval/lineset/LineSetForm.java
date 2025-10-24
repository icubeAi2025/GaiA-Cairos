package kr.co.ideait.platform.gaiacairos.core.persistence.vo.eapproval.lineset;

import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.ApLineSet;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.ApLinesetMng;
import lombok.Data;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;

import java.util.List;

@Mapper(componentModel = ComponentModel.SPRING)
public interface LineSetForm {

	@Data
	class LineSetDeleteList {
		@Description(name = "삭제 리스트", description = "", type = Description.TYPE.FIELD)
		List<Integer> delList;
	}

	@Data
	class LineSetSaveList {
		@Description(name = "계약 번호", description = "", type = Description.TYPE.FIELD)
		String cntrctNo;

		@Description(name = "결재선 관리 객체", description = "", type = Description.TYPE.FIELD)
		ApLinesetMng apLinesetMng;

		@Description(name = "결재선 설정 객체", description = "", type = Description.TYPE.FIELD)
        List<ApLineSet> apLineSet;
	}
}
