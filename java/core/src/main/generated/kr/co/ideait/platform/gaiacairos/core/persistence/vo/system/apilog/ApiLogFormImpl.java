package kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.apilog;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:18+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class ApiLogFormImpl implements ApiLogForm {

    @Override
    public ApiLogMybatisParam.ApiLogListInput toApiLogListInput(ApiLogListGet apiLogListGet) {
        if ( apiLogListGet == null ) {
            return null;
        }

        ApiLogMybatisParam.ApiLogListInput apiLogListInput = new ApiLogMybatisParam.ApiLogListInput();

        apiLogListInput.setPageable( apiLogListGet.getPageable() );
        apiLogListInput.setApiSource( apiLogListGet.getApiSource() );
        apiLogListInput.setTarget( apiLogListGet.getTarget() );
        apiLogListInput.setApiType( apiLogListGet.getApiType() );
        apiLogListInput.setApiId( apiLogListGet.getApiId() );
        apiLogListInput.setStartDt( apiLogListGet.getStartDt() );
        apiLogListInput.setEndDt( apiLogListGet.getEndDt() );

        return apiLogListInput;
    }
}
