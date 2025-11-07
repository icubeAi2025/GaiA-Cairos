package kr.co.ideait.platform.gaiacairos.comp.system.service;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.document.DocumentForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.document.CbgnHtmlFormDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnContract;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.commoncode.CommonCodeDto;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.document.CbgnPropertyDto;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.document.ConstructionBeginsDocDto;
import kr.co.ideait.platform.gaiacairos.core.type.FileUploadType;
import kr.co.ideait.platform.gaiacairos.core.util.FileService;
import kr.co.ideait.platform.gaiacairos.core.util.FileService.FileMeta;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DocumentManageService extends AbstractGaiaCairosService {
	
	@Autowired
	FileService fileService;
	String baseDirPath = FileUploadType.DOCUMENT_FORM.getDirPath();
	/**
	 * 검색 요청 한 네비게이션 노드 리스트를 반환
	 * 
	 * @param rgstrId ({@link String}) 현재 로그인 된 유저의 회원 아이디
	 * @return List 검색된 {@link ConstructionBeginsDocDto}를 담고있는 리스트
	 */
	public List<ConstructionBeginsDocDto> getNavigationList(String rgstrId, String loginId) {
		List<ConstructionBeginsDocDto> list = mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.document.selectNaviList",rgstrId);
		if(list.size() == 0){
			ConstructionBeginsDocDto params = new ConstructionBeginsDocDto();
			params.setCbgnNm("착공계 관리 문서");
			params.setCbgnPath("");
			params.setUpCbgnNo(0);
			params.setNaviType("FOLDR");
			params.setCbgnLevel(1);
			params.setDocYn("N");
			params.setRgstrId(loginId);

			mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.document.insertNavi",params);
		}
		return list;

	}

	/**
	 * 생성 요청 한 네비게이션 노드의 정보를 테이블에 저장
	 * 
	 * @param loginId ({@link String}) 로그인 한 유저의 로그인 아이디 - ex) apple@juice.com 
	 * @param params ({@link ConstructionBeginsDocDto}) 등록 될 정보들을 담고있는 객체
	 * @return (<em>boolean</em>) 생성 성공 여부
	 */
	public ConstructionBeginsDocDto createNavigationNode(String loginId, @Valid ConstructionBeginsDocDto params) {
		params.setRgstrId(loginId);
		if(mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.document.insertNavi",params) == 1) {
			return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.document.selectDocumentFormData",params.getCbgnNo());
		}
		return null;
	}
	
	/**
	 * 수정 요청 한 네비게이션 노드를 테이블에서 찾아 수정
	 * 
	 * @param loginId ({@link String}) 로그인 한 유저의 로그인 아이디 - ex) apple@juice.com 
	 * @param params ({@link ConstructionBeginsDocDto}) 수정 될 정보들을 담고있는 객체
	 * @return ({@link ConstructionBeginsDocDto}) 생성된 데이터 반환
	 */

	public ConstructionBeginsDocDto updateNavigationNode(String loginId, @Valid ConstructionBeginsDocDto params) {
		params.setRgstrId(loginId);
		if(mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.document.updateNavi",params) != 0) {
			ConstructionBeginsDocDto dto = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.document.selectDocumentFormData",params.getCbgnNo());
			return dto;
		}
		return null;
	}

	/**
	 * Dropdown을 생성할 리스트 검색 후 반환
	 * 
	 * @return {@link List}<{@link CommonCodeDto.CommonCode}> 검색 된 정보들을 담은 리스트
	 */
	public List<CommonCodeDto.CommonCode> getDropdownList() {
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.document.selectDropdownList");
	}

	/**
	 * 문서 양식 파일 정보를 저장
	 * 
	 * @param upCbgnNo ({@link String}) 상위 네비게이션 번호
	 * @param orgnlDocNm ({@link String}) 사용자가 작성한 이름
	 * @param cbgnDocType ({@link String}) 문서 양식 코드
	 * @param cbgnPath ({@link String}) 네비게이션 생성 경로
	 * @param orgFile ({@link MultipartFile}) 업로드 된 파일 데이터
	 * @param cbgnLevel (<em>int</em>) 생성될 파일 노드의 레벨
	 * @param loginId ({@link String}) 현재 로그인 된 유저의 로그인 아이디
	 * @return {@link HashMap}<{@link String},{@link Object}> 생성 후 분기에 따른 응답 데이터
	 */
	public HashMap<String, Object> createDocumentForm(String upCbgnNo, String orgnlDocNm, String cbgnDocType, String cbgnPath, MultipartFile orgFile,
			int cbgnLevel, String loginId, String pdfDocNm, MultipartFile pdfFile) {
		HashMap<String, Object> result = new HashMap<String, Object>();
		String orgFileName = orgFile.getOriginalFilename();
		String pdfFileName = pdfFile.getOriginalFilename();
		String ext = fileService.getFileExtension(orgFileName);
		String pdfExt = fileService.getFileExtension(pdfFileName);
		log.debug("result = " + result);
		if(!(ext.toUpperCase().equals(".XLSX") || ext.toUpperCase().equals(".HWPX"))) {
			result.put("result", "허용되지 않은 확장자");
			return result;
		}
		if(!(pdfExt.toUpperCase().equals(".PDF"))) {
			result.put("result", "허용되지 않은 확장자");
			return result;
		}
		
		ConstructionBeginsDocDto dto = new ConstructionBeginsDocDto();
		
		FileMeta documentFormFileMeta = fileService.save(baseDirPath, orgFile);
		String savedDirPath = documentFormFileMeta.getDirPath();
		String savedFileName = documentFormFileMeta.getFileName();

		FileMeta pdfFormFileMeta = fileService.save(baseDirPath, pdfFile);
		String savedPdfDirPath = pdfFormFileMeta.getDirPath();
		String savedPdfFileName = pdfFormFileMeta.getFileName();
		
		dto.setUpCbgnNo(Integer.parseInt(upCbgnNo));
		dto.setCbgnLevel(cbgnLevel);
		dto.setCbgnPath(cbgnPath);
		dto.setCbgnNm(orgnlDocNm+ext);
		dto.setOrgnlDocDiskNm(savedFileName);
		dto.setOrgnlDocDiskPath(savedDirPath);
		dto.setOrgnlDocNm(orgFileName);
		dto.setPdfDocNm(pdfDocNm);
		dto.setPdfDocDiskNm(savedPdfFileName);
		dto.setPdfDocDiskPath(savedPdfDirPath);
		dto.setDocYn("Y");
		dto.setCbgnDocType(cbgnDocType);
		dto.setRgstrId(loginId);
		
		if(mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.document.insertDocumentFormFile",dto)==1) {
			result.put("createdNode", mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.document.selectDocumentFormData",dto.getCbgnNo()));
		}
		
		return result;
	}
	
	/**
	 * 현재 로그인 된 유저의 모든 계약 목록을 반환
	 * 
	 * @param rgstrId ({@link String}) 현재 로그인 된 유저의 로그인 아이디
	 * @param pjtNo
	 * @return {@link List}<{@link CnContract}> 검색된 계약 리스트
	 */

	public List<CnContract> getMyContractList(String rgstrId, String pjtNo) {
		HashMap<String, String> datas = new HashMap<String, String>();
		datas.put("rgstrId", rgstrId);
		datas.put("pjtNo", pjtNo);
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.document.selectMyContractList",datas);
	}

	public Resource getFileResource(int cbgnNo) throws IOException{
		ConstructionBeginsDocDto dto = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.document.selectDocumentFormData",cbgnNo);

		if(dto.getPdfDocDiskPath() == null || dto.getPdfDocDiskNm() == null) {
			log.debug("dto.getPdfDocDiskPath(), dto.getPdfDocDiskNm() null 값 존재");
			return null;
		}

		Resource resource = fileService.getFile(dto.getPdfDocDiskPath(), dto.getPdfDocDiskNm());
		return resource;
	}

	/**
	 * 
	 * @param params {@link ConstructionBeginsDocDto} 삭제해야 할 노드 정보를 담은 DTO
	 * @return {@link ConstructionBeginsDocDto} 삭제 성공한 노드의 정보를 담은 DTO
	 */
	public ConstructionBeginsDocDto removeNavigationNode(@Valid ConstructionBeginsDocDto params) {
		mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.document.deleteDocumentFormData",params);
		ConstructionBeginsDocDto dto = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.document.selectDocumentFormData",params.getCbgnNo());
		if(dto != null) {
			if("Y".equals(dto.getDltYn())){
				return dto;
			}
		}
		return null;
	}

	/**
	 * 선택 된 계약에 등록되어 있는 문서 양식 파일 리스트 반환
	 * 
	 * @param contractNo ({@link String}) 계약 번호
	 * @return {@link List}<{@link ConstructionBeginsDocDto}> 검색 된 문서 양식 파일 리스트
	 */
	public List<ConstructionBeginsDocDto> getDocumentList(String contractNo) {
		List<ConstructionBeginsDocDto> list = mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.document.selectDocumentListOfContract",contractNo);
		return list;
	}

	/**
	 * 네비게이션의 두 노드의 배치 순서를 전환
	 * 
	 * @param datas ({@link List}<{@link Integer}>) 0,1 에는 바뀔 대상의 정보들, 2,3 에는 이벤트가 일어날 대상 노드의 정보들이 담김<br>
	 * ex) 94번 노드, 95번 노드 순서로 배치되어 있었다면 -> <b>datas:[94,1,95,2]</b>
	 * @return (<em>boolean</em>) 업데이트 성공에 대한 여부
	 */
	public boolean updateOrder(List<Integer> datas) {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		
		map.put("id1", datas.get(0));
		map.put("id2", datas.get(2));
		map.put("order1", datas.get(1));
		map.put("order2", datas.get(3));
		int result = mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.document.updateOrdr",map);
		return result == 2;
	}

	public List<CbgnPropertyDto> getPropertyList(int cbgnNo) {
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.document.selectCbgnPropertyList",cbgnNo);
	}

	public boolean checkDuplicated(String attrbtCd, Integer cbgnNo) {
		HashMap<String, Object> datas = new HashMap<String, Object>();
		datas.put("attrbtCd", attrbtCd);
		datas.put("cbgnNo", cbgnNo);
		int result = (int)mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.document.countAttrbtCdOfCbgnNo",datas);
		return  result == 0;
	}

	public CbgnPropertyDto registProperty(CbgnPropertyDto dto) {
		if(mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.document.insertProperty",dto) == 1) {
			return dto;
		}
		return null;
	}

	public boolean removeProperty(List<Long> attrbtNoList, String usrId) {
		HashMap<String, Object> datas = new HashMap<String, Object>();
		datas.put("attrbtNoList", attrbtNoList);
		datas.put("usrId", usrId);
		return mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.document.deleteProperty",datas) != 0;
	}

	public CbgnPropertyDto getPropertyOfattrbtNo(Integer attrbtNo) {
		return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.document.selectPropertyOfAttrbtNo",attrbtNo);
	}

	public boolean modifyProperty(CbgnPropertyDto dto) {
		return mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.document.updateProperty",dto) == 1;
	}

	// 문서 번호로 pdf 파일명 조회
	public String getPdfDocNm(int cbgnNo) throws IOException{
		ConstructionBeginsDocDto dto = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.document.selectDocumentFormData",cbgnNo);

		if(dto.getPdfDocNm() == null || dto.getPdfDocNm().isEmpty()) {
			log.debug("getPdfDocNm: dto.getPdfDocNm null 값 존재");
			return null;
		}

		return dto.getPdfDocNm();
	}

	public CbgnHtmlFormDto createHtmlPropertyForm(CbgnHtmlFormDto htmlFormDto, String userid) {
		htmlFormDto.setRgstrId(userid);
		if(mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.document.insertHtmlPropertyForm",htmlFormDto) == 1){
			return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.document.selectHtmlPropertyForm",htmlFormDto);
		}
		return null;

	}

	public List<CbgnHtmlFormDto> getHtmlPropertyFormList(String userId) {
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.document.selectHtmlPropertyFormList",userId);
	}

	public boolean removeHtmlPropertyForm(List<Long> formNoList, String usrId) {
		HashMap<String, Object> datas = new HashMap<String, Object>();
		datas.put("formNoList", formNoList);
		datas.put("usrId", usrId);
		return mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.document.deleteHtmlPropertyForm",datas) != 0;
	}

	public CbgnHtmlFormDto getHtmlPropertyForm(Integer formNo, String usrId) {
		CbgnHtmlFormDto htmlFormDto = new CbgnHtmlFormDto();
		htmlFormDto.setFormNo(formNo);
		htmlFormDto.setRgstrId(usrId);
		return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.document.selectHtmlPropertyForm",htmlFormDto);
	}

	public ConstructionBeginsDocDto checkDuplicateOfNavi(String cbgnNm, String userId) {
		HashMap<String, Object> datas = new HashMap<>();
		datas.put("cbgnNm", cbgnNm);
		datas.put("usrId", userId);
		return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.document.selectNaviOfCbgnNm",datas);
	}

	public HashMap<String, Object> getPdfFileResource(Integer cbgnNo) {
		ConstructionBeginsDocDto dto = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.document.selectDocumentFormData",cbgnNo);

		String diskPath = dto.getPdfDocDiskPath();
		String diskNm = dto.getPdfDocDiskNm();

		// 경로 탐색 및 특수문자 검증: "..", "/", "\" 등이 포함되면 예외를 발생.
		if (diskNm.contains("..") || diskNm.contains("/") || diskNm.contains("\\")) {
			throw new GaiaBizException(ErrorType.BAD_REQUEST, "Invalid file name.");
		}

		File file = new File(diskPath, diskNm);
		log.info("FILE : "+file);
		if (!file.exists()) {
			return null;
		}

		String encodedDownloadFile = URLEncoder.encode(diskNm, StandardCharsets.UTF_8);

		HashMap<String,Object> result = new HashMap<>();
		result.put("resource",fileService.getFile(diskPath, diskNm));
		result.put("encodedDownloadFile",encodedDownloadFile);

		return result;
	}

	public CbgnHtmlFormDto modifyHtmlPropertyForm(String userId, CbgnHtmlFormDto htmlFormDto) {
		htmlFormDto.setChgId(userId);
		htmlFormDto.setRgstrId(userId);
		if(mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.document.updateHtmlPropertyForm",htmlFormDto) == 1){
			return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.document.selectHtmlPropertyForm",htmlFormDto);
		}
		return null;
	}

	/**
	 *
	 * @param cbgnDocType ({@link String})
	 * <ul>
	 * 		<li>APP01 - 감리일지</li>
	 *      <li>APP02 - 작업일지</li>
	 *      <li>APP03 - 책임감리일지</li>
	 * </ul>
	 * @return {@link HashMap}
	 * <ul>
	 *     <li>cbgnDto - 양식 파일 DTO</li>
	 *     <li>properties - 해당 폴더에 등록된 속성 DTO 리스트</li>
	 * </ul>
	 */
	public HashMap<String,Object> getCbgnAndProperties(String cbgnDocType){
		HashMap<String,Object> params = new HashMap<>();
		HashMap<String,Object> result = new HashMap<>();

		params.put("cbgnDocType",cbgnDocType);
		params.put("dltYn","N");
		ConstructionBeginsDocDto cbgnDto = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.document.selectDocumentForm",params);
		if(cbgnDto == null){
			throw new GaiaBizException(ErrorType.BAD_REQUEST, "Invalid cbgnDocType.");
		}

		List<CbgnPropertyDto> properties = mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.document.selectCbgnPropertyList",cbgnDto.getUpCbgnNo());

		result.put("cbgnDto",cbgnDto);
		result.put("properties",properties);
		return result;
	}


	/**
	 * 
	 * @param properties ({@link List}) {@link CbgnPropertyDto} 타입이 담긴 리스트
	 * @param naviId 등록할 네비 아이디
	 * @return {@link List} {@link kr.co.ideait.platform.gaiacairos.core.persistence.vo.document.DocumentForm.PropertyCreate} 타입이 담긴 리스트
	 */
	public List<DocumentForm.PropertyCreate> parseToPropertyCreate(List<CbgnPropertyDto> properties, String naviId) {
		if(properties != null) {
			List<DocumentForm.PropertyCreate> propertyList = new ArrayList<>();
			short order = 1;

			for (CbgnPropertyDto property : properties) {
				DocumentForm.PropertyCreate propertyCreate = new DocumentForm.PropertyCreate();
				propertyCreate.setNaviId(naviId);
				propertyCreate.setAttrbtCd(property.getAttrbtCd());
				propertyCreate.setAttrbtCdType(property.getAttrbtCdType());
				propertyCreate.setAttrbtType(property.getAttrbtType());
				propertyCreate.setAttrbtTypeSel(property.getAttrbtTypeSel());
				propertyCreate.setAttrbtNmEng(property.getAttrbtNmEng());
				propertyCreate.setAttrbtNmKrn(property.getAttrbtNmKrn());
				propertyCreate.setAttrbtDsplyOrder(property.getAttrbtDsplyOrder().shortValue());
				propertyCreate.setAttrbtDsplyYn(property.getAttrbtDsplyYn());
				propertyCreate.setAttrbtChgYn(property.getAttrbtChgYn());

				propertyList.add(propertyCreate);
			}

			return propertyList;
		}
		return null;
	}
}









