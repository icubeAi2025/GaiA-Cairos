package kr.co.ideait.platform.gaiacairos.web.entrypoint.common;

import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.iframework.annotation.Description.TYPE;
import kr.co.ideait.platform.gaiacairos.comp.system.service.CommonCodeService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmComCode;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/t")
public class TApiController extends AbstractController {
	
	//기초 데이터 관련 공통 요청
	@Autowired
	CommonCodeService commonCodeService;

	@GetMapping("/workType/list")
	@Description(name = "대공종 리스트 조회",description = "대공종 리스트를 조회한다", type =  TYPE.MEHTOD)
	public Result getWorkTypeList(CommonReqVo commonReqVo) {
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("대공종 리스트 조회");

		List<SmComCode> workTypeList = commonCodeService.getCommonCodeListByGroupCode("19a8bb53-74b4-405a-8d91-2b38555fc7d9");

		userLog.setResult("성공");
		
		return Result.ok().put("workTypeList", workTypeList);
	}

	@GetMapping("/demandAgency/list")
	@Description(name = "수요기관 리스트 조회",description = "수요기관 리스트를 조회한다", type =  TYPE.MEHTOD)
	public Result getDemandAgencyList(CommonReqVo commonReqVo) {
		return null;
	}
}














