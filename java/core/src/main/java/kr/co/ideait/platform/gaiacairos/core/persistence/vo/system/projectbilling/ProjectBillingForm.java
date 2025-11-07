package kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.projectbilling;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmProjectBilling;
import kr.co.ideait.platform.gaiacairos.core.config.GlobalMapperConfig;
import lombok.Data;
import org.mapstruct.Mapper;

@Mapper(config = GlobalMapperConfig.class)
public interface ProjectBillingForm {

    SmProjectBilling toSmProjectBilling (ProjectBilling bill);

    @Data
    class ProjectBilling {
        Integer bilNo;
        Integer menuNo;
        String menuCd;
        String bilCode;
        String pjtNo;
        String cntrctNo;
        String pjtType;
    }

}
