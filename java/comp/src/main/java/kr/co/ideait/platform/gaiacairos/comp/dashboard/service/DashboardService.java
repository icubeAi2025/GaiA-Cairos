package kr.co.ideait.platform.gaiacairos.comp.dashboard.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.ideait.platform.gaiacairos.core.util.CoordinateConverter;
import kr.co.ideait.platform.gaiacairos.comp.document.service.DocumentService;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;

import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DcStorageMain;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.dashboard.PortalWeather;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.ideait.platform.gaiacairos.core.persistence.vo.dashboard.DashboardMybatisParam.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

@Slf4j
@Service
public class DashboardService extends AbstractGaiaCairosService {

	@Autowired
	DocumentService documentService;

	// 메인대시보드
	public TodayOutput getToday(MainInput mainInput) {

		TodayOutput todayOutput = mybatisSession
				.selectOne("kr.co.ideait.platform.gaiacairos.mappers.dashboard.getToday", mainInput);

		return todayOutput;
	}

	public ResourceOutput getResource(MainInput mainInput) {

		ResourceOutput resourceOutput = mybatisSession
				.selectOne("kr.co.ideait.platform.gaiacairos.mappers.dashboard.getResource", mainInput);

		return resourceOutput;
	}

	public WorkOutput getWork(MainInput mainInput) {

		WorkOutput workOutput = mybatisSession
				.selectOne("kr.co.ideait.platform.gaiacairos.mappers.dashboard.getWork", mainInput);

		return workOutput;
	}

	public PhotoOutput getProjectPhoto(MainInput mainInput) {

		PhotoOutput projectPhotoOutput = mybatisSession
				.selectOne("kr.co.ideait.platform.gaiacairos.mappers.dashboard.getProjectPhoto", mainInput);

		return projectPhotoOutput;
	}

	public EscOutput getProjectEsc(MainInput mainInput) {

		EscOutput projectEscOutput = mybatisSession
				.selectOne("kr.co.ideait.platform.gaiacairos.mappers.dashboard.getProjectEsc", mainInput);

		return projectEscOutput;
	}

	public List<PhotoOutput> getPhotoList(MainInput mainInput) {

		List<PhotoOutput> projectPhotoOutputs = mybatisSession
				.selectList("kr.co.ideait.platform.gaiacairos.mappers.dashboard.getPhotoList", mainInput);

		return projectPhotoOutputs;
	}

	public List<MainActivityOutput> getActivityList(MainInput mainInput) {

		List<MainActivityOutput> activityOutputs = mybatisSession
				.selectList("kr.co.ideait.platform.gaiacairos.mappers.dashboard.getActivityList", mainInput);

		return activityOutputs;
	}

	public AuditOutput getAudit(MainInput mainInput) {

		AuditOutput auditOutput = mybatisSession
				.selectOne("kr.co.ideait.platform.gaiacairos.mappers.dashboard.getAudit", mainInput);

		return auditOutput;
	}

	public Map<String,Object> getEcoScore(MainInput mainInput) {

		Map<String, Object> eco = new HashMap<>();
		DcStorageMain greenLevelDoc = null;
		DcStorageMain energyEffectLevelDoc = null;
		DcStorageMain zeroEnergyLevelDoc = null;

		EcoScoreOutput ecoScoreOutput = mybatisSession
				.selectOne("kr.co.ideait.platform.gaiacairos.mappers.dashboard.getEcoScore", mainInput);

		if (ecoScoreOutput != null) {
			greenLevelDoc = documentService.getDcStorageMain(ecoScoreOutput.getGreenLevelDocId());
			energyEffectLevelDoc = documentService.getDcStorageMain(ecoScoreOutput.getEnergyEffectLevelDocId());
			zeroEnergyLevelDoc = documentService.getDcStorageMain(ecoScoreOutput.getZeroEnergyLevelDocId());
		}

		eco.put("ecoScore", ecoScoreOutput);
		eco.put("greenLevelDoc", greenLevelDoc);
		eco.put("energyEffectLevelDoc", energyEffectLevelDoc);
		eco.put("zeroEnergyLevelDoc", zeroEnergyLevelDoc);

		return eco;
	}

	public List<GovsplyMtrlListOutput> getGovsplyMtrlList(MainInput mainInput) {

		List<GovsplyMtrlListOutput> govsplyMtrlListOutputs = mybatisSession
				.selectList("kr.co.ideait.platform.gaiacairos.mappers.dashboard.getGovsplyMtrlList", mainInput);

		return govsplyMtrlListOutputs;
	}

	public ProcessOutput getProcess(MainInput mainInput) {

		ProcessOutput processOutput = mybatisSession
				.selectOne("kr.co.ideait.platform.gaiacairos.mappers.dashboard.getProcess", mainInput);

		return processOutput;
	}





	// 종합대시보드
	public List getComprehensiveDashBoard() {

		return mybatisSession.selectList(
				"kr.co.ideait.platform.gaiacairos.mappers.dashboard.getComprehensiveDashBoard");

	}

	public List<RegionOutput> regionList(MybatisInput input) {


		List<RegionOutput> regionOutputs = mybatisSession
				.selectList("kr.co.ideait.platform.gaiacairos.mappers.dashboard.getRegionList", input);

		return regionOutputs;
	}

	public List<RegionProjectOutput> projectListGet(MybatisInput input) {
		List<RegionProjectOutput> regionProjectOutputs = mybatisSession
				.selectList("kr.co.ideait.platform.gaiacairos.mappers.dashboard.getRegionProjectList",
						input);

		return regionProjectOutputs;
	}

	public List<RankProjectOutput> rankProjectListGet(MybatisInput input) {
		List<RankProjectOutput> rankProjectOutputs = mybatisSession
				.selectList("kr.co.ideait.platform.gaiacairos.mappers.dashboard.getRankProjectList", input);

		return rankProjectOutputs;
	}

	public List<BigDataOutput> bigDataGet(MybatisInput input) {
		List<BigDataOutput> bigDataOutputs = mybatisSession
				.selectList("kr.co.ideait.platform.gaiacairos.mappers.dashboard.getBigData", input);

		return bigDataOutputs;
	}

	public List<BigDataPhotoOutput> bigDataPhotoListGet(MybatisInput input) {
		List<BigDataPhotoOutput> bigDataPhotoOutputs = mybatisSession
				.selectList("kr.co.ideait.platform.gaiacairos.mappers.dashboard.getBigDataPhotoList", input);

		return bigDataPhotoOutputs;
	}

	public HashMap<String,Object> getProjectAddress(String pjtNo) {
        return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mappers.dashboard.getProjectAddress", pjtNo);
	}
	
	/**
     * 계약별 친환경 자제 목록 가져오기
     * @param String (cntrctNo)
     * @return List<Map<String, ?>>
     * @throws 
     */
    public List<Map<String, ?>> selectEcoFriendlyList(MybatisInput input) {
    	
    	return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mappers.dashboard.selectEcoFriendlyList", input);
    }
    
    /**
     * 프로젝트의 해당 계약리스트 가져오기
     * @param String (pjtNo)
     * @return List<Map<String, ?>>
     * @throws 
     */
    public List<Map<String, ?>> selectContractList(String pjtNo) {
    	
    	return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mappers.dashboard.selectContractList", pjtNo);
    }
}
