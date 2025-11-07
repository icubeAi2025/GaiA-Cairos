package kr.co.ideait.platform.gaiacairos.comp.progress.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import org.springframework.stereotype.Service;

import kr.co.ideait.platform.gaiacairos.core.persistence.vo.progress.ActualtoplanMybatisParam.ActivityGraphOutPut;

@Service
public class ActualtoplanService extends AbstractGaiaCairosService {

    /**
     * 계획대비실적 그래프 목록
     */
    public List<ActivityGraphOutPut> getActivityList(String cntrctChgId) {
        Map<String, Object> params = new HashMap<>();
        params.put("cntrctChgId", cntrctChgId);
        return mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.actualtoplan.getActivityGraphList",
                params);

    }

    /**
     * 계획대비실적 그래프 연도
     */
    public Map<String, ?> getYear(String cntrctChgId) {
        Map<String, Object> params = new HashMap<>();
        params.put("cntrctChgId", cntrctChgId);
        return mybatisSession.selectOne(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.actualtoplan.getGraphYear",
                params);

    }

    /**
     * 계획대비실적 그래프, 일정 그리드 데이터
     */
    public List<Map<String, ?>> getData(Map<String, String> params) {

        params.put("cntrctChgId",  params.get("cntrctChgId"));
        params.put("cntrctNo",  params.get("cntrctNo"));

        if ("quarter".equals(params.get("viewType"))) {

            return mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.actualtoplan.getQuarterData", params);

        }else if ("month".equals(params.get("viewType"))) {

            return mybatisSession.selectList(
            "kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.actualtoplan.getMonthData", params);

        }

        return List.of();
    }
}
