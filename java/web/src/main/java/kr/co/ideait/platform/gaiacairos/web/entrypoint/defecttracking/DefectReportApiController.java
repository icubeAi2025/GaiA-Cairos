package kr.co.ideait.platform.gaiacairos.web.entrypoint.defecttracking;

import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.comp.defecttracking.DefectReportComponent;
import kr.co.ideait.platform.gaiacairos.comp.defecttracking.service.DefectReportService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.report.defectreport.DefectReportForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("api/defecttracking/defectReport")
public class DefectReportApiController extends AbstractController {

	@Autowired
	DefectReportService defectReportService;

	@Autowired
	DefectReportComponent defectReportComponent;
	
	@Autowired
	DefectReportForm defectReportForm;
	
	
	/**
	 * 결함보고서 목록 조회
	 * @param defectReportList
	 * @return
	 */
	@PostMapping("/list")
	@Description(name = "결함보고서 목록 조회", description = "결함보고서 목록 조회", type = Description.TYPE.MEHTOD)
	public Result getDefectReport(CommonReqVo commonReqVo, @RequestBody DefectReportForm.DefectReportList defectReportList,
								  @CookieValue(name = "lang", required = false, defaultValue = "ko") String langInfo) {
		
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("결함보고서 목록 조회");

		systemLogComponent.addUserLog(userLog);
		
		defectReportList.setLang(langInfo);
		
		return Result.ok().put("report", defectReportComponent.selectDefectReport(defectReportForm.toDefectReportListInput(defectReportList)));
	}
	
	
	/**
	 * 결함보고서 상세 조회
	 * @param defectReportDetail
	 * @param langInfo
	 * @return
	 */
	@PostMapping("/detail")
	@Description(name = "결함보고서 상세 조회", description = "결함보고서 상세 조회", type = Description.TYPE.MEHTOD)
	public Result getReportDetail(CommonReqVo commonReqVo, @RequestBody DefectReportForm.DefectReportDetail defectReportDetail,
									@CookieValue(name = "lang", required = false, defaultValue = "ko") String langInfo) {
		
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("결함보고서 상세 조회");

		systemLogComponent.addUserLog(userLog);
		
		MybatisInput input = MybatisInput.of().add("cntrctNo", defectReportDetail.getCntrctNo())
												.add("dfccyNo", defectReportDetail.getDfccyNo())
												.add("lang", langInfo);
		
		return Result.ok().put("reportDetail", defectReportComponent.selectDfccyReportDetail(input));
	}
	
	
	/**
	 * 검색 셀렉트 옵션 - 결함단계 조회
	 * @param cntrctNo
	 * @return
	 */
	@GetMapping("/phase-selectbox/{cntrctNo}")
	@Description(name = "결함단계 조회", description = "검색 셀렉트 옵션 - 결함단계 조회", type = Description.TYPE.MEHTOD)
	public Result getPhaseSelectbox(CommonReqVo commonReqVo, @PathVariable("cntrctNo") String cntrctNo) {
		
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("결함보고서 결함단계 조회");

		systemLogComponent.addUserLog(userLog);
		
		return Result.ok().put("dfccyPhaseList", defectReportComponent.selectPhaseCd(cntrctNo));
	}
	
	
	/**
	 * 검색 셀렉트 옵션 - 작성자 목록 조회
	 * @param cntrctNo
	 * @return
	 */
	@GetMapping("/rgstr-selectbox/{cntrctNo}")
	@Description(name = "작성자 목록 조회", description = "검색 셀렉트 옵션 - 작성자 목록 조회", type = Description.TYPE.MEHTOD)
	public Result getRgstrSelectbox(CommonReqVo commonReqVo, @PathVariable("cntrctNo") String cntrctNo) {
		
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("결함보고서 작성자 목록 조회 ");

		systemLogComponent.addUserLog(userLog);
		
		return Result.ok().put("rgstrList", defectReportComponent.selectRgstrNm(cntrctNo));
	}
	
	
	

	
}
