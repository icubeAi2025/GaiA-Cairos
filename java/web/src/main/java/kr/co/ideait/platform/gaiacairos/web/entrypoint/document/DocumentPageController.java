package kr.co.ideait.platform.gaiacairos.web.entrypoint.document;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import kr.co.ideait.platform.gaiacairos.comp.document.DocumentComponent;
import kr.co.ideait.platform.gaiacairos.comp.system.service.CommonCodeService;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmComCode;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.iframework.annotation.Description;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@Slf4j
@Controller
@RequestMapping("/document")
public class DocumentPageController extends AbstractController {

    @Autowired
    CommonCodeService commonCodeService;

	/**
     * 통합문서관리 메인화면 
     */
    @GetMapping("")
    @Description(name = "문서관리 화면 페이지", description = "div: 01 = 통합문서관리, 02 = 사업비 관리 문서, 03 = 부진공정만회대책, 04= 착공계 문서관리", type = Description.TYPE.MEHTOD)
    public String document(CommonReqVo commonReqVo, Model model, @RequestParam("div") String naviDiv, @CookieValue(name = "lang", required = false, defaultValue = "ko") String langInfo,
                           @RequestParam(value = "pjtNo") String pjtNo, @RequestParam(value = "cntrctNo") String cntrctNo) {
        if(naviDiv == null){
            throw new GaiaBizException(ErrorType.INVAILD_INPUT_DATA);
        }

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        switch (naviDiv) {
            case "01":
                userLog.setExecType("통합문서관리 화면 접속");
                break;
            case "02":
                userLog.setExecType("사업비 관리 문서 화면 접속");
                break;
            case "03":
                userLog.setExecType("부진공정만회대책 화면 접속");
                break;
            case "04":
                userLog.setExecType("착공계 문서관리 화면 접속");
                break;
            default:
                break;
        }

        systemLogComponent.addUserLog(userLog);

        if(pjtNo == null || pjtNo.equals("") || cntrctNo == null || cntrctNo.equals("")) {
            return "redirect:/";
        }
        

        SmComCode documentTypeInfo = commonCodeService.getDocumentTypeInfo(naviDiv);

        if(documentTypeInfo == null){
            throw new GaiaBizException(ErrorType.NO_DATA);
        }

        model.addAttribute("documentType", documentTypeInfo.getCmnCd());
        model.addAttribute("menuId", documentTypeInfo.getAttrbtCd2());

        if("en".equals(langInfo)){
            model.addAttribute("docTitle", documentTypeInfo.getCmnCdNmEng());
        }
        else{
            model.addAttribute("docTitle", documentTypeInfo.getCmnCdNmKrn());
        }
        
        return "page/document/document";
    }
    
    /**
     * 통합문서관리 네비게이션 권한설정 권한 사용자 조회 팝업
     */
    @GetMapping("/navi/authority/popup")
    @Description(name = "문서관리 네비게이션 권한 사용자 조회 팝업 화면", description = "문서관리 네비게이션 권한 사용자 조회 팝업 화면", type = Description.TYPE.MEHTOD)
    public String naviAuthorityPopup(CommonReqVo commonReqVo, @RequestParam(value = "pjtNo") String pjtNo, @RequestParam(value = "cntrctNo") String cntrctNo) {
        return "page/document/common/common_document_popup";
    }

    /**
     * 통합문서관리 네비게이션 속성 정의 조회 팝업
     */
    @GetMapping("/navi/property/popup")
    @Description(name = "문서관리 네비게이션 속성 정의 조회 팝업 화면", description = "문서관리 - 문서경로 컨텍스트 메뉴(속성 정의) > 조회 팝업 화면", type = Description.TYPE.MEHTOD)
    public String naviPropertyPopup(CommonReqVo commonReqVo, @RequestParam(value = "pjtNo") String pjtNo, @RequestParam(value = "cntrctNo") String cntrctNo) {
        return "page/document/common/common_document_popup_property";
    }

    /**
     * 통합문서관리 PDF 미리보기 팝업
     */
    @GetMapping("/pdf-file/preview")
    @Description(name = "문서관리 PDF 미리보기 팝업 화면", description = "문서관리 - 문서 컨텍스트 메뉴(미리보기) > PDF 미리보기 팝업 화면", type = Description.TYPE.MEHTOD)
    public String pdfFilePreviewPopup(CommonReqVo commonReqVo) {
        return "page/document/common/common_document_pdf_preview";
    }

    /**
     * 문서24 등록 팝업
     */
    @GetMapping("/gdoc-send-popup")
    public String gdocSendPopup(CommonReqVo commonReqVo) {
        return "page/document/common/gdoc_send_modal_popup";
    }
    
    @GetMapping("approval-send-popup")
    public String approvalSendPopup(CommonReqVo commonReqVo) {
        return "page/document/common/approval_send_modal_popup";
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    @GetMapping("/navigation-popup")
    public String documentNaviPopup() {
        return "page/document/document_navigation_cu";
    }

    @GetMapping("/navigation-authority-popup")
    public String documentNaviAuthorityGroupPopup() {
        return "page/document/document_navigation_authority_cu";
    }

    @GetMapping("/navigation-authority-view-popup")
    public String documentNaviAuthorityGroupViewPopup() {
        return "page/document/document_navigation_authority";
    }

    @GetMapping("/navigation-authority-user-view-popup")
    public String documentNaviAuthorityGroupUserViewPopup() {
        return "page/document/document_navigation_authority_user";
    }

    @GetMapping("/navigation-property-list-popup")
    public String documentNavigationPropertyPopup() {
        return "page/document/document_navigation_property";
    }

    @GetMapping("/navigation-property-item-popup")
    public String documentNavigationPropertyPopPopup() {
        return "page/document/document_navigation_property_cu";
    }

    @GetMapping("/file-folder-popup")
    public String documentFileFolderPopup() {
        return "page/document/document_folder_cu";
    }

    @GetMapping("/file-upload-popup")
    public String documentFileUploadPopup() {
        return "page/document/document_file_upload";
    }

    @GetMapping("/file-copy-move-popup")
    public String documentFileCopyMovePopup() {
        return "page/document/document_file_copy_move";
    }

    @GetMapping("/file-rename-popup")
    public String documentFilePopup() {
        //return "page/document/document_file_rename";
         return "page/document/document_name_u_pop_up";
    }

    @GetMapping("/file-property-popup")
    public String documentFilePropertyPopup() {
        return "page/document/document_item_property";
    }

    @GetMapping("/item-popup")
    public String documentItemPopup() {
        return "page/document/document_item";
    }



}
