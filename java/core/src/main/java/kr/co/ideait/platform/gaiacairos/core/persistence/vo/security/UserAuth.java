package kr.co.ideait.platform.gaiacairos.core.persistence.vo.security;

import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.department.DepartmentDto;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.User.TokenUser;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.iframework.secu.BaseUserAuth;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;
import java.util.List;

@Slf4j
@Getter
@Setter
public class UserAuth extends BaseUserAuth {

    String usrId;
    String login_Id;
    String system_type;
    boolean admin;

    // common selected project and contract
    String pjtNo;
    String cntrctNo;
    boolean selected;

    List<DepartmentDto.Department> departments;

    public UserAuth(Object aPrincipal, Object aCredentials, Collection<? extends GrantedAuthority> anAuthorities) {
        super(aPrincipal, aCredentials, anAuthorities);
    }

    public static UserAuth of(TokenUser token) {
        UserAuth userAuthentication = new UserAuth(token.getPrincical(), token.getCredentials(), token.getRoles());
        userAuthentication.setUsrId(token.getId());
        userAuthentication.setLogin_Id(token.getLoginId());
        token.getRoles().stream().forEach(role -> {
            if (role.getAuthority().equals("ROLE_2")) {
                userAuthentication.admin = true;
            }
        });
        return userAuthentication;
    }

    public static UserAuth dummy() {
        UserAuth dummy = new UserAuth(null, null, null);
        dummy.setUsrId("dummy");
        dummy.setLogin_Id("dummy");
        return dummy;
    }

    public static UserAuth get() {
        return get(false);
    }

    public static UserAuth get(boolean ex) {
        Authentication user = SecurityContextHolder.getContext().getAuthentication();
        if (user != null) {
            return (UserAuth) user;
        }
        if (!ex) {
            return null;
        }
        throw new GaiaBizException(ErrorType.UNAUTHORIZED, "UserAuth정보가 없습니다.");
    }

    public void setSelectedProjectAndContract(String str) {
        if (str != null) {
            String[] arr = str.split(":");
            if (arr.length == 2) {
                setPjtNo(arr[0]);
                setCntrctNo(arr[1]);
                setSelected(true);
            }
        }
    }

    public void superChange(String pjtNo, String cntrctNo) {
        if (admin) {
            if (pjtNo != null && !pjtNo.isEmpty()) {
                setPjtNo(pjtNo);
            }
            if (cntrctNo != null && !cntrctNo.isEmpty()) {
                setCntrctNo(cntrctNo);
            }
        } else {
            throw new GaiaBizException(ErrorType.FORBIDDEN);
        }
    }
}
