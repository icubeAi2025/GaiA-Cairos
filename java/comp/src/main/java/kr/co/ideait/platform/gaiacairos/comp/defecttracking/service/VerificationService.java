package kr.co.ideait.platform.gaiacairos.comp.defecttracking.service;

import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DtAttachments;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DtDeficiencyConfirm;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DtDeficiencyConfirmHistory;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.DtAttachmentsRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.DtDeficiencyConfirmHistoryRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.DtDeficiencyConfirmRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisOutput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.tool.verification.VerificationMybatisParam.ConfirmHistoryInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.tool.verification.VerificationMybatisParam.DfccyConfirmDetailInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.tool.verification.VerificationMybatisParam.DfccyConfirmListInput;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerificationService extends AbstractGaiaCairosService {

	@Autowired
	DtDeficiencyConfirmRepository dtDeficiencyConfirmRepository;

	@Autowired
	DtDeficiencyConfirmHistoryRepository dtDeficiencyConfirmHistoryRepository;

	@Autowired
	DtAttachmentsRepository dtAttachmentsRepository;


	/**
	 * 결함 확인 관리 목록조회 (결함, 답변, 첨부파일)
	 * @param dfccyConfirmListInput
	 * @return
	 */
	public List<MybatisOutput> selectDfccyConfirmList(DfccyConfirmListInput dfccyConfirmListInput) {
		List<MybatisOutput> output = mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.defecttracking.verification.selectDfccyConfirmList", dfccyConfirmListInput);

		if(output != null && !output.isEmpty()) {
			output.forEach(item -> {
				// 결함 첨부파일
				if(item.get("dfccy_file_no") != null) {
					Integer dfccyFileNo = (Integer)item.get("dfccy_file_no");
					List<DtAttachments> dfccyFiles = dtAttachmentsRepository.findByFileNoAndDltYn(dfccyFileNo, "N");
					item.put("dfccyFiles", dfccyFiles);
				}

				// 답변 첨부파일
				if(item.get("reply_atch_file_no") != null) {
					Integer replyAtchFileNo = (Integer)item.get("reply_atch_file_no");
					List<DtAttachments> replyFiles = dtAttachmentsRepository.findByFileNoAndDltYn(replyAtchFileNo, "N");
					item.put("replyFiles", replyFiles);
				}
			});
		}

		return output;
	}


	/**
	 * 결함 확인 관리 목록 개수 조회
	 * @param dfccyConfirmListInput
	 * @return
	 */
	public Long selectDfccyConfirmCount(DfccyConfirmListInput dfccyConfirmListInput) {
		return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.defecttracking.verification.selectDfccyConfirmListCount", dfccyConfirmListInput);
	}


	/**
	 * 검색 셀렉트 옵션 - 결함분류 조회
	 * @return
	 */
	public List<MybatisOutput> selectDfccyCd() {
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.defecttracking.verification.selectDfccyCd");
	}


	/**
	 * 결함 확인 상세조회 (등록된 모든 확인의견&첨부파일)
	 * @param dfccyConfirmDetailInput
	 * @return
	 */
	public List<MybatisOutput> selectDfccyConfirmDetail(DfccyConfirmDetailInput dfccyConfirmDetailInput) {
		List<MybatisOutput> output = mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.defecttracking.verification.selectDfccyConfirmDetail", dfccyConfirmDetailInput);
		output.forEach(item -> {
			if(item.get("dfccy_seq") != null && item.get("atch_file_no") != null) {
				Integer atchFileNo = (Integer)item.get("atch_file_no");
				List<DtAttachments> fileList = dtAttachmentsRepository.findByFileNoAndDltYn(atchFileNo, "N");
				item.put("confirmFiles", fileList);
			}
		});
		return output;
	}


	/**
	 * QA / 관리관 확인 이력 조회
	 * @param confirmHistoryInput
	 * @return
	 */
	public List<MybatisOutput> selectConfirmHistoryList(ConfirmHistoryInput confirmHistoryInput) {
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.defecttracking.verification.selectConfirmHistoryList", confirmHistoryInput);
	}


	/**
	 * 확인 의견 삭제 - 의견, 첨부파일
	 * @param dtDeficiencyConfirm
	 * @param dltId
	 */
	public void deleteDfccyConfirm(DtDeficiencyConfirm dtDeficiencyConfirm, String dltId) {
		dtDeficiencyConfirmRepository.updateDelete(dtDeficiencyConfirm, dltId);
	}


	/**
	 * 확인 의견 순서 최댓값 조회
	 * @param dfccyNo
	 * @return
	 */
	public Short getConfirmMaxRgstOrder(String dfccyNo) {
		return dtDeficiencyConfirmRepository.findByMaxRgstOrderByDfccyNo(dfccyNo);
	}


	/**
	 * 확인 의견 저장
	 * @param dtDeficiencyConfirm
	 * @return
	 */
	public DtDeficiencyConfirm saveDtDeficiencyConfirm(DtDeficiencyConfirm dtDeficiencyConfirm) {
		return dtDeficiencyConfirmRepository.save(dtDeficiencyConfirm);
	}


	/**
	 * 확인 결과 저장
	 * @param dtDeficiencyConfirmHistory
	 * @return
	 */
	public DtDeficiencyConfirmHistory saveDtDeficiencyConfirmHistory(DtDeficiencyConfirmHistory dtDeficiencyConfirmHistory) {
		return dtDeficiencyConfirmHistoryRepository.save(dtDeficiencyConfirmHistory);
	}


	/**
	 * 확인 의견 조회
	 * @param dfccySeq
	 * @return
	 */
	public DtDeficiencyConfirm selectDfccyConfirm(String dfccySeq) {
		return dtDeficiencyConfirmRepository.findByDfccySeqAndDltYn(dfccySeq, "N").orElse(null);
	}

	public List<DtDeficiencyConfirm> selectDfccyConfirmByUsrId(String dfccyNo, String usrId) {
		return dtDeficiencyConfirmRepository.findByDfccyNoAndRgstrIdAndDltYn(dfccyNo, usrId, "N");
	}


	/**
	 * 확인 결과 등록 순서 최댓값 조회
	 * @param dfccyNo
	 * @param cnfrmDiv
	 * @return
	 */
	public Short getConfirmHistoryMaxRgstOrder(String dfccyNo, String cnfrmDiv) {
		return dtDeficiencyConfirmHistoryRepository.findByMaxRgstOrderByDfccyNo(dfccyNo, cnfrmDiv);
	}

}
