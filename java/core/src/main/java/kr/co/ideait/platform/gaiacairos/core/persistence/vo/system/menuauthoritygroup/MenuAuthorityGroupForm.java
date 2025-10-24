package kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.menuauthoritygroup;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnSubcontract;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmMenuAuthorityGroup;
import kr.co.ideait.platform.gaiacairos.core.config.GlobalMapperConfig;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.CommonForm;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(config = GlobalMapperConfig.class)
public interface MenuAuthorityGroupForm {

    List<SmMenuAuthorityGroup> toSmMenuAuthorityGroupList(List<MenuAuthorityGroupCreate> menuAuthorityGroupCreateList);

    void updateSmMenuAuthorityGroup(MenuAuthorityGroupUpdate menuAuthorityGroup, @MappingTarget SmMenuAuthorityGroup smMenuAuthorityGroup);

    @Data
    @EqualsAndHashCode(callSuper = true)
    class MenuAuthorityGroupListGet extends CommonForm {
        // @NotNull
        // Integer menuNo;
        // @NotBlank
        // String cntrctNo;
        @NotBlank
        String menuCd;
        @NotBlank
        String cntrctNo;
        String systemType;

        String searchText;
        String columnNm;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    class MenuAuthorityGroupCreate extends CommonForm {
        @NotBlank
        String menuCd;
        @NotBlank
        String rghtGrpCd;
        @NotBlank(message = "권한 종류가 없습니다.")
        String rghtKind;
    }

    @Data
    class MenuAuthorityGroupUpdate{
        @NotNull
        Integer menuRghtNo;
        @NotBlank(message = "권한 종류가 없습니다.")
        String rghtKind;
    }
    
    /**
     * 메뉴권한그룹 권한수정
     */
    @Data
    class MenuAuthorityGroupRghtKindUpdate{
        @NotNull
        String menuCd;
        @NotNull
        String rghtGrpCd;
        List<String> checkedRghtKindAndActionType;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    class MenuAuthorityGroupCreateList extends CommonForm {
        @NotNull
        @Valid
        List<MenuAuthorityGroupCreate> menuAuthorityGroupList;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    class MenuAuthorityGroupDeleteList extends CommonForm {
        @NotNull
        String menuCd;
        @NotNull
        List<String> rghtGrpCdList;
    }
    
    @Data
    class MenuAuthorityGroupDelete{
    	List<String> menuAuthorityGroupDelete;
    }

    
    @Data
    class selectMenuAuthorityForm{
        String menuCd;
    }

}
