package kr.co.ideait.platform.gaiacairos.web.entrypoint.document;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.comp.document.DocumentComponent;
import kr.co.ideait.platform.gaiacairos.comp.document.service.DocumentService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.config.security.annotation.IsUser;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.document.DocumentDto;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.document.DocumentForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@IsUser
@RestController
@RequestMapping({"/interface/document"})
public class DocumentInterfaceController extends AbstractController {

    @Autowired
    DocumentService documentService;

    @Autowired
    DocumentComponent documentComponent;
//
//    @Autowired
//    CommonCodeService commonCodeService;
//
//    @Autowired
//    ContractstatusService contractstatusService;
//
//    @Autowired
//    ResourceService resourceService;
//
//    @Autowired
//    ExcelCostWriterService excelCostWriterService;
//
//
    @Autowired
    DocumentDto documentDto;
//    @Autowired
//    private DocumentDtoImpl documentDtoImpl;
//
//    @Autowired
//    CommonCodeDto commonCodeDto;
//
//    @Autowired
//    DocumentForm documentForm;
//
//    @Autowired
//    FileService fileService;
//
//    @Value("${spring.application.name}")
//    String pjtType;
//
//    @Autowired
//    PortalComponent portalComponent;
//


    /**
     * 문서 삭제
     * @param commonReqVo
     * @param params
     * @return
     */
    @PutMapping("")
    @Description(name = "문서 삭제", description = "문서를 삭제한다.", type = Description.TYPE.MEHTOD)
    public Result removeDocument(CommonReqVo commonReqVo, @RequestBody Map<String, Object> params) {
        return documentComponent.removeDocument(params);
    }

    /**
     * 문서 삭제 취소
     * @param commonReqVo
     * @param params
     * @return
     */
    @PostMapping("/delete/rollback")
    @Description(name = "문서 삭제 롤백", description = "문서 삭제를 취소한다", type = Description.TYPE.MEHTOD)
    public Result rollbackRemovedDocument(CommonReqVo commonReqVo, @RequestBody Map<String, Object> params) {
        return documentComponent.rollbackDocument(params);
    }

//    /**
//     * 문서 업로드 - 확장.
//     */
    @PostMapping("/file/create-ex")
    @Description(name = "문서 업로드", description = "문서 경로 종류가 폴더형(FOLDR)인 경로에 파일 저장 및 DB 데이터 저장", type = Description.TYPE.MEHTOD)
    public Result createDocumentEx(CommonReqVo commonReqVo, @Valid @RequestPart(value = "docData") DocumentForm.DocCreateEx doc,
            @RequestPart(value = "files", required = false) List<MultipartFile> files)
            throws IllegalStateException, IOException {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("문서 업로드");
//        systemLogComponent.addUserLog(userLog);

        log.info("============request data=============");
        log.info("docInfo={}", doc);
        log.info("fileInfo={}", files);
        log.info("============request data=============");

        // 입력 값 검증
        if (doc == null) {
            return Result.nok(ErrorType.NO_DATA, "Invalid document data.");
        }

        return Result.ok().put("data", documentComponent.addDocumentListEx(doc, files));
    }

    @PostMapping("/navigation/list/create")
    @Description(name = "문서 네비게이션 다중 생성", description = "문서 네비게이션 리스트 추가", type = Description.TYPE.MEHTOD)
    public Result createNavigationList(CommonReqVo commonReqVo, @RequestBody List<DocumentForm.NavigationCreate> navigationList) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("문서 네비게이션 다중 생성");
//        systemLogComponent.addUserLog(userLog);

        documentComponent.createNavigationList(navigationList);

        return Result.ok();
    }

    @PostMapping("/lastest-document-by-folder")
    @Description(name = "폴더유형 네비 최신 문서 정보 조회", description = "폴더유형 네비 최신 문서 정보 조회한다.", type = Description.TYPE.MEHTOD)
    public Result getLastestDcStorageMainByFolderType(CommonReqVo commonReqVo, @RequestBody Map<String, String> requestParams) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("폴더유형 네비 최신 문서 정보 조회");
