package kr.co.ideait.platform.gaiacairos.core.persistence.model;

import java.time.LocalDateTime;
import java.util.HashMap;

import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import org.apache.ibatis.type.Alias;
import org.springframework.data.domain.Pageable;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Alias("input")
public class MybatisInput extends HashMap<String, Object> {

    Pageable pageable;

    public MybatisInput() {
        setUser();
    }

    public MybatisInput(UserAuth user) {
        setUser(user);
    }

    public static MybatisInput of() {
        return new MybatisInput();
    }

    public static MybatisInput of(UserAuth user) {
        return new MybatisInput(user);
    }

    public MybatisInput add(String key, Object value) {
        put(key, value);
        return this;
    }

    public MybatisInput setUser() {
        return setUser(UserAuth.get());
    }

    public MybatisInput setUser(UserAuth user) {
        if (user != null) {
            add("user", user);
        }
        return this;
    }

    public MybatisInput setLoginIdAndNow() {
        put("usrId", UserAuth.get(true).getUsrId());
        put("loginId", UserAuth.get(true).getLogin_Id());
        put("now", LocalDateTime.now());
        return this;
    }
}
