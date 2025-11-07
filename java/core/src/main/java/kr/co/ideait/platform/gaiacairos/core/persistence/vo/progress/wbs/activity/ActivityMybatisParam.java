package kr.co.ideait.platform.gaiacairos.core.persistence.vo.progress.wbs.activity;

import org.apache.ibatis.type.Alias;
import org.mapstruct.Mapper;

import kr.co.ideait.platform.gaiacairos.core.config.GlobalMapperConfig;
import lombok.Data;

@Mapper(config = GlobalMapperConfig.class)
public interface ActivityMybatisParam {

    @Data
    @Alias("activityListOutput")
    public class ActivityListOutput {

        String cntrctChgId;
        String revisionId;
        String activityId;
        String wbsCd;
        String activityNm;
        String activityKind;
        String earlyStart;
        String earlyFinish;
        String lateStart;
        String lateFinish;
        String planStart;
        String planFinish;
        String actualStart;
        String actualFinish;
        String currentStart;
        String currentFinish;
        Integer intlDuration;
        Integer remndrDuration;
        Integer totalFloat;
        Long exptCost;
        Long remndrCost;
        String predecessors;
        String successors;
        Integer cmpltPercent;
        String rmrk;
        String dltYn;

        String wbsNm;
        String activityKindKrn;
        Integer DEPTH;
        Integer wbsLevel;

        // 20251015 추가 - 작업일보 실적 금액
        Long acmtlCost;
    }

    // 결함추적관리 activity 상세보기
    @Data
    @Alias("activityListInput")
    public class ActivityListInput {

        String wbsCd; // 트리 선택한 값
        String cntrctChgId; // 계약변경차수
        String searchType; // 진행상태
        String searchText; // 검색 텍스트
        String searchTerm; // 검색 기간
        String startDate; // 시작 날짜
        String endDate; // 종료 날짜

        String wbsNm;
        String activityId;
        String activityNm;

        String cmnGrpCd;

    }

    @Data
    @Alias("deffecttrackingActivityInput")
    public class DeffecttrackingActivityInput {
        String cntrctNo;
        String dfccyNo;
    }

    @Data
    @Alias("deffecttrackingActivityOutput")
    class DeffecttrackingActivityOutput {
        String wbsNm;
        String activityNm;
        String planStart;
        String planFinish;
        String actualStart;
        String actualFinish;
        String state;
    }
}
