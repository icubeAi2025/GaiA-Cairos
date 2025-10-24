package kr.co.ideait.platform.gaiacairos.web.entrypoint.common;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import org.apache.tika.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.comp.common.CommonUtilComponent;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.util.UtilForm;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/util")
public class CommonUtilApiController extends AbstractController {
	
	@Autowired
	CommonUtilComponent commonUtilComponent;
	
	/**
     * 공통코드 테이블 데이터로 셀렉트박스 만들어 주는 Util
     */
    @PostMapping("/make-selectBox")
	@Description(name = "공통코드 셀렉트 박스 생성", description = "공통코드로 셀렉트박스를 생성해서 리턴한다.",type = Description.TYPE.MEHTOD)
    public Result getSelectBoxList(CommonReqVo commonReqVo, @RequestBody @Valid List<UtilForm.ComCodeSelectBoxGet> comCodeSelectBoxGet, @CookieValue(name = "lang", required = false) String langInfo) {

    	Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("공통코드 셀렉트 박스 생성");
        
    	return Result.ok().put("returnMap", commonUtilComponent.getSelectBoxList(comCodeSelectBoxGet, langInfo));
    }

	/**
     * 쿠키에서 GAIA인지 CMIS인지 ADMIN인지 가져오기
     */
	@GetMapping("/check-auth")
	@Description(name = "사용자/시스템 타입 조회", description = "포털정보 쿠키에서 사용자/시스템 타입을 조회하여 리턴한다.",type = Description.TYPE.MEHTOD)
	public Result checkAuth(CommonReqVo commonReqVo, HttpServletRequest request) {
		
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("사용자/시스템 타입 조회");
        
		return Result.ok(commonUtilComponent.checkAuth(request));
	}
	
	/**
     * 전체 메뉴 목록 가져오기
     */
	@GetMapping("/gat-menuList")
	@Description(name = "전체 메뉴 조회", description = "시스템 전체 메뉴를 조히하여 리터한다.",type = Description.TYPE.MEHTOD)
	public Result getAllMenuList(CommonReqVo commonReqVo, HttpServletRequest request) {
		
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("전체 메뉴 조회");

		return Result.ok().put("menuAllList", commonUtilComponent.getAllMenuList());
	}
	
	/**
     * PDF 파일 미리보기 유효성 검사 (파일 존재 유무 확인)
     */
	@PostMapping("/pdf-file-check")
	@Description(name = "pdf 뷰어 파일 검증", description = "미리보기 할 파일의 존재 유무를 체크한다.",type = Description.TYPE.MEHTOD)
	public Result pdfFileCheck(CommonReqVo commonReqVo, @RequestBody @Valid UtilForm.PdfView PdfViewParam) {

		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("pdf 뷰어 파일 검증");
        
		return Result.ok().put("checkValue", commonUtilComponent.viewFileCheck(PdfViewParam));
	}
	
	/**
     * PDF 파일 뷰어에 파일 다운로드 (뷰어창 미리보기용)
     */
	@GetMapping("/{viewKey}/pdf-view-down/{viewType}")
	@Description(name = "pdf 뷰어창 파일 다운로드", description = "PDF뷰어창에서 미리보기 할 파일을 다운로드한다..",type = Description.TYPE.MEHTOD)
	public ResponseEntity<Resource> pdfFileDownload(CommonReqVo commonReqVo, @PathVariable("viewKey") String viewKey, @PathVariable("viewType") String viewType) {
		
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("pdf 뷰어창 파일 다운로드");
		
		Map<String, Object> returnMap = commonUtilComponent.viewDownload(viewType, viewKey);
		
		Resource resource 	= (Resource) returnMap.get("fileInfo");
		String fileNm 		= URLEncoder.encode((String) returnMap.get("fileNm"), StandardCharsets.UTF_8); // 파일명이 한글이면, 인코딩을 해야 다운로드 가능
		
		return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileNm + "\"")
                .header(HttpHeaders.CONTENT_ENCODING, "binary")
                .header(HttpHeaders.PRAGMA, "no-cache")
                .header(HttpHeaders.EXPIRES, "-1")
                .body(resource);
	}
	
	/**
     * PDF 파일 뷰어에 파일 다운로드 (미리보기 파일 실제 다운로드용)
     */
	@GetMapping("/{viewKey}/pdf-file-down/{viewType}")
	@Description(name = "pdf 뷰어 실제 파일 다운로드", description = "미리보기 한 파일을 실제 다운로드한다..",type = Description.TYPE.MEHTOD)
	public ResponseEntity<Resource> pdfFileRealDownload(CommonReqVo commonReqVo, @PathVariable("viewKey") String viewKey, @PathVariable("viewType") String viewType) {
		
		Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("pdf 뷰어 실제 파일 다운로드");
		
		Map<String, Object> returnMap = commonUtilComponent.viewFileDownload(viewType, viewKey);
		
		Resource resource 	= (Resource) returnMap.get("fileInfo");
		String fileNm 		= URLEncoder.encode((String) returnMap.get("fileNm"), StandardCharsets.UTF_8); // 파일명이 한글이면, 인코딩을 해야 다운로드 가능
		long fileLength  	= (long) returnMap.get("fileLength");
		
		return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileNm + "\"")
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileLength))
                .body(resource);
	}
	
	/**
     * 기상청 날씨정보 가져오기
     */
    @PostMapping("/kma-weather")
    @Description(name = "기상청 날씨정보 조회", description = "선택한 날짜의 기상청 날씨정보를 조회한다.", type = Description.TYPE.MEHTOD)
    public Result getKmaWeather(@RequestBody @Valid UtilForm.KmaWeather kmaWeather) throws IOException {
    	
    	return Result.ok().put("kma", commonUtilComponent.getKmaWeather(kmaWeather));
    }
    
    /**
     * 첨부파일 다운로드(공통)
     */
    @GetMapping("/{appId}/{fileNo}/{sno}/file-download")
    @Description(name = "첨부파일 다운로드", description = "첨부파일을 다운로드 한다.", type = Description.TYPE.MEHTOD)
    public ResponseEntity<Resource> fileDownload(CommonReqVo commonReqVo,
            									 @PathVariable("appId") String appId,
                                                 @PathVariable("fileNo") Integer fileNo,
                                                 @PathVariable("sno") Integer sno) {
    	
    	String logMsg = String.format("업무 아이디(%s)의 첨부파일(파일번호: %s, 순번: %s) 다운로드", appId, fileNo, sno);
    	log.info("첨부파일 다운로드 :: {}", logMsg);

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType(logMsg);

		if (StringUtils.isEmpty(appId)) {
			throw new GaiaBizException(ErrorType.BAD_REQUEST, "잘못된 요청 정보입니다. appId: {}", appId);
		}

        return commonUtilComponent.fileDownload(appId.substring(0,2), fileNo, sno);
    }
}
