package kr.co.ideait.platform.gaiacairos.web.entrypoint.design;

import kr.co.ideait.platform.gaiacairos.comp.design.DesignBackCheckComponent;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.tool.backcheck.BackCheckForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisOutput;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.iframework.annotation.Description;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/design/backCheck")
public class BackCheckApiConroller extends AbstractController {

	@Autowired
	DesignBackCheckComponent designBackCheckComponent;

	@Autowired
	BackCheckForm backCheckForm;
	
	
	/**
	 * 백체크 목록조회 (결함, 답변, 평가, 첨부파일, 설계도서)
	 * @param backCheckList
	 * @return
	 */
	@PostMapping("/list")
	@Description(name = "설계검토 백체크 목록조회", description = "백체크할 설계 검토 의견 데이터(결함, 답변, 평가, 첨부파일, 설계도서) 및 개수 리턴", type = Description.TYPE.MEHTOD)
	public Result getBackCheckList(CommonReqVo commonReqVo, @RequestBody BackCheckForm.BackCheckList backCheckList,
								   @CookieValue(name = "lang", required = false, defaultValue = "ko") String langInfo) {

		backCheckList.setUsrId(UserAuth.get(true).getUsrId());
		backCheckList.setLang(langInfo);

		int page = backCheckList.getPage();
		int size = backCheckList.getSize();

		Pageable pageable = PageRequest.of(page - 1, size);
		Page<MybatisOutput> pageData = designBackCheckComponent.getBackCheckListData(backCheckForm.toBackCheckListInput(backCheckList), pageable);

		Long totalCount = pageData.getTotalElements();

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("설계검토 백체크 목록조회");
		systemLogComponent.addUserLog(userLog);

		return Result.ok().put("dsgnList", pageData.getContent())
							.put("totalCount", totalCount);
	}

	/**
	 * 백체크 상세조회 (등록된 모든 백체크 의견&첨부파일)
	 * @param backCheckDetail
	 * @return
	 */
	@PostMapping("/detail")
	@Description(name = "설계검토 백체크 상세조회", description = "백체크 상세조회 (등록된 모든 백체크 의견&첨부파일)", type = Description.TYPE.MEHTOD)
	public Result getBackCheckDetail(CommonReqVo commonReqVo, @RequestBody BackCheckForm.BackCheckDetail backCheckDetail) {

		List<MybatisOutput> backchkList = designBackCheckComponent.getBackCheckData(backCheckDetail.getDsgnNo(), backCheckDetail.getDsgnPhaseNo());

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("설계검토 백체크 상세조회");
		systemLogComponent.addUserLog(userLog);
		
		return Result.ok().put("backchkList", backchkList);
	}

	/**
	 * 백체크 추가 - 의견, 첨부파일
	 * @param backCheckInsert
	 * @param files
	 * @return
	 */
	@PostMapping("/create")
	@Description(name = "설계검토 백체크 추가", description = "백체크 추가 - 의견, 첨부파일", type = Description.TYPE.MEHTOD)
	public Result insertBackCheck(CommonReqVo commonReqVo, @RequestPart("saveData") BackCheckForm.BackCheckInsert backCheckInsert,
									@RequestPart(value = "files", required = false) List<MultipartFile> files) {

		designBackCheckComponent.registBackCheck(backCheckInsert, files, commonReqVo);

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("설계검토 백체크 추가");
		systemLogComponent.addUserLog(userLog);

		return Result.ok();
	}
	
	/**
	 * 백체크 수정 - 의견, 첨부파일
	 * @param backCheckInsert
	 * @param files
	 * @return
	 */
	@PostMapping("/update")
	@Description(name = "설계검토 백체크 수정", description = "백체크 수정 - 의견, 첨부파일", type = Description.TYPE.MEHTOD)
	public Result updateBackCheck(CommonReqVo commonReqVo, @RequestPart("saveData") BackCheckForm.BackCheckInsert backCheckInsert,
									@RequestPart(value = "files", required = false) List<MultipartFile> files) {

		designBackCheckComponent.modifyBackCheck(backCheckInsert, files, commonReqVo);

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("설계검토 백체크 수정");
		systemLogComponent.addUserLog(userLog);
		
		return Result.ok();
	}

	/**
	 * 백체크 결과 등록 (미결 / 종결)
	 * @param backchkCdUpdate
	 * @return
	 */
	@PostMapping("/update-backchkCd")
	@Description(name = "설계검토 백체크 결과 등록", description = "백체크 결과 등록 (미결 / 종결)", type = Description.TYPE.MEHTOD)
	public Result updateBackchkCd(CommonReqVo commonReqVo, @RequestBody BackCheckForm.BackchkCdUpdate backchkCdUpdate) {

		designBackCheckComponent.modifyBackCheckResult(backchkCdUpdate.getCntrctNo(), backchkCdUpdate.getDsgnNo(), backchkCdUpdate.getBackchkCd(), commonReqVo);

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("설계검토 백체크 결과 등록");
		systemLogComponent.addUserLog(userLog);

		return Result.ok();
	}

	/**
	 * 백체크 삭제 - 의견, 첨부파일
	 * @param backSeq
	 * @return
	 */
	@GetMapping("/delete/{backSeq}")
	@Description(name = "설계검토 백체크 삭제", description = "백체크 삭제 - 의견, 첨부파일", type = Description.TYPE.MEHTOD)
	public Result deleteBackCheck(CommonReqVo commonReqVo, @PathVariable("backSeq") String backSeq) {

		designBackCheckComponent.removeBackCheck(backSeq, commonReqVo);

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("설계검토 백체크 삭제");
		systemLogComponent.addUserLog(userLog);

		return Result.ok();
	}
	
	/**
	 * 백체크 의견 일괄 삭제 - 의견, 첨부파일
	 * @param delList
	 * @return
	 */
	@PostMapping("/delete-list")
	@Description(name = "설계검토 백체크 의견 일괄 삭제", description = "백체크 의견 일괄 삭제 - 의견, 첨부파일", type = Description.TYPE.MEHTOD)
	public Result deleteBackCheckList(CommonReqVo commonReqVo, @RequestBody BackCheckForm.BackCheckDeleteAll delList) {

		designBackCheckComponent.removeAllBackCheck(delList.getDelList(), commonReqVo);

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("설계검토 백체크 의견 일괄 삭제");
		systemLogComponent.addUserLog(userLog);
		
		return Result.ok();
	}


}
