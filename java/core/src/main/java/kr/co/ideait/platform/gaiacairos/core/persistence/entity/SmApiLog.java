package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import kr.co.ideait.platform.gaiacairos.core.config.GlobalMapperConfig;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;
import org.mapstruct.Mapper;

@Data
@EqualsAndHashCode(callSuper = true)
@Alias("smApiLog")
@Mapper(config = GlobalMapperConfig.class)
public class SmApiLog extends AbstractRudIdTime {

    @Description(name = "API 로그 No", description = "", type = Description.TYPE.FIELD)
    Long apiLogNo;

    @Description(name = "API ID", description = "", type = Description.TYPE.FIELD)
    String apiId;

    @Description(name = "API 구분", description = "", type = Description.TYPE.FIELD)
    String apiType;

    @Description(name = "SOURCE 시스템 코드", description = "", type = Description.TYPE.FIELD)
    String sourceSystemCode;

    @Description(name = "TARGET 시스템 코드", description = "", type = Description.TYPE.FIELD)
    String targetSystemCode;

    @Description(name = "서비스 구분 코드", description = "", type = Description.TYPE.FIELD)
    String serviceType;

    @Description(name = "서비스 UUID", description = "", type = Description.TYPE.FIELD)
    String serviceUuid;

    @Description(name = "메소드", description = "", type = Description.TYPE.FIELD)
    String reqMethod;

    @Description(name = "응답 코드", description = "", type = Description.TYPE.FIELD)
    Integer resultCode;

    @Description(name = "요청 헤더", description = "", type = Description.TYPE.FIELD)
    String reqHeader;

    @Description(name = "요청 데이터", description = "", type = Description.TYPE.FIELD)
    String reqData;

    @Description(name = "요청 일시", description = "", type = Description.TYPE.FIELD)
    String reqDt;

    @Description(name = "응답 헤더", description = "", type = Description.TYPE.FIELD)
    String resHeader;

    @Description(name = "응답 데이터", description = "", type = Description.TYPE.FIELD)
    String resData;

    @Description(name = "응답 일시", description = "", type = Description.TYPE.FIELD)
    String resDt;

    @Description(name = "에러 여부", description = "", type = Description.TYPE.FIELD)
    String errorYn;

    @Description(name = "에러 사유", description = "", type = Description.TYPE.FIELD)
    String errorReason;
}
