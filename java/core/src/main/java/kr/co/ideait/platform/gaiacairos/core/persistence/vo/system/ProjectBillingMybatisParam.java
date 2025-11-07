package kr.co.ideait.platform.gaiacairos.core.persistence.vo.system;

import org.apache.ibatis.type.Alias;
import org.mapstruct.Mapper;

import kr.co.ideait.platform.gaiacairos.core.config.GlobalMapperConfig;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisPageable;
import lombok.Data;

@Mapper(config = GlobalMapperConfig.class)
public interface ProjectBillingMybatisParam {

    @Data
    @Alias("projectBillingListInput")
    public class ProjectBillingListInput extends MybatisPageable {
        String cntrctNo;
    }

}
