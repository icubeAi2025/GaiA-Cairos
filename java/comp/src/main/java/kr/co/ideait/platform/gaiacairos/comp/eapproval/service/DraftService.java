package kr.co.ideait.platform.gaiacairos.comp.eapproval.service;

import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.*;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.ApDocRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.ApFavoritesRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.ApLineRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.ApShareRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisOutput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.eapproval.approval.ApprovalMybatisParam.ApprovalShareListOutput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.eapproval.draft.DraftMybatisParam;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.eapproval.draft.DraftMybatisParam.SearchAppLine;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DraftService extends AbstractGaiaCairosService {

	@Autowired
	ApDocRepository apDocRepository;

	@Autowired
	ApLineRepository apLineRepository;

	@Autowired
	ApShareRepository apShareRepository;

	@Autowired
	ApFavoritesRepository apFavoritesRepository;



	/**
	 * 서식 그룹 가져오기
	 * @param input
	 * @return
	 */
	public List selectFormTypeList(MybatisInput input){
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.draft.selectFormTypeList", input);
	}


	/**
	 * 기안문서 서식 검색
	 * @param input
	 * @return
	 */
	public List selectFormList(MybatisInput input){
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.draft.selectFormList", input);
	}


	/**
	 * 최근 기안문서 조회
	 * @param input
	 * @return
	 */
	public List selectLatestFormList(MybatisInput input) {
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.draft.selectLatestFormList", input);
	}


	/**
	 * 기안문 작성페이지 - 선택 서식 조회
	 * @param input
	 * @return
	 */
	public Map selectDraftForm(MybatisInput input) {
		return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.draft.selectDraftForm", input);
	}


	/**
	 * 임시저장 문서 조회
	 * @param input
	 * @return
	 */
	public List searchTemporaryList(MybatisInput input){
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.draft.selectTemporaryApDocNoList", input);
	}


	/**
	 * 사용자 검색
	 * @param searchAppLine
	 * @param userType
	 * @return
	 */
	public List<MybatisOutput> searchUserList(SearchAppLine searchAppLine, String userType){
		if(userType.equals("ADMIN")) {
			return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.draft.searchAdminAppLine", searchAppLine);
		} else {
			return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.draft.searchAppLine", searchAppLine);
		}
	}


	/**
	 * 부서 검색
	 * @param searchAppLine
	 * @param userType
	 * @param pjtType
	 * @return
	 */
	public List<MybatisOutput> searchDeptList(SearchAppLine searchAppLine, String userType, String pjtType){
		if(userType.equals("ADMIN")) {
			return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.draft.searchAdminDept", searchAppLine);
		} else {
			if(pjtType.equals("PGAIA")) {
				searchAppLine.setDeptId("G"+UserAuth.get(true).getCntrctNo());
			} else {
				searchAppLine.setDeptId("C"+UserAuth.get(true).getCntrctNo());
			}
			return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.draft.searchAppLineDept", searchAppLine);
		}
	}


	/**
	 * 임시저장문서 or 상신문서 상세 가져오기
	 * @param input
	 * @return
	 */
	public List selectApDoc(MybatisInput input){
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.draft.selectApDoc", input);
	}


	/**
	 * 임시저장문서 or 상신문서 결재라인 가져오기
	 * @param input
	 * @return
	 */
	public List selectApLineList(MybatisInput input){
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.draft.selectApLineList", input);
	}


	/**
	 * 임시저장문서 or 상신문서 첨부파일 가져오기
	 * @param apDocId
	 * @return
	 */
	public List selectApFileList(String apDocId){
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.draft.selectApFileList", apDocId);
	}


	/**
	 * 임시저장문서 or 상신문서 공유자 가져오기
	 * @param apDocId
	 * @return
	 */
	public List<ApprovalShareListOutput> selectApShareList(String apDocId) {
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.approval.selectApShareDetail", apDocId);
	}


	/**
	 * AP_DOC 임시저장 OR 상신 최초 입력
	 * @param apDoc
	 * @return
	 */
	public ApDoc createApDoc(ApDoc apDoc) {
		return apDocRepository.save(apDoc);
	}


	/**
	 * AP_DOC 임시저장 후 상신 or 임시저장
	 * @param apDoc
	 */
	public void updateApDoc(ApDoc apDoc) {
		MybatisInput input = MybatisInput.of().add("apDocId", apDoc.getApDocId())
						.add("apDocTitle", apDoc.getApDocTitle())
						.add("apDocEdtr", apDoc.getApDocEdtr())
						.add("apDocTxt", apDoc.getApDocTxt())
						.add("apDocStats", apDoc.getApDocStats())
						.add("apLoginId", apDoc.getApLoginId())
						.add("recipientNm", apDoc.getRecipientNm())
						.add("senderNm", apDoc.getSenderNm())
						.add("uuid", apDoc.getUuid())
						.add("naviId", apDoc.getNaviId());

		mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.draft.updateApdoc", input);
	}


	/**
	 * 결재라인&첨부파일 최초 저장을 위해 AP_DOC 기안문서 번호 가져오기
	 * @param apDocId (문서 아이디 UUID)
	 * @return String
	 * @throws
	 */
	public String selectApDocNo (String apDocId) {
		return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.draft.selectApDocNo", apDocId);
	}


	/**
	 * 결재라인 저장
	 * @param apLine
	 * @return
	 */
	public ApLine createApLineList(ApLine apLine) {
		return apLineRepository.save(apLine);
	}


	/**
	 * 임시저장문서 삭제 (결재라인)
	 * @param apDocId
	 */
	public void deleteTemporaryApLine(String apDocId) {
		mybatisSession.delete("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.draft.deleteApLine", apDocId);
	}


	/**
	 * 임시저장문서 삭제 (첨부파일)
	 * @param fileNo (파일번호)
	 * @return
	 * @throws
	 */
	public void deleteTemporaryApAttachment(int fileNo, String userId) {
		MybatisInput input = MybatisInput.of()
				.add("fileNo", fileNo)
				.add("userId", userId);
		mybatisSession.delete("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.draft.deleteTemporaryApAttachments", input);
	}


	/**
	 * 서식 즐겨찾기 목록 조회
	 * @param input
	 * @return
	 */
	public List selectBookmarkList(MybatisInput input) {
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.draft.selectBookmarkList", input);
	}


	/**
	 * 서식 즐겨찾기 추가
	 * @param apFavorites
	 */
	public void createBookmark(ApFavorites apFavorites) {
		apFavoritesRepository.save(apFavorites);
	}


	/**
	 * 서식 즐겨찾기 삭제
	 * @param params
	 */
	public void deleteBookmark(Map<String, Object> params) {
		mybatisSession.delete("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.draft.deleteBookmarkList", params);
	}


	/**
	 * 최근 사용 서식 추가 또는 업데이트
	 * @param frmNo
	 * @param apUsrId
	 */
	public void createUpdateLatestForm(Integer frmNo, String apUsrId) {
		ApFavorites existForm = apFavoritesRepository.findByFrmNoAndFvrtsDivAndUsrId(frmNo, "2", apUsrId);
		if(existForm != null) {
			existForm.setRgstDt(LocalDateTime.now());
			apFavoritesRepository.save(existForm);
		} else {
			ApFavorites newFavorite = new ApFavorites();
			newFavorite.setFrmNo(frmNo);
			newFavorite.setFvrtsDiv("2");
			newFavorite.setUsrId(apUsrId);
			newFavorite.setLoginId(apUsrId);
			apFavoritesRepository.save(newFavorite);
		}
	}


	/**
	 * 공유자 추가 API통신
	 * @param apShareList
	 */
	public void insertApShareToApi(List<ApShare> apShareList) {
		apShareRepository.saveAll(apShareList);
	}


	/**
	 * 임시저장 첨부파일 전체 삭제
	 * @param input
	 */
	public void deleteTemporaryApAttachmentsAll(MybatisInput input) {
		mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.draft.deleteTemporaryApAttachmentsAll", input);
	}


	/**
	 * 결재문서 삭제(dltYn 변경: N->Y)
	 * @param input
	 */
	public void deleteApDoc(MybatisInput input) {
		mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.draft.deleteApDoc", input);
	}


	/**
	 * 결재선 삭제
	 * @param input
	 */
	public void deleteApLine(MybatisInput input) {
		mybatisSession.delete("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.draft.deleteApLine", input);
	}

	public void deleteApLineList(List<ApDoc> deleteApDocList) {
		mybatisSession.delete("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.draft.deleteApLineList", deleteApDocList);
	}


	/**
	 * 첨부파일 삭제(dltYn 변경: N->Y)
	 * @param deleteApDocList
	 */
	public void deleteAttachmentList(List<ApDoc> deleteApDocList) {
		mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.draft.deleteAttachmentList", deleteApDocList);
	}


	/**
	 * 결재문서 일괄 삭제(dltYn 변경: N->Y)
	 * @param deleteApDocList
	 */
	public void deleteApDocList(List<ApDoc> deleteApDocList) {
		mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.draft.deleteApDocList", deleteApDocList);
	}


	/**
	 * 공유자 삭제(dltYn 변경: N->Y)
	 * @param deleteApDocList
	 */
	public void deleteApShareList(List<ApDoc> deleteApDocList) {
		mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.draft.deleteApShareList", deleteApDocList);
	}


	/**
	 * 전자결재 서식 조회
	 * @param input
	 * @return
	 */
	public List<DraftMybatisParam.ApFormListOutput> selectApFormList(MybatisInput input) {
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.draft.selectApFormList", input);
	}


	/**
	 * 첫번째 결재자 PGAIA 체크
	 * @param pjtNo
	 * @param usrId
	 * @return
	 */
	public boolean checkPgaiaFirstApproverForDraft(String pjtNo, String usrId) {
		Map<String, Object> checkParams = new HashMap<>();
		checkParams.put("pjtNo", pjtNo);
		checkParams.put("usrId", usrId);
		return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.draft.checkPgaiaFirstApproverForDraft", checkParams);
	}


	/**
	 * 결재선 PGAIA 사용자 체크
	 * @param checkParams
	 * @return
	 */
	public boolean checkPgaiaUserByApDocId(Map<String, Object> checkParams) {
		return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.draft.checkPgaiaUser", checkParams);
	}


	/**
	 * 기본서식 생성
	 * @param pjtNo
	 * @param cntrctNo
	 * @param usrId
	 * @return
	 */
	public List<ApDraftForm> createDefaultApForm(String pjtNo, String cntrctNo, String usrId) {
		List<ApDraftForm> apDraftFormList = mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.draft.selectDefaultApFormList");
		List<ApDraftForm> createApFormList = new ArrayList<>();
		apDraftFormList.forEach(form -> {
			ApDraftForm apDraftForm = new ApDraftForm();
			apDraftForm.setFrmNo(form.getFrmNo());
			apDraftForm.setFrmId(form.getFrmId());
			apDraftForm.setPjtNo(pjtNo);
			apDraftForm.setCntrctNo(cntrctNo);
			apDraftForm.setPjtType(platform.toUpperCase());
			apDraftForm.setDltYn("N");
			apDraftForm.setRgstrId(usrId);
			apDraftForm.setRgstDt(LocalDateTime.now());
			apDraftForm.setChgId(usrId);
			apDraftForm.setChgDt(LocalDateTime.now());
			createApFormList.add(apDraftForm);
		});
		createApForm(createApFormList);
		return createApFormList;
	}


	/**
	 * 서식 생성
	 * @param apFormList
	 */
	public void createApForm(List<ApDraftForm> apFormList) {
		mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.draft.insertApForm", apFormList);
	}


	/**
	 * 서식 삭제
	 * @param pjtNo
	 * @param cntrctNo
	 * @param usrId
	 */
	public void deleteApForm(String pjtNo, String cntrctNo, String usrId) {
		MybatisInput input = MybatisInput.of()
				.add("pjtNo", pjtNo)
				.add("cntrctNo", cntrctNo)
				.add("usrId", usrId);
		mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.draft.deleteApForm", input);
	}
}
