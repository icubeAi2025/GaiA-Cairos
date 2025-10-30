package kr.co.ideait.platform.gaiacairos.core.persistence.vo.eapproval.draft;

import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisPageable;
import lombok.Data;
import org.apache.ibatis.type.Alias;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;

@Mapper(componentModel = ComponentModel.SPRING)
public interface DraftMybatisParam {
	
	@Data
	@Alias("searchAppLine")
	public class SearchAppLine extends MybatisPageable {
		String pjtNo;
		String cntrctNo;
		String pstnCd;
		String searchText;
		String apCnrsRng;
		String usrId;
		String deptId;
		String deptType;
	}

	@Data
	@Alias("apFormListOutput")
    class ApFormListOutput {
        String frmNm;
        String frmId;
		Integer frmNo;
        String frmGroup;
    }

}
