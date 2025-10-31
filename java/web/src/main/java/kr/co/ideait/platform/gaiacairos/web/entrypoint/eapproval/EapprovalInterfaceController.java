package kr.co.ideait.platform.gaiacairos.web.entrypoint.eapproval;

import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.comp.eapproval.DraftComponent;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/interface/eapproval")
public class EapprovalInterfaceController {

    @Autowired
    DraftComponent draftComponent;

    /**
	 * 전자결재 서식 조회
	 * @param commonReqVo
	 * @return
	 */
	@GetMapping("/select-apFormList")
	@Description(name = "전자결재 서식 조회", description = "전자결재 서식 조회", type = Description.TYPE.MEHTOD)
	public Result getApFormList(CommonReqVo commonReqVo, @RequestParam Map<String, String> requestParams) {
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("전자결재 서식 조회");

		MybatisInput input = MybatisInput.of()
			.add("pjtNo", requestParams.get("pjtNo"))
			.add("cntrctNo", requestParams.get("cntrctNo"))
			.add("lang", requestParams.get("lang"));

		return Result.ok().put("apFormList", draftComponent.selectApFormList(input));
	}
}
