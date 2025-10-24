package kr.co.ideait.platform.gaiacairos.core.persistence.vo.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.processing.Generated;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmUserInfo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.department.DepartmentDto;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:17+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class UserImpl implements User {

    @Override
    public SmUserInfo toSmUserInfo(PortalMe me) {
        if ( me == null ) {
            return null;
        }

        SmUserInfo smUserInfo = new SmUserInfo();

        smUserInfo.setUsrId( me.getUsr_id() );
        smUserInfo.setLoginId( me.getLogin_id() );
        smUserInfo.setUsrNm( me.getUsr_nm() );
        smUserInfo.setEmailAdrs( me.getLogin_id() );

        return smUserInfo;
    }

    @Override
    public void updateSmUserInfo(PortalMe portalMe, SmUserInfo smUserInfo) {
        if ( portalMe == null ) {
            return;
        }
    }

    @Override
    public PortalMe toPortalMe(Map<String, Object> me) {
        if ( me == null ) {
            return null;
        }

        PortalMe portalMe = new PortalMe();

        portalMe.setUsr_id( me.getOrDefault("usr_id", "").toString() );
        portalMe.setLogin_id( me.getOrDefault("login_id", "").toString() );
        portalMe.setUsr_nm( me.getOrDefault("usr_nm", "").toString() );

        return portalMe;
    }

    @Override
    public SimpleUser toSimpleUser(UserAuth user) {
        if ( user == null ) {
            return null;
        }

        SimpleUser simpleUser = new SimpleUser();

        simpleUser.setName( user.getName() );
        simpleUser.setUsrId( user.getUsrId() );
        simpleUser.setAdmin( user.isAdmin() );
        simpleUser.setPjtNo( user.getPjtNo() );
        simpleUser.setCntrctNo( user.getCntrctNo() );
        simpleUser.setSelected( user.isSelected() );
        List<DepartmentDto.Department> list = user.getDepartments();
        if ( list != null ) {
            simpleUser.setDepartments( new ArrayList<DepartmentDto.Department>( list ) );
        }

        return simpleUser;
    }
}
