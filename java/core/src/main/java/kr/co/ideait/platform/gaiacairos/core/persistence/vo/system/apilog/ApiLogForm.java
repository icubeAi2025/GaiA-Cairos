package kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.apilog;

import kr.co.ideait.platform.gaiacairos.core.persistence.model.CommonForm;
import lombok.Data;
import lombok.ToString;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;

import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.apilog.ApiLogMybatisParam.ApiLogListInput;
@Mapper(componentModel = ComponentModel.SPRING)
public interface ApiLogForm {

    ApiLogListInput toApiLogListInput(ApiLogListGet apiLogListGet);

    /**
     * API 로그 항목 검색 폼
     */
    @Data
    @ToString
    class ApiLogListGet extends CommonForm {
        String apiSource;
        String target;
        String apiType;
        String apiId;
        String startDt;
        String endDt;
    }

}
