package kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.userlog;


import lombok.Data;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.userlog.UserLogMybatisParam.UserLogOutput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.userlog.UserLogMybatisParam.DetailUserLogOutput;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserLogDto {

    UserLog fromSmUserLogOutput(UserLogOutput userLogOutput);
    DetailUserLog fromSmUserLogOutput(DetailUserLogOutput detailUserLogOutput);

    @Data
    class UserLog{
        Long logNo;
        String logType;
        String userName;
        String userId;
        String execType;
        String rgstDt;
        String result;
    }

    @Data
    class DetailUserLog{
        Long logNo;
        String userName;
        String userId;
        String userIp;
        String platform;
        String logType;
        String rgstDt;
        String execType;
        String result;
        String referer;
        String userAgent;
        String errorReason;
    }
}

