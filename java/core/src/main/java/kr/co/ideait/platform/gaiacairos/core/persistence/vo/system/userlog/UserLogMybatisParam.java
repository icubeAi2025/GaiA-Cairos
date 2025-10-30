package kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.userlog;

import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisPageable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

public interface UserLogMybatisParam {

    @Data
    @Alias("userLogListInput")
    @EqualsAndHashCode(callSuper = true)
    public class UserLogListInput extends MybatisPageable {
        String logType;
        String user;
        String startDt;
        String endDt;
        String lang;
    }


    @Data
    @Alias("userLogOutput")
    @EqualsAndHashCode(callSuper = true)
    public class UserLogOutput extends MybatisPageable {
        Long logNo;
        String logType;
        String userName;
        String userId;
        String execType;
        String rgstDt;
        String result;
    }

    @Data
    @Alias("detailUserLogOutput")
    public class DetailUserLogOutput{
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
