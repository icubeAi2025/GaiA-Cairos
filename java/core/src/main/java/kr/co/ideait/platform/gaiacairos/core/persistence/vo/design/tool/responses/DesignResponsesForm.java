package kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.tool.responses;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DmAttachments;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DmDwg;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DmResponse;
import kr.co.ideait.platform.gaiacairos.core.config.GlobalMapperConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;
import java.util.Map;

@Mapper(config = GlobalMapperConfig.class)
public interface DesignResponsesForm {
    // 답변 조회
    DesignResponsesMybatisParam.DesignResponsesInput toDesignResponsesInput(DesignResponsesGet responsesGet);

    // 답변 데이터 조회
    @Data
    @EqualsAndHashCode(callSuper = false)
    class DesignResponsesGet {
        String cntrctNo;
        String dsgnPhaseNo;
        String resSeq;
        String dsgnNo;
    }

    // 추가
    DmResponse toDmResponse(DesignResponsesSave responses);

    // 수정
    void toUpdateDesignResponse(DesignResponsesSave responsesSave, @MappingTarget DmResponse dmResponse);

    // 답변관리 추가/수정
    @Data
    @EqualsAndHashCode(callSuper = false)
    class DesignResponsesSave {
        String cntrctNo;
        String resSeq;
        String dsgnNo;
        String rplyCd;
        String rplyCntnts;
        String atchFileNo;
        String dltYn;
        String dwgNo;

        // 답변 도서 설명
        String dwgDscrpt;

        String deleteDwgNo;

        List<DmAttachments> removedFiles;
    }

    // 수정
    void toUpdateDesignResponseDwg(DesignResponsesDwgSave designResponsesDwgSave, @MappingTarget DmDwg dmDwg);

    // 답변 도서 수정
    @Data
    @EqualsAndHashCode(callSuper = false)
    class DesignResponsesDwgSave {
        String dwgNo;
        String dwgCd;
        String dwgDscrpt;
        Short sno;
        String atchFileNo;
        String dltYn;
    }

    // 삭제
    @Data
    class DesignResponsesList {
        List<DmResponse> responsesList;
    }

}
