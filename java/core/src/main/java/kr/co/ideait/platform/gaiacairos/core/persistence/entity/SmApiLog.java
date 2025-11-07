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

    @Description(name = "API 로그 No", description = "")
    Long apiLogNo;

    @Description(name = "API ID", description = "")
    String apiId;

    @Description(name = "API 구분", description = "")
    String apiType;

    @Description(name = "SOURCE 시스템 코드", description = "")
    String sourceSystemCode;

    @Description(name = "TARGET 시스템 코드", description = "")
    String targetSystemCode;

    @Description(name = "서비스 구분 코드", description = "")
    String serviceType;

    @Description(name = "서비스 UUID", description = "")
    String serviceUuid;

    @Description(name = "재실행 거래 아이디", description = "")
    String invokeTranId;

    @Description(name = "메소드", description = "")
    String reqMethod;

    @Description(name = "응답 코드", description = "")
    Integer resultCode;

    @Description(name = "요청 헤더", description = "")
    String reqHeader;

    @Description(name = "요청 데이터", description = "")
    String reqData;

    @Description(name = "요청 일시", description = "")
    String reqDt;

    @Description(name = "응답 헤더", description = "")
    String resHeader;

    @Description(name = "응답 데이터", description = "")
    String resData;

    @Description(name = "응답 일시", description = "")
    String resDt;

    @Description(name = "에러 여부", description = "")
    String errorYn;

    @Description(name = "에러 사유", description = "")
    String errorReason;
}
