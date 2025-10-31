package kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.pjinstall;

import com.fasterxml.jackson.annotation.JsonFormat;
import kr.co.ideait.platform.gaiacairos.core.config.GlobalMapperConfig;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmProjectBilling;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MapDto;
import lombok.Data;
import lombok.Getter;
import org.apache.ibatis.type.Alias;
import org.mapstruct.Mapper;

import java.time.LocalDateTime;
import java.util.Map;

@Mapper(config = GlobalMapperConfig.class)
@Data
@Alias("pjInstallCriteria")
public class PjInstallCriteria {
    private String platform;
    private String workType;
    private String dminsttType;
    private String cntrctType;
    private String openPstats;
    private String pjtBgnDate;
    private String pjtEndDate;
    private String searchText;
    private boolean flag;
}
