package kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.tool.designreview;

import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.core.config.GlobalMapperConfig;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MapDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mapstruct.Mapper;

import java.time.LocalDateTime;
import java.util.Map;

@Mapper(config = GlobalMapperConfig.class)
public interface DesignReviewDto {

    DsgnPhase toDsgnPhase(Map<String, ?> map);

    @Data
    @EqualsAndHashCode(callSuper = false)
    class DsgnPhase extends MapDto {
        @Description(name = "설계 단계 번호", description = "", type = Description.TYPE.FIELD)
        String dsgnPhaseNo;
        @Description(name = "계약 번호", description = "", type = Description.TYPE.FIELD)
        String cntrctNo;
        @Description(name = "설계 단계 이름", description = "", type = Description.TYPE.FIELD)
        String dsgnPhaseNm;
        @Description(name = "설계 단계 순서", description = "", type = Description.TYPE.FIELD)
        Short dsplyOrdr;
        @Description(name = "설계 일정 종료일", description = "", type = Description.TYPE.FIELD)
        LocalDateTime endDate;
        @Description(name = "설계 일정 시작일", description = "", type = Description.TYPE.FIELD)
        LocalDateTime bgnDate;
        
        @Description(name = "설계 단계 별 전체 결함 수", description = "", type = Description.TYPE.FIELD)
        Integer totalCount;

        @Description(name = "설계 단계 별 종료 결함 수", description = "", type = Description.TYPE.FIELD)
        Integer endCount;

        @Description(name = "설계 단계 진행 상태", description = "", type = Description.TYPE.FIELD)
        String status;
    }

}
