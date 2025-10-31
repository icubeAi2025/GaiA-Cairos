package kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.userlog;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:17+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class UserLogFormImpl implements UserLogForm {

    @Override
    public UserLogMybatisParam.UserLogListInput toUserLogListInput(UserLogListGet userLogListGet) {
        if ( userLogListGet == null ) {
            return null;
        }

        UserLogMybatisParam.UserLogListInput userLogListInput = new UserLogMybatisParam.UserLogListInput();

        userLogListInput.setPageable( userLogListGet.getPageable() );
        userLogListInput.setLogType( userLogListGet.getLogType() );
        userLogListInput.setUser( userLogListGet.getUser() );
        userLogListInput.setStartDt( userLogListGet.getStartDt() );
        userLogListInput.setEndDt( userLogListGet.getEndDt() );

        return userLogListInput;
    }
}
