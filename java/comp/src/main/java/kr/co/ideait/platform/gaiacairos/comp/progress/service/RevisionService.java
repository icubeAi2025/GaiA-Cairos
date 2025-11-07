package kr.co.ideait.platform.gaiacairos.comp.progress.service;

import java.util.List;
import java.util.Map;

import kr.co.ideait.iframework.EtcUtil;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.progress.wbs.revision.RevisionMybatisParam;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.contract.contractstatus.ContractstatusMybatisParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.PrRevision;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.PrRevisionRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.progress.wbs.revision.RevisionMybatisParam.DeleteRevisionInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.progress.wbs.revision.RevisionMybatisParam.RevisionListInput;

@Service
public class RevisionService extends AbstractGaiaCairosService {

	@Autowired
	PrRevisionRepository prRevisionRepository;
	

	// Revision 목록 조회
	public List selectRevisionList(RevisionListInput revisionListInput) {
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.core.persistence.mybatis.progress.revision.selectRevisionList", revisionListInput);
	}
	
	// 계약변경 조회
	public Map selectContractChange(String cntrctNo) {
		return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.core.persistence.mybatis.progress.revision.selectContractChange", cntrctNo);
	}
	
	// 리비젼 조회
	public PrRevision getRevision(String cntrctChgId, String revisionId) {
		return prRevisionRepository.findByCntrctChgIdAndRevisionId(cntrctChgId, revisionId);
	}
	
	// 리비젼 생성
	@Transactional
	public void insertRevision(PrRevision prRevision) {
		prRevision.prePersist();
		prRevisionRepository.saveAndFlush(prRevision);
	}

	// 리비젼 삭제 (삭제 여부 업데이트)
	@Transactional
	public void deleteRevision(List<DeleteRevisionInput> delRevisionList, String userId) {
		delRevisionList.forEach( delRevision -> {
			PrRevision findeRevision = prRevisionRepository.findByCntrctChgIdAndRevisionId(delRevision.getCntrctChgId(), delRevision.getRevisionId());
			if(findeRevision != null) {
				
				// 2025-06-18 CAIROS-PGAIA 연동 AUTHENTICATION 으로 인한 추가
				prRevisionRepository.updateDelete(findeRevision, userId);
			}
		});
	}

	/**
	 * 리비젼 전부 삭제
	 */
	public void deleteAllRevision(MybatisInput input) {
		mybatisSession.update("kr.co.ideait.platform.gaiacairos.core.persistence.mybatis.progress.revision.deleteAllRevision", input);
	}


	// 리비젼 수정
	@Transactional
	public void updateRevision(PrRevision prRevision) {
		prRevisionRepository.save(prRevision);
	}

	/**
	 * Update 하려는 revision이 최종버전일 경우 계약건에 대한 setLastRevisionYn("N") 처리
	 * 변경
	 * 		20250714 장기계속계약_차수별 인경우 해당 차수만 초기화, 그 외는 전체 초기화
	 */
	@Transactional
	public void updateLastYn(String cntrctNo) {
		mybatisSession.update("kr.co.ideait.platform.gaiacairos.core.persistence.mybatis.progress.revision.updateLastYn", cntrctNo);
	}


	/**
	 * 계약 - 이전 최종 Revision 조회
	 * @param vo
	 * @return
	 */
	public Map getPrevLastRevision(Map vo) {
		RevisionMybatisParam.PrevRevision prevRevision = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.core.persistence.mybatis.progress.revision.getPrevLastRevision", vo);
		if (prevRevision != null) {
			return EtcUtil.convertObjectToMap(prevRevision);
		} else {
			return null;
		}
	}


	/**
	 * 계약변경ID 기준 RevisionId 조회
	 * @param vo (cntrctChgId)
	 * @return
	 */
	public String getRevisionId(Map vo) {
		return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.core.persistence.mybatis.progress.revision.getRevisionId", vo);
	}


	
}
