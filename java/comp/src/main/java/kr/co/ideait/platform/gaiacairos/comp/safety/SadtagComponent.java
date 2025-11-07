package kr.co.ideait.platform.gaiacairos.comp.safety;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.co.ideait.iframework.BizException;
import kr.co.ideait.platform.gaiacairos.comp.construction.service.QualityinspectionService;
import kr.co.ideait.platform.gaiacairos.comp.eapproval.service.ApprovalRequestService;
import kr.co.ideait.platform.gaiacairos.comp.mail.service.MailService;
import kr.co.ideait.platform.gaiacairos.comp.project.service.ContractstatusService;
import kr.co.ideait.platform.gaiacairos.comp.safety.service.SafetymgmtService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractComponent;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwSadtag;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.safety.CheckForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.safety.SadtagForm.Sadtag;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.util.FileService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SadtagComponent extends AbstractComponent {

    @Autowired
    SafetymgmtService safetyService;

    @Autowired
    ApprovalRequestService approvalRequestService;

    /**
     * 안전지적서 승인요청
     * 
     * @param sadtag
     * @param isApiYn
     * @param pjtDiv
     */
    @Transactional
    public void requestApprovalSadtag(Sadtag sadtag, String isApiYn, String pjtDiv) {
        CwSadtag output = safetyService.getSadTagData(sadtag.getCntrctNo(), sadtag.getSadtagNo());
        if (output == null) {
            throw new BizException("안전지적서 정보가 없습니다.");
        }

        Map<String, ?> outputString = safetyService.getSadtag(sadtag.getCntrctNo(), sadtag.getSadtagNo());
        String dfccyTypKnm = (String) outputString.get("dfccy_typ_knm");
        String pstatsKnm = (String) outputString.get("pstats_knm");

        // 안전지적서 승인요청 시 필요 데이터
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("pjtNo", UserAuth.get(true).getPjtNo());
        requestMap.put("cntrctNo", sadtag.getCntrctNo());
        requestMap.put("isApiYn", isApiYn);
        requestMap.put("pjtDiv", pjtDiv);
        requestMap.put("usrId", UserAuth.get(true).getUsrId());

        approvalRequestService.insertSadtagIspDoc(output, dfccyTypKnm, pstatsKnm, requestMap);

    }

    /**
     * 안전지적서 삭제
     */
    @Transactional
    public void deleteList(CwSadtag delete) {
        safetyService.deleteList(delete);
        if (delete.getApDocId() != null) {
            approvalRequestService.deleteApDoc(delete.getApDocId());
        }
    }

}
