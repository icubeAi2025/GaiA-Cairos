package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import kr.co.ideait.platform.gaiacairos.core.config.GlobalMapperConfig;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;
import org.mapstruct.Mapper;

@Data
@EqualsAndHashCode(callSuper = true)
@Alias("smUserLog")
@Mapper(config = GlobalMapperConfig.class)
public class SmUserLog extends AbstractRudIdTime {
    @Description(name = "로그_번호", description = "")
    Long logNo;

    @Description(name = "플랫폼", description = "")
    String platform;

    @Description(name = "프로젝트번호")
    private String pjtNo;

    @Description(name = "계약번호")
    private String cntrctNo;

    @Description(name = "거래ID", description = "")
    String trnId;

    @Description(name = "사용자 아이디", description = "")
    String userId;

    @Description(name = "사용자 이름", description = "")
    String userName;

    @Description(name = "사용자 IP", description = "")
    String userIp;

    @Description(name = "로그 유형", description = "")
    String logType;

    @Description(name = "수행 업무", description = "")
    String execType;

    @Description(name = "수행 결과", description = "")
    String result;

    @Description(name = "요청URL")
    private String reqUrl;

    @Description(name = "referer", description = "")
    String referer;

    @Description(name = "user agent", description = "")
    String userAgent;

    @Description(name = "요청 헤더", description = "")
    String reqHeader;

    @Description(name = "요청 데이터", description = "")
    String reqData;

    @Description(name = "요청 일시", description = "")
    String reqDt;

    @Description(name = "에러 사유", description = "")
    String errorReason;

    String corpNo;
}
