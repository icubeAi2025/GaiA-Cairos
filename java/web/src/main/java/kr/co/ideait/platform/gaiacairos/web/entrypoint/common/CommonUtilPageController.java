package kr.co.ideait.platform.gaiacairos.web.entrypoint.common;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;

@Controller
@RequestMapping("/util")
public class CommonUtilPageController extends AbstractController {
	
	/**
     * PDF 미리보기 팝업
     */
    @GetMapping("/pdf-file-view")
    @Description(name = "공통 PDF 미리보기 팝업화면", description = "공통 기능의 PDF 미리보기 팝업화면을 조회한다.", type = Description.TYPE.MEHTOD)
    public String pdfFilePreviewPopup(CommonReqVo commonReqVo) {

    	Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("공통 PDF 미리보기 팝업화면 이동");
        
        return "page/common/pdf_view";
    }

}
