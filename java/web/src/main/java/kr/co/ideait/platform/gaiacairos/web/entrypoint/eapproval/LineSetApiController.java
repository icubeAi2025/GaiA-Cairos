package kr.co.ideait.platform.gaiacairos.web.entrypoint.eapproval;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.comp.eapproval.LineSetComponent;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.ApLinesetMng;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.eapproval.lineset.LineSetForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/eapproval/lineset")
public class LineSetApiController extends AbstractController {

	@Autowired
	LineSetComponent lineSetComponent;

	/**
	 * 나의 결재선 조회
	 * @return
	 */
	@GetMapping("/myList")
	@Description(name = "전자결재 설정 - 나의 결재선 조회", description = "사용자가 등록한 결재선 조회 (indvdl_yn: Y)", type = Description.TYPE.MEHTOD)
	public Result getMyLineSetList(CommonReqVo commonReqVo) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("전자결재 설정 - 나의 결재선 조회");

		systemLogComponent.addUserLog(userLog);

		return Result.ok().put("lineset", lineSetComponent.getMyLineSetList());
	}


	/**
	 * 관리자 결재선 조회
	 * @return
	 */
	@GetMapping("/adminList/{cntrctNo}")
	@Description(name = "전자결재 설정 - 관리자 결재선 조회", description = "관리자가 등록한 결재선 조회 (indvdl_yn: N)", type = Description.TYPE.MEHTOD)
	public Result getAdminLineSetList(CommonReqVo commonReqVo, @PathVariable("cntrctNo") String cntrctNo) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("전자결재 설정 - 관리자 결재선 조회");

		systemLogComponent.addUserLog(userLog);

		MybatisInput input = MybatisInput.of().add("cntrctNo", cntrctNo);
		return Result.ok().put("lineset", lineSetComponent.getAdminLineSetList(input));
	}


	/**
	 * 나의 결재선 상세 조회
	 * @param request
	 * @param apLineNo
	 * @return
	 */
	@GetMapping("/detail/{apLineNo}/{cntrctNo}")
	@Description(name = "전자결재 설정 - 나의 결재선 상세 조회", description = "사용자가 등록한 결재선 상세 조회", type = Description.TYPE.MEHTOD)
	public Result getApLineSetDetail(CommonReqVo commonReqVo,
									 HttpServletRequest request,
									 @PathVariable("apLineNo") Integer apLineNo,
									 @PathVariable("cntrctNo") String cntrctNo) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("전자결재 설정 - 결재선 상세 조회");

		systemLogComponent.addUserLog(userLog);

		String userInfo = cookieService.getCookie(request, cookieVO.getPortalCookieName());
		String [] param = userInfo.split(":");

		return Result.ok().put("detailList", lineSetComponent.getMyLineSetDetail(apLineNo, param[1], cntrctNo));
	}


	/**
	 * 관리자 결재선 상세 조회
	 * @param commonReqVo
	 * @param apLineNo
	 * @param cntrctNo
	 * @return
	 */
	@GetMapping("/adminDetail/{apLineNo}/{cntrctNo}")
	@Description(name = "전자결재 설정 - 관리자 결재선 상세 조회", description = "관리자가 등록한 결재선 상세 조회", type = Description.TYPE.MEHTOD)
	public Result getAdminApLineSetDetail(CommonReqVo commonReqVo,
										 @PathVariable("apLineNo") Integer apLineNo,
										 @PathVariable("cntrctNo") String cntrctNo) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("전자결재 설정 - 관리자 결재선 상세 조회");

		systemLogComponent.addUserLog(userLog);

		return Result.ok().put("detailList", lineSetComponent.getAdminLineSetDetail(apLineNo, cntrctNo));
	}


	/**
	 * 나의 결재선 삭제
	 * @param delList
	 * @return
	 */
	@PostMapping("/delete-my-lineset")
	@Description(name = "전자결재 설정 - 나의 결재선 삭제", description = "나의 결재선 삭제(ApLinesetMng, ApLineSet 연계삭제)", type = Description.TYPE.MEHTOD)
	public Result deleteMyLineSet(CommonReqVo commonReqVo, @RequestBody LineSetForm.LineSetDeleteList delList) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("전자결재 설정 - 나의 결재선 삭제");

		systemLogComponent.addUserLog(userLog);

		lineSetComponent.deleteLineSetList(delList.getDelList());

		return Result.ok();
	}


	/**
	 * 관리자 결재선 삭제
	 * @param delList
	 * @return
	 */
	@PostMapping("/delete-admin-lineset")
	@Description(name = "전자결재 설정 - 관리자 결재선 삭제", description = "관리자 결재선 삭제(ApLinesetMng, ApLineSet 연계삭제)", type = Description.TYPE.MEHTOD)
	public Result deleteAdminLineSet(CommonReqVo commonReqVo, @RequestBody LineSetForm.LineSetDeleteList delList) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("전자결재 설정 - 관리자 결재선 삭제");

		systemLogComponent.addUserLog(userLog);

		lineSetComponent.deleteLineSetList(delList.getDelList());

		return Result.ok();
	}


	/**
	 * 나의 결재선 추가
	 * @param commonReqVo
	 * @param saveList
	 * @return
	 */
	@PostMapping("/save-my-lineset")
	@Description(name = "전자결재 설정 - 나의 결재선 추가", description = "나의 결재선 추가", type = Description.TYPE.MEHTOD)
	public Result saveMyLineSet(CommonReqVo commonReqVo, @RequestBody LineSetForm.LineSetSaveList saveList) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("전자결재 설정 - 나의 결재선 추가");

		systemLogComponent.addUserLog(userLog);

		lineSetComponent.createLineSet(saveList.getApLinesetMng(), saveList.getApLineSet(), saveList.getCntrctNo());

		return Result.ok();
	}


	/**
	 * 나의 결재선 수정
	 * @param commonReqVo
	 * @param saveList
	 * @return
	 */
	@PostMapping("/update-my-lineset")
	@Description(name = "전자결재 설정 - 나의 결재선 수정", description = "나의 결재선 수정", type = Description.TYPE.MEHTOD)
	public Result updateMyLineSet(CommonReqVo commonReqVo, @RequestBody LineSetForm.LineSetSaveList saveList) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("전자결재 설정 - 나의 결재선 수정");

		systemLogComponent.addUserLog(userLog);

		lineSetComponent.updateLineSet(saveList.getApLinesetMng(), saveList.getApLineSet());

		return Result.ok();
	}


	/**
	 * 관리자 결재선 추가
	 * @param commonReqVo
	 * @param saveList
	 * @return
	 */
	@PostMapping("/save-admin-lineset")
	@Description(name = "전자결재 설정 - 관리자 결재선 추가", description = "관리자 결재선 추가", type = Description.TYPE.MEHTOD)
	public Result saveAdminLineSet(CommonReqVo commonReqVo, @RequestBody LineSetForm.LineSetSaveList saveList) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("전자결재 설정 - 관리자 결재선 추가");

		systemLogComponent.addUserLog(userLog);

		// 중복체크: 착공계(문서코드10) 중복 가능
		checkDuplicate(saveList.getApLinesetMng().getApType(), saveList.getCntrctNo());

		lineSetComponent.createLineSet(saveList.getApLinesetMng(), saveList.getApLineSet(), saveList.getCntrctNo());

		return Result.ok();
	}


	/**
	 * 관리자 결재선 수정
	 * @param commonReqVo
	 * @param saveList
	 * @return
	 */
	@PostMapping("/update-admin-lineset")
	@Description(name = "전자결재 설정 - 관리자 결재선 수정", description = "관리자 결재선 수정", type = Description.TYPE.MEHTOD)
	public Result updateAdminLineSet(CommonReqVo commonReqVo, @RequestBody LineSetForm.LineSetSaveList saveList) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("전자결재 설정 - 관리자 결재선 수정");

		systemLogComponent.addUserLog(userLog);

		ApLinesetMng old = lineSetComponent.getAdminLineSet(saveList.getApLinesetMng().getApLineNo());
		if(!old.getApType().equals(saveList.getApLinesetMng().getApType())) {
			// 중복체크: 착공계(문서코드10) 중복 가능
			checkDuplicate(saveList.getApLinesetMng().getApType(), saveList.getCntrctNo());
		}

		lineSetComponent.updateLineSet(saveList.getApLinesetMng(), saveList.getApLineSet());

		return Result.ok();
	}


	/**
	 * 관리자 결재선 - 부서 조회
	 * @param commonReqVo
	 * @return
	 */
	@GetMapping("/admin-lineset-deptInfo/{cntrctNo}")
	@Description(name = "관리자 결재선 부서 조회", description = "관리자 결재선 부서 조회", type = Description.TYPE.MEHTOD)
	public Result getCompanyInfo(CommonReqVo commonReqVo, @PathVariable String cntrctNo) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("관리자 결재선 부서 조회");

		systemLogComponent.addUserLog(userLog);

		MybatisInput input = MybatisInput.of().add("pjtNo", UserAuth.get(true).getPjtNo())
												.add("cntrctNo", cntrctNo);

		return Result.ok().put("deptInfo", lineSetComponent.selectAdminLinesetDeptInfo(input));

	}


	/**
	 * 관리자 결재선 - 결재자 검색
	 * @param commonReqVo
	 * @param searchText
	 * @param cntrctNo
	 * @return
	 */
	@GetMapping("/search-admin-lineset")
	@Description(name = "관리자 결재선 검색", description = "관리자 결재선 검색", type = Description.TYPE.MEHTOD)
	public Result searchAdminLineset(CommonReqVo commonReqVo, @RequestParam String searchText, @RequestParam String cntrctNo ) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("관리자 결재선 검색");

		systemLogComponent.addUserLog(userLog);

		MybatisInput input = MybatisInput.of().add("pjtNo", UserAuth.get(true).getPjtNo())
												.add("usrId", UserAuth.get(true).getUsrId())
												.add("cntrctNo", cntrctNo)
												.add("searchText", searchText);

		return Result.ok().put("searchList", lineSetComponent.selectAdminLinesetUser(input));
	}


	/**
	 * 관리자 결재선 중복 체크
	 * @param apType
	 * @param cntrctNo
	 */
	private void checkDuplicate(String apType, String cntrctNo) {
		if(!"10".equals(apType) && !"01".equals(apType)) {
			MybatisInput input = MybatisInput.of().add("cntrctNo", cntrctNo).add("apType", apType);
			Integer count = lineSetComponent.checkDuplicate(input);
			if(count > 0) {
				throw new GaiaBizException(ErrorType.DUPLICATION_DATA, "선택한 문서 타입에 이미 등록된 결재선이 있습니다.");
			}
		}
	}
}
