package kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.resources;

import org.apache.ibatis.type.Alias;

import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisPageable;
import lombok.Data;
import lombok.EqualsAndHashCode;

public interface ResourcesMybatisParam {
	
	@Data
    @Alias("resourcesListInput")
    @EqualsAndHashCode(callSuper = true)
    public class ResourcesListInput extends MybatisPageable {
        String searchType;
        String searchValue;
        String cmnGrpCd;
    }
	
	@Data
    @Alias("resourcesListOutput")
    @EqualsAndHashCode(callSuper = true)
    public class ResourcesListOutput extends MybatisPageable {		
		String rescId;
		String menuNm;
		String menuDepth;
		String menuCd;
		String rghtKind;
		String rghtKindNmKr;
		String rghtKindNmEn;
		String rescNm;
		String rescUrl;
		String useYn;
        Long cnt;
    }
	
	@Data
    @Alias("resourcesInsertInput")
    public class ResourcesInsertInput {		
		String rescId;
		String menuCd;
		String rescNm;
		String rghtKind;
		String rescUrl;
		String rescDscr;
		String useYn;
		String usrId;
        String cmnGrpCd;
    }
	
	@Data
    @Alias("resourcesInfoInput")
    public class ResourcesInfoInput {
        String rescId;
        String cmnGrpCd;
    }
}
