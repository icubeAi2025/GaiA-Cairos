package kr.co.ideait.platform.gaiacairos.comp.progress;

import kr.co.ideait.iframework.BizException;
import kr.co.ideait.platform.gaiacairos.comp.eapproval.service.ApprovalRequestService;
import kr.co.ideait.platform.gaiacairos.comp.progress.service.WeeklyreportService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractComponent;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.PrWeeklyReport;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.PrWeeklyReportRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.progress.weeklyreport.WeeklyreportMybatisParam;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class WeeklyreportComponent extends AbstractComponent {

    @Autowired
    PrWeeklyReportRepository prWeeklyReportRepository;

    @Autowired
    ApprovalRequestService approvalRequestService;

    @Autowired
    WeeklyreportService weeklyreportService;


    /**
	 * 주간보고 전자결재 승인요청
	 * @param updateReportList
	 * @param cntrctNo
	 */
	@Transactional
	public void requestApprovalWeeklyreport(List<WeeklyreportMybatisParam.UpdateWeeklyreportInput> updateReportList, String cntrctNo, String isApiYn, String pjtDiv) {
		updateReportList.forEach(report -> {
			PrWeeklyReport findReport = prWeeklyReportRepository.findByCntrctChgIdAndWeeklyReportId(report.getCntrctChgId(), report.getWeeklyReportId()).orElse(null);

			if(findReport == null) {
				throw new BizException("주간공정보고 정보가 없습니다.");
			}

//			updateApprovalStatus(findReport, UserAuth.get(true).getUsrId(), "E");

			// 승인요청 시 필요 데이터
			Map<String, Object> requestMap = new HashMap<>();
			requestMap.put("pjtNo", UserAuth.get(true).getPjtNo());
			requestMap.put("cntrctNo", cntrctNo);
			requestMap.put("isApiYn", isApiYn);
			requestMap.put("pjtDiv", pjtDiv);
			requestMap.put("usrId", UserAuth.get(true).getUsrId());

			// 주간보고 데이터
			Map<String, Object> resourceMap = weeklyreportService.selectWeeklyReportResource(findReport.getCntrctChgId(), findReport.getWeeklyReportId());

			approvalRequestService.insertWeeklyReportDoc(findReport, requestMap, resourceMap);
		});
	}


    /**
	 * 주간보고 삭제
	 * @param delList
	 */
	@Transactional
	public void deleteWeeklyreport(List<WeeklyreportMybatisParam.UpdateWeeklyreportInput> delList) {
		delList.forEach(report -> {
			weeklyreportService.deleteWeeklyActivityAndProgress(report.getCntrctChgId(), report.getWeeklyReportId(), UserAuth.get(true).getUsrId());

			PrWeeklyReport findReport = prWeeklyReportRepository.findByCntrctChgIdAndWeeklyReportId(report.getCntrctChgId(), report.getWeeklyReportId()).orElse(null);

			if(findReport == null) {
				throw new BizException("삭제할 보고서가 없습니다");
			}

			if(findReport.getApDocId() != null) {
				approvalRequestService.deleteApDoc(findReport.getApDocId());
			}

			prWeeklyReportRepository.updateDelete(findReport);

		});
	}
}
