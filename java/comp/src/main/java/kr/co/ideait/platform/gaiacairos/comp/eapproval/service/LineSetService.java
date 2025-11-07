package kr.co.ideait.platform.gaiacairos.comp.eapproval.service;

import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.ApLineSet;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.ApLinesetMng;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.ApLineSetRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.ApLinesetMngRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisOutput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class LineSetService extends AbstractGaiaCairosService {

	@Autowired
	ApLinesetMngRepository apLinesetMngRepository;

	@Autowired
	ApLineSetRepository apLineSetRepository;

	/**
	 * 나의 결재선 조회
	 * @return
	 */
	public List getMyLineSetList(MybatisInput input) {
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.lineset.selectMyLineSet", input);
	}


	/**
	 * 관리자 결재선 조회
	 * @param input
	 * @return
	 */
	public List getAdminLineSetList(MybatisInput input) {
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.lineset.selectAdminLineSet", input);
	}


	/**
	 * 나의 결재선 상세 조회
	 * @param input
	 * @return
	 */
	public List getMyLineSetDetail(MybatisInput input) {
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.lineset.selectLineSetDetail", input);
	}


	/**
	 * 관리자 결재선 상세 조회
	 * @param input
	 * @return
	 */
	public List getAdminLineSetDetail(MybatisInput input) {
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.lineset.selectLineSetDetailByAdmin", input);
	}


	/**
	 * ApLinesetMng 저장
	 * @param apLinesetMng
	 * @return
	 */
	public ApLinesetMng saveApLinesetMng(ApLinesetMng apLinesetMng) {
		return apLinesetMngRepository.save(apLinesetMng);
	}


	/**
	 * ApLineSet 저장
	 * @param apLineSet
	 */
	public void saveApLineSet(List<ApLineSet> apLineSet) {
		apLineSetRepository.saveAll(apLineSet);
	}


	/**
	 * 관리자 결재선 중복체크
	 * @param input
	 * @return
	 */
	public Integer checkDuplicate(MybatisInput input) {
		return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.lineset.checkDuplicate", input);
	}


	/**
	 * 관리자 결재선 부서조회
	 * @param input
	 * @return
	 */
	public List<MybatisOutput> selectAdminLinesetDeptInfo(MybatisInput input) {
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.lineset.selectAdminLinesetDeptInfo", input);
	}


	/**
	 * 관리자 결재선 검색
	 * @param input
	 * @return
	 */
	public List<MybatisOutput> selectAdminLinesetUser(MybatisInput input) {
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.lineset.selectAdminLinesetUser", input);
	}


	/**
	 * ApLineSet 삭제 (dltYn 업데이트 N->Y)
	 * @param input
	 */
	public void deleteApLineSetList(MybatisInput input) {
		mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.lineset.deleteApLineSetList", input);
	}


	/**
	 * ApLineSetMng 삭제 (dltYn 업데이트 N->Y)
	 * @param input
	 */
	public void deleteApLineSetMngList(MybatisInput input) {
		mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.lineset.deleteApLineSetMngList", input);
	}


	/**
	 * ApLineSet 삭제
	 * @param apLineNo
	 */
	public void deleteApLineSet(Integer apLineNo) {
		mybatisSession.delete("kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.lineset.deleteAplineSet", apLineNo);
	}


	/**
	 * ApLinesetMng 조회
	 * @param apLineNo
	 * @return
	 */
	public ApLinesetMng getApLinesetMng(Integer apLineNo) {
		return apLinesetMngRepository.findByApLineNo(apLineNo).orElse(null);
	}
}
