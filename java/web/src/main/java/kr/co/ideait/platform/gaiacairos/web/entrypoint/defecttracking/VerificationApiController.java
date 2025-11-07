package kr.co.ideait.platform.gaiacairos.web.entrypoint.defecttracking;

import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.comp.defecttracking.VerificationComponent;
import kr.co.ideait.platform.gaiacairos.comp.defecttracking.helper.DefectTrackingHelper;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DtDeficiencyConfirmHistory;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisOutput;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.tool.verification.VerificationForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.tool.verification.VerificationForm.*;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/defecttracking/verification")
public class VerificationApiController extends AbstractController {

	@Autowired
	VerificationComponent verificationComponent;

	@Autowired
	DefectTrackingHelper defectTrackingHelper;
	
	@Autowired
	VerificationForm verificationForm;
	
	
	/**
	 * 결함 확인 관리 목록조회 (결함, 답변, 첨부파일)
	 * @param dfccyConfirmList
	 * @param langInfo
	 * @return
	 */
	@PostMapping("/list")
	@Description(name = "결함추적 확인 관리 목록조회", description = "결함 확인 관리 목록조회 (결함, 답변, 첨부파일)", type = Description.TYPE.MEHTOD)
	public Result getDfccyConfirmList(CommonReqVo commonReqVo, @RequestBody DfccyConfirmList dfccyConfirmList,
									  @CookieValue(name = "lang", required = false, defaultValue = "ko") String langInfo) {
		
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("결함추적 확인 관리 목록조회");

		systemLogComponent.addUserLog(userLog);
		
		dfccyConfirmList.setLang(langInfo);
		dfccyConfirmList.setUsrId(UserAuth.get(true).getUsrId());
		
		int page = dfccyConfirmList.getPage();
		int size = dfccyConfirmList.getSize();
		
		Pageable pageable = PageRequest.of(page - 1, size); 
		Page<MybatisOutput> pageData = verificationComponent.selectDfccyConfirmList(verificationForm.toDfccyConfirmListInput(dfccyConfirmList), pageable);
		Long totalCount = pageData.getTotalElements();
		
		return Result.ok().put("dfccyList", pageData.getContent())
							.put("totalCount", totalCount);
		
	}
	
	
	/**
	 * 검색 셀렉트 옵션 - 결함분류 조회
	 * @return
	 */
	@GetMapping("/dfccy-selectbox")
	@Description(name = "결함추적 결함분류 조회", description = "결함추적 검색 셀렉트 옵션 - 결함분류 조회", type = Description.TYPE.MEHTOD)
	public Result getDfccySelectbox(CommonReqVo commonReqVo) {
		
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("결함추적 결함분류 조회");

		systemLogComponent.addUserLog(userLog);
		
		return Result.ok().put("dfccySelectbox", verificationComponent.selectDfccyCd());
	}
	
	
	
	/**
	 * 결함 확인 상세조회 (등록된 모든 확인의견&첨부파일)
	 * @param dfccyConfirmDetail
	 * @return
	 */
	@PostMapping("/detail")
	@Description(name = "결함추적 확인 상세조회", description = "결함추적 확인 상세조회 (등록된 모든 확인의견&첨부파일)", type = Description.TYPE.MEHTOD)
	public Result getDfccyConfirmDetail(CommonReqVo commonReqVo, @RequestBody DfccyConfirmDetail dfccyConfirmDetail) {
		
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("결함추적 결함분류 조회");

		systemLogComponent.addUserLog(userLog);
		
		return Result.ok().put("dfccyConfirm", verificationComponent.selectDfccyConfirmDetail(verificationForm.toDfccyConfirmDetailInput(dfccyConfirmDetail)));
	}
	
	
	/**
	 * QA / 관리관 확인 이력 조회
	 * @param confirmHistory
	 * @return
	 */
	@PostMapping("/history")
	@Description(name = "결함추적 QA / 관리관 확인 이력 조회", description = "결함추적 QA / 관리관 확인 이력 조회", type = Description.TYPE.MEHTOD)
	public Result getConfirmHistory(CommonReqVo commonReqVo, 
									 @RequestBody ConfirmHistory confirmHistory,
									 @CookieValue(name = "lang", required = false, defaultValue = "ko") String langInfo) {
		
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("결함추적 QA / 관리관 확인 이력 조회");

		systemLogComponent.addUserLog(userLog);
		
		confirmHistory.setLang(langInfo);
		return Result.ok().put("historyList", verificationComponent.selectConfirmHistoryList(verificationForm.toConfirmHistoryInput(confirmHistory)));
	}


