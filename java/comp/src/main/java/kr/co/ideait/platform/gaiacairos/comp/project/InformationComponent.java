package kr.co.ideait.platform.gaiacairos.comp.project;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Maps;
import kr.co.ideait.iframework.BizException;
import kr.co.ideait.platform.gaiacairos.comp.document.service.DocumentService;
import kr.co.ideait.platform.gaiacairos.comp.project.helper.ProjectInitializer;
import kr.co.ideait.platform.gaiacairos.comp.project.service.ContractstatusService;
import kr.co.ideait.platform.gaiacairos.comp.project.service.InformationService;
import kr.co.ideait.platform.gaiacairos.comp.system.service.DepartmentService;
import kr.co.ideait.platform.gaiacairos.comp.system.service.ProjectInstallManageService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractComponent;
import kr.co.ideait.platform.gaiacairos.core.constant.CommonCodeConstants;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.*;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.contract.contractstatus.ContractstatusMybatisParam;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.overview.information.InformationForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.overview.information.InformationMybatisParam;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.overview.information.InformationMybatisParam.InformationListInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.overview.information.InformationMybatisParam.InformationOutput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.overview.information.InformationMybatisParam.ProjectProcedure;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.type.FileUploadType;
import kr.co.ideait.platform.gaiacairos.core.type.PlatformType;
import kr.co.ideait.platform.gaiacairos.core.util.FileService;
import kr.co.ideait.platform.gaiacairos.core.util.FileService.FileMeta;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class InformationComponent extends AbstractComponent {

	@Autowired
	InformationService informationService;

	@Autowired
	FileService fileService;

	@Autowired
	ProjectInitializer projectInitializer;

	@Autowired
	ContractstatusService contractService;

	@Autowired
	InformationForm informationForm;

	@Autowired
	DocumentService documentService;

	@Autowired
	ProjectInstallManageService projectInstallManageService;

	@Autowired
	DepartmentService departmentService;

    @Autowired
    private ContractStatusComponent contractStatusComponent;

	/**
	 * 사업정보 목록조회
	 * @param InformationForm.InformationListGet
	 * @return List<InformationOutput>
	 */
	public List<InformationOutput> getInformationList(InformationForm.InformationListGet informationListGet) {
		InformationMybatisParam.InformationListInput input = informationForm.toInformationListInput(informationListGet);

		LocalDate today = LocalDate.now();
		DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

		String searchTerm = input.getSearchTerm();
		String startDate;
		String endDate;

		if (searchTerm != null && !searchTerm.isBlank() && !"all".equalsIgnoreCase(searchTerm)) {
			if ("custom".equalsIgnoreCase(searchTerm)) {
				startDate = LocalDate.parse(input.getStartDate(), inputFormatter).format(outputFormatter);
				endDate = LocalDate.parse(input.getEndDate(), inputFormatter).format(outputFormatter);
			} else {
				// searchTerm이 숫자일 때, startDate = 오늘날짜 - searchTerm * 달, endDate = 오늘날짜
				int months = Integer.parseInt(searchTerm);
				startDate = today.minusMonths(months).format(outputFormatter);
				endDate = today.format(outputFormatter);
			}
			input.setStartDate(startDate);
			input.setEndDate(endDate);
		}

		// 공통 입력값 설정
		input.setCmnGrpCdMajorCnstty(CommonCodeConstants.MAJOR_CNSTTY_CODE_GROUP_CODE);
		input.setCmnGrpCdConPstats(CommonCodeConstants.CON_PSTATS_CODE_GROUP_CODE);
		input.setSearchTerm(searchTerm);
		input.setUserType(UserAuth.get(true).isAdmin() ? "ADMIN" : "NORMAL");
		input.setUsrId(UserAuth.get(true).getUsrId());
		input.setSystemType(platform.toUpperCase());

		return informationService.getInformationList(input);
	}

	/**
	 * 사업정보 상세조회
	 * @param String (pjtNo)
	 * @return Map<String, Object>
	 */
	public Map<String, Object> getInformationDetail(String pjtNo) {

		InformationListInput infoInput = new InformationListInput();

		infoInput.setPjtNo(pjtNo);
		infoInput.setCmnGrpCdMajorCnstty(CommonCodeConstants.MAJOR_CNSTTY_CODE_GROUP_CODE);
		infoInput.setCmnGrpCdRgnCd(CommonCodeConstants.RG_CODE_GROUP_CODE);
		infoInput.setCmnGrpCdAcrarchlawUsgCd(CommonCodeConstants.ACRARCHLAW_USG_GROUP_CODE);
		infoInput.setCmnGrpCdCntrctType(CommonCodeConstants.CNTRCT_TYPE_GROUP_CODE);
		infoInput.setCmnGrpCdConPstats(CommonCodeConstants.CON_PSTATS_CODE_GROUP_CODE);
		infoInput.setCmnGrpCdDminsttCd(CommonCodeConstants.DMINSTT_CODE_GROUP_CODE);

		InformationOutput cnProject = informationService.getProjectDetail(infoInput);

		Map<String, Object> returnMap = new HashMap<>();
		returnMap.put("project",cnProject);

		// 조감도
		String airvwAtchFileNo = cnProject.getAirvwAtchFileNo();
		if (airvwAtchFileNo != null && !airvwAtchFileNo.isEmpty()) {
			CnAttachments attachment = informationService.getFile(Integer.parseInt(airvwAtchFileNo));
			returnMap.put("attachment", attachment);
		}

		// 녹색건축인증
		String greenLevelDocId = cnProject.getGreenLevelDocId();
		if (greenLevelDocId != null && !greenLevelDocId.isEmpty()) {
			DcStorageMain greenLevelDoc = documentService.getDcStorageMain(greenLevelDocId);
			returnMap.put("greenLevelDoc", greenLevelDoc);
		}
		// 건축물에너지
		String energyEffectLevelDocId = cnProject.getEnergyEffectLevelDocId();
		if (energyEffectLevelDocId != null && !energyEffectLevelDocId.isEmpty()) {
			DcStorageMain energyEffectLevelDoc = documentService.getDcStorageMain(energyEffectLevelDocId);
			returnMap.put("energyEffectLevelDoc", energyEffectLevelDoc);
		}
		//제로에너지
		String zeroEnergyLevelDocId = cnProject.getZeroEnergyLevelDocId();
		if (zeroEnergyLevelDocId != null && !zeroEnergyLevelDocId.isEmpty()) {
			DcStorageMain zeroEnergyLevelDoc = documentService.getDcStorageMain(zeroEnergyLevelDocId);
			returnMap.put("zeroEnergyLevelDoc", zeroEnergyLevelDoc);
		}
		// BF
		String bfLevelDocId = cnProject.getBfLevelDocId();
		if (bfLevelDocId != null && !bfLevelDocId.isEmpty()) {
			DcStorageMain bfLevelDoc = documentService.getDcStorageMain(bfLevelDocId);
			returnMap.put("bfLevelDoc", bfLevelDoc);
		}

		return returnMap;
	}

	/**
	 * 사업정보 추가
	 * @param InformationForm.RegisterInformation (information)
	 * @param MultipartFile (fileNm)
	 * @param MultipartFile (greenLevelDoc)
	 * @param MultipartFile (energyEffectLevelDoc)
	 * @param MultipartFile (zeroEnergyLevelDoc)
	 * @param MultipartFile (bfLevelDoc)
	 * @param String (apiYn)
	 * @return CnProject
	 */
	@Transactional
	public CnProject createInformation(InformationForm.RegisterInformation information, MultipartFile mFile, MultipartFile greenLevelDoc,
									   MultipartFile energyEffectLevelDoc, MultipartFile zeroEnergyLevelDoc, MultipartFile bfLevelDoc,String apiYn) {

		CnProject cnProject = informationForm.toRegisterInformation(information);

		// 친환경 인증파일 저장
		Map<MultipartFile, String> fileDocIdMap = new LinkedHashMap<>();
		if (greenLevelDoc != null) {
			String docId = UUID.randomUUID().toString();
			fileDocIdMap.put(greenLevelDoc, docId);
			cnProject.setGreenLevelDocId(docId);
		}
		if (energyEffectLevelDoc != null) {
			String docId = UUID.randomUUID().toString();
			fileDocIdMap.put(energyEffectLevelDoc, docId);
			cnProject.setEnergyEffectLevelDocId(docId);
		}
		if (zeroEnergyLevelDoc != null) {
			String docId = UUID.randomUUID().toString();
			fileDocIdMap.put(zeroEnergyLevelDoc, docId);
			cnProject.setZeroEnergyLevelDocId(docId);
		}
		if (bfLevelDoc != null) {
			String docId = UUID.randomUUID().toString();
			fileDocIdMap.put(bfLevelDoc, docId);
			cnProject.setBfLevelDocId(docId);
		}

		//현장 개설 요청을 통한 개설(CAIROS, GAIA, PGAIA)
		String pjtDiv = !StringUtils.isEmpty(cnProject.getPlcReqNo()) ? cnProject.getPlcReqNo().substring(0, 1) : PlatformType.PGAIA.getName().equals(platform) ? "P" : "G";

		LocalDate now = LocalDate.now();
		String year = now.format(DateTimeFormatter.ofPattern("yyyy"));
		String month = now.format(DateTimeFormatter.ofPattern("MM"));
		String yearMonth = year + month;


		cnProject.setPjtNo(informationService.generatePjtNo(pjtDiv, yearMonth));
		cnProject.setPjtDiv(pjtDiv);
		cnProject.setDltYn("N");

		// 첨부한 파일이 있으면
		if (mFile != null && !mFile.isEmpty()) {
			Integer fileNo = setAttachment(mFile,null, UserAuth.get(true).getUsrId(),cnProject.getPjtNo());
			cnProject.setAirvwAtchFileNo(fileNo.toString());
		}

		// 프로젝트 등록
		CnProject saveProject = informationService.saveProject(cnProject);
		runProcedure(cnProject,"ADD");

		if (saveProject == null) {
			throw new GaiaBizException(ErrorType.BAD_REQUEST, "프로젝트 생성에 실패했습니다.");
		}

		//개설요청 - 프로젝트번호 업데이트
		if(cnProject.getPlcReqNo() != null && !cnProject.getPlcReqNo().isBlank()){
			projectInstallManageService.updateProjectNoByPlcReqNo(cnProject.getPlcReqNo(), cnProject.getPjtNo());
		}

		//자동 생성된 부서에 deptUuid 만들기
		HashMap<String,String> deptUuidMap = new HashMap<>();
		List<SmDepartment> departmentList = departmentService.getDepartmentListOfProject(saveProject.getPjtNo());
		for(SmDepartment department : departmentList){
			String deptUuid = UUID.randomUUID().toString();
			departmentService.modifyUuidOfDepartmentFindByDeptId(department.getDeptId(),deptUuid);
			deptUuidMap.put(department.getDeptId(),deptUuid);
		}

		// 친환경 네비게이션 생성
		DcNavigation dcNavigation = documentService.createEvrfrndNavi(cnProject.getPjtNo(),"");

		if (dcNavigation == null) {
			throw new GaiaBizException(ErrorType.BAD_REQUEST, "친환경 경로 생성에 실패했습니다.");
		}

		// 친환경 첨부파일이 비어있지 않으면 문서 생성
		List<DcStorageMain> savedDocs = null;
		if (!fileDocIdMap.isEmpty()) {
			savedDocs = documentService.createEvrfrndDoc( cnProject.getPjtNo(), cnProject.getPjtNm(), fileDocIdMap, dcNavigation.getNaviId(), dcNavigation.getNaviNo(),null);

			if (savedDocs == null || savedDocs.isEmpty()) {
				throw new GaiaBizException(ErrorType.BAD_REQUEST, "친환경 문서 생성에 실패했습니다.");
			}
		}

		// API 연동
		// PGAIA : PGAIA -> CAIROS || CAIROS의 plcReqNo가 P : CAIROS -> PGAIA
		Map<String, Object> invokeParams = Maps.newHashMap();

		invokeParams.put("cnProject", saveProject);
		invokeParams.put("usrId", UserAuth.get(true).getUsrId());
		invokeParams.put("deptUuidMap", deptUuidMap);

		Map<String, Object> fileMap = Maps.newHashMap();
		fileMap.put("file", mFile);
		fileMap.put("greenLevelDoc", greenLevelDoc);
		fileMap.put("energyEffectLevelDoc", energyEffectLevelDoc);
		fileMap.put("zeroEnergyLevelDoc", zeroEnergyLevelDoc);
		fileMap.put("bfLevelDoc", bfLevelDoc);

		if("Y".equals(apiYn)){
			Map response = null;
			// PGAIA2CAIROS
			if ( PlatformType.PGAIA.getName().equals(platform) ) {
				response = invokePgaia2Cairos("GACA1004", invokeParams, fileMap);
				if (!"00".equals( MapUtils.getString(response, "resultCode") ) ) {
					throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(response, "resultMsg"));
				}

				// CAIROS2PGAIA
			}else if(PlatformType.CAIROS.getName().equals(platform) &&  cnProject.getPlcReqNo() != null && !cnProject.getPlcReqNo().isBlank() && "P".equals(pjtDiv)) {
				response = invokeCairos2Pgaia("GACA1004", invokeParams, fileMap);
				if (!"00".equals( MapUtils.getString(response, "resultCode") ) ) {
					throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(response, "resultMsg"));
				}
			}
		}

		return saveProject;
	}

	/**
	 * 사업정보 수정
	 * @param String (pjtNo)
	 * @param InformationForm.InformationUpdate (information)
	 * @param MultipartFile (fileNm)
	 * @param MultipartFile (greenLevelDoc)
	 * @param MultipartFile (energyEffectLevelDoc)
	 * @param MultipartFile (zeroEnergyLevelDoc)
	 * @param MultipartFile (bfLevelDoc)
	 * @param String (apiYn)
	 * @return CnProject
	 */
	@Transactional
	public void updateInformation(String pjtNo, InformationForm.InformationUpdate information, MultipartFile newFiles, String deleteFileYn,
								  MultipartFile greenLevelDoc, MultipartFile energyEffectLevelDoc,
								  MultipartFile zeroEnergyLevelDoc, MultipartFile bfLevelDoc, List<String> deleteEcoDocIds, String apiYn)
			throws IllegalStateException {

		CnProject cnProject = informationService.getProject(pjtNo);

		Map<MultipartFile, String> fileDocIdMap = new LinkedHashMap<>();

		// 각 문서 처리
		if (deleteEcoDocIds != null) {
			for (String deleteEcoDocId : deleteEcoDocIds) {
				documentService.deleteEvrfrndDoc(deleteEcoDocId, UserAuth.get(true).getUsrId());
			}
		}
		if (greenLevelDoc != null) {
			String docId = UUID.randomUUID().toString();
			fileDocIdMap.put(greenLevelDoc, docId);
			cnProject.setGreenLevelDocId(docId);
		}
		if (energyEffectLevelDoc != null) {
			String docId = UUID.randomUUID().toString();
			fileDocIdMap.put(energyEffectLevelDoc, docId);
			cnProject.setEnergyEffectLevelDocId(docId);
		}
		if (zeroEnergyLevelDoc != null) {
			String docId = UUID.randomUUID().toString();
			fileDocIdMap.put(zeroEnergyLevelDoc, docId);
			cnProject.setZeroEnergyLevelDocId(docId);
		}
		if (bfLevelDoc != null) {
			String docId = UUID.randomUUID().toString();
			fileDocIdMap.put(bfLevelDoc, docId);
			cnProject.setBfLevelDocId(docId);
		}

		// 네비 생성
		DcNavigation dcNavigation = documentService.createEvrfrndNavi(cnProject.getPjtNo(),"");
		if (dcNavigation == null) {
			throw new GaiaBizException(ErrorType.BAD_REQUEST, "친환경 경로 생성에 실패했습니다.");
		}

		// 새 파일 등록
		List<DcStorageMain> savedDocs = null;
		if (!fileDocIdMap.isEmpty()) {
			savedDocs = documentService.createEvrfrndDoc(
					cnProject.getPjtNo(), cnProject.getPjtNm(),
					fileDocIdMap, dcNavigation.getNaviId(), dcNavigation.getNaviNo(),null);

			if (savedDocs == null || savedDocs.isEmpty()) {
				throw new GaiaBizException(ErrorType.BAD_REQUEST, "친환경 문서 생성에 실패했습니다.");
			}
		}

		// 사업정보 수정 공통 호출
		informationForm.updateCnProject(information, cnProject);
		updateProject(cnProject, newFiles, deleteFileYn, UserAuth.get(true).getUsrId());

		// API 연동
		if("Y".equals(apiYn)) {
			Map<String, Object> invokeParams = Maps.newHashMap();
			invokeParams.put("cnProject", cnProject);
			invokeParams.put("deleteFileYn", deleteFileYn);
			invokeParams.put("deleteEcoDocIds", deleteEcoDocIds);
			invokeParams.put("usrId", UserAuth.get(true).getUsrId());

			Map<String, Object> fileMap = Maps.newHashMap();
			fileMap.put("file", newFiles);
			fileMap.put("greenLevelDoc", greenLevelDoc);
			fileMap.put("energyEffectLevelDoc", energyEffectLevelDoc);
			fileMap.put("zeroEnergyLevelDoc", zeroEnergyLevelDoc);
			fileMap.put("bfLevelDoc", bfLevelDoc);

			Map response = null;
			// PGAIA2CAIROS
			if (PlatformType.PGAIA.getName().equals(platform)) {
				response = invokePgaia2Cairos("GACA1005", invokeParams, fileMap);
				if (!"00".equals(MapUtils.getString(response, "resultCode"))) {
					throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(response, "resultMsg"));
				}
				// CAIROS2PGAIA, PGAIA에서 만든 현장개설 요청 및 PGAIA에서 만든 프로젝트일때
			} else if (PlatformType.CAIROS.getName().equals(platform) && "P".equals(cnProject.getPjtDiv())) {
				response = invokeCairos2Pgaia("GACA1005", invokeParams, fileMap);
				if (!"00".equals(MapUtils.getString(response, "resultCode"))) {
					throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(response, "resultMsg"));
				}
				// GAIA2PGAIA, PGAIA에서 만든 프로젝트일때
			} else if (PlatformType.GAIA.getName().equals(platform) && "P".equals(cnProject.getPjtDiv())) {
				response = invokeGaia2Pgaia("GACA1005", invokeParams, fileMap);
				if (!"00".equals(MapUtils.getString(response, "resultCode"))) {
					throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(response, "resultMsg"));
				}
			}
		}
	}

	/**
	 * 사업정보 수정 공통 부분 추출 (화면 수정 & API 수정)
	 * @param String (pjtNo)
	 * @param InformationForm.InformationUpdate (information)
	 * @param MultipartFile (fileNm)
	 * @param MultipartFile (greenLevelDoc)
	 * @param MultipartFile (energyEffectLevelDoc)
	 * @param MultipartFile (zeroEnergyLevelDoc)
	 * @param MultipartFile (bfLevelDoc)
	 * @param String (apiYn)
	 * @return CnProject
	 */
	public void updateProject (CnProject cnProject, MultipartFile newFiles, String deleteFileYn, String usrId){
		String airvwAtchFileNo = cnProject.getAirvwAtchFileNo();

		// 기존 조감도 파일번호가 있을 때
		if (airvwAtchFileNo != null && !airvwAtchFileNo.isBlank()) {

			CnAttachments existingAttachments = informationService.getFile(Integer.parseInt(airvwAtchFileNo));

			// 삭제할 파일이 있으면 삭제
			if ("Y".equalsIgnoreCase(deleteFileYn) && existingAttachments != null) {
				informationService.deleteAttachment(existingAttachments, usrId);
			}
			// 첨부파일 있으면 추가
			if (newFiles != null && !newFiles.isEmpty()) {
				setAttachment(newFiles, Integer.parseInt(airvwAtchFileNo), usrId, cnProject.getPjtNo());
			}

		// 기존 조감도 파일번호가 없을 때 첨부파일 있으면 추가
		}else if(newFiles != null && !newFiles.isEmpty()){

			Integer fileNo = setAttachment(newFiles,null, usrId, cnProject.getPjtNo());
			cnProject.setAirvwAtchFileNo(fileNo.toString());

		}

		informationService.saveProject(cnProject);
		// 프로시저 실행
		runProcedure(cnProject,"UPDATE");
	}

	/**
	 * 사업정보 삭제
	 * @param List<String> (informationList)
	 * @param String (apiYn)
	 * @return
	 */
	@Transactional
	public void deleteInformation (List<String> informationList, String apiYn){

		List<String> pgaiaList = new ArrayList<>();

		informationList.forEach(id1 -> {
			CnProject project = informationService.getProject(id1);
			if (project == null) {
				log.info("삭제 대상 프로젝트가 존재하지 않음: {}", id1);
				return;
			}
			if("P".equals(project.getPjtDiv())){
				pgaiaList.add(id1);
			}

			InformationMybatisParam.InformationDeleteInput deleteInput = new InformationMybatisParam.InformationDeleteInput();
			deleteInput.setPjtNo(id1);
			deleteInput.setUsrId(UserAuth.get(true).getUsrId());

			informationService.deleteProject(deleteInput);
			runProcedure(project, "DEL");
		});

		contractStatusComponent.deleteAllContract(informationList,UserAuth.get(true).getUsrId());

		// API 통신
		if("Y".equals(apiYn)){
			Map<String, Object> invokeParams = Maps.newHashMap();
			invokeParams.put("usrId", UserAuth.get(true).getUsrId());

			Map response;
			// PGAIA일 경우만
			if ( PlatformType.PGAIA.getName().equals(platform)) {
				invokeParams.put("informationList", informationList);
				response = invokePgaia2Cairos("GACA1006", invokeParams);
				if (!"00".equals( MapUtils.getString(response, "resultCode") ) ) {
					throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(response, "resultMsg"));
				}

				// GAIA일 경우만 API 통신
			}else if(PlatformType.GAIA.getName().equals(platform)){
				invokeParams.put("informationList", pgaiaList);
				response = invokeGaia2Pgaia("GACA1006", invokeParams);
				if (!"00".equals( MapUtils.getString(response, "resultCode") ) ) {
					throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(response, "resultMsg"));
				}

				// CAIROS일 경우만 API 통신
			}else if(PlatformType.CAIROS.getName().equals(platform)){
				invokeParams.put("informationList", pgaiaList);
				response = invokeCairos2Pgaia("GACA1006", invokeParams);
				if (!"00".equals( MapUtils.getString(response, "resultCode") ) ) {
					throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(response, "resultMsg"));
				}
			}
		}
	}

	/**
	 * 부서정보 및 자동 생성
	 * @param CnProject (project)
	 * @param String (type)
	 * @return
	 */
	public void runProcedure(CnProject project, String type) {
		String pPjttype = "P";
		String pPjtno = project.getPjtNo();
		String pCntrctno = project.getPjtNo();
		String pItemname = project.getPjtNm();
		String pItemdesc = (project.getEtcCntnts() != null) ? project.getEtcCntnts() : "";
		String pCorpno = (project.getDminsttCd() != null) ? project.getDminsttCd() : "0";

		ProjectProcedure input = new ProjectProcedure();
		input.setPInserttype(type); // "ADD", "UPDATE", "DEL"
		input.setPPjttype("P");
		input.setPPjtno(pPjtno);
		input.setPCntrctno(pCntrctno);
		input.setPItemname(pItemname);
		input.setPItemdesc(pItemdesc);
		input.setPCorpno(pCorpno);

		// ProjectInitializer 호출
		switch (type.toUpperCase()) {
			case "ADD" -> projectInitializer.addProject(pCorpno, pPjttype, pPjtno, pCntrctno, pItemname, pItemdesc);
			case "UPDATE" -> projectInitializer.modifyProject(pCorpno, pPjttype, pPjtno, pCntrctno, pItemname, pItemdesc);
			case "DEL" -> projectInitializer.removeProject(pPjttype, pPjtno, pCntrctno);
		}
	}

	// 첨부파일저장
	public Integer setAttachment (MultipartFile newFile,Integer fileNo, String usrId, String pjtNo){

		FileMeta fileMeta = fileService.save(getUploadPathByWorkType(FileUploadType.PROJECT, pjtNo), newFile);

		// fileNo != null 이면 수정 아니면 추가
		Integer setFileNo = fileNo != null ? fileNo : informationService.generateFileNo();
		Integer sno = fileNo != null ? informationService.generateSno(setFileNo) : 1 ;

		CnAttachments cnAttachment = new CnAttachments();

		cnAttachment.setFileNm(newFile.getOriginalFilename());
		cnAttachment.setFileDiskNm(fileMeta.getFileName());
		cnAttachment.setFileDiskPath(fileMeta.getDirPath());
		cnAttachment.setFileSize(fileMeta.getSize());
		cnAttachment.setDltYn("N");
		cnAttachment.setFileHitNum(0);
		cnAttachment.setFileNo(setFileNo);
		cnAttachment.setSno(sno);
		cnAttachment.setRgstrId(usrId);
		cnAttachment.setChgId(usrId);
		cnAttachment.setRgstrId(usrId);
		cnAttachment.setChgId(usrId);

		informationService.saveCnAttachment(cnAttachment);

		return setFileNo;
	}

	// 수요기관 조회
	public ContractstatusMybatisParam.Dminstt getDminstt (String cntrctNo){
		if (cntrctNo == null) {
			return null;
		}
		String baseCntrctNo = cntrctNo.split("\\.")[0];
		return contractService.getDminstt(baseCntrctNo);
	}
	// ----------------------------------------API통신--------------------------------------------

	/**
	 * 프로젝트 API 통신 (프로젝트 정보 추가)
	 *
	 * @param msgId
	 * @param params
	 * @return
	 */
	@Transactional
	public Map<String, Object> insertProjectApi(String msgId, Map<String, Object> params) {
		Map<String, Object> result = new HashMap<>();

		if (!"GACA1004".equals(msgId) || org.apache.commons.collections.MapUtils.isEmpty(params)) {
			throw new BizException("유효하지 않은 트랜잭션 또는 파라미터 없음");
		}

		log.info("API 연동 params : {}", params);

		// 필수 데이터 변환
		CnProject cnProject = objectMapper.convertValue(params.get("cnProject"), CnProject.class);
		HashMap<String,String> deptUuidMap = objectMapper.convertValue(params.get("deptUuidMap"), HashMap.class);
		String usrId = (String) params.get("usrId");

		// 첨부파일 추출
		MultipartFile mFile = extractMultipartFile(params.get("file"));
		MultipartFile greenLevelDoc = extractMultipartFile(params.get("greenLevelDoc"));
		MultipartFile energyEffectLevelDoc = extractMultipartFile(params.get("energyEffectLevelDoc"));
		MultipartFile zeroEnergyLevelDoc = extractMultipartFile(params.get("zeroEnergyLevelDoc"));
		MultipartFile bfLevelDoc = extractMultipartFile(params.get("bfLevelDoc"));

		// 문서 ID 세팅 및 파일 매핑
		Map<MultipartFile, String> fileDocIdMap = new LinkedHashMap<>();
		if (greenLevelDoc != null) {
			fileDocIdMap.put(greenLevelDoc, cnProject.getGreenLevelDocId());
		}
		if (energyEffectLevelDoc != null) {
			fileDocIdMap.put(energyEffectLevelDoc, cnProject.getEnergyEffectLevelDocId());
		}
		if (zeroEnergyLevelDoc != null) {
			fileDocIdMap.put(zeroEnergyLevelDoc, cnProject.getZeroEnergyLevelDocId());
		}
		if (bfLevelDoc != null) {
			fileDocIdMap.put(bfLevelDoc, cnProject.getBfLevelDocId());
		}

		// 첨부한 파일이 있으면
		if (mFile != null && !mFile.isEmpty()) {
			Integer fileNo = setAttachment(mFile,null, usrId, cnProject.getPjtNo());
			cnProject.setAirvwAtchFileNo(fileNo.toString());
		}

		cnProject.setRgstrId(usrId);
		cnProject.setChgId(usrId);
		informationService.saveProject(cnProject);

		// 20250822 PGAIA-GAIA 연동 사업생성시 부서 생성 임시 주석처리 jhkim
		// runProcedure(cnProject,"ADD");

		Set<Map.Entry<String,String>> entrySet = deptUuidMap.entrySet();
		for (Map.Entry<String,String> entry : entrySet) {
			String key = entry.getKey();
			String value = entry.getValue();
			departmentService.modifyUuidOfDepartmentFindByDeptId(key,value);
		}

		//개설요청 - 프로젝트번호 업데이트
		if(cnProject.getPlcReqNo() != null && !cnProject.getPlcReqNo().isBlank()){
			projectInstallManageService.updateProjectNoByPlcReqNo(cnProject.getPlcReqNo(), cnProject.getPjtNo());
		}


		// 20250822 PGAIA-GAIA 연동 사업생성시 부서 생성 임시 주석처리 jhkim
		// 친환경 네비게이션 생성
//		DcNavigation dcNavigation = documentService.createEvrfrndNavi(cnProject.getPjtNo(), usrId);
//		if (dcNavigation == null) {
//			throw new GaiaBizException(ErrorType.BAD_REQUEST, "친환경 네비게이션 생성 실패");
//		}

		// 문서 등록
//		if (!fileDocIdMap.isEmpty()) {
//			List<DcStorageMain> savedDocs = documentService.createEvrfrndDoc(
//					cnProject.getPjtNo(),
//					cnProject.getPjtNm(),
//					fileDocIdMap,
//					dcNavigation.getNaviId(),
//					dcNavigation.getNaviNo(),
//					usrId
//			);
//
//			if (savedDocs == null || savedDocs.isEmpty()) {
//				throw new GaiaBizException(ErrorType.BAD_REQUEST, "친환경 문서 생성 실패");
//			}
//		}

		result.put("resultCode", "00");
		return result;
	}

	/**
	 * 프로젝트 API 통신 (프로젝트 정보 수정)
	 *
	 * @param msgId
	 * @param params
	 * @return
	 */
	@Transactional
	public Map<String, Object> updateProjectApi(String msgId, Map<String, Object> params) throws IOException {
		Map<String, Object> result = new HashMap<>();

		if (org.apache.commons.collections.MapUtils.isEmpty(params)) {
			throw new BizException("params is empty");
		}

		if ("GACA1005".equals(msgId)) {
			log.info("API 연동 params : {}", params);

			CnProject cnProject = objectMapper.convertValue(params.get("cnProject"), CnProject.class);

			// 첨부파일 추출
			MultipartFile newFiles = extractMultipartFile(params.get("file"));
			MultipartFile greenLevelDoc = extractMultipartFile(params.get("greenLevelDoc"));
			MultipartFile energyEffectLevelDoc = extractMultipartFile(params.get("energyEffectLevelDoc"));
			MultipartFile zeroEnergyLevelDoc = extractMultipartFile(params.get("zeroEnergyLevelDoc"));
			MultipartFile bfLevelDoc = extractMultipartFile(params.get("bfLevelDoc"));

			String deleteFileYn = (String) params.get("deleteFileYn");
			List<String> deleteEcoDocIds = (List<String>) params.get("deleteEcoDocIds");
			String usrId = (String) params.get("usrId");

			CnProject preProject = informationService.getProject(cnProject.getPjtNo());
			cnProject.setAirvwAtchFileNo(preProject.getAirvwAtchFileNo());
			// 사업정보 수정 공통 호출
			updateProject(cnProject, newFiles, deleteFileYn, usrId);

			// 네비 생성
//			DcNavigation dcNavigation = documentService.createEvrfrndNavi(cnProject.getPjtNo(),"");
//			if (dcNavigation == null) {
//				throw new GaiaBizException(ErrorType.BAD_REQUEST, "친환경 경로 생성에 실패");
//			}

			// 문서 ID 세팅 및 파일 매핑
//			if (deleteEcoDocIds != null) {
//				for (String deleteEcoDocId : deleteEcoDocIds) {
//					documentService.deleteEvrfrndDoc(deleteEcoDocId, usrId);
//				}
//			}

			Map<MultipartFile, String> fileDocIdMap = new LinkedHashMap<>();
			if (greenLevelDoc != null) {
				String docId = cnProject.getGreenLevelDocId();
				fileDocIdMap.put(greenLevelDoc, docId);
				cnProject.setGreenLevelDocId(docId);
			}
			if (energyEffectLevelDoc != null) {
				String docId = cnProject.getEnergyEffectLevelDocId();
				fileDocIdMap.put(energyEffectLevelDoc, docId);
				cnProject.setEnergyEffectLevelDocId(docId);
			}
			if (zeroEnergyLevelDoc != null) {
				String docId = cnProject.getZeroEnergyLevelDocId();
				fileDocIdMap.put(zeroEnergyLevelDoc, docId);
				cnProject.setZeroEnergyLevelDocId(docId);
			}
			if (bfLevelDoc != null) {
				String docId = cnProject.getBfLevelDocId();
				fileDocIdMap.put(bfLevelDoc, docId);
				cnProject.setBfLevelDocId(docId);
			}

			// 새 파일 등록
//			List<DcStorageMain> savedDocs = null;
//			if (!fileDocIdMap.isEmpty()) {
//				savedDocs = documentService.createEvrfrndDoc(
//						cnProject.getPjtNo(), cnProject.getPjtNm(),
//						fileDocIdMap, dcNavigation.getNaviId(), dcNavigation.getNaviNo(),usrId);
//
//				if (savedDocs == null || savedDocs.isEmpty()) {
//					throw new GaiaBizException(ErrorType.BAD_REQUEST, "친환경 문서 생성에 실패");
//				}
//			}
		}

		result.put("resultCode", "00");

		return result;
	}

	/**
	 * 프로젝트 API 통신 (프로젝트 정보 삭제)
	 *
	 * @param msgId
	 * @param params
	 * @return
	 */
	@Transactional
	public Map<String, Object> deleteProjectApi(String msgId, Map<String, Object> params) throws IOException {
		Map<String, Object> result = new HashMap<>();

		if (org.apache.commons.collections.MapUtils.isEmpty(params)) {
			throw new BizException("params is empty");
		}

		if ("GACA1006".equals(msgId)) {
			log.info("API 연동 params : {}", params);
			// PGAIA에 게시글 삭제
			List<String> informationList = objectMapper.convertValue(params.get("informationList"),new TypeReference<List<String>>() {});
			String usrId = (String) params.get("usrId");

			informationList.forEach(id1 -> {
				CnProject project = informationService.getProject(id1);
				if (project == null) {
					log.info("삭제 대상 프로젝트가 존재하지 않음: {}", id1);
					return;
				}

				InformationMybatisParam.InformationDeleteInput deleteInput = new InformationMybatisParam.InformationDeleteInput();
				deleteInput.setPjtNo(id1);
				deleteInput.setUsrId(usrId);

				//프로젝트 삭제
				informationService.deleteProject(deleteInput);

				// 20250822 PGAIA-GAIA 연동 사업생성시 부서 생성 임시 주석처리 jhkim
				// runProcedure(project, "DEL");
			});
			//계약삭제
			contractStatusComponent.deleteAllContract(informationList,usrId);
		}

		result.put("resultCode", "00");

		return result;
	}

	private MultipartFile extractMultipartFile(Object fileObj) {
		return (fileObj instanceof MultipartFile) ? (MultipartFile) fileObj : null;
	}
}
