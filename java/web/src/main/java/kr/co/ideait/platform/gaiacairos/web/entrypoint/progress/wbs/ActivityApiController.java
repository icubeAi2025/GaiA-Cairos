package kr.co.ideait.platform.gaiacairos.web.entrypoint.progress.wbs;

import jakarta.validation.Valid;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.progress.wbs.activity.ActivityMybatisParam.ActivityListInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.progress.wbs.activity.ActivityMybatisParam.DeffecttrackingActivityInput;
import kr.co.ideait.platform.gaiacairos.comp.progress.service.ActivityService;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.progress.wbs.wbs.WbsMybatisParam.WbsListInput;
import kr.co.ideait.platform.gaiacairos.comp.progress.service.WbsService;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.progress.wbs.activity.ActivityForm;
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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/progress/wbs/activity")
public class ActivityApiController extends AbstractController {

	@Autowired
	ActivityService activityService;

	@Autowired
	ActivityForm activityForm;

	@Autowired
	WbsService wbsService;

	@Autowired
	WbsForm wbsForm;


	// wbs 트리 리스트
	@GetMapping("/treeList")
	@Description(name = "WBS 목록 조회", description = "계약의 차수별 WBS 목록 조회", type = Description.TYPE.MEHTOD)
	public Result getWbsTreeList(CommonReqVo commonReqVo, @Valid WbsForm.WbsListGet wbsListGet) {
		wbsListGet.setListType("tree");
		WbsListInput input = wbsForm.toWbsListInput(wbsListGet);
		return Result.ok().put("wbsList", wbsService.getWbsList(input));
	}

	// activity 그리드 리스트
	@GetMapping("/gridList")
	@Description(name = "Activity 목록 조회", description = "계약의 차수별 Activity 목록 조회", type = Description.TYPE.MEHTOD)
	public GridResult getWbsGridList(CommonReqVo commonReqVo, @Valid ActivityForm.ActivityListGet activityListGet) {
		String searchTerm = activityListGet.getSearchTerm();

		LocalDate day = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		String today = day.format(formatter);

		String firstDayOfWeek = day.with(DayOfWeek.MONDAY).format(formatter);
		String endDayOfWeek = day.with(DayOfWeek.SUNDAY).format(formatter);

		String firstDayOfMonth = day.withDayOfMonth(1).format(formatter);
		String endDayOfMonth = day.withDayOfMonth(day.lengthOfMonth()).format(formatter);

		if (searchTerm != null) {
			if (searchTerm.equals("today")) {
				activityListGet.setStartDate(today);
				activityListGet.setEndDate(today);
			} else if (searchTerm.equals("week")) {
				activityListGet.setStartDate(firstDayOfWeek);
				activityListGet.setEndDate(endDayOfWeek);
			} else if (searchTerm.equals("month")) {
				activityListGet.setStartDate(firstDayOfMonth);
				activityListGet.setEndDate(endDayOfMonth);
			}
		}
		ActivityListInput input = activityForm.toActivityListInput(activityListGet);
		return GridResult.ok(activityService.getActivityList(input));
	}

	// 결함추적관리 Activity 조회
	@GetMapping("/deffecttrackingActivity")
	@Description(name = "결함 추적관리 Activity 목록 조회", description = "결함관리의 결함별 Activity 목록 조회", type = Description.TYPE.MEHTOD)
	public GridResult getDeffecttrackingActivity(CommonReqVo commonReqVo, @Valid DeffecttrackingActivityInput input) {

		return GridResult
				.ok(activityService.getDeffecttrackingActivity(input));
	}
}
