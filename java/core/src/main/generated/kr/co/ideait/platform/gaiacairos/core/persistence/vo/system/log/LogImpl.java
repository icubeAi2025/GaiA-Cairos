package kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log;

import javax.annotation.processing.Generated;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmApiLog;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmUserLog;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:17+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class LogImpl implements Log {

    @Override
    public SmUserLog toUserLogEntity(SmUserLogDto logDto) {
        if ( logDto == null ) {
            return null;
        }

        SmUserLog smUserLog = new SmUserLog();

        smUserLog.setRgstrId( logDto.getRgstrId() );
        smUserLog.setRgstDt( logDto.getRgstDt() );
        smUserLog.setChgId( logDto.getChgId() );
        smUserLog.setChgDt( logDto.getChgDt() );
        smUserLog.setDltId( logDto.getDltId() );
        smUserLog.setDltDt( logDto.getDltDt() );
        smUserLog.setLogNo( logDto.getLogNo() );
        smUserLog.setPlatform( logDto.getPlatform() );
        smUserLog.setPjtNo( logDto.getPjtNo() );
        smUserLog.setCntrctNo( logDto.getCntrctNo() );
        smUserLog.setTrnId( logDto.getTrnId() );
        smUserLog.setUserId( logDto.getUserId() );
        smUserLog.setUserName( logDto.getUserName() );
        smUserLog.setUserIp( logDto.getUserIp() );
        smUserLog.setLogType( logDto.getLogType() );
        smUserLog.setExecType( logDto.getExecType() );
        smUserLog.setResult( logDto.getResult() );
        smUserLog.setReqUrl( logDto.getReqUrl() );
        smUserLog.setReferer( logDto.getReferer() );
        smUserLog.setUserAgent( logDto.getUserAgent() );
        smUserLog.setReqHeader( logDto.getReqHeader() );
        smUserLog.setReqData( logDto.getReqData() );
        smUserLog.setReqDt( logDto.getReqDt() );
        smUserLog.setErrorReason( logDto.getErrorReason() );

        return smUserLog;
    }

    @Override
    public SmApiLog toApiLogEntity(SmApiLogDto apiLogForm) {
        if ( apiLogForm == null ) {
            return null;
        }

        SmApiLog smApiLog = new SmApiLog();

        smApiLog.setRgstrId( apiLogForm.getRgstrId() );
        smApiLog.setRgstDt( apiLogForm.getRgstDt() );
        smApiLog.setChgId( apiLogForm.getChgId() );
        smApiLog.setChgDt( apiLogForm.getChgDt() );
        smApiLog.setDltId( apiLogForm.getDltId() );
        smApiLog.setDltDt( apiLogForm.getDltDt() );
        smApiLog.setApiLogNo( apiLogForm.getApiLogNo() );
        smApiLog.setApiId( apiLogForm.getApiId() );
        smApiLog.setApiType( apiLogForm.getApiType() );
        smApiLog.setSourceSystemCode( apiLogForm.getSourceSystemCode() );
        smApiLog.setTargetSystemCode( apiLogForm.getTargetSystemCode() );
        smApiLog.setServiceType( apiLogForm.getServiceType() );
        smApiLog.setServiceUuid( apiLogForm.getServiceUuid() );
        smApiLog.setReqMethod( apiLogForm.getReqMethod() );
        smApiLog.setResultCode( apiLogForm.getResultCode() );
        smApiLog.setReqHeader( apiLogForm.getReqHeader() );
        smApiLog.setReqData( apiLogForm.getReqData() );
        smApiLog.setReqDt( apiLogForm.getReqDt() );
        smApiLog.setResHeader( apiLogForm.getResHeader() );
        smApiLog.setResData( apiLogForm.getResData() );
        smApiLog.setResDt( apiLogForm.getResDt() );
        smApiLog.setErrorYn( apiLogForm.getErrorYn() );
        smApiLog.setErrorReason( apiLogForm.getErrorReason() );

        return smApiLog;
    }
}
