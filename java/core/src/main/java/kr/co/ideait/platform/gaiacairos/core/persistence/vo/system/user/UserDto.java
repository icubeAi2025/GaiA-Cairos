package kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.user;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmUserInfo;
import kr.co.ideait.platform.gaiacairos.core.config.GlobalMapperConfig;
import lombok.Data;
import org.mapstruct.Mapper;

import java.time.LocalDateTime;

@Mapper(config = GlobalMapperConfig.class)
public interface UserDto {
    User fromSmUserOutput(UserMybatisParam.UserOutput smUserOutput);

    User toUser(SmUserInfo smUserInfo);

    UserCreated toUserCreated(SmUserInfo smUserInfo);

    // 사용자 조회 용
    User fromUser(SmUserInfo smUserInfo);

    @Data
    class User {
        String usrId;
        String loginId;
        String usrNm;
        String ratngCd;
        String pstnCd;
        String phoneNo;
        String telNo;
        String emailAdrs;
        String useYn;
        String dltYn;
        String rgstrId;
        LocalDateTime rgstrDt;
        String chgId;
        LocalDateTime chgDt;
        String dltId;
        LocalDateTime dltDt;

        String compNm;
        String ratngCdKrn;
        String pstnCdKrn;
    }

    @Data
    class UserCreated {
        String usrId;
        String loginId;
        String usrNm;
    }
}
