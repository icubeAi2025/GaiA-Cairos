package kr.co.ideait.platform.gaiacairos.web.entrypoint.project;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.ideait.platform.gaiacairos.comp.portal.PortalComponent;
import kr.co.ideait.platform.gaiacairos.comp.portal.service.PortalService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/project/contractstatus")
public class ContractstatusPageController extends AbstractController {

	@Autowired
	PortalService portalService;

	@Autowired
	PortalComponent portalComponent;


	/**
	 * 계약현황 기본화면
	 */
	@GetMapping("")
	@Description(name = "계약현황 목록 화면", description = "계약현황 목록 화면", type = Description.TYPE.MEHTOD)
	public String contractstatus(CommonReqVo commonReqVo,
								 @RequestParam(value = "returnParam", required = false) String returnParam,
								 HttpServletRequest request, Model model, @RequestParam(value = "pjtNo", required = false) String pjtNo,
								 @RequestParam(value = "cntrctNo", required = false) String cntrctNo) {


		// 계약목록 버튼
		String[] ctBtnId = {"CNTRCT_D_01", "CNTRCT_U_02", "CNTRCT_C_02"};
		String[] ctBtnClass = {"btn _outline", "btn _outline", "btn _fill"};
		String[] ctBtnFun = {"onclick=\"contractgrid.deleteContract()\"",
				"onclick=\"contractgrid.updateContract()\"",
				"onclick=\"contractgrid.addContract()\""};
		String[] ctBtnMsg = {"btn.002", "btn.003", "btn.001"};
		String ctBtnHtml = portalComponent.selectBtnAuthorityList(ctBtnId, ctBtnClass, ctBtnFun, ctBtnMsg);


		// 계약 내역서 버튼
		String[] cbBtnId = {"CNTRCT_BID_C_02", "CNTRCT_R_04"};
		String[] cbBtnClass = {"btn _fill", "btn _fill"};
		String[] cbBtnFun = {"onclick=\"contractgrid.addContractBid()\"",
				"onclick=\"contractgrid.getContractBid()\""};
		String[] cbBtnMsg = {"btn.049", "btn.050"};
		String cbBtnHtml = portalComponent.selectBtnAuthorityList(cbBtnId, cbBtnClass, cbBtnFun, cbBtnMsg);


		// 도급 버튼
		String[] cpBtnId = {"CNTRCT_CHG_D_01", "CNTRCT_COMP_U_02", "CNTRCT_COMP_C_02"};
		String[] cpBtnClass = {"btn _outline", "btn _outline", "btn _fill"};
		String[] cpBtnFun = {"onclick=\"companygrid.deleteCompany()\"",
				"onclick=\"companygrid.updateCompany()\"",
				"onclick=\"companygrid.addCompany()\""};
		String[] cpBtnMsg = {"btn.002", "btn.003", "btn.001"};
		String cpBtnHtml = portalComponent.selectBtnAuthorityList(cpBtnId, cpBtnClass, cpBtnFun, cpBtnMsg);

		// 계약변경 버튼
		String[] ccBtnId = {"CNTRCT_D_01", "CNTRCT_CHG_U_02", "CNTRCT_CHG_C_02", "CNTRCT_R_04"};
		String[] ccBtnClass = {"btn _outline", "btn _outline", "btn _fill", "btn _fill"};
		String[] ccBtnFun = {"onclick=\"changegrid.deleteChange()\"", "onclick=\"changegrid.updateChange()\"",
				"onclick=\"changegrid.addChange()\"", "onclick=\"changegrid.contractcost()\""};
		String[] ccBtnMsg = {"btn.002", "btn.003", "btn.001", "btn.051"};
		String ccBtnHtml = portalComponent.selectBtnAuthorityList(ccBtnId, ccBtnClass, ccBtnFun, ccBtnMsg);

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.VIEW.name());
		userLog.setExecType("계약현황 목록 화면 접속");
		systemLogComponent.addUserLog(userLog);


		model.addAttribute("ctBtnHtml", ctBtnHtml);
		model.addAttribute("cpBtnHtml", cpBtnHtml);
		model.addAttribute("cbBtnHtml", cbBtnHtml);
		model.addAttribute("ccBtnHtml", ccBtnHtml);

		boolean isCntDelAuth = ctBtnHtml.contains("deleteContract");
		boolean isCompDelAuth = ctBtnHtml.contains("deleteCompany");
		boolean isChgDelAuth = ctBtnHtml.contains("deleteChange");
		model.addAttribute("isCntDelAuth", isCntDelAuth);
		model.addAttribute("isCompDelAuth", isCompDelAuth);
		model.addAttribute("isChgDelAuth", isChgDelAuth);

