package kr.co.ideait.platform.gaiacairos.comp.eapproval.service;

import kr.co.ideait.iframework.BizException;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.ApDoc;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.ApLine;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.ApShare;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.ApDocRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.ApLineRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.ApShareRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.SmComCodeRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisOutput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.eapproval.approval.ApprovalMybatisParam.*;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApprovalService extends AbstractGaiaCairosService {

	@Autowired
	ApLineRepository apLineRepository;

	@Autowired
	ApDocRepository apDocRepository;

	@Autowired
	ApShareRepository apShareRepository;

	@Autowired
	SmComCodeRepository smComCodeRepository;


	/**
	 * 상세검색 셀렉트옵션 - 서식 리스트 조회
	 * @param input
	 * @return
	 */
	public List<ApprovalFormListOutput> getFormAllList(MybatisInput input) {
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.approval.selectApprovalFormList", input);
	}


	/**
	 * 상세검색 셀렉트옵션 - 문서구분 조회
	 * @return
	 */
	public List selectApTypeOptionsList(String cmnGrpCdType) {
		return smComCodeRepository.findByCmnGrpCdAndDltYn(cmnGrpCdType, "N");
	}


	/**
	 * 그리드 - 결재선 모달창 조회
	 * @param input
	 * @return
	 */
	public List<MybatisOutput> getApprovalLine(MybatisInput input) {
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.approval.selectApprovalLine", input);
	}


	/**
	 * 상세페이지 결재문서 조회
	 * @param input
	 * @return
	 */
	public ApprovalDetailOutput getApDocDetail(MybatisInput input) {
		return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.approval.selectApDocDetail", input);
	}


	/**
	 * 상세페이지 결재선 조회
	 * @param input
	 * @return
	 */
	public List<MybatisOutput> getApLineDetail(MybatisInput input) {
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.approval.selectApLineDetail", input);
	}


	/**
	 * 상세페이지 공유자 리스트 조회
	 * @param apDocId
	 * @return
	 */
	public List<ApprovalShareListOutput> getApShareDetail(String apDocId) {
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.approval.selectApShareDetail", apDocId);
	}


	/**
	 * 로그인유저 결재상태 조회
	 * @param input
	 * @return
	 */
	public List<MybatisOutput> getMyApLine(MybatisInput input) {
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.approval.selectMyApLine", input);
	}


	/**
	 * 공유자 추가
	 * @param apShareList
	 * @param pjtNo
	 * @param cntrctNo
	 */
	public List<ApShare> createApShareList(List<ApShare> apShareList, String pjtNo, String cntrctNo) {
		apShareList.forEach(apShare -> {
			// 전체공유면 계약단위로 세팅
			if("01".equals(apShare.getApCnrsDiv())){
				apShare.setApCnrsId(cntrctNo);
			}
			// 기존 공유자 여부 체크
			boolean exists = apShareRepository.existsByApDocIdAndApCnrsIdAndDltYn(apShare.getApDocId(), apShare.getApCnrsId(), "N");
			if(!exists) {
				apShare.setDltYn("N");
				apShareRepository.save(apShare);
			}
		});
		return apShareList;
	}

	public List<ApShare> createApShareList(List<ApShare> apShareList) {
		return apShareRepository.saveAll(apShareList);
	};


	/**
	 * 공유자 삭제
	 * @param apDocNo
	 * @param apDocId
	 */
	public void updateDeleteApShareList(int apDocNo, String apDocId) {
		apShareRepository.findByApDocNoAndApDocIdAndDltYn(apDocNo, apDocId, "N").forEach(apShare -> {
			apShareRepository.updateDelete(apShare);
		});
	}

	public void updateDeleteApShareList(List<ApShare> delShareList) {
		mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.approval.updateDeleteShare", delShareList);
	}


	/**
	 * 참조자 문서확인 업데이트
	 * @param apLine
	 */
	@Transactional
	public void updateReferenceDate(ApLine apLine) {
		mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.approval.updateReferenceDate" , apLine);
	}



	/**
	 * 부서별 직원 리스트 조회
	 * @param deptId
	 * @return
	 */
	public List selectEmployeeList(String deptId) {
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.approval.selectEmployeeList", deptId);
	}


	/**
	 * 결재문서 생성 TO API
	 * @param apDoc
	 * @return
	 */
	public ApDoc insertApDocToApi(ApDoc apDoc) {
		apDoc.setPjtType(platform.toUpperCase());
		ApDoc savedDoc = apDocRepository.save(apDoc);
		if(savedDoc == null) {
			throw new BizException("전자결재 문서 생성 실패");
		}
		return savedDoc;
	}


	/**
	 * 결재라인 생성 TO API
	 * @param apLineList
	 * @return
	 */
	public List<ApLine> insertApLineToApi(List<ApLine> apLineList) {
		List<ApLine> savedApLine = apLineRepository.saveAll(apLineList);
		if(savedApLine.isEmpty()) {
			throw new BizException("결재선 생성 실패");
		}
		return savedApLine;
	}


	/**
	 * 결재문서 조회
	 * @param apDocId
	 * @return
	 */
	public ApDoc getApprovalDoc(String apDocId) {
		return apDocRepository.findByApDocId(apDocId);
	}


	/**
	 * 공문 발신 부서 확인
	 * @param apDocId
	 * @return
	 */
	public Map<String, Object> checkSendDeptId(String apDocId) {
		MybatisInput input = MybatisInput.of().add("usrId", UserAuth.get(true).getUsrId())
				.add("cntrctNo", UserAuth.get(true).getCntrctNo())
				.add("pjtNo", UserAuth.get(true).getPjtNo())
				.add("apDocId", apDocId);
		return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.approval.checkSendDeptId", input);
	}


	public List<ApprovalListOutput> selectRequestList(ApprovalListInput approvalListInput) {
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.approval.selectRequestList", approvalListInput);
	}


	public Long selectRequestListCount(ApprovalListInput approvalListInput) {
		return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.approval.selectRequestListCount", approvalListInput);
	}

	public List<ApprovalListOutput> selectTemporaryList(ApprovalListInput approvalListInput) {
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.approval.selectTemporaryList", approvalListInput);
	}


	public Long selectTemporaryListCount(ApprovalListInput approvalListInput) {
		return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.approval.selectTemporaryListCount", approvalListInput);
	}


	public List<ApprovalListOutput> selectWaitingList(ApprovalListInput approvalListInput) {
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.approval.selectWaitingList", approvalListInput);
	}

	public Long selectWaitingListCount(ApprovalListInput approvalListInput) {
		return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.approval.selectWaitingListCount", approvalListInput);
	}


	public List<ApprovalListOutput> selectProgressList(ApprovalListInput approvalListInput) {
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.approval.selectProgressList", approvalListInput);
	}


	public Long selectProgressListCount(ApprovalListInput approvalListInput) {
		return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.approval.selectProgressListCount", approvalListInput);
	}


	public List<ApprovalListOutput> selectClosedList(ApprovalListInput approvalListInput) {
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.approval.selectClosedList", approvalListInput);
	}


	public Long selectClosedListCount(ApprovalListInput approvalListInput) {
		return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.approval.selectClosedListCount", approvalListInput);
	}


	public List<ApprovalListOutput> selectRejectedList(ApprovalListInput approvalListInput) {
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.approval.selectRejectedList", approvalListInput);
	}


	public Long selectRejectedListCount(ApprovalListInput approvalListInput) {
		return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.approval.selectRejectedListCount", approvalListInput);
	}


	public List<ApprovalListOutput> selectSharedList(ApprovalListInput approvalListInput) {
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.approval.selectSharedList", approvalListInput);
	}


	public Long selectSharedListCount(ApprovalListInput approvalListInput) {
		return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.approval.selectSharedListCount", approvalListInput);
	}


	public List<ApprovalListOutput> selectPendingList(ApprovalListInput approvalListInput) {
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.approval.selectPendingList", approvalListInput);
	}


	public Long selectPendingListCount(ApprovalListInput approvalListInput) {
		return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.approval.selectPendingListCount", approvalListInput);
	}

	public int selectApprovalMaxOrder(String apDocId) {
		return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.approval.selectApprovalMaxOrder", apDocId);
	}


	public int selectMyApLineOrder(Map<String, Object> params) {
		return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.approval.selectMyApLineOrder", params);
	}


	public void updateApLineStatus(ApLineUpdate target) {
		mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.approval.updateApLine", target);
	}


	public void updateApDoc(List<ApLineUpdate> targetList) {
		mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.approval.updateApDoc", targetList);
	}


	public boolean checkPgaiaApprover(Map<String, Object> checkParams) {
		return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.approval.checkPgaiaApprover", checkParams);
	}

	public List<ApLine> getApLineByApDocId(String apDocId) {
		return apLineRepository.findByApDocId(apDocId);
	}


	public List<ApShare> getApShareByApDocId(String apDocId) {
		return apShareRepository.findByApDocIdAndDltYn(apDocId, "N");
	}


	public boolean checkApShare(String apDocId, String apCnrsId) {
		return apShareRepository.existsByApDocIdAndApCnrsIdAndDltYn(apDocId, apCnrsId, "N");
	}


	public void updateDeleteApShareByApDocId(MybatisInput input) {
		mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.approval.updateDeleteApShareByApDocId", input);
	}


	public boolean checkPgaiaReference(Map<String, Object> checkParams) {
		return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.approval.checkPgaiaReference", checkParams);
	}


	public List<MybatisOutput> selectAdminDept(MybatisInput input) {
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.approval.selectAdminDept", input);
	}


	public List<MybatisOutput> selectDepartmentInfo(MybatisInput input) {
		return  mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.approval.selectDepartmentInfo", input);
	}


	public Map<String, String> selectUserNameForDocument(String apDocId) {
		return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.approval.selectUserNameForDocument", apDocId);
	}

	public boolean checkSupervisor(String pjtNo, String usrId) {
		Map<String, Object> checkParams = new HashMap<>();
		checkParams.put("pjtNo", pjtNo);
		checkParams.put("usrId", usrId);
		return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.approval.checkSupervisor", checkParams);
	}
}
