package kr.co.ideait.platform.gaiacairos.core.persistence.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import kr.co.ideait.platform.gaiacairos.core.persistence.Device;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnProject;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.iframework.annotation.Description;
import lombok.*;

@Data
@ToString
@EqualsAndHashCode
public class CommonReqVo {
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Log.SmUserLogDto userLogDto;

    public CommonReqVo() {
        userLogDto =  new Log.SmUserLogDto();
    }

    @Description(name = "플랫폼")
    private String platform;

    @Description(name = "서버 모드", description = "local, dev, staging, prod")
    private String envMode;

    @Description(name = "거래ID")
    private String trnId;

    @Description(name = "사용자ID")
    private String userId;

    @JsonIgnore
    @Description(name = "로그인ID")
    private String loginId;

    @Description(name = "사용자명")
    private String userName;

    @Description(name = "사용자IP")
    private String userIp;

    @Description(name = "로그인 여부")
    private Boolean admin;

    @Description(name = "요청URL")
    private String reqUrl;

    @Description(name = "요청일시")
    private String reqDt;

    @Description(name = "referer")
    private String referer;

    @Description(name = "user agent")
    private String userAgent;

    @Description(name = "요청 헤더정보")
    private String reqHeader;

    @Description(name = "요청 데이터")
    private String reqData;

    @Description(name = "로그 유형", description = "")
    String logType;

    @Description(name = "수행 업무", description = "")
    String execType;

    @JsonIgnore
    @Description(name = "서버 모드", description = "local: 로컬, dev: 개발 서버, stg: 스테이징 서버, prod: 운영 서버")
    private String srvMode;

    @Description(name = "프로젝트")
    private CnProject project;

    @Description(name = "프로젝트번호")
    private String pjtNo;

    @Description(name = "프로젝트 구분", description = "G: 민간 GAIA, P: 공공 GAIA")
    private String pjtDiv;

    @Description(name = "계약번호")
    private String cntrctNo;

    @JsonIgnore
    @Description(name = "사용자 인증 정보")
    private UserAuth userAuth;
    
    @Description(name = "내부 API 사용여부")
    private String apiYn;

    @JsonIgnore
    @Description(name = "사용자 정보")
    private String[] userParam;

    @JsonIgnore
    @Description(name = "프로젝트 정보")
    private String[] pjtParam;

    @Description(name = "디바이스", description = "")
    private Device device;

    public Log.SmUserLogDto toSmUserLogDto() {
        userLogDto.setPlatform(this.platform);
        userLogDto.setTrnId(this.trnId);
        userLogDto.setUserId(this.userId);
        userLogDto.setUserName(this.userName);
        userLogDto.setUserIp(this.userIp);
        userLogDto.setReqUrl(this.reqUrl);
        userLogDto.setReferer(this.referer);
        userLogDto.setUserAgent(this.userAgent);
        userLogDto.setReqHeader(this.reqHeader);
        userLogDto.setReqData(this.reqData);
        userLogDto.setReqDt(this.reqDt);
        userLogDto.setLogType(this.logType);
        userLogDto.setExecType(this.execType);

        return userLogDto;
    }
}
