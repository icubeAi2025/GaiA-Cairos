package kr.co.ideait.platform.gaiacairos.comp.safety;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import kr.co.ideait.platform.gaiacairos.comp.safety.service.SafetyDiaryService;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwAttachments;
import kr.co.ideait.platform.gaiacairos.core.type.FileUploadType;
import kr.co.ideait.platform.gaiacairos.core.util.FileService;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import kr.co.ideait.platform.gaiacairos.comp.safety.service.EducationDiaryService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractComponent;
import kr.co.ideait.platform.gaiacairos.core.constant.CommonCodeConstants;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.safety.EducationDiaryForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.safety.EducationDiaryForm.EduDiaryPersonnelCrateForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.safety.EducationDiaryMybatisParam.EducationDiaryListInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.safety.EducationDiaryMybatisParam.EducationDiaryListOutput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class EducationDiaryComponent extends AbstractComponent {
	
	@Autowired
	EducationDiaryService educationDiaryService;

	@Autowired
	SafetyDiaryService safetyDiaryService;

	@Autowired
	FileService fileService;
	
	@Autowired
	private final EducationDiaryForm educationDiaryForm;
	
	/**
	 * 교육일지 목록조회
	 *
	 * @throws
	 */
	public Page<EducationDiaryListOutput> educationDiaryList(EducationDiaryForm.EduDiaryListForm eduDiaryListForm) {
		
		EducationDiaryListInput input = educationDiaryForm.toEducationDiaryListInput(eduDiaryListForm);
		
		List<EducationDiaryListOutput> educationDiaryList = educationDiaryService.selectEducationDiaryList(input);	
		
		long totalCount = educationDiaryList.size();
 		if (totalCount != 0) {
 			totalCount = educationDiaryList.get(0).getCnt();
 		}
		
		return new PageImpl<>(educationDiaryList, eduDiaryListForm.getPageable(), totalCount);
	}
	
	/**
	 * 교육일지 상세조회
	 *
	 * @throws
	 */
	public Map<String, Object> educationDiary(String eduId, String openType) {
		Map<String, Object> result = new HashMap<String, Object>();
		
		MybatisInput input = MybatisInput.of().add("eduId", eduId).add("cmnGrpCd", CommonCodeConstants.EDUCATION_CODE_GROUP_CODE);
		
		List<Map<String, Object>> educationDiaryInfo = educationDiaryService.selectEducationDiary(input);
		
		result.put("educationDiaryInfo", educationDiaryInfo);
		
		if(openType != null && openType.equals("true")) {
			List<Map<String, Object>> eduTypeList = educationDiaryService.selectEduTypeList(input);
			result.put("eduTypeList", eduTypeList);
		}

		// 첨부파일 목록
		if (educationDiaryInfo.getFirst().get(("atch_file_no")) != null) {
			Map<String, Object> param = new HashMap<>();
			param.put("fileNo", educationDiaryInfo.getFirst().get(("atch_file_no")));
			List <CwAttachments> fileList = safetyDiaryService.getSafetyDiaryAttachments(param);

			result.put("fileList", fileList);
		}
		
		return result;	
	}
	
	/**
	 * 교육일지 등록
	 *
	 * @throws
	 */
	@Transactional
	public void createEducationDiary(EducationDiaryForm.EduDiaryCrateForm eduDiaryCrateFormm) throws JsonProcessingException {
		
		String eduID = UUID.randomUUID().toString();
		String usrId = UserAuth.get(true).getUsrId();

		MybatisInput input = MybatisInput.of().add("eduId", eduID)
				.add("cntrctNo", eduDiaryCrateFormm.getCntrctNo())
				.add("eduDt", eduDiaryCrateFormm.getEduDt())
				.add("eduType", eduDiaryCrateFormm.getEduType())
				.add("eduRank", eduDiaryCrateFormm.getEduRank())
				.add("eduNm", eduDiaryCrateFormm.getEduNm())
				.add("eduSurvM", Integer.parseInt(eduDiaryCrateFormm.getEduSurvM()))
				.add("eduSurvF", Integer.parseInt(eduDiaryCrateFormm.getEduSurvF()))
				.add("eduSurvNote", eduDiaryCrateFormm.getEduSurvNote())
				.add("eduActiM", Integer.parseInt(eduDiaryCrateFormm.getEduActiM()))
				.add("eduActiF", Integer.parseInt(eduDiaryCrateFormm.getEduActiF()))
				.add("eduActiNote", eduDiaryCrateFormm.getEduActiNote())
				.add("eduNoActiM", Integer.parseInt(eduDiaryCrateFormm.getEduNoActiM()))
				.add("eduNoActiF", Integer.parseInt(eduDiaryCrateFormm.getEduNoActiF()))
				.add("eduNoActiNote", eduDiaryCrateFormm.getEduNoActiNote())
				.add("outline", eduDiaryCrateFormm.getOutline())
				.add("subject", eduDiaryCrateFormm.getSubject())
				.add("method", eduDiaryCrateFormm.getMethod())
				.add("time", eduDiaryCrateFormm.getTime())
				.add("textbook", eduDiaryCrateFormm.getTextbook())
				.add("location", eduDiaryCrateFormm.getLocation())
				.add("note", eduDiaryCrateFormm.getNote())
				.add("usrId", usrId);
		
		int resultCount = educationDiaryService.insertEducationDiaryInfo(input);
		
		log.info("교육일지 입력 결과는 {} 건입니다.", resultCount);
		
		List<EduDiaryPersonnelCrateForm> eduDiaryPersonnelList = eduDiaryCrateFormm.getEduDiaryPersonnelList();
		List<Map<String, Object>> insertEducationDiaryPersonnelList  = new ArrayList<Map<String, Object>>();
		
		if(resultCount > 0 && !ObjectUtils.isEmpty(eduDiaryPersonnelList)) {
			for(EducationDiaryForm.EduDiaryPersonnelCrateForm eduDiaryPersonnel : eduDiaryPersonnelList) {
				Map<String, Object> map = new HashMap<String, Object>();
				
				map.put("eduId", eduID);
				map.put("eduVicOccu", eduDiaryPersonnel.getEduVicOccu());
				map.put("eduVicNm", eduDiaryPersonnel.getEduVicNm());
				map.put("usrId", usrId);
				
				insertEducationDiaryPersonnelList.add(map);
			}

			educationDiaryService.insertEducationDiaryPersonnelInfo(insertEducationDiaryPersonnelList);
		}
		

		// 첨부파일로직
		List<FileService.FileMeta> fileList = eduDiaryCrateFormm.getFileList();
		if (!ObjectUtils.isEmpty(fileList)) {
			FileService.FileMeta newMeta = null;
			String savePath = String.format("%s/%s", uploadPath, getUploadPathByWorkType(FileUploadType.SAFETY, eduDiaryCrateFormm.getCntrctNo()));

			Integer sno = 1;
			Integer atchFileNo = educationDiaryService.selectEducationDiaryAttachmentMaxFileNo();
			
			MybatisInput fileInfoInput = MybatisInput.of().add("cntrctNo", eduDiaryCrateFormm.getCntrctNo())
					.add("eduId", eduID)
					.add("atchFileNo", atchFileNo);

			// 교육일지 첨부파일번호 업데이트
			educationDiaryService.updateEducationDiaryAttachmentFileNo(fileInfoInput);
			
			for (FileService.FileMeta meta : fileList) {

				String metaString = objectMapper.writeValueAsString(meta);
				newMeta = fileService.build(metaString, savePath);             // 실제 물리파일 처리
				FileService.FileMeta tempRvwPhotoFileMeta = objectMapper.readValue(metaString, FileService.FileMeta.class);
				
				CwAttachments cwFile = new CwAttachments();
				cwFile.setFileNo(atchFileNo);
				cwFile.setSno(sno);
				cwFile.setFileNm(newMeta.getOriginalFilename());
				cwFile.setFileDiskNm(newMeta.getFileName());
				cwFile.setFileDiskPath(newMeta.getDirPath());
				cwFile.setFileSize(newMeta.getSize());
				cwFile.setFileDiv("F");
				cwFile.setRgstrId(usrId);
				cwFile.setChgId(usrId);
				
				educationDiaryService.insertEducationDiaryAttachmentFile(cwFile);            // 메타 테이블 INSERT				
				fileService.moveFile(tempRvwPhotoFileMeta.getFilePath(), newMeta.getFilePath()); // Temp ->  리얼폴더로 이동
				
				sno += 1;
			}
		}
	}
	
	/**
	 * 교육일지수정
	 *
	 * @throws
	 */
	@Transactional
	public void updateEducationDiary(EducationDiaryForm.EduDiaryCrateForm eduDiaryCrateFormm) throws JsonProcessingException {

		String usrId = UserAuth.get(true).getUsrId();

		MybatisInput input = MybatisInput.of().add("eduId", eduDiaryCrateFormm.getEduId())
				.add("eduType", eduDiaryCrateFormm.getEduType())
				.add("eduRank", eduDiaryCrateFormm.getEduRank())
				.add("eduNm", eduDiaryCrateFormm.getEduNm())
				.add("eduSurvM", Integer.parseInt(eduDiaryCrateFormm.getEduSurvM()))
				.add("eduSurvF", Integer.parseInt(eduDiaryCrateFormm.getEduSurvF()))
				.add("eduSurvNote", eduDiaryCrateFormm.getEduSurvNote())
				.add("eduActiM", Integer.parseInt(eduDiaryCrateFormm.getEduActiM()))
				.add("eduActiF", Integer.parseInt(eduDiaryCrateFormm.getEduActiF()))
				.add("eduActiNote", eduDiaryCrateFormm.getEduActiNote())
				.add("eduNoActiM", Integer.parseInt(eduDiaryCrateFormm.getEduNoActiM()))
				.add("eduNoActiF", Integer.parseInt(eduDiaryCrateFormm.getEduNoActiF()))
				.add("eduNoActiNote", eduDiaryCrateFormm.getEduNoActiNote())
				.add("outline", eduDiaryCrateFormm.getOutline())
				.add("subject", eduDiaryCrateFormm.getSubject())
				.add("method", eduDiaryCrateFormm.getMethod())
				.add("time", eduDiaryCrateFormm.getTime())
				.add("textbook", eduDiaryCrateFormm.getTextbook())
				.add("location", eduDiaryCrateFormm.getLocation())
				.add("note", eduDiaryCrateFormm.getNote())
				.add("usrId", usrId);
		
		int resultCount = educationDiaryService.updapeEducationDiaryInfo(input);
		
		log.info("교육일지 입력 결과는 {} 건입니다.", resultCount);
		
		List<EduDiaryPersonnelCrateForm> eduDiaryPersonnelList = eduDiaryCrateFormm.getEduDiaryPersonnelList();
		List<Map<String, Object>> insertEducationDiaryPersonnelList  = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> updateEducationDiaryPersonnelList  = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> deleteEducationDiaryPersonnelList  = new ArrayList<Map<String, Object>>();
		
		if(resultCount > 0 && !ObjectUtils.isEmpty(eduDiaryPersonnelList)) {
			if(!ObjectUtils.isEmpty(eduDiaryPersonnelList)) {
				for(EducationDiaryForm.EduDiaryPersonnelCrateForm eduDiaryPersonnel : eduDiaryPersonnelList) {
					Map<String, Object> map = new HashMap<String, Object>();
					
					map.put("eduId", eduDiaryCrateFormm.getEduId());
					map.put("eduVicSeq", eduDiaryPersonnel.getEduVicSeq());
					map.put("eduVicOccu", eduDiaryPersonnel.getEduVicOccu());
					map.put("eduVicNm", eduDiaryPersonnel.getEduVicNm());
					map.put("usrId", UserAuth.get(true).getUsrId());
					
					if("I".equals(eduDiaryPersonnel.getActionType())) {
						insertEducationDiaryPersonnelList.add(map);
					}else if("U".equals(eduDiaryPersonnel.getActionType())) {
						updateEducationDiaryPersonnelList.add(map);
					}else if("D".equals(eduDiaryPersonnel.getActionType())) {
						deleteEducationDiaryPersonnelList.add(map);
					}		
				}
				
				//교육일지 수정 시 등록된 참석자 등록처리
				if(!ObjectUtils.isEmpty(insertEducationDiaryPersonnelList)) {
					educationDiaryService.insertEducationDiaryPersonnelInfo(insertEducationDiaryPersonnelList);
				}
				
				//교육일지 수정 시 수정된 참석자 수정처리
				if(!ObjectUtils.isEmpty(updateEducationDiaryPersonnelList)) {
					educationDiaryService.updateEducationDiaryPersonnelInfo(updateEducationDiaryPersonnelList);
				}
				
				//교육일지 수정 시 삭제된 참석자 삭제처리
				if(!ObjectUtils.isEmpty(deleteEducationDiaryPersonnelList)) {
					MybatisInput deleteEducationDiaryPersonnel = MybatisInput.of().add("eduId", eduDiaryCrateFormm.getEduId())
							.add("usrId", UserAuth.get(true).getUsrId())
							.add("eduVicSeqList", deleteEducationDiaryPersonnelList);
					
					
					educationDiaryService.deleteEducationDiaryPersonnelInfo(deleteEducationDiaryPersonnel);
				}
			}
		}	

		// 첨부파일 로직 시작
		List<FileService.FileMeta> fileList = eduDiaryCrateFormm.getFileList();
		if (!ObjectUtils.isEmpty(fileList)) {
			FileService.FileMeta newMeta = null;
			String savePath = String.format("%s/%s", uploadPath, getUploadPathByWorkType(FileUploadType.SAFETY, eduDiaryCrateFormm.getCntrctNo()));

			// 교육일지 - 첨부문서 존재 확인
			Integer atchFileNo = eduDiaryCrateFormm.getAtchFileNo();
			if (ObjectUtils.isEmpty(atchFileNo)) {
				// 기존 파일첨부번호가 없다면 신규 채번 + 업데이트처리
				atchFileNo = educationDiaryService.selectEducationDiaryAttachmentMaxFileNo();
				
				MybatisInput fileInfoInput = MybatisInput.of().add("cntrctNo", eduDiaryCrateFormm.getCntrctNo())
						.add("eduId", eduDiaryCrateFormm.getEduId())
						.add("atchFileNo", atchFileNo)
						.add("usrId", usrId);

				educationDiaryService.updateEducationDiaryAttachmentFileNo(fileInfoInput);
			}
			Integer sno = educationDiaryService.selectEducationDiaryAttachmentMaxSno(MybatisInput.of().add("fileNo", atchFileNo));

			for (FileService.FileMeta meta : fileList) {
				log.info("첨부파일 처리모드는 {}입니다", meta.getMode());
				if ("C".equals(meta.getMode())) {
					// 구분 "입력" 일경우 - INSERT
					String metaString = objectMapper.writeValueAsString(meta);
					newMeta = fileService.build(metaString, savePath);             // 실제 물리파일 처리
					FileService.FileMeta tempRvwPhotoFileMeta = objectMapper.readValue(metaString, FileService.FileMeta.class);

					CwAttachments cwFile = new CwAttachments();
					cwFile.setFileNo(atchFileNo);
					cwFile.setSno(sno);
					cwFile.setFileNm(newMeta.getOriginalFilename());
					cwFile.setFileDiskNm(newMeta.getFileName());
					cwFile.setFileDiskPath(newMeta.getDirPath());
					cwFile.setFileSize(newMeta.getSize());
					cwFile.setFileDiv("F");
					cwFile.setRgstrId(usrId);
					cwFile.setChgId(usrId);
					
					educationDiaryService.insertEducationDiaryAttachmentFile(cwFile);            // 메타 테이블 INSERT	
					fileService.moveFile(tempRvwPhotoFileMeta.getFilePath(), newMeta.getFilePath()); // Temp ->  리얼폴더로 이동
					
					log.info("첨부파일 추가 :{} / {}", atchFileNo, cwFile.getFileDiskNm());
				} else if ("D".equals(meta.getMode())) {

					// 구분 "삭제" 일경우 - 메타 테이블 DELETE
					MybatisInput fileInput = MybatisInput.of().add("fileName", meta.getFileName())
							.add("fileNo", atchFileNo)
							.add("usrId", usrId);

					educationDiaryService.deleteEducationDiaryAttachmentFile(fileInput);
					log.info("첨부파일 삭제 :{} / {}", atchFileNo, meta.getFileName());
				}

				sno += 1;

			}
		}
	}
	
	/**
	 * 교육일지 & 교육 참가자 삭제
	 *
	 * @throws
	 */
	@Transactional
	public int deleteEducationDiary(EducationDiaryForm.eduIdListForm eduIdList) {
		
		List<Map<String, Object>> eduDelList = new ArrayList<Map<String, Object>>();
        
        for(Map<String, Object> eduMap : eduIdList.getEduIdList()) {

        	Map<String, Object> map = new HashMap<String, Object>();
        	map.put("eduId", MapUtils.getString(eduMap, "eduId"));
        	map.put("usrId", UserAuth.get(true).getUsrId());

			// 교육일지 - 첨부파일 논리삭제
			if (eduMap.get("atchFileNo") != null) {
				map.put("fileNo", Integer.parseInt(MapUtils.getString(eduMap, "atchFileNo")));
				safetyDiaryService.deleteSafetyDiaryAttachmentsByFileNo(map);
			}

			// 2. 교육일지 - 삭제목록 ADD
        	eduDelList.add(map);
        }
        
        return educationDiaryService.deleteEducationDiaryInfo(eduDelList);
	}

}
