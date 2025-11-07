package kr.co.ideait.platform.gaiacairos.comp.design.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Maps;
import kr.co.ideait.iframework.BizException;
import kr.co.ideait.platform.gaiacairos.comp.design.helper.DesignHelper;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DmAttachments;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DmDesignReview;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DmDwg;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DmEvaluation;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.DmAttachmentsRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.DmDesignReviewRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.DmDwgRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.DmEvaluationRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisOutput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.tool.evaluation.EvaluationMybatisParam.EvaluationDetailInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.tool.evaluation.EvaluationMybatisParam.EvaluationInsertInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.tool.evaluation.EvaluationMybatisParam.EvaluationListInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.type.FileUploadType;
import kr.co.ideait.platform.gaiacairos.core.util.FileService;
import kr.co.ideait.platform.gaiacairos.core.util.FileService.FileMeta;
import org.apache.commons.collections.ListUtils;
import org.apache.tika.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class EvaluationService extends AbstractGaiaCairosService {
	
	@Autowired
	FileService fileService;
	
	@Autowired
	DmEvaluationRepository dmEvaluationRepository;
	
	@Autowired
	DmDesignReviewRepository dmDesignReviewRepository;
	
	@Autowired
	DmAttachmentsRepository dmAttachmentsRepository;
	
	@Autowired
	DmDwgRepository dmDwgRepository;

	@Autowired
	DesignHelper designHelper;

	
	/**
	 * 설계 평가 관리 목록조회(결함, 답변, 첨부파일, 설계도서)
	 * @param evaluationListInput
	 * @param pageable
	 * @return
	 */
	public Page<MybatisOutput> selectEvaluationList(EvaluationListInput evaluationListInput, Pageable pageable) {
		evaluationListInput.setPageable(pageable);
		List<MybatisOutput> output = null;
		Long totalCount = null;
		output = mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.design.evaluation.selectEvaluationList", evaluationListInput);
		totalCount = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.design.evaluation.selectEvaluationListCount", evaluationListInput);
		
		if(!output.isEmpty()) {			
			output.forEach(item -> {
				// 결함 첨부파일
				if(item.get("dsgn_file_no") != null) {
					String dfccyFileNo = (String)item.get("dsgn_file_no");
					List<DmAttachments> dsgnFiles = dmAttachmentsRepository.findByFileNoAndDltYn(dfccyFileNo, "N");
					item.put("dsgnFiles", dsgnFiles);
				}
				
				// 답변 첨부파일
				if(item.get("rply_atch_file_no") != null) {
					String replyAtchFileNo = (String)item.get("rply_atch_file_no");
					List<DmAttachments> replyFiles = dmAttachmentsRepository.findByFileNoAndDltYn(replyAtchFileNo, "N");
					item.put("rplyFiles", replyFiles);
				}
				
				// 검토도서 조회 및 매핑
	            if (item.get("rvw_dwg_no") != null) {
	            	String rvwDwgNo = (String)item.get("rvw_dwg_no");

					DmDwg dmDwg = dmDwgRepository.findByDwgNoAndDltYn(rvwDwgNo, "N");

					if (dmDwg != null) {
						DmAttachments rvwDwgFile = dmAttachmentsRepository.findByFileNoAndSnoAndDltYn(dmDwg.getAtchFileNo(), dmDwg.getSno(), "N");
						item.put("rvwDwgFile", rvwDwgFile);
					}
	            }

	            // 변경요청도서 조회 및 매핑
	            if (item.get("chg_dwg_no") != null) {
	            	String chgDwgNo = (String)item.get("chg_dwg_no");

					DmDwg dmDwg = dmDwgRepository.findByDwgNoAndDltYn(chgDwgNo, "N");

					if (dmDwg != null) {
						DmAttachments chgDwgFile = dmAttachmentsRepository.findByFileNoAndSnoAndDltYn(dmDwg.getAtchFileNo(), dmDwg.getSno(), "N");
						item.put("chgDwgFile", chgDwgFile);
					}
	            }
	            
	            // 답변도서 조회 및 매핑
	            if (item.get("rply_dwg_no") != null) {
	            	String replyDwgNo = (String)item.get("rply_dwg_no");

					DmDwg dmDwg = dmDwgRepository.findByDwgNoAndDltYn(replyDwgNo, "N");

					if (dmDwg != null) {
						DmAttachments rplyDwgFile = dmAttachmentsRepository.findByFileNoAndSnoAndDltYn(dmDwg.getAtchFileNo(), dmDwg.getSno(), "N");
						item.put("rplyDwgFile", rplyDwgFile);
					}
	            }
			});
		}
		
		return new PageImpl<>(output, evaluationListInput.getPageable(), totalCount);
	}

	
	
	/**
	 * 검색 셀렉트 옵션 - 검토분류 조회
	 * @return
	 */
	public List<MybatisOutput> selectDsgnCd() {
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.design.evaluation.selectDsgnCd");
	}


	
	/**
	 * 평가 의견 상세조회 (등록된 모든 평가의견&첨부파일)
	 * @param evaluationDetailInput
	 * @return
	 */
	public List<MybatisOutput> selectEvaluationDetail(EvaluationDetailInput evaluationDetailInput) {
		List<MybatisOutput> output = mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.design.evaluation.selectEvaluationDetail", evaluationDetailInput);
		output.forEach(item -> {
			if(item.get("eva_seq") != null && item.get("atch_file_no") != null) {
				String atchFileNo = (String)item.get("atch_file_no");
				List<DmAttachments> fileList = dmAttachmentsRepository.findByFileNoAndDltYn(atchFileNo, "N");
				item.put("evaFiles", fileList);
			}
		});
		return output;
	}
	
	
	
	/**
	 * 평가 의견 추가 - 의견, 첨부파일
	 * @param evaluationInsertInput
	 * @param files
	 */
	public Map<String, Object> insertEvaluation(EvaluationInsertInput evaluationInsertInput, List<MultipartFile> files) {
		return insertEvaluation(evaluationInsertInput, files, null);
	}
	public Map<String, Object> insertEvaluation(EvaluationInsertInput evaluationInsertInput, List<MultipartFile> files, Map<String, Object> params) {
		if (params == null) {
			params = Maps.newHashMap();
		}

		String userId = (String)params.get("userId");

		if (userId == null) {
			userId = UserAuth.get(true).getUsrId();
		}

		DmEvaluation dmEvaluation = evaluationInsertInput.getDmEvaluation();
		
		// 1. 파일저장
		List<DmAttachments> attachmentsListParam = objectMapper.convertValue(params.get("dmAttachmentsList"), new TypeReference<List<DmAttachments>>() {});
	    List<DmAttachments> dmAttachmentsList = new ArrayList<>();
		String savePath = getUploadPathByWorkType(FileUploadType.DESIGN,dmEvaluation.getCntrctNo());
		boolean isApiParam = attachmentsListParam != null && attachmentsListParam.size() > 0;
		String fileNo = null;
	    if (files != null && !files.isEmpty()) {
	    	fileNo = UUID.randomUUID().toString();
			int i = 0;

            for (MultipartFile file : files) {
				if(isApiParam){
					DmAttachments apiAttachment = attachmentsListParam.get(i);
					fileService.save(savePath,file,apiAttachment.getFileDiskNm());
					mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.design.attachment.insertAttachment",apiAttachment);
				}
				else{
					FileMeta fileMeta = fileService.save(savePath, file);
					DmAttachments dmAttachments = new DmAttachments();

					dmAttachments.setFileKey(UUID.randomUUID().toString());
					dmAttachments.setFileNo(fileNo);

					short sno = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.design.designReview.selectMaxSno",dmAttachments);
					dmAttachments.setSno((short)(sno+1));
					dmAttachments.setFileNm(file.getOriginalFilename());
					dmAttachments.setFileDiskNm(fileMeta.getFileName());
					dmAttachments.setFileDiskPath(fileMeta.getDirPath());
					dmAttachments.setFileSize(fileMeta.getSize());
					dmAttachments.setDltYn("N"); // DB에서 기본값 세팅되면 코드 삭제
					dmAttachments.setFileHitNum((short)0); // DB에서 기본값 세팅되면 코드 삭제
					dmAttachments.setRgstrId(userId);
					dmAttachments.setChgId(userId);

					dmAttachmentsList.add(dmAttachments);

					mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.design.attachment.insertAttachment",dmAttachments);
				}
            }
            
	    }
	    
	    // 2. 확인 의견 저장
	    Short maxRgstOrder = dmEvaluationRepository.findByMaxRgstOrderByDsgnNo(dmEvaluation.getDsgnNo());
	    dmEvaluation.setRgstOrdr(++maxRgstOrder);
	    dmEvaluation.setDltYn("N");
	    dmEvaluation.setAtchFileNo(fileNo);

		if (StringUtils.isEmpty(dmEvaluation.getEvaSeq())) {
			dmEvaluation.setEvaSeq(UUID.randomUUID().toString());
		}

		dmEvaluation.setRgstrId(userId);
		dmEvaluation.setChgId(userId);

		mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.design.evaluation.insertDmEvaluation", dmEvaluation);

		Map<String, Object> result = Maps.newHashMap();
		result.put("dmEvaluation", dmEvaluation);
		result.put("dmAttachmentsList", dmAttachmentsList);

		return result;
	}
	
	
	
	/**
	 * 평가 의견 수정 - 의견, 첨부파일
	 * @param evaluationInsertInput
	 * @param files
	 */
	public Map<String, Object> updateEvaluation(EvaluationInsertInput evaluationInsertInput, List<MultipartFile> files) {
		return updateEvaluation(evaluationInsertInput, files, null);
	}
	public Map<String, Object> updateEvaluation(EvaluationInsertInput evaluationInsertInput, List<MultipartFile> files, Map<String, Object> params) {
		if (params == null) {
			params = new HashMap<>();
		}

		String userId = (String)params.get("userId");

		if (StringUtils.isEmpty(userId)) {
			userId = UserAuth.get(true).getUsrId();
		}

		final String finalUserId = userId;

		Map<String, Object> result = null;
		DmEvaluation dmEvaluation = evaluationInsertInput.getDmEvaluation();
		List<DmAttachments> delFileList = evaluationInsertInput.getDelFileList();

		//기존 평가 검색
		DmEvaluation findEva = dmEvaluationRepository.findByEvaSeqAndDltYn(dmEvaluation.getEvaSeq(), "N").orElse(null);
		if(findEva != null){
			String existingFileNo = findEva.getAtchFileNo();

			// 1. 기존 파일삭제
			if(!delFileList.isEmpty()) {
				delFileList.forEach(file -> {
					DmAttachments findFile = dmAttachmentsRepository.findByFileNoAndFileKey(file.getFileNo(), file.getFileKey());

					if (findFile != null) {
						if (finalUserId == null) {
							dmAttachmentsRepository.updateDelete(findFile);
						} else {
							dmAttachmentsRepository.updateDelete(findFile, finalUserId);
						}
					}
				});
			}

			// 2. 새 파일저장
			//API 통신으로 받은 리스트
			List<DmAttachments> attachmentsListParam = objectMapper.convertValue(params.get("dmAttachmentsList"), new TypeReference<List<DmAttachments>>() {});

			//로직을 위한 list
			List<DmAttachments> dmAttachmentsList = new ArrayList<>();

			String savePath = getUploadPathByWorkType(FileUploadType.DESIGN,findEva.getCntrctNo());
			if(files != null && !files.isEmpty()) {
				boolean isApiParam = attachmentsListParam != null && attachmentsListParam.size() > 0;
				int i = 0;
				existingFileNo = existingFileNo == null?UUID.randomUUID().toString():existingFileNo;
				dmEvaluation.setAtchFileNo(existingFileNo);

				for (MultipartFile file : files) {
					//
					if(isApiParam){
						DmAttachments apiAttachment = attachmentsListParam.get(i);
						fileService.save(savePath,file,apiAttachment.getFileDiskNm());
						mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.design.attachment.insertAttachment",apiAttachment);
					}
					else{
						FileMeta fileMeta = fileService.save(savePath, file);
						DmAttachments dmAttachments = new DmAttachments();

						dmAttachments.setFileKey(UUID.randomUUID().toString());
						dmAttachments.setFileNo(existingFileNo);

						short sno = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.design.designReview.selectMaxSno",dmAttachments);
						dmAttachments.setSno((short)(sno+1));
						dmAttachments.setFileNm(file.getOriginalFilename());
						dmAttachments.setFileDiskNm(fileMeta.getFileName());
						dmAttachments.setFileDiskPath(fileMeta.getDirPath());
						dmAttachments.setFileSize(fileMeta.getSize());
						dmAttachments.setDltYn("N"); // DB에서 기본값 세팅되면 코드 삭제
						dmAttachments.setFileHitNum((short)0); // DB에서 기본값 세팅되면 코드 삭제
						dmAttachments.setRgstrId(finalUserId);
						dmAttachments.setChgId(finalUserId);

						dmAttachmentsList.add(dmAttachments);

						mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.design.attachment.insertAttachment",dmAttachments);
					}
				}
			}


			// 3. 평가 의견 수정

			dmEvaluation.setChgId(finalUserId);
			mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.design.evaluation.updateDmEvaluation", dmEvaluation);
			result = Maps.newHashMap();
			result.put("dmEvaluation", dmEvaluation);
			result.put("dmAttachmentsList", dmAttachmentsList);
		}
		return result;
	}


	/**
	 * 평가 의견 삭제 - 의견, 첨부파일
	 * @param evaSeq
	 */
	public void deleteEvaluation(String evaSeq) {
		deleteEvaluation(evaSeq, null);
	}

	public void deleteEvaluation(String evaSeq, String userId) {
		DmEvaluation findEva = dmEvaluationRepository.findByEvaSeqAndDltYn(evaSeq, "N").orElse(null);

		if (findEva == null) {
			throw new BizException("평가 의견 없음.");
		}

		if (userId == null) {
			userId = UserAuth.get(true).getUsrId();
		}

		if (findEva.getAtchFileNo() != null) {
			deleteAtchFile(findEva.getAtchFileNo(), userId);
		}

		if (userId == null) {
			dmEvaluationRepository.updateDelete(findEva);
		} else {
			dmEvaluationRepository.updateDelete(findEva, userId);
		}
	}

	
	
	/**
	 * 평가 의견 전체 삭제 - 의견, 첨부파일
	 * @param delEvaList
	 */
	public void deleteAllEvaluation(List<DmEvaluation> delEvaList) {
		deleteAllEvaluation(delEvaList, null);
	}
	public void deleteAllEvaluation(List<DmEvaluation> delEvaList, String userId) {
		if (userId == null) {
			userId = UserAuth.get(true).getUsrId();
		}

		final String finalUserId = userId;

		delEvaList.forEach(eva -> {
			//TODO. 삭제 확인 필요
			DmEvaluation findEva = dmEvaluationRepository.findByDsgnNoAndRgstrIdAndDltYn(eva.getDsgnNo(), finalUserId, "N").orElse(null);

//			List<DmEvaluation> findList = dmEvaluationRepository.findByDsgnNoAndDltYn(eva.getDsgnNo(), "N").orElse(null);

			if (findEva != null) {
				if (findEva.getAtchFileNo() != null) {
					deleteAtchFile(findEva.getAtchFileNo());
				}

				if (finalUserId == null) {
					dmEvaluationRepository.updateDelete(findEva);
				} else {
					dmEvaluationRepository.updateDelete(findEva, finalUserId);
				}
			}
		});
	}

	
	
	/**
	 * 평가 첨부파일 삭제
	 * @param atchFileNo
	 */
	private void deleteAtchFile(String atchFileNo) {
		deleteAtchFile(atchFileNo, null);
	}
	private void deleteAtchFile(String atchFileNo, String userId) {
		List<DmAttachments> fileList = dmAttachmentsRepository.findByFileNoAndDltYn(atchFileNo, "N");
		fileList.forEach(file -> {
			if (userId == null) {
				dmAttachmentsRepository.updateDelete(file);
			} else {
				dmAttachmentsRepository.updateDelete(file, userId);
			}
		});
	}


	
	/**
	 * 평가자 결과 등록 (동의 / 동의안함)
	 * @param cntrctNo
	 * @param dsgnNo
	 * @param apprerCd
	 */
	public void updateApprer(String cntrctNo, String dsgnNo, String apprerCd) {
		updateApprer(cntrctNo, dsgnNo, apprerCd, null);
	}
	public void updateApprer(String cntrctNo, String dsgnNo, String apprerCd, String userId) {
		DmDesignReview findReview = dmDesignReviewRepository.findByCntrctNoAndDsgnNoAndDltYn(cntrctNo, dsgnNo, "N");
		findReview.setApprerCd(apprerCd);
		findReview.setApprerRgstDt(LocalDateTime.now());

		if (userId == null) {
			userId = UserAuth.get(true).getUsrId();
		}

		findReview.setApprerRgstrId(userId);

		dmDesignReviewRepository.save(findReview);
	}



	
}
