package kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.userlog;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import kr.co.ideait.platform.gaiacairos.core.persistence.model.CommonForm;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import org.springframework.format.annotation.DateTimeFormat;


import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.userlog.UserLogMybatisParam.UserLogListInput;
import lombok.Data;
import lombok.ToString;

@Mapper(componentModel = ComponentModel.SPRING)
public interface UserLogForm {

    UserLogListInput toUserLogListInput(UserLogListGet userLogListGet);

    /**
     * 사용자 로그 항목 검색 폼
     */
    @Data
    @ToString
    class UserLogListGet extends CommonForm {
        String logType;
        String user;
        String startDt;
        String endDt;
    }

    /**
     * 사용자 로그 번호 리스트
     */
    @Data
    class LogNoList {
        List<Long> logNoList;
    }

}
