package kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.apilog;

import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.apilog.ApiLogMybatisParam.ApiLogOutput;
import lombok.Data;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;

@Mapper(componentModel = ComponentModel.SPRING)
public interface ApiLogDto {

    ApiLog fromSmApiLogOutput(ApiLogOutput apiLogOutput);

    @Data
    class ApiLog{
        Long apiLogNo;
        String sourceSystemCode;
        String targetSystemCode;
        String apiType;
        String serviceType;
        String apiId;
        String resultCode;
        String rgstDt;
    }
}