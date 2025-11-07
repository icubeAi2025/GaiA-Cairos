package kr.co.ideait.platform.gaiacairos.core.persistence.vo.progress.wbs.activity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;

@Mapper(componentModel = ComponentModel.SPRING)
public interface ActivityForm {

    // 그리드 목록 조회
    ActivityMybatisParam.ActivityListInput toActivityListInput(ActivityListGet activityListGet);

    @Data
    @EqualsAndHashCode(callSuper = false)
    class ActivityListGet {

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
    }
}
