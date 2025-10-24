package kr.co.ideait.platform.gaiacairos.comp.design.service;

import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisOutput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.tool.dashboard.DesignDashboardForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.tool.setting.DesignSettingMybatisParam;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.tool.setting.DesignSettingMybatisParam.DesignDashboardListInput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class DesignDashboardService extends AbstractGaiaCairosService {
	@Autowired
	DesignDashboardForm designDashboardForm;
	/**
	 * 대시보드 결함단계 목록 조회
	 * @param dashboardList
	 * @param pageable
	 * @return
	 */
	public Page<MybatisOutput> getDesignDashboardList(DesignDashboardForm.DesignDashboardList dashboardList, Pageable pageable) {

		DesignSettingMybatisParam.DesignDashboardListInput input = designDashboardForm.toDesignDashboardListInput(dashboardList);
		List<MybatisOutput> output = null;
		Long totalCount = null;
		input.setPageable(pageable);
		output = mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.design.designSetting.getDesignDashboardList", input);
		totalCount = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.design.designSetting.getDesignDashboardListCount", input);
		return new PageImpl<>(output, input.getPageable(), totalCount);
	}

}
