package kr.co.ideait.platform.gaiacairos.web.entrypoint.system;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kr.co.ideait.platform.gaiacairos.comp.portal.PortalComponent;
import kr.co.ideait.platform.gaiacairos.comp.portal.service.PortalService;
import kr.co.ideait.platform.gaiacairos.comp.system.service.CommonCodeService;
import kr.co.ideait.platform.gaiacairos.comp.system.service.DocumentManageService;
import kr.co.ideait.platform.gaiacairos.core.annotation.RequiredProjectSelect;
import kr.co.ideait.platform.gaiacairos.core.constant.CommonCodeConstants;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnContract;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.commoncode.CommonCodeDto;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.document.CbgnHtmlFormDto;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.document.CbgnPropertyDto;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.document.ConstructionBeginsDocDto;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.type.FileUploadType;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.iframework.annotation.Description.TYPE;
import kr.co.ideait.platform.gaiacairos.core.util.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/system/document")
public class DocumentManageApiController extends AbstractController {
	
	@Autowired
	DocumentManageService dmService;

	@Autowired
	PortalComponent portalComponent;

	@Autowired
	CommonCodeService commonCodeService;

	@Autowired
	FileService fileService;
	
	//네비게이션 관련
	
	@GetMapping("/main")
	@Description(name = "착공계 문서 관리 접속", description = "", type = TYPE.MEHTOD)
	public Result getMainInfo(CommonReqVo commonReqVo, @RequestParam("pjtNo") String pjtNo) {
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("착공계 문서 관리 접속");

		String userId = commonReqVo.getUserId();
		String loginId = commonReqVo.getLoginId();
		List<ConstructionBeginsDocDto> naviList = dmService.getNavigationList(userId,loginId);
		List<CommonCodeDto.CommonCode> dropdownList = dmService.getDropdownList();

		userLog.setResult("성공");

		return Result.ok()
				.put("naviList", naviList)
				.put("dropdownList", dropdownList)
				.put("me", commonReqVo.getUserName());
	}

	@GetMapping("/check-navi")
	@Description(name = "착공계 관리 네비 중복 체크",description = "착공계 관리 네비게이션에 사용 될 노드명의 중복 여부 체크", type=TYPE.MEHTOD)
	public Result checkDuplicateOfNavi(CommonReqVo commonReqVo, @RequestParam("cbgnNm") String cbgnNm) {
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("착공계 관리 네비 중복 체크");

		String userId = commonReqVo.getUserId();
		ConstructionBeginsDocDto dto = dmService.checkDuplicateOfNavi(cbgnNm,userId);
		return Result.ok().put("result", dto == null);
	}

	@PostMapping("/create-navi")
	@Description(name = "착공계 관리 네비 생성", description = "착공계 관리 네비게이션에 사용 될 노드 생성", type = TYPE.MEHTOD)
	/**
	 * 전송된 json 객체에 포함된 정보들로 네비게이션 노드 생성
	 * 
	 * @param ConstructionBeginsDocDto (params) 등록 할 노드의 정보
	 * @return ({@link ConstructionBeginsDocDto}) 등록 된 노드의 정보
	 */
	public Result createNavigationNode(CommonReqVo commonReqVo, @Valid @RequestBody ConstructionBeginsDocDto params, HttpServletRequest request) {
        String loginId = commonReqVo.getLoginId();
        
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("착공계 관리 네비 생성");

        
        ConstructionBeginsDocDto createdNode = dmService.createNavigationNode(loginId,params);
        if(createdNode != null) {
        	systemLogComponent.addUserLog(userLog);
        	return Result.ok().put("createdNode", createdNode);
        }
        else {
        	userLog.setResult("FAIL");
        	userLog.setErrorReason("실패/데이터베이스 에러");
        	systemLogComponent.addUserLog(userLog);
        	return Result.nok(ErrorType.DATABSE_ERROR);
        }
	}
	@PostMapping("/update-navi")
	@Description(name = "착공계 관리 네비 수정", description = "착공계 관리 네비게이션에 사용 될 노드 수정", type = TYPE.MEHTOD)
	/**
	 * 전송된 json 객체에 포함된 정보들로 네비게이션 노드 생성
	 * 
	 * @param ConstructionBeginsDocDto (params) 수정 할 노드의 정보
	 * @return ({@link String})
	 */
	public Result updateNavigationNode(CommonReqVo commonReqVo, @Valid @RequestBody ConstructionBeginsDocDto params, HttpServletRequest request) {
		String loginId = commonReqVo.getLoginId();
		
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("착공계 관리 네비 수정");

		
		ConstructionBeginsDocDto updatedNode = dmService.updateNavigationNode(loginId,params);
		if(updatedNode != null) {
			userLog.setResult("성공");
			systemLogComponent.addUserLog(userLog);
			return Result.ok().put("result", "성공");
		}
		else {
			userLog.setResult("실패/데이터베이스 에러");
			systemLogComponent.addUserLog(userLog);
			return Result.nok(ErrorType.DATABSE_ERROR);
		}
	}
	@PostMapping("/remove-navi")
	@Description(name = "착공계 관리 네비 삭제", description = "착공계 관리 네비게이션에 사용 될 노드 삭제", type = TYPE.MEHTOD)
	/**
	 * 전송된 json 객체에 포함된 정보들로 네비게이션 노드 삭제
	 * 
	 * @param ConstructionBeginsDocDto (params) 삭제 할 노드의 정보
	 * @return ({@link String})
	 */
	public Result removeNavigationNode(CommonReqVo commonReqVo, @Valid @RequestBody ConstructionBeginsDocDto params, HttpServletRequest request) {
		String loginId = commonReqVo.getLoginId();
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("착공계 관리 네비 삭제");
		
		params.setRgstrId(loginId);
		
		ConstructionBeginsDocDto removedNode = dmService.removeNavigationNode(params);
		if(removedNode != null) {
			userLog.setResult("성공");
			systemLogComponent.addUserLog(userLog);
			return Result.ok().put("result", "성공");
		}
		else {
			userLog.setResult("실패/데이터베이스 에러");
			systemLogComponent.addUserLog(userLog);
			return Result.nok(ErrorType.DATABSE_ERROR);
		}
	}
	
