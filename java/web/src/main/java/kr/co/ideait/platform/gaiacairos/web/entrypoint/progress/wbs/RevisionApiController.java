package kr.co.ideait.platform.gaiacairos.web.entrypoint.progress.wbs;

import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.comp.api.service.ApiService;
import kr.co.ideait.platform.gaiacairos.comp.progress.RevisionComponent;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.PrRevision;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.GridResult;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.progress.wbs.revision.RevisionForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.progress.wbs.revision.RevisionMybatisParam;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@RestController
@RequestMapping("/api/progress/wbs")
public class RevisionApiController extends AbstractController {

	@Autowired
	ApiService apiService;

	@Autowired
	RevisionComponent revisionComponent;

	@Autowired
	RevisionForm revisionForm;

	/**
	 * 계약변경 조회
	 * @param commonReqVo
	 * @param cntrctNo
	 * @return
	 */
	@GetMapping("/revision/select-contractChange/{cntrctNo}")
	@Description(name = "계약변경 차수 조회", description = "계약변경 차수 조회", type = Description.TYPE.MEHTOD)
	public Result getContractChangeList(CommonReqVo commonReqVo, @PathVariable("cntrctNo") String cntrctNo) {
		return Result.ok().put("contractChange", revisionComponent.selectContractChange(cntrctNo));
	}

	/**
	 * Revision 목록 조회
	 * @param revisionList
	 * @return
	 */
	@PostMapping("/revision/list")
	@Description(name = "Revision 목록 조회", description = "Revision 목록 조회", type = Description.TYPE.MEHTOD)
	public GridResult getRevisionList(CommonReqVo commonReqVo, @RequestBody RevisionForm.RevisionList revisionList) {
		return GridResult.ok(revisionComponent.selectRevisionList(revisionForm.toRevisionListInput(revisionList)));
	}

	/**
	 * revision 생성
	 * @param prRevision
	 * @return
	 */
	@PostMapping("/revision/create")
	@Description(name = "revision 생성", description = "revision, wbs, activity 생성", type = Description.TYPE.MEHTOD)
	public Result insertRevision(CommonReqVo commonReqVo, @RequestBody PrRevision prRevision, @RequestParam(value="cntrctNo") String cntrctNo) {

		// param set
		Map<String, Object> vo = new HashMap<>();
		vo.put("usrId", commonReqVo.getUserId());
		vo.put("cntrctNo", cntrctNo);
		vo.put("isApiYn", commonReqVo.getApiYn());
		vo.put("pjtDiv", commonReqVo.getPjtDiv());

		revisionComponent.insertRevisionWbsActivity(prRevision, vo);

		return Result.ok().put("success", true);
	}

	/**
	 * revision 수정
	 * TODO CMIS → GAIA 연동여부
	 * TODO CMIS 포털 여부
	 * @param revisionUpdate
	 * @return
	 */
	@PostMapping("/revision/update")
	@Description(name = "revision 수정", description = "revision, wbs, activity 수정", type = Description.TYPE.MEHTOD)
	public Result updateRevision(CommonReqVo commonReqVo, @RequestBody RevisionForm.RevisionUpdate revisionUpdate, @RequestParam(value="cntrctNo") String cntrctNo) {

		// update 대상 조회
		PrRevision findRevision = revisionComponent.getRevision(revisionUpdate.getCntrctChgId(), revisionUpdate.getRevisionId());
		if(findRevision != null) {
			revisionForm.updatePrRevision(revisionUpdate, findRevision);

			Map<String, Object> vo = new HashMap<>();
			vo.put("usrId", commonReqVo.getUserId());
			vo.put("cntrctNo", cntrctNo);
			vo.put("isApiYn", commonReqVo.getApiYn());
			vo.put("pjtDiv", commonReqVo.getPjtDiv());
			revisionComponent.updateRevisionWbsActivity(findRevision, vo);
		} else {
			// msg.revision.005 - 수정할 대상이 없습니다.
			String msg = messageSource.getMessage("msg.revision.005", null, LocaleContextHolder.getLocale());
			throw new GaiaBizException(ErrorType.INTERNAL_SERVER_ERROR, msg);
		}

		return Result.ok().put("success", true);
	}
	
	/**
	 * Revision 삭제 (삭제 여부 업데이트)
	 * @param delRevision
	 * @return
	 */
	@PostMapping("/revision/delete")
	@Description(name = "revision 삭제", description = "revision 삭제", type = Description.TYPE.MEHTOD)
	public Result deleteRevisionList(CommonReqVo commonReqVo, @RequestBody RevisionForm.RevisionDeleteList delRevision) {
		List<RevisionMybatisParam.DeleteRevisionInput> deleteRevisionInput = revisionForm.toDeleteRevisionInput(delRevision.getDelRevisionList());
		revisionComponent.deleteRevision(deleteRevisionInput, Map.of(
				"usrId", commonReqVo.getUserId(),
				"isApiYn", commonReqVo.getApiYn(),
				"pjtDiv", commonReqVo.getPjtDiv()
		));
		return Result.ok();
	}


	/**
	 * Primavera 목록 조회 요청
	 *
	 * @return
	 */
	@PostMapping("/revision/primavera/message")
	@Description(name = "Primavera 목록 조회", description = "Primavera 목록 조회", type = Description.TYPE.MEHTOD)
	public Result postPrimaveraProjectList(CommonReqVo commonReqVo, @RequestBody @Valid RevisionForm.WorkTypeRequest workTypeRequest) {

		// post 메써드로 넘길 요청 본문 생성
        //var requestBody = new ObjectMapper().convertValue(workTypeRequest, Map.class);
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("login_id", Objects.requireNonNull(UserAuth.get(true)).getUsrId());
		requestBody.put("platform", platform);

		String workType = workTypeRequest.getWorkType();

		if ("GAPR0010".equals(workType)) {
			requestBody.put("workType", workType);
		}

		if ("GAPR0020".equals(workType)) {
			requestBody.put("workType", workType);
			requestBody.put("epsObjId", workTypeRequest.getEpsObjId());
		}

		if ("GAPR0030".equals(workType)) {
			requestBody.put("workType", workType);
			requestBody.put("projId", workTypeRequest.getProjId());
		}


		return apiService.primaveraApiGetPost(requestBody);
	}

}
