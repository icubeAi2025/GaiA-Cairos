package kr.co.ideait.platform.gaiacairos.core.persistence.vo.safety;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwSafetyInspection;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwSafetyInspectionList;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwSafetyInspectionPhoto;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwStandardInspectionList;
import kr.co.ideait.platform.gaiacairos.core.config.GlobalMapperConfig;
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
public interface CheckForm {

    @Mapping(target = "ispDt", source = "ispDt", qualifiedByName = "stringToLocalDateTime")
    CwSafetyInspection toEntity(Safety safety);

    CwSafetyInspectionList toSafeEntity(SafetyList safetyList);

    CwStandardInspectionList toStandardEntity(SafetyList safetyList);

    @Mapping(target = "shotDate", source = "shotDate", qualifiedByName = "stringToLocalDateTime")
    CwSafetyInspectionPhoto toEntity(Photo photo);

    @Mapping(target = "ispDt", source = "ispDt", qualifiedByName = "stringToLocalDateTime")
    void updateSafety(Safety safety, @MappingTarget CwSafetyInspection safetyInspection);

    @Data
    public class Safety { // 안전점검
        String cntrctNo;
        String inspectionNo;

        String ispDocNo; // 점검 문서 번호
        String title; // 제목
        String ispDt; // 점검일자
        String cnsttyCd; // 공종
        String cnsttyCdL1; // 대공종
        String cnsttyCdL2; // 공종 코드
        short ispLstNo; // 항목 번호

        List<SafetyList> safetyLists;// 점검 항목 리스트

        List<Photo> photos;

        List<Integer> deleteSno;

        List<Integer> deletePhtSno;

        String selectedStatus; // 승인 상태
        String selectedValue; // 공종 선택 값
        String searchValue;
    }

    @Data
    public class SafetyList { // 안전점검 리스트
        String cntrctNo; // 계약번호
        String ispLstId; // 점검리스트 ID
        String cnsttyYn; // 공종 여부
        String cnsttyCd; // 공종코드
        String cnsttyNm; // 공종명
        short cnsttyLvl; // 레벨
        String upCnsttyCd; // 상위공종코드
        short ispLstNo; // 점검항목 번호
        short ispSno; // 순번

        String ispLstDscrpt; // 점검항목
        String ispDscrpt;

        Short gdFltyYn; // 양호불량 여부
        String imprvReq; // 개선요망사항

        List<Integer> deleteSno;

        List<Integer> deletePhtSno;

        String selectedWorkType;
        String searchValue;
        String useType;
    }

    @Data
    public class Photo {
        int phtSno;
        String titlNm;
        String dscrpt;
        String shotDate;
    }

    @Data
    public class SafetyLists { // 삭제할 안전점검들의 번호
        List<Safety> safetyList;
    }

    @Data
    public class IspLstIds { // 삭제할 안전점검 리스트들의 번호
        List<String> ispLstIds;
    }

    @Data
    public class CnsttyCds { // 데이터 조회용 공종코드들
        String cntrctNo;
        String cnsttyCd;
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
