package kr.co.ideait.platform.gaiacairos.web.entrypoint.system;

import kr.co.ideait.platform.gaiacairos.comp.system.ProjectInstallManageComponent;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmComCode;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.ProjectDto;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.pjinstall.PjInstallCriteria;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/system/pjinstall")
public class ProjectInstallManageApiController extends AbstractController {

	@Autowired
	ProjectInstallManageComponent component;

	@GetMapping("/main")
	public Result getMainPageData(CommonReqVo commonReqVo){
		HashMap<String,Object> result = component.getMainPageData();
		List<ProjectDto.ProjectInstall> pjInstallList = (List<ProjectDto.ProjectInstall>)result.get("pjInstallList");
		List<SmComCode> workTypeList =  (List<SmComCode>)result.get("workTypeList");
		List<HashMap<String,String>> dminsttTypeList =  (List<HashMap<String,String>>)result.get("dminsttTypeList");
		if(pjInstallList != null){
			if(workTypeList != null){
				return Result.ok()
						.put("pjInstallList",pjInstallList)
						.put("workTypeList",workTypeList)
						.put("dminsttTypeList",dminsttTypeList);
			}

		}
		return Result.nok(ErrorType.DATABSE_ERROR,"/api/system/pjinstall/list");
	}

	@GetMapping("/list")
	public Result getPjInstallList(PjInstallCriteria cri){
		return Result.ok().put("pjInstallList",component.getPjInstallList(cri));
	}

	@GetMapping("/detail/{plcReqNo}")
	public Result getDetailPageData(@PathVariable("plcReqNo") String plcReqNo){
		HashMap<String,Object> result = component.getDetailPageData(plcReqNo);
		return Result.ok(result);
	}

	@PostMapping("/update/{plcReqNo}")
	public Result updatePjInstallOpenStats(CommonReqVo commonReqVo, @PathVariable("plcReqNo") String plcReqNo,@RequestBody HashMap<String,String> params){
		return Result.ok().put("result", component.modifyOpenPstats(plcReqNo,params, commonReqVo));
	}

	@GetMapping("/{plcReqNo}")
	public Result getPjInstall(@PathVariable("plcReqNo") String plcReqNo){
		return Result.ok()
				.put("pjInstall",component.getProjectInstall(plcReqNo));
	}

}














