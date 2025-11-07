package kr.co.ideait.platform.gaiacairos.core.persistence.vo.security;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmUserInfo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.department.DepartmentDto;
import kr.co.ideait.platform.gaiacairos.core.config.GlobalMapperConfig;
import kr.co.ideait.platform.gaiacairos.core.constant.KeyConstants;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;
import org.apache.ibatis.type.Alias;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Map;

@Mapper(config = GlobalMapperConfig.class)
public interface User {

    /*********************************************************************
     * 포털 사용자 정보 임시
     */
    @Mapping(target = "usrId", source = "me.usr_id")
    @Mapping(target = "loginId", source = "me.login_id")
    @Mapping(target = "usrNm", source = "me.usr_nm")
    @Mapping(target = "emailAdrs", source = "me.login_id")
    SmUserInfo toSmUserInfo(PortalMe me);

    void updateSmUserInfo(PortalMe portalMe, @MappingTarget SmUserInfo smUserInfo);

    @Mapping(target = "usr_id", expression = "java(me.getOrDefault(\"usr_id\", \"\").toString())")
    @Mapping(target = "login_id", expression = "java(me.getOrDefault(\"login_id\", \"\").toString())")
    @Mapping(target = "usr_nm", expression = "java(me.getOrDefault(\"usr_nm\", \"\").toString())")
    PortalMe toPortalMe(Map<String, Object> me);

    SimpleUser toSimpleUser(UserAuth user);

    @Data
    class PortalMe {
        String usr_id;
        String login_id;
        String usr_nm;
    }

    @Data
    @Alias("accessAuthority")
    class AccessAuthority {
        String userType;
    }

    @Data
    @Alias("accessMenuAuthority")
    class AccessMenuAuthority {
        String menuNo;
        String cntrctNo;
    }

    @Data
    class TokenUser {
        String id;
        String loginId;
        List<? extends GrantedAuthority> roles;

        @SuppressWarnings("unchecked")
        public static TokenUser of(Map<String, Object> claims) {
            TokenUser tokenUser = new TokenUser();
            tokenUser.setId(claims.get(KeyConstants.TOKEN_ID).toString());
            tokenUser.setLoginId(claims.get(KeyConstants.TOKEN_LOGINID).toString());
            if (claims.containsKey(KeyConstants.TOKEN_ROLES)) {
                List<String> roles = (List<String>) claims.get(KeyConstants.TOKEN_ROLES);
                tokenUser.setRoles(roles.stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role)).toList());
            } else {
                tokenUser.setRoles(List.of());
            }
            return tokenUser;
        }

        public Object getPrincical() {
            return this;
        }

        public Object getCredentials() {
            return null;
        }
    }

    @Data
    class SimpleUser {

        String name;
        String usrId;
        String loginId;
        boolean admin;
        String pjtNo;
        String cntrctNo;
        boolean selected;

        @Description(name = "사용자 부서 목록", description = "")
        List<DepartmentDto.Department> departments;

        @Description(name = "사용자 프로젝트 목록", description = "")
        private List<Map<String, Object>> projects;
    }
}
