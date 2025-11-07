package kr.co.ideait.platform.gaiacairos.web.entrypoint.document;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import kr.co.ideait.platform.gaiacairos.comp.portal.PortalComponent;
import kr.co.ideait.platform.gaiacairos.comp.system.service.CommonCodeService;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.*;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.construction.resource.ResourceMybatisParam.RawGovsplyMtrlItem;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.construction.resource.ResourceMybatisParam.RawLbrEqList;
import kr.co.ideait.platform.gaiacairos.comp.construction.service.ResourceService;
import kr.co.ideait.platform.gaiacairos.comp.document.DocumentComponent;
import kr.co.ideait.platform.gaiacairos.comp.document.service.DocumentService;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.eapproval.draft.DraftMybatisParam;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.contract.contractstatus.ContractstatusMybatisParam.RawCbsItem;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.contract.contractstatus.ContractstatusMybatisParam.RawContractItem;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.contract.contractstatus.ContractstatusMybatisParam.RawCostItem;
import kr.co.ideait.platform.gaiacairos.comp.project.service.ContractstatusService;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.document.DocumentDto;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.document.DocumentForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.commoncode.CommonCodeDto;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.document.ConstructionBeginsDocDto;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.config.security.annotation.IsUser;
import kr.co.ideait.platform.gaiacairos.core.constant.CommonCodeConstants;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.GridResult;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.type.DocumentType;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import kr.co.ideait.platform.gaiacairos.core.type.PlatformType;
import kr.co.ideait.platform.gaiacairos.core.util.FileResource;
import kr.co.ideait.platform.gaiacairos.core.util.FileService;
import kr.co.ideait.platform.gaiacairos.core.util.excel.*;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.core.util.restclient.DocumentServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.tika.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@IsUser
@RestController
@RequestMapping({"/api/document"})
public class DocumentApiController extends AbstractController {

    @Autowired
    DocumentService documentService;

    @Autowired
    CommonCodeService commonCodeService;

    @Autowired
    ContractstatusService contractstatusService;

    @Autowired
    ResourceService resourceService;

    @Autowired
    ExcelCostWriterService excelCostWriterService;

    @Autowired
    DocumentComponent documentComponent;

    @Autowired
    DocumentDto documentDto;

    @Autowired
    CommonCodeDto commonCodeDto;

    @Autowired
    DocumentForm documentForm;

    @Autowired
    FileService fileService;

    @Value("${spring.application.name}")
    String pjtType;

    /**
     * 통합검색
     */
    @PostMapping("/search")
    @ApiResponse(description = "통합검색")
    @Description(name = "통합검색", description = "통합검색", type = Description.TYPE.MEHTOD)
    public GridResult search(CommonReqVo commonReqVo, @RequestBody Map<String, Object> params, HttpServletRequest request) {
        log.info("params : {}", params);

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("통합검색");

        params.put("refSysKey", "1");

        Map<String, Object> resultMap = documentComponent.search(params);
        String result = (String) resultMap.get("result");
        if(!"success".equals(result)) {
            return GridResult.nok(ErrorType.BAD_REQUEST);
        }

        Page<Object> resultData = (Page<Object>) resultMap.get("searchResult");

        return GridResult.ok(resultData);
    }

    //####################################[네비게이션 관련]####################################

    /**
     * 메인 화면의 트리 전시를 위한 네비게이션 목록 데이터
     */
    @PostMapping("/navigation/list")
    @ApiResponse(description = "네비게이션 리스트 조회")
    @Description(name = "네비게이션 리스트 조회", description = "문서 네비게이션 리스트 조회", type = Description.TYPE.MEHTOD)
    public Result documentNaviList(CommonReqVo commonReqVo, HttpServletRequest request, UserAuth user,
                                   @Valid @RequestBody DocumentForm.NavigationList inputParam) {
        log.info("INPUTPARAM : {}", inputParam);

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("네비게이션 리스트 조회");
        systemLogComponent.addUserLog(userLog);

        HashMap<String,Object> result = documentComponent.getDocumentMainData(inputParam,commonReqVo);

        return Result.ok()
                .put("navigationList",result.get("navigationList"))
                .put("naviAuthority", result.get("naviAuthority"))
                .put("contractList", result.get("contractList"))
                .put("availableFileExt", result.get("availableFileExt"));
    }
    /**
     * 네비게이션 경로명 중복 체크
     */
    @PostMapping("/naviExist")
    @ApiResponse(description = "네비게이션 경로명 중복 체크")
    @Description(name = "네비게이션 경로명 중복 체크", description = "문서 네비게이션 경로명 중복 체크(결과: Y / N)", type = Description.TYPE.MEHTOD)
    public Result naviExist(CommonReqVo commonReqVo, @Valid @RequestBody DocumentForm.NaviExist inputParam) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("네비게이션 경로명 중복 체크");
        systemLogComponent.addUserLog(userLog);

        return Result.ok().put("addNaviExist", documentService.addNaviExist(inputParam.getNaviDiv(),
                inputParam.getUpNaviId(), inputParam.getNaviNm()));
    }
    /**
     * 폴더(네비게이션)종류 중복 체크
     */
    @PostMapping("/navigation/check/type")
    @ApiResponse(description = "폴더(네비게이션)종류 중복 체크")
    @Description(name = "폴더(네비게이션)종류 중복 체크", description = "문서 폴더(네비게이션)종류 중복 체크(결과: Y / N)", type = Description.TYPE.MEHTOD)
    public Result checkHasNavigationType(CommonReqVo commonReqVo, @Valid @RequestBody DocumentForm.CheckHasFolderType inputParam) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("폴더종류 중복 체크");
        systemLogComponent.addUserLog(userLog);

