package kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.report.summary;

import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.core.config.GlobalMapperConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = GlobalMapperConfig.class)
public interface SummaryForm {

    // 결함요약 목록,검색
    @Data
    @EqualsAndHashCode(callSuper = false)
    class SummaryListGet {
        @Description(name = "계약번호", description = "", type = Description.TYPE.FIELD)
        String cntrctNo;

        @Description(name = "요약 타입", description = "", type = Description.TYPE.FIELD)
        String summaryType;

        @Description(name = "결함 단계 리스트", description = "", type = Description.TYPE.FIELD)
        List<String> dfccyPhaseNoList;

        @Description(name = "작성자ID 리스트", description = "", type = Description.TYPE.FIELD)
        List<String> rgstrIdList;
    }

}
