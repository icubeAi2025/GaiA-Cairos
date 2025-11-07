package kr.co.ideait.platform.gaiacairos.comp.progress;

import kr.co.ideait.iframework.BizException;
import kr.co.ideait.platform.gaiacairos.comp.eapproval.service.ApprovalRequestService;
import kr.co.ideait.platform.gaiacairos.comp.progress.service.MonthlyreportService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractComponent;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.PrMonthlyReport;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.PrMonthlyReportRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.progress.monthlyreport.MonthlyreportMybatisParam;
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
public class MonthlyreportComponent extends AbstractComponent {

    @Autowired
    ApprovalRequestService approvalRequestService;

    @Autowired
    MonthlyreportService monthlyreportService;

    @Autowired
    PrMonthlyReportRepository prMonthlyReportRepository;

    /**
	 * 월간보고 전자결재 승인요청
	 * @param updateReportList
	 * @param cntrctNo
	 */
	@Transactional
	public void requestApprovalMonthlyreport(List<MonthlyreportMybatisParam.UpdateMonthlyreportInput> updateReportList, String cntrctNo, String isApiYn, String pjtDiv) {
		for (MonthlyreportMybatisParam.UpdateMonthlyreportInput report : updateReportList) {
			PrMonthlyReport findReport = prMonthlyReportRepository.findByCntrctChgIdAndMonthlyReportId(report.getCntrctChgId(), report.getMonthlyReportId()).orElse(null);

			if (findReport == null) {
				throw new BizException("월간공정보고 정보가 없습니다.");
			}

//			updateApprovalStatus(findReport, UserAuth.get(true).getUsrId(), "E");

            // 승인요청 시 필요 데이터
			Map<String, Object> requestMap = new HashMap<>();
			requestMap.put("pjtNo", UserAuth.get(true).getPjtNo());
			requestMap.put("cntrctNo", cntrctNo);
			requestMap.put("isApiYn", isApiYn);
			requestMap.put("pjtDiv", pjtDiv);
			requestMap.put("usrId", UserAuth.get(true).getUsrId());

            // 월간보고 데이터
			Map<String, Object> resourceMap = monthlyreportService.selectMonthlyReportResource(findReport.getCntrctChgId(), findReport.getMonthlyReportId());

			approvalRequestService.insertMonthlyReportDoc(findReport, requestMap, resourceMap);
		}

	}


    /**
	 * 월간보고 삭제
	 * @param delMonthlyreportList
	 */
	@Transactional
	public void deleteMonthlyreport(List<MonthlyreportMybatisParam.UpdateMonthlyreportInput> delMonthlyreportList) {
		delMonthlyreportList.forEach(delMonthlyreport -> {
			monthlyreportService.deleteMonthlyResource(delMonthlyreport.getCntrctChgId(), delMonthlyreport.getMonthlyReportId(), UserAuth.get(true).getUsrId());

			PrMonthlyReport findReport = prMonthlyReportRepository.findByCntrctChgIdAndMonthlyReportId(delMonthlyreport.getCntrctChgId(), delMonthlyreport.getMonthlyReportId()).orElse(null);

			if(findReport == null) {
				throw new BizException("삭제할 보고서가 없습니다.");
			}

			if(findReport.getApDocId() != null) {
				approvalRequestService.deleteApDoc(findReport.getApDocId());
			}

			prMonthlyReportRepository.updateDelete(findReport);
		});
	}
}
