package kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.apilog;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:17+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class ApiLogDtoImpl implements ApiLogDto {

    @Override
    public ApiLog fromSmApiLogOutput(ApiLogMybatisParam.ApiLogOutput apiLogOutput) {
        if ( apiLogOutput == null ) {
            return null;
        }

        ApiLog apiLog = new ApiLog();

        apiLog.setApiLogNo( apiLogOutput.getApiLogNo() );
        apiLog.setSourceSystemCode( apiLogOutput.getSourceSystemCode() );
        apiLog.setTargetSystemCode( apiLogOutput.getTargetSystemCode() );
        apiLog.setApiType( apiLogOutput.getApiType() );
        apiLog.setServiceType( apiLogOutput.getServiceType() );
        apiLog.setApiId( apiLogOutput.getApiId() );
        apiLog.setResultCode( apiLogOutput.getResultCode() );
        apiLog.setRgstDt( apiLogOutput.getRgstDt() );

        return apiLog;
    }
}
