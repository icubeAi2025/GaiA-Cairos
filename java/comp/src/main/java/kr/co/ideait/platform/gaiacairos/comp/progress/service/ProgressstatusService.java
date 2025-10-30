package kr.co.ideait.platform.gaiacairos.comp.progress.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import org.springframework.stereotype.Service;

@Service
public class ProgressstatusService extends AbstractGaiaCairosService {

    /**
     * 공정률 현황 목록(금액)
     */
    public List<Map<String, ?>> getProcessRate(String cntrctChgId, Integer weekType, String startDate, String endDate) {
    	
    	Map<String, Object> baseParams = new HashMap<>();
    	baseParams.put("weekType", weekType);
    	baseParams.put("startDate", startDate);
    	baseParams.put("endDate", endDate);
        
        Map<String, Object> baseResult = mybatisSession.selectOne( "kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.progressstatus.getProcessRateBaseData", baseParams);
    	
        String[] parts = cntrctChgId.split("\\.");
        String cntrctNo = parts.length >= 2 ? parts[0] + "." + parts[1] : cntrctChgId;
        
    	Map<String, Object> resultParam = new HashMap<>();
    	resultParam.put("cntrctChgId", cntrctChgId);
        resultParam.put("cntrctNo", cntrctNo);
        resultParam.put("thisStart", baseResult.get("this_start"));
        resultParam.put("thisEnd", baseResult.get("this_end"));
        resultParam.put("nextStart", baseResult.get("next_start"));
        resultParam.put("nextEnd", baseResult.get("next_end"));
        resultParam.put("prevStart", baseResult.get("prev_start"));
        resultParam.put("prevEnd", baseResult.get("prev_end"));
        resultParam.put("nextStart2", baseResult.get("next_start2"));
        return mybatisSession.selectList( "kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.progressstatus.newTypeGetProcessRate", resultParam);
    }

    /**
     * 주요작업 현황 목록
     */
    public List<Map<String, ?>> getActivityList(String cntrctChgId, Integer weekType, String startDate,
            String endDate) {
        Map<String, Object> params = new HashMap<>();
        params.put("cntrctChgId", cntrctChgId);
        params.put("weekType", weekType);
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        return mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.progressstatus.getActivityList",
                params);

    }
}