	@PostMapping("/update-ordr")
	@Description(name = "네비게이션 순서 변경", description = "네비게이션 배치 순서를 변경하는 요청",type = TYPE.MEHTOD)
	@Deprecated
	/**
	 * 
	 * @param List (datas) 0,1 에는 바뀔 대상의 정보들, 2,3 에는 이벤트가 일어날 대상 노드의 정보들이 담김
	 * ex) 94번 노드, 95번 노드 순서로 배치되어 있었다면 -> datas:[94,1,95,2]
	 * @return No Data, Just Code
	 */
	public Result updateOrder(CommonReqVo commonReqVo, @RequestBody List<Integer> datas) {
		// 공통로그
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("네비게이션 순서 변경");
		systemLogComponent.addUserLog(userLog);

		if(dmService.updateOrder(datas)) {
			return Result.ok();
		}
		else {
			return Result.nok(ErrorType.DATABSE_ERROR);
		}
	}

	//문서 관련
	@PostMapping("/create-doc-form")
	@Description(name = "착공계 문서 양식 등록", description = "착공계 문서 양식 파일 등록", type = TYPE.MEHTOD)
	/**
	 * 전송된 파일과 정보들로 네비게이션 생성 및 파일 저장
	 * 
	 * @param String (upCbgnNo)	상위 네비(노드)의 고유번호 - ex) 10 
	 * @param String (orgnlDocNm) 사용자가 지정한 문서 양식 파일 명 - ex) apple
	 * @param String (cbgnDocType) 등록하는 문서 양식 파일의 양식 코드 - ex) D0001
	 * @param String (cbgnPath) 등록하는 문서 양식 파일의 네비게이션 경로 - ex) 착공계 관리 문서 > 테스트폴더1 > 하위폴더1
	 * @param MultipartFile (orgFile) 등록되는 문서 양식 파일
	 * @return ok/nok
	 */
	public Result createDocumentForm(CommonReqVo commonReqVo, @RequestParam("upCbgnNo") String upCbgnNo, @RequestParam("orgnlDocNm") String orgnlDocNm,
		    @RequestParam("cbgnDocType") String cbgnDocType,@RequestParam("cbgnPath")String cbgnPath,
		    @RequestParam("orgFile") MultipartFile orgFile, @RequestParam("cbgnLevel") int cbgnLevel,
			@RequestParam("pdfFile") MultipartFile pdfFile, @RequestParam("pdfNm") String pdfNm,
			HttpServletRequest request) {
		String loginId = commonReqVo.getLoginId();
		
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("착공계 문서 양식 등록");
		
		HashMap<String, Object> result = dmService.createDocumentForm(upCbgnNo, orgnlDocNm, cbgnDocType, cbgnPath, orgFile, cbgnLevel, loginId, pdfNm, pdfFile);
		if(result != null) {
			if("허용되지 않은 확장자".equals((String)result.get("result"))) {
				userLog.setResult("실패/허용되지 않은 확장자");
				systemLogComponent.addUserLog(userLog);
				return Result.nok(ErrorType.INVAILD_INPUT_DATA);
			}
			else{
				Object saved = result.get("createdNode");
				if(saved!= null) {
					userLog.setResult("성공");
					systemLogComponent.addUserLog(userLog);
					return Result.ok().put("createdNode", saved);
				}
			}
		}
		return Result.nok(ErrorType.DATABSE_ERROR);
	}
	
