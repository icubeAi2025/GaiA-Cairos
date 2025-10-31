package kr.co.ideait.platform.gaiacairos.comp.progress.service;

import kr.co.ideait.iframework.BizException;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.ApDoc;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.PrWeeklyReport;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.PrWeeklyReportActivity;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.PrWeeklyReportProgress;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.PrWeeklyReportActivityRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.PrWeeklyReportProgressRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.PrWeeklyReportRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.progress.weeklyreport.WeeklyreportMybatisParam.DeleteWeeklyActivityList;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.progress.weeklyreport.WeeklyreportMybatisParam.UpdateWeeklyActivityInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.progress.weeklyreport.WeeklyreportMybatisParam.UpdateWeeklyProgress;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.progress.weeklyreport.WeeklyreportMybatisParam.WeeklyBohalRate;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeeklyreportService extends AbstractGaiaCairosService{

	@Autowired
	PrWeeklyReportRepository prWeeklyReportRepository;

	@Autowired
	PrWeeklyReportActivityRepository prWeeklyReportActivityRepository;

	@Autowired
	PrWeeklyReportProgressRepository prWeeklyReportProgressRepository;


	/**
	 * 주간보고 목록 조회
	 * @param input
	 * @return
	 */
	public List selectWeeklyreportList(MybatisInput input) {
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.weeklyreport.selectWeeklyreportList", input);
	}


	/**
	 * 주간공정보고 상세 조회
	 * @param input
	 * @return
	 */
	public PrWeeklyReport selectWeeklyreportDetail(MybatisInput input) {
		return prWeeklyReportRepository.findByCntrctChgIdAndWeeklyReportId((String)input.get("cntrctChgId"), (Long)input.get("weeklyReportId")).orElse(null);
	}


	/**
	 * 공정현황 목록 조회
	 * @param input
	 * @return
	 */
	public List selectWeeklyProgress(MybatisInput input) {
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.weeklyreport.selectWeeklyProgress", input);
	}


	/**
	 * 금주 주요작업 Activity 조회
	 * @param input
	 * @return
	 */
	public List selectWeeklyMajorActivityList(MybatisInput input) {
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.weeklyreport.selectWeeklyMajorActivityList", input);
	}


	/**
	 * 금주 지연 Activity 조회
	 * @param input
	 * @return
	 */
	public List selectWeeklyDelayActivityList(MybatisInput input) {
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.weeklyreport.selectWeeklyDelayActivityList", input);
	}


	/**
	 * 차주 주요 Activity 조회
	 * @param input
	 * @return
	 */
	public List selectNextWeekActivityList(MybatisInput input) {
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.weeklyreport.selectNextWeekActivityList", input);
	}


	/**
	 * 금주 주요작업 Activity 추가 리스트 조회
	 * @param input
	 * @return
	 */
	public List selectWeeklyAddMajorActivityList(MybatisInput input) {
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.weeklyreport.selectWeeklyAddMajorActivityList", input);
	}


	/**
	 * 금주 지연 Activity 추가 리스트 조회
	 * @param input
	 * @return
	 */
	public List selectWeeklyAddDelayActivityList(MybatisInput input) {
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.weeklyreport.selectWeeklyAddDelayActivityList", input);
	}


	/**
	 * 차주 주요 예정 Activity 추가 리스트 조회
	 * @param input
	 * @return
	 */
	public List selectAddNextWeekActivityList(MybatisInput input) {
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.weeklyreport.selectAddNextWeekActivityList", input);
	}


	/**
	 * 최종계약번호 조회
	 * @param cntrctNo
	 * @return
	 */
	public String selectLastCntrctChgId(String cntrctNo) {
		return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.monthlyreport.selectLastCntrctChgId", cntrctNo);
	}


	/**
	 * 주간보고 중복검사
	 * @param cntrctNo
	 * @param reportDate
	 * @return
	 */
	public int checkWeeklyReport(String cntrctNo, String reportDate) {
		return prWeeklyReportRepository.existsByCntrctNoAndReportDate(cntrctNo, reportDate);
	}


	/**
	 * 주간보고 업데이트 BY apDocId
	 * @param apDocId
	 * @param apUsrId
	 * @param apDocStats
	 */
	public void updateWeeklyreportByApDocId(String apDocId, String apUsrId, String apDocStats) {
		PrWeeklyReport prWeeklyReport = prWeeklyReportRepository.findByApDocId(apDocId).orElse(null);

		if(prWeeklyReport == null) {
			throw new BizException("ApDocId와 일치하는 보고서가 없습니다 : " + apDocId);
		}

		String apStats = "C".equals(apDocStats) ? "A" : "R";
		updateApprovalStatus(prWeeklyReport, apUsrId, apStats);
	}

	/**
	 * 주간보고 데이터 조회
	 * @param apDocId
	 * @return
	 */
	public Map<String, Object> selectWeeklyreportByApDocId(String apDocId) {
		Map<String, Object> returnMap = new HashMap<>();
		PrWeeklyReport prWeeklyReport = prWeeklyReportRepository.findByApDocId(apDocId).orElse(null);
		if(prWeeklyReport != null) {
			returnMap.put("report", prWeeklyReport);
			returnMap.put("resources", selectWeeklyReportResource(prWeeklyReport.getCntrctChgId(), prWeeklyReport.getWeeklyReportId()));
		}
		return returnMap;
	}

	/**
	 * 주간보고 추가 - 보고서 & Activity & Progress
	 * @param cntrctNo
	 * @param prWeeklyReport
	 * @return
	 */
	@Transactional
	public PrWeeklyReport insertWeeklyreport(String cntrctNo, PrWeeklyReport prWeeklyReport) {
		String cntrctChgId = selectLastCntrctChgId(cntrctNo);
		Long maxId = prWeeklyReportRepository.findMaxWeeklyReportIdByCntrctNo(cntrctNo);
		prWeeklyReport.setCntrctChgId(cntrctChgId);
		prWeeklyReport.setWeeklyReportId(maxId+1);
		prWeeklyReport.setDltYn("N");

		// 주간보고서 저장
		PrWeeklyReport savedReport = prWeeklyReportRepository.saveAndFlush(prWeeklyReport);

		// Activity 및 Progress 저장
		insertWeeklyActivityAndProgress(cntrctNo, savedReport.getWeeklyReportId(), cntrctChgId, savedReport.getReportDate());

		// 보할 계산
		MybatisInput bohalInput = MybatisInput.of().add("cntrctChgId", cntrctChgId)
				.add("weeklyReportId", savedReport.getWeeklyReportId());

		WeeklyBohalRate bohal = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.weeklyreport.selectSumWeeklyBohalRate", bohalInput);
		savedReport.setAcmltPlanBohalRate(bohal.getAcmltPlanBohalRate());
		savedReport.setAcmltArsltBohalRate(bohal.getAcmltArsltBohalRate());
		savedReport.setThismthPlanBohalRate(bohal.getThismthPlanBohalRate());
		savedReport.setThismthArsltBohalRate(bohal.getThismthArsltBohalRate());
		savedReport.setLsmthPlanBohalRate(bohal.getLsmthPlanBohalRate());
		savedReport.setLsmthArsltBohalRate(bohal.getLsmthArsltBohalRate());

		return savedReport;
	}


	/**
	 * 주간보고 Activity 추가
	 * @param cntrctNo
	 * @param weeklyReportId
	 * @param cntrctChgId
	 * @param reportDate
	 */
	private void insertWeeklyActivityAndProgress(String cntrctNo, Long weeklyReportId, String cntrctChgId, String reportDate) {
		MybatisInput input = MybatisInput.of().add("cntrctNo", cntrctNo)
				.add("weeklyReportId", weeklyReportId)
				.add("cntrctChgId", cntrctChgId)
				.add("reportDate", reportDate)
				.add("rgstrId", UserAuth.get(true).getUsrId());

		mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.weeklyreport.insertThisWeekActivity", input);
		mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.weeklyreport.insertThisWeekDelayActivity", input);
		mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.weeklyreport.insertNextWeekActivity", input);
		mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.weeklyreport.insertWeeklyProgress", input);
	}


	/**
	 * 주간 액티비티 & 공종 삭제
	 * @param cntrctChgId
	 * @param weeklyReportId
	 */
	public void deleteWeeklyActivityAndProgress(String cntrctChgId, Long weeklyReportId, String usrId) {
		MybatisInput input = MybatisInput.of().add("cntrctChgId", cntrctChgId)
				.add("weeklyReportId", weeklyReportId)
				.add("usrId", usrId);

		mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.weeklyreport.updateWeeklyProgressAll", input);
		mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.weeklyreport.updateWeeklyActivityAll", input);

	}


	/**
	 * 주간보고 Activity 업데이트(삭제, 추가)
	 * @param updateWeeklyActivityInput
	 */
	@Transactional
	public void updateActivity(UpdateWeeklyActivityInput updateWeeklyActivityInput) {
		List<PrWeeklyReportActivity> addList = updateWeeklyActivityInput.getAddActivityList();
		List<DeleteWeeklyActivityList> delList = updateWeeklyActivityInput.getDelActivityList();
		List<PrWeeklyReportActivity> updateList = updateWeeklyActivityInput.getUpdateActivityList();

		if(!addList.isEmpty()) {
			insertWeeklyActivity(addList);
		}

		if(!delList.isEmpty()) {
			deleteWeeklyActivity(delList);
		}

		if(!updateList.isEmpty()) {
			updateWeeklyActivityList(updateList, updateWeeklyActivityInput.getModalType());
		}
	}


	/**
	 * 액티비티 추가
	 * @param addList
	 */
	private void insertWeeklyActivity(List<PrWeeklyReportActivity> addList) {
		addList.forEach(activity -> {
			activity.setRgstrId(UserAuth.get(true).getUsrId());
		});

		mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.weeklyreport.insertWeeklyActivity", addList);
	}


	/**
	 * 액티비티 삭제
	 * @param delList
	 */
	private void deleteWeeklyActivity(List<DeleteWeeklyActivityList> delList) {
		delList.forEach(activity -> {
			activity.setChgId(UserAuth.get(true).getUsrId());
			activity.setDltId(UserAuth.get(true).getUsrId());
		});

		mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.weeklyreport.updateWeeklyActivity", delList);
	}


	/**
	 * 액티비티 수정
	 * @param updateList
	 * @param modalType
	 */
	private void updateWeeklyActivityList(List<PrWeeklyReportActivity> updateList, String modalType) {
		updateList.forEach(activity -> {
			activity.setChgId(UserAuth.get(true).getUsrId());
		});

		if(modalType.equals("d")) {
			mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.weeklyreport.updateWeeklyDelayActivity", updateList);
		} else {
			mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.weeklyreport.updateWeeklyNextActivity", updateList);
		}
	}


	/**
	 * 주간보고 업데이트
	 * @param prWeeklyReport
	 * @param progressList
	 * @return
	 */
	@Transactional
	public PrWeeklyReport updateWeeklyreport(PrWeeklyReport prWeeklyReport, List<UpdateWeeklyProgress> progressList) {
		PrWeeklyReport findReport = prWeeklyReportRepository.findByCntrctChgIdAndWeeklyReportId(prWeeklyReport.getCntrctChgId(), prWeeklyReport.getWeeklyReportId()).orElse(null);

		if(findReport == null) {
			throw new BizException("수정할 보고서가 없습니다 : ");
		}

		if(!progressList.isEmpty()) {
			progressList.forEach(p -> {
				PrWeeklyReportProgress progress = prWeeklyReportProgressRepository.findByWeeklyCnsttyId(p.getWeeklyCnsttyId());
				progress.setRmk(p.getRmk());
				prWeeklyReportProgressRepository.save(progress);
			});
		}

		// 주간보고 정보 업데이트
		boolean hasUpdate = false;

		if(prWeeklyReport.getRmrkCntnts() != null) {
			findReport.setRmrkCntnts(prWeeklyReport.getRmrkCntnts());
			hasUpdate = true;
		}
		if(prWeeklyReport.getWeeklyReportDate() != null) {
			findReport.setWeeklyReportDate(prWeeklyReport.getWeeklyReportDate());
			hasUpdate = true;
		}
		if(prWeeklyReport.getTitle() != null) {
			findReport.setTitle(prWeeklyReport.getTitle());
			hasUpdate = true;
		}
		if(prWeeklyReport.getThisWeekStartDay() != null) {
			findReport.setThisWeekStartDay(prWeeklyReport.getThisWeekStartDay());
			hasUpdate = true;
		}
		if(prWeeklyReport.getThisWeekEndDay() != null) {
			findReport.setThisWeekEndDay(prWeeklyReport.getThisWeekEndDay());
			hasUpdate = true;
		}
		if(prWeeklyReport.getNextWeekStartDay() != null) {
			findReport.setNextWeekStartDay(prWeeklyReport.getNextWeekStartDay());
			hasUpdate = true;
		}
		if(prWeeklyReport.getNextWeekEndDay() != null) {
			findReport.setNextWeekEndDay(prWeeklyReport.getNextWeekEndDay());
			hasUpdate = true;
		}
		if(prWeeklyReport.getThisWeekPromotion() != null) {
			findReport.setThisWeekPromotion(prWeeklyReport.getThisWeekPromotion());
			hasUpdate = true;
		}
		if(prWeeklyReport.getNextWeekPlan() != null) {
			findReport.setNextWeekPlan(prWeeklyReport.getNextWeekPlan());
			hasUpdate = true;
		}

		if(hasUpdate) {
			prWeeklyReportRepository.save(findReport);
		}

		return findReport;

	}


	/**
	 * 주간보고 승인상태 변경
	 * @param report
	 * @param usrId
	 * @param apprvlStats
	 */
	public void updateApprovalStatus(PrWeeklyReport report, String usrId, String apprvlStats) {
		report.setApprvlStats(apprvlStats);

		if("E".equals(apprvlStats)) {
			report.setApprvlReqId(usrId);
			report.setApprvlReqDt(LocalDateTime.now());
		} else {
			report.setApprvlId(usrId);
			report.setApprvlDt(LocalDateTime.now());
		}

		prWeeklyReportRepository.save(report);
	}


	/**
	 * 주간보고 API통신 -> Activity 및 Progress 저장
	 * @param activityList
	 * @param progressList
	 */
	public void insertResourcesToApi(PrWeeklyReport prWeeklyReport, List<PrWeeklyReportActivity> activityList, List<PrWeeklyReportProgress> progressList) {
		prWeeklyReportRepository.save(prWeeklyReport);

		if(!activityList.isEmpty()) {
			prWeeklyReportActivityRepository.saveAll(activityList);
		}

		if(!progressList.isEmpty()) {
			prWeeklyReportProgressRepository.saveAll(progressList);
		}
	}


	/**
	 * API 통신 시 연계데이터 조회
	 * @param cntrctChgId
	 * @param weeklyReportId
	 * @return
	 */
	public Map<String, Object> selectWeeklyReportResource(String cntrctChgId, Long weeklyReportId) {
		List<PrWeeklyReportActivity> activityList = prWeeklyReportActivityRepository.findByCntrctChgIdAndWeeklyReportIdAndDltYn(cntrctChgId, weeklyReportId, "N");
		List<PrWeeklyReportProgress> progressList = prWeeklyReportProgressRepository.findByCntrctChgIdAndWeeklyReportIdAndDltYn(cntrctChgId, weeklyReportId, "N");
		Map<String, Object> resourceMap = new HashMap<>();
		resourceMap.put("activityList", activityList);
		resourceMap.put("progressList", progressList);

		return resourceMap;
	}


	/**
	 * 결재요청 삭제 -> 주간보고 컬럼 값 삭제 or 데이터 삭제
	 * @param apDocList
	 * @param usrId
	 * @param toApi
	 */
	public void updateWeeklyreportApprovalReqCancel(List<ApDoc> apDocList, String usrId, boolean toApi) {
		apDocList.forEach(apDoc -> {
			PrWeeklyReport prWeeklyReport = prWeeklyReportRepository.findByApDocId(apDoc.getApDocId()).orElse(null);

			if(prWeeklyReport == null) return;

			if(toApi) {
				// api 통신 true -> 데이터 삭제
				deleteWeeklyreportDataToApi(prWeeklyReport);
			} else {
				// api 통신 false -> 컬럼 값 변경
				prWeeklyReportRepository.updateApprovalStausCancel(null, null, null, null, prWeeklyReport.getCntrctChgId(), prWeeklyReport.getWeeklyReportId(), usrId);
			}
		});
	}


	/**
	 * 결재요청 삭제 -> 주간보고 데이터 삭제
	 * @param prWeeklyReport
	 */
	private void deleteWeeklyreportDataToApi(PrWeeklyReport prWeeklyReport) {
		mybatisSession.delete("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.weeklyreport.deleteWeeklyActivityToApi", prWeeklyReport);
		mybatisSession.delete("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.weeklyreport.deleteWeeklyProgressToApi", prWeeklyReport);
		mybatisSession.delete("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.weeklyreport.deleteWeeklyreportToApi", prWeeklyReport);
	}


}
