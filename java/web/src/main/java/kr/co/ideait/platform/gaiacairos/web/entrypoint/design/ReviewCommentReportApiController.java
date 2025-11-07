package kr.co.ideait.platform.gaiacairos.web.entrypoint.design;

import jakarta.validation.Valid;
import kr.co.ideait.platform.gaiacairos.comp.design.DesignComponent;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.report.reviewcommentreport.ReviewCommentReportForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.tool.designreview.DesignReviewForm;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.iframework.annotation.Description;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("api/design/reviewcommentreport")
public class ReviewCommentReportApiController extends AbstractController {

	@Autowired
	DesignComponent designComponent;

	@PostMapping("/list")
    @Description(name = "설계 검토 목록 조회", description = "계약의 설계 검토 목록 조회", type = Description.TYPE.MEHTOD)
	public Result getReviewReportList(CommonReqVo commonReqVo, @RequestBody @Valid ReviewCommentReportForm.ReviewReportList designReviewListGet,
									  @CookieValue(name = "lang", required = false, defaultValue = "ko") String langInfo, UserAuth user) {
		;
		return Result.ok().put("report", designComponent.getReviewReportListData(designReviewListGet, langInfo,user.getUsrId()));
	}

	@PostMapping("/detail")
    @Description(name = "설계 검토 상세 조회", description = "계약의 설계 검토 상세 조회", type = Description.TYPE.MEHTOD)
	public Result getReviewReportDetail(CommonReqVo commonReqVo, 
			@RequestBody @Valid ReviewCommentReportForm.ReviewReportDetail designReviewListGet,
			@CookieValue(name = "lang", required = false, defaultValue = "ko") String langInfo, UserAuth user) {

		return Result.ok().put("reportDetail", designComponent.getDetailReviewReportData(designReviewListGet.getCntrctNo(),designReviewListGet.getDsgnNo(),langInfo,user.getUsrId()) );
	}
}
