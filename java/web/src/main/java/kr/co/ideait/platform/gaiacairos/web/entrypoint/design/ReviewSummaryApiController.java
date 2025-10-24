package kr.co.ideait.platform.gaiacairos.web.entrypoint.design;

import jakarta.validation.Valid;
import kr.co.ideait.platform.gaiacairos.comp.design.DesignComponent;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.report.reviewsummary.ReviewSummaryForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.tool.designreview.DesignReviewForm;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.GridResult;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.iframework.annotation.Description;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("api/design/reviewsummary")
public class ReviewSummaryApiController extends AbstractController {

    @Autowired
    DesignComponent designComponent;

    /*
     * 검토 요약 - 검토 목록 조회
     */
    @GetMapping("/reviewsummaryList")
    @Description(name = "설계 검토 목록 조회", description = "계약의 설계 검토 목록 조회", type = Description.TYPE.MEHTOD)
    public GridResult getSummaryList(CommonReqVo commonReqVo, @Valid ReviewSummaryForm.ReviewsummaryList summaryList,
                                     @CookieValue(name = "lang", required = false, defaultValue = "ko") String langInfo, UserAuth user) {

        return GridResult.ok(designComponent.getSummaryListData(summaryList.getCntrctNo(), summaryList.getSummaryType(),summaryList.getDsgnPhaseNoList(),summaryList.getRgstrIdList(),user.getUsrId(),langInfo,user.getUsrId()));
    }
}