	/**
	 * 확인 의견 추가 - 의견, 첨부파일
	 * @param commonReqVo
	 * @param dfccyConfirmInsert
	 * @param files
	 * @return
	 */
	@PostMapping("/create")
	@Description(name = "결함추적 확인 의견 추가", description = "결함추적 확인 의견 추가 - 의견, 첨부파일", type = Description.TYPE.MEHTOD)
	public Result insertVerification(CommonReqVo commonReqVo,
									 @RequestPart("confirmData") DfccyConfirmInsert dfccyConfirmInsert,
									 @RequestPart(value = "files", required = false) List<MultipartFile> files) {
		
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("결함추적 확인 의견 추가");

		systemLogComponent.addUserLog(userLog);

		verificationComponent.insertDfccyConfirm(dfccyConfirmInsert.getDtDeficiencyConfirm(), files, defectTrackingHelper.createReqVoMap(commonReqVo));
		
		return Result.ok();
	}
	
	
	
	/**
	 * 확인 의견 수정 - 의견, 첨부파일
	 * @param dfccyConfirmInsert
	 * @param files
	 * @return
	 */
	@PostMapping("/update")
	@Description(name = "결함추적 확인 의견 수정", description = "결함추적 확인 의견 수정 - 의견, 첨부파일", type = Description.TYPE.MEHTOD)
	public Result updateVerification(CommonReqVo commonReqVo, @RequestPart("confirmData") DfccyConfirmInsert dfccyConfirmInsert,
									  @RequestPart(value = "files", required = false) List<MultipartFile> files) {
		
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("결함추적 확인 의견 수정");

		systemLogComponent.addUserLog(userLog);

		verificationComponent.updateDfccyConfirm(verificationForm.toDfccyConfirmInsertInput(dfccyConfirmInsert), files, defectTrackingHelper.createReqVoMap(commonReqVo));
		
		return Result.ok();
	}

	
	/**
	 * 확인 의견 삭제 - 의견, 첨부파일
	 * @param dfccySeq
	 * @return
	 */
	@GetMapping("/delete/{dfccySeq}")
	@Description(name = "결함추적 확인 의견 삭제", description = "결함추적 확인 의견 삭제 - 의견, 첨부파일", type = Description.TYPE.MEHTOD)
	public Result deleteVerification(CommonReqVo commonReqVo, @PathVariable("dfccySeq") String dfccySeq) {
		
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("결함추적 확인 의견 삭제");

		systemLogComponent.addUserLog(userLog);

		verificationComponent.deleteVerification(dfccySeq, true, UserAuth.get(true).getUsrId(), UserAuth.get(true).getPjtNo(), defectTrackingHelper.createReqVoMap(commonReqVo));
		
		return Result.ok();
	}

	
	/**
	 * 확인 의견 전체 삭제 - 의견, 첨부파일
	 * @param delDfccyList
	 * @return
	 */
	@PostMapping("/delete-list")
	@Description(name = "결함추적 확인 의견 전체 삭제", description = "결함추적 확인 의견 전체 삭제 - 의견, 첨부파일", type = Description.TYPE.MEHTOD)
	public Result deleteVerificationList(CommonReqVo commonReqVo, @RequestBody DfccyConfirmDeleteAll delDfccyList) {
		
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("결함추적 확인 의견 전체 삭제");

		systemLogComponent.addUserLog(userLog);

		verificationComponent.deleteAllVerification(delDfccyList.getDelDfccyList(), true, UserAuth.get(true).getUsrId(), UserAuth.get(true).getPjtNo(), defectTrackingHelper.createReqVoMap(commonReqVo));
		
		return Result.ok();
	}


	/**
	 * 확인결과 관리관 일괄 종료
	 * @param commonReqVo
	 * @param finishList
	 * @return
	 */
	@PostMapping("/createHistory-spvs")
	@Description(name = "결함추적 확인결과 일괄 종료", description = "결함추적 확인결과 관리관 일괄 종료", type = Description.TYPE.MEHTOD)
	public Result insertConfirmHistoryBySpvs(CommonReqVo commonReqVo, @RequestBody List<DtDeficiencyConfirmHistory> finishList) {
		
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("결함추적 확인결과_관리관 일괄 종료");

		systemLogComponent.addUserLog(userLog);

		if(finishList != null && !finishList.isEmpty()) {
			verificationComponent.finishList(finishList, defectTrackingHelper.createReqVoMap(commonReqVo));
		}

		return Result.ok();
	}


	/**
	 * 확인결과 QA 일괄 종료
	 * @param commonReqVo
	 * @param finishList
	 * @return
	 */
	@PostMapping("/createHistory-qa")
	@Description(name = "결함추적 확인결과 일괄 종료", description = "결함추적 확인결과 QA 일괄 종료", type = Description.TYPE.MEHTOD)
	public Result insertConfirmHistoryByQa(CommonReqVo commonReqVo, @RequestBody List<DtDeficiencyConfirmHistory> finishList) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("결함추적 확인결과_QA 일괄 종료");

		systemLogComponent.addUserLog(userLog);

		if(finishList != null && !finishList.isEmpty()) {
			verificationComponent.finishList(finishList, defectTrackingHelper.createReqVoMap(commonReqVo));
		}

		return Result.ok();
	}
}