//        return Result.ok().put("result", documentService.checkHasFolderType(inputParam));
        return Result.ok().put("result", documentComponent.checkHasNavigationType(inputParam));
    }

    //####################################[폴더 관련]####################################

    /**
     * 폴더명 중복 체크
     */
    @PostMapping("/folder/check/exist")
    @ApiResponse(description = "폴더명 중복 체크")
    @Description(name = "폴더명 중복 체크", description = "문서 폴더명 중복 체크(결과: Y / N)", type = Description.TYPE.MEHTOD)
    public Result checkFolderExist(CommonReqVo commonReqVo, @Valid @RequestBody DocumentForm.DocExist inputParam) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("폴더명 중복 체크");
        systemLogComponent.addUserLog(userLog);

        return Result.ok().put("checkFolderExist",
                documentComponent.checkFolderExist(inputParam.getNaviId(), inputParam.getUpDocId(), inputParam.getDocNm()));
    }





    /**
     * 신규 네비게이션
     * (네비게이션 정보) => (저장) => 네비게이션
     */
    @PostMapping("/navigation/create")
    @ApiResponse(description = "네비게이션 경로 생성")
    @Description(name = "네비게이션 경로 생성", description = "문서 네비게이션 경로 생성", type = Description.TYPE.MEHTOD)
    public Result createNavigation(CommonReqVo commonReqVo, @Valid @RequestBody DocumentForm.NavigationCreate navigation, UserAuth user) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("네비게이션 경로 생성");
        systemLogComponent.addUserLog(userLog);

        navigation.setNaviId(java.util.UUID.randomUUID().toString());
        navigation.setRgstrId(commonReqVo.getUserId());
        navigation.setChgId(commonReqVo.getUserId());

        return Result.ok().put("navigation",
                documentComponent.createNavigation(navigation).map(documentDto::toSimpleNavigation));
    }

    /**
     * 네비게이션 수정
     * (네비게이션 정보) => (저장) => 네비게이션
     */
    @PostMapping("/navigation/update")
    @ApiResponse(description = "네비게이션 경로 수정")
    @Description(name = "문서 네비게이션 이름 변경", description = "문서 네비게이션 이름 변경", type = Description.TYPE.MEHTOD)
    public Result updateNavigation(CommonReqVo commonReqVo, @Valid @RequestBody DocumentForm.NavigationUpdate navigation, UserAuth user) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("문서 네비게이션 이름 변경");
        systemLogComponent.addUserLog(userLog);

        DcNavigation dcNavigation = documentService.getNavigation(navigation.getNaviNo());
        if (dcNavigation != null) {
            documentForm.updateDcNavigation(navigation, dcNavigation);
            return Result.ok().put("navigation", documentService.updateNavigation(dcNavigation)
                    .map(documentDto::toSimpleNavigation));
        }
        throw new GaiaBizException(ErrorType.NO_DATA);

    }

    /**
     * 문서폴더 생성
     * (문서폴더 (Strign)) => (저장) => 파일 폴더
     */
    @PostMapping("/file-folder/create")
    @ApiResponse(description = "문서폴더 생성")
    @Description(name = "문서폴더 생성", description = "저장할 문서 중 폴더 문서 생성", type = Description.TYPE.MEHTOD)
    public Result createFileFolder(CommonReqVo commonReqVo, @Valid @RequestBody DocumentForm.DocCreate doc, UserAuth user) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("문서폴더 생성");
        systemLogComponent.addUserLog(userLog);

        DcStorageMain dcStorageMain = documentForm.toDcStorageMain(doc);
        dcStorageMain.setDocType(DocumentType.FOLDER.toString());
        dcStorageMain.setDocId(java.util.UUID.randomUUID().toString());
        dcStorageMain.setDltYn("N");
        log.debug("==========================================================");
        log.debug("==========================================================");
        log.debug("dcStorageMain : >>>>>> " + dcStorageMain.toString());
        log.debug("==========================================================");
        log.debug("==========================================================");

        return Result.ok().put("folder",
                documentComponent.createDcStorageMain(dcStorageMain).map(documentDto::toSimpleDocument));
    }

    /**
     * 네비게이션 권한그룹 조회
     */
    @PostMapping("/navigation-authority/list")
    @Description(name = "문서 네비게이션 권한그룹 조회", description = "문서 경로 권한 설정을 위한 데이터 조회", type = Description.TYPE.MEHTOD)
    public Result documentNavigationAuthorityList(CommonReqVo commonReqVo, HttpServletRequest request,
                                                  @Valid @RequestBody DocumentForm.AuthorityList authority,
                                                  @CookieValue(name = "lang", required = false, defaultValue = "ko") String langInfo) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("문서 네비게이션 권한그룹 조회");
        systemLogComponent.addUserLog(userLog);

        String[] param = commonReqVo.getUserParam();
        String cmnGrpCd = CommonCodeConstants.AKIND_CODE_GROUP_CODE;

        // pjtType이 'PGAIA'인 경우, GAIA로 변경.
        if ("PGAIA".equals(param[2])) {
            param[2] = PlatformType.GAIA.getName().toUpperCase();
        }
        return Result.ok()
                .put("authorityList", documentService
                        .getDocumentNavigationAuthorityList(param[2], authority.getNaviNo(), authority.getNaviId(),
                                authority.getUpNaviNo(), authority.getUpNaviId(), authority.getPjtNo(),
                                authority.getCntrctNo(), authority.getNaviLevel())
                        .stream()
                        .map(documentDto::toSimpleAuthorityList))
                .put("rghtTyList", commonCodeService.getCommonCodeListByGroupCode(cmnGrpCd).stream()
                        .map(smComCode -> {
                            CommonCodeDto.CommonCodeCombo codeCombo = commonCodeDto.fromSmComCodeToCombo(smComCode);
                            codeCombo.setCmnCdNm(
                                    langInfo.equals("en") ? smComCode.getCmnCdNmEng() : smComCode.getCmnCdNmKrn());
                            return codeCombo;
                        }));
    }

    /**
     * 네비게이션 권한설정 셋팅 (입력, 수정, 삭제)
     */
    @PostMapping("/navigation-authority/setup")
    @Description(name = "문서 네비게이션 권한설정", description = "문서 경로 권한 설정 - actionType(ADD,DEL,UPDATE)에 따라 추가, 수정, 삭제 진행", type = Description.TYPE.MEHTOD)
    public Result naviAuthorityGroupUserList(CommonReqVo commonReqVo, @Valid @RequestBody DocumentForm.SetAuthorityList authority) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("문서 네비게이션 권한설정");
        systemLogComponent.addUserLog(userLog);

        String authType = "navi";

        return documentComponent.setNaviAuthorityList(authority.getSetAuthorityList(), authority.getAllYn(), authType);

    }

    /**
     * 네비게이션 권한설정 권한사용자 조회
     */
    @PostMapping("/navigation-authority/user-list")
    @Description(name = "문서 네비게이션 권한설정 권한사용자 조회", description = "해당 권한그룹에 속한 권한 사용자 리스트 조회", type = Description.TYPE.MEHTOD)
    public Result selectNaviAuthorityGroupUserList(CommonReqVo commonReqVo, HttpServletRequest request,
            @Valid @RequestBody DocumentForm.SelectNaviAuthorityGroupUserList param) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("문서 네비게이션 권한설정 권한사용자 조회");
        systemLogComponent.addUserLog(userLog);

        String userInfo = cookieService.getCookie(request, cookieVO.getPortalCookieName());

        log.debug(param.toString());
        return Result.ok()
                .put("naviAuthorityGroupUserList",
                        documentService.getNaviAuthorityGroupUserList(Integer.parseInt(param.getRghtGrpNo()),
                                param.getRghtGrpCd(), CommonCodeConstants.PSTN_CODE_GROUP_CODE))
                .put("userInfo", userInfo);
    }

    /**
     * 네비게이션 속성 리스트 조회 (속성 복사 리스트 조회)
     */
    @GetMapping("/{navigationId}/property/list")
    @Description(name = "문서 네비게이션 속성 리스트 조회", description = "문서 경로 속성 리스트 조회", type = Description.TYPE.MEHTOD)
    public Result documentNavigationAuthorityList(CommonReqVo commonReqVo, @PathVariable("navigationId") String navigationId, UserAuth user,
            @RequestParam(value = "type", required = false) String type) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("문서 네비게이션 속성 리스트 조회");
        systemLogComponent.addUserLog(userLog);

        List<Object> propertyList = documentService.getDocumentNavigationPropertyList(navigationId).stream()
                .map("copy".equals(type) ? documentDto::toSimpleCopyProperty : documentDto::toSimpleProperty)
                .collect(Collectors.toList());

        return Result.ok().put("propertyList", propertyList);
    }

    /**
     * 네비게이션 속성 추가 > 속성 종류, 속성 타입, 속성 타입 종류 콤보박스 데이터 조회
     */
    @GetMapping("/property/code-combo-list")
    @Description(name = "문서 네비게이션 속성 추가 데이터 조회", description = "속성 종류, 속성 타입, 속성 타입 종류 콤보박스 데이터 조회", type = Description.TYPE.MEHTOD)
    public Result documentNavigationSetPropertyOptionData(CommonReqVo commonReqVo, 
            @CookieValue(name = "lang", required = false, defaultValue = "ko") String langInfo) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("문서 네비게이션 속성 추가 데이터 조회");
        systemLogComponent.addUserLog(userLog);

        List<String> cmnGrpCdList = new ArrayList<>();
        cmnGrpCdList.add(CommonCodeConstants.ATTBTKIND_CODE_GROUP_CODE);
        cmnGrpCdList.add(CommonCodeConstants.ATTBTTYPE_CODE_GROUP_CODE);

        List<Map<String, Object>> attrbtTypeSelOptions = commonCodeService.getAttrbtTypeSelOptions(langInfo);
        List<Map<String, Object>> attrbtHtmlOptions = documentService.getHtmlformList();

        Map<String, List<Map<String, Object>>> codeComboMap = commonCodeService
                .getCommonCodeListByGroupCode(cmnGrpCdList, langInfo);

        codeComboMap.put("attrbtTypeSel", attrbtTypeSelOptions);
        codeComboMap.put("attrbtHtmlOptions", attrbtHtmlOptions);

        return Result.ok().put("codeComboMap", codeComboMap);
    }

    /**
     * 네비게이션 속성 코드 중복체크
     */
    @PostMapping("/property/attrbtCd/exist")
    @Description(name = "문서 네비게이션 속성 코드 중복체크", description = "문서 경로 속성 추가 시, 속성 코드 중복 체크", type = Description.TYPE.MEHTOD)
    public Result attributeCodeExist(CommonReqVo commonReqVo, @Valid @RequestBody DocumentForm.AttrbtCdExist inputParam) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("문서 네비게이션 속성 코드 중복체크");
        systemLogComponent.addUserLog(userLog);

        DcProperty existProperty = documentService.attrbtCdExist(inputParam.getAttrbtCd(), inputParam.getNaviId());

        String exist = "";
        if (existProperty == null) {
            exist = "N";
        } else {
            exist = "Y";
        }

        return Result.ok().put("attrbtCdExist", exist);
    }

    /**
     * 네비게이션 속성 추가
     * 
     */
    @PostMapping("/navigation-property/create")
    @Description(name = "문서 네비게이션 속성 추가", description = "문서 경로의 속성 추가", type = Description.TYPE.MEHTOD)
    public Result createNavigationProperty(CommonReqVo commonReqVo, @Valid @RequestBody DocumentForm.PropertyCreate property, UserAuth user) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("문서 네비게이션 속성 추가");
        systemLogComponent.addUserLog(userLog);

        DcProperty dcProperty = documentForm.toDcProperty(property);
        dcProperty.setRgstrId(commonReqVo.getUserId());
        return Result.ok().put("property", documentService.createProperty(dcProperty)
                .map(documentDto::toSimpleProperty));
    }

    /**
     * 네비게이션 속성 수정
     */
    @PostMapping("/navigation-property/update")
    @Description(name = "문서 네비게이션 속성 수정", description = "문서 경로의 속성 수정", type = Description.TYPE.MEHTOD)
    public Result updateNavigationProperty(CommonReqVo commonReqVo, @Valid @RequestBody DocumentForm.PropertyUpdate property, UserAuth user) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("문서 네비게이션 속성 수정");
        systemLogComponent.addUserLog(userLog);

        DcProperty dcProperty = documentService.getProperty(property.getAttrbtNo());
        if (dcProperty != null) {
            documentForm.updateDcProperty(property, dcProperty);
            return Result.ok().put("property", documentService.updateProperty(dcProperty)
                    .map(documentDto::toSimpleProperty));
        }
        throw new GaiaBizException(ErrorType.NO_DATA);
    }

    /**
     * 네비게이션 속성 삭제
     */
    @PostMapping("/navigation-property/delete")
    @Description(name = "문서 네비게이션 속성 삭제", description = "문서 경로의 속성 삭제", type = Description.TYPE.MEHTOD)
    public Result deleteNavigationProperty(CommonReqVo commonReqVo, @Valid @RequestBody DocumentForm.PropertyDelete property, UserAuth user) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("문서 네비게이션 속성 삭제");
        systemLogComponent.addUserLog(userLog);

        documentService.deleteProperty(property.getAttrbtNoList());
        return Result.ok();
    }

    /**
     * 문서(아이템형) 아이템 추가 > 네비게이션 속성 리스트 html 요소 생성
     */
    @PostMapping("/add-item/html/create")
    @Description(name = "속성 정보 수정 html 요소 데이터 조회", description = "속성 정보 수정 시, html 입력 요소 생성을 위해 필요한 데이터 조회", type = Description.TYPE.MEHTOD)
    public Result getPropertyToHtml(CommonReqVo commonReqVo, @Valid @RequestBody DocumentForm.propertyHtmlParam data,
            @CookieValue(name = "lang", required = false, defaultValue = "ko") String langInfo) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("속성 정보 수정 html 요소 데이터 조회");
        systemLogComponent.addUserLog(userLog);

        // actionTy 값 검증
        if (data.getActionTy() != null && !"update".equals(data.getActionTy())) {
            return Result.nok(ErrorType.BAD_REQUEST);
        }

        MybatisInput input = MybatisInput.of().add("naviId", data.getNaviId())
                .add("docNo", data.getDocNo())
                .add("lang", langInfo);
        // grid contextMenu의 속성 정보 수정인 경우,
        if ("update".equals(data.getActionTy())) {
            input.add("actionTy", data.getActionTy());
        }

        List<Map<String, ?>> result = documentService.getPropertyToStringHtmlElements(input);

        return Result.ok().put("propertyList", result);
    }

    /**
     * ITEM 문서 추가
     */
    @PostMapping("/file-item/create")
    @Description(name = "문서 아이템 추가", description = "문서 경로 종류가 'ITEM'인 경로에 문서 아이템 추가", type = Description.TYPE.MEHTOD)
    public Result createDocItem(CommonReqVo commonReqVo, @Valid @RequestPart(value = "storageData") DocumentForm.DocCreate doc,
            @RequestPart(value = "attrbtData") DocumentForm.ItemAttbtDataCreate attrbtDataList,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @RequestPart(value = "subFiles", required = false) List<MultipartFile> newSubFiles,
            @RequestParam(value = "subFileAttrbtCd", required = false) List<String> subFileAttrbtCds)
            throws IllegalStateException, IOException {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("문서 아이템 추가");
        systemLogComponent.addUserLog(userLog);


        // 입력 값 검증
        if (doc == null) {
            return Result.nok(ErrorType.NO_DATA, "Invalid document data.");
        }

        DcStorageMain dcStorageMain = documentForm.toDcStorageMain(doc);
        dcStorageMain.setDocType(DocumentType.ITEM.toString());
        dcStorageMain.setDocId(java.util.UUID.randomUUID().toString());
        dcStorageMain.setDltYn("N");

        List<DcPropertyData> propertyDataList = attrbtDataList.getAttrbtDataList().stream().map(attrbtData -> {
            DcPropertyData propertyData = new DcPropertyData();
            propertyData.setAttrbtCd(attrbtData.getAttrbtCd());
            propertyData.setAttrbtCntnts(attrbtData.getAttrbtCntnts());

            return propertyData;
        }).toList();

        documentService.createDocItem(dcStorageMain, propertyDataList, doc.getCntrctNo(),files, newSubFiles, subFileAttrbtCds);

        return Result.ok();
    }

    /**
     * 네비게이션 권한 복사 - 권한 존재 여부 확인.
     */
    @PostMapping("/navigation-authority/copy")
    @Description(name = "문서 네비게이션 권한 복사", description = "문서 경로 권한 복사 시, 권한 존재 여부 확인", type = Description.TYPE.MEHTOD)
    public Result copyNavigationAuthority(CommonReqVo commonReqVo, @Valid @RequestBody DocumentForm.CopyParam copyParam) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("문서 네비게이션 권한 복사");
        systemLogComponent.addUserLog(userLog);

        List<DcAuthority> result = documentService.getAuthority(copyParam.getTargetId(), copyParam.getTargetNo());

        return Result.ok().put("authorities", result);

    }

    /**
     * 네비게이션 권한 붙이기
     */
    @PostMapping("/navigation-authority/paste")
    @Description(name = "문서 네비게이션 권한 붙이기", description = "문서 경로 권한 복사 시, 권한 붙이기", type = Description.TYPE.MEHTOD)
    public Result pasteNavigationAuthority(CommonReqVo commonReqVo, @Valid @RequestBody DocumentForm.AuthorityPaste authorityPaste,
            UserAuth user) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("문서 네비게이션 권한 붙이기");
        systemLogComponent.addUserLog(userLog);

        MybatisInput copyAuthorityInput = MybatisInput.of().add("targetId", authorityPaste.getTargetId())
                .add("targetNo", authorityPaste.getTargetNo())
                .add("sourceId", authorityPaste.getSourceId())
                .add("sourceNo", authorityPaste.getSourceNo())
                .add("usrId", user.getUsrId());

        documentService.pasteAuthority(copyAuthorityInput);

        return Result.ok();
    }

    /**
     * 네비게이션 속성 복사 - 속성 존재 여부 확인.
     */
    @PostMapping("/navigation-property/copy")
    @Description(name = "문서 네비게이션 속성 복사", description = "문서 경로 속성 복사 시, 속성 존재 여부 확인", type = Description.TYPE.MEHTOD)
    public Result copyNavigationProperty(CommonReqVo commonReqVo, @Valid @RequestBody DocumentForm.CopyParam copyParam) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("문서 네비게이션 속성 복사");
        systemLogComponent.addUserLog(userLog);

        List<DcProperty> result = documentService.getNavigationProperty(copyParam.getTargetId(),
                copyParam.getTargetNo());

        return Result.ok().put("properties", result);

    }

    /**
     * 네비게이션 속성 붙이기
     */
    @PostMapping("/navigation-property/paste")
    @Description(name = "문서 네비게이션 속성 붙이기", description = "문서 경로 속성 복사 시, 속성 붙이기", type = Description.TYPE.MEHTOD)
    public Result copyNavigationProperty(CommonReqVo commonReqVo, @Valid @RequestBody DocumentForm.PropertyPaste propertyPaste, UserAuth user) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("문서 네비게이션 속성 붙이기");
        systemLogComponent.addUserLog(userLog);

        MybatisInput copyPropertyInput = MybatisInput.of().add("targetId", propertyPaste.getTargetId())
                .add("targetNo", propertyPaste.getTargetNo())
                .add("sourceId", propertyPaste.getSourceId())
                .add("sourceNo", propertyPaste.getSourceNo())
                .add("usrId", user.getUsrId());
        documentService.pasteNavigationProperty(copyPropertyInput);

        return Result.ok();
    }

    /**
     * 문서리스트 조회
     * 
     * @param docListGet
     * @param user
     * @return
     */
    @GetMapping("/grid-list")
    @Description(name = "문서 리스트 조회", description = "문서 경로에 해당하는 문서 리스트를 JSON 구조로 반환", type = Description.TYPE.MEHTOD)
    public GridResult getDocumentList(CommonReqVo commonReqVo, @Valid DocumentForm.DocListGet docListGet, UserAuth user) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("문서 리스트 조회");
        systemLogComponent.addUserLog(userLog);

        if (docListGet.getUpDocId() == null) {
            docListGet.setUpDocId("#");
        }

        MybatisInput docListGetInput = MybatisInput.of().add("naviId", docListGet.getNaviId())
                .add("upDocId", docListGet.getUpDocId())
                .add("loginId", user.getLogin_Id())
                .add("isAdmin", user.isAdmin())
                .add("pageable", docListGet.getPageable());

        if (docListGet.getColumnNm() != null && docListGet.getSearchText() != null) {
            docListGetInput.add("searchText", docListGet.getSearchText());
            docListGetInput.add("columnNm", docListGet.getColumnNm());
        }

        docListGetInput.setPageable(docListGet.getPageable());

        Page<JsonNode> jsonResult = documentService.getDocumentList(docListGetInput);

        return GridResult.ok(jsonResult);
    }

    /**
     * 속성(컬럼명)리스트 조회
     */
    @GetMapping("/grid-property-list/{naviId}")
    @Description(name = "문서 속성 리스트 조회", description = "문서 경로에 해당하는 속성 리스트를 JSON 구조로 반환", type = Description.TYPE.MEHTOD)
    public Result getPropertyList(CommonReqVo commonReqVo, @PathVariable("naviId") String naviId,
            @CookieValue(name = "lang", required = false) String langInfo) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("문서 속성 리스트 조회");
        systemLogComponent.addUserLog(userLog);

        return Result.ok().put("propertyList", documentService.getPropertyList(naviId, langInfo));
    }

    /**
     * 문서명 중복 체크
     */
    @PostMapping("/name/exist")
    @ApiResponse(description = "문서명 중복 체크")
    @Description(name = "문서명 중복 체크", description = "문서명 중복 체크(결과: Y / N)", type = Description.TYPE.MEHTOD)
    public Result docExist(CommonReqVo commonReqVo, @Valid @RequestBody DocumentForm.DocExist inputParam) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("문서명 중복 체크");
        systemLogComponent.addUserLog(userLog);

        return Result.ok().put("docNmExist",
                documentService.updateDocExist(inputParam.getNaviId(), inputParam.getUpDocId(), inputParam.getDocNm()));
    }

    /**
     * 문서 이름 변경
     */
    @PostMapping("/name/update")
    @Description(name = "문서 이름 변경", description = "문서 이름 변경", type = Description.TYPE.MEHTOD)
    public Result updateDocName(CommonReqVo commonReqVo, @Valid @RequestBody DocumentForm.DocUpdate doc, UserAuth user) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("문서 이름 변경");
        systemLogComponent.addUserLog(userLog);

        DcStorageMain dcStorageMain = documentService.getDcStorageMain(doc.getDocId());

        String newDocNm = doc.getDocNm();
        String currentDocNm = dcStorageMain.getDocNm();
        int lastDotIndex = currentDocNm.lastIndexOf('.');
        String extension = lastDotIndex != -1 ? currentDocNm.substring(lastDotIndex) : "";

        // 확장자를 제외한 새 이름 설정
        if (newDocNm.contains(".")) {
            newDocNm = newDocNm.substring(0, newDocNm.lastIndexOf('.')); // 확장자 제거
        }

        dcStorageMain.setDocNm(newDocNm + extension);
        return Result.ok().put("document",
                documentService.updateDocument(dcStorageMain, user).map(documentDto::toSimpleDocument));
    }

    /**
     * 속성 정보 수정
     * resultCode
     * - 01 , 00 : 성공
     * - 02 : 착공계 문서 템플릿 조회 실패
     * - 03 : 윈도우 서버 통신 실패
     */
    @PostMapping("/property-data/update")
    @Description(name = "문서 속성 정보 수정", description = "문서 속성 정보 수정 - 착공계 문서일 경우, 문서 24 통신 후 처리", type = Description.TYPE.MEHTOD)
    public Result updatePropertyData(CommonReqVo commonReqVo, @RequestPart(value = "attrbtData") @Valid DocumentForm.PropertyDataUpdate propData,
                                     @RequestPart(value = "files", required = false) List<MultipartFile> newFiles,
                                     @RequestPart(value = "subFiles", required = false) List<MultipartFile> newSubFiles,
                                     @RequestParam(value = "subFileAttrbtCd", required = false) List<String> subFileAttrbtCds,
                                     @RequestParam(value = "removedFiles[]", required = false) List<Integer> removedFileNos,
                                     @RequestParam(value = "removedSubFiles[]", required = false) List<Integer> removedSubFileNos,
                                     UserAuth user) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("문서 속성 정보 수정");
        systemLogComponent.addUserLog(userLog);

        Map<String, Object> result = documentComponent.updatePropertyData(propData, newFiles, newSubFiles, subFileAttrbtCds, removedFileNos,
                removedSubFileNos, user);

        if("02".equals(result.get("resultCode"))){
            return Result.nok(ErrorType.NOT_FOUND, (String)result.get("resultMsg"));
        }
        else if("03".equals(result.get("resultCode"))){
            return Result.nok(ErrorType.INTERNAL_SERVER_ERROR, (String)result.get("resultMsg"));
        }

        return Result.ok();
    }

    /**
     * 문서 권한그룹 조회
     */
    @PostMapping("/document-authority/list")
    @Description(name = "문서 권한그룹 조회", description = "문서 권한 설정을 위한 데이터 조회", type = Description.TYPE.MEHTOD)
    public Result documentAuthorityList(CommonReqVo commonReqVo, HttpServletRequest request,
            @Valid @RequestBody DocumentForm.DocAuthorityList authority,
            @CookieValue(name = "lang", required = false, defaultValue = "ko") String langInfo) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("문서 권한그룹 조회");
        systemLogComponent.addUserLog(userLog);

        String cmnGrpCd = CommonCodeConstants.AKIND_CODE_GROUP_CODE;

        return Result.ok()
                .put("authorityList",
                        documentService
                                .getDocumentAuthorityList(pjtType, authority.getDocNo(), authority.getDocId(),
                                        authority.getNaviNo(), authority.getNaviId(), authority.getUpDocNo(),
                                        authority.getUpDocId(), authority.getPjtNo(), authority.getCntrctNo())
                                .stream()
                                .map(documentDto::toSimpleAuthorityList))
                .put("rghtTyList", commonCodeService.getCommonCodeListByGroupCode(cmnGrpCd).stream()
                        .map(smComCode -> {
                            CommonCodeDto.CommonCodeCombo codeCombo = commonCodeDto.fromSmComCodeToCombo(smComCode);
                            codeCombo.setCmnCdNm(
                                    langInfo.equals("en") ? smComCode.getCmnCdNmEng() : smComCode.getCmnCdNmKrn());
                            return codeCombo;
                        }));
    }

    /**
     * 문서 권한설정 셋팅 (입력, 수정, 삭제)
     */
    @PostMapping("/document-authority/setup")
    @Description(name = "문서 권한설정", description = "문서 권한 설정 - actionType(ADD,DEL,UPDATE)에 따라 추가, 수정, 삭제 진행", type = Description.TYPE.MEHTOD)
    public Result docAuthorityGroupUserList(CommonReqVo commonReqVo, @Valid @RequestBody DocumentForm.SetAuthorityList authority) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("문서 권한설정");
        systemLogComponent.addUserLog(userLog);


        //컴포넌트 호출로 변경.
        return documentComponent.setNaviAuthorityList(authority.getSetAuthorityList(), authority.getAllYn(), "doc");

    }

    /**
     * 문서 업로드
     */
    @PostMapping("/file/create")
    @Description(name = "문서 업로드", description = "문서 경로 종류가 폴더형(FOLDR)인 경로에 파일 저장 및 DB 데이터 저장", type = Description.TYPE.MEHTOD)
    public Result createDocument(CommonReqVo commonReqVo, @Valid @RequestPart(value = "docData") DocumentForm.DocCreate doc,
            @RequestPart(value = "files", required = false) List<MultipartFile> files)
            throws IllegalStateException, IOException {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("문서 업로드");
        systemLogComponent.addUserLog(userLog);


        log.debug("============request data=============");
        log.debug("docInfo={}", doc);
        log.debug("fileInfo={}", files);
        log.debug("============request data=============");

        // 입력 값 검증
        if (doc == null) {
            return Result.nok(ErrorType.NO_DATA, "Invalid document data.");
        }

        DcStorageMain dcStorageMainForm = documentForm.toDcStorageMain(doc);
        dcStorageMainForm.setDocType(DocumentType.FILE.toString());
        dcStorageMainForm.setDocId(java.util.UUID.randomUUID().toString());
        dcStorageMainForm.setDltYn("N");

        documentComponent.addDocumentList(dcStorageMainForm, files, doc.getCntrctNo());

        return Result.ok();
    }

    /**
     * 폴더 업로드
     */
    @PostMapping("/folder/upload")
    public Result uploadFilesByFolder(CommonReqVo commonReqVo, @RequestPart("files") List<MultipartFile> files, @RequestPart("dcStorageMainList") List<DcStorageMain> dcStorageMainList) {
        if(documentComponent.uploadFilesByFolder(files,dcStorageMainList, commonReqVo)){
            return Result.ok();
        }
        return Result.nok(ErrorType.ETC,"ETC...");
    }

    /**
     * 문서 다운로드
     */
    @GetMapping("/file/{docId}/download")
    @Description(name = "문서 다운로드", description = "문서경로 폴더형(FOLDR) 문서 단일 파일 다운로드", type = Description.TYPE.MEHTOD)
    public ResponseEntity<Resource> downloadFile(CommonReqVo commonReqVo, @PathVariable("docId") String docId, UserAuth user) {
        DcStorageMain dcStorageMain = documentService.getDcStorageMain(docId);
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("문서 다운로드");
        systemLogComponent.addUserLog(userLog);

        if (dcStorageMain != null) {
            String docNm = dcStorageMain.getDocNm();

            // 경로 탐색 및 특수문자 검증: "..", "/", "\" 등이 포함되면 예외를 발생.
            if (docNm.contains("..") || docNm.contains("/") || docNm.contains("\\")) {
                throw new GaiaBizException(ErrorType.BAD_REQUEST, "Invalid file name.");
            }

            Resource resource = fileService.getFile(dcStorageMain.getDocDiskPath(), dcStorageMain.getDocDiskNm());
            if (resource == null || !resource.exists()) {
                throw new GaiaBizException(ErrorType.NOT_FOUND, "Not found file data.");
            }
            String encodedDownloadFile = URLEncoder.encode(docNm, StandardCharsets.UTF_8); // 파일명이 한글이면, 인코딩을 해야 다운로드
                                                                                           // 가능.
            encodedDownloadFile = encodedDownloadFile.replaceAll("\\+", "%20"); // 문서 이름의 빈 칸이 +로 치환되는거 방지.

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedDownloadFile + "\"")
                    .body(resource);
        }
        throw new GaiaBizException(ErrorType.NO_DATA);
    }

    /**
     * 문서 다운로드(다중 압축파일)
     */
    @PostMapping("/file/download-zip")
    @Description(name = "문서 다운로드", description = "문서 폴더형(FOLDR) 다중 파일 다운로드 (압축파일로 생성)", type = Description.TYPE.MEHTOD)
    public void downloadFileZip(CommonReqVo commonReqVo, @Valid @RequestBody DocumentForm.DocDownload docDownload, UserAuth user,
            HttpServletResponse response) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("문서 다운로드(압축)");
        systemLogComponent.addUserLog(userLog);

        // TODO: 문서의 다운로드 권한이 있는지 확인 필요 (UserAuth 활용)
        List<String> docIdList = docDownload.getDocIdList();

        if (!docIdList.isEmpty()) {
            List<DcStorageMain> dcStorageMainList = documentService.getDcStorageMainList(docIdList);

            if (dcStorageMainList.isEmpty()) {
                throw new GaiaBizException(ErrorType.NOT_FOUND);
            } else {
                // 파일 다운로드 서비스 호출
                List<FileResource> fileResources = dcStorageMainList.stream().map(doc -> {
                    FileResource resource = new FileResource();
                    resource.setDiskFileName(doc.getDocDiskNm());
                    resource.setOriginalFileName(doc.getDocNm());
                    resource.setDiskFilePath(doc.getDocDiskPath());

                    return resource;
                })
                        .toList();
                try {
                    fileService.downloadFilesAsZip(fileResources, response);
                } catch (GaiaBizException e) {
                    response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                }
            }

        }

    }

    /**
     * 문서 다운로드 (아이템형)
     */
    @PostMapping("/file/download-zip/item")
    @Description(name = "문서 다운로드", description = "문서 아이템형(ITEM) 다중 파일 다운로드 (압축파일로 생성)", type = Description.TYPE.MEHTOD)
    public void itemDownloadFileZip(CommonReqVo commonReqVo, @Valid @RequestBody DocumentForm.ItemDownload itemDownload, UserAuth user,
            HttpServletResponse response) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("문서 다운로드(아이템형)");
        systemLogComponent.addUserLog(userLog);

        // TODO: 문서의 다운로드 권한이 있는지 확인 필요 (UserAuth 활용)
        // List<File> fileResources = new ArrayList<>();

        if (itemDownload.getItemDocId() != null) {
            List<DcAttachments> attachmentFiles = documentService.getDcAttachmentList(itemDownload.getItemDocId());
            log.debug("======================attachmentFiles======================");
            log.debug("itemDocId={}", attachmentFiles);
            log.debug("======================attachmentFiles======================");

            if (attachmentFiles.isEmpty()) {
                throw new GaiaBizException(ErrorType.NOT_FOUND);
            } else {
                // 파일 다운로드 서비스 호출
                List<FileResource> fileResources = attachmentFiles.stream().map(attachment -> {
                    FileResource resource = new FileResource();
                    resource.setDiskFileName(attachment.getFileDiskNm());
                    resource.setOriginalFileName(attachment.getFileNm());
                    resource.setDiskFilePath(attachment.getFileDiskPath());

                    return resource;
                }).toList();
                try {
                    fileService.downloadFilesAsZip(fileResources, response);
                } catch (GaiaBizException e) {
                    response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                }
            }

        }

    }

    /**
     * PDF 미리보기
     */
    @GetMapping("/pdf-file/{docId}")
    @Description(name = "PDF 미리보기", description = "문서가 PDF 파일인 경우, PDF 문서 미리보기", type = Description.TYPE.MEHTOD)
    public ResponseEntity<Resource> previewPDFDocument(CommonReqVo commonReqVo, @PathVariable("docId") String docId, UserAuth user) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("PDF 미리보기");
        systemLogComponent.addUserLog(userLog);

        DcStorageMain dcStorageMain = documentService.getDcStorageMain(docId);
        if (dcStorageMain != null) {
            String docNm = dcStorageMain.getDocNm();

            // 경로 탐색 및 특수문자 검증: "..", "/", "\" 등이 포함되면 예외를 발생.
            if (docNm.contains("..") || docNm.contains("/") || docNm.contains("\\")) {
                throw new GaiaBizException(ErrorType.BAD_REQUEST, "Invalid file name.");
            }

            String filePath = dcStorageMain.getDocDiskPath();
            File file = new File(filePath);

            if (!file.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(null);
            }

            Resource resource = fileService.getFile(dcStorageMain.getDocDiskPath(), dcStorageMain.getDocDiskNm());
            String encodedDownloadFile = URLEncoder.encode(docNm, StandardCharsets.UTF_8); // 파일명이 한글이면, 인코딩을 해야 다운로드
                                                                                           // 가능.

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedDownloadFile + "\"")
                    .header(HttpHeaders.CONTENT_ENCODING, "binary")
                    .header(HttpHeaders.PRAGMA, "no-cache")
                    .header(HttpHeaders.EXPIRES, "-1")
                    .body(resource);
        }
        throw new GaiaBizException(ErrorType.NO_DATA);
    }

    /**
     * PDF 다운로드
     */
    @GetMapping("/pdf-file/{docId}/download")
    @Description(name = "PDF 다운로드", description = "문서가 PDF 파일인 경우, PDF 문서 다운로드", type = Description.TYPE.MEHTOD)
    public ResponseEntity<Resource> pdfDocumentDownload(CommonReqVo commonReqVo, @PathVariable("docId") String docId, UserAuth user) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("PDF 다운로드");
        systemLogComponent.addUserLog(userLog);

        DcStorageMain dcStorageMain = documentService.getDcStorageMain(docId);
        if (dcStorageMain != null) {
            String docNm = dcStorageMain.getDocNm();

            // 경로 탐색 및 특수문자 검증: "..", "/", "\" 등이 포함되면 예외를 발생.
            if (docNm.contains("..") || docNm.contains("/") || docNm.contains("\\")) {
                throw new GaiaBizException(ErrorType.BAD_REQUEST, "Invalid file name.");
            }

            String filePath = dcStorageMain.getDocDiskPath() + "/" + dcStorageMain.getDocDiskNm();
            File file = new File(filePath);

            if (!file.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            long contentLength = file.length(); // 파일 크기 가져오기

            Resource resource = fileService.getFile(dcStorageMain.getDocDiskPath(), dcStorageMain.getDocDiskNm());
            String encodedDownloadFile = URLEncoder.encode(docNm, StandardCharsets.UTF_8); // 파일명이 한글이면, 인코딩을 해야 다운로드
                                                                                           // 가능.

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedDownloadFile + "\"")
                    // .header(HttpHeaders.CONTENT_ENCODING, "binary")
                    .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(contentLength))
                    .body(resource);
        }
        throw new GaiaBizException(ErrorType.NO_DATA);
    }

    /**
     * 파일, 폴더 개수 조회
     */
    @PostMapping("/hierarchy/count")
    @Description(name = "파일, 폴더 개수 조회", description = "문서 경로에 해당하는 파일과 폴더 개수 조회", type = Description.TYPE.MEHTOD)
    public Result getFileFolderCounts(CommonReqVo commonReqVo, @Valid @RequestBody DocumentForm.DocDelete docDelete) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("파일, 폴더 개수 조회");
        systemLogComponent.addUserLog(userLog);

        Map<String, Long> result = documentService.getFileFolderCounts(docDelete.getDocId());

        return Result.ok().put("fileFolderCount", result);
    }

    /**
     * 문서 삭제
     */
    @PostMapping("/list/delete")
    @Description(name = "문서 삭제", description = "문서 경로에 해당하는 문서 삭제", type = Description.TYPE.MEHTOD)
    public Result deleteDocumentList(CommonReqVo commonReqVo, @RequestBody @Valid DocumentForm.DocDeleteList docDeleteList, UserAuth user) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("문서 삭제");
        systemLogComponent.addUserLog(userLog);

        documentService.deleteDocument(docDeleteList.getDocIdList(), user);
        return Result.ok();
    }

    /**
     * 네비, 문서 full 경로 조회
     */
    @PostMapping("/full-tree")
    @Description(name = "문서경로, 문서 full tree 조회", description = "문서 경로와 문서를 합친 계층 데이터를 조회", type = Description.TYPE.MEHTOD)
    public Result getNaviDocTreeList(CommonReqVo commonReqVo, @RequestBody @Valid DocumentForm.FullTreeList fullTreeList, UserAuth user) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("문서경로, 문서 full tree 조회");
        systemLogComponent.addUserLog(userLog);

        List<Map<String, ?>> fullTree = documentService.getNaviDocTreeList(fullTreeList.getTopNaviId(),
                fullTreeList.getDocFolderIdList(), user);
        return Result.ok().put("fullTree", fullTree);
    }

    /**
     * 문서 이동
     */
    @PostMapping("/move")
    @Description(name = "문서 이동", description = "이동할 문서 경로 위치에 해당 문서 이동", type = Description.TYPE.MEHTOD)
    public Result moveDocument(CommonReqVo commonReqVo, @Valid @RequestBody DocumentForm.DocMoveCopy doc, UserAuth user) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("문서 이동");
        systemLogComponent.addUserLog(userLog);

        List<Map<String, ?>> moveDocResult = documentService.moveDocument(doc.getSourceItemId(), doc.getSourceItemNo(),
                doc.getSourceItemKind(), doc.getSourceItemPath(), doc.getTargetDocIdList(), user);

        return Result.ok().put("moveDoc", moveDocResult.stream().map(documentDto::toDocumentList));
    }

    /**
     * 문서 복사
     */
    @PostMapping("/copy")
    @Description(name = "문서 복사", description = "복사할 문서 경로 위치에 해당 문서 복사", type = Description.TYPE.MEHTOD)
    public Result copyDocument(CommonReqVo commonReqVo, @Valid @RequestBody DocumentForm.DocMoveCopy doc, UserAuth user) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("문서 복사");
        systemLogComponent.addUserLog(userLog);

        List<Map<String, ?>> copyDocResult = documentService.copyDocument(doc.getSourceItemId(), doc.getSourceItemNo(),
                doc.getSourceItemKind(), doc.getSourceItemPath(), doc.getTargetDocIdList(), user);

        return Result.ok().put("copyDoc", copyDocResult.stream().map(documentDto::toDocumentList));
    }

    /**
     * 네비게이션 삭제
     * (네비게이션 정보) => (저장) => 네비게이션
     */
    @PostMapping("/navigation/{navigationNo}/delete")
    @Description(name = "문서 네비게이션 삭제", description = "해당하는 문서 경로 삭제(하위 문서 존재 X)", type = Description.TYPE.MEHTOD)
    public Result deleteNavigation(CommonReqVo commonReqVo, @PathVariable("navigationNo") Integer navigationNo, UserAuth user) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("문서 네비게이션 삭제");
        systemLogComponent.addUserLog(userLog);

        DcNavigation dcNavigation = documentService.getNavigation(navigationNo);
        if (dcNavigation != null) {
            documentService.deleteNavigation(dcNavigation, user.getUsrId());
            return Result.ok();
        }

        throw new GaiaBizException(ErrorType.NO_DATA);

    }

    /**
     * 네비게이션 삭제(하위 문서가 있는 경우)
     */
    @PostMapping("/navigation/sub-document/delete/{naviId}")
    @Description(name = "문서 네비게이션 삭제", description = "해당하는 문서 경로 삭제(하위 문서 존재 O)", type = Description.TYPE.MEHTOD)
    public Result deleteNavigationAndSubDocumentList(CommonReqVo commonReqVo, @PathVariable("naviId") String naviId, UserAuth user) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("문서 네비게이션 삭제(하위 문서 존재 O)");
        systemLogComponent.addUserLog(userLog);

        if (naviId != null) {
            documentService.deleteNavigationAndSubDocumentList(naviId, user.getUsrId());
            return Result.ok();
        }

        throw new GaiaBizException(ErrorType.NO_DATA);

    }

    /**
     * 네비게이션 위로 이동
     */
    @PostMapping("/navigation/move-up")
    @Description(name = "문서 네비게이션 위로 이동", description = "해당하는 문서 경로 위로 이동", type = Description.TYPE.MEHTOD)
    public Result moveUpNavigation(CommonReqVo commonReqVo, @RequestBody @Valid DocumentForm.NaviMove naviMove, UserAuth user) {
        DcNavigation moveUpNaviForm = documentForm.toDcNavigation(naviMove);
        boolean result = documentService.upNaviDsplyOrdr(moveUpNaviForm);

        if (result) {
            return Result.ok();
        } else {
            return Result.nok(ErrorType.DATABSE_ERROR, "Please try again.");
        }

    }

    /**
     * 네비게이션 아래로 이동
     */
    @PostMapping("/navigation/move-down")
    @Description(name = "문서 네비게이션 아래로 이동", description = "해당하는 문서 경로 아래로 이동", type = Description.TYPE.MEHTOD)  
    public Result moveDownNavigation(@RequestBody @Valid DocumentForm.NaviMove naviMove, UserAuth user) {
        DcNavigation moveDownNaviForm = documentForm.toDcNavigation(naviMove);
        boolean result = documentService.downNaviDsplyOrdr(moveDownNaviForm);

        if (result) {
            return Result.ok();
        } else {
            return Result.nok(ErrorType.DATABSE_ERROR, "Please try again.");
        }

    }

    /**
     * 휴지통 문서리스트 조회
     */
    @GetMapping("/trash/list")
    @Description(name = "휴지통 문서 리스트 조회", description = "문서 중 dlt_yn = 'Y' & doc_trash_yn = 'N'인 문서 리스트를 조회", type = Description.TYPE.MEHTOD)
    public Result getTrashDocumentList(CommonReqVo commonReqVo, @Valid DocumentForm.TrashDocGet trashDocGet, UserAuth user) {

        if (trashDocGet.getCntrctNo() == null) {
            return Result.nok(ErrorType.NOT_FOUND, "Not found contract number.");
        }

        List<Map<String, ?>> trashDocList = documentService.getTrashDocumentList(trashDocGet.getCntrctNo(),
                trashDocGet.getColumnNm(), trashDocGet.getSearchText(), user, trashDocGet.getDocumentType());
        return Result.ok().put("trashDocList", trashDocList);
    }

    /**
     * 휴지통 문서 복원
     */
    @PostMapping("/trash/recover")
    @Description(name = "휴지통 문서 복원", description = "해당 문서의 dlt_yn 값을 'N'으로 변경", type = Description.TYPE.MEHTOD)
    public Result recoverTrashDocumentList(CommonReqVo commonReqVo, @RequestBody @Valid DocumentForm.TrashDocParam trashDocParam,
            UserAuth user) {

        Integer recoverCnt = documentService.recoverTrashDocList(trashDocParam.getTrashDocIdList(), user);

        if (recoverCnt == null || recoverCnt == 0) {
            return Result.nok(ErrorType.DATABSE_ERROR);
        }

        return Result.ok();
    }

    /**
     * 휴지통 문서 영구 삭제
     */
    @PostMapping("/trash/remove")
    @Description(name = "휴지통 문서 영구 삭제", description = "해당 문서의 doc_trash_yn 값을 'Y'로 변경", type = Description.TYPE.MEHTOD)
    public Result removeTrashDocumentList(CommonReqVo commonReqVo, @RequestBody @Valid DocumentForm.TrashDocParam trashDocParam, UserAuth user) {

        if (trashDocParam.getTrashDocIdList() == null || trashDocParam.getTrashDocIdList().isEmpty()) {
            return Result.nok(ErrorType.NO_DATA, "Not found trash document id list.");
        }

        documentService.removeTrashDocumentList(trashDocParam.getTrashDocIdList(), user);

        return Result.ok();
    }

    /**
     * 휴지통 비우기 (전체 삭제)
     */
    @PostMapping("/trash/remove-all")
    @Description(name = "휴지통 비우기", description = "해당 문서들의 doc_trash_yn 값을 'Y'로 변경", type = Description.TYPE.MEHTOD)
    public Result removeAllTrashDocumentList(CommonReqVo commonReqVo, @RequestBody @Valid DocumentForm.TrashDocParam trashDocParam,
            UserAuth user) {

        if (trashDocParam.getTrashDocIdList() == null || trashDocParam.getTrashDocIdList().isEmpty()) {
            return Result.nok(ErrorType.NO_DATA, "Not found trash document id list.");
        }

        documentService.removeAllTrashDocumentList(trashDocParam.getTrashDocIdList(), user);

        return Result.ok();
    }

     //=================착공계 관련=================//

    /**
     * 착공계 통합 문서 다운로드
     * 
     * @throws IOException
     */
    @GetMapping("/download/all-zip/{rootNaviId}/{rootNaviNm}")
    @Description(name = "착공계 통합 문서 다운로드", description = "착공계 문서 경로 구조로 압축파일 다운로드", type = Description.TYPE.MEHTOD)
    public ResponseEntity<Resource> constructionDocumentTotalDownload(CommonReqVo commonReqVo, @PathVariable("rootNaviId") String rootNaviId,
            @PathVariable("rootNaviNm") String rootNaviNm, UserAuth user) throws IOException {
        // 파일명 검증 (경로 탐색 공격 방지)
        if (rootNaviNm != null && (rootNaviNm.contains("..") || rootNaviNm.contains("/") || rootNaviNm.contains("\\"))) {
            return ResponseEntity.badRequest().build(); // 잘못된 요청 방지
        }

        Path zipPath = documentService.constructionDocumentTotalDownload(rootNaviId);
        Resource resource = new UrlResource(zipPath.toUri());

        // 파일명 설정 (한글 파일명 인코딩)
        String encodedDownloadFile = URLEncoder.encode(rootNaviNm, StandardCharsets.UTF_8).replace("+", "%20");

        ResponseEntity<Resource> response = ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + encodedDownloadFile + ".zip;")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);

        return response;
    }


    /**
     * 임시 압축파일(착공계 통합 문서) 삭제
     * 
     * @param rootNaviId
     * @return
     */
    @GetMapping("/delete-zip/{rootNaviId}")
    @Description(name = "착공계 통합 문서 삭제", description = "착공계 통합문서 다운로드 완료 후, 임시 폴더, 파일 삭제", type = Description.TYPE.MEHTOD)
    public Result deleteZipFile(CommonReqVo commonReqVo, @PathVariable String rootNaviId) {
        try {
            Path zipPath = documentService.getZipPath(rootNaviId);

            if (zipPath == null || !Files.exists(zipPath)) {
                return Result.nok(ErrorType.NOT_FOUND, "Not found file.");
            }

            Files.delete(zipPath);
            documentService.removeZipPath(rootNaviId);

            return Result.ok();
        } catch (IOException e) {
            log.error("압축 파일 삭제 중 오류 발생: ", e);
            throw new GaiaBizException(ErrorType.INTERNAL_SERVER_ERROR, "압축 파일 삭제 중 오류가 발생했습니다.");
        }
    }

    /**
     * 문서 24 전송 - 공문 자동발송 요청
     *
     * @throws Exception
     */
    @PostMapping("/gdoc/request")
    public Result requestGDoc(@RequestBody Map<String, Object> param) throws Exception {
        // 필수 파라미터 체크
        String[] requiredKeys = {
                "usr_id", "usr_pw", "rcv_org_id", "rcv_org_nm",
                "login_usr_ty", "log_vrf_ty", "chk_agree",
                "gdoc_ttl", "gdoc_cntnt", "gdoc_stmp_use_yn", "gdoc_sndr_nm", "gdoc_appr_nm", "docIds"
        };

        for (String key : requiredKeys) {
            if (StringUtils.isEmpty(MapUtils.getString(param, key))) {
                throw new GaiaBizException(ErrorType.ETC, "필수 입력값이 누락되었습니다: " + key);
            }
        }

        return Result.ok(documentComponent.sendDoc24(param));
    }

    @PostMapping("/shared-history/create")
    public Result createDocSharedHistory(CommonReqVo commonReqVo, @RequestBody DocumentDto.ApprovalRequestData approvalRequestData) {
        approvalRequestData.setRgstrId(commonReqVo.getUserId());
        Map<String,Object> result = documentComponent.createDocSharedHistory(approvalRequestData);
        return Result.ok(result);
    }


    /**
     * 착공계 문서 생성
     * resultCode
     * - 01 , 00 : 성공
     * - 02 : 착공계 문서 템플릿 조회 실패
     * - 03 : 윈도우 서버 통신 실패
     * @param constDocData
     * @param cntrctNo
     * @return
     */
    @PostMapping("/create/construct-document")
    @Description(name = "착공계 문서 생성", description = "착공계 기본 문서 생성(callType = 01)", type = Description.TYPE.MEHTOD)
    public Result createCntsNavi(CommonReqVo commonReqVo, @RequestBody List<ConstructionBeginsDocDto> constDocData, @RequestParam("cntrctNo") String cntrctNo, @RequestParam("cntrctNm") String cntrctNm, @RequestParam("cbgnDocType") String cbgnDocType) {
        if(constDocData == null || constDocData.isEmpty() || cntrctNo == null) {
            log.info("constDocData={}", constDocData);
            log.info("cntrctNo={}", cntrctNo);
            return Result.nok(ErrorType.NO_DATA, "no data.");
        }

        String[] pjtData = cntrctNo.split("\\.");
        String pjtNo = pjtData[0];

        log.info("======================constDocData======================");
        log.info("constDocData={}", constDocData);
        log.info("pjtNo={}", pjtNo);
        log.info("cntrctNo={}", cntrctNo);
        log.info("======================constDocData======================");

        Map<String, Object> result = new HashMap<>();

        // 문서 종류에 따라 메서드 다르게 호출
        // C == 엑셀
        if("C".equals(cbgnDocType)) {
            result = documentComponent.createConstructDocumentExcel(pjtNo, cntrctNo, constDocData, cntrctNm);
        }
        // D == 한글
        else if("D".equals(cbgnDocType)) {
            result = documentComponent.createConstructDocument(pjtNo, cntrctNo, constDocData);
        }

        if("02".equals(result.get("resultCode"))){
            return Result.nok(ErrorType.NOT_FOUND, (String)result.get("resultMsg"));
        }
        else if("03".equals(result.get("resultCode"))){
            return Result.nok(ErrorType.INTERNAL_SERVER_ERROR, (String)result.get("resultMsg"));
        }

        return Result.ok();
    }

	/**
	 * 전송된 객체에 포함된 정보로 등록되었던 착공계 문서 삭제(등록 해제)
	 * 
	 * @param docDeleteList ({@link DocumentForm.DocDeleteList}) 등록 해제 할 문서에 대한 정보를 담은 객체
	 * @return result ({@link String})
	 */
	@PostMapping("/delete/construct-document")
    @Description(name = "착공계 문서 삭제", description = "등록되었던 착공계 문서 삭제", type = Description.TYPE.MEHTOD)
	public Result deleteCntsNavi(CommonReqVo commonReqVo, @RequestBody @Valid DocumentForm.DocDeleteList docDeleteList, UserAuth user) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("착공계 관리에서 착공계 문서 삭제");
        systemLogComponent.addUserLog(userLog);

        documentComponent.deleteConstructDocument(docDeleteList.getDocIdList(), user);

        return Result.ok();

    }

}
