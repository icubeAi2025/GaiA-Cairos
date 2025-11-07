package kr.co.ideait.platform.gaiacairos.core.persistence.vo.safety;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwSadtag;
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
public interface SadtagForm {

    @Mapping(target = "findDt", source = "findDt", qualifiedByName = "stringToLocalDateTime")
    @Mapping(target = "actnTmlmt", source = "actnTmlmt", qualifiedByName = "stringToLocalDateTime")
    @Mapping(target = "actnDt", source = "actnDt", qualifiedByName = "stringToLocalDateTime")
    CwSadtag toEntity(Sadtag sadTag);

    @Mapping(target = "findDt", source = "findDt", qualifiedByName = "stringToLocalDateTime")
    @Mapping(target = "actnTmlmt", source = "actnTmlmt", qualifiedByName = "stringToLocalDateTime")
    @Mapping(target = "actnDt", source = "actnDt", qualifiedByName = "stringToLocalDateTime")
    void updateSadtag(Sadtag sadtag, @MappingTarget CwSadtag cwSadtag);

    @Data
    public class Sadtag {
        String cntrctNo;
        String sadtagNo;

        String sadtagDocNo; // 안전지적 문서 번호
        String dfccyTyp; // 결함타입
        String title; // 제목
        String findId; // 발견자
        String findDt; // 발견일자
        String dfccyCntnts; // 결함-부적합내용
        String dfccyLct; // 결함위치
        String actnTmlmt; // 조치기한
        String pstats; // 진행상태
        String actnId; // 조치자
        String actnDt; // 조치일자
        String actnRslt; // 조치결과
        
        String selectedStatus;
        String searchValue;
    }

    @Data
    public class Sadtags{
         List<Sadtag> sadtagList;
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
