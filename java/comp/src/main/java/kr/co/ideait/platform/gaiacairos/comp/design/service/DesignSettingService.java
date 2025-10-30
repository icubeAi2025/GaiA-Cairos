package kr.co.ideait.platform.gaiacairos.comp.design.service;

import kr.co.ideait.platform.gaiacairos.comp.design.helper.DesignHelper;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DmDesignPhase;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DmDesignSchedule;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.DmDesignPhaseRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.DmDesignScheduleRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.tool.setting.DesignSettingMybatisParam.DesignDisplayOrderMoveInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.tool.setting.DesignSettingMybatisParam.DesignPhaseDetailInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class DesignSettingService extends AbstractGaiaCairosService {

	@Autowired
	DmDesignPhaseRepository dmDesignPhaseRepository;

	@Autowired
	DmDesignScheduleRepository dmDesignScheduleRepository;

	@Autowired
	DesignHelper designHelper;


	/**
	 * 설계검토단계 목록 조회
	 * @param cntrctNo
	 * @return
	 */
	public List selectDesignPhaseList(String cntrctNo) {
		DesignPhaseDetailInput designPhaseDetailInput = new DesignPhaseDetailInput();
		designPhaseDetailInput.setCntrctNo(cntrctNo);
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.design.designSetting.selectDesignPhaseList", designPhaseDetailInput);
	}


	/**
	 * 설계검토단계 상세조회(검토의견, 답변, 평가, 백체크의 상태조회)
	 * @param designPhaseDetailInput
	 * @return
	 */
	public List selectDesignPhase(DesignPhaseDetailInput designPhaseDetailInput) {
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.design.designSetting.selectDesignPhaseList", designPhaseDetailInput);
	}


	/**
	 * 설계검토단계 추가(검토의견, 답변, 평가, 백체크 기간 설정)
	 * @param dmDesignPhase
	 * @param scheduleArr
	 */
	public DmDesignPhase insertDesignPhase(DmDesignPhase dmDesignPhase, List<DmDesignSchedule> scheduleArr) {
		return insertDesignPhase(dmDesignPhase, scheduleArr, null);
	}
	public DmDesignPhase insertDesignPhase(DmDesignPhase dmDesignPhase, List<DmDesignSchedule> scheduleArr, String userId) {
		String dsgnPhaseNo = "";

		if (userId == null) {
			userId = UserAuth.get(true).getUsrId();
			dsgnPhaseNo = designHelper.generateUUID();
		} else {
			dsgnPhaseNo = dmDesignPhase.getDsgnPhaseNo();
		}

		dmDesignPhase.setDsgnPhaseNo(dsgnPhaseNo);

		for (DmDesignSchedule schedule : scheduleArr) {
			schedule.setDsgnPhaseNo(dsgnPhaseNo);
			schedule.setRgstrId(userId);
			schedule.setChgId(userId);
		}

		Short maxDsplyOrdr = dmDesignPhaseRepository.findMaxDsplyOrdrByCntrctNo(dmDesignPhase.getCntrctNo(), "N");
		dmDesignPhase.setDsplyOrdr(++maxDsplyOrdr);
		dmDesignPhase.setDltYn("N");
		dmDesignPhase.setRgstrId(userId);
		dmDesignPhase.setChgId(userId);

		DmDesignPhase savedPhase = dmDesignPhaseRepository.save(dmDesignPhase);

		for (int i = 0; i < scheduleArr.size(); i++) {
			DmDesignSchedule schedule = scheduleArr.get(i);
			schedule.setCntrctNo(savedPhase.getCntrctNo());
			schedule.setDltYn("N");
			schedule.setRgstrId(userId);
			schedule.setChgId(userId);
			schedule.setDsgnPhaseNo(savedPhase.getDsgnPhaseNo());
			dmDesignScheduleRepository.save(schedule);
		}

		return dmDesignPhase;
	}


	/**
	 * 설계검토단계 수정(단계명, 시작일, 종료일 변경)
	 * @param dmDesignPhase
	 * @param scheduleArr
	 */
	public void updateDesignPhase(DmDesignPhase dmDesignPhase, List<DmDesignSchedule> scheduleArr) {
		updateDesignPhase(dmDesignPhase, scheduleArr, null);
	}
	public void updateDesignPhase(DmDesignPhase dmDesignPhase, List<DmDesignSchedule> scheduleArr, String userId) {
		if (userId == null) {
			userId = UserAuth.get(true).getUsrId();
		}

		final String finalUserId = userId;

		String dsgnPhaseNo = dmDesignPhase.getDsgnPhaseNo();

		if (!scheduleArr.isEmpty()) {
			scheduleArr.forEach(schedule -> {
				DmDesignSchedule findSchedule = dmDesignScheduleRepository
						.findByDsgnPhaseNoAndDsgnPhaseCdAndDltYn(dsgnPhaseNo, schedule.getDsgnPhaseCd(), "N")
						.orElse(null);
				if (findSchedule != null) {
					findSchedule.setBgnDate(schedule.getBgnDate());
					findSchedule.setEndDate(schedule.getEndDate());
					findSchedule.setRgstrId(finalUserId);
					findSchedule.setChgId(finalUserId);

					dmDesignScheduleRepository.save(findSchedule);
				}
			});
		}

		DmDesignPhase findPhase = dmDesignPhaseRepository.findByDsgnPhaseNoAndDltYn(dsgnPhaseNo, "N").orElse(null);

		if (findPhase != null) {
			findPhase.setRgstrId(finalUserId);
			findPhase.setChgId(finalUserId);
			findPhase.setDsgnPhaseNm(dmDesignPhase.getDsgnPhaseNm());

			dmDesignPhaseRepository.save(findPhase);
		}
	}


	/**
	 * 설계검토단계 삭제(결함 단계 및 일정) -> 삭제 이후 순서 재정렬
	 * @param delPhaseList
	 * @param cntrctNo
	 */
	public boolean deleteDesignPhase(List<String> delPhaseList, String cntrctNo, String dltId) {
		dmDesignPhaseRepository.findAllById(delPhaseList).forEach(phase -> {
			dmDesignScheduleRepository.deleteDesignSchedule(phase.getDsgnPhaseNo(), dltId, "N");
			dmDesignPhaseRepository.deleteDesignPhase(phase.getDsgnPhaseNo(), dltId);
		});

		// 설계단계 순서 재정렬
		sortDisplayOrder(cntrctNo);
		return true;
	}


	/**
	 * 설계검토단계 순서 재정렬
	 * @param cntrctNo
	 */
	public void sortDisplayOrder(String cntrctNo) {
		List<DmDesignPhase> findList = dmDesignPhaseRepository.findByCntrctNoAndDltYnOrderByDsplyOrdrAsc(cntrctNo, "N");
		if (!findList.isEmpty()) {
			Short newOrder = 1;
			for (DmDesignPhase phase : findList) {
				phase.setDsplyOrdr(newOrder++);
			}
			dmDesignPhaseRepository.saveAll(findList);
		}

	}


	/**
	 * 설계검토단계 순서 변경(up, down 이동)
	 * @param moveList
	 */
	public void updateDesignDisplayOrder(List<DesignDisplayOrderMoveInput> moveList) {
		updateDesignDisplayOrder(moveList, null);
	}
	public void updateDesignDisplayOrder(List<DesignDisplayOrderMoveInput> moveList, String userId) {
		moveList.forEach(item -> {
			DmDesignPhase findPhase = dmDesignPhaseRepository.findByDsgnPhaseNoAndDltYn(item.getDsgnPhaseNo(), "N")
					.orElse(null);
			findPhase.setDsplyOrdr(item.getDsplyOrdr());
			dmDesignPhaseRepository.save(findPhase);
		});
	}

}
