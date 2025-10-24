package kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.user;

import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmUserInfo;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:18+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class UserDtoImpl implements UserDto {

    @Override
    public User fromSmUserOutput(UserMybatisParam.UserOutput smUserOutput) {
        if ( smUserOutput == null ) {
            return null;
        }

        User user = new User();

        user.setUsrId( smUserOutput.getUsrId() );
        user.setLoginId( smUserOutput.getLoginId() );
        user.setUsrNm( smUserOutput.getUsrNm() );
        user.setRatngCd( smUserOutput.getRatngCd() );
        user.setPstnCd( smUserOutput.getPstnCd() );
        user.setPhoneNo( smUserOutput.getPhoneNo() );
        user.setTelNo( smUserOutput.getTelNo() );
        user.setEmailAdrs( smUserOutput.getEmailAdrs() );
        user.setUseYn( smUserOutput.getUseYn() );
        user.setDltYn( smUserOutput.getDltYn() );
        user.setRgstrId( smUserOutput.getRgstrId() );
        user.setRgstrDt( smUserOutput.getRgstrDt() );
        user.setChgId( smUserOutput.getChgId() );
        if ( smUserOutput.getChgDt() != null ) {
            user.setChgDt( LocalDateTime.parse( smUserOutput.getChgDt() ) );
        }
        user.setDltId( smUserOutput.getDltId() );
        user.setDltDt( smUserOutput.getDltDt() );
        user.setCompNm( smUserOutput.getCompNm() );
        user.setRatngCdKrn( smUserOutput.getRatngCdKrn() );
        user.setPstnCdKrn( smUserOutput.getPstnCdKrn() );

        return user;
    }

    @Override
    public User toUser(SmUserInfo smUserInfo) {
        if ( smUserInfo == null ) {
            return null;
        }

        User user = new User();

        user.setUsrId( smUserInfo.getUsrId() );
        user.setLoginId( smUserInfo.getLoginId() );
        user.setUsrNm( smUserInfo.getUsrNm() );
        user.setRatngCd( smUserInfo.getRatngCd() );
        user.setPstnCd( smUserInfo.getPstnCd() );
        user.setPhoneNo( smUserInfo.getPhoneNo() );
        user.setTelNo( smUserInfo.getTelNo() );
        user.setEmailAdrs( smUserInfo.getEmailAdrs() );
        user.setUseYn( smUserInfo.getUseYn() );
        user.setDltYn( smUserInfo.getDltYn() );
        user.setRgstrId( smUserInfo.getRgstrId() );
        user.setChgId( smUserInfo.getChgId() );
        user.setChgDt( smUserInfo.getChgDt() );
        user.setDltId( smUserInfo.getDltId() );
        user.setDltDt( smUserInfo.getDltDt() );

        return user;
    }

    @Override
    public UserCreated toUserCreated(SmUserInfo smUserInfo) {
        if ( smUserInfo == null ) {
            return null;
        }

        UserCreated userCreated = new UserCreated();

        userCreated.setUsrId( smUserInfo.getUsrId() );
        userCreated.setLoginId( smUserInfo.getLoginId() );
        userCreated.setUsrNm( smUserInfo.getUsrNm() );

        return userCreated;
    }

    @Override
    public User fromUser(SmUserInfo smUserInfo) {
        if ( smUserInfo == null ) {
            return null;
        }

        User user = new User();

        user.setUsrId( smUserInfo.getUsrId() );
        user.setLoginId( smUserInfo.getLoginId() );
        user.setUsrNm( smUserInfo.getUsrNm() );
        user.setRatngCd( smUserInfo.getRatngCd() );
        user.setPstnCd( smUserInfo.getPstnCd() );
        user.setPhoneNo( smUserInfo.getPhoneNo() );
        user.setTelNo( smUserInfo.getTelNo() );
        user.setEmailAdrs( smUserInfo.getEmailAdrs() );
        user.setUseYn( smUserInfo.getUseYn() );
        user.setDltYn( smUserInfo.getDltYn() );
        user.setRgstrId( smUserInfo.getRgstrId() );
        user.setChgId( smUserInfo.getChgId() );
        user.setChgDt( smUserInfo.getChgDt() );
        user.setDltId( smUserInfo.getDltId() );
        user.setDltDt( smUserInfo.getDltDt() );

        return user;
    }
}
