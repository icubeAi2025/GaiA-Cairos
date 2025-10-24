package kr.co.ideait.platform.gaiacairos.comp.progress.service;

import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import kr.co.ideait.platform.gaiacairos.core.constant.CommonCodeConstants;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.progress.monthlyreport.MonthlyreportAdminMybatisParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MonthlyreportAdminService extends AbstractGaiaCairosService {

	/**
	 * 월간보고관리관용 목록 조회
	 * @param input
	 * @return
	 */
	public List<MonthlyreportAdminMybatisParam.MonthlyreportAdminOutput> getMonthlyreportAdminList(MonthlyreportAdminMybatisParam.MonthlyreportAdminInput input) {
		input.setApprvlStatsCd(CommonCodeConstants.APPSTATUS_CODE_GROUP_CODE);
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.monthlyreportadmin.getMonthlyreportAdminList", input);
	}

	/**
	 * 월간보고관리관용 상세 조회
	 * @param input
	 * @return
	 */
	public MonthlyreportAdminMybatisParam.MonthlyreportAdminOutput getMonthlyreportAdminDetail(MonthlyreportAdminMybatisParam.MonthlyreportAdminInput input) {
		return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.monthlyreportadmin.getMonthlyreportAdminDetail", input);
	}

	/**
	 * 월간보고관리관용 월 중복확인
	 * @param input
	 * @return
	 */
	public Integer existsMonthlyreportAdmin(MonthlyreportAdminMybatisParam.MonthlyreportAdminInput input) {
		return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.monthlyreportadmin.existsMonthlyreportAdmin", input);
	}

	/**
	 * 월간보고관리관용 추가
	 * @param input
	 * @return
	 */
	public String createMonthlyreportAdmin(MonthlyreportAdminMybatisParam.MonthlyreportAdminInput input) {
		int insert = mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.monthlyreportadmin.createMonthlyreportAdmin", input);
		return insert == 1 ? "success" : "fail";
	}

	/**
	 * 월간보고관리관용 수정
	 * @param input
	 * @return
	 */
	public String updateMonthlyreportAdmin(MonthlyreportAdminMybatisParam.MonthlyreportAdminInput input) {
		int insert = mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.monthlyreportadmin.updateMonthlyreportAdmin", input);
		return insert == 1 ? "success" : "fail";
	}

	/**
	 * 월간보고관리관용 삭제
	 *
	 * @param input
	 */
	public String deleteMonthlyreportAdmin(String cntrctChgId, Long monthlyReportAdminId, String usrId) {
		MybatisInput input = new MybatisInput().add("cntrctChgId", cntrctChgId).add("monthlyReportAdminId", monthlyReportAdminId).add("usrId", usrId);
		int insert = mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.monthlyreportadmin.deleteMonthlyreportAdmin", input);
		return insert == 1 ? "success" : "fail";
	}


}
