package kr.co.ideait.platform.gaiacairos.comp.progress.service;

import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import kr.co.ideait.platform.gaiacairos.core.constant.CommonCodeConstants;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.progress.wbs.activityqdb.ActivityQdbMybatisParam.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityQdbService extends AbstractGaiaCairosService {

	public List selectActivityQdbContractList(String pjtNo) {
		ActivityQdbContractListInput activityQdbContractListInput = new ActivityQdbContractListInput();
		activityQdbContractListInput.setPjtNo(pjtNo);
		activityQdbContractListInput.setMajorCnsttyGroupCode(CommonCodeConstants.MAJOR_CNSTTY_CODE_GROUP_CODE);
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.activityqdb.selectActivityQdbContractList", activityQdbContractListInput);
	}

	
	public List selectActivityQdbCntrctChgList(String cntrctNo) {
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.activityqdb.selectActivityQdbCntrctChgList", cntrctNo);
	}

	
	public List selectActivityWbsList(ActivityWbsListInput activityWbsListInput) {
		activityWbsListInput.setActkindGroupCode(CommonCodeConstants.ACTIVITY_KIND_CODE_GROUP_CODE);
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.activityqdb.selectActivityWbsList", activityWbsListInput);
	}


	public List selectActivityWbsQdbList(ActivityWbsQdbListInput activityWbsQdbListInput) {
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.activityqdb.selectActivityWbsQdbList", activityWbsQdbListInput);
	}

	
	public List selectActivityCbsList(ActivityCbsListInput activityCbsListInput) {
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.activityqdb.selectActivityCbsList", activityCbsListInput);
	}

	
	public List selectActivityCbsQdbList(ActivityCbsQdbListInput activityCbsQdbListInput) {
		activityCbsQdbListInput.setActkindGroupCode(CommonCodeConstants.ACTIVITY_KIND_CODE_GROUP_CODE);
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.activityqdb.selectActivityCbsQdbList", activityCbsQdbListInput);
	}
	
	
	public List selectWbsTreeList(ActivityTreeListInput activityTreeListInput) {
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.activityqdb.selectWbsTreeList", activityTreeListInput);
	}

	
	public List selectCbsTreeList(ActivityTreeListInput activityTreeListInput) {
		return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.activityqdb.selectCbsTreeList", activityTreeListInput);
	}


	
}