//        systemLogComponent.addUserLog(userLog);

        return Result.ok().put("resultCode", "00").put("data", documentComponent.getLastestDcStorageMainByFolderType(requestParams));
    }

    //============ 전자 결재 처리 관련 (documentInterfaceController로 이동.)========
    @PostMapping("/shared-history/create")
    public Result createDocSharedHistory(@RequestBody Map<String,Object> requestParams){
        Map<String,Object> result = documentService.createDocSharedHistory(requestParams);
        return Result.ok(result);
    }

    @PostMapping("/shared-history/update")
    @Description(name = "결재 공유 문서 정보 수정", description = "결재 요청 완료 후, 공유 문서 정보 수정", type = Description.TYPE.MEHTOD)
    public Result updateApprovalDocResponse (CommonReqVo commonReqVo, @RequestBody Map<String, String> requestParams) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("결재 공유 문서 정보 수정");

        int result = documentService.updateDocSharedHistory(requestParams);

        if(result <= 0){
            Result.ok().put("resultCode", "01");
        }

        return Result.ok().put("resultCode", "00");
    }


    //####################################[네비게이션 관련]####################################
    @GetMapping("/navigation/check/type")
    public Result checkHasNavigationType(CommonReqVo commonReqVo,DocumentForm.CheckHasFolderType checkHasFolderType) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("폴더명 중복 체크");
        systemLogComponent.addUserLog(userLog);

        String result = documentService.checkHasNavigationType(checkHasFolderType);

        return Result.ok().put("checkHasNavigationType",result);
    }
    

    /**
     * 네비게이션 리스트 조회
     */
    @PostMapping("/navigation/list/ex")
    @ApiResponse(description = "네비게이션 리스트 조회")
    @Description(name = "네비게이션 리스트 조회", description = "문서 네비게이션 리스트 조회", type = Description.TYPE.MEHTOD)
    public Result documentNaviListToApproval(CommonReqVo commonReqVo, @RequestBody Map<String, String> inputParam) {
        log.info("INPUTPARAM : {}", inputParam);

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("전자결재 문서 네비게이션 리스트 조회");
        systemLogComponent.addUserLog(userLog);

        String naviDiv = "01"; // 통합문서관리
        String cntrctNo = inputParam.get("cntrctNo");
        String naviId = String.format("%s_%s", naviDiv, cntrctNo);
        String loginId = inputParam.get("loginId");

        boolean isAdmin = commonReqVo.getAdmin();

        log.debug("loginId 					: >>>>> " + inputParam.get("loginId"));
        log.debug("cntrctNo 					: >>>>> " + inputParam.get("cntrctNo"));
        log.debug("naviId 						: >>>>> " + naviId);


        return Result.ok()
                .put("navigationList",
                        documentService.getDocumentNavigationList(isAdmin, naviId, loginId));
    }

    @GetMapping("main-data")
    public Result getDocumentMainData(@RequestParam("pjtNo") String pjtNo,@RequestParam("cntrctNo") String cntrctNo, @RequestParam("pjtType") String pjtType, @NotBlank @RequestParam("menuId") String menuId,@RequestParam("loginId") String loginId,@RequestParam("isAdmin") Boolean isAdmin,@RequestParam("naviId") String naviId, @RequestParam("cmnGrpCd")String cmnGrpCd){
        List<Map<String, ?>> naviAuthority = null;
        if(isAdmin){
            naviAuthority = new ArrayList<>();
            Map<String, String> hashMap = new HashMap<>(1);

            hashMap.put("rght_kind", "A");
            naviAuthority.add(hashMap);
        }else {
            naviAuthority = documentService.getDocumentNavigationListAuthority(cmnGrpCd, pjtNo, cntrctNo, pjtType, menuId, loginId);
        }
        List<String> availableFileExt = documentService.getAvailableFileExt();

        List<DocumentDto.NavigationTree> navigationList = documentService.getDocumentNavigationList(isAdmin, naviId, loginId).stream()
                .map(documentDto::toNavigationTree).collect(Collectors.toList());

        return Result.ok()
                .put("naviAuthority",naviAuthority)
                .put("availableFileExt",availableFileExt)
                .put("navigationList",navigationList);
    }

    /**
     * 승인 완료 결재 문서 생성
     */
    @PostMapping("/approval/document/create")
    @ApiResponse(description = "결재 승인 문서 생성")
    @Description(name = "결재 승인 문서 생성", description = "승인 완료된 결재 문서 생성", type = Description.TYPE.MEHTOD)
    public Result createApprovalDocument(CommonReqVo commonReqVo, @RequestBody Map<String, Object> inputParam) {
        log.info("INPUTPARAM : {}", inputParam);

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("결재 승인 문서 생성");
        systemLogComponent.addUserLog(userLog);

        Map<String, Object> result = documentService.createApprovalDocument(inputParam);
        String resultMsg = result != null && !"00".equals(result.get("resultCode").toString()) ?
                            result.get("resultMsg").toString() : "internal server error";

        if(result != null && "00".equals(result.get("resultCode"))){
            return Result.ok().put("resultCode", "00").put("resultMsg", "success");
        }

        return Result.nok(ErrorType.INTERNAL_SERVER_ERROR, resultMsg);
    }

    @GetMapping("/folder/check/exist")
    @Description(name = "폴더명 중복 체크", description = "문서 폴더명 중복 체크(결과: Y / N)", type = Description.TYPE.MEHTOD)
    public Result checkFolderExist(CommonReqVo commonReqVo,
    String naviId,String upDocId, String docNm) {

        return Result.ok().put("checkFolderExist",
                documentService.checkFolderExist(naviId, upDocId, docNm));

    }
}
