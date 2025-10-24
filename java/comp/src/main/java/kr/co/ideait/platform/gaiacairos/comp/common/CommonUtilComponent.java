package kr.co.ideait.platform.gaiacairos.comp.common;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.ideait.platform.gaiacairos.comp.common.service.CommonUtilService;
import kr.co.ideait.platform.gaiacairos.comp.document.service.DocumentService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractComponent;
import kr.co.ideait.platform.gaiacairos.core.constant.CommonCodeConstants;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DcStorageMain;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.cookie.CookieVO;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.util.UtilForm;
import kr.co.ideait.platform.gaiacairos.core.util.CookieService;
import kr.co.ideait.platform.gaiacairos.core.util.FileService;
import kr.co.ideait.platform.gaiacairos.core.util.UtilMybatisParam.ComCodeSelectInput;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommonUtilComponent extends AbstractComponent {
	
	@Autowired
	CommonUtilService commonUtilService;
	
	@Autowired
    FileService fileService;
	
	@Autowired
    DocumentService documentService;
	
	@Autowired
	KmaWeatherComponent kmaWeatherComponent;
	
	private CookieVO cookieVO;
	/**
     * 공통코드 테이블 데이터로 셀렉트박스 만들어 주는 Util
     * 
     * @param  List<UtilForm.ComCodeSelectBoxGet> comCodeSelectBoxGet
     * @param  String langInfo
     * @return Map<String, Object>
     */
	public Map<String, Object> getSelectBoxList(List<UtilForm.ComCodeSelectBoxGet> comCodeSelectBoxGet, String langInfo) {
		
		Map<String, Object> returnMap = new HashMap<String, Object>();
		
		if(comCodeSelectBoxGet.size() > 0) {
			  
    		for(UtilForm.ComCodeSelectBoxGet makeSelectBox : comCodeSelectBoxGet) {
    			String orderByCol = (makeSelectBox.getOrderByCol() == null || makeSelectBox.getOrderByCol().isBlank()) ? "CMN_CD_DSPLY_ORDER" : makeSelectBox.getOrderByCol(); 
    			String orderByType = (makeSelectBox.getOrderByType() == null || makeSelectBox.getOrderByType().isBlank()) ? "ASC" : makeSelectBox.getOrderByCol(); 
    			String ckeckValue = (makeSelectBox.getCkeckedValue() == null || makeSelectBox.getCkeckedValue().isBlank()) ? "" : makeSelectBox.getCkeckedValue(); 
    			String initType = (makeSelectBox.getInitText() == null || makeSelectBox.getInitText().isBlank()) ? "noInit" :"init";
    			Boolean initSelect = (makeSelectBox.getInitSelect() == null || makeSelectBox.getInitSelect().isBlank() || makeSelectBox.getInitSelect().equals("N")) ? false : true;
    			
    			ComCodeSelectInput comCodeSelectInput = new ComCodeSelectInput();
    	    	comCodeSelectInput.setCmnGrpCd(makeSelectBox.getCmnGrpCd());
    	    	comCodeSelectInput.setOrderByCol(orderByCol);
    	    	comCodeSelectInput.setOrderByType(orderByType);
    			
    			List<Map<String, Object>> selectBoxSrcList = commonUtilService.selectComCodeList(comCodeSelectInput);    			
    			StringBuilder selectBox = new StringBuilder();
    			
    			selectBox.append("<select id='").append(makeSelectBox.getSelectBoxId()).append("' name='").append(makeSelectBox.getSelectBoxId());
    			if(makeSelectBox.getFunName() != null && !makeSelectBox.getFunName().isBlank()) {
    				selectBox.append("' ").append(makeSelectBox.getFuntype()).append("='").append(makeSelectBox.getFunName()).append("(").append(makeSelectBox.getFunParam()).append(")");
    			}    			
    			selectBox.append("'>");
    			
    			if("init".equals(initType)) {
					selectBox.append("<option selected value=''>").append(makeSelectBox.getInitText()).append("</option>");
    			}
    			for(Map<String, Object> selectBoxSrc : selectBoxSrcList){

        			String selectedAttribute = (selectBoxSrc.get("cmn_cd").equals(ckeckValue)) ? " selected" : "";
    				selectBox.append("<option value='").append(selectBoxSrc.get("cmn_cd")).append("'").append(selectedAttribute).append(">");
    				
    				if("en".equals(langInfo)) {
						selectBox.append(selectBoxSrc.get("eng_nm")).append("</option>");
    				}else {
    					selectBox.append(selectBoxSrc.get("kor_nm")).append("</option>");
    				}
    			}
    			selectBox.append("</select>");    			
    			
    			returnMap.put(makeSelectBox.getParamNm(), selectBox.toString());
    		}    		
    	}

		return returnMap;
	}
	
	/**
     * 쿠키에서 GAIA인지 CMIS인지 ADMIN인지 가져오는 Util
     * 
     * @param  HttpServletRequest
     * @return Map<String, Object>
     */
	public Map<String, Object> checkAuth(HttpServletRequest request) {
		
		cookieVO = new CookieVO(platform.toUpperCase());
		
		String userInfoCookie 	= cookieService.getCookie(request, cookieVO.getPortalCookieName());     // 구분자-portal-auth 쿠키정보 가져오기

		String authType = ""; // GaiA or CaiROS
		String userType = ""; // ADIMN or NOMAL
		if(userInfoCookie != null){
			authType = userInfoCookie.split(":")[2];
			userType = userInfoCookie.split(":")[1];
		}else{
			throw new GaiaBizException(ErrorType.UNAUTHORIZED, "정상적인 접근이 아닙니다.");
		}

		Map<String, Object> returnMap = new HashMap<>();
		returnMap.put("authType", authType);
		returnMap.put("userType", userType);

		return returnMap;
	}
	
	/**
     * 전체 메뉴 목록 가져오기 Util
     * 
     * @param 
     * @return List<Map<String, Object>>
     */
	public List<Map<String, Object>> getAllMenuList() {

		List<Map<String, Object>> returnList = commonUtilService.selectAllMenuList();

		return returnList;
	}
	
	/**
     * view파일 존재 여부 확인하는 Util
     * 
     * @param  UtilForm.PdfView PdfViewParam
     * @return Map<String, Object
     */
	public Map<String, Object> viewFileCheck(UtilForm.PdfView PdfViewParam) {
		
		Map<String, Object> returnMap = new HashMap<>();
		String fileNm = null;
		String checkValue = "TRUE";
		
		//뷰어 조회 할 타입별 파일 유무 체크
		if("DOC".equals(PdfViewParam.getViewType().toUpperCase())) {
			DcStorageMain dcStorageMain = documentService.getDcStorageMain(PdfViewParam.getViewKey());
			
			if (dcStorageMain != null) {
				fileNm = dcStorageMain.getDocNm();
	        }else {
	        	checkValue = "FALSE";
	        }
		}else if("GUIDE".equals(PdfViewParam.getViewType().toUpperCase())) {
			String filePath = String.format("%s/%s.pdf", guidePath, PdfViewParam.getViewKey());

			File file = new File(filePath);

			if (file.exists()) {
				switch (PdfViewParam.getViewKey()) {	
				case "1":	
					fileNm = "사업관리 가이드.pdf";
					break;
				case "2":		
					fileNm = "작업일지 가이드.pdf";
					break; 
				case "3":
					fileNm = "기성신청 가이드.pdf";
					break;
				case "4":
					fileNm = "월간공정보고 가이드.pdf";
					break;
				case "5":
					fileNm = "CM업무일지 가이드.pdf";
					break;
				default:
					fileNm = "가이드.pdf";
				}
			}else {
				checkValue = "FALSE";
			}
		}else {
			checkValue = "AMBIGUOUS";
		}		
		
		//파일 유무가 정상이면 파일 확장자 검사 PDF일 경우만 정상
		if("TRUE".equals(checkValue)) {
			String ext = FilenameUtils.getExtension(fileNm);
			if(!"PDF".equals(StringUtils.defaultString(ext).toUpperCase())) {
				checkValue = "NOPDF";
			}			
		}
		
		//파일 정상 유무 설정
    	returnMap.put("returnValue", checkValue);
    	
		//미리보기 파일 유무가 정상 추가 정보 설정
		if("TRUE".equals(checkValue)) {
	    	returnMap.put("docNm", fileNm);				
		}

		return returnMap;
	}
	
	/**
     * 뷰어창 미리보기용 다운로드 Util
     * 
     * @param  String viewType
     * @param  String viewKey
     * @return Map<String, Object>
     */
	public Map<String, Object> viewDownload(String viewType, String viewKey) {
		
		String fileNm = null;
		Resource resource = null;
		
		if("DOC".equals(viewType)) {
			DcStorageMain dcStorageMain = documentService.getDcStorageMain(viewKey);
			
			resource = fileService.getFile(dcStorageMain.getDocDiskPath(), dcStorageMain.getDocDiskNm());
			
			fileNm = dcStorageMain.getDocNm();
		}else if("GUIDE".equals(viewType)) {
			
			String guideNm = String.format("%s.pdf", viewKey);

			resource = fileService.getFile(guidePath, guideNm);

			switch (viewKey) {	
			case "1":	
				fileNm = "사업관리 가이드.pdf";
				break;
			case "2":		
				fileNm = "작업일지 가이드.pdf";
				break; 
			case "3":
				fileNm = "기성신청 가이드.pdf";
				break;
			case "4":
				fileNm = "월간공정보고 가이드.pdf";
				break;
			case "5":
				fileNm = "CM업무일지 가이드.pdf";
				break;
			default:
				fileNm = "가이드.pdf";
			}
		}
		
		Map<String, Object> returnMap = new HashMap<>();
    	returnMap.put("fileInfo", resource);
    	returnMap.put("fileNm", fileNm);

		return returnMap;
	}
	
	/**
     * 뷰어파일 다운로드 Util
     * 
     * @param  String viewType
     * @param  String viewKey
     * @return Map<String, Object>
     */
	public Map<String, Object> viewFileDownload(String viewType, String viewKey) {

		String orgFileNm 		= null;
		String fileNm 		= null;
		String filepath 	= null;
		
		if("DOC".equals(viewType)) {
			DcStorageMain dcStorageMain = documentService.getDcStorageMain(viewKey);
			orgFileNm 	= dcStorageMain.getDocNm();
			fileNm 		= dcStorageMain.getDocDiskNm();
			filepath 	= dcStorageMain.getDocDiskPath();		
			
		}else if("GUIDE".equals(viewType)) {
			orgFileNm 	= String.format("%s.pdf", viewKey);
			fileNm 		= String.format("%s.pdf", viewKey);
			filepath 	= guidePath;
			
		}
		
		Resource resource = fileService.getFile(filepath, fileNm);
		File file = new File(String.format("%s/%s", filepath, fileNm));
		long fileLength = file.length();
		
		Map<String, Object> returnMap = new HashMap<>();
    	returnMap.put("fileInfo", resource);
    	returnMap.put("fileNm", orgFileNm);
    	returnMap.put("fileLength", fileLength);

		return returnMap;
	}
	 
	/**
     * 기상청 날씨정보 조회
     * 
     * @param  UtilForm.KmaWeather kmaWeather
     * @return List<Map<String, Object>>
	 * @throws IOException 
     */
	public Map<String, Object> getKmaWeather(UtilForm.KmaWeather kmaWeather) throws IOException {
		
		MybatisInput input = MybatisInput.of().add("cmnGrpCd", CommonCodeConstants.KMA_CODE_GROUP_CODE)
				.add("pjtNo", kmaWeather.getPjtNo()).add("getDay",kmaWeather.getTm());
		
		Map<String, Object> resultMapList = commonUtilService.selectProjectWeatherInfo(input);
		
		if(MapUtils.isEmpty(resultMapList)) {
			setKmaWeather("NOAUTO", kmaWeather.getTm());			
		}
		
		return resultMapList;
	}
	
	/**
     *  기상청 날씨&기온정보 등록
     * 
     * @param  String type
     * @param  String gatDay
     * @return void
	 * @throws IOException 
     */
	public void setKmaWeather(String type, String gatDay) throws IOException {

		List<Map<String, Object>> weatherCodeList = commonUtilService.selectAllWeatherCode();	// DB에 저장할 기상청 지점 코그를 조회
		
		if(!ObjectUtils.isEmpty(weatherCodeList)) {
			String weatherUrl;																	// 기상청 온도조회 호출 URL
			String forecastUrl;																	// 기상청 날씨조회 호출 URL
			String[] cityCode = new String[weatherCodeList.size()]; 							// 관측지점 도시 코드
			String[] cityAreaCode = new String[weatherCodeList.size()];							// 예보 지역 코드
			
			for(int i = 0; i < weatherCodeList.size(); i++) {
				Map<String, Object> weatherCode = weatherCodeList.get(i);
				
				cityCode[i] 	= (String) weatherCode.get("city_code");
				cityAreaCode[i] = (String) weatherCode.get("city_area_code");
			}	
	        
	        if ("AUTO".equals(type)) {
	        	weatherUrl = String.format("https://apihub.kma.go.kr/api/typ01/url/kma_sfcdd.php?tm=&stn=&disp=1&help=1&authKey=xlhYexSqTSuYWHsUqg0reg"); 										// 현재 시간의 기상관측 정보를 가져오는 API URL을 설정합니다		
	        	forecastUrl = String.format("https://apihub.kma.go.kr/api/typ01/url/fct_afs_dl.php?reg=&tmfc=&disp=1&help=1&authKey=xlhYexSqTSuYWHsUqg0reg"); 									// 가장 최근에 발표된 예보를 가져오는 API URL을 설정합니다			
			}else {
				weatherUrl = String.format("https://apihub.kma.go.kr/api/typ01/url/kma_sfcdd.php?tm=%s&stn=&disp=1&help=1&authKey=xlhYexSqTSuYWHsUqg0reg", gatDay); 							// 지정한 날짜의 기상관측 정보를 가져오는 API URL을 설정합니다
				forecastUrl = String.format("https://apihub.kma.go.kr/api/typ01/url/fct_afs_dl.php?reg=&tmfc1=%s00&tmfc2=%s17&disp=1&help=1&authKey=xlhYexSqTSuYWHsUqg0reg", gatDay, gatDay);  	// 지정한 날짜에 발표된 예보를 가져오는 API URL을 설정합니다
			}
	        
	        log.info("2-0.날씨 조회 URL : >>>> {}", forecastUrl);
	        
	        kmaWeatherComponent.setForecastInfo(cityAreaCode, forecastUrl, gatDay);
	        
	        log.info("1-0.기온 조회 URL : >>>> {}", weatherUrl);
	        
	        kmaWeatherComponent.setWeatherInfo(cityCode, weatherUrl, gatDay);
			
		}else {
			throw new GaiaBizException(ErrorType.NO_DATA, "기상청 날씨 조회에 실패했습니다.(지점 코드가 없습니다.)");
		}
	}	
	
	/**
     * (공통 )첨부파일 다운로드
     * @param String	appId
     * @param Integer	fileNo
     * @param Integer	sno
     * @return ResponseEntity<Resource> 
     */
    public ResponseEntity<Resource> fileDownload(String appId, Integer fileNo, Integer sno) {
        // 1. 파일 메타정보 조회 및 다운로드 수 업데이트
    	MybatisInput input = MybatisInput.of().add("appId", appId.toUpperCase()).add("fileNo", fileNo).add("sNo", sno);
    	Map<String, Object> fileInfo = commonUtilService.selectDownloadFileInfo(input);
    	
        if (ObjectUtils.isEmpty(fileInfo)) {
            throw new GaiaBizException(ErrorType.NOT_FOUND, "첨부파일 정보가 없습니다.");
        }

        // 2. 실제 파일 경로
        Resource resource = fileService.getFile((String)fileInfo.get("file_disk_path"), (String)fileInfo.get("file_disk_nm"));
        if (resource == null || !resource.exists()) {
            throw new GaiaBizException(ErrorType.NOT_FOUND, "파일이 존재하지 않습니다.");
        }

        // 3. 파일명 인코딩 (한글/공백/특수문자 대응)
        String encodedDownloadFile = URLEncoder.encode((String)fileInfo.get("file_nm"), StandardCharsets.UTF_8);
        encodedDownloadFile = encodedDownloadFile.replaceAll("\\+", "%20");

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedDownloadFile + "\"")
                .body(resource);
    }
}