	@GetMapping("/preview/{cbgnNo}")
	@Description(name = "착공계 문서 양식 프리뷰", description = "착공계 문서 양식 파일 프리뷰를 띄우기 위한 리소스 반환", type = TYPE.MEHTOD)
	public ResponseEntity<Resource> getPreview(CommonReqVo commonReqVo, @PathVariable("cbgnNo") int cbgnNo) {
		// 공통로그
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("착공계 문서 양식 프리뷰");
		systemLogComponent.addUserLog(userLog);

		log.debug("getPreview: cbgnNo = {}", cbgnNo);
		Resource resource;
		try {
			String pdfDocNm = dmService.getPdfDocNm(cbgnNo);
			resource = dmService.getFileResource(cbgnNo);

			if (pdfDocNm == null || resource == null) {
				log.debug("getPreview: 파일 미존재");
				return ResponseEntity.noContent().build();
			}
			log.debug("getPreview: pdfDocNm = {}", pdfDocNm);

			String encodedDownloadFile = URLEncoder.encode(pdfDocNm, StandardCharsets.UTF_8); // 파일명이 한글이면, 인코딩을 해야 다운로드 가능
			return ResponseEntity.ok()
					.contentType(MediaType.APPLICATION_PDF)
					.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + encodedDownloadFile + "\"")
					.body(resource);
		} catch (IOException e) {
			throw new GaiaBizException(ErrorType.INTERNAL_SERVER_ERROR,e);
		}
	}

	@GetMapping("/document-list")
	@Description(name = "착공계 문서 양식 리스트 조회", description = "착공계 문서 양식 파일 리스트", type = TYPE.MEHTOD)
	/**
	 * 선택한 계약에 등록 된 문서 양식 파일 목록을 보여주기 위한 리스트 반환
	 * 
	 * @param contractNo ({String}) 계약번호
	 * @return ({@link List}) 검색된 문서 양식 파일(ConstructionBeginsDocDto) 리스트
	 */
	public Result getDocumentList(CommonReqVo commonReqVo, @RequestParam("cntrctNo") String cntrctNo) {
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("착공계 문서 양식 파일 목록 조회");
        
		List<ConstructionBeginsDocDto> documentList = dmService.getDocumentList(cntrctNo);
		if(documentList != null) {
			userLog.setResult("성공");
			systemLogComponent.addUserLog(userLog);
			return Result.ok().put("documentList", documentList);
		}
		userLog.setResult("실패");
		systemLogComponent.addUserLog(userLog);
		return Result.nok(ErrorType.DATABSE_ERROR);
	}
	
	//속성 관련
	@GetMapping("/property/code-combo-list")
	@Description(name = "문서 네비게이션 속성 추가 데이터 조회", description = "속성 종류, 속성 타입, 속성 타입 종류 콤보박스 데이터 조회", type = Description.TYPE.MEHTOD)
	public Result documentNavigationSetPropertyOptionData(CommonReqVo commonReqVo,
														  @CookieValue(name = "lang", required = false, defaultValue = "ko") String langInfo) {
		// 공통로그
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("문서 네비게이션 속성 추가 데이터 조회");
		systemLogComponent.addUserLog(userLog);

		String userId = commonReqVo.getUserId();

		List<String> cmnGrpCdList = new ArrayList<>();
		cmnGrpCdList.add(CommonCodeConstants.ATTBTKIND_CODE_GROUP_CODE);
		cmnGrpCdList.add(CommonCodeConstants.ATTBTTYPE_CODE_GROUP_CODE);

		List<Map<String, Object>> attrbtTypeSelOptions = commonCodeService.getAttrbtTypeSelOptions(langInfo);
		List<CbgnHtmlFormDto> attrbtHtmlOptions = dmService.getHtmlPropertyFormList(userId);

		Map<String, List<Map<String, Object>>> codeComboMap = commonCodeService
				.getCommonCodeListByGroupCode(cmnGrpCdList, langInfo);

		codeComboMap.put("attrbtTypeSel", attrbtTypeSelOptions);

		return Result.ok().put("codeComboMap", codeComboMap).put("attrbtHtmlOptions", attrbtHtmlOptions);
	}

	@GetMapping("/property/list/{cbgnNo}")
	@Description(name = "네비에 등록된 속성 목록 조회", description = "네비에 등록된 속성 목록을 조회한다",type = TYPE.MEHTOD)
	/**
	 * 
	 * @param cbgnNo (
	 * @return
	 */
	public Result getPropertyList(CommonReqVo commonReqVo, @PathVariable("cbgnNo") int cbgnNo) {
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("네비에 등록된 속성 목록 조회");
		systemLogComponent.addUserLog(userLog);

		List<CbgnPropertyDto> propertyList = dmService.getPropertyList(cbgnNo);
		return Result.ok().put("propertyList", propertyList);
	}
	
	@GetMapping("/property/check-duplicated")
	@Description(name = "속성 코드의 중복 여부 체크", description = "속성 코드의 중복 여부를 체크한다",type = TYPE.MEHTOD)
	public Result checkDuplicated(CommonReqVo commonReqVo, @RequestParam("attrbtCd") String attrbtCd,@RequestParam("cbgnNo") Integer cbgnNo) {
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("속성 코드의 중복 여부 체크");
		
		boolean result = dmService.checkDuplicated(attrbtCd,cbgnNo);
		if(result) {
			userLog.setResult("성공");
			systemLogComponent.addUserLog(userLog);
			return Result.ok();
		} else {
			userLog.setResult("실패/중복된 코드 존재");
			systemLogComponent.addUserLog(userLog);
			return Result.nok(ErrorType.DUPLICATION_DATA);
		}
	}
	
	@PostMapping("/property/regist")
	@Description(name = "네비 속성 정보 등록", description = "네비에 속성 정보를 등록한다",type = TYPE.MEHTOD)
	public Result registProperty(CommonReqVo commonReqVo, @RequestBody CbgnPropertyDto dto, HttpServletRequest request) {
		String usrId = commonReqVo.getUserId();
		
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("네비 속성 정보 등록");
		
		dto.setRgstrId(usrId);
		dto.setChgId(usrId);
		CbgnPropertyDto result = dmService.registProperty(dto);
		if(result != null) {
			if(result.getAttrbtNo() != 0) {
				userLog.setResult("성공");
				systemLogComponent.addUserLog(userLog);
				return Result.ok().put("property", result);
			}
		}
		userLog.setResult("실패/데이터베이스 에러");
		systemLogComponent.addUserLog(userLog);
		return Result.nok(ErrorType.DATABSE_ERROR);
	}
	
	@PostMapping("/property/remove")
	@Description(name = "네비 등록 속성 정보 삭제", description = "네비에 등록된 속성 정보를 삭제한다",type = TYPE.MEHTOD)
	public Result removeProperty(CommonReqVo commonReqVo, @RequestBody HashMap<String, List<Long>> params, HttpServletRequest request) {
		String usrId = commonReqVo.getUserId();
		
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("네비 등록 속성 정보 삭제");
		
		List<Long> attrbtNoList = params.get("attrbtNoList");
		if(dmService.removeProperty(attrbtNoList,usrId)) {
			userLog.setResult("성공");
			systemLogComponent.addUserLog(userLog);
			return Result.ok();
		}
		userLog.setResult("실패/데이터베이스 에러");
		systemLogComponent.addUserLog(userLog);
		return Result.nok(ErrorType.DATABSE_ERROR);
	}
	
	@GetMapping("/property/{attrbtNo}")
	@Description(name = "속성 정보 조회", description = "속성 정보를 조회한다",type = TYPE.MEHTOD)
	public Result getPropertyOfAttrbtNo(CommonReqVo commonReqVo, @PathVariable("attrbtNo") Integer attrbtNo) {
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("속성 정보 조회");
		
        userLog.setResult("성공");
        systemLogComponent.addUserLog(userLog);
		return Result.ok().put("property",dmService.getPropertyOfattrbtNo(attrbtNo));
	}
	
	@PostMapping("/property/modify")
	@Description(name = "속성 정보 수정", description = "속성 정보를 수정한다",type = TYPE.MEHTOD)
	public Result modifyProperty(CommonReqVo commonReqVo, @RequestBody CbgnPropertyDto dto, HttpServletRequest request) {
		String usrId = commonReqVo.getUserId();
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("속성 정보 수정");
		
		dto.setChgId(usrId);
		if(dmService.modifyProperty(dto)) {
			userLog.setResult("성공");
			systemLogComponent.addUserLog(userLog);
			return Result.ok();
		}
		else {
			userLog.setResult("실패/데이터베이스 에러");
			systemLogComponent.addUserLog(userLog);
			return Result.nok(ErrorType.DATABSE_ERROR);
		}
	}

	@GetMapping("/html/list")
	@Description(name = "TODO", description = "",type = TYPE.MEHTOD)
	public Result getHtmlPropertyFormList(CommonReqVo commonReqVo){
		// 공통로그
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("TODO");
		systemLogComponent.addUserLog(userLog);

		String userid = commonReqVo.getUserId();
		List<CbgnHtmlFormDto> htmlFormList = dmService.getHtmlPropertyFormList(userid);
		if(htmlFormList != null) {
			return Result.ok().put("htmlFormList", htmlFormList);
		}
		return Result.nok(ErrorType.DATABSE_ERROR);
	}

	@PostMapping("/html/create")
	@Description(name = "TODO", description = "",type = TYPE.MEHTOD)
	public Result createHtmlPropertyForm(CommonReqVo commonReqVo, @RequestBody CbgnHtmlFormDto htmlFormDto){
		// 공통로그
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("TODO");
		systemLogComponent.addUserLog(userLog);

		String userid = commonReqVo.getUserId();
		CbgnHtmlFormDto result = dmService.createHtmlPropertyForm(htmlFormDto,userid);
		if(result != null) {
			return Result.ok().put("result", result);
		}
		else  {
			return Result.nok(ErrorType.DATABSE_ERROR);
		}
	}

	@PostMapping("/html/remove")
	@Description(name = "TODO", description = "",type = TYPE.MEHTOD)
	public Result removeHtmlPropertyForm(CommonReqVo commonReqVo, @RequestBody HashMap<String, List<Long>> params){
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("등록된 HTML 양식 삭제");

		String usrId = commonReqVo.getUserId();

		List<Long> formNoList = params.get("formNoList");
		if(dmService.removeHtmlPropertyForm(formNoList,usrId)) {
			userLog.setResult("성공");
			systemLogComponent.addUserLog(userLog);
			return Result.ok();
		}
		userLog.setResult("실패/데이터베이스 에러");
		systemLogComponent.addUserLog(userLog);
		return Result.nok(ErrorType.DATABSE_ERROR);
	}

	@GetMapping("/html/{formNo}")
	@Description(name = "TODO", description = "",type = TYPE.MEHTOD)
	public Result getHtmlPropertyForm(CommonReqVo commonReqVo, @PathVariable("formNo") Integer formNo){
		// 공통로그
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("html form 조회");
		systemLogComponent.addUserLog(userLog);


		String usrId = commonReqVo.getUserId();

		CbgnHtmlFormDto htmlFormDto	= dmService.getHtmlPropertyForm(formNo,usrId);
		if(htmlFormDto != null) {
			return Result.ok().put("htmlForm", htmlFormDto);
		}
		return Result.nok(ErrorType.DATABSE_ERROR);
	}

	@PostMapping("/html/modify")
	public Result modifyHtmlPropertyForm(CommonReqVo commonReqVo, @RequestBody CbgnHtmlFormDto htmlFormDto){
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("등록된 HTML 양식 수정");

		String userId = commonReqVo.getUserId();
		CbgnHtmlFormDto result = dmService.modifyHtmlPropertyForm(userId, htmlFormDto);
		if(result != null) {
			userLog.setResult("성공");
			return Result.ok().put("result", result);
		}
		else  {
			userLog.setResult("실패/데이터베이스 에러");
			return Result.nok(ErrorType.DATABSE_ERROR);
		}
	}
	@RequiredProjectSelect
	@GetMapping("/pdf-file/{cbgnNo}")
	@Description(name = "TODO", description = "",type = TYPE.MEHTOD)
	public ResponseEntity<Resource> previewPDFDocument(CommonReqVo commonReqVo, @PathVariable("cbgnNo") Integer cbgnNo) {
		// 공통로그
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
		userLog.setLogType(LogType.FUNCTION.name());
		userLog.setExecType("TODO");
		systemLogComponent.addUserLog(userLog);


		HashMap<String,Object> result = dmService.getPdfFileResource(cbgnNo);
		Resource resource = null;
		String encodedDownloadFile = null;
		if(result != null){
			resource = (Resource)result.get("resource");
			encodedDownloadFile = (String)result.get("encodedDownloadFile");
		}

		if(resource == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(null);
		}

		return ResponseEntity.ok()
				.contentType(MediaType.APPLICATION_PDF)
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedDownloadFile + "\"")
				.header(HttpHeaders.CONTENT_ENCODING, "binary")
				.header(HttpHeaders.PRAGMA, "no-cache")
				.header(HttpHeaders.EXPIRES, "-1")
				.body(resource);

	}
}














