package kr.co.ideait.platform.gaiacairos.comp.project;

import com.google.common.collect.Maps;
import kr.co.ideait.iframework.BizException;
import kr.co.ideait.platform.gaiacairos.comp.project.service.ProjectService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractComponent;
import kr.co.ideait.platform.gaiacairos.core.constant.CommonCodeConstants;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnAttachments;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnProjectInstall;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.ProjectForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.ProjectMybatisParam;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.type.FileUploadType;
import kr.co.ideait.platform.gaiacairos.core.type.PlatformType;
import kr.co.ideait.platform.gaiacairos.core.util.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProjectComponent extends AbstractComponent {

	@Autowired
	ProjectService projectService;

	@Autowired
	ProjectForm projectForm;

	@Autowired
	FileService fileService;

	// 현장개설요청 목록 조회
	public List<ProjectMybatisParam.ProjectInstallOutput> getProjectInstallList() {

		ProjectMybatisParam.ProjectInstallInput input = new ProjectMybatisParam.ProjectInstallInput();
		input.setOpenPstatsGroupCode(CommonCodeConstants.OPEN_PSTATS_GROUP_CODE);
		input.setPlatformType(platform.toUpperCase().substring(0, 1));

		return projectService.getProjectInstallList(input);
	}

	// 현장개설요청 상세 조회
	@Transactional
	public Map<String,Object> getProjectInstall(String pjtNo) {

		Map<String,Object> result = new HashMap<>();

		CnProjectInstall projectInstall = projectService.getProjectInstall(pjtNo);

		result.put("projectInstall",projectInstall);
		if (projectInstall.getAtchFileNo() != null) {
			result.put("attachments", projectService.getFileList(projectInstall.getAtchFileNo()));
		}

		return result;
	}


	// 현장개설요청 추가
	@Transactional
	public void createProject(ProjectForm.ProjectInstall project, List<MultipartFile> files, String apiYn) {

		CnProjectInstall cnProjectInstall = projectForm.toProjectInstall(project);

		// pjtNo 생성
		if(cnProjectInstall.getPlcReqNo() == null) {
			LocalDate now = LocalDate.now();
			String year = now.format(DateTimeFormatter.ofPattern("yyyy"));
			String month = now.format(DateTimeFormatter.ofPattern("MM"));
			String yearMonth = year + month;
			String plcReqType = platform.toUpperCase().substring(0, 1);
			Optional<Integer> maxSerialNumberOpt = projectService.maxSerialNumber(yearMonth,plcReqType);
			int newSerialNumber = maxSerialNumberOpt.map(max -> max + 1).orElse(1);

			if ( PlatformType.PGAIA.getName().equals(platform) ) {
				cnProjectInstall.setPltReqType("P");
				cnProjectInstall.setPlcReqNo(String.format("PR%s%s%03d", year, month, newSerialNumber));
			}else{
				cnProjectInstall.setPltReqType("G");
				cnProjectInstall.setPlcReqNo(String.format("GR%s%s%03d", year, month, newSerialNumber));
			}
		}
		cnProjectInstall.setDltYn("N");
		cnProjectInstall.setOpenPstats("0701");

		if (files != null && !files.isEmpty()) {
			Integer fileNo = saveCnAttachmentsList(files,null,UserAuth.get(true).getUsrId());
			cnProjectInstall.setAtchFileNo(fileNo);
		}

		projectService.saveProject(cnProjectInstall);

		// PGAIA일 경우만 API 통신
			if ( PlatformType.PGAIA.getName().equals(platform) ) {

				Map<String, Object> invokeParams = Maps.newHashMap();

				invokeParams.put("cnProjectInstall", cnProjectInstall);
				invokeParams.put("usrId", UserAuth.get(true).getUsrId());

				Map<String, Object> fileMap = Maps.newHashMap();

				fileMap.put("files", files);

				Map response;

				if(files != null && !files.isEmpty()) {
					response = invokePgaia2Cairos("GACA1001", invokeParams, fileMap);
				}else{
					response = invokePgaia2Cairos("GACA1001", invokeParams);
				}

				if (!"00".equals( MapUtils.getString(response, "resultCode") ) ) {
					throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(response, "resultMsg"));
				}
			}
	}

	// 현장개설요청 수정
	@Transactional
	public CnProjectInstall updateProject( String pjtNo, ProjectForm.ProjectInstall project, List<MultipartFile> newFiles,
							   List<Integer> removedFileNos, List<Integer> removedSnos, String apiYn) {
		CnProjectInstall cnProjectInstall = projectService.getProjectInstall(pjtNo);

		// 1. 현장 개설 정보 업데이트
		projectForm.updateProject(project, cnProjectInstall);

		// 2. 파일 삭제 처리
		if (removedFileNos != null && removedSnos != null) {
			for (int i = 0; i < removedSnos.size(); i++) {
				projectService.deleteAttachment(removedFileNos.get(i), removedSnos.get(i),null);
			}
		}

		// 3. 새 파일 추가 처리
		if (newFiles != null && !newFiles.isEmpty()) {
			Integer atchFileNo = cnProjectInstall.getAtchFileNo();
			Integer fileNo = saveCnAttachmentsList(newFiles,atchFileNo,UserAuth.get(true).getUsrId());
			cnProjectInstall.setAtchFileNo(fileNo);
		}

		projectService.saveProject(cnProjectInstall);

		// PGAIA일 경우만 API 통신
			if (PlatformType.PGAIA.getName().equals(platform)) {

				Map<String, Object> invokeParams = Maps.newHashMap();

				invokeParams.put("cnProjectInstall", cnProjectInstall);
				invokeParams.put("removedFileNos", removedFileNos);
				invokeParams.put("removedSnos", removedSnos);
				invokeParams.put("usrId", UserAuth.get(true).getUsrId());

				Map<String, Object> fileMap = Maps.newHashMap();

				fileMap.put("files", newFiles);

				Map response;

				if (newFiles != null && !newFiles.isEmpty()) {
					response = invokePgaia2Cairos("GACA1002", invokeParams, fileMap);
				} else {
					response = invokePgaia2Cairos("GACA1002", invokeParams);
				}


				if (!"00".equals(MapUtils.getString(response, "resultCode"))) {
					throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(response, "resultMsg"));
				}
			}

		return cnProjectInstall;
	}


	// 현장개설요청 삭제
	@Transactional
	public void deleteProject( String plcReqNo, String apiYn) {

		projectService.deleteProject(plcReqNo,UserAuth.get(true).getUsrId());

		// PGAIA일 경우만 API 통신
			if (PlatformType.PGAIA.getName().equals(platform)) {

				Map<String, Object> invokeParams = Maps.newHashMap();

				invokeParams.put("usrId", UserAuth.get(true).getUsrId());
				invokeParams.put("plcReqNo", plcReqNo);

				Map response = invokePgaia2Cairos("GACA1003", invokeParams);

				if (!"00".equals(MapUtils.getString(response, "resultCode"))) {
					throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(response, "resultMsg"));
				}
			}
	}

	// 첨부파일 저장
	@Transactional
	public Integer saveCnAttachmentsList( List<MultipartFile> files, Integer preFileNo, String usrId) {
		Integer fileNo = preFileNo != null ? preFileNo : projectService.generateFileNo();
		Integer sno = projectService.generateSNo(fileNo);

		List<CnAttachments> cnAttachmentsList = new ArrayList<>();

		if (files != null && !files.isEmpty()) {
			for (MultipartFile file : files) {

				if (file == null || file.isEmpty()) {
					continue;
				}

				FileService.FileMeta fileMeta = fileService.save(getUploadPathByWorkType(FileUploadType.PERSONAL, usrId), file);

				CnAttachments cnAttachments = new CnAttachments();
				cnAttachments.setFileNo(fileNo);
				cnAttachments.setSno(sno++); // 순차적 sno 설정

				cnAttachments.setFileNm(file.getOriginalFilename());
				cnAttachments.setFileDiskNm(fileMeta.getFileName());
				cnAttachments.setFileDiskPath(fileMeta.getDirPath());
				cnAttachments.setFileSize(fileMeta.getSize());
				cnAttachments.setDltYn("N"); // DB 기본값이면 생략 가능
				cnAttachments.setFileHitNum(0); // DB 기본값이면 생략 가능
				cnAttachments.setRgstrId(usrId);
				cnAttachments.setChgId(usrId);
				cnAttachmentsList.add(cnAttachments);
			}
		}

		// 저장할 항목이 없으면 fileNo 반환 (또는 null 반환을 선택할 수 있음)
		if (!cnAttachmentsList.isEmpty()) {
			for (CnAttachments cnAttachment : cnAttachmentsList) {
				projectService.saveCnAttachmentsList(cnAttachment);
			}
		}

		return fileNo;
	}

	// ----------------------------------------API통신--------------------------------------------

	/**
	 * 현장개설요청 추가 API 통신
	 *
	 * @param msgId
	 * @param params
	 * @return
	 */
	@Transactional
	public Map<String, Object> insertProjectInstallApi(String msgId, Map<String, Object> params) {
		Map<String, Object> result = new HashMap<>();

		if (org.apache.commons.collections.MapUtils.isEmpty(params)) {
			throw new BizException("params is empty");
		}

		if ("GACA1001".equals(msgId)) {
			log.info("API 연동 params : {}", params);
			// PGAIA에 게시글 추가
			CnProjectInstall cnProjectInstall = objectMapper.convertValue(params.get("cnProjectInstall"), CnProjectInstall.class);
			String usrId = (String) params.get("usrId");

			if(params.get("files") != null) {
				List<MultipartFile> fileObj = (List<MultipartFile>) params.get("files");

                Integer fileNo = saveCnAttachmentsList(fileObj,null,usrId);
				cnProjectInstall.setAtchFileNo(fileNo);
			}

			cnProjectInstall.setRgstrId(usrId);
			cnProjectInstall.setChgId(usrId);
			projectService.saveProject(cnProjectInstall);
		}

		result.put("resultCode", "00");

		return result;
	}

	/**
	 * 현장개설요청 수정 API 통신
	 *
	 * @param msgId
	 * @param params
	 * @return
	 */
	@Transactional
	public Map<String, Object> updateProjectInstallApi(String msgId, Map<String, Object> params) {
		Map<String, Object> result = new HashMap<>();

		if (org.apache.commons.collections.MapUtils.isEmpty(params)) {
			throw new BizException("params is empty");
		}

		if ("GACA1002".equals(msgId)) {
			log.info("API 연동 params : {}", params);

			// 1. 현장개설요청 수정
			CnProjectInstall cnProjectInstall = objectMapper.convertValue(params.get("cnProjectInstall"), CnProjectInstall.class);
			CnProjectInstall preCnProjectInstall = projectService.getProjectInstall(cnProjectInstall.getPlcReqNo());;
			List<Integer> removedSnos = (List<Integer>) params.get("removedSnos");
			String usrId = (String) params.get("usrId");

			// 1. 이전 현장 개설 정보
			Integer preFileNo = preCnProjectInstall.getAtchFileNo();
			if (preFileNo != null) {
				cnProjectInstall.setAtchFileNo(preFileNo);
			}

			// 2. 파일 삭제 처리
			if (preFileNo != null && removedSnos != null) {
				for (int i = 0; i < removedSnos.size(); i++) {
					projectService.deleteAttachment(preFileNo, removedSnos.get(i),usrId);
				}
			}

			// 3. 새 파일 추가 처리
			if(params.get("files") != null) {
				List<MultipartFile> fileObj = (List<MultipartFile>) params.get("files");
                Integer fileNo = saveCnAttachmentsList(fileObj,preFileNo,usrId);
				cnProjectInstall.setAtchFileNo(fileNo);
			}

			projectService.saveProject(cnProjectInstall);
		}

		result.put("resultCode", "00");

		return result;
	}

	/**
	 * 현장개설요청 삭제 API 통신
	 *
	 * @param msgId
	 * @param params
	 * @return
	 */
	@Transactional
	public Map<String, Object> deleteProjectInstallApi(String msgId, Map<String, Object> params) {
		Map<String, Object> result = new HashMap<>();

		if (org.apache.commons.collections.MapUtils.isEmpty(params)) {
			throw new BizException("params is empty");
		}

		if ("GACA1003".equals(msgId)) {
			log.info("API 연동 params : {}", params);

			String plcReqNo = (String) params.get("plcReqNo");
			String usrId = (String) params.get("usrId");

			projectService.deleteProject(plcReqNo,usrId);

		}

		result.put("resultCode", "00");

		return result;
	}


}