		return "page/project/contractstatus/contractstatus";
	}

	/**
	 * 계약 상세조회
	 */
	@GetMapping("/contract")
	@Description(name = "계약 상세조회 화면", description = "계약 상세조회 화면", type = Description.TYPE.MEHTOD)
	public String getContractStatus(CommonReqVo commonReqVo, HttpServletRequest request, Model model,
									@RequestParam(value = "pjtNo", required = false) String pjtNo,
									@RequestParam(value = "cntrctNo", required = false) String cntrctNo) {
		String[] userParam = {
				commonReqVo.getLoginId(),
				commonReqVo.getAdmin() ? "ADMIN" : "NORMAL",
				commonReqVo.getPlatform()
		};

		String[] btnId = {"CNTRCT_U_02"};
		String[] btnClass = {"btn _outline"};
		String[] btnFun = {"onclick=\"page.update()\""};
		String[] btnMsg = {"btn.003"};
		String[] btnEtc = {"id='update-button'"};
		String btnHtml = portalComponent.selectBtnAuthorityList(btnId, btnClass, btnFun, btnMsg, btnEtc);

		model.addAttribute("btnHtml", btnHtml);

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.VIEW.name());
		userLog.setExecType("계약현황 상세조회 화면 접속");
		systemLogComponent.addUserLog(userLog);

		return "page/project/contractstatus/contractstatus_r";
	}

	/**
	 * 계약 추가
	 */
	@GetMapping("/addContract")
	@Description(name = "계약 추가 화면", description = "계약 추가 화면", type = Description.TYPE.MEHTOD)
	public String addContract(CommonReqVo commonReqVo, HttpServletRequest request, Model model,
							  @RequestParam(value = "pjtNo", required = false) String pjtNo,
							  @RequestParam(value = "cntrctNo", required = false) String cntrctNo) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.VIEW.name());
		userLog.setExecType("계약 추가 화면 접속");
		systemLogComponent.addUserLog(userLog);

		return "page/project/contractstatus/contractstatus_c";
	}

	/**
	 * 계약 수정
	 */
	@GetMapping("/updateContract")
	@Description(name = "계약 수정 화면", description = "계약 수정 화면", type = Description.TYPE.MEHTOD)
	public String updateContract(CommonReqVo commonReqVo, HttpServletRequest request, Model model,
								 @RequestParam(value = "pjtNo", required = false) String pjtNo,
								 @RequestParam(value = "cntrctNo", required = false) String cntrctNo) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.VIEW.name());
		userLog.setExecType("계약 수정 화면 접속");
		systemLogComponent.addUserLog(userLog);

		return "page/project/contractstatus/contractstatus_u";
	}

	/**
	 * 도급 상세조회
	 */
	@GetMapping("/company")
	@Description(name = "계약도급 조회 화면", description = "계약도급 조회 화면", type = Description.TYPE.MEHTOD)
	public String getContractCompany(CommonReqVo commonReqVo, HttpServletRequest request, Model model,
									 @RequestParam(value = "pjtNo", required = false) String pjtNo,
									 @RequestParam(value = "cntrctNo", required = false) String cntrctNo) {
		String[] userParam = {
				commonReqVo.getLoginId(),
				commonReqVo.getAdmin() ? "ADMIN" : "NORMAL",
				commonReqVo.getPlatform()
		};

		String[] btnId = {"CNTRCT_COMP_U_02"};
		String[] btnClass = {"btn _outline"};
		String[] btnFun = {"onclick=\"page.update()\""};
		String[] btnMsg = {"btn.003"};
		String[] btnEtc = {"id='update-button'"};

		String btnHtml = portalComponent.selectBtnAuthorityList(btnId, btnClass, btnFun, btnMsg, btnEtc);

		model.addAttribute("btnHtml", btnHtml);

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.VIEW.name());
		userLog.setExecType("계약도급 조회 화면 접속");
		systemLogComponent.addUserLog(userLog);

		return "page/project/contractstatus/contract_company_r";
	}

	/**
	 * 도급 추가
	 */
	@GetMapping("/createCompany")
	@Description(name = "계약도급 추가 화면", description = "계약도급 추가 화면", type = Description.TYPE.MEHTOD)
	public String createContractCompany(CommonReqVo commonReqVo, HttpServletRequest request, Model model,
										@RequestParam(value = "pjtNo", required = false) String pjtNo,
										@RequestParam(value = "cntrctNo", required = false) String cntrctNo) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.VIEW.name());
		userLog.setExecType("계약도급 조회 화면 접속");
		systemLogComponent.addUserLog(userLog);

		return "page/project/contractstatus/contract_company_c";
	}

	/**
	 * 도급 수정
	 */
	@GetMapping("/updateCompany")
	@Description(name = "계약도급 수정 화면", description = "계약도급 수정 화면", type = Description.TYPE.MEHTOD)
	public String updateContractCompany(CommonReqVo commonReqVo, HttpServletRequest request, Model model,
										@RequestParam(value = "pjtNo", required = false) String pjtNo,
										@RequestParam(value = "cntrctNo", required = false) String cntrctNo) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.VIEW.name());
		userLog.setExecType("계약도급 수정 화면 접속");
		systemLogComponent.addUserLog(userLog);

		return "page/project/contractstatus/contract_company_u";
	}

	/**
	 * 변경 조회
	 */
	@GetMapping("/change")
	@Description(name = "계약변경 조회 화면", description = "계약변경 조회 화면", type = Description.TYPE.MEHTOD)
	public String getContractChange(CommonReqVo commonReqVo, HttpServletRequest request, Model model,
									@RequestParam(value = "pjtNo", required = false) String pjtNo,
									@RequestParam(value = "cntrctNo", required = false) String cntrctNo) {
		String[] userParam = {
				commonReqVo.getLoginId(),
				commonReqVo.getAdmin() ? "ADMIN" : "NORMAL",
				commonReqVo.getPlatform()
		};


		String[] btnId = {"CNTRCT_CHG_U_02"};
		String[] btnClass = {"btn _outline"};
		String[] btnFun = {"onclick=\"page.update()\""};
		String[] btnMsg = {"btn.003"};
		String[] btnEtc = {"id='update-button'"};
		String btnHtml = portalComponent.selectBtnAuthorityList(btnId, btnClass, btnFun, btnMsg, btnEtc);

		model.addAttribute("btnHtml", btnHtml);

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.VIEW.name());
		userLog.setExecType("계약변경 조회 화면 접속");
		systemLogComponent.addUserLog(userLog);

		return "page/project/contractstatus/contract_change_r";
	}

	/**
	 * 변경 추가
	 */
	@GetMapping("/createChange")
	@Description(name = "계약변경 추가 화면", description = "계약변경 추가 화면", type = Description.TYPE.MEHTOD)
	public String getContractChangeForm(CommonReqVo commonReqVo, HttpServletRequest request, Model model,
										@RequestParam(value = "pjtNo", required = false) String pjtNo,
										@RequestParam(value = "cntrctNo", required = false) String cntrctNo) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.VIEW.name());
		userLog.setExecType("계약변경 추가 화면 접속");
		systemLogComponent.addUserLog(userLog);

		return "page/project/contractstatus/contract_change_c";
	}

	/**
	 * 변경 수정
	 */
	@GetMapping("/updateChange")
	@Description(name = "계약변경 수정 화면", description = "계약변경 수정 화면", type = Description.TYPE.MEHTOD)
	public String updateContractChange(CommonReqVo commonReqVo, HttpServletRequest request, Model model,
									   @RequestParam(value = "pjtNo", required = false) String pjtNo,
									   @RequestParam(value = "cntrctNo", required = false) String cntrctNo) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.VIEW.name());
		userLog.setExecType("계약변경 수정 화면 접속");
		systemLogComponent.addUserLog(userLog);

		return "page/project/contractstatus/contract_change_u";
	}

	/**
	 * 계약내역서 등록
	 */
	@GetMapping("/bidform")
	@Description(name = "계약내역서 등록 화면", description = "계약내역서 등록 화면", type = Description.TYPE.MEHTOD)
	public String getContractBidForm(CommonReqVo commonReqVo, @RequestParam("cntrctNo") String cntrctNo,
									 @RequestParam(value = "type", required = false) String type, Model model) {
		model.addAttribute("cntrctNo", cntrctNo);
		if ("d".equals(type)) {
			model.addAttribute("header", true);
		} else if ("p".equals(type)) {
			model.addAttribute("header", false);
		}

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.VIEW.name());
		userLog.setExecType("계약내역서 등록 화면 접속");
		systemLogComponent.addUserLog(userLog);

		return "page/project/contractstatus/contract_bid_c";
	}

	/**
	 * 계약내역서 보기
	 */
	@GetMapping("/bid")
	@Description(name = "계약내역서 조회 화면", description = "계약내역서 조회 화면", type = Description.TYPE.MEHTOD)
	public String getContractBid(CommonReqVo commonReqVo, @RequestParam("cntrctNo") String cntrctNo, Model model) {
		model.addAttribute("cntrctNo", cntrctNo);

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.VIEW.name());
		userLog.setExecType("계약내역서 조회 화면 접속");
		systemLogComponent.addUserLog(userLog);

		if ("pgaia".equals(platform)) {
			return "page/project/contractstatus/contract_bid_pgaia";
		} else {
			return "page/project/contractstatus/contract_bid";
		}
	}
}

