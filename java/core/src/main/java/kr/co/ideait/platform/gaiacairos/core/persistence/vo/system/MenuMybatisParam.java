package kr.co.ideait.platform.gaiacairos.core.persistence.vo.system;

import org.apache.ibatis.type.Alias;
import org.mapstruct.Mapper;

import kr.co.ideait.platform.gaiacairos.core.config.GlobalMapperConfig;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisPageable;
import lombok.Data;

@Mapper(config = GlobalMapperConfig.class)
public interface MenuMybatisParam {

    @Data
    @Alias("menuBillingListInput")
    public class MenuBillingListInput extends MybatisPageable {
        Integer menuNo;
        String cmnGrpCd;
    }

    @Data
    @Alias("existBillingInput")
    public class ExistBillingInput extends MybatisPageable {
        Integer menuNo;
        String menuCd;
        String bilCode;
    }

    @Data
    @Alias("menuMoveInput")
    public class MenuMoveInput extends MybatisPageable {
        Short menuDsplyOrdr;
        Short menuLvl;
        String upMenuCd;
        String menuCd;
    }

    @Data
    @Alias("menuBtnAuthorityListInput")
    public class MenuBtnAuthorityListInput extends MybatisPageable {
        Integer menuNo;
        String menuCd;

        String sortColumn;
        boolean sortDirection;
    }

}
