package kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.projectbilling;

import com.fasterxml.jackson.annotation.JsonFormat;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmProjectBilling;
import kr.co.ideait.platform.gaiacairos.core.config.GlobalMapperConfig;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MapDto;
import lombok.Data;
import lombok.Getter;
import org.mapstruct.Mapper;

import java.time.LocalDateTime;
import java.util.Map;

@Mapper(config = GlobalMapperConfig.class)
public interface ProjectBillingDto {
    
    ProjectBilling fromSmProjectBillingMybatis(Map<String, ?> map);

    SetProjectBilling fromSmProjectBilling(SmProjectBilling smPjtBill);

    @Getter
    class ProjectBilling extends MapDto {
        Integer isBil;
        Integer isOldBil;
        Integer pjtBilNo;
        Integer bilNo;
        Integer menuNo;
        String menuNm;
        String menuCd;
        String bilCode;
        String bilNm;
        String bilDscrpt;
    }

    @Data
    class SetProjectBilling {
        Integer pjtBilNo;
        Integer bilNo;
        Integer menuNo;
        String menuCd;
        String bilCode;
        String pjtNo;
        String cntrctNo;
        String pjtType;
        String dltYn;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd", timezone = "Asia/Seoul")
        LocalDateTime rgstDt;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd", timezone = "Asia/Seoul")
        LocalDateTime chgDt;
    }
}
