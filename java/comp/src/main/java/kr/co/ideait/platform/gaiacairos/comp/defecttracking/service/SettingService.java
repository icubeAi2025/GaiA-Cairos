package kr.co.ideait.platform.gaiacairos.comp.defecttracking.service;

import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DtDeficiencyPhase;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DtDeficientySchedule;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.DtDeficiencyPhaseRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.DtDeficientyScheduleRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisOutput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.tool.setting.SettingMybatisParam.DashboardListInput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class SettingService extends AbstractGaiaCairosService {

	@Autowired
	DtDeficiencyPhaseRepository dtDeficiencyPhaseRepository;

	@Autowired
	DtDeficientyScheduleRepository dtDeficientyScheduleRepository;


	/**
	 * 결함단계 목록 / 상세조회 (결함, 답변, 확인, 종결의 상태조회)
	 * @param input
	 * @return
	 */
	public List selectDeficiencyPhaseList(MybatisInput input) {
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.defecttracking.setting.selectDeficientyPhaseList", input);
	}


	/**
	 * 대시보드 결함단계 목록 조회
	 * @param input
	 * @return
	 */
	public List<MybatisOutput> getDashboardList(DashboardListInput input) {
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.defecttracking.setting.getDashboardList", input);
	}


	/**
	 * 대시보드 결함단계 개수 조회
	 * @param input
	 * @return
	 */
	public Long getDashboardListCount(DashboardListInput input) {
		return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.defecttracking.setting.getDashboardListCount", input);
	}


	/**
	 * 결함 단계 순서 최댓값 조회
	 * @param cntrctNo
	 * @return
	 */
	public Short getMaxDsplyOrdr(String cntrctNo) {
		return dtDeficiencyPhaseRepository.findMaxDsplyOrdrByCntrctNo(cntrctNo, "N");
	}


	/**
	 * 결함 단계 저장
	 * @param dtDeficiencyPhase
	 * @return
	 */
	public DtDeficiencyPhase saveDeficiencyPhase(DtDeficiencyPhase dtDeficiencyPhase) {
		return dtDeficiencyPhaseRepository.save(dtDeficiencyPhase);
	}

	public List<DtDeficiencyPhase> saveDeficiencyPhaseList(List<DtDeficiencyPhase> findList) {
		return dtDeficiencyPhaseRepository.saveAll(findList);
	}


	/**
	 * 결함 단계 일정 저장
	 * @param scheduleArr
	 * @return
	 */
	public List<DtDeficientySchedule> saveDeficientySchedule(List<DtDeficientySchedule> scheduleArr) {
		return dtDeficientyScheduleRepository.saveAll(scheduleArr);
	}


	/**
	 * 결함 단계 일정 조회
	 * @param dfccyPhaseNo
	 * @param dfccyPhaseCd
	 * @return
	 */
	public DtDeficientySchedule selectDeficientySchedule(String dfccyPhaseNo, String dfccyPhaseCd) {
		return dtDeficientyScheduleRepository.findByDfccyPhaseNoAndDfccyPhaseCdAndDltYn(dfccyPhaseNo, dfccyPhaseCd, "N").orElse(null);
	}


	/**
	 * 결함 단계 조회
	 * @param dfccyPhaseNo
	 * @return
	 */
	public DtDeficiencyPhase selectDeficiencyPhase(String dfccyPhaseNo) {
		return dtDeficiencyPhaseRepository.findByDfccyPhaseNoAndDltYn(dfccyPhaseNo, "N").orElse(null);
	}

	public DtDeficiencyPhase selectDeficiencyPhaseByDfccyPhaseNo(String dfccyPhaseNo) {
		return dtDeficiencyPhaseRepository.findByDfccyPhaseNoAndDltYn(dfccyPhaseNo, "N").orElse(null);
	}

	public List<DtDeficiencyPhase> selectDeficiencyPhaseListByCntrctNo(String cntrctNo) {
		return dtDeficiencyPhaseRepository.findByCntrctNoAndDltYnOrderByDsplyOrdrAsc(cntrctNo, "N");
	}


	/**
	 * 결함 단계 삭제
	 * @param delPhaseList
	 * @param usrId
	 */
	public void deleteDeficiencyPhase(List<String> delPhaseList, String usrId) {
		dtDeficiencyPhaseRepository.findAllById(delPhaseList).forEach(phase -> {
			dtDeficientyScheduleRepository.deleteDeficientySchedule(phase.getDfccyPhaseNo(), usrId, "N");
			dtDeficiencyPhaseRepository.updateDelete(phase, usrId);
		});
	}

}
