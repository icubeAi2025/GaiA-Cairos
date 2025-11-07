package kr.co.ideait.platform.gaiacairos.comp.eapproval.service;

import kr.co.ideait.platform.gaiacairos.comp.construction.service.DailyreportService;
import kr.co.ideait.platform.gaiacairos.comp.construction.service.MainmtrlReqfrmService;
import kr.co.ideait.platform.gaiacairos.comp.construction.service.QualityinspectionService;
import kr.co.ideait.platform.gaiacairos.comp.eapproval.helper.EapprovalHelper;
import kr.co.ideait.platform.gaiacairos.comp.progress.service.MonthlyreportService;
import kr.co.ideait.platform.gaiacairos.comp.progress.service.WeeklyreportService;
import kr.co.ideait.platform.gaiacairos.comp.projectcost.service.DepositService;
import kr.co.ideait.platform.gaiacairos.comp.projectcost.service.PaymentService;
import kr.co.ideait.platform.gaiacairos.comp.safety.service.SafetyDiaryIntegrationService;
import kr.co.ideait.platform.gaiacairos.comp.safety.service.SafetymgmtService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.*;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.*;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisOutput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.safety.SafetymgmtMybatisParam;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApprovalRequestService extends AbstractGaiaCairosService {

	@Autowired
	EapprovalHelper eapprovalHelper;

	@Autowired
    DraftService draftService;

	@Autowired
	PaymentService paymentService;

	@Autowired
	DepositService depositService;

	@Autowired
	DailyreportService dailyreportService;

	@Autowired
	WeeklyreportService weeklyreportService;

	@Autowired
	SafetyDiaryIntegrationService safetyDiaryIntegrationService;

	@Autowired
	MonthlyreportService monthlyreportService;

	@Autowired
	QualityinspectionService qualityinspectionService;

	@Autowired
	SafetymgmtService safetymgmtService;

	@Autowired
	MainmtrlReqfrmService mainmtrlReqfrmService;

	@Autowired
	CwPayMngRepository cwPayMngRepository;

	@Autowired
	CwFrontMoneyRepository cwFrontMoneyRepository;

	@Autowired
	CwDailyReportRepository cwDailyReportRepository;

	@Autowired
	PrMonthlyReportRepository prMonthlyReportRepository;

	@Autowired
	CwQualityInspectionRepository cwQualityInspectionRepository;

	@Autowired
	CwSafetyInspectionRepository safetyRepository;

	@Autowired
	CwSadtagRepositories sadtagRepositories;

	@Autowired
	ApLineRepository apLineRepository;

	@Autowired
	ApDocRepository apDocRepository;

	@Autowired
	PrWeeklyReportRepository prWeeklyReportRepository;

	@Autowired
	CwInspectionReportRepository inspectionReportRepository;

	@Autowired
	CwMainmtrlReqfrmRepository mainmtrlReqfrmRepository;


	@Value("${link.domain.url}")
	private String domainUrl;

	// 결재문서 유형
	public static final String DAILY_DOC = "02"; 			// 작업일보
	public static final String PAYMENT_DOC = "03"; 			// 기성관리
	public static final String MONTHLY_DOC = "04"; 			// 월간보고
	public static final String DEPOSIT_DOC = "05"; 			// 선급, 선급금
	public static final String QUALITY_ISP_DOC = "06";		// 품질 검측요청
	public static final String QUALITY_APP_DOC = "07";		// 품질 결재요청
	public static final String SAFETY_DOC = "08";			// 안전점검 승인요청
	public static final String SADTAG_DOC = "09"; 			// 안전지적서 승인요청
	public static final String WEEKLY_DOC = "11"; 			// 주간보고 승인요청
	public static final String INSPECTION_DOC = "12"; 		// 감리일지 승인요청
	public static final String SAFETY_REP_DOC = "13"; 		// 안전점검 점검결과 작성 요청
	public static final String SAFETY_DIARY_DOC = "14"; 	// 안전일지 승인요청
	public static final String MAINMTRL_REQFRM_DOC = "15";	// 주요자재 검수요청서 승인요청


	/**
	 * 기성관리 승인요청 -> 결재문서 및 결재선 생성
	 * @param cwPayMng
	 */
	@Transactional
	public void insertPaymentDoc(CwPayMng cwPayMng, Map<String, Object> requestMap, Map<String, Object> resourceMap) {
		log.info("insertPaymentDoc: 기성관리 승인 요청 진행 *****************\n" +
		"1. 대상 리포트 cwPayMng = {} \n" +
		"2. 승인요청 데이터 requestMap = {} \n" +
		"3. 기성관리 데이터 resourceMap = {}", cwPayMng, requestMap, resourceMap);

		String cntrctNo = (String) requestMap.get("cntrctNo");
		String usrId = (String) requestMap.get("usrId");

		// 결재선 조회
		List<MybatisOutput> apLineSetList = checkApLineSet(cntrctNo, PAYMENT_DOC);

		// 결재문서 양식 조회
		String edtrText = getHtmlForm("form/approval/payment.html");

		//기성회차
		String payprceTmnum = String.format("%d 회", cwPayMng.getPayprceTmnum());
		// 기성년월
		String payprceYm = String.format("%s년 %s월", cwPayMng.getPayprceYm().substring(0, 4), cwPayMng.getPayprceYm().substring(4, 6));
		// 기성신청일
		String payApprvlDate = isNotEmpty(cwPayMng.getPayApprvlDate()) ? String.format("%s-%s-%s", cwPayMng.getPayApprvlDate().substring(0, 4), cwPayMng.getPayApprvlDate().substring(4, 6), cwPayMng.getPayApprvlDate().substring(6, 8)) : "";
		// 금회기성
		String thtmAcomAmt = cwPayMng.getThtmAcomAmt() != null ? String.format("%,d 원", cwPayMng.getThtmAcomAmt()) : "";
		// 선급금 공제 금액
		String ppaymnyCacltAmt = cwPayMng.getPpaymnyCacltAmt() != null ? String.format("%,d 원", cwPayMng.getPpaymnyCacltAmt())	: "";
		// 실지급액
		String thtmPaymntAmt = cwPayMng.getThtmPaymntAmt() != null ? String.format("%,d 원", cwPayMng.getThtmPaymntAmt()) : "";
		// 잔여
		String remndrAmt = cwPayMng.getRemndrAmt() != null ? String.format("%,d 원", cwPayMng.getRemndrAmt()) : "";
		// 검사일
		String inspctDate = isNotEmpty(cwPayMng.getInspctDate()) ? String.format("%s-%s-%s", cwPayMng.getInspctDate().substring(0, 4), cwPayMng.getInspctDate().substring(4, 6), cwPayMng.getInspctDate().substring(6, 8)) : "";
		// 대금지급일
		String paymntDate = isNotEmpty(cwPayMng.getPaymntDate()) ? String.format("%s-%s-%s", cwPayMng.getPaymntDate().substring(0, 4), cwPayMng.getPaymntDate().substring(4, 6), cwPayMng.getPaymntDate().substring(6, 8)) : "";
		// 비고
		String rmrk = isNotEmpty(cwPayMng.getRmrk()) ? cwPayMng.getRmrk() : "";
		// 상세보기 링크
		String linkParams = String.format("/projectcost/payment/detail?type=d&sType=d&pjtNo=%s&cntrctNo=%s&sNo=%s", UserAuth.get(true).getPjtNo(), cntrctNo, cwPayMng.getPayprceSno());

		// html 본문 생성
		edtrText = edtrText.replace("payprceTmnum", payprceTmnum)
				.replace("payprceYm", payprceYm)
				.replace("payApprvlDate", payApprvlDate)
				.replace("thtmAcomAmt", thtmAcomAmt)
				.replace("ppaymnyCacltAmt", ppaymnyCacltAmt)
				.replace("thtmPaymntAmt", thtmPaymntAmt)
				.replace("remndrAmt", remndrAmt)
				.replace("inspctDate", inspctDate)
				.replace("paymntDate", paymntDate)
				.replace("rmrk", rmrk)
				.replace("paramValue", linkParams);
		// 텍스트
		String text = """
		기성회차	%s
		기성년월	%s
		기성신청일	%s
		금회기성	%s
		선급금 공제 금액	%s		
		실 지급액	%s		
		잔여	%s
		검사일	%s
		대금지급일	%s
		비고	%s
		""".formatted(payprceTmnum, payprceYm, payApprvlDate, thtmAcomAmt, ppaymnyCacltAmt, thtmPaymntAmt, remndrAmt, inspctDate, paymntDate, rmrk);

		String pjtName = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.approval.selectProjectName", cntrctNo);
		String apDocTitle = String.format("[%s] %d회차 기성 신청", pjtName, cwPayMng.getPayprceTmnum());

		// 전자결재 생성
		ApDoc savedApDoc = createApDoc(cwPayMng.getCntrctNo(), apDocTitle, edtrText, text, PAYMENT_DOC);

		// 기성관리 -> 전자결재문서 id 저장
		cwPayMng.setApDocId(savedApDoc.getApDocId());
		cwPayMng.setApprvlReqId(usrId);
		cwPayMng.setApprvlReqDt(LocalDateTime.now());
		cwPayMngRepository.save(cwPayMng);

		// 결재선 생성
		List<ApLine> savedApLineList = createApLine(apLineSetList, savedApDoc.getApDocNo(), savedApDoc.getApDocId());

		// 알림메시지
		eapprovalHelper.insertInitAlarm(savedApDoc.getApDocId(), savedApDoc.getApDocStats());

		// 전결체크
		boolean isDelegatable = checkDelegate(savedApDoc, savedApLineList);
		String apprvlStats = isDelegatable && savedApLineList.size() == 1 ? "A" : "E"; // 주간보고 승인상태 세팅
		String checkPgaiaUser = savedApLineList.get(0).getApUsrId();
		if(isDelegatable && savedApLineList.size() > 1) {
			checkPgaiaUser = savedApLineList.get(1).getApUsrId();
		}
		paymentService.updateApprovalStatus(cwPayMng, usrId, apprvlStats);

		// API 통신조건 체크
		boolean toApi = checkApiCondition("기성관리", requestMap, isDelegatable, checkPgaiaUser, apprvlStats);

		// API 통신
		if(toApi && !savedApLineList.isEmpty()) {
			log.info("insertPaymentDoc: savedApDoc = {}", savedApDoc);
			//params 값 셋팅
			Map<String, Object> params = new HashMap<>();

			params.put("report", cwPayMng);
			params.put("resources", resourceMap);
			params.put("reportType", PAYMENT_DOC);
			params.put("usrId", UserAuth.get(true).getUsrId());
			params.put("apDoc", savedApDoc);
			params.put("apLineList", savedApLineList);
			params.put("apDocStats", savedApDoc.getApDocStats());

			log.info("기성관리 API 통신 params = {}", params);

			sendParams(params);

		}
	}



	/**
	 * 선급금/공제금 승인요청 -> 결재문서 및 결재선 생성
	 * @param cwFrontMoney
	 * @param requestMap
	 * @param resourceMap
	 */
	@Transactional
	public void insertDepositDoc(CwFrontMoney cwFrontMoney, Map<String, Object> resourceMap, Map<String, Object> requestMap) {
		log.info("insertDepositDoc: 선금및공제금 승인 요청 진행 *****************\n" +
			"1. 대상 리포트 prWeeklyReport = {} \n" +
			"2. 승인요청 데이터 requestMap = {} \n" +
			"3. 선금및공제금 데이터 resourceMap = {}", cwFrontMoney, requestMap, resourceMap);

		String cntrctNo = (String) requestMap.get("cntrctNo");
		String usrId = (String) requestMap.get("usrId");

		Long ppaymnySno = cwFrontMoney.getPpaymnySno();
		// 결재선 조회
		List<MybatisOutput> apLineSetList = checkApLineSet(cntrctNo, DEPOSIT_DOC);

		// 결재문서 양식 조회
		String edtrText = getHtmlForm("form/approval/deposit.html");

		// 기성회차 조회
		Long payprceTmnum = cwPayMngRepository.findPayprceTmnumByCntrctNoAndPayprceSno(cntrctNo, ppaymnySno).orElse(null);
		// 공제 기성회차
		String payprceTmnumStr = String.format("%d 회", payprceTmnum);
		// 발생일자
		String ocrnceDate = isNotEmpty(cwFrontMoney.getOcrnceDate()) ? String.format("%s년 %s월", cwFrontMoney.getOcrnceDate().substring(0, 4), cwFrontMoney.getOcrnceDate().substring(4, 6)) : "";
		// 구분
		String payType = "0301".equals(cwFrontMoney.getPayType()) ? "선급금" : "공제금";
		// 선급금 지급금액
		String ppaymnyAmt = cwFrontMoney.getPpaymnyAmt() != null ? String.format("%,d 원", cwFrontMoney.getPpaymnyAmt()) : "";
		// 선급금 정산 금액
		String ppaymnyCacltAmt = cwFrontMoney.getPpaymnyCacltAmt() != null ? String.format("%,d 원", cwFrontMoney.getPpaymnyCacltAmt()) : "";
		// 지체상금 금액
		String dfrcmpnstAmt = cwFrontMoney.getDfrcmpnstAmt() != null ? String.format("%,d 원", cwFrontMoney.getDfrcmpnstAmt()) : "";
		// 비고
		String rmrk = isNotEmpty(cwFrontMoney.getRmrk()) ? cwFrontMoney.getRmrk() : "";
		// 상세보기 링크
		String linkParams = String.format("/projectcost/deposit/detail?type=d&sType=d&pjtNo=%s&cntrctNo=%s&sNo=%s", UserAuth.get(true).getPjtNo(), cntrctNo, ppaymnySno);
		// html 본문 생성
		edtrText= edtrText.replace("payprceTmnumStr", payprceTmnumStr)
				.replace("ocrnceDate", ocrnceDate)
				.replace("payType", payType)
				.replace("ppaymnyAmt", ppaymnyAmt)
				.replace("ppaymnyCacltAmt", ppaymnyCacltAmt)
				.replace("dfrcmpnstAmt", dfrcmpnstAmt)
				.replace("rmrk", rmrk)
				.replace("paramValue", linkParams);
		// 텍스트
		String text = """
		공제기성회차	%s
		발생일자	%s
		구분	%s
		선급금 지급 금액	%s
		선급금 정산 금액	%s		
		지체상금액	%s		
		비고	%s
		""".formatted(payprceTmnumStr, ocrnceDate, payType, ppaymnyAmt, ppaymnyCacltAmt, dfrcmpnstAmt, rmrk);

		String pjtName = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.approval.selectProjectName", cntrctNo);
		String apDocTitle = String.format("[%s] %d회차 선급금/공제금 신청", pjtName, payprceTmnum);

		// 전자결재 생성
		ApDoc savedApDoc = createApDoc(cwFrontMoney.getCntrctNo(), apDocTitle, edtrText, text, DEPOSIT_DOC );

		// 선급금, 공제금 -> 전자결재문서 id 저장
		cwFrontMoney.setApDocId(savedApDoc.getApDocId());
		cwFrontMoney.setApprvlReqId(usrId);
		cwFrontMoney.setApprvlDt(LocalDateTime.now());
		cwFrontMoneyRepository.save(cwFrontMoney);

		// 결재선 생성
		List<ApLine> savedApLineList = createApLine(apLineSetList, savedApDoc.getApDocNo(), savedApDoc.getApDocId());

		// 알림메시지
		eapprovalHelper.insertInitAlarm(savedApDoc.getApDocId(), savedApDoc.getApDocStats());

		// 전결체크
		boolean isDelegatable = checkDelegate(savedApDoc, savedApLineList);
		String apprvlStats = isDelegatable && savedApLineList.size() == 1 ? "A" : "E";
		String checkPgaiaUser = savedApLineList.get(0).getApUsrId();
		if(isDelegatable && savedApLineList.size() > 1) {
			checkPgaiaUser = savedApLineList.get(1).getApUsrId();
		}
		depositService.updateApprovalStatus(cwFrontMoney, usrId, apprvlStats);

		// API 통신조건 체크
		boolean toApi = checkApiCondition("선금및공제금", requestMap, isDelegatable, checkPgaiaUser, apprvlStats);

		// API 통신
		if(toApi && !savedApLineList.isEmpty()) {
			//params 값 셋팅
			Map<String, Object> params = new HashMap<>();

			params.put("report", cwFrontMoney);
			params.put("resources", resourceMap);
			params.put("reportType", DEPOSIT_DOC);
			params.put("usrId", UserAuth.get(true).getUsrId());
			params.put("apDoc", savedApDoc);
			params.put("apLineList", savedApLineList);
			params.put("apDocStats", savedApDoc.getApDocStats());

			log.info("선금및공제금 API 통신 params : {}", params);

			sendParams(params);

		}

	}


	/**
	 * 작업일지 승인요청 -> 결재문서 및 결재선 생성
	 * @param cwDailyReport
	 * @param requestMap
	 * @param resourceMap
	 */
	@Transactional
	public void insertDailyReportDoc(CwDailyReport cwDailyReport, Map<String, Object> requestMap, Map<String, Object> resourceMap) {
		log.info("insertDailyReportDoc: 작업일보 승인 요청 진행 *****************\n" +
				"1. 대상 리포트 prWeeklyReport = {} \n" +
				"2. 승인요청 데이터 requestMap = {} \n" +
				"3. 작업일보 데이터 resourceMap = {}", cwDailyReport, requestMap, resourceMap);

		String cntrctNo = (String) requestMap.get("cntrctNo");
		String usrId = (String) requestMap.get("usrId");

		List<MybatisOutput> apLineSetList = checkApLineSet(cntrctNo, DAILY_DOC);

		String edtrText = getHtmlForm("form/approval/dailyreport.html");

		// 보고일자
		String dailyReportDate = cwDailyReport.getDailyReportDate();
		String dailyReportDateFmt = LocalDate.parse(dailyReportDate,  DateTimeFormatter.ofPattern("yyyy-MM-dd")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

		// 보고서 번호
		String reportNo = isNotEmpty(cwDailyReport.getReportNo()) ? cwDailyReport.getReportNo() : "";
		// 제목
		String title = isNotEmpty(cwDailyReport.getTitle()) ? cwDailyReport.getTitle() : "";
		// 오전 날씨
		String amWthr = isNotEmpty(cwDailyReport.getAmWthr()) ? cwDailyReport.getAmWthr() : "";
		// 오후 날씨
		String pmWthr = isNotEmpty(cwDailyReport.getPmWthr()) ? cwDailyReport.getPmWthr() : "";
		// 최저온도
		String dlowstTmprtVal = isNotEmpty(cwDailyReport.getDlowstTmprtVal()) ? String.format("%s (˚C)", cwDailyReport.getDlowstTmprtVal()) : "";
		// 최고온도
		String dtopTmprtVal = isNotEmpty(cwDailyReport.getDtopTmprtVal()) ? String.format("%s (˚C)", cwDailyReport.getDtopTmprtVal()) : "";
		// 강수량
		String prcptRateStr = cwDailyReport.getPrcptRate() != null ? String.format("%d (mm)", cwDailyReport.getPrcptRate()) : "";
		// 강설량
		String snowRateStr = cwDailyReport.getSnowRate() != null ? String.format("%d (mm)", cwDailyReport.getSnowRate()) : "";
		// 당일 계획
		// null 인경우 0.0(double) 비교하다 오류남
		Double todayPlanBohalRate = cwDailyReport.getTodayPlanBohalRate();
		String todayPlanBohalRateStr = (todayPlanBohalRate != null && todayPlanBohalRate != 0.0) ? String.format("%s %%", cwDailyReport.getTodayPlanBohalRate()) : "0%";
		// 당일 실적
		Double todayArsltBohalRate = cwDailyReport.getTodayArsltBohalRate();
		String todayArsltBohalRateStr = (todayArsltBohalRate != null && todayArsltBohalRate != 0.0)? String.format("%s %%", cwDailyReport.getTodayArsltBohalRate()) : "0%";
		// 당일 대비
		String todayProcess = String.format("%s %%", cwDailyReport.getTodayProcess());
		// 누적 계획
		Double acmltPlanBohalRate = cwDailyReport.getAcmltPlanBohalRate();
		String acmltPlanBohalRateStr = (acmltPlanBohalRate != null && acmltPlanBohalRate != 0.0) ? String.format("%s %%", cwDailyReport.getAcmltPlanBohalRate()) : "0%";
		// 누적 실적
		Double acmltArsltBohalRate = cwDailyReport.getAcmltArsltBohalRate();
		String acmltArsltBohalRateStr = (acmltArsltBohalRate != null && acmltArsltBohalRate != 0.0)? String.format("%s %%", cwDailyReport.getAcmltArsltBohalRate()) : "0%";
		// 누적 대비
		String acmltProcess = String.format("%s %%", cwDailyReport.getAcmltProcess());
		// 주요안건
		String majorMatter = isNotEmpty(cwDailyReport.getMajorMatter()) ? cwDailyReport.getMajorMatter() : "";
		// 상세보기 링크
		String linkParams = String.format("/construction/dailyreport/detail?type=d&sType=d&pjtNo=%s&cntrctNo=%s&rId=%s", UserAuth.get(true).getPjtNo(), cntrctNo, cwDailyReport.getDailyReportId());
		// html 본문 생성
		edtrText = edtrText.replace("dailyReportDateFmt", dailyReportDateFmt)
				.replace("reportNo", reportNo)
				.replace("title", title)
				.replace("amWthr", amWthr)
				.replace("pmWthr", pmWthr)
				.replace("dlowstTmprtVal", dlowstTmprtVal)
				.replace("dtopTmprtVal", dtopTmprtVal)
				.replace("prcptRateStr", prcptRateStr)
				.replace("snowRateStr", snowRateStr)
				.replace("todayPlanBohalRateStr", todayPlanBohalRateStr)
				.replace("todayArsltBohalRateStr", todayArsltBohalRateStr)
				.replace("todayProcess", todayProcess)
				.replace("acmltPlanBohalRateStr", acmltPlanBohalRateStr)
				.replace("acmltArsltBohalRateStr", acmltArsltBohalRateStr)
				.replace("acmltProcess", acmltProcess)
				.replace("majorMatter", majorMatter)
				.replace("paramValue", linkParams);
		// 텍스트
		String text = """
		보고일자	%s
		보고서번호	%s
		제목	%s
		날씨오전	%s
		날씨오후	%s
		최저온도	%s
		최고온도	%s
		강수량	%s
		강설량	%s
		당일계획	%s
		당일실적	%s
		당일대비	%s
		누적계획	%s
		누적실적	%s
		누적대비	%s
		주요안건	%s
		""".formatted(dailyReportDateFmt, reportNo, title, amWthr, pmWthr, dlowstTmprtVal, dtopTmprtVal, prcptRateStr, snowRateStr, todayPlanBohalRateStr, todayArsltBohalRateStr, todayProcess, acmltPlanBohalRateStr, acmltArsltBohalRateStr, acmltProcess, majorMatter);

		String pjtName = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.approval.selectProjectName", cntrctNo);
		String apDocTitle = String.format("[%s] %s 작업일보", pjtName, dailyReportDate);

		// 전자결재 생성
		ApDoc savedApDoc = createApDoc(cwDailyReport.getCntrctNo(), apDocTitle, edtrText, text, DAILY_DOC);

		// 작업일보 -> 전자결재문서 id 저장
		cwDailyReport.setApDocId(savedApDoc.getApDocId());
		cwDailyReport.setApprvlReqId(usrId);
		cwDailyReport.setApprvlReqDt(LocalDateTime.now());
		cwDailyReportRepository.save(cwDailyReport);

		// 결재선 생성
		List<ApLine> savedApLineList = createApLine(apLineSetList, savedApDoc.getApDocNo(), savedApDoc.getApDocId());

		// 알림메시지
		eapprovalHelper.insertInitAlarm(savedApDoc.getApDocId(), savedApDoc.getApDocStats());

		// 전결체크
		boolean isDelegatable = checkDelegate(savedApDoc, savedApLineList);
		String apprvlStats = isDelegatable && savedApLineList.size() == 1 ? "A" : "E";
		String checkPgaiaUser = savedApLineList.get(0).getApUsrId();
		if(isDelegatable && savedApLineList.size() > 1) {
			checkPgaiaUser = savedApLineList.get(1).getApUsrId();
		}
		dailyreportService.updateApprovalStatus(cwDailyReport, usrId, apprvlStats);

		// API 통신조건 체크
		boolean toApi = checkApiCondition("작업일보", requestMap, isDelegatable, checkPgaiaUser, apprvlStats);

		if(toApi && !savedApLineList.isEmpty()) {
			// params 값 셋팅
			Map<String, Object> params = new HashMap<>();
			params.put("report", cwDailyReport);
			params.put("resources", resourceMap);
			params.put("reportType", DAILY_DOC);
			params.put("usrId", UserAuth.get(true).getUsrId());
			params.put("apDoc", savedApDoc);
			params.put("apLineList", savedApLineList);
			params.put("apDocStats", savedApDoc.getApDocStats());

			log.info("작업일보 API 통신 params : {}", params);

			sendParams(params);
		}
	}


	/**
	 * 월간보고 전자결재 생성
	 * @param prMonthlyReport
	 * @param requestMap
	 * @param resourceMap
	 */
	@Transactional
	public void insertMonthlyReportDoc(PrMonthlyReport prMonthlyReport, Map<String, Object> requestMap, Map<String, Object> resourceMap) {
		log.info("insertMonthlyReportDoc: 월간보고 승인 요청 진행 *****************\n" +
		"1. 대상 리포트 prMonthlyReport = {} \n" +
		"2. 승인요청 데이터 requestMap = {} \n" +
		"3. 월간보고 데이터 resourceMap = {}", prMonthlyReport, requestMap, resourceMap);

		String cntrctNo = (String) requestMap.get("cntrctNo");
		String usrId = (String) requestMap.get("usrId");

		List<MybatisOutput> apLineSetList = checkApLineSet(cntrctNo, MONTHLY_DOC);

		String edtrText = getHtmlForm("form/approval/monthlyreport.html");

		// 보고년월
		String reportYm = prMonthlyReport.getReportYm();
		// 보고일자
		String monthlyReportDate = prMonthlyReport.getMonthlyReportDate();
		// 제목
		String title = prMonthlyReport.getTitle();
		// 금월추진사항
		String thisMonthPromotion = prMonthlyReport.getThisMonthPromotion() != null ? prMonthlyReport.getThisMonthPromotion() : "";
		// 차월추진계획
		String nextMonthPlan = prMonthlyReport.getThisMonthPromotion() != null ? prMonthlyReport.getNextMonthPlan() : "";
		// 주요안건
		String rmrkCntnts = prMonthlyReport.getRmrkCntnts();
		// 상세보기 링크
		String linkParams = String.format("/progress/monthlyreport/detail?type=view&pjtNo=%s&cntrctNo=%s&cntrctChgId=%s&monthlyReportId=%s", UserAuth.get(true).getPjtNo(), cntrctNo, prMonthlyReport.getCntrctChgId(), prMonthlyReport.getMonthlyReportId());
		// html 본문 생성
		edtrText = edtrText.replace("reportYm", reportYm)
				.replace("monthlyReportDate", monthlyReportDate)
				.replace("title", title)
				.replace("thisMonthPromotion", thisMonthPromotion)
				.replace("nextMonthPlan", nextMonthPlan)
				.replace("rmrkCntnts", rmrkCntnts)
				.replace("paramValue", linkParams);
		// 텍스트
		String text = """
		보고년월	%s
		보고일자	%s
		제목	%s
		금월추진사항	%s
		차월추진계획	%s
		주요안건	%s
		""".formatted(reportYm, monthlyReportDate,title,thisMonthPromotion,nextMonthPlan,rmrkCntnts);

		String pjtName = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.approval.selectProjectName", cntrctNo);
		String apDocTitle = String.format("[%s] %s 월간보고", pjtName, reportYm);

		// 전자결재 생성
		ApDoc savedApDoc = createApDoc(cntrctNo, apDocTitle, edtrText, text, MONTHLY_DOC);

		// 월간보고 -> 전자결재문서 id 저장
		prMonthlyReport.setApDocId(savedApDoc.getApDocId());
		prMonthlyReport.setApprvlReqId(usrId);
		prMonthlyReport.setApprvlReqDt(LocalDateTime.now());
		prMonthlyReportRepository.save(prMonthlyReport);

		// 결재선 생성
		List<ApLine> savedApLineList = createApLine(apLineSetList, savedApDoc.getApDocNo(), savedApDoc.getApDocId());

		// 알림메시지
		eapprovalHelper.insertInitAlarm(savedApDoc.getApDocId(), savedApDoc.getApDocStats());

		// 전결체크
		boolean isDelegatable = checkDelegate(savedApDoc, savedApLineList);
		String apprvlStats = isDelegatable && savedApLineList.size() == 1 ? "A" : "E"; // 주간보고 승인상태 세팅
		String checkPgaiaUser = savedApLineList.get(0).getApUsrId();
		if(isDelegatable && savedApLineList.size() > 1) {
			checkPgaiaUser = savedApLineList.get(1).getApUsrId();
		}
		monthlyreportService.updateApprovalStatus(prMonthlyReport, usrId, apprvlStats);

		// API 통신조건 체크
		boolean toApi = checkApiCondition("월간보고", requestMap, isDelegatable, checkPgaiaUser, apprvlStats);

		// API 통신
		if(toApi && !savedApLineList.isEmpty()) {
			//params 값 셋팅
			Map<String, Object> params = new HashMap<>();

			params.put("report", prMonthlyReport);
			params.put("resources", resourceMap);
			params.put("reportType", MONTHLY_DOC);
			params.put("usrId", usrId);
			params.put("apDoc", savedApDoc);
			params.put("apLineList", savedApLineList);
			params.put("apDocStats", savedApDoc.getApDocStats());

			log.info("월간보고 API 통신 params : {}", params);

			sendParams(params);

		}

	}


	/**
	 * 품질검측 검측요청 -> 결재문서 및 결재선 생성
	 * @param qualityInspection
	 * @param cmnCdNmKrn
	 */
	@Transactional
	public void insertQualityIspDoc(CwQualityInspection qualityInspection, String cmnCdNmKrn, boolean toApi, Map<String, Object> resourceMap) {
		List<MybatisOutput> apLineSetList = checkApLineSet(qualityInspection.getCntrctNo(), QUALITY_ISP_DOC);

		String edtrText = getHtmlForm("form/approval/qualityinspection.html");

		// 계약번호
		String cntrctNo = qualityInspection.getCntrctNo();
		// 품질검측 번호
		String qltyIspId = qualityInspection.getQltyIspId();
		// 문서번호
		String ispDocNo = qualityInspection.getIspDocNo();
		// 검측요청일자
		String ispReqDt = qualityInspection.getIspReqDt() != null ? qualityInspection.getIspReqDt().toString() : "";
		String ispReqDtFmt = ispReqDt.length() >= 10 ? ispReqDt.substring(0, 10) : "";
		// 위치
		String ispLct = isNotEmpty(qualityInspection.getIspLct()) ? qualityInspection.getIspLct() : "";
		// 검측부위
		String ispPart = isNotEmpty(qualityInspection.getIspPart()) ? qualityInspection.getIspPart() : "";
		// Activity 명
		Map<String, Object> sqlParams = new HashMap<String, Object>();
		sqlParams.put("qltyIspId", qltyIspId);
		sqlParams.put("cntrctNo", cntrctNo);

		List<String> activityNmList = mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.approval.selectActivityNmForQuality", sqlParams);

		StringBuilder stringBuilder = new StringBuilder();

		if (!activityNmList.isEmpty()) {
			for (String activity : activityNmList) {
				stringBuilder.append(activity).append("<br>");
			}
		}

		String activityNm = stringBuilder.toString();
		// 검측사항
		String ispIssue = isNotEmpty(qualityInspection.getIspIssue()) ? qualityInspection.getIspIssue() : "";
		// 상세보기 링크
		String linkParams = String.format("/construction/qualityinspection/addResult?type=d&mode=create&pjtNo=%s&cntrctNo=%s&qltyIspId=%s",	UserAuth.get(true).getPjtNo(), cntrctNo, qltyIspId);
		// html 본문생성
		edtrText = edtrText.replace("ispDocNo", ispDocNo)
				.replace("ispReqDtFmt", ispReqDtFmt)
				.replace("ispLct", ispLct)
				.replace("cmnCdNmKrn", cmnCdNmKrn)
				.replace("ispPart", ispPart)
				.replace("activityNm", activityNm)
				.replace("ispIssue", ispIssue)
				.replace("paramValue", linkParams);
		// 텍스트
		String text = """
		문서번호	%s
		검측요청일자	%s
		위치	%s
		공종	%s
		검측 부위	%s
		Activity명	%s
		검측사항	%s
		""".formatted(ispDocNo, ispReqDtFmt, ispLct, cmnCdNmKrn, ispPart, activityNm, ispIssue);

		String pjtName = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.approval.selectProjectName", cntrctNo);
		// 문서 제목
		String apDocTitle = String.format("[%s] %s - 품질 검측 요청(문서번호: %s)", pjtName, cmnCdNmKrn, ispDocNo);

		// 전자결재 생성
		ApDoc savedApDoc = createApDoc(cntrctNo, apDocTitle, edtrText, text, QUALITY_ISP_DOC);

		// 검측요청 -> 전자결재문서 id 저장
		qualityInspection.setIspApDocId(savedApDoc.getApDocId());
		cwQualityInspectionRepository.save(qualityInspection);

		// 결재선 생성
		List<ApLine> savedApLineList = createApLine(apLineSetList, savedApDoc.getApDocNo(), savedApDoc.getApDocId());

		// 알림메시지
		eapprovalHelper.insertInitAlarm(savedApDoc.getApDocId(), savedApDoc.getApDocStats());

		// API 통신
		if (toApi && !savedApLineList.isEmpty()) {
			// params 값 셋팅
			Map<String, Object> params = new HashMap<>();
			log.info("params : {}", params);

			params.put("report", qualityInspection);
			params.put("reportType", QUALITY_ISP_DOC);
			params.put("usrId", UserAuth.get(true).getUsrId());
			params.put("apDoc", savedApDoc);
			params.put("apLineList", savedApLineList);
			params.put("apDocStats", savedApDoc.getApDocStats());
			///////////////////////////// 인서트할 리소스 넣기 /////////////////////////////
			params.put("activity", resourceMap.get("activity"));
			params.put("checkList", resourceMap.get("checkList"));
			params.put(("cntqltyLists"), resourceMap.get("cntqltyLists"));
			params.put("photo", resourceMap.get("photo"));
			params.put("qualityFileInfo", resourceMap.get("qualityFileInfo"));
			params.put("photoFileInfo", resourceMap.get("photoFileInfo"));

			log.info("품질검측 검측요청 API 통신 params : {}", params);

			sendParams(params);

		}
	}


	/**
	 * 품질검측 결재요청 -> 결재문서 및 결재선 생성
	 * @param qualityInspection
	 * @param cmnCdNmKrn	-> 전자결재 양식에 들어가는 공종명
	 * @param rsltCdKrn	-> 전자결재 양식에 들어가는 검측결과명
	 * @param resourceMap
	 * @param requestMap
	 */
	@Transactional
	public void insertQualityAppDoc(CwQualityInspection qualityInspection, String cmnCdNmKrn, String rsltCdKrn, Map<String, Object> resourceMap, Map<String, Object> requestMap) {

		log.info("insertQualityAppDoc: 품질검측 결재요청 승인 요청 진행 *****************\n" +
		"1. 대상 리포트 qualityInspection = {} \n" +
		"2. 승인요청 데이터 requestMap = {} \n" +
		"3. 품질검측 데이터 resourceMap = {}", qualityInspection, requestMap, resourceMap);

		String cntrctNo = (String) requestMap.get("cntrctNo");
		String usrId = (String) requestMap.get("usrId");

		List<MybatisOutput> apLineSetList = checkApLineSet(qualityInspection.getCntrctNo(), QUALITY_APP_DOC);

		String edtrText = getHtmlForm("form/approval/qualityapproval.html");

		// 품질검측 번호
		String qltyIspId = qualityInspection.getQltyIspId();
		// 품질검측 문서번호
		String ispDocNo = qualityInspection.getIspDocNo();
		// 검측요청일자
		String ispReqDt = qualityInspection.getIspReqDt() != null ? qualityInspection.getIspReqDt().toString() : "";
		String ispReqDtFmt = ispReqDt.length() >= 10 ? ispReqDt.substring(0, 10) : "";
		// 위치
		String ispLct = isNotEmpty(qualityInspection.getIspLct()) ? qualityInspection.getIspLct() : "";
		// 검측부위
		String ispPart = isNotEmpty(qualityInspection.getIspPart()) ? qualityInspection.getIspPart() : "";
		// Activity 명
		Map<String, Object> sqlParams = new HashMap<String, Object>();
		sqlParams.put("qltyIspId", qltyIspId);
		sqlParams.put("cntrctNo", cntrctNo);

		List<String> activityNmList = mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.approval.selectActivityNmForQuality", sqlParams);

		StringBuilder stringBuilder = new StringBuilder();
		if (!activityNmList.isEmpty()) {
			for (String activity : activityNmList) {
				stringBuilder.append(activity).append("<br>");
			}
		}
		String activityNm = stringBuilder.toString();
		// 검측사항
		String ispIssue = isNotEmpty(qualityInspection.getIspIssue()) ? qualityInspection.getIspIssue() : "";
		// 검측결과 문서번호
		String rsltDocNo = qualityInspection.getRsltDocNo();
		// 검측 일자
		String rsltDt = qualityInspection.getRsltDt() != null ? qualityInspection.getRsltDt()+"" : null;
		String rsltDtFmt = rsltDt != null ? rsltDt.substring(0, 10) : "";
		// 검측 결과
		String rsltCd = rsltCdKrn;
		// 지시사항
		String ordeOpinin = qualityInspection.getOrdeOpnin() != null ? qualityInspection.getOrdeOpnin() : "";
		// 상세보기 링크
		String linkParams = String.format("/construction/qualityinspection/addPayment?pjtNo=%s&cntrctNo=%s&qltyIspId=%s", UserAuth.get(true).getPjtNo(), cntrctNo, qltyIspId);
		// html 본문생성
		edtrText = edtrText.replace("ispDocNo", ispDocNo)
				.replace("ispReqDtFmt", ispReqDtFmt)
				.replace("ispLct", ispLct)
				.replace("cmnCdNmKrn", cmnCdNmKrn)
				.replace("ispPart", ispPart)
				.replace("activityNm", activityNm)
				.replace("ispIssue", ispIssue)
				.replace("rsltDocNo", rsltDocNo)
				.replace("rsltDtFmt", rsltDtFmt)
				.replace("rsltCd", rsltCd)
				.replace("ordeOpinin", ordeOpinin)
				.replace("paramValue", linkParams);

		String text = """
		품질검측 문서번호	%s
		검측요청일자	%s
		위치	%s
		공종	%s
		검측 부위	%s
		Activity명	%s
		검측사항	%s
		검측결과 문서번호	%s
		검측일자	%s
		검측결과	%s
		지시사항	%s
		""".formatted(ispDocNo, ispReqDtFmt, ispLct, cmnCdNmKrn, ispPart, activityNm, ispIssue, rsltDocNo, rsltDtFmt, rsltCd, ordeOpinin);

		String pjtName = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.approval.selectProjectName", cntrctNo);
		String apDocTitle = String.format("[%s] %s- 검측 결과 결재 요청(문서번호: %s)", pjtName, cmnCdNmKrn, ispDocNo);

		// 전자결재 생성
		ApDoc savedApDoc = createApDoc(cntrctNo, apDocTitle, edtrText, text, QUALITY_APP_DOC);

		// 품질검측 결재요청 -> 전자결재문서 id 저장
		qualityInspection.setApDocId(savedApDoc.getApDocId());
		qualityInspection.setApReqId(usrId);
		qualityInspection.setApReqDt(LocalDateTime.now());
		
		// 기존 결재 재요청 시
		if(qualityInspection.getApprvlId() != null) {
			qualityInspection.setApprvlId(null);
			qualityInspection.setApprvlDt(null);
		}
		cwQualityInspectionRepository.save(qualityInspection);

		// 결재선 생성
		List<ApLine> savedApLineList = createApLine(apLineSetList, savedApDoc.getApDocNo(), savedApDoc.getApDocId());

		// 알림메시지
		eapprovalHelper.insertInitAlarm(savedApDoc.getApDocId(), savedApDoc.getApDocStats());

		// 전결체크
		boolean isDelegatable = checkDelegate(savedApDoc, savedApLineList);
		String apprvlStats = isDelegatable && savedApLineList.size() == 1 ? "A" : "E";
		String checkPgaiaUser = savedApLineList.get(0).getApUsrId();
		if(isDelegatable && savedApLineList.size() > 1) {
			checkPgaiaUser = savedApLineList.get(1).getApUsrId();
		}
		qualityinspectionService.updateApprovalStatus(qualityInspection, usrId, apprvlStats, null,"APP");

		// API 통신조건 체크
		boolean toApi = checkApiCondition("품질검측 결재요청", requestMap, isDelegatable, checkPgaiaUser, apprvlStats);

		// API 통신
		if(toApi && !savedApLineList.isEmpty()) {
			//params 값 셋팅
			Map<String, Object> params = new HashMap<>();

			params.put("report", qualityInspection);
			params.put("reportType", QUALITY_APP_DOC);
			params.put("usrId", UserAuth.get(true).getUsrId());
			params.put("apDoc", savedApDoc);
			params.put("apLineList", savedApLineList);
			params.put("apDocStats", savedApDoc.getApDocStats());
			///////////////////////////// 인서트할 리소스 넣기 /////////////////////////////
			params.put("activity", resourceMap.get("activity"));
			params.put("checkList", resourceMap.get("checkList"));
			params.put(("cntqltyLists"), resourceMap.get("cntqltyLists"));
			params.put("photo", resourceMap.get("photo"));
			params.put("qualityFileInfo", resourceMap.get("qualityFileInfo"));
			params.put("photoFileInfo", resourceMap.get("photoFileInfo"));


			log.info("품질검측 결재요청 API 통신 params : {}", params);

			sendParams(params);

		}
	}



	/**
	 * 안전점검 점검결과 작성 요청 -> 결재문서 및 결재선 생성
	 * @param safety
	 * @param output
	 * @param list
	 */
	@Transactional
	public void insertSafetyAppDoc(CwSafetyInspection safety, SafetymgmtMybatisParam.SafetyOutput output, boolean toApi, List<CwSafetyInspectionList> list, Map<String, Object> resourceMap) {
		List<MybatisOutput> apLineSetList = checkApLineSet(output.getCntrctNo(), SAFETY_REP_DOC);

		String edtrText = getHtmlForm("form/approval/safetyreport.html");

		// 계약번호
		String cntrctNo = output.getCntrctNo();
		// 안전점검 번호
		String inspectionNo = output.getInspectionNo();
		// 안전점검 문서번호
		String ispDocNo = output.getIspDocNo();
		// 점검일
		String ispDt = output.getIspDt() != null ? output.getIspDt() + "" : null;
		String ispDtFmt = ispDt != null ? ispDt.substring(0, 10) : "";
		// 제목
		String title = output.getTitle() != null ? output.getTitle() : "";
		// 공종1
		String cnsttyNm1 = output.getCnsttyNm1() != null ? output.getCnsttyNm1() : "";
		// 공종2
		String cnsttyNm2 = output.getCnsttyNm2() != null ? output.getCnsttyNm2() : "";

		StringBuilder stringBuilder = new StringBuilder();

		if (!list.isEmpty()) {
			for (CwSafetyInspectionList item : list) {
				stringBuilder.append("<tr>");
				stringBuilder.append("<td style='text-align:center;'>").append(item.getIspLstNo()).append("</td>");
				stringBuilder.append("<td style='text-align:center;'>").append(item.getCnsttyNm()).append("</td>");
				stringBuilder.append("<td>").append(item.getIspDscrpt()).append("</td>");
				stringBuilder.append("</tr>");
			}
		}

		// 상세보기 링크
		String linkParams = String.format("/safetymgmt/check/add/result?cntrctNo=%s&inspectionNo=%s", cntrctNo, inspectionNo);
		// html 본문생성
		edtrText = edtrText.replace("ispDocNo", ispDocNo)
				.replace("ispDtFmt", ispDtFmt)
				.replace("title", title)
				.replace("cnsttyNm1", cnsttyNm1)
				.replace("cnsttyNm2", cnsttyNm2)
				.replace("paramValue", linkParams)
				.replace("{{inspectionTableRows}}", stringBuilder.toString());

		String text = """
				점검번호	%s
				점검일	%s
				제목	%s
				대공종	%s
				공종	%s
				""".formatted(ispDocNo, ispDtFmt, title, cnsttyNm1, cnsttyNm2, linkParams);

		String pjtName = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.approval.selectProjectName", cntrctNo);
		String apDocTitle = String.format("[%s] %s - 안전 점검 점검결과 작성 요청(문서번호: %s)", pjtName, title, ispDocNo);

		// 전자결재 생성
		ApDoc savedApDoc = createApDoc(cntrctNo, apDocTitle, edtrText, text, SAFETY_REP_DOC);

		// 안전점검 항목 저장
		safety.setRepApDocId(savedApDoc.getApDocId());
		safetyRepository.save(safety);

		// 결재선 생성
		List<ApLine> savedApLineList = createApLine(apLineSetList, savedApDoc.getApDocNo(), savedApDoc.getApDocId());

		// 알림메시지
		eapprovalHelper.insertInitAlarm(savedApDoc.getApDocId(), savedApDoc.getApDocStats());

		// API 통신
		if (toApi && !savedApLineList.isEmpty()) {
			// params 값 셋팅
			Map<String, Object> params = new HashMap<>();

			params.put("report", safety);
			params.put("reportType", SAFETY_REP_DOC);
			params.put("usrId", UserAuth.get(true).getUsrId());
			params.put("apDoc", savedApDoc);
			params.put("apLineList", savedApLineList);
			params.put("apDocStats", savedApDoc.getApDocStats());
			///////////////////////////// 인서트할 리소스 넣기 /////////////////////////////
			params.put("inspectionlist", resourceMap.get("inspectionlist"));
			params.put("standardLists", resourceMap.get("standardLists"));
			params.put("photo", resourceMap.get("photo"));
			params.put("safetyFileInfo", resourceMap.get("safetyFileInfo"));

			log.info("안전점검 점검결과 작성 요청 API 통신 params : {}", params);

			sendParams(params);
		}
	}

	/**
	 * 안전점검 승인요청 -> 결재문서 및 결재선 생성
	 * @param output -> 전자결재 양식에 들어가는 데이터들
	 * @param safety -> 안전점검 실제 데이터
	 * @param resourceMap
	 */
	@Transactional
	public void insertSafetyIspDoc(SafetymgmtMybatisParam.SafetyOutput output, CwSafetyInspection safety, Map<String, Object> requestMap, Map<String, Object> resourceMap) {

		log.info("insertSafetyIspDoc: 안전점검 승인 요청 진행 *****************\n" +
		"1. 대상 리포트 CwSafetyInspection = {} \n" +
		"2. 승인요청 데이터 requestMap = {} \n" +
		"3. 안전점검 데이터 resourceMap = {}", safety, requestMap, resourceMap);

		String cntrctNo = (String) requestMap.get("cntrctNo");
		String usrId = (String) requestMap.get("usrId");

		List<MybatisOutput> apLineSetList = checkApLineSet(output.getCntrctNo(), SAFETY_DOC);

		String edtrText = getHtmlForm("form/approval/safety.html");

		// 안전점검 번호
		String inspectionNo = output.getInspectionNo();
		// 안전점검 문서번호
		String ispDocNo = output.getIspDocNo();
		// 점검일
		String ispDt = output.getIspDt() != null ? output.getIspDt()+"" : null;
		String ispDtFmt = ispDt != null ? ispDt.substring(0, 10) : "";
		// 제목
		String title = output.getTitle() != null ? output.getTitle() : "";
		// 공종1
		String cnsttyNm1 = output.getCnsttyNm1() != null ? output.getCnsttyNm1() : "";
		// 공종2
		String cnsttyNm2 = output.getCnsttyNm2() != null ? output.getCnsttyNm2() : "";
		// 상세보기 링크
		String linkParams = String.format("/safetymgmt/check/add/result?pjtNo=%s&cntrctNo=%s&inspectionNo=%s", UserAuth.get(true).getPjtNo(), cntrctNo, inspectionNo);
		// 본문생성
		edtrText = edtrText.replace("ispDocNo", ispDocNo)
				.replace("ispDtFmt", ispDtFmt)
				.replace("title", title)
				.replace("cnsttyNm1", cnsttyNm1)
				.replace("cnsttyNm2", cnsttyNm2)
				.replace("paramValue", linkParams);

		String text = """
		점검번호	%s
		점검일	%s
		제목	%s
		대공종	%s
		공종	%s
		""".formatted(ispDocNo, ispDtFmt, title, cnsttyNm1, cnsttyNm2, linkParams);

		String pjtName = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.approval.selectProjectName", cntrctNo);
		String apDocTitle = String.format("[%s] %s - 안전 점검 승인 요청(문서번호: %s)", pjtName, title, ispDocNo);

		// 전자결재 생성
		ApDoc savedApDoc = createApDoc(cntrctNo, apDocTitle, edtrText, text, SAFETY_DOC);

		// 안전점검 항목 저장
		safety.setApDocId(savedApDoc.getApDocId());
		safety.setApReqId(usrId);
		safety.setApReqDt(LocalDateTime.now());
		safetyRepository.save(safety);

		// 결재선 생성
		List<ApLine> savedApLineList = createApLine(apLineSetList, savedApDoc.getApDocNo(), savedApDoc.getApDocId());

		// 알림메시지
		eapprovalHelper.insertInitAlarm(savedApDoc.getApDocId(), savedApDoc.getApDocStats());

		// 전결체크
		boolean isDelegatable = checkDelegate(savedApDoc, savedApLineList);
		String apprvlStats = isDelegatable && savedApLineList.size() == 1 ? "A" : "E";
		String checkPgaiaUser = savedApLineList.get(0).getApUsrId();
		if(isDelegatable && savedApLineList.size() > 1) {
			checkPgaiaUser = savedApLineList.get(1).getApUsrId();
		}
		safetymgmtService.updateSafetyApprovalStatus(safety, usrId, apprvlStats, null);

		// API 통신조건 체크
		boolean toApi = checkApiCondition("안전점검", requestMap, isDelegatable, checkPgaiaUser, apprvlStats);

		// API 통신
		if(toApi && !savedApLineList.isEmpty()) {
			//params 값 셋팅
			Map<String, Object> params = new HashMap<>();

			params.put("report", safety);
			params.put("reportType", SAFETY_DOC);
			params.put("usrId", UserAuth.get(true).getUsrId());
			params.put("apDoc", savedApDoc);
			params.put("apLineList", savedApLineList);
			params.put("apDocStats", savedApDoc.getApDocStats());
			///////////////////////////// 인서트할 리소스 넣기 /////////////////////////////
			params.put("inspectionlist", resourceMap.get("inspectionlist"));
			params.put("standardLists", resourceMap.get("standardLists"));
			params.put("photo", resourceMap.get("photo"));
			params.put("safetyFileInfo", resourceMap.get("safetyFileInfo"));

			log.info("안전점검 승인요청 API 통신 params : {}", params);

			sendParams(params);

		}
	}


	/**
	 * 안전지적서 승인요청 -> 결재문서 및 결재선 생성
	 * @param sadtag
	 * @param dfccyTypKnm -> 전자결재 양식에 들어가는 결함유형명
	 * @param pstatsKnm -> 전자결재 양식에 들어가는 진행상태명
	 * @param requestMap
	 */
	@Transactional
	public void insertSadtagIspDoc(CwSadtag sadtag, String dfccyTypKnm, String pstatsKnm, Map<String, Object> requestMap) {

		log.info("insertSadtagIspDoc: 안전지적서 승인 요청 진행 *****************\n" +
		"1. 대상 리포트 CwSadtag = {} \n" +
		"2. 승인요청 데이터 requestMap = {} \n", sadtag, requestMap);

		String cntrctNo = (String) requestMap.get("cntrctNo");
		String usrId = (String) requestMap.get("usrId");

		List<MybatisOutput> apLineSetList = checkApLineSet(sadtag.getCntrctNo(), SADTAG_DOC);

		String edtrText = getHtmlForm("form/approval/sadtag.html");

		// 안전점검 번호
		String sadtagNo = sadtag.getSadtagNo();
		// 안전점검 문서번호
		String sadtagDocNo = sadtag.getSadtagDocNo();
		// 제목
		String title = sadtag.getTitle() != null ? sadtag.getTitle() : "";
		// 발견일자
		String findDt = sadtag.getFindDt() != null ? sadtag.getFindDt()+"" : null;
		String findDtFmt = findDt != null ? findDt.substring(0, 10) : "";
		// 발견자
		String findId = sadtag.getFindId() != null ? sadtag.getFindId() : "";
		// 결함-부적합
		String dfccyCntnts = sadtag.getDfccyCntnts() != null ? sadtag.getDfccyCntnts() : "";
		// 결함위치
		String dfccyLct = sadtag.getDfccyLct() != null ? sadtag.getDfccyLct() : "";
		// 조치기한
		String actnTmlmt = sadtag.getFindDt() != null ? sadtag.getFindDt()+"" : null;
		String actnTmlmtFmt = actnTmlmt != null ? actnTmlmt.substring(0, 10) : "";
		// 조치일자
		String actnDt = sadtag.getActnDt() != null ? sadtag.getActnDt()+"" : null;
		String actnDtFmt = actnDt != null ? actnDt.substring(0, 10) : "";
		// 조치자
		String actnId = sadtag.getActnId() != null ? sadtag.getActnId() : "";
		// 조치결과
		String actnRslt = sadtag.getActnRslt() != null ? sadtag.getActnRslt() : "";
		// 진행상태
		String pstats = pstatsKnm == null ? "" : pstatsKnm;
		// 상세보기 링크
		String linkParams = String.format("/safetymgmt/sadtag/read?pjtNo=%s&cntrctNo=%s&sadtagNo=%s", UserAuth.get(true).getPjtNo(), cntrctNo, sadtagNo);
		// 본문생성
		edtrText = edtrText.replace("sadtagDocNo", sadtagDocNo)
				.replace("dfccyTypKnm", dfccyTypKnm)
				.replace("title", title)
				.replace("findDtFmt", findDtFmt)
				.replace("findId", findId)
				.replace("dfccyCntnts", dfccyCntnts)
				.replace("dfccyLct", dfccyLct)
				.replace("actnTmlmtFmt", actnTmlmtFmt)
				.replace("pstatsKnm", pstats)
				.replace("actnDtFmt", actnDtFmt)
				.replace("actnId", actnId)
				.replace("actnRslt", actnRslt)
				.replace("paramValue", linkParams);

		String text = """
		번호	%s
		타입	%s
		제목	%s
		발견일자	%s
		발견자	%s
		결함-부적합	%s
		결함위치	%s
		조치기한	%s
		진행상태	%s
		조치일자	%s
		조치자	%s
		조치결과	%s
		""".formatted(sadtagDocNo, dfccyTypKnm, title, findDtFmt, findId, dfccyCntnts, dfccyLct, actnTmlmtFmt, pstatsKnm, actnDtFmt, actnId, actnRslt);

		String pjtName = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.approval.selectProjectName", cntrctNo);
		String apDocTitle = String.format("[%s] %s - 안전 점검 승인 요청(문서번호: %s)", pjtName, title, sadtagDocNo);

		// 전자결재 생성
		ApDoc savedApDoc = createApDoc(cntrctNo, apDocTitle, edtrText, text, SADTAG_DOC);

		// 안전지적서 항목 저장
		sadtag.setApDocId(savedApDoc.getApDocId());
		sadtag.setApReqId(usrId);
		sadtag.setApReqDt(LocalDateTime.now());
		sadtagRepositories.save(sadtag);

		// 결재선 생성
		List<ApLine> savedApLineList = createApLine(apLineSetList, savedApDoc.getApDocNo(), savedApDoc.getApDocId());

		// 알림메시지
		eapprovalHelper.insertInitAlarm(savedApDoc.getApDocId(), savedApDoc.getApDocStats());

		// 전결체크
		boolean isDelegatable = checkDelegate(savedApDoc, savedApLineList);
		String apprvlStats = isDelegatable && savedApLineList.size() == 1 ? "A" : "E";
		String checkPgaiaUser = savedApLineList.get(0).getApUsrId();
		if(isDelegatable && savedApLineList.size() > 1) {
			checkPgaiaUser = savedApLineList.get(1).getApUsrId();
		}
		safetymgmtService.updateSadtagApprovalStatus(sadtag, usrId, apprvlStats, null);

		// API 통신조건 체크
		boolean toApi = checkApiCondition("안전지적서", requestMap, isDelegatable, checkPgaiaUser, apprvlStats);

		// API 통신
		if(toApi && !savedApLineList.isEmpty()) {
			//params 값 셋팅
			Map<String, Object> params = new HashMap<>();
			log.info("params : {}", params);

			params.put("report", sadtag);
			params.put("reportType", SADTAG_DOC);
			params.put("usrId", UserAuth.get(true).getUsrId());
			params.put("apDoc", savedApDoc);
			params.put("apLineList", savedApLineList);
			params.put("apDocStats", savedApDoc.getApDocStats());

			log.info("안전지적서 승인요청 API 통신 params : {}", params);

			sendParams(params);

		}
	}


	/**
	 * 주간공정보고 -> 결재문서 및 결재선 생성
	 * @param prWeeklyReport
	 * @param requestMap
	 * @param resourceMap
	 */
	@Transactional
	public void insertWeeklyReportDoc(PrWeeklyReport prWeeklyReport, Map<String, Object> requestMap, Map<String, Object> resourceMap) {
		log.info("insertWeeklyReportDoc: 주간보고 승인 요청 진행 *****************\n" +
				"1. 대상 리포트 prWeeklyReport = {} \n" +
				"2. 승인요청 데이터 requestMap = {} \n" +
				"3. 주간보고 데이터 resourceMap = {}", prWeeklyReport, requestMap, resourceMap);

		String pjtNo = (String) requestMap.get("pjtNo");
		String cntrctNo = (String) requestMap.get("cntrctNo");
		String usrId = (String) requestMap.get("usrId");

		List<MybatisOutput> apLineSetList = checkApLineSet(cntrctNo, EapprovalHelper.WEEKLY_DOC);

		String edtrText = getHtmlForm("form/approval/weeklyreport.html");

		// 보고기준일
		String reportDate = prWeeklyReport.getReportDate();
		// 보고일자
		String weeklyReportDate = prWeeklyReport.getWeeklyReportDate();
		// 제목
		String title = prWeeklyReport.getTitle();
		// 금주월추진사항
		String thisWeekPromotion = prWeeklyReport.getThisWeekPromotion();
		// 차주추진계획
		String nextWeekPlan = prWeeklyReport.getNextWeekPlan();
		// 주요안건
		String rmrkCntnts = prWeeklyReport.getRmrkCntnts();
		// 상세보기 링크
		String linkParams = String.format("/progress/weeklyreport/detail?pjtNo=%s&cntrctNo=%s&cntrctChgId=%s&weeklyReportId=%s", pjtNo, cntrctNo, prWeeklyReport.getCntrctChgId(), prWeeklyReport.getWeeklyReportId());
		// html 본문 생성
		edtrText = edtrText.replace("reportDate", reportDate)
				.replace("weeklyReportDate", weeklyReportDate)
				.replace("title", title)
				.replace("thisWeekPromotion", thisWeekPromotion)
				.replace("nextWeekPlan", nextWeekPlan)
				.replace("rmrkCntnts", rmrkCntnts)
				.replace("paramValue", linkParams);
		// 텍스트
		String text = """
		보고기준일	%s
		보고일자	%s
		제목	%s
		금주추진사항	%s
		차주추진계획	%s
		주요안건	%s
		""".formatted(reportDate, weeklyReportDate,title,thisWeekPromotion,nextWeekPlan,rmrkCntnts);

		String pjtName = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.approval.selectProjectName", cntrctNo);
		String apDocTitle = String.format("[%s] %s 주간보고", pjtName, reportDate);

		// 전자결재 생성
		ApDoc savedApDoc = createApDoc(cntrctNo, apDocTitle, edtrText, text, WEEKLY_DOC);

		// 주간보고 -> 전자결재문서 id 저장
		prWeeklyReport.setApDocId(savedApDoc.getApDocId());
		prWeeklyReport.setApprvlReqId(usrId);
		prWeeklyReport.setApprvlReqDt(LocalDateTime.now());
		prWeeklyReportRepository.save(prWeeklyReport);

		// 결재선 생성
		List<ApLine> savedApLineList = createApLine(apLineSetList, savedApDoc.getApDocNo(), savedApDoc.getApDocId());

		// 알림메시지
		eapprovalHelper.insertInitAlarm(savedApDoc.getApDocId(), savedApDoc.getApDocStats());

		// 전결체크
		boolean isDelegatable = checkDelegate(savedApDoc, savedApLineList);
		String apprvlStats = isDelegatable && savedApLineList.size() == 1 ? "A" : "E"; // 주간보고 승인상태 세팅
		String checkPgaiaUser = savedApLineList.get(0).getApUsrId();
		if(isDelegatable && savedApLineList.size() > 1) {
			checkPgaiaUser = savedApLineList.get(1).getApUsrId();
		}
		weeklyreportService.updateApprovalStatus(prWeeklyReport, usrId, apprvlStats);

		// API 통신조건 체크
		boolean toApi = checkApiCondition("주간보고", requestMap, isDelegatable, checkPgaiaUser, apprvlStats);

		if(toApi && !savedApLineList.isEmpty()) {
			//params 값 셋팅
			Map<String, Object> params = new HashMap<>();
			params.put("report", prWeeklyReport);
			params.put("resources", resourceMap);
			params.put("reportType", WEEKLY_DOC);
			params.put("usrId", usrId);
			params.put("apDoc", savedApDoc);
			params.put("apLineList", savedApLineList);
			params.put("apDocStats", savedApDoc.getApDocStats());

			log.info("주간보고 API 통신 params : {}", params);

			sendParams(params);

		}
	}


	/**
	 * 감리일지 승인요청 -> 결재문서 및 결재선 생성
	 * @param report
	 * @param toApi
	 * @param resourceMap
	 */
	@Transactional
	public void insertInspectionReport(CwInspectionReport report, boolean toApi, Map<String, Object> resourceMap) {
		List<MybatisOutput> apLineSetList = checkApLineSet(report.getCntrctNo(), INSPECTION_DOC);

		String edtrText = getHtmlForm("form/approval/inspectionreport.html");

		// 계약번호
		String cntrctNo = report.getCntrctNo();
		// 작업보고 번호
		Long dailyReportId = report.getDailyReportId();
		// 보고일자
		String dailyReportDate = report.getDailyReportDate();
		// 보고서 번호
		String reportNo = report.getReportNo();
		// 제목
		String title = report.getTitle() != null ? report.getTitle() : "";
		// 날씨오전
		String amWthr = report.getAmWthr() != null ? report.getAmWthr() : "";
		// 날씨오후
		String pmWthr = report.getPmWthr() != null ? report.getPmWthr() : "";
		// 최저기온
		String dlowstTmprtVal = report.getDlowstTmprtVal() != null ? report.getDlowstTmprtVal() : "";
		// 최저기온
		String dtopTmprtVal = report.getDtopTmprtVal() != null ? report.getDtopTmprtVal() : "";
		// 강수량
		String prcptRate = report.getPrcptRate() != null ? report.getPrcptRate().stripTrailingZeros().toPlainString() : "-";
		// 강설량
		String snowRate = report.getSnowRate() != null ? report.getSnowRate().stripTrailingZeros().toPlainString() : "-";
		// 누적계획보할율
		String acmltPlanBohalRate = report.getAcmltPlanBohalRate() != null ? report.getAcmltPlanBohalRate().stripTrailingZeros().toPlainString() : "-";
		// 누적실적보할율
		String acmltArsltBohalRate = report.getAcmltArsltBohalRate() != null ? report.getAcmltArsltBohalRate().stripTrailingZeros().toPlainString()	: "-";
		// 누적대비율
		String acmltProcess = report.getAcmltProcess() != null ? report.getAcmltProcess().stripTrailingZeros().toPlainString() : "-";
		// 당일계획보할율
		String todayPlanBohalRate = report.getTodayPlanBohalRate() != null ? report.getTodayPlanBohalRate().stripTrailingZeros().toPlainString() : "-";
		// 당일실적보할율
		String todayArsltBohalRate = report.getTodayArsltBohalRate() != null ? report.getTodayArsltBohalRate().stripTrailingZeros().toPlainString() : "-";
		// 강설량
		String todayProcess = report.getTodayProcess() != null ? report.getTodayProcess().stripTrailingZeros().toPlainString() : "-";
		// 주요안건
		String majorMatter = report.getMajorMatter() != null ? report.getMajorMatter() : "";

		// 상세보기 링크
		String linkParams = String.format("/construction/inspectionreport/getReport?pjtNo=%s&cntrctNo=%s&dailyReportId=%s", UserAuth.get(true).getPjtNo(), cntrctNo, dailyReportId);

		edtrText = edtrText
				.replace("dailyReportDate", dailyReportDate)
				.replace("reportNo", reportNo)
				.replace("title", title)
				.replace("amWthr", amWthr)
				.replace("pmWthr", pmWthr)
				.replace("dlowstTmprtVal", dlowstTmprtVal)
				.replace("dtopTmprtVal", dtopTmprtVal)
				.replace("prcptRate", prcptRate)
				.replace("snowRate", snowRate)
				.replace("todayPlanBohalRate", todayPlanBohalRate)
				.replace("todayArsltBohalRate", todayArsltBohalRate)
				.replace("todayProcess", todayProcess)
				.replace("acmltPlanBohalRate", acmltPlanBohalRate)
				.replace("acmltArsltBohalRate", acmltArsltBohalRate)
				.replace("acmltProcess", acmltProcess)
				.replace("majorMatter", majorMatter)
				.replace("paramValue", linkParams);

		String text = """
		보고일자: %s
		보고서 번호: %s
		제목: %s

		[기상현황]
		- 날씨 오전: %s
		- 날씨 오후: %s
		- 최저 온도: %s℃
		- 최고 온도: %s℃
		- 강수량: %s mm
		- 강설량: %s mm

		[공정현황]
		- 당일: 계획 %s%% / 실적 %s%% / 대비 %s%%
		- 누적: 계획 %s%% / 실적 %s%% / 대비 %s%%

		[주요안건]
		%s
		""".formatted(
				dailyReportDate,
				reportNo,
				title,
				amWthr,
				pmWthr,
				dlowstTmprtVal,
				dtopTmprtVal,
				prcptRate,
				snowRate,
				todayPlanBohalRate,
				todayArsltBohalRate,
				todayProcess,
				acmltPlanBohalRate,
				acmltArsltBohalRate,
				acmltProcess,
				majorMatter
		);

		String pjtName = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.approval.selectProjectName", cntrctNo);
		String apDocTitle = String.format("[%s] %s - 감리일지 승인 요청 (문서번호: %s)", pjtName, title, reportNo);

		// 전자결재 생성
		ApDoc savedApDoc = createApDoc(cntrctNo, apDocTitle, edtrText, text, INSPECTION_DOC);

		// 감리일지 전자결재문서 id 저장
		report.setApDocId(savedApDoc.getApDocId());
		inspectionReportRepository.save(report);

		// 결재선 생성
		List<ApLine> savedApLineList = createApLine(apLineSetList, savedApDoc.getApDocNo(), savedApDoc.getApDocId());

		// 알림메시지
		eapprovalHelper.insertInitAlarm(savedApDoc.getApDocId(), savedApDoc.getApDocStats());

		// API 통신
		if(toApi && !savedApLineList.isEmpty()) {
			//params 값 셋팅
			Map<String, Object> params = new HashMap<>();

			params.put("report", report);
			params.put("reportType", INSPECTION_DOC);
			params.put("usrId", UserAuth.get(true).getUsrId());
			params.put("apDoc", savedApDoc);
			params.put("apLineList", savedApLineList);
			params.put("apDocStats", savedApDoc.getApDocStats());
			////////////////////// 리소스 //////////////////////////
			params.put("activity", resourceMap.get("activity"));
			params.put("photo", resourceMap.get("photo"));
			params.put("inspectionFileInfo", resourceMap.get("inspectionFileInfo"));

			log.info("감리일지 API 통신 params : {}", params);

			sendParams(params);

		}
	}


	/**
	 * 안전일지 -> 결재문서 및 결재선 생성
	 * @param diary
	 * @param cntrctNo
	 * @param resourceMap
	 */
	public void insertSafetyDiaryDoc(Map<String, Object> diary,String cntrctNo, Map<String, Object> resourceMap) {

		// 1. 결재라인
		List<MybatisOutput> apLineSetList = checkApLineSet(cntrctNo, SAFETY_DIARY_DOC);

		// 2. HTML 본문 생성
		String edtrText = getHtmlForm("form/approval/safetydiary.html");

		// 3. 사용 변수 정의
		// FIXME PARAM
		String diaryId = diary.get("safe_diary_id").toString();		// 안전일지 ID
		String repoNo = diary.get("repo_no").toString();			// 보고서 번호
		String cntrctNm = diary.get("cntrct_nm").toString();		// 계약명
		String reportDate = diary.get("repo_dt").toString();		// 보고일자
		String usrId = diary.get("usrId").toString();				// 사용자 명
		String pjtName = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.approval.selectProjectName", cntrctNo);

		String apDocTitle = String.format("[%s] - 안전일지 승인 요청(문서번호: %s)", pjtName, repoNo);

		String linkParams = String.format("/safety/safety-diary/detail?pjtNo=%s&cntrctNo=%s&diaryId=%s", UserAuth.get(true).getPjtNo(), cntrctNo, diaryId);


		edtrText = edtrText
				.replace("cntrctNm", cntrctNm)
				.replace("reportDate", reportDate)
				.replace("paramValue", linkParams);

		String text = """
    			안전일지(순회점검) 및 교육일지
				계약명:	%s
				보고일자: %s
				""".formatted(cntrctNm, reportDate);

		// 4. 전자결재 생성
		ApDoc savedApDoc = createApDoc(cntrctNo, apDocTitle, edtrText, text, SAFETY_DIARY_DOC);

		// 5. 안전일지 -> 전자결재문서 id 저장
		Map<String, Object> updateParams = new HashMap<>();
		updateParams.put("apDocId", savedApDoc.getApDocId());
		updateParams.put("cntrctNo", Objects.toString(diary.get("cntrct_no"), ""));
		updateParams.put("safetyDiaryId", Objects.toString(diary.get("safe_diary_id"), ""));
		diary.put("ap_doc_id", savedApDoc.getApDocId());
		safetyDiaryIntegrationService.updateApprovalDocId(updateParams);

		// 결재선 생성
		List<ApLine> savedApLineList = createApLine(apLineSetList, savedApDoc.getApDocNo(), savedApDoc.getApDocId());

		// 알림 메시지
		eapprovalHelper.insertInitAlarm(savedApDoc.getApDocId(), savedApDoc.getApDocStats());

		// 전결체크
		boolean isDelegatable = checkDelegate(savedApDoc, savedApLineList);
		String apprvlStats = isDelegatable && savedApLineList.size() == 1 ? "A" : "E"; // 주간보고 승인상태 세팅
		String checkPgaiaUser = savedApLineList.get(0).getApUsrId();
		if(isDelegatable && savedApLineList.size() > 1) {
			checkPgaiaUser = savedApLineList.get(1).getApUsrId();
		}

		if ("A".equals(apprvlStats)) {
			// 전결 처리시 업데이트
			safetyDiaryIntegrationService.updateApprovalStatus(diaryId, cntrctNo, usrId, Map.of("apprvlStats", apprvlStats));
		}

		// API 통신조건 체크
		boolean toApi = checkApiCondition("안전일지", diary, isDelegatable, checkPgaiaUser, apprvlStats);

		// FIXME API 통신 (TEST 필요)
		if(toApi && !savedApLineList.isEmpty()) {
			log.info("insertSafetyDiaryDoc: savedApDoc = {}", savedApDoc);
			//params 값 셋팅
			Map<String, Object> params = new HashMap<>();

			params.put("diary", diary);
			params.put("resources", resourceMap);
			params.put("reportType", SAFETY_DIARY_DOC);
			params.put("usrId", UserAuth.get(true).getUsrId());
			params.put("apDoc", savedApDoc);
			params.put("apLineList", savedApLineList);
			params.put("apDocStats", savedApDoc.getApDocStats());

			log.info("insertSafetyDiaryDoc: API 통신 진행 params = {}", params);

			sendParams(params);

		}
	}

	/**
	 * 주요자재 검수요청서 결재요청 -> 결재문서 및 결재선 생성
	 * @param cwMainmtrlReqfrm
	 * @param requestMap
	 * @param cnsttyNm -> 공종
	 * @param rsltNm -> 판정
	 */
	@Transactional
	public void insertMainmtrlReqfrmAppDoc(CwMainmtrlReqfrm cwMainmtrlReqfrm, Map<String, Object> requestMap, String cnsttyNm, String rsltNm) {

		log.info("insertMainmtrlReqfrmAppDoc: 주요자재 검수요청서 승인 요청 진행 *****************\n" +
		"1. 대상 리포트 cwMainmtrlReqfrm = {} \n" +
		"2. 승인요청 데이터 requestMap = {}", cwMainmtrlReqfrm, requestMap);

		String pjtNo = (String) requestMap.get("pjtNo");
		String cntrctNo = (String) requestMap.get("cntrctNo");
		String isApiYn = (String) requestMap.get("isApiYn");
		String pjtDiv = (String) requestMap.get("pjtDiv");
		String usrId = (String) requestMap.get("usrId");

		List<MybatisOutput> apLineSetList = checkApLineSet(cwMainmtrlReqfrm.getCntrctNo(), MAINMTRL_REQFRM_DOC);

		String edtrText = getHtmlForm("form/approval/mainmtrlreqfrm.html");

		// 주요자재 번호
		String reqfrmNo = cwMainmtrlReqfrm.getReqfrmNo();

		// 주요자재 문서번호
		String docNo = cwMainmtrlReqfrm.getDocNo();
		// 검수요청일자
		String reqDt = cwMainmtrlReqfrm.getReqDt() != null ? cwMainmtrlReqfrm.getReqDt().toString() : "";
		String reqDtFmt = reqDt.length() >= 10 ? reqDt.substring(0, 10) : "";
		// 수신
		String rxcorpNm = cwMainmtrlReqfrm.getRxcorpNm();
		// 품명
		String prdNm = cwMainmtrlReqfrm.getPrdnm();
		// 제조회사명
		String markNm = cwMainmtrlReqfrm.getMakrNm();
		// 비고
		String rmrk = isNotEmpty(cwMainmtrlReqfrm.getRmrk()) ? cwMainmtrlReqfrm.getRmrk() : "";

		// 검수일자
		String cmDt = cwMainmtrlReqfrm.getCmDt() != null ? cwMainmtrlReqfrm.getCmDt()+"" : null;
		String cmDtFmt = cmDt != null ? cmDt.substring(0, 10) : "";
		// 자재검사 의견
		String rsltOpinin = isNotEmpty(cwMainmtrlReqfrm.getRsltOpnin()) ? cwMainmtrlReqfrm.getRsltOpnin() : "";

		// 상세보기 링크
		String linkParams = String.format("/construction/mainmtrlreqfrm/getMtrlReqfrm?pjtNo=%s&cntrctNo=%s&reqfrmNo=%s&returnType=A", UserAuth.get(true).getPjtNo(), cntrctNo, reqfrmNo);
		// html 본문생성
		edtrText = edtrText.replace("docNo", docNo)
				.replace("reqDtFmt", reqDtFmt)
				.replace("rxcorpNm", rxcorpNm)
				.replace("cnsttyNm", cnsttyNm)
				.replace("prdNm", prdNm)
				.replace("markNm", markNm)
				.replace("rmrk", rmrk)
				.replace("rsltNm", rsltNm)
				.replace("cmDtFmt", cmDtFmt)
				.replace("rsltOpinin", rsltOpinin)
				.replace("paramValue", linkParams);

		String text = """
		문서번호	%s
		검수요청일자	%s
		수신	%s
		공종	%s
		품명	%s
		제조회사명	%s
		비고	%s
		판정	%s
		검수일	%s
		자재검사 의견	%s
		""".formatted(docNo, reqDtFmt, rxcorpNm, cnsttyNm, prdNm, markNm, rmrk, rsltNm, cmDtFmt, rsltOpinin);

		String pjtName = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.approval.selectProjectName", cntrctNo);
		String apDocTitle = String.format("[%s] 주요자재 검수요청서 결재 요청(문서번호: %s)", pjtName, docNo);

		// 전자결재 생성
		ApDoc savedApDoc = createApDoc(cntrctNo, apDocTitle, edtrText, text, MAINMTRL_REQFRM_DOC);

		// 주요자재 결재요청 -> 전자결재문서 id 저장
		cwMainmtrlReqfrm.setApDocId(savedApDoc.getApDocId());
		cwMainmtrlReqfrm.setApReqId(usrId);
		cwMainmtrlReqfrm.setApReqDt(LocalDateTime.now());

		// 기존 결재 재요청 시
		if(cwMainmtrlReqfrm.getApprvlId() != null) {
			cwMainmtrlReqfrm.setApprvlId(null);
			cwMainmtrlReqfrm.setApprvlDt(null);
		}
		mainmtrlReqfrmRepository.save(cwMainmtrlReqfrm);

		// 결재선 생성
		List<ApLine> savedApLineList = createApLine(apLineSetList, savedApDoc.getApDocNo(), savedApDoc.getApDocId());

		// 알림메시지
		eapprovalHelper.insertInitAlarm(savedApDoc.getApDocId(), savedApDoc.getApDocStats());

		// 전결체크

		boolean isDelegatable = checkDelegate(savedApDoc, savedApLineList);
		String apprvlStats = isDelegatable && savedApLineList.size() == 1 ? "A" : "E";
		mainmtrlReqfrmService.updateApprovalStatus(cwMainmtrlReqfrm, usrId, apprvlStats, null,"APP");
	}


	/**
	 * 문서 양식 html 조회
	 * @param path
	 * @return
	 */
	private String getHtmlForm(String path) {
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(path);
		if (inputStream == null) {
		   return null;
		}

		try (inputStream;
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
			String content = reader.lines().collect(Collectors.joining("\n"));

			if(content.isEmpty()) {
			   throw new GaiaBizException(ErrorType.NOT_FOUND, "문서 양식이 없습니다.");
			}
			return content;
		} catch (IOException e) {
			throw new GaiaBizException(ErrorType.INTERNAL_SERVER_ERROR, "문서를 읽는 중 오류가 발생했습니다.", e);
		}
	}


	/**
	 * API 통신
	 * @param params
	 */
	private void sendParams(Map<String, Object> params) {

		Map response = invokeCairos2Pgaia("GACA8200", params);

		if (!"00".equals( org.apache.commons.collections4.MapUtils.getString(response, "resultCode") ) ) {
			throw new GaiaBizException(ErrorType.INTERFACE, org.apache.commons.collections4.MapUtils.getString(response, "resultMsg"));
		}
	}



	/**
	 * 전자결재 생성
	 * @param cntrctNo
	 * @param apDocTitle
	 * @param edtrText
	 * @param text
	 * @param apType
	 * @return
	 */
	private ApDoc createApDoc(String cntrctNo, String apDocTitle, String edtrText, String text, String apType) {
		ApDoc apDoc = new ApDoc();
		apDoc.setApDocId(UUID.randomUUID().toString());
		apDoc.setFrmNo(8);
		apDoc.setFrmId("8");
		apDoc.setPjtNo(UserAuth.get(true).getPjtNo());
		apDoc.setCntrctNo(cntrctNo);
		apDoc.setPjtType(platform.toUpperCase());
		apDoc.setApDocTitle(apDocTitle);
		apDoc.setApDocEdtr(edtrText);
		apDoc.setApDocTxt(text);
		apDoc.setApUsrId(UserAuth.get(true).getUsrId());
		apDoc.setApLoginId(UserAuth.get(true).getLogin_Id());
		apDoc.setApAppDt(LocalDateTime.now());
		apDoc.setApDocStats("W");
		apDoc.setDltYn("N");
		apDoc.setApType(apType);
		apDocRepository.save(apDoc);
		return apDoc;

	}


	/**
	 * 설정할 결재선 조회
	 * @param cntrctNo
	 * @param apType
	 * @return
	 */
	public List<MybatisOutput> checkApLineSet(String cntrctNo, String apType) {
		MybatisInput input = MybatisInput.of().add("cntrctNo", cntrctNo)
				.add("apType", apType);
		List<MybatisOutput> apLineSetList = mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.approval.selectApLineSet", input);

		if(apLineSetList.isEmpty()) {
			throw new GaiaBizException(ErrorType.NO_DATA, "지정된 결재선이 없습니다.");
		}

		return apLineSetList;
	}


	/**
	 * 결재선 생성
	 * @param apLineSetList
	 * @param apDocNo
	 * @param apDocId
	 * @return
	 */
	private List<ApLine> createApLine(List<MybatisOutput> apLineSetList, Integer apDocNo, String apDocId) {
		List<ApLine> apLineList = new ArrayList<ApLine>();

		// 결재선 생성
		apLineSetList.forEach(user -> {
			ApLine apLine = new ApLine();
			apLine.setApId(UUID.randomUUID().toString());
			apLine.setApDocNo(apDocNo);
			apLine.setApDocId(apDocId);
			apLine.setApOrder(((Integer) user.get("ap_order")).shortValue());
			apLine.setApDiv((String)user.get("ap_div"));
			apLine.setApStats("I");
			apLine.setApUsrId((String)user.get("ap_usr_id"));
			apLine.setApLoginId((String)user.get("ap_login_id"));
			apLine.setApUsrOpnin("");
			apLineRepository.save(apLine);
			apLineList.add(apLine);
		});
		return apLineList;
	}


	/**
	 * null 체크
	 * @param str
	 * @return
	 */
	private boolean isNotEmpty(String str) {
		return str != null && !str.isEmpty();
	}


	/**
	 * 전자결재 문서 및 결재선 삭제
	 * @param apDocId
	 */
	public void deleteApDoc(String apDocId) {
		// 결재선 삭제
		deleteApLineByApDocId(apDocId);

		// 결재문서 삭제
		deleteApDocByApDocId(apDocId);
	}

	public void deleteApDocByApDocId(String apDocId) {
		ApDoc findApDoc = apDocRepository.findByApDocId(apDocId);
		apDocRepository.updateDelete(findApDoc);
	}

	public void deleteApLineByApDocId(String apDocId) {
		List<ApLine> findApLine = apLineRepository.findByApDocId(apDocId);
		apLineRepository.deleteAll(findApLine);
	}


	/**
	 * 전결여부 체크
	 * @param savedApDoc
	 * @param savedApLineList
	 * @return
	 */
	public boolean checkDelegate(ApDoc savedApDoc, List<ApLine> savedApLineList){
		if (savedApLineList == null || savedApLineList.isEmpty()) {
			return false;
		}

		String firstUsrId = savedApLineList.get(0).getApUsrId();
		String firstApDiv = savedApLineList.get(0).getApDiv();

		// 첫번째 결재자 = 로그인 사용자인지 체크
		boolean isDelegatable = firstUsrId.equals(UserAuth.get(true).getUsrId()) && "A".equals(firstApDiv);
		if (!isDelegatable) return false;

		savedApLineList.get(0).setApStats("A");

		int lineCount = savedApLineList.size();

		if (lineCount == 1) {
			// 결재선 1개: 전자결재 문서상태 W(대기) -> C(완료) 변경
			savedApDoc.setApDocStats("C");
			savedApDoc.setApCmpltDt(LocalDateTime.now());
		} else {
			String nextApDiv = savedApLineList.get(1).getApDiv();
			if ("A".equals(nextApDiv)) {
				// 결재선 1개 이상 & 다음 결재구분 A: 전자결재 문서상태 W(대기) -> I(진행) 변경
				savedApDoc.setApDocStats("I");
			} else if ("R".equals(nextApDiv)) {
				// 결재선 1개 이상 & 다음 결재구분 R: 전자결재 문서상태 W(대기) -> C(완료) 변경
				savedApDoc.setApDocStats("C");
				savedApDoc.setApCmpltDt(LocalDateTime.now());
			}
		}
		return true;
	};


	/**
	 * 첫번째 결재자 pgaia 사용자(관리관) 체크
	 * @param pjtNo
	 * @param cntrctNo
	 * @param apType
	 * @return
	 */
	public boolean checkPgaiaFirstApprover(String pjtNo, String cntrctNo, String apType) {
		Map<String, Object> checkParams = new HashMap<>();
		checkParams.put("pjtNo", pjtNo);
		checkParams.put("cntrctNo", cntrctNo);
		checkParams.put("apType", apType);
		return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.approval.checkPgaiaFirstApprover", checkParams);
	}



	/**
	 * Cairos ↔ PGAIA API 통신 여부 체크
	 *
	 * @param reportName    API 요청 문서
	 * @param isApiYn       API 사용 여부 (Y/N)
	 * @param pjtDiv        프로젝트 구분
	 * @param isDelegatable 전결 가능 여부
	 * @param apprvlStats   결재 상태
	 * @param pjtNo         프로젝트 번호
	 * @param checkPgaiaUser 다음 결재자 정보
	 * @return toApi        최종 API 통신 여부
	 */
	private boolean checkApiCondition(String reportName,
									  Map<String, Object> requestMap,
									  boolean isDelegatable,
									  String checkPgaiaUser,
									  String apprvlStats) {
		String isApiYn = (String) requestMap.get("isApiYn");
		String pjtDiv = (String) requestMap.get("pjtDiv");
		String pjtNo = (String) requestMap.get("pjtNo");

		// cairos API 통신 조건
		boolean isCairosApi = "CAIROS".equals(platform.toUpperCase()) && "Y".equals(isApiYn) && "P".equals(pjtDiv);

		// 다음 결재자가 PGAIA 유저인지
		boolean isPgaiaUser = draftService.checkPgaiaFirstApproverForDraft(pjtNo, checkPgaiaUser);

		// 최종 통신 여부
		boolean toApi = isCairosApi && (isPgaiaUser || (isDelegatable && "A".equals(apprvlStats)));

		log.info("[{} 승인요청 -> API 통신 여부 체크]\n" +
				 "  1. 다음 PGAIA결재자 여부: isPgaiaUser = {}\n" +
				 "  2. 전결여부: isDelegatable = {}, apprvlStats = {}\n" +
				 "  3. 카이로스 API통신 여부: isCairosApi = {}\n" +
				 "  4. toApi = {}",
				 reportName, isPgaiaUser, isDelegatable, apprvlStats, isCairosApi, toApi);

		return toApi;
	}


}
