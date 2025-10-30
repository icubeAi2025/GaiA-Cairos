package kr.co.ideait.platform.gaiacairos.core.persistence.vo.system;

import org.apache.ibatis.type.Alias;
import org.mapstruct.Mapper;

import kr.co.ideait.platform.gaiacairos.core.config.GlobalMapperConfig;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisPageable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Mapper(config = GlobalMapperConfig.class)
public interface MenuAuthorityGorupMybatisParam {

    @Data
    @Alias("menuAuthorityGroupListInput")
    @EqualsAndHashCode(callSuper = true)
    public class MenuAuthorityGroupListInput extends MybatisPageable {
        String cntrctNo;
        String systemType;
        String menuCd;    
        String searchText;
        String aTypeCode;
        String roleCode;
        String kindCode;
        String lang;
        String columnNm;
    }

    @Data
    @Alias("menuAuthorityGroupListOutput")
    @EqualsAndHashCode(callSuper = true)
    public class MenuAuthorityGroupListOutput extends MybatisPageable {
        String cmnCd;
        String cmnCdNmKrn;
    }
}
