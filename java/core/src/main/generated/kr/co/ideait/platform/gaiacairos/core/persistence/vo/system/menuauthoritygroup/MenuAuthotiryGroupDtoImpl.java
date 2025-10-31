package kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.menuauthoritygroup;

import javax.annotation.processing.Generated;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmMenuAuthorityGroup;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisOutput;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:17+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class MenuAuthotiryGroupDtoImpl implements MenuAuthotiryGroupDto {

    @Override
    public MenuAuthorityGroup fromMybatisOutput(MybatisOutput output) {
        if ( output == null ) {
            return null;
        }

        MenuAuthorityGroup menuAuthorityGroup = new MenuAuthorityGroup();

        for ( java.util.Map.Entry<String, Object> entry : output.entrySet() ) {
            String key = entry.getKey();
            Object value = entry.getValue();
            menuAuthorityGroup.put( key, value );
        }

        return menuAuthorityGroup;
    }

    @Override
    public MenuAuthorityGroup fromSmMenuAuthorityGroup(SmMenuAuthorityGroup smMenuAuthorityGroup) {
        if ( smMenuAuthorityGroup == null ) {
            return null;
        }

        MenuAuthorityGroup menuAuthorityGroup = new MenuAuthorityGroup();

        menuAuthorityGroup.setMenuRghtNo( smMenuAuthorityGroup.getMenuRghtNo() );
        menuAuthorityGroup.setMenuCd( smMenuAuthorityGroup.getMenuCd() );
        menuAuthorityGroup.setRghtGrpNo( smMenuAuthorityGroup.getRghtGrpNo() );
        menuAuthorityGroup.setRghtGrpCd( smMenuAuthorityGroup.getRghtGrpCd() );
        menuAuthorityGroup.setRghtKind( smMenuAuthorityGroup.getRghtKind() );
        menuAuthorityGroup.setChgDt( smMenuAuthorityGroup.getChgDt() );

        return menuAuthorityGroup;
    }
}
