package kr.co.ideait.platform.gaiacairos.core.persistence.vo.progress.wbs.wbs;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;

@Mapper(componentModel = ComponentModel.SPRING)
public interface WbsForm {

    // 그리드 목록 조회
    WbsMybatisParam.WbsListInput toWbsListInput(WbsListGet wbsListGet);

    @Data
    @EqualsAndHashCode(callSuper = false)
    class WbsListGet {
        String listType;
        String searchText;
        String cntrctChgId;
        String upWbsCd;
    }
}
