package kr.co.ideait.platform.gaiacairos.core.persistence.vo.construction.qualityinspection;

import kr.co.ideait.platform.gaiacairos.core.config.GlobalMapperConfig;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.*;
import lombok.Data;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Mapper(config = GlobalMapperConfig.class)
public interface QualityinspectionForm {

    @Mapping(target = "ispReqDt", source = "ispReqDt", qualifiedByName = "stringToLocalDateTime")
    CwQualityInspection toEntity(CreateQuality createQuality);

    CwQualityActivity toEntity(Activity activity);

    CwQualityCheckList toEntity(CheckList checklist);

    CwCntqltyCheckList toEntity(CreateCntCheckList checkList);

    @Mapping(target = "shotDate", source = "shotDate", qualifiedByName = "stringToLocalDateTime")
    CwQualityPhoto toEntity(Photo photo);

    @Mapping(target = "ispReqDt", source = "ispReqDt", qualifiedByName = "stringToLocalDateTime")
    @Mapping(target = "rsltDt", source = "rsltDt", qualifiedByName = "stringToLocalDateTime")
    @Mapping(target = "apprvlDt", source = "apprvlDt", qualifiedByName = "stringToLocalDateTime")
    void updateQuality(UpdateQuality quality, @MappingTarget CwQualityInspection qualityInspection);

    void updateActivity(Activity activity, @MappingTarget CwQualityActivity qualityActivity);

    void updateCheckList(CheckList checkList, @MappingTarget CwQualityCheckList qualityCheckList);

    @Data
    class CreateQuality {
        String cntrctNo; // 계약번호

        String ispDocNo; // 품질검측 문서 번호
        String ispReqDt; // 검측요청일자
        String ispLct; // 위치
        String cnsttyCd; // 상위공종
        String cnsttyCdL1; // 공종코드1
        String cnsttyCdL2; // 공종코드2
        String ispPart; // 검측부위
        String ispIssue; // 검측사항

        List<Activity> activity;

        List<CheckList> checklist;

        List<Photo> photos;
    }

    @Data
    class UpdateQuality {
        String cntrctNo; // 계약번호
        String qltyIspId; // 품질검측 ID
        String ispDocNo; // 품질검측 문서 번호
        String ispReqDt; // 검측요청일자
        String ispLct; // 위치
        String cnsttyCd; // 상위공종
        String cnsttyCdL1; // 공종코드1
        String cnsttyCdL2; // 공종코드2
        String ispPart; // 검측부위
        String ispIssue; // 검측사항

        List<Activity> activity;

        List<CheckList> checklist;

        List<Photo> photos;

        List<Integer> deleteSno;

        List<Integer> deletePhtSno;

        String rsltDocNo;   // 검측결과 문서번호
        String rsltDt;   // 검측일자
        String rsltCd;  // 검측결과 코드
        String ordeOpnin;   // 지시사항

        String apprvlId;    // 결재 승안자
        String apprvlDt;    // 결재일
        String apprvlStats; // 결재 결과
        String apOpinion;   // 결재 의견

        String baseUrl;
        String imgDir;
    }

    @Data
    class Activity {
        String wbsCd; // wbs코드
        String activityId; // ActivityID
    }

    @Data
    class CheckList {
        String chklstId; // 체크리스트 ID
        short chklstSno; // 검사항목 번호
        String chklstBssCd; // 검사기준 코드
        String cnstrtnYn; // 시공담당자 확인 여부
        String cqcYn; // 검측자 확인 여부
        String actnDscrpt; // 조치 사항
    }

    @Data
    class Photo {
        int phtSno;
        String titlNm;
        String dscrpt;
        String shotDate;
    }

    @Data
    class CreateCntCheckList { // 체크 리스트
        String cntrctNo; // 계약번호
        Integer qltyIspId; // 품질검측 ID
        String chklstId; // 체크리스트 ID
        Integer cnsttyLvl; // 레벨
        String cnsttyYn; // 공종 여부
        String cnsttyCd; // 공종코드
        String cnsttyNm; // 공종명
        String upCnsttyCd; // 상위공종코드
        Integer chklstSno; // 검사항목 번호
        String chklstDscrpt; // 검사항목
        String chklstBssCd; // 검사기준 코드

        String selectedWorkType;
        String searchValue;
        String useType;
    }

    @Data
    class ChklstIdList {
        List<String> chklstIds;
    }

    @Data
    class QualityList {
        List<CwQualityInspection> qualityList;
        String cmnCdNmKrn;
        String rsltDocNo;
    }

    @Named("stringToLocalDateTime")
    public static LocalDateTime stringToLocalDateTime(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE)
                .atStartOfDay(ZoneOffset.UTC)
                .toLocalDateTime();
    }
}
