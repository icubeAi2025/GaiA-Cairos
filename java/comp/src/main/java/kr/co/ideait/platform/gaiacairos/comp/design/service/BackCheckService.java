package kr.co.ideait.platform.gaiacairos.comp.design.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Maps;
import kr.co.ideait.platform.gaiacairos.comp.design.helper.DesignHelper;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DmAttachments;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DmBackcheck;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DmDesignReview;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DmDwg;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.DmAttachmentsRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.DmBackcheckRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.DmDesignReviewRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.DmDwgRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisOutput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.tool.backcheck.BackCheckForm.BackCheckInsert;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.tool.backcheck.BackCheckMybatisParam.BackCheckListInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.type.FileUploadType;
import kr.co.ideait.platform.gaiacairos.core.util.FileService;
import kr.co.ideait.platform.gaiacairos.core.util.FileService.FileMeta;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class BackCheckService extends AbstractGaiaCairosService {
	
	@Autowired
	FileService fileService;
	
	@Autowired
	DmBackcheckRepository dmBackcheckRepository;
	
	@Autowired
	DmDesignReviewRepository dmDesignReviewRepository;
	
	@Autowired
	DmAttachmentsRepository dmAttachmentsRepository;
	
	@Autowired
	DmDwgRepository dmDwgRepository;

	@Autowired
	DesignHelper designHelper;
	
    /**
     * 백체크 목록조회 (결함, 답변, 평가, 첨부파일, 설계도서)
     * @param backCheckListInput
     * @param pageable
     * @return
     */
	public Page<MybatisOutput> selectBackCheckList(BackCheckListInput backCheckListInput, Pageable pageable) {
		backCheckListInput.setPageable(pageable);
		
		List<MybatisOutput> output = null;
		Long totalCount = null;
		output = mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.design.backCheck.selectBackCheckList", backCheckListInput);
		totalCount = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.design.backCheck.selectBackCheckListCount", backCheckListInput);
		
		if(!output.isEmpty()) {			
			output.forEach(item -> {
				if(item != null){
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

					// 평가
					MybatisInput input = MybatisInput.of().add("dsgnNo", item.get("dsgn_no")).add("cntrctNo", item.get("cntrct_no"));
					List<MybatisOutput> evaluationList = mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.design.backCheck.selectEvaluationByDsgnNoAndCntrctNo", input);

					if(!evaluationList.isEmpty()) {
						evaluationList.forEach(evaluation -> {
							// 평가 첨부파일
							if(evaluation.get("atch_file_no") != null) {
								List<DmAttachments> evaluationFile = dmAttachmentsRepository.findByFileNoAndDltYn((String)evaluation.get("atch_file_no"), "N");
								evaluation.put("evaluationFile", evaluationFile);
							}
						});
						item.put("evaluationList", evaluationList);
					}
				}
			});
		}
		
		return new PageImpl<>(output, backCheckListInput.getPageable(), totalCount);
	}
	
	
	/**
	 * 백체크 상세조회 (등록된 모든 백체크 의견&첨부파일)
	 * @param dsgnNo
	 * @param dsgnPhaseNo
	 * @return
	 */
	public List<MybatisOutput> selectBackCheckDetail(String dsgnNo, String dsgnPhaseNo) {
		MybatisInput input = MybatisInput.of().add("dsgnNo", dsgnNo)
												.add("dsgnPhaseNo", dsgnPhaseNo);
		List<MybatisOutput> output = mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.design.backCheck.selectBackCheckDetail", input);
		output.forEach(item -> {
			if(item.get("back_seq") != null && item.get("atch_file_no") != null) {
				String atchFileNo = (String)item.get("atch_file_no");
				List<DmAttachments> fileList = dmAttachmentsRepository.findByFileNoAndDltYn(atchFileNo, "N");
				item.put("backchkFiles", fileList);
			}
		});
		return output;
	}
	
	
	/**
	 * 백체크 추가 - 의견, 첨부파일
	 * @param backCheckInsert
	 * @param files
	 */
	public Map<String, Object> insertBackCheck(BackCheckInsert backCheckInsert, List<MultipartFile> files) {
		return insertBackCheck(backCheckInsert, files, null);
	}
	public Map<String, Object> insertBackCheck(BackCheckInsert backCheckInsert, List<MultipartFile> files, Map<String, Object> params) {
		if (params == null) {
			params = Maps.newHashMap();
		}

		String userId = (String)params.get("userId");
		if (StringUtils.isEmpty(userId)) {
			UserAuth userAuth = UserAuth.get(true);
			if(userAuth == null){
				throw new GaiaBizException(ErrorType.UNAUTHORIZED,"userAuth is null");
			}
			userId = userAuth.getUsrId();
		}

		DmBackcheck dmBackcheck = backCheckInsert.getDmBackcheck();

		// 1. 파일저장
		List<DmAttachments> attachmentsListParam = objectMapper.convertValue(params.get("dmAttachmentsList"), new TypeReference<List<DmAttachments>>() {});
	    List<DmAttachments> dmAttachmentsList = new ArrayList<>();

		String savePath = getUploadPathByWorkType(FileUploadType.DESIGN,dmBackcheck.getCntrctNo());
		boolean isApiParam = attachmentsListParam != null && !attachmentsListParam.isEmpty();
	    String fileNo = null;
	    if(files != null && !files.isEmpty()) {
			int i = 0;
			fileNo = UUID.randomUUID().toString();

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
	    Short maxRgstOrder = dmBackcheckRepository.findByMaxRgstOrderByDsgnNo(dmBackcheck.getDsgnNo());
	    dmBackcheck.setRgstOrdr(++maxRgstOrder);
	    dmBackcheck.setDltYn("N");

	    dmBackcheck.setAtchFileNo(fileNo);
		dmBackcheck.setBackSeq(StringUtils.defaultIfEmpty(backCheckInsert.getDmBackcheck().getBackSeq(), UUID.randomUUID().toString()));
		dmBackcheck.setRgstrId(userId);
		dmBackcheck.setChgId(userId);

	    dmBackcheckRepository.save(dmBackcheck);

		Map<String, Object> result = Maps.newHashMap();
		result.put("dmBackcheck", dmBackcheck);
		result.put("dmAttachmentsList", dmAttachmentsList);

		return result;
	}
	
	
	/**
	 * 백체크 수정 - 의견, 첨부파일
	 * @param backCheckInsert
	 * @param files
	 */
	public Map<String, Object> updateBackCheck(BackCheckInsert backCheckInsert, List<MultipartFile> files) {
		return updateBackCheck(backCheckInsert, files, null);
	}
	public Map<String, Object> updateBackCheck(BackCheckInsert backCheckInsert, List<MultipartFile> files, Map<String, Object> params) {
		if (params == null) {
			params = Maps.newHashMap();
		}

		String userId = (String)params.get("userId");

		if (StringUtils.isEmpty(userId)) {
			UserAuth userAuth = UserAuth.get(true);
			if(userAuth == null){
				throw new GaiaBizException(ErrorType.UNAUTHORIZED,"userAuth is null");
			}
			userId = userAuth.getUsrId();
		}

		final String finalUserId = userId;

		DmBackcheck dmBackcheck = backCheckInsert.getDmBackcheck();
		List<DmAttachments> delFileList = backCheckInsert.getDelFileList();

		//기존 백체크 검색
		DmBackcheck findBackchk = dmBackcheckRepository.findByBackSeqAndDltYn(dmBackcheck.getBackSeq(), "N").orElse(null);

		if(findBackchk == null) {
			throw new GaiaBizException(ErrorType.NO_DATA, "not found back check");
		}

		// 1. 기존 파일 삭제
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
		String existingFileNo = findBackchk.getAtchFileNo();
		//API 통신으로 받은 리스트
		List<DmAttachments> attachmentsListParam = objectMapper.convertValue(params.get("dmAttachmentsList"), new TypeReference<List<DmAttachments>>() {});

		//로직을 위한 list
		List<DmAttachments> dmAttachmentsList = new ArrayList<>();

		String savePath = getUploadPathByWorkType(FileUploadType.DESIGN,findBackchk.getCntrctNo());
		if (files != null && !files.isEmpty()) {
			boolean isApiParam = attachmentsListParam != null && !attachmentsListParam.isEmpty();
			int i = 0;
			existingFileNo = existingFileNo == null?UUID.randomUUID().toString():existingFileNo;
			findBackchk.setAtchFileNo(existingFileNo);

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
					dmAttachments.setFileNo(existingFileNo);

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

		// 3. 백체크 의견 수정
		findBackchk.setBckchkOpnin(dmBackcheck.getBckchkOpnin());
		dmBackcheckRepository.save(findBackchk);

		Map<String, Object> result = Maps.newHashMap();
		result.put("dmBackcheck", findBackchk);
		result.put("dmAttachmentsList", dmAttachmentsList);

		return result;
	}
	
	/**
	 * 백체크 결과 등록 (미결 / 종결)
	 * @param cntrctNo
	 * @param dsgnNo
	 * @param backchkCd
	 */
	public void updateBackchkCd(String cntrctNo, String dsgnNo, String backchkCd) {
		updateBackchkCd(cntrctNo, dsgnNo, backchkCd, null);
	}
	public void updateBackchkCd(String cntrctNo, String dsgnNo, String backchkCd, String userId) {
		if (userId == null) {
			UserAuth userAuth = UserAuth.get(true);
			if(userAuth == null){
				throw new GaiaBizException(ErrorType.UNAUTHORIZED,"userAuth is null");
			}
			userId = userAuth.getUsrId();
		}

		DmDesignReview findReview = dmDesignReviewRepository.findByCntrctNoAndDsgnNoAndDltYn(cntrctNo, dsgnNo, "N");
		findReview.setBackchkCd(backchkCd);
		findReview.setBackchkRgstDt(LocalDateTime.now());
		findReview.setBackchkRgstrId(userId);
		dmDesignReviewRepository.save(findReview);
	}
	
	
	
	/**
	 * 백체크 삭제
	 * @param backSeq
	 */
	public void deleteBackCheck(String backSeq) {
		deleteBackCheck(backSeq, null);
	}
	public void deleteBackCheck(String backSeq, String userId) {
		if (userId == null) {
			UserAuth userAuth = UserAuth.get(true);
			if(userAuth == null){
				throw new GaiaBizException(ErrorType.UNAUTHORIZED,"userAuth is null");
			}
			userId =userAuth.getUsrId();
		}

		final String finalUserId = userId;

		DmBackcheck findBackchk = dmBackcheckRepository.findByBackSeqAndDltYn(backSeq, "N").orElse(null);
		if(findBackchk != null) {
			if(findBackchk.getAtchFileNo() != null) {
				deleteAtchFile(findBackchk.getAtchFileNo(), userId);
			}
			dmBackcheckRepository.updateDelete(findBackchk, finalUserId);
		}
	}
	
	
	
	/**
	 * 백체크 의견 일괄 삭제 - 의견, 첨부파일
	 * @param delList
	 */
	public void deleteAllBackCheck(List<DmBackcheck> delList) {
		deleteAllBackCheck(delList, null);
	}
	public void deleteAllBackCheck(List<DmBackcheck> delList, String userId) {
		if (userId == null) {
			UserAuth userAuth = UserAuth.get(true);
			if(userAuth == null){
				throw new GaiaBizException(ErrorType.UNAUTHORIZED,"userAuth is null");
			}
			userId = userAuth.getUsrId();
		}

		final String finalUserId = userId;

		delList.forEach(item -> {
			List<DmBackcheck> findBackchkList = dmBackcheckRepository.findAllByDsgnNoAndRgstrIdAndDltYn(item.getDsgnNo(), finalUserId, "N").orElse(null);

			if (findBackchkList != null && !findBackchkList.isEmpty()) {
				for (DmBackcheck findBackchk : findBackchkList) {
					if(findBackchk.getAtchFileNo() != null) {
						deleteAtchFile(findBackchk.getAtchFileNo(), finalUserId);
					}

					dmBackcheckRepository.updateDelete(findBackchk, finalUserId);
				}
			}
		});
	}
	
	
	
	/**
	 * 백체크 첨부파일 삭제
	 * @param atchFileNo
	 */
	private void deleteAtchFile(String atchFileNo, String userId) {
		if (userId == null) {
			UserAuth userAuth = UserAuth.get(true);
			if(userAuth == null){
				throw new GaiaBizException(ErrorType.UNAUTHORIZED,"userAuth is null");
			}
			userId =userAuth.getUsrId();
		}

		final String finalUserId = userId;

		List<DmAttachments> fileList = dmAttachmentsRepository.findByFileNoAndDltYn(atchFileNo, "N");
		fileList.forEach(file -> {
			dmAttachmentsRepository.updateDelete(file, finalUserId);
		});
	}


}
