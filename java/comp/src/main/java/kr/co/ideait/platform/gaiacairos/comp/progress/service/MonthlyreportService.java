package kr.co.ideait.platform.gaiacairos.comp.progress.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import kr.co.ideait.iframework.BizException;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import kr.co.ideait.platform.gaiacairos.core.constant.CommonCodeConstants;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.*;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.PrMonthlyReportActivityRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.PrMonthlyReportProgressRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.PrMonthlyReportRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.progress.monthlyreport.MonthlyreportForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.progress.monthlyreport.MonthlyreportMybatisParam.*;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.type.FileUploadType;
import kr.co.ideait.platform.gaiacairos.core.util.FileService;
import kr.co.ideait.platform.gaiacairos.core.util.restclient.RestClientUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MonthlyreportService extends AbstractGaiaCairosService {
	
	private final RestClientUtil restClientUtil;

	@Autowired
	FileService fileService;
	
	@Autowired
	PrMonthlyReportRepository prMonthlyReportRepository;

	@Autowired
	PrMonthlyReportActivityRepository prMonthlyReportActivityRepository;
	
	@Autowired
	PrMonthlyReportProgressRepository prMonthlyReportProgressRepository;


	/**
	 * 월간보고 목록 조회
	 * @param monthlyreportListInput
	 * @return
	 */
	public List selectMonthlyreportList(MonthlyreportListInput monthlyreportListInput) {
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.monthlyreport.selectMonthlyreportList", monthlyreportListInput);
	}



	/**
	 * 월간공정보고 상세 조회
	 * @param monthlyreportActivityDetailInput
	 * @return
	 */
	public PrMonthlyReport selectMonthlyreport(MonthlyreportActivityDetailInput monthlyreportActivityDetailInput) {
		return prMonthlyReportRepository.findByCntrctChgIdAndMonthlyReportId(monthlyreportActivityDetailInput.getCntrctChgId(), monthlyreportActivityDetailInput.getMonthlyReportId()).orElse(null);
	}


	/**
	 * 공정현황 목록 조회
	 * @param monthlyreportActivityDetailInput
	 * @return
	 */
	public List selectProgressStatusList(MonthlyreportActivityDetailInput monthlyreportActivityDetailInput) {
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.monthlyreport.selectProgressStatus", monthlyreportActivityDetailInput);
	}


	/**
	 * 금월 주요작업 Activity 조회
	 * @param monthlyreportActivityDetailInput
	 * @return
	 */
	public List selectMajorActivityList(MonthlyreportActivityDetailInput monthlyreportActivityDetailInput) {
		monthlyreportActivityDetailInput.setCmnGrpCd(CommonCodeConstants.ACTSTATUS_CODE_GROUP_CODE);
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.monthlyreport.selectMajorActivityList", monthlyreportActivityDetailInput);
	}


	/**
	 * 금월 지연 Activity 조회
	 * @param monthlyreportActivityDetailInput
	 * @return
	 */
	public List selectDelayActivityList(MonthlyreportActivityDetailInput monthlyreportActivityDetailInput) {
		monthlyreportActivityDetailInput.setCmnGrpCd(CommonCodeConstants.ACTSTATUS_CODE_GROUP_CODE);
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.monthlyreport.selectDelayActivityList", monthlyreportActivityDetailInput);
	}


	/**
	 * 익월 주요 Activity 조회
	 * @param monthlyreportActivityDetailInput
	 * @return
	 */
	public List selectNextActivityList(MonthlyreportActivityDetailInput monthlyreportActivityDetailInput) {
		monthlyreportActivityDetailInput.setCmnGrpCd(CommonCodeConstants.ACTSTATUS_CODE_GROUP_CODE);
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.monthlyreport.selectNextActivityList", monthlyreportActivityDetailInput);
	}



	/**
	 * 금월 주요작업 Activity 추가 리스트 조회
	 * @param searchAddActivityInput
	 * @return
	 */
	public List selectAddMajorActivityList(SearchAddActivityInput searchAddActivityInput) {
		searchAddActivityInput.setCmnGrpCd(CommonCodeConstants.ACTSTATUS_CODE_GROUP_CODE);
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.monthlyreport.selectAddMajorActivityList", searchAddActivityInput);
	}



	/**
	 * 금월 지연 Activity 추가 리스트 조회
	 * @param searchAddActivityInput
	 * @return
	 */
	public List selectAddDelayActivityList(SearchAddActivityInput searchAddActivityInput) {
		searchAddActivityInput.setCmnGrpCd(CommonCodeConstants.ACTSTATUS_CODE_GROUP_CODE);
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.monthlyreport.selectAddDelayActivityList", searchAddActivityInput);
	}



	/**
	 * 익월 주요 예정 Activity 추가 리스트 조회
	 * @param searchAddActivityInput
	 * @return
	 */
	public List selectAddNextActivityList(SearchAddActivityInput searchAddActivityInput) {
		searchAddActivityInput.setCmnGrpCd(CommonCodeConstants.ACTSTATUS_CODE_GROUP_CODE);
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.monthlyreport.selectAddNextActivityList", searchAddActivityInput);
	}



	/**
	 * 월간 액티비티 & 공종 삭제
	 * @param cntrctChgId
	 * @param monthlyReportId
	 * @param usrId
	 */
	public void deleteMonthlyResource(String cntrctChgId, Long monthlyReportId, String usrId) {
		UpdateMonthlyreportInput updateMonthlyreportInput = new UpdateMonthlyreportInput();
		updateMonthlyreportInput.setCntrctChgId(cntrctChgId);
		updateMonthlyreportInput.setMonthlyReportId(monthlyReportId);
		updateMonthlyreportInput.setChgId(usrId);
		updateMonthlyreportInput.setDltId(usrId);

		mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.monthlyreport.updateMonthlyActivityAll", updateMonthlyreportInput);
		mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.monthlyreport.updateMonthlyProgressAll", updateMonthlyreportInput);
		mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.monthlyreport.updateMonthlyStatusAll", updateMonthlyreportInput);
	}



	/**
	 * 월간보고 추가
	 *
	 * @param prMonthlyReport
	 * @param monthlyreportStatusList
	 * @param monthlyPhoto
	 */
	@Transactional
	public PrMonthlyReport insertMonthlyreport(String cntrctNo,
											   PrMonthlyReport prMonthlyReport,
											   List<MonthlyreportStatus> monthlyreportStatusList,
											   List<MonthlyreportForm.MonthlyPhoto> monthlyPhoto) throws JsonProcessingException {

		String cntrctChgId = selectLastCntrctChgId(cntrctNo);

		Long maxMrId = prMonthlyReportRepository.findMaxMonthlyReportIdByCntrctNo(cntrctNo);
		prMonthlyReport.setCntrctChgId(cntrctChgId);
		prMonthlyReport.setMonthlyReportId(maxMrId+1);
		prMonthlyReport.setDltYn("N");

		// 월간보고서 저장
		PrMonthlyReport savedReport = prMonthlyReportRepository.saveAndFlush(prMonthlyReport);

		// Activity 및 Progress 저장
		insertMonthlyActivityAndProgress(cntrctNo, prMonthlyReport.getMonthlyReportId(), cntrctChgId, prMonthlyReport.getReportYm());

		// 보할 계산
		BohalRateInput bohalRateInput = new BohalRateInput(cntrctChgId, prMonthlyReport.getMonthlyReportId());
		BohalRate bohal = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.monthlyreport.selectSumBohalRate", bohalRateInput);
		savedReport.setAcmltPlanBohalRate(bohal.getAcmltPlanBohalRate());
		savedReport.setAcmltArsltBohalRate(bohal.getAcmltArsltBohalRate());
		savedReport.setThismthPlanBohalRate(bohal.getThismthPlanBohalRate());
		savedReport.setThismthArsltBohalRate(bohal.getThismthArsltBohalRate());
		savedReport.setLsmthPlanBohalRate(bohal.getLsmthPlanBohalRate());
		savedReport.setLsmthArsltBohalRate(bohal.getLsmthArsltBohalRate());

		// 월간보고 주요공사현황 & 현안사항 저장
		monthlyreportStatusList.forEach(id -> {
			id.setMonthlyReportId(savedReport.getMonthlyReportId());
			id.setCntrctChgId(savedReport.getCntrctChgId());
			id.setRgstrId(savedReport.getRgstrId());
			id.setChgId(savedReport.getChgId());
			mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.monthlyreport.insertPrMonthlyReportStatus", id);
		});

		// 사진 저장
		if(monthlyPhoto != null && !monthlyPhoto.isEmpty()) {
			createAttachmentsPhoto(monthlyPhoto, savedReport, cntrctNo, null);
		}

		return savedReport;
	}


	private void insertPrPhoto(PrMonthlyReportPhoto prMonthlyReportPhoto) {
		mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.monthlyreport.insertPrMonthlyReportPhoto", prMonthlyReportPhoto);
	}

	private PrMonthlyReportPhoto createMonthlyPhoto(MonthlyreportForm.MonthlyPhoto photo, PrMonthlyReport savedReport, Integer fileNo, Short sno) {
		PrMonthlyReportPhoto prMonthlyPhoto = new PrMonthlyReportPhoto();
		prMonthlyPhoto.setCntrctChgId(savedReport.getCntrctChgId());
		prMonthlyPhoto.setMonthlyReportId(savedReport.getMonthlyReportId());
		prMonthlyPhoto.setSno(sno);
		prMonthlyPhoto.setFileNo(fileNo);
		prMonthlyPhoto.setTitlNm(photo.getTitlNm());
		prMonthlyPhoto.setDscrpt(photo.getDscrpt());
		prMonthlyPhoto.setShotDate(photo.getShotDate());
		prMonthlyPhoto.setDltYn("N");
		prMonthlyPhoto.setRgstrId(savedReport.getRgstrId());
		prMonthlyPhoto.setChgId(savedReport.getRgstrId());
		return prMonthlyPhoto;
	}


	private Integer getPrAttachmentMaxFileNo() {
		return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.monthlyreport.selectPrAttachmentMaxFileNo");
	}

	private void insertPrAttachment(PrAttachments prAttachments) {
		mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.monthlyreport.savedPrAttachment", prAttachments);
	}

	private PrAttachments createPrAttachments(FileService.FileMeta newMeta, String usrId) {
		PrAttachments prAttachments = new PrAttachments();
		prAttachments.setFileNm(newMeta.getOriginalFilename());
		prAttachments.setFileDiskNm(newMeta.getFileName());
		prAttachments.setFileDiskPath(newMeta.getDirPath());
		prAttachments.setFileSize(newMeta.getSize());
		prAttachments.setFileHitNum((short)0);
		prAttachments.setDltYn("N");
		prAttachments.setRgstrId(usrId);
		prAttachments.setChgId(usrId);
		return prAttachments;
	}


	/**
	 * Monthly Activity 추가
	 * @param monthlyReportId
	 * @param cntrctChgId
	 * @param reportYm
	 */
	private void insertMonthlyActivityAndProgress(String cntrctNo, Long monthlyReportId, String cntrctChgId, String reportYm) {
		InsertMonthlyDataInput insertMonthlyDataInput = new InsertMonthlyDataInput();
		insertMonthlyDataInput.setCntrctNo(cntrctNo);
		insertMonthlyDataInput.setMonthlyReportId(monthlyReportId);
		insertMonthlyDataInput.setCntrctChgId(cntrctChgId);
		insertMonthlyDataInput.setReportYm(reportYm);
		insertMonthlyDataInput.setRgstrId(UserAuth.get(true).getUsrId());

		mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.monthlyreport.insertThisMonthActivity", insertMonthlyDataInput);
		mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.monthlyreport.insertDelayActivity", insertMonthlyDataInput);
		mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.monthlyreport.insertNextActivity", insertMonthlyDataInput);
		mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.monthlyreport.insertMonthlyProgress", insertMonthlyDataInput);
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
	 * 월간 작성 중복조회
	 * @param cntrctNo
	 * @param reportYm
	 * @return
	 */
	public int checkMonthlyReport(String cntrctNo, String reportYm) {
		return prMonthlyReportRepository.existsByCntrctNoAndReportYm(cntrctNo, reportYm);
	}



	/**
	 * Monthly Activity 업데이트(삭제, 추가)
	 * @param updateActivityListInput
	 */
	@Transactional
	public void updateActivity(UpdateActivityListInput updateActivityListInput) {
		List<PrMonthlyReportActivity> addList = updateActivityListInput.getAddActivityList();
		List<DeleteActivityList> delList = updateActivityListInput.getDelActivityList();
		List<PrMonthlyReportActivity> updateList = updateActivityListInput.getUpdateActivityList();

		if(!addList.isEmpty()) {
			insertMonthlyActivity(addList);
		}
		if(!delList.isEmpty()) {
			deleteActivity(delList);
		}
		if(!updateList.isEmpty()) {
			updateActivityList(updateList, updateActivityListInput.getModalType());
		}
	}


	/**
	 * Monthly Activity 추가
	 * @param addList
	 */
	private void insertMonthlyActivity(List<PrMonthlyReportActivity> addList) {
		addList.forEach(activity -> {
			activity.setRgstrId(UserAuth.get(true).getUsrId());
		});

		mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.monthlyreport.insertMonthlyActivity", addList);
	}



	/**
	 * Monthly Activity 삭제
	 * @param delList
	 */
	private void deleteActivity(List<DeleteActivityList> delList) {
		delList.forEach(activity -> {
			activity.setChgId(UserAuth.get(true).getUsrId());
			activity.setDltId(UserAuth.get(true).getUsrId());
		});

		mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.monthlyreport.updateMonthlyActivity", delList);
	}



	/**
	 * 월간보고 업데이트
	 *
	 * @param cntrctNo
	 * @param prMonthlyReport
	 * @param progressList
	 * @param delPhotoList
	 * @param monthlyPhoto
	 * @return
	 */
	@Transactional
	public strictfp PrMonthlyReport updateMonthlyreport(String cntrctNo,
											   PrMonthlyReport prMonthlyReport,
											   List<PrMonthlyReportProgress> progressList,
											   List<MonthlyreportStatus> statusList,
											   List<PrMonthlyReportPhoto> delPhotoList,
											   List<MonthlyreportForm.MonthlyPhoto> monthlyPhoto) throws JsonProcessingException {
		PrMonthlyReport findReport = prMonthlyReportRepository.findByCntrctChgIdAndMonthlyReportId(prMonthlyReport.getCntrctChgId(), prMonthlyReport.getMonthlyReportId()).orElse(null);

		if(findReport == null) {
			throw new BizException("수정할 보고서가 없습니다 : ");
		}

		// PrMonthlyReport에 저장할 보할 합계
		double sumThismthPlan = 0;	// 금월계획보할율 합계
		double sumAcmltPlan = 0;	// 누적계획보할율 합계
		double sumThismthArslt = 0;	// 금월실적보할율 합계
		double sumAcmltArslt = 0;	// 누적실적보할율 합계
		if(!progressList.isEmpty()) {
			for(PrMonthlyReportProgress p : progressList) {
				PrMonthlyReportProgress progress = prMonthlyReportProgressRepository.findByMonthlyCnsttyId(p.getMonthlyCnsttyId());
				double cntrctBohalRate = progress.getCntrctBohalRate();
				progress.setRmk(p.getRmk());
				progress.setThismthPlanBohalRate(p.getThismthPlanBohalRate());
				progress.setAcmltPlanBohalRate(p.getAcmltPlanBohalRate());
				progress.setThismthArsltBohalRate(p.getThismthArsltBohalRate());
				progress.setAcmltArsltBohalRate(p.getAcmltArsltBohalRate());

				sumThismthPlan += (cntrctBohalRate * p.getThismthPlanBohalRate()) / 100.0;
				sumAcmltPlan += (cntrctBohalRate * p.getAcmltPlanBohalRate()) / 100.0;
				sumThismthArslt += (cntrctBohalRate * p.getThismthArsltBohalRate()) / 100.0;
				sumAcmltArslt += (cntrctBohalRate * p.getAcmltArsltBohalRate()) / 100.0;

				prMonthlyReportProgressRepository.save(progress);
			}

			findReport.setThismthPlanBohalRate(sumThismthPlan);
			findReport.setAcmltPlanBohalRate(sumAcmltPlan);
			findReport.setThismthArsltBohalRate(sumThismthArslt);
			findReport.setAcmltArsltBohalRate(sumAcmltArslt);
			prMonthlyReportRepository.save(findReport);
		}

		// 월간보고 주요공사현황 & 현안사항 업데이트
		if(!statusList.isEmpty()) {
			statusList.forEach(m -> {
				m.setCntrctChgId(findReport.getCntrctChgId());
				m.setMonthlyReportId(findReport.getMonthlyReportId());
				m.setChgId(UserAuth.get(true).getUsrId());
				mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.monthlyreport.updateMonthlyreportStatus", m);
			});
		}

		// 사진 저장
		Integer findFileNo = getPrAttachmentMaxFileNoById(findReport.getCntrctChgId(), findReport.getMonthlyReportId());
		if(monthlyPhoto != null && !monthlyPhoto.isEmpty()) {
			createAttachmentsPhoto(monthlyPhoto, findReport, cntrctNo, findFileNo);
		}

		// 월간보고 사진 삭제
		if(!delPhotoList.isEmpty()) {
			delPhotoList.forEach(p -> {
				p.setDltId(UserAuth.get(true).getUsrId());
				mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.monthlyreport.updateDeleteMonthlyreportPhoto", p);
				mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.monthlyreport.updateDeletePrAttachments", p);
			});
		}

		// 월간보고 정보 업데이트
		boolean hasUpdate = false;
		
		if(prMonthlyReport.getRmrkCntnts() != null) {
			findReport.setRmrkCntnts(prMonthlyReport.getRmrkCntnts());
			hasUpdate = true;
		}
		if(prMonthlyReport.getMonthlyReportDate() != null) {
			findReport.setMonthlyReportDate(prMonthlyReport.getMonthlyReportDate());
			hasUpdate = true;
		}
		if(prMonthlyReport.getTitle() != null) {
			findReport.setTitle(prMonthlyReport.getTitle());
			hasUpdate = true;
		}
		if(prMonthlyReport.getThisMonthPromotion() != null) {
			findReport.setThisMonthPromotion(prMonthlyReport.getThisMonthPromotion());
			hasUpdate = true;
		}
		if(prMonthlyReport.getNextMonthPlan() != null) {
			findReport.setNextMonthPlan(prMonthlyReport.getNextMonthPlan());
			hasUpdate = true;
		}

		if(hasUpdate) {
			prMonthlyReportRepository.save(findReport);
		}

		return findReport;
	}


	private Integer getPrAttachmentMaxFileNoById(String cntrctChgId, Long monthlyReportId) {
		MybatisInput input = MybatisInput.of().add("cntrctChgId", cntrctChgId).add("monthlyReportId", monthlyReportId);
		return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.monthlyreport.selectPrAttachmentMaxFileNoById", input);
	}

	private void createAttachmentsPhoto(List<MonthlyreportForm.MonthlyPhoto> monthlyPhoto,
										PrMonthlyReport savedReport,
										String cntrctNo,
										Integer existingFileNo) throws JsonProcessingException {
		FileService.FileMeta oldMeta = null;
		FileService.FileMeta newMeta = null;
		String savePath = String.format("%s/%s",uploadPath, getUploadPathByWorkType(FileUploadType.MONTHLY_REPORT, cntrctNo));
		Integer fileNo = existingFileNo != null ? existingFileNo : getPrAttachmentMaxFileNo() + 1;
		Short sno = fileNo != null ? (short) (getPrAttachmentMaxSNo(fileNo) + 1) : 1;

		for(MonthlyreportForm.MonthlyPhoto photo : monthlyPhoto) {
			Map<String,Object> map = photo.getMeta();
			String mapString = objectMapper.writeValueAsString(map);
			oldMeta = objectMapper.readValue(mapString, FileService.FileMeta.class);
			newMeta = fileService.build(mapString, savePath);

			PrAttachments createFile = createPrAttachments(newMeta, savedReport.getRgstrId());
			createFile.setFileNo(fileNo);
			createFile.setSno(sno);
			insertPrAttachment(createFile);

			PrMonthlyReportPhoto prPhoto = createMonthlyPhoto(photo, savedReport, fileNo, sno);
			insertPrPhoto(prPhoto);

			fileService.moveFile(oldMeta.getFilePath(), newMeta.getFilePath());
			sno++;
		}
	}

	private Short getPrAttachmentMaxSNo(Integer fileNo) {
		return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.monthlyreport.selectPrAttachmentMaxSno", fileNo);
	}


	/**
	 * Monthly Activity 수정
	 * @param updateList
	 * @param modalType
	 */
	private void updateActivityList(List<PrMonthlyReportActivity> updateList, String modalType) {
		updateList.forEach(activity -> {
			activity.setChgId(UserAuth.get(true).getUsrId());
		});

		if(modalType.equals("d")) {
			mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.monthlyreport.updateDelayActivity", updateList);
		} else {
			mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.monthlyreport.updateNextActivity", updateList);
		}

	}


	/**
	 * 월간보고 승인상태 변경
	 * @param report
	 * @param usrId
	 * @param apprvlStats
	 */
	public void updateApprovalStatus(PrMonthlyReport report, String usrId, String apprvlStats) {
		report.setApprvlStats(apprvlStats);

		if("E".equals(apprvlStats)) {
			report.setApprvlReqId(usrId);
			report.setApprvlReqDt(LocalDateTime.now());
		} else {
			report.setApprvlId(usrId);
			report.setApprvlDt(LocalDateTime.now());
		}

		prMonthlyReportRepository.save(report);
	}


	/**
	 * 월간보고 업데이트 BY apDocId
	 * @param apDocId
	 * @param apUsrId
	 * @param apDocStats
	 */
	public void updateMonthlyReportByApDocId(String apDocId, String apUsrId, String apDocStats) {
		PrMonthlyReport prMonthlyReport = prMonthlyReportRepository.findByApDocId(apDocId).orElse(null);

		if(prMonthlyReport == null) {
			throw new BizException("ApDocId와 일치하는 보고서가 없습니다 : " + apDocId);
		}

		String apStats = "C".equals(apDocStats) ? "A" : "R";
		updateApprovalStatus(prMonthlyReport, apUsrId, apStats);
	}

	/**
	 * 월간보고 데이터 조회
	 * @param apDocId
	 * @return
	 */
	public Map<String, Object> selectMonthlyReportByApDocId(String apDocId) {
		Map<String, Object> returnMap = new HashMap<>();
		PrMonthlyReport prMonthlyReport = prMonthlyReportRepository.findByApDocId(apDocId).orElse(null);
		if (prMonthlyReport != null) {
			returnMap.put("report", prMonthlyReport);
			returnMap.put("resources", selectMonthlyReportResource(prMonthlyReport.getCntrctChgId(), prMonthlyReport.getMonthlyReportId()));
		}
		return returnMap;
	}

	/**
	 * 월간보고 API통신 -> Activity 및 Progress 저장
	 * @param activityList
	 * @param progressList
	 */
	public void insertResourcesToApi(String cntrctNo,
									 PrMonthlyReport prMonthlyReport,
									 List<PrMonthlyReportActivity> activityList,
									 List<PrMonthlyReportProgress> progressList,
									 List<PrMonthlyReportStatus> statusList,
									 List<PrMonthlyReportPhoto> photoList,
									 List<Map<String, Object>> prFileInfo) {
		prMonthlyReportRepository.saveAndFlush(prMonthlyReport);

		if(!activityList.isEmpty()) {
			for(PrMonthlyReportActivity activity : activityList){
				if(activity.getChgId() == null) activity.setChgId(prMonthlyReport.getRgstrId());
			}
			prMonthlyReportActivityRepository.saveAll(activityList);
		}

		if(!progressList.isEmpty()) {
			for(PrMonthlyReportProgress progress : progressList){
				if(progress.getChgId() == null) progress.setChgId(prMonthlyReport.getRgstrId());
			}
			prMonthlyReportProgressRepository.saveAll(progressList);
		}

		if(!statusList.isEmpty()) {
			for(PrMonthlyReportStatus status : statusList){
				if(status.getChgId() == null) status.setChgId(prMonthlyReport.getRgstrId());
				mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.monthlyreport.insertPrMonthlyReportStatus", status);
			}
		}

		if(photoList != null && !photoList.isEmpty()) {
			for(PrMonthlyReportPhoto photo : photoList){
				insertPrPhoto(photo);
			}
		}

		if(prFileInfo != null && !prFileInfo.isEmpty()) {
			insertPrFileInfoToApi(prMonthlyReport, prFileInfo, cntrctNo);
		}
	}


	/**
	 * API 통신 시 첨부파일 저장
	 * @param prMonthlyReport
	 * @param files
	 */
	public void insertPrFileInfoToApi(PrMonthlyReport prMonthlyReport, List<Map<String, Object>> files, String cntrctNo) {
		if (files != null && !files.isEmpty()) {
			 String fullPath = Path.of(uploadPath, getUploadPathByWorkType(FileUploadType.MONTHLY_REPORT, cntrctNo)).toString().replace("\\", "/");
			 for(Map<String, Object> fileInfo : files) {
				 String fileName = (String) fileInfo.get("fileNm"); // 실제 파일명

                // 파일 이름이 비어있거나 null인 경우 건너뛰기
                if (fileName == null || fileName.trim().isEmpty()) {
                    continue;
                }

				try {
					// Base64로 인코딩된 파일 내용을 디코딩
					String base64Content = (String) fileInfo.get("fileContent");
                    if (base64Content == null || base64Content.isEmpty()) {
                        continue;
                    }

					byte[] fileContent = Base64.getDecoder().decode(base64Content);

					// 파일을 디스크에 저장
                    String savedFileName = (String) fileInfo.get("fileDiskNm"); // 서버 저장 파일명(uuid)
                    Path savedFilePath = Paths.get(fullPath, savedFileName); // uploadpath + 업무 + 년월 + 서버 저장 파일명
					Path parent = savedFilePath.getParent();

					if(parent != null) {
						// 디렉토리가 없으면 생성
						Files.createDirectories(parent);

						// 파일 저장
						Files.write(savedFilePath, fileContent);
					}

					PrAttachments prAttachments = new PrAttachments();
					prAttachments.setFileNo((Integer) fileInfo.get("fileNo"));
				    prAttachments.setSno((Short) fileInfo.get("sno"));
				    prAttachments.setFileNm(fileName);
				    prAttachments.setFileDiskNm(savedFileName);
				    prAttachments.setFileDiskPath(fullPath);
				    prAttachments.setFileSize((Integer) fileInfo.get("fileSize"));
				    prAttachments.setFileHitNum((Short) fileInfo.get("fileHitNum"));
				    prAttachments.setDltYn("N");
				    prAttachments.setRgstrId(prMonthlyReport.getRgstrId());
				    prAttachments.setChgId(prMonthlyReport.getChgId());

					insertPrAttachment(prAttachments);
				} catch (IOException e) {
					log.error("첨부파일 저장 오류 {}: {} ", fileName, e.getMessage(), e);
				}
			 }
		}
	}

	/**
	 * API 통신 시 연계데이터 조회
	 * @param cntrctChgId
	 * @param monthlyReportId
	 * @return
	 */
	public Map<String, Object> selectMonthlyReportResource(String cntrctChgId, Long monthlyReportId) {
		List<PrMonthlyReportActivity> activityList = prMonthlyReportActivityRepository.findByCntrctChgIdAndMonthlyReportIdAndDltYn(cntrctChgId, monthlyReportId, "N");
		List<PrMonthlyReportProgress> progressList = prMonthlyReportProgressRepository.findByCntrctChgIdAndMonthlyReportIdAndDltYn(cntrctChgId, monthlyReportId, "N");

		MybatisInput input = MybatisInput.of().add("cntrctChgId", cntrctChgId).add("monthlyReportId", monthlyReportId);
		List<PrMonthlyReportStatus> statusList = mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.monthlyreport.selectMonthlyStatusById", input);
		List<PrMonthlyReportPhoto> photoList = mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.monthlyreport.selectMonthlyPhotoById", input);
		List<PrAttachments> prAttachments = new ArrayList<>();

		if(photoList != null && !photoList.isEmpty()) {
			prAttachments = selectMonthlyAttachments(photoList.get(0).getFileNo());
		}

		List<Map<String, Object>> prFileInfo = Collections.emptyList();
		if(prAttachments != null && !prAttachments.isEmpty()) {
			prFileInfo = convertToFileInfo(prAttachments);
		}

		Map<String, Object> resourceMap = new HashMap<>();
		resourceMap.put("activityList", activityList);
		resourceMap.put("progressList", progressList);
		resourceMap.put("statusList", statusList);
		resourceMap.put("photoList", photoList);
		resourceMap.put("prFileInfo", prFileInfo);

		return resourceMap;
	}

	private List<Map<String, Object>> convertToFileInfo(List<PrAttachments> attachments) {
		if (attachments == null || attachments.isEmpty()) {
            log.info("##### No attachments to convert");
            return Collections.emptyList();
        }

		List<Map<String, Object>> fileInfoList = new ArrayList<>();

		for (PrAttachments attachment : attachments) {
			if (attachment == null || attachment.getFileNm() == null) {
				log.warn("##### Invalid attachment data: {}", attachment);
                continue;
			}

			// 파일 경로가 없는 경우 건너뛰기
            if (attachment.getFileDiskPath() == null || attachment.getFileDiskNm() == null) {
                log.warn("##### Physical file path not found for attachment: {}", attachment.getFileNm());
                continue;
            }

			Path filePath = Paths.get(attachment.getFileDiskPath(), attachment.getFileDiskNm());
            if (!Files.exists(filePath)) {
                log.warn("##### File not found: {}", filePath);
                continue;
            }

			try {
                // 파일 내용을 Base64로 인코딩
                byte[] fileContent = Files.readAllBytes(filePath);
                String base64Content = Base64.getEncoder().encodeToString(fileContent);

                Map<String, Object> fileInfo = new HashMap<>();
                fileInfo.put("fileNo", attachment.getFileNo());
                fileInfo.put("sno", attachment.getSno());
                fileInfo.put("fileNm", attachment.getFileNm()); // 실제 파일명
                fileInfo.put("fileDiskNm", attachment.getFileDiskNm()); // 서버 파일명
                fileInfo.put("fileDiskPath", attachment.getFileDiskPath()); // 물리적 경로
                fileInfo.put("fileSize", attachment.getFileSize());
                fileInfo.put("fileHitNum", attachment.getFileHitNum());
                fileInfo.put("dltYn", attachment.getDltYn());
                fileInfo.put("rgstrId", attachment.getRgstrId());
                fileInfo.put("chgId", attachment.getChgId());
                fileInfo.put("fileContent", base64Content); // Base64로 인코딩된 파일 내용

                fileInfoList.add(fileInfo);

            } catch (IOException e) {
                log.error("##### Error reading file {}: {}", filePath, e.getMessage());
                continue;
            }
        }
        log.info("##### Successfully converted {} attachments to file info", fileInfoList.size());
        return fileInfoList;
	}

	private List<PrAttachments> selectMonthlyAttachments(Integer fileNo) {
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.monthlyreport.selectMonthlyAttachments", fileNo);
	}


	/**
	 * 결재요청 삭제 -> 월간보고 컬럼 값 삭제 or 데이터 삭제
	 * @param apDocList
	 * @param usrId
	 * @param toApi
	 */
	public void updateMonthlyreportApprovalReqCancel(List<ApDoc> apDocList, String usrId, boolean toApi) {
		apDocList.forEach(apDoc -> {
			PrMonthlyReport prMonthlyReport = prMonthlyReportRepository.findByApDocId(apDoc.getApDocId()).orElse(null);

			if(prMonthlyReport == null) return;

			if(toApi) {
				// api 통신 true -> 데이터 삭제
				deleteMonthlyreportDataToApi(prMonthlyReport);
			} else {
				// api 통신 false -> 컬럼 값 변경
				prMonthlyReportRepository.updateApprovalStausCancel(null, null, null, null, prMonthlyReport.getCntrctChgId(), prMonthlyReport.getMonthlyReportId(), usrId);
			}
		});
	}


	/**
	 * 결재요청 삭제 -> 월간보고 데이터 삭제
	 * @param prMonthlyReport
	 */
	private void deleteMonthlyreportDataToApi(PrMonthlyReport prMonthlyReport) {
		mybatisSession.delete("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.monthlyreport.deleteMonthlyActivityToApi", prMonthlyReport);
		mybatisSession.delete("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.monthlyreport.deleteMonthlyProgressToApi", prMonthlyReport);
		mybatisSession.delete("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.monthlyreport.deleteMonthlyreportToApi", prMonthlyReport);
	}

	/**
	 * 월간보고 공사종류 조회
	 * @param cntrctNo
	 * @return
	 */
	public List getUnitCnstType(String cntrctNo) {
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.monthlyreport.getUnitCnstType", cntrctNo);
	}


	/**
	 * 월간보고 현황 조회
	 *
	 * @param cntrctChgId
	 * @param monthlyReportId
	 * @return
	 */
	public List selectMonthlyStatusList(String cntrctChgId, Long monthlyReportId) {
		MybatisInput input = MybatisInput.of().add("cntrctChgId", cntrctChgId).add("monthlyReportId", monthlyReportId);
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.monthlyreport.selectMonthlyStatusList", input);
	}

	public List selectMonthlyPhoto(String cntrctChgId, Long monthlyReportId) {
		MybatisInput input = MybatisInput.of().add("cntrctChgId", cntrctChgId).add("monthlyReportId", monthlyReportId);
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.monthlyreport.selectMonthlyPhoto", input);
	}


}
