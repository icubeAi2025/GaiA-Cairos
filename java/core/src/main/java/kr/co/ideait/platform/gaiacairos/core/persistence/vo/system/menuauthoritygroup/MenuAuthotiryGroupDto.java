package kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.menuauthoritygroup;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmMenuAuthorityGroup;
import kr.co.ideait.platform.gaiacairos.core.config.GlobalMapperConfig;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MapDto;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisOutput;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mapstruct.Mapper;

import java.time.LocalDateTime;

@Mapper(config = GlobalMapperConfig.class)
public interface MenuAuthotiryGroupDto {

    MenuAuthorityGroup fromMybatisOutput(MybatisOutput output);

    MenuAuthorityGroup fromSmMenuAuthorityGroup(SmMenuAuthorityGroup smMenuAuthorityGroup);

    @Data
    @EqualsAndHashCode(callSuper = false)
    class MenuAuthorityGroup extends MapDto {
        Integer menuRghtNo;
        // Integer menuNo;
        String menuCd;
        Integer rghtGrpNo;
        String rghtGrpCd;
        // String pjtNo;
        // String cntrctNo;
        String pjtType;
        // String rghtGrpNmEng;
        // String rghtGrpNmKrn;
        String rghtGrpNm;
        String rghtGrpDscrpt;
        String rghtGrpTy;
        String rghtGrpTyNm;
        String rghtGrpRole;
        String rghtGrpRoleNm;
        String rghtKind;
        String rghtKindNm;
        String delKey;
        String useYn;
        Long cnt;
        LocalDateTime chgDt;
    }

}
