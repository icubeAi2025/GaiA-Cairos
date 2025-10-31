package kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.tool.defecttracking;

import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DtAttachments;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DtDeficiency;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DtDeficiencyActivity;
import kr.co.ideait.platform.gaiacairos.core.config.GlobalMapperConfig;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.CommonForm;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(config = GlobalMapperConfig.class)
public interface DefectTrackingForm {

    // 결함 검색 조건
    DefectTrackingMybatisParam.DfccySearchInput toDfccySearchInput(DefectTrackingListGet defectTrackingListGet);

    DtDeficiency toDeficiency(CreateUpdateDfccy deficiency);

    DtDeficiencyActivity toDeficiencyActivity(Activity activity);

    List<DtDeficiencyActivity> toDeficiencyActivityList(List<Activity> activity);

    DtDeficiency updateDeficiency(CreateUpdateDfccy update, @MappingTarget DtDeficiency oldDeficiency);


    //결함단계 조회
    @Data
    class DfccyPhaseListGet {
        @Description(name = "결함단계 번호", description = "", type = Description.TYPE.FIELD)
        String dfccyPhaseNo;

        @Description(name = "계약번호", description = "", type = Description.TYPE.FIELD)
        String cntrctNo;

        @Description(name = "결함단계 코드", description = "", type = Description.TYPE.FIELD)
        String dfccyPhaseCd;
    }

    // 결함 목록,검색
    @Data
    @EqualsAndHashCode(callSuper = false)
    class DefectTrackingListGet extends CommonForm {
        @Description(name = "계약 번호", description = "", type = Description.TYPE.FIELD)
        String cntrctNo;

        @Description(name = "결함단계 번호", description = "", type = Description.TYPE.FIELD)
        String dfccyPhaseNo;

        // 검색 조건
        @Description(name = "작성자", description = "", type = Description.TYPE.FIELD)
        String rgstr;

        @Description(name = "결함코드", description = "", type = Description.TYPE.FIELD)
        String dfccyCd;

        @Description(name = "검색어", description = "", type = Description.TYPE.FIELD)
        String keyword;

        // 상세 검색 조건
        @Description(name = "내 의견", description = "", type = Description.TYPE.FIELD)
        String rgstrMy;

        @Description(name = "작성자 이름", description = "", type = Description.TYPE.FIELD)
        String rgstrNm;

        @Description(name = "결함ID 검색 시작 값", description = "", type = Description.TYPE.FIELD)
        Long startDfccyNo;

        @Description(name = "결함ID 검색 종료 값", description = "", type = Description.TYPE.FIELD)
        Long endDfccyNo;

        @Description(name = "액티비티 명", description = "", type = Description.TYPE.FIELD)
        String activityNm;

        @Description(name = "답변 결과 상태", description = "", type = Description.TYPE.FIELD)
        String rplyStatus;

        @Description(name = "답변 결과 코드", description = "", type = Description.TYPE.FIELD)
        String rplyCd;

        @Description(name = "QA 결과 상태", description = "", type = Description.TYPE.FIELD)
        String qaStatus;

        @Description(name = "QA 결과 코드", description = "", type = Description.TYPE.FIELD)
        String qaCd;

        @Description(name = "관리관 결과 상태", description = "", type = Description.TYPE.FIELD)
        String spvsStatus;

        @Description(name = "관리관 결과 코드", description = "", type = Description.TYPE.FIELD)
        String spvsCd;

        @Description(name = "종결 결과 코드", description = "", type = Description.TYPE.FIELD)
        String edCd;

        @Description(name = "종결 결과 상태", description = "", type = Description.TYPE.FIELD)
        String edStatus;

        @Description(name = "입력기간 시작일", description = "", type = Description.TYPE.FIELD)
        String startRgstDt;

        @Description(name = "입력기간 종료일", description = "", type = Description.TYPE.FIELD)
        String endRgstDt;

        @Description(name = "첨부파일 여부", description = "", type = Description.TYPE.FIELD)
        String atachYn;

        @Description(name = "생명/보건/안전 관련 여부", description = "", type = Description.TYPE.FIELD)
        String crtcIsueYn;

        @Description(name = "내 의견 여부", description = "", type = Description.TYPE.FIELD)
        String myRplyYn;

        @Description(name = "답변 입력기간 시작일", description = "", type = Description.TYPE.FIELD)
        String startRplyRecentDt;

        @Description(name = "답변 입력기간 종료일", description = "", type = Description.TYPE.FIELD)
        String endRplyRecentDt;

        @Description(name = "중요 결함 여부", description = "", type = Description.TYPE.FIELD)
        String priorityCheck;

        // 답변관리 검색용
        @Description(name = "검색어", description = "", type = Description.TYPE.FIELD)
        String searchText;

        @Description(name = "페이지 목록 개수", description = "", type = Description.TYPE.FIELD)
        Integer size;
    }

    // 결함 추가/수정
    @Data
    class CreateUpdateDfccy {
        @Description(name = "계약번호", description = "", type = Description.TYPE.FIELD)
        String cntrctNo;

        @Description(name = "결함번호", description = "", type = Description.TYPE.FIELD)
        String dfccyNo;

        @Description(name = "결함 단계 번호", description = "", type = Description.TYPE.FIELD)
        String dfccyPhaseNo;

        @Description(name = "제목", description = "", type = Description.TYPE.FIELD)
        String title;

        @Description(name = "결함코드", description = "", type = Description.TYPE.FIELD)
        String dfccyCd;

        @Description(name = "결함위치", description = "", type = Description.TYPE.FIELD)
        String dfccyLct;

        @Description(name = "생명/보건/안전 관련 여부", description = "", type = Description.TYPE.FIELD)
        String crtcIsueYn;

        @Description(name = "결함내용", description = "", type = Description.TYPE.FIELD)
        String dfccyCntnts;

        @Description(name = "액티비티 리스트", description = "", type = Description.TYPE.FIELD)
        List<Activity> activity;

        @Description(name = "중요 여부", description = "", type = Description.TYPE.FIELD)
        char priorityCheck;

        @Description(name = "삭제할 첨부파일 리스트", description = "", type = Description.TYPE.FIELD)
        List<DtAttachments> delFileList;
    }

    // activity 추가
    @Data
    class Activity {
        @Description(name = "wbs코드", description = "", type = Description.TYPE.FIELD)
        String wbsCd;

        @Description(name = "액티비티 ID", description = "", type = Description.TYPE.FIELD)
        String activityId;
    }

    // 결함삭제
    @Data
    class DfccyNoList {
        @Description(name = "결함번호 리스트", description = "", type = Description.TYPE.FIELD)
        List<String> dfccyNoList;
    }

}
