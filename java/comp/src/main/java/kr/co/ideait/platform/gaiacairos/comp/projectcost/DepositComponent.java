package kr.co.ideait.platform.gaiacairos.comp.projectcost;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;
import kr.co.ideait.platform.gaiacairos.comp.eapproval.service.ApprovalRequestService;
import kr.co.ideait.platform.gaiacairos.comp.projectcost.service.DepositService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractComponent;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwFrontMoney;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DepositComponent extends AbstractComponent {

    @Autowired
    DepositService depositService;

    @Autowired
    ApprovalRequestService approvalRequestService;

    /**
     * 선급금 및 공제금 승인 상태 업데이트
     */
    @Transactional
    public void updateDepositList(List<CwFrontMoney> cwfrontmoney, String isApiYn, String pjtDiv) {
        cwfrontmoney.forEach(id -> {
            CwFrontMoney cwFrontMoney = depositService.getDepositData(id.getCntrctNo(), id.getPpaymnySno());

            if (cwFrontMoney != null) {
                if (id.getApprvlStats().equals("E")) {

                    // 선금 및 공제금 리소스 데이터
                    Map<String, Object> resourceMap = new HashMap<>();
                    resourceMap = depositService.selectDepositResource(cwFrontMoney.getCntrctNo(),
                            cwFrontMoney.getPayprceSno());

                    // 선금 및 공제금 승인요청 시 필요 데이터
                    Map<String, Object> requestMap = new HashMap<>();
                    requestMap.put("pjtNo", UserAuth.get(true).getPjtNo());
                    requestMap.put("cntrctNo", cwFrontMoney.getCntrctNo());
                    requestMap.put("isApiYn", isApiYn);
                    requestMap.put("pjtDiv", pjtDiv);
                    requestMap.put("usrId", UserAuth.get(true).getUsrId());

                    approvalRequestService.insertDepositDoc(cwFrontMoney, resourceMap, requestMap);
                }
                if (id.getApprvlStats().equals("A") || id.getApprvlStats().equals("R")) {
                    depositService.updateDeposit(id.getApprvlStats(),
                            UserAuth.get(true).getUsrId(), LocalDateTime.now(), id.getCntrctNo(), id.getPpaymnySno());
                }
            }
        });
    }

    /**
     * 선급금 및 공제금 삭제
     */
    @Transactional
    public void delDeposit(List<CwFrontMoney> cwfrontmoney) {
        cwfrontmoney.forEach(id -> {
            CwFrontMoney cwFrontMoney = depositService.getDepositData(id.getCntrctNo(), id.getPpaymnySno());
            if (cwFrontMoney != null) {
                depositService.updateDelAcmtlPpaymnyAmt(id.getPpaymnyAmt(), id.getPayType() ,id.getPpaymnySno());
                depositService.delDeposit(id);

                if (cwFrontMoney.getApDocId() != null) {
                    approvalRequestService.deleteApDoc(cwFrontMoney.getApDocId());
                }
            }
        });
    }
}
