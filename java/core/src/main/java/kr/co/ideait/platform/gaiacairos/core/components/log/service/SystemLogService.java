package kr.co.ideait.platform.gaiacairos.core.components.log.service;

import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmApiLog;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmUserLog;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.apilog.ApiLogMybatisParam.ApiLogListInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.apilog.ApiLogMybatisParam.ApiLogOutput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.userlog.UserLogMybatisParam.DetailUserLogOutput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.userlog.UserLogMybatisParam.UserLogListInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.userlog.UserLogMybatisParam.UserLogOutput;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SystemLogService extends AbstractGaiaCairosService {

    public Page<UserLogOutput> getUserLogList(UserLogListInput userLogListInput) {
        List<UserLogOutput> userLogOutputs = mybatisSession
                .selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.user_log.selectList", userLogListInput);
        Long totalCount = mybatisSession
                .selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.user_log.selectCount", userLogListInput);
        return new PageImpl<>(userLogOutputs, userLogListInput.getPageable(), totalCount);
    }

    public SmUserLog getUserLog(Long logNo) {
        return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.user_log.selectOne", logNo);
    }

    public void addUserLog(SmUserLog userLog) {
        mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.user_log.insert", userLog);
    }

    public Page<ApiLogOutput> getApiLogList(ApiLogListInput apiLogListInput) {
        List<ApiLogOutput> apiLogOutputs = mybatisSession
                .selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.api_log.selectList", apiLogListInput);
        Long totalCount = mybatisSession
                .selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.api_log.selectCount", apiLogListInput);
        return new PageImpl<>(apiLogOutputs, apiLogListInput.getPageable(), totalCount);
    }

    public SmApiLog getApiLog(Long apiLogNo) {
        return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.api_log.selectOne", apiLogNo);
    }

    public Long addApiLog(SmApiLog apiLog) {
        mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.api_log.insert", apiLog);

        return apiLog.getApiLogNo();
    }

    public void modifyApiLog(SmApiLog apiLog) {
        mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.api_log.update", apiLog);
    }
}
