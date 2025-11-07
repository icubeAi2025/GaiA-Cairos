package kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.tool.designreview;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DmDesignReview;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DmDwg;
import kr.co.ideait.platform.gaiacairos.core.config.GlobalMapperConfig;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.CommonForm;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(config = GlobalMapperConfig.class)
public interface DesignReviewForm {

    // 설계 검색 조건
    DesignReviewMybatisParam.DsgnSearchInput toDsgnSearchInput(DesignReviewListGet designReviewListGet);

    DmDesignReview toDesignReview(CreateUpdateDsgn designReview);

    DmDwg toDmDwg(Dwg dwg);

    void updateDesignReview(CreateUpdateDsgn updateDsgn, @MappingTarget DmDesignReview oldDesignReview);

    /**
     * 설계단계 조회 폼
     */
    @Data
    class DsgnPhaseListGet {
        String dsgnPhaseNo;
        String cntrctNo;
        String dsgnPhaseCd;
    }

    // 설계 목록,검색
    @Data
    @EqualsAndHashCode(callSuper = false)
    class DesignReviewListGet extends CommonForm {
        String cntrctNo;
        String dsgnPhaseNo;
        String dsgnNo;

        // 검색 조건
        String rgstr;
        String dsgnCd;
        String keyword;

        // 상세 검색 조건
        String rgstrNm;
        String myRplyYn;
        Long startDsgnNo;
        Long endDsgnNo;
        String rplyCd;
        String apprerCd;
        String backchkCd;
        String startRecentDt;
        String endRecentDt;
        String startRplyRecentDt;
        String endRplyRecentDt;
        String isuYn;
        String lesnYn;
        String atachYn;

        // 답변 일반 검색
        String rplyStatus;
    }

    // 설계 검토 추가 수정 폼
    @Data
    class CreateUpdateDsgn {
        String cntrctNo;
        String dsgnNo;
        String dsgnPhaseNo;
        String title;
        String dsgnCd;
        String docNo;
        String dwgNo;
        String dwgNm;
        String rvwOpnin;
        String isuYn;
        String lesnYn;
//        String chgDwgNo;
//        String rvwDwgNo;


        List<Dwg> dwgs;

        String deleteRvwFileNo;
        String deleteRvwFileKey;

        String deleteChgFileNo;
        String deleteChgFileKey;
    }

    // 설계 도서 폼
    @Data
    class Dwg {
        Short sno;
        String dwgDscrpt;
        String dwgCd;
    }

    // 설계 검토 번호 리스트 폼
    @Data
	public class DsgnNoList {
        List<String> dsgnNoList;
	}

}
