package kr.co.ideait.platform.gaiacairos.web.entrypoint.design;

import kr.co.ideait.platform.gaiacairos.comp.design.DesignComponent;
import kr.co.ideait.platform.gaiacairos.comp.design.DesignEvaluationComponent;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.tool.evaluation.EvaluationForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.iframework.annotation.Description;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/design/evaluation")
public class EvaluationApiController extends AbstractController {

	@Autowired
	DesignEvaluationComponent designEvaluationComponent;


	/**
	 * 설계 평가 관리 목록조회(결함, 답변, 첨부파일, 설계도서)
	 * @param evaluationList
	 * @param langInfo
	 * @return
	 */
	@PostMapping("/list")
	@Description(name = "설계검토 평가 관리 목록조회", description = "평가할 설계 검토 의견 데이터(결함, 답변, 첨부파일, 설계도서) 및 개수 리턴", type = Description.TYPE.MEHTOD)
	public Result getEvaluationList(CommonReqVo commonReqVo, @RequestBody EvaluationForm.EvaluationList evaluationList,
									@CookieValue(name = "lang", required = false, defaultValue = "ko") String langInfo) {
		
		HashMap<String,Object> result = designEvaluationComponent.getEvaluationListData(evaluationList, langInfo, UserAuth.get(true).getUsrId());

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("설계검토 평가 관리 목록조회");
		systemLogComponent.addUserLog(userLog);

		return Result.ok().put("dsgnList", result.get("dsgnList"))
							.put("totalCount", result.get("totalCount"));
	}
	
	/**
	 * 평가 의견 상세조회 (등록된 모든 평가의견&첨부파일)
	 * @param evaluationDetail
	 * @return
	 */
	@PostMapping("/detail")
	@Description(name = "설계검토 평가 의견 상세조회", description = "평가 의견 상세조회(등록된 모든 평가의견&첨부파일)", type = Description.TYPE.MEHTOD)
	public Result getEvaluationDetail(CommonReqVo commonReqVo, @RequestBody EvaluationForm.EvaluationDetail evaluationDetail) {
		
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("설계검토 평가 목록조회");
		systemLogComponent.addUserLog(userLog);

		return Result.ok().put("evaList", designEvaluationComponent.getDetailEvaluationData(evaluationDetail));
	}
	
	
	/**
	 * 평가 의견 추가 - 의견, 첨부파일
	 * @param evaluationInsert
	 * @param files
	 * @return
	 */
	@PostMapping("/create")
	@Description(name = "설계검토 평가 의견 추가", description = "평가 의견 추가 - 의견, 첨부파일", type = Description.TYPE.MEHTOD)
	public Result insertEvaluation(CommonReqVo commonReqVo, @RequestPart("saveData") EvaluationForm.EvaluationInsert evaluationInsert,
									@RequestPart(value = "files", required = false) List<MultipartFile> files) {
		
		designEvaluationComponent.registEvaluation(evaluationInsert,files, commonReqVo);

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("설계검토 평가 의견 추가");
		systemLogComponent.addUserLog(userLog);

		return Result.ok();
	}
	
	/**
	 * 평가 의견 수정 - 의견, 첨부파일
	 * @param evaluationInsert
	 * @param files
	 * @return
	 */
	@PostMapping("/update")
	@Description(name = "설계검토 평가 의견 수정", description = "평가 의견 수정 - 의견, 첨부파일", type = Description.TYPE.MEHTOD)
	public Result updateEvaluation(CommonReqVo commonReqVo, @RequestPart("saveData") EvaluationForm.EvaluationInsert evaluationInsert,
									@RequestPart(value = "files", required = false) List<MultipartFile> files) {
		
		designEvaluationComponent.modifyEvaluation(evaluationInsert,files, commonReqVo);

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("설계검토 평가 의견 수정");
		userLog.setResult("성공");
		systemLogComponent.addUserLog(userLog);

		return Result.ok();
	}

	/**
	 * 평가 의견 삭제 - 의견, 첨부파일
	 * @param evaSeq
	 * @return
	 */
	@GetMapping("/delete/{evaSeq}")
	@Description(name = "설계검토 평가 의견 삭제", description = "평가 의견 삭제 - 의견, 첨부파일", type = Description.TYPE.MEHTOD)
	public Result deleteEvaluation(CommonReqVo commonReqVo, @PathVariable("evaSeq") String evaSeq) {

		designEvaluationComponent.removeEvaluation(evaSeq, commonReqVo);

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("설계검토 평가 의견 삭제");
		systemLogComponent.addUserLog(userLog);

		return Result.ok();
	}
	
	/**
	 * 평가 의견 일괄 삭제 - 의견, 첨부파일
	 * @param delEvaList
	 * @return
	 */
	@PostMapping("/delete-list")
	@Description(name = "설계검토 평가 의견 일괄 삭제", description = "평가 의견 일괄 삭제 - 의견, 첨부파일", type = Description.TYPE.MEHTOD)
	public Result deleteEvaluationList(CommonReqVo commonReqVo, @RequestBody EvaluationForm.EvaluationDeleteAll delEvaList) {
		
		designEvaluationComponent.removeEvaluationList(delEvaList.getDelEvaList(), commonReqVo);

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("설계검토 평가 의견 일괄 삭제");
		systemLogComponent.addUserLog(userLog);

		return Result.ok();
	}

	/**
	 * 평가자 결과 등록 (동의 / 동의안함)
	 * @param apprerUpdate
	 * @return
	 */
	@PostMapping("/update-apprer")
	@Description(name = "설계검토 평가자 결과 등록", description = "평가자 결과 등록 (동의 / 동의안함)", type = Description.TYPE.MEHTOD)
	public Result updateApprer(CommonReqVo commonReqVo, @RequestBody EvaluationForm.ApprerUpdate apprerUpdate) {

		designEvaluationComponent.modifyApprer(apprerUpdate.getCntrctNo(), apprerUpdate.getDsgnNo(), apprerUpdate.getApprerCd(), commonReqVo);

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("설계검토 평가자 결과 등록");
		systemLogComponent.addUserLog(userLog);

		return Result.ok();
	}
	
	
}
