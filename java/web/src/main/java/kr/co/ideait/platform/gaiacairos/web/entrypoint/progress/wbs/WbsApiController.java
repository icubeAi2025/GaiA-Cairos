package kr.co.ideait.platform.gaiacairos.web.entrypoint.progress.wbs;

import jakarta.validation.Valid;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.progress.wbs.wbs.WbsMybatisParam.WbsListInput;
import kr.co.ideait.platform.gaiacairos.comp.progress.service.WbsService;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.progress.wbs.wbs.WbsForm;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.GridResult;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.iframework.annotation.Description;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/progress/wbs/wbs")
public class WbsApiController extends AbstractController {

	@Autowired
	WbsService wbsService;

	@Autowired
	WbsForm wbsForm;

	// wbs 트리 리스트
	@GetMapping("/treeList")
	@Description(name = "WBS 목록 조회", description = "Tree용 계약의 차수별 WBS 목록 조회", type = Description.TYPE.MEHTOD)
	public Result getWbsTreeList(CommonReqVo commonReqVo, @Valid WbsForm.WbsListGet wbsListGet) {
		wbsListGet.setListType("tree");
		WbsListInput input = wbsForm.toWbsListInput(wbsListGet);
		return Result.ok().put("wbsList", wbsService.getWbsList(input));
	}

	// wbs 그리드 리스트
	@GetMapping("/grideList")
	@Description(name = "WBS 목록 조회", description = "Grid용 계약의 차수별 WBS 목록 조회", type = Description.TYPE.MEHTOD)
	public GridResult getWbsGridList(CommonReqVo commonReqVo, @Valid WbsForm.WbsListGet wbsListGet) {
		wbsListGet.setListType("grid");
		WbsListInput input = wbsForm.toWbsListInput(wbsListGet);
		return GridResult.ok(wbsService.getWbsList(input));
	}
}
