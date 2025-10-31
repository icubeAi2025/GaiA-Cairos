package kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.menuauthoritygroup;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmMenuAuthorityGroup;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:16+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class MenuAuthorityGroupFormImpl implements MenuAuthorityGroupForm {

    @Override
    public List<SmMenuAuthorityGroup> toSmMenuAuthorityGroupList(List<MenuAuthorityGroupCreate> menuAuthorityGroupCreateList) {
        if ( menuAuthorityGroupCreateList == null ) {
            return null;
        }

        List<SmMenuAuthorityGroup> list = new ArrayList<SmMenuAuthorityGroup>( menuAuthorityGroupCreateList.size() );
        for ( MenuAuthorityGroupCreate menuAuthorityGroupCreate : menuAuthorityGroupCreateList ) {
            list.add( menuAuthorityGroupCreateToSmMenuAuthorityGroup( menuAuthorityGroupCreate ) );
        }

        return list;
    }

    @Override
    public void updateSmMenuAuthorityGroup(MenuAuthorityGroupUpdate menuAuthorityGroup, SmMenuAuthorityGroup smMenuAuthorityGroup) {
        if ( menuAuthorityGroup == null ) {
            return;
        }

        if ( menuAuthorityGroup.getMenuRghtNo() != null ) {
            smMenuAuthorityGroup.setMenuRghtNo( menuAuthorityGroup.getMenuRghtNo() );
        }
        if ( menuAuthorityGroup.getRghtKind() != null ) {
            smMenuAuthorityGroup.setRghtKind( menuAuthorityGroup.getRghtKind() );
        }
    }

    protected SmMenuAuthorityGroup menuAuthorityGroupCreateToSmMenuAuthorityGroup(MenuAuthorityGroupCreate menuAuthorityGroupCreate) {
        if ( menuAuthorityGroupCreate == null ) {
            return null;
        }

        SmMenuAuthorityGroup smMenuAuthorityGroup = new SmMenuAuthorityGroup();

        smMenuAuthorityGroup.setMenuCd( menuAuthorityGroupCreate.getMenuCd() );
        smMenuAuthorityGroup.setRghtGrpCd( menuAuthorityGroupCreate.getRghtGrpCd() );
        smMenuAuthorityGroup.setRghtKind( menuAuthorityGroupCreate.getRghtKind() );

        return smMenuAuthorityGroup;
    }
}
