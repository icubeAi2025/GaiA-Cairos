package kr.co.ideait.platform.gaiacairos.comp.design;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Maps;
import jakarta.validation.Valid;
import kr.co.ideait.platform.gaiacairos.comp.design.service.*;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractComponent;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.*;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.tool.designreview.DesignReviewForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.tool.designreview.DesignReviewMybatisParam;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.tool.responses.DesignResponsesForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.tool.responses.DesignResponsesMybatisParam;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.tool.setting.DesignSettingMybatisParam;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.type.FileUploadType;
import kr.co.ideait.platform.gaiacairos.core.type.PlatformType;
import kr.co.ideait.platform.gaiacairos.core.util.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class DesignResponsesComponent extends AbstractComponent {

	@Autowired
	FileService fileService;

	@Autowired
	DesignResponsesForm responsesForm;

	@Autowired
	DesignReviewForm designReviewForm;

	@Autowired
	DesignReviewService reviewService;

	@Autowired
	DesignResponsesService responsesService;

	@Autowired
	DesignSettingService settingService;

	@Autowired
	AttachmentsService attachmentsService;

	@Autowired
	DwgService dwgService;
	/**
	 * 설계 검토 관리 - 설계 목록 조회
	 */
	public Page<DesignReviewMybatisParam.DesignReviewListOutput> getDsgnListDataToGrid(DesignReviewForm.@Valid DesignReviewListGet designReviewListGet, String langInfo, String usrId) {
		DesignReviewMybatisParam.DsgnSearchInput searchInput = designReviewForm.toDsgnSearchInput(designReviewListGet);

		if ("my".equals(searchInput.getRgstr())) {
			searchInput.setRgstr(usrId);
		}

		MybatisInput input = MybatisInput.of().add("cntrctNo", designReviewListGet.getCntrctNo())
				.add("dsgnPhaseNo", designReviewListGet.getDsgnPhaseNo())
				.add("lang", langInfo)
				.add("pageable", designReviewListGet.getPageable())
				.add("searchInput", searchInput)
				.add("usrId", usrId);
		input.setPageable(designReviewListGet.getPageable());

		return reviewService.getDsgnListToGrid(input);
	}

	/**
	 * 답변 - 입력창 기본데이터
	 */
	public HashMap<String, Object> getDesignResponsesData(DesignResponsesMybatisParam.DesignResponsesInput responsesInput, DesignSettingMybatisParam.DesignPhaseDetailInput phaseInput) {
		HashMap<String,Object> result = new HashMap<>();
		DmResponse response = responsesService.getDesignResponses(responsesInput);

		List<DmAttachments> files = Collections.emptyList(); // 기본값
		List<DmAttachments> dwgFiles = Collections.emptyList(); // 도면 기본값
		DmDwg dmDwg = null;

		if (response != null && response.getAtchFileNo() != null) {
			files = attachmentsService.selectAttachmentsOfFileNo(response.getAtchFileNo());
			files = files.stream().filter(item -> "N".equals(item.getDltYn())).collect(Collectors.toList());
		}
		if (response != null && response.getDwgNo() != null) {
			dmDwg = dwgService.selectDmDwg(response.getDwgNo());
			if("Y".equals(dmDwg.getDltYn())){
				dmDwg = null;
			}
			if(dmDwg != null){
				dwgFiles = attachmentsService.selectAttachmentsOfFileNo(dmDwg.getAtchFileNo());
			}
		}

		result.put("phase",settingService.selectDesignPhase(phaseInput));
		result.put("response",response != null ? response : Collections.emptyMap());
		result.put("files",files);
		result.put("dwgFiles",dwgFiles);
		result.put("dwg",dmDwg);
		return result;
	}

	/**
	 * 답변 저장
	 * @param inputData
	 * @param files
	 * @param dwgFile
	 * @return
	 */
	@Transactional
	public Map<String, Object> saveResponses(DesignResponsesForm.DesignResponsesSave inputData, List<MultipartFile> files, Map<String,Object> dwgFile, CommonReqVo commonReqVo) throws JsonProcessingException {
		String resSeq = inputData.getResSeq();
		String saveType = StringUtils.isEmpty(resSeq) ? "create" : "update";

		String cntrctNo = inputData.getCntrctNo();
//		String dsgnNo = inputData.getDsgnNo();
		String userId = commonReqVo.getUserId();

		String savePath = String.format("%s/%s",uploadPath, getUploadPathByWorkType(FileUploadType.DESIGN,cntrctNo));

		DmResponse response = responsesForm.toDmResponse(inputData);
		List<DmAttachments> dwgAttachmentList = new ArrayList<>();
		DmDwg savedDwg = null;
		List<DmAttachments> responseAttachmentList = null;
		DmResponse savedResponse = null;
		
		FileService.FileMeta oldDwgMeta = null;
		FileService.FileMeta newDwgMeta = null;

		//새로운 추가인 경우
		if("create".equals(saveType)){
			//도면 파일 있다면
			if(dwgFile != null && !dwgFile.isEmpty()) {
				String dwgMapString = objectMapper.writeValueAsString(dwgFile);
				oldDwgMeta = objectMapper.readValue(dwgMapString, FileService.FileMeta.class);
				newDwgMeta = fileService.build(dwgMapString, savePath);

				//도면 파일 데이터 저장
				DmAttachments createdDmAttachment = attachmentsService.createDmAttachment(newDwgMeta, userId);
				dwgAttachmentList.add(createdDmAttachment);

				//도면 데이터 저장
				savedDwg = dwgService.createdmDwg("0503", inputData.getDwgDscrpt(), createdDmAttachment, userId);

				response.setDwgNo(savedDwg.getDwgNo());
			}
			//첨부 파일 있다면
			if(files != null && !files.isEmpty()){
				//첨부된 파일 물리 파일들 저장 및 파일 데이터 저장
				responseAttachmentList = attachmentsService.insertDmAttachments(files,cntrctNo,null);
				String atchFileNo = responseAttachmentList.get(0).getFileNo();

				response.setAtchFileNo(atchFileNo);
			}
		}
		//기존 것 수정인 경우
		else{
			String fileNo = response.getAtchFileNo();
			fileNo = fileNo == null?UUID.randomUUID().toString():fileNo;
			//도면 파일 있다면
			if(dwgFile != null && !dwgFile.isEmpty()){
				String dwgNo = inputData.getDwgNo();

				String dwgMapString = objectMapper.writeValueAsString(dwgFile);
				oldDwgMeta = objectMapper.readValue(dwgMapString, FileService.FileMeta.class);
				newDwgMeta = fileService.build(dwgMapString,savePath);

				if(!StringUtils.isEmpty(dwgNo)){
					//기존에 있었고 수정한 경우

					//삭제 먼저
					DmDwg oldDwg = dwgService.selectDmDwg(dwgNo);
					DmAttachments deleteDmAttachment = new DmAttachments();
					deleteDmAttachment.setFileNo(oldDwg.getAtchFileNo());
					deleteDmAttachment.setFileKey(oldDwg.getFileKey());
					deleteDmAttachment.setDltId(userId);
					attachmentsService.deleteDmAttachment(deleteDmAttachment);
					dwgService.deleteDmDwg(dwgNo,userId);
				}

				//도면 파일 데이터 저장
				DmAttachments createdDmAttachment = attachmentsService.createDmAttachment(newDwgMeta,userId);
				dwgAttachmentList.add(createdDmAttachment);

				//도면 데이터 저장
				savedDwg = dwgService.createdmDwg("0503",inputData.getDwgDscrpt(),createdDmAttachment,userId);

				response.setDwgNo(savedDwg.getDwgNo());
			}
			//도면 파일 없다면
			else{
				String dwgNo = inputData.getDwgNo();
				String deleteDwgNo = inputData.getDeleteDwgNo();
				//기존에 있었다가 없어진 경우(dwg 삭제)
				if(!StringUtils.isEmpty(deleteDwgNo)){
					//삭제만 진행
					DmDwg oldDwg = dwgService.selectDmDwg(dwgNo);
					DmAttachments deleteDmAttachment = new DmAttachments();
					deleteDmAttachment.setFileNo(oldDwg.getAtchFileNo());
					deleteDmAttachment.setFileKey(oldDwg.getFileKey());
					deleteDmAttachment.setDltId(userId);
					attachmentsService.deleteDmAttachment(deleteDmAttachment);
					dwgService.deleteDmDwg(dwgNo,userId);
					response.setDwgNo(null);
				}
				//없어지진 않은 경우(dwg 수정)
				else{
					DmDwg updateDwg = new DmDwg();
					updateDwg.setDwgNo(dwgNo);
					updateDwg.setDwgDscrpt(inputData.getDwgDscrpt());
					dwgService.updateDmDwg(updateDwg,userId);
				}
			}
			//삭제된 파일 있다면
			List<DmAttachments> removedFiles = inputData.getRemovedFiles();
			if(removedFiles != null && !removedFiles.isEmpty()){
				attachmentsService.deleteDmAttachments(removedFiles,userId);
			}

			//첨부 파일 있다면
			if(files != null && !files.isEmpty()){
				//첨부된 파일 물리 파일들 저장 및 파일 데이터 저장

				Map<String,Object> fileNoMap = new HashMap<>();
				fileNoMap.put("fileNo",fileNo);
				responseAttachmentList = attachmentsService.insertDmAttachments(files,cntrctNo,fileNoMap);
				response.setAtchFileNo(fileNo);
			}
		}
		//답변 데이터 저장
		savedResponse = responsesService.saveResponses(response,saveType,userId);

		Map result = new HashMap();

		result.put("response",savedResponse);
		result.put("saveType",saveType);

//		if ( PlatformType.CAIROS.getName().equals(platform) ) {
		if ( PlatformType.CAIROS.getName().equals(platform) && "P".equals(commonReqVo.getPjtDiv()) && "Y".equals(commonReqVo.getApiYn()) ) {
			Map<String, Object> params = new HashMap<>();
			params.put("userId", UserAuth.get(true).getUsrId());
			params.put("responses", inputData);
			params.put("savedResponse", savedResponse);
			params.put("dwgAttachmentList", dwgAttachmentList);
			params.put("responseAttachmentList", responseAttachmentList);
			params.put("savedDwg",savedDwg);
			params.put("saveType",saveType);

			Map<String, Object> fileMap = new HashMap<>();
			fileMap.put("files", files);
			fileMap.put("dwgFile", dwgFile);

			Map resp = invokeCairos2Pgaia("receiveBySaveResponses", params, fileMap);

			if (!"00".equals( MapUtils.getString(resp, "resultCode") ) ) {
				throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(resp, "resultMsg"));
			}
		}
		if(oldDwgMeta != null && newDwgMeta != null){
			fileService.moveFile(oldDwgMeta.getFilePath(),newDwgMeta.getFilePath());
		}
		return result;
	}
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Map receiveBySaveResponses(String transactionId, Map params) {
		if (!"receiveBySaveResponses".equals(transactionId)) {
			throw new GaiaBizException(ErrorType.INTERFACE, "유효하지 않은 거래ID 입니다.");
		}

		String userId = (String) params.get("userId");
		DesignResponsesForm.DesignResponsesSave responses = objectMapper.convertValue(params.get("responses"), DesignResponsesForm.DesignResponsesSave.class);
		DmResponse savedResponse = objectMapper.convertValue(params.get("savedResponse"), DmResponse.class);
		List<DmAttachments> dwgAttachmentList = objectMapper.convertValue(params.get("dwgAttachmentList"), new TypeReference<List<DmAttachments>>() {});
		List<DmAttachments> responseAttachmentList = objectMapper.convertValue(params.get("responseAttachmentList"), new TypeReference<List<DmAttachments>>() {});
		DmDwg savedDwg = objectMapper.convertValue(params.get("savedDwg"), DmDwg.class);
		String saveType = (String) params.get("saveType");

		List<MultipartFile> files = (List<MultipartFile>)params.get("files");
		List<MultipartFile> dwgFile = (List<MultipartFile>)params.get("dwgFile");

		Map<String, Object> result = Maps.newHashMap();
		result.put("resultCode", "01");

		String cntrctNo = responses.getCntrctNo();
		String savePath = getUploadPathByWorkType(FileUploadType.DESIGN,cntrctNo);
		//새로운 추가인 경우
		if("create".equals(saveType)){
			//도면 파일 있다면
			for(int i=0;i<dwgFile.size(); i++){
				MultipartFile file = dwgFile.get(i);
				//도면 물리 파일 저장
				fileService.save(savePath,file,dwgAttachmentList.get(i).getFileDiskNm());
				//도면 파일 데이터 저장
				attachmentsService.insertDmAttachment(dwgAttachmentList.get(i));

				//도면 데이터 저장
				dwgService.insertDmDwg(savedDwg,userId);
			}

			//첨부 파일 있다면
			if(files != null && !files.isEmpty()){
				//첨부된 파일 물리 파일들 저장 및 파일 데이터 저장
				for(int i=0;i<files.size();i++){
					MultipartFile file = files.get(i);
					fileService.save(savePath,file,responseAttachmentList.get(i).getFileDiskNm());
					attachmentsService.insertDmAttachment(responseAttachmentList.get(i));
				}
			}

			//답변 데이터 저장
			responsesService.saveResponses(savedResponse,saveType,userId);
		}
		//기존 것 수정인 경우
		else{
			//도면 파일 있다면
			if(dwgFile != null && !dwgFile.isEmpty()){
				String dwgNo = responses.getDwgNo();

				//기존에 없었다가 생긴 경우
				if(StringUtils.isEmpty(dwgNo)){
					for(int i=0;i<dwgFile.size();i++){
						MultipartFile file = dwgFile.get(i);
						//도면 물리 파일 저장
						fileService.save(savePath,file,dwgAttachmentList.get(i).getFileDiskNm());
						//도면 파일 데이터 저장
						DmAttachments createdDmAttachment = attachmentsService.insertDmAttachment(dwgAttachmentList.get(i));

						//도면 데이터 저장
						dwgService.insertDmDwg(savedDwg,userId);
					}
				}
				//기존에 있었고 수정한 경우
				else{
					//삭제 먼저
					DmDwg oldDwg = dwgService.selectDmDwg(dwgNo);
					DmAttachments deleteDmAttachment = new DmAttachments();
					deleteDmAttachment.setFileNo(oldDwg.getAtchFileNo());
					deleteDmAttachment.setFileKey(oldDwg.getFileKey());
					deleteDmAttachment.setDltId(userId);
					attachmentsService.deleteDmAttachment(deleteDmAttachment);
					dwgService.deleteDmDwg(dwgNo,userId);

					//그 다음 추가
					for(int i=0;i<dwgFile.size();i++){
						MultipartFile file = dwgFile.get(i);
						//도면 물리 파일 저장
						fileService.save(savePath,file,dwgAttachmentList.get(i).getFileDiskNm());
						//도면 파일 데이터 저장
						attachmentsService.insertDmAttachment(dwgAttachmentList.get(i));

						//도면 데이터 저장
						dwgService.insertDmDwg(savedDwg,userId);
					}
				}
			}
			//도면 파일 없다면
			else{
				String dwgNo = responses.getDwgNo();

				String deleteDwgNo = responses.getDeleteDwgNo();
				//기존에 있었다가 없어진 경우
				if(!StringUtils.isEmpty(deleteDwgNo)){
					//삭제만 진행
					DmDwg oldDwg = dwgService.selectDmDwg(dwgNo);
					DmAttachments deleteDmAttachment = new DmAttachments();
					deleteDmAttachment.setFileNo(oldDwg.getAtchFileNo());
					deleteDmAttachment.setFileKey(oldDwg.getFileKey());
					deleteDmAttachment.setDltId(userId);
					attachmentsService.deleteDmAttachment(deleteDmAttachment);
					dwgService.deleteDmDwg(dwgNo,userId);
				}
			}
			//삭제된 파일 있다면
			List<DmAttachments> removedFiles = responses.getRemovedFiles();
			if(removedFiles != null && !removedFiles.isEmpty()){
				attachmentsService.deleteDmAttachments(removedFiles,userId);
			}

			//첨부 파일 있다면
			if(files != null && !files.isEmpty()){
				//첨부된 파일 물리 파일들 저장 및 파일 데이터 저장
				for(int i=0;i<files.size();i++){
					MultipartFile file = files.get(i);
					fileService.save(savePath,file,responseAttachmentList.get(i).getFileDiskNm());
					attachmentsService.insertDmAttachment(responseAttachmentList.get(i));
				}
			}

			//답변 데이터 저장
			responsesService.saveResponses(savedResponse,saveType,userId);
		}

		result.put("resultCode","00");

		return result;
	}

	/**
	 * 답변 삭제
	 */
	@Transactional
	public void removeResponses(List<DmResponse> responsesList, CommonReqVo commonReqVo) {
		String userId = commonReqVo.getUserId();
		responsesList.forEach(response -> {
			if ( StringUtils.isEmpty(response.getResSeq()) ) {
				return;
			}

			DmResponse dmResponse = responsesService.getDesignResponse(response.getResSeq(),response.getDsgnNo());
			if (dmResponse == null) {
				return;
			}

			String atchFileNo = dmResponse.getAtchFileNo();
			if(!StringUtils.isEmpty(atchFileNo)){
				attachmentsService.deleteDmAttachmentsOfFileNo(atchFileNo,userId);
			}

			String dwgNo = dmResponse.getDwgNo();
			if(!StringUtils.isEmpty(dwgNo)){
				DmDwg dmDwg = dwgService.selectDmDwg(dwgNo);
				attachmentsService.deleteDmAttachmentsOfFileNo(dmDwg.getAtchFileNo(),userId);
				dwgService.deleteDmDwg(dwgNo,userId);
			}
		});
		responsesService.deleteResponses(responsesList,userId);

//		if ( PlatformType.CAIROS.getName().equals(platform) ) {
		if ( PlatformType.CAIROS.getName().equals(platform) && "P".equals(commonReqVo.getPjtDiv()) && "Y".equals(commonReqVo.getApiYn()) ) {
			Map<String, Object> params = new HashMap<>();
			params.put("userId", userId);
			params.put("responsesList", responsesList);

			Map<String, Object> fileMap = new HashMap<>();

			Map resp = invokeCairos2Pgaia("receiveByRemoveResponses", params, fileMap);

			if (!"00".equals( MapUtils.getString(resp, "resultCode") ) ) {
				throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(resp, "resultMsg"));
			}
		}
	}
	@Transactional
	public Map receiveByRemoveResponses(String transactionId, Map params) {
		if (!"receiveByRemoveResponses".equals(transactionId)) {
			throw new GaiaBizException(ErrorType.INTERFACE, "유효하지 않은 거래ID 입니다.");
		}

		List<DmResponse> responsesList = objectMapper.convertValue(params.get("responsesList"), new TypeReference<List<DmResponse>>() {});
		String userId = objectMapper.convertValue(params.get("userId"), String.class);

		responsesList.forEach(response -> {
			if ( StringUtils.isEmpty(response.getResSeq()) ) {
				return;
			}

			DmResponse dmResponse = responsesService.getDesignResponse(response.getResSeq(),response.getDsgnNo());
			if (dmResponse == null) {
				return;
			}

			String atchFileNo = dmResponse.getAtchFileNo();
			if(StringUtils.isEmpty(atchFileNo)){
				attachmentsService.deleteDmAttachmentsOfFileNo(atchFileNo,userId);
			}

			String dwgNo = dmResponse.getDwgNo();
			if(StringUtils.isEmpty(dwgNo)){
				DmDwg dmDwg = dwgService.selectDmDwg(dwgNo);
				attachmentsService.deleteDmAttachmentsOfFileNo(dmDwg.getAtchFileNo(),userId);
				dwgService.deleteDmDwg(dwgNo,userId);
			}
		});
		responsesService.deleteResponses(responsesList,userId);

		Map result = new HashMap();
		result.put("resultCode", "00");

		return result;
	}
}
