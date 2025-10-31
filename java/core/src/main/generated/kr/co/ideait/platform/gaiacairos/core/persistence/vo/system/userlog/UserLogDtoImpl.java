package kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.userlog;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:18+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class UserLogDtoImpl implements UserLogDto {

    @Override
    public UserLog fromSmUserLogOutput(UserLogMybatisParam.UserLogOutput userLogOutput) {
        if ( userLogOutput == null ) {
            return null;
        }

        UserLog userLog = new UserLog();

        userLog.setLogNo( userLogOutput.getLogNo() );
        userLog.setLogType( userLogOutput.getLogType() );
        userLog.setUserName( userLogOutput.getUserName() );
        userLog.setUserId( userLogOutput.getUserId() );
        userLog.setExecType( userLogOutput.getExecType() );
        userLog.setRgstDt( userLogOutput.getRgstDt() );
        userLog.setResult( userLogOutput.getResult() );

        return userLog;
    }

    @Override
    public DetailUserLog fromSmUserLogOutput(UserLogMybatisParam.DetailUserLogOutput detailUserLogOutput) {
        if ( detailUserLogOutput == null ) {
            return null;
        }

        DetailUserLog detailUserLog = new DetailUserLog();

        detailUserLog.setLogNo( detailUserLogOutput.getLogNo() );
        detailUserLog.setUserName( detailUserLogOutput.getUserName() );
        detailUserLog.setUserId( detailUserLogOutput.getUserId() );
        detailUserLog.setUserIp( detailUserLogOutput.getUserIp() );
        detailUserLog.setPlatform( detailUserLogOutput.getPlatform() );
        detailUserLog.setLogType( detailUserLogOutput.getLogType() );
        detailUserLog.setRgstDt( detailUserLogOutput.getRgstDt() );
        detailUserLog.setExecType( detailUserLogOutput.getExecType() );
        detailUserLog.setResult( detailUserLogOutput.getResult() );
        detailUserLog.setReferer( detailUserLogOutput.getReferer() );
        detailUserLog.setUserAgent( detailUserLogOutput.getUserAgent() );
        detailUserLog.setErrorReason( detailUserLogOutput.getErrorReason() );

        return detailUserLog;
    }
}
