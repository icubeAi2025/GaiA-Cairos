package kr.co.ideait.platform.gaiacairos.core.components.log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import kr.co.ideait.iframework.event.listener.PlatformEventDto;
import kr.co.ideait.iframework.notify.NotifyMessage;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractComponent;
import kr.co.ideait.platform.gaiacairos.core.components.log.service.SystemLogService;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmApiLog;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.apilog.ApiLogMybatisParam;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.userlog.UserLogMybatisParam;
import kr.co.ideait.platform.gaiacairos.core.type.ActionResult;
import kr.co.ideait.platform.gaiacairos.core.type.PlatformType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

@Slf4j
@Component
public class SystemLogComponent extends AbstractComponent {
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private SystemLogService systemLogService;

    @Autowired
    private Log logDto;

    @Autowired
    private ObjectMapper objectMapper;

    public Page<UserLogMybatisParam.UserLogOutput> getUserLogList(UserLogMybatisParam.UserLogListInput logDto) {
        return systemLogService.getUserLogList(logDto);
    }

    public Log.SmUserLogDto getUserLog(Long logNo) {
        return objectMapper.convertValue(systemLogService.getUserLog(logNo), Log.SmUserLogDto.class);
    }

    @Deprecated
    public void addUserLog(Log.SmUserLogDto userLogDto) {
        log.info("addUserLog Deprecated");
    }

    @Async("taskExecutor")
    public void insertUserLog(Log.SmUserLogDto userLogDto) {
        if (userLogDto == null) {
            return;
        }

        if (userLogDto.getErrorReason() == null && userLogDto.getResult() == null) {
            userLogDto.setResult(ActionResult.SUCCESS.name());
        }

        systemLogService.addUserLog(logDto.toUserLogEntity(userLogDto));

        if (PlatformType.PGAIA.getName().equals(platform)) {
            Map<String, Object> params = Maps.newHashMap();
            params.put("userLogDto", userLogDto);

//            invokePgaia2Cairos("GACA9001", params);
        }
    }


    public Page<ApiLogMybatisParam.ApiLogOutput> getApiLogList(ApiLogMybatisParam.ApiLogListInput apiLogDto) {
        return systemLogService.getApiLogList(apiLogDto);
    }

    public Log.SmApiLogDto getApiLog(Long logNo) {
        return objectMapper.convertValue(systemLogService.getApiLog(logNo), Log.SmApiLogDto.class);
    }

    @Async("taskExecutor")
    public void asyncAddApiLog(Log.SmApiLogDto apiLogDto) {
        try {
            Long logNo = systemLogService.addApiLog(logDto.toApiLogEntity(apiLogDto));
            apiLogDto.setApiLogNo(logNo);
            apiLogDto.setRgstrId(URLDecoder.decode(apiLogDto.getRgstrId(), "UTF-8"));

            if (PlatformType.PGAIA.getName().equals(platform)) {
                Map<String, Object> params = Maps.newHashMap();
                params.put("apiLog", apiLogDto);

                invokePgaia2Cairos("GACA9002", params);
            }
        } catch (GaiaBizException | UnsupportedEncodingException e) {
            log.error("asyncAddApiLog() exception", e);
        }
    }

    @Async("taskExecutor")
    public void modifyApiLog(Log.SmApiLogDto apiLogDto) {
        systemLogService.modifyApiLog(logDto.toApiLogEntity(apiLogDto));
    }

    @Async("taskExecutor")
    public void addApiLog(Log.SmApiLogDto apiLogDto) {
        Long logNo = systemLogService.addApiLog(logDto.toApiLogEntity(apiLogDto));

        apiLogDto.setApiLogNo(logNo);

        Map resData = objectMapper.convertValue(apiLogDto.getResData(), Map.class);
        SmApiLog apiLog = this.getApiLog(apiLogDto.getApiLogNo());
        NotifyMessage notifyMessage = NotifyMessage.builder()
                .type("notice")
                .target(apiLog.getRgstrId())
                .message(
                    "N".equals(apiLogDto.getErrorYn())
                    ? String.format( "문서24 임시저장함에 저장되었습니다.<br/>문서번호 : %s", MapUtils.getString(resData, "gdoc_no") )
                    : apiLogDto.getErrorReason()
                )
                .build();

        applicationEventPublisher.publishEvent(
            PlatformEventDto
                .builder()
                .userId(apiLog.getRgstrId())
                .data(notifyMessage)
                .build()
        );

        if (PlatformType.PGAIA.getName().equals(platform)) {
            Map<String, Object> params = Maps.newHashMap();
            params.put("apiLog", apiLogDto);

            invokePgaia2Cairos("GACA9002", params);
        }
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Map receiveInterfaceService(String transactionId, Map params) {
        Map<String, Object> result = Maps.newHashMap();
        result.put("resultCode", "00");

        try {
            if ("GACA9001".equals(transactionId)) {
                Log.SmUserLogDto userLogDto = objectMapper.convertValue(params.get("userLogDto"), Log.SmUserLogDto.class);

                systemLogService.addUserLog(logDto.toUserLogEntity(userLogDto));
            }
            if ("GACA9002".equals(transactionId)) {
                Log.SmApiLogDto apiLogDto = objectMapper.convertValue(params.get("apiLogDto"), Log.SmApiLogDto.class);

                Long logNo = systemLogService.addApiLog(logDto.toApiLogEntity(apiLogDto));
                log.info(logNo.toString());
            }
        } catch (GaiaBizException e) {
            log.error(e.getMessage(), e);
            result.put("resultCode", "01");
            result.put("resultMsg", e.getMessage());
        }

        return result;
    }
}
