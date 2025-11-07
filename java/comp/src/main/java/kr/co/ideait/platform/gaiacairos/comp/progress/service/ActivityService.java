package kr.co.ideait.platform.gaiacairos.comp.progress.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.co.ideait.iframework.EtcUtil;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.PrRevision;
import kr.co.ideait.platform.gaiacairos.core.type.ActivityType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import kr.co.ideait.platform.gaiacairos.core.constant.CommonCodeConstants;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.PrActivity;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.PrActivityRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.progress.wbs.activity.ActivityMybatisParam.ActivityListInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.progress.wbs.activity.ActivityMybatisParam.ActivityListOutput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.progress.wbs.activity.ActivityMybatisParam.DeffecttrackingActivityInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.progress.wbs.activity.ActivityMybatisParam.DeffecttrackingActivityOutput;

@Service
public class ActivityService extends AbstractGaiaCairosService {

    @Autowired
    PrActivityRepository prActivityRepository;

    // 20250227 - 정적검사 수정 [Performance Warnings] SS_SHOULD_BE_STATIC
    private static final String DEFAULT_MAPPER_PATH = "kr.co.ideait.platform.gaiacairos.mybatis.mappers.progress.activity";

    String cmnGrpCd = CommonCodeConstants.ACTIVITY_KIND_CODE_GROUP_CODE;

    // Activity 목록 조회
    public List<ActivityListOutput> getActivityList(ActivityListInput activityListInput) {

        activityListInput.setCmnGrpCd(cmnGrpCd);

        List<ActivityListOutput> activityList = mybatisSession.selectList(
                DEFAULT_MAPPER_PATH + ".getActivityList",
                activityListInput);

        return activityList;
    }

    // Activity 생성
    @Transactional
    public void createActivity(PrRevision prRevision, ArrayList<HashMap<String, Object>> activityList) {

        ArrayList<PrActivity> prActivityList = new ArrayList<>();
        if (activityList != null && !activityList.isEmpty()) {
            for (HashMap<String, Object> activity : activityList) {

                if (activity == null) continue;

                PrActivity prActivity = new PrActivity();
                prActivity.setCntrctChgId(prRevision.getCntrctChgId());
                prActivity.setRevisionId(prRevision.getRevisionId());
                prActivity.setWbsCd(EtcUtil.nullConvert(activity.get("wbsObjectId")));
                prActivity.setActivityId(EtcUtil.nullConvert(activity.get("id")));
                prActivity.setActivityNm(EtcUtil.nullConvert(activity.get("name")));

                // 20250630 ActivityKind - Primavera 포맷 > Gaia 포맷 변경
                String activityKind = ActivityType.getGaiaTypeByPrimaveraType(EtcUtil.nullConvert(activity.get("type")));
                prActivity.setActivityKind(activityKind);	// [확인]

                prActivity.setEarlyStart(EtcUtil.nullConvert(activity.get("earlyStartDate")));
                prActivity.setEarlyFinish(EtcUtil.nullConvert(activity.get("earlyFinishDate")));
                prActivity.setLateStart(EtcUtil.nullConvert(activity.get("lateStartDate")));
                prActivity.setLateFinish(EtcUtil.nullConvert(activity.get("lateFinishDate")));
                prActivity.setPlanStart(EtcUtil.nullConvert(activity.get("plannedStartDate")));
                prActivity.setPlanFinish(EtcUtil.nullConvert(activity.get("plannedFinishDate")));
                prActivity.setActualStart(EtcUtil.nullConvert(activity.get("actualStartDate")));
                prActivity.setActualFinish(EtcUtil.nullConvert(activity.get("actualFinishDate")));
                prActivity.setCurrentStart(EtcUtil.nullConvert(activity.get("startDate")));					// [확인]
                prActivity.setCurrentFinish(EtcUtil.nullConvert(activity.get("finishDate")));				// [확인]
                prActivity.setIntlDuration(EtcUtil.zeroConvertDouble(activity.get("plannedDuration")));		// [확인] - 최초기간
                prActivity.setRemndrDuration(EtcUtil.zeroConvertDouble(activity.get("remainingDuration")));	// [확인] - 잔여기간
                prActivity.setTotalFloat(EtcUtil.zeroConvertDouble(activity.get("totalFloat")));			// [확인] - 총여유
                prActivity.setExptCost(EtcUtil.zeroConvertDouble(activity.get("plannedTotalCost")));		// [확인] - 예상비용
                prActivity.setRemndrCost(EtcUtil.zeroConvertDouble(activity.get("remainingTotalCost")));	// [확인] - 잔여비용
                prActivity.setPredecessors("");
                prActivity.setSuccessors("");
                prActivity.setCmpltPercent(EtcUtil.zeroConvertDouble(activity.get("durationPercentComplete")));	// [확인필요] - 완료진행율
                prActivity.setRmrk("");
                prActivity.setDltYn("N");
                prActivity.setRgstrId(prRevision.getRgstrId());

                prActivity.setP6ActivityObjId(Integer.parseInt(activity.get("objectId").toString()));
                prActivity.setP6WbsObjId(Integer.parseInt(activity.get("wbsObjectId").toString()));
                prActivityList.add(prActivity);
            }
        }

        // insert
        prActivityRepository.saveAllAndFlush(prActivityList);

        // 2025-01-22 테이블 외래키 사용 ~ cascade update 처리로 인한 미사용처리
        // 동작 확인후 삭제 예정
        // insert 후 코드 정보 가공
        // HashMap<String, Object> vo = new HashMap<>();
        // vo.put("CNTRCT_CHG_ID", prActivityList.getFirst().getCntrctChgId());
        // vo.put("REVISION_ID", prActivityList.getFirst().getRevisionId());
        // updateActivityWbsCd(vo);
    }

    // Activity 물리 삭제 - REVISION 생성시점 활용
    public void deleteActivity(HashMap<String, Object> activity) {
        mybatisSession.delete(DEFAULT_MAPPER_PATH + ".deleteActivityList", activity);
    }

    // Activity 논리 삭제 - REVISION 삭제시점 활용
    public void updateActivityDeleteState(HashMap<String, Object> activity) {
        mybatisSession.delete(DEFAULT_MAPPER_PATH + ".updateActivityDeleteState", activity);
    }


    // Activity 코드 업데이트
    public void updateActivityWbsCd(HashMap<String, Object> activity) {
        mybatisSession.update(DEFAULT_MAPPER_PATH + ".updateActivityWbsCd", activity);
    }

    // 결함추적관리 activity 상세보기
    public List<DeffecttrackingActivityOutput> getDeffecttrackingActivity(DeffecttrackingActivityInput input) {

        List<DeffecttrackingActivityOutput> activityOutputs = mybatisSession
                .selectList(DEFAULT_MAPPER_PATH + ".getDeffecttrackingActivity", input);

        return activityOutputs;
    }

    /**
     * 작업일보 -> 프리마베라 실적업데이트 활용
     * Activity 목록 조회
     * @param vo
     * @return
     */
    public List getActivityListForPrimavera(Map<String, Object> vo) {
        return mybatisSession.selectList(DEFAULT_MAPPER_PATH + ".getActivityListForPrimavera", vo);
    }
}
