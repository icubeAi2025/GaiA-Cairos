package kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.resources;

import java.util.List;

import org.mapstruct.Mapper;

import kr.co.ideait.platform.gaiacairos.core.config.GlobalMapperConfig;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.CommonForm;
import lombok.Data;



@Mapper(config = GlobalMapperConfig.class)
public interface ResourcesForm { 
	
	ResourcesMybatisParam.ResourcesListInput toResourcesListInput(ResourcesListForm resourcesListForm);
	
	ResourcesMybatisParam.ResourcesInsertInput toResourcesInsertInput(ResourcesInsertForm resourcesInsertForm);
	
	ResourcesMybatisParam.ResourcesInfoInput toResourcesInfoInput(ResourcesReadForm resourcesReadForm);
	
	@Data
    class ResourcesListForm extends CommonForm {
        String searchType;
        String searchValue;
    }
	
	@Data
    class ResourcesExistForm {
        String existParam;
    }
	
	@Data
    class ResourcesInsertForm {
        String rescId;
        String menuCd;
        String rescNm;
        String rghtKind;
        String rescUrl;
        String rescDscr;
        String useYn;
    }
	
	/**
     * 프로그램 아이디 List
     */
    @Data
    class RescIdList {
        List<String> rescIdList;
    }
    
    @Data
    class ResourcesReadForm {
        String rescId;
    }
}