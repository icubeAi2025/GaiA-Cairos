package kr.co.ideait.platform.gaiacairos.web.entrypoint.projectcost;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.ideait.platform.gaiacairos.comp.portal.service.PortalService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.config.security.annotation.IsUser;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@IsUser
@Controller
@RequestMapping("/projectcost")
public class ContractPageController extends AbstractController {

    @Autowired
    PortalService portalService;

    /**
     * 사업비관리 > 공사비관리 Main
     */
    @GetMapping("/contract")
    @Description(name = "공사비관리 화면", description = "공사비관리 화면 페이지", type = Description.TYPE.MEHTOD)
    public String getContract(CommonReqVo commonReqVo, HttpServletRequest request, Model model,
                              @RequestParam(value = "type", required = false) String type) {
        log.info("headerReqVo: {}", commonReqVo);

//        String userInfo = cookieService.getCookie(request, cookieVO.getPortalCookieName());
//        String pjtInfo = cookieService.getCookie(request, cookieVO.getSelectCookieName());

//        String[] userParam = userInfo.split(":");
//        String[] pjtParam = pjtInfo.split(":");
//
//        String[] btnId = { "M0301_P_C_C" };
//
//        String[] btnClass = { "btn" };
//
//        String[] btnFun = { "" };
//
//        String[] btnMsg = { "item.projectcost.019" };
//
//        String[] btnEtc = { "id='addCntrct'" };
//
//        String btnHtml = portalService.selectBtnAuthorityList(userParam[1], userParam[0], userParam[2], pjtParam[0],
//                pjtParam[1], "M0301", btnId, btnClass, btnFun, btnMsg, btnEtc);
//        model.addAttribute("btnHtml", btnHtml);
//        model.addAttribute("isDelAuth", "Y");

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("공사비 관리 화면 접속");
        systemLogComponent.addUserLog(userLog);
        if(type != null && !type.equals("") && "p".equals(type.toString())) {
            model.addAttribute("header", false);
        }else{
            model.addAttribute("header", true);
        }


        if ("pgaia".equals(platform)) {
			return "page/projectcost/contract/contract_pgaia";
		} else {
			return "page/projectcost/contract/contract";
		}        
    }

    /**
     * 사업비관리 > 내역서 등록
     */
    @GetMapping("/contract/create")
    @Description(name = "공사비 관리 > 계약 내역서 등록 화면", description = "공사비관리 > 계약 내역서 등록 화면 페이지", type = Description.TYPE.MEHTOD)
    public String setContract(CommonReqVo commonReqVo, HttpServletRequest request, Model model,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "no", required = false) Integer no,
            @RequestParam(value = "id", required = false) String id) {

//        String userInfo = cookieService.getCookie(request, cookieVO.getPortalCookieName());
//        String pjtInfo = cookieService.getCookie(request, cookieVO.getSelectCookieName());
//
//        String[] userParam = userInfo.split(":");
//        String[] pjtParam = pjtInfo.split(":");
//
//        String[] btnId = { "M0301_F_C_C" };
//
//        String[] btnClass = { "btn _outline" };
//
//        String[] btnFun = { "" };
//
//        String[] btnMsg = { "btn.006" };
//
//        String[] btnEtc = { "id=\"save\"" };
//
//        String btnHtml = portalService.selectBtnAuthorityList(userParam[1], userParam[0], userParam[2], pjtParam[0],
//                pjtParam[1], "M0301", btnId, btnClass, btnFun, btnMsg, btnEtc);
//        model.addAttribute("btnHtml", btnHtml);
//
//        if ("d".equals(type.toString())) {
//            model.addAttribute("header", true);
//        } else if ("p".equals(type.toString())) {
//            model.addAttribute("header", false);
//        }

        return "page/projectcost/contract/contract_c";
    }

    /**
     * 사업비관리 > 내역서 상세조회
     */
    @GetMapping("/contract-detail")
    @Description(name = "공사비관리 > 계약 내역서 리스트 조회 화면", description = "공사비관리 > 계약 내역서 리스트 데이터 조회 화면 페이지", type = Description.TYPE.MEHTOD)
    public String getContractDetail(CommonReqVo commonReqVo, Model model, @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "no", required = false) String no,
            @RequestParam(value = "type", required = false) String type) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("공사비 관리 화면 접속");
        systemLogComponent.addUserLog(userLog);
        return "page/projectcost/contract/contract_detail";
    }
}
