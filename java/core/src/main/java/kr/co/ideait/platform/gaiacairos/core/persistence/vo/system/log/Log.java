package kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmApiLog;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmUserLog;
import kr.co.ideait.platform.gaiacairos.core.config.GlobalMapperConfig;
import lombok.Data;
import lombok.ToString;
import org.apache.ibatis.type.Alias;
import org.mapstruct.Mapper;

@Mapper(config = GlobalMapperConfig.class)
public interface Log {
    SmUserLog toUserLogEntity(SmUserLogDto logDto);

    SmApiLog toApiLogEntity(SmApiLogDto apiLogForm);

    @Data
    @Alias("smUserLogDto")
    @ToString(callSuper = true)
    class SmUserLogDto extends SmUserLog {
    }

    @Data
    @Alias("smApiLogDto")
    @ToString(callSuper = true)
    class SmApiLogDto extends SmApiLog {
        private String userId;
    }
}
