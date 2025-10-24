package kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.tool.designreview;

import javax.annotation.processing.Generated;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DmDesignReview;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DmDwg;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:16+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class DesignReviewFormImpl implements DesignReviewForm {

    @Override
    public DesignReviewMybatisParam.DsgnSearchInput toDsgnSearchInput(DesignReviewListGet designReviewListGet) {
        if ( designReviewListGet == null ) {
            return null;
        }

        DesignReviewMybatisParam.DsgnSearchInput dsgnSearchInput = new DesignReviewMybatisParam.DsgnSearchInput();

        dsgnSearchInput.setRgstr( designReviewListGet.getRgstr() );
        dsgnSearchInput.setDsgnCd( designReviewListGet.getDsgnCd() );
        dsgnSearchInput.setKeyword( designReviewListGet.getKeyword() );
        dsgnSearchInput.setRgstrNm( designReviewListGet.getRgstrNm() );
        dsgnSearchInput.setMyRplyYn( designReviewListGet.getMyRplyYn() );
        dsgnSearchInput.setStartDsgnNo( designReviewListGet.getStartDsgnNo() );
        dsgnSearchInput.setEndDsgnNo( designReviewListGet.getEndDsgnNo() );
        dsgnSearchInput.setRplyCd( designReviewListGet.getRplyCd() );
        dsgnSearchInput.setApprerCd( designReviewListGet.getApprerCd() );
        dsgnSearchInput.setBackchkCd( designReviewListGet.getBackchkCd() );
        dsgnSearchInput.setStartRecentDt( designReviewListGet.getStartRecentDt() );
        dsgnSearchInput.setEndRecentDt( designReviewListGet.getEndRecentDt() );
        dsgnSearchInput.setStartRplyRecentDt( designReviewListGet.getStartRplyRecentDt() );
        dsgnSearchInput.setEndRplyRecentDt( designReviewListGet.getEndRplyRecentDt() );
        dsgnSearchInput.setIsuYn( designReviewListGet.getIsuYn() );
        dsgnSearchInput.setLesnYn( designReviewListGet.getLesnYn() );
        dsgnSearchInput.setAtachYn( designReviewListGet.getAtachYn() );
        dsgnSearchInput.setRplyStatus( designReviewListGet.getRplyStatus() );

        return dsgnSearchInput;
    }

    @Override
    public DmDesignReview toDesignReview(CreateUpdateDsgn designReview) {
        if ( designReview == null ) {
            return null;
        }

        DmDesignReview dmDesignReview = new DmDesignReview();

        dmDesignReview.setDsgnNo( designReview.getDsgnNo() );
        dmDesignReview.setCntrctNo( designReview.getCntrctNo() );
        dmDesignReview.setDsgnPhaseNo( designReview.getDsgnPhaseNo() );
        dmDesignReview.setTitle( designReview.getTitle() );
        dmDesignReview.setDsgnCd( designReview.getDsgnCd() );
        dmDesignReview.setDocNo( designReview.getDocNo() );
        dmDesignReview.setDwgNo( designReview.getDwgNo() );
        dmDesignReview.setDwgNm( designReview.getDwgNm() );
        dmDesignReview.setRvwOpnin( designReview.getRvwOpnin() );
        dmDesignReview.setIsuYn( designReview.getIsuYn() );
        dmDesignReview.setLesnYn( designReview.getLesnYn() );
        dmDesignReview.setRvwDwgNo( designReview.getRvwDwgNo() );
        dmDesignReview.setChgDwgNo( designReview.getChgDwgNo() );

        return dmDesignReview;
    }

    @Override
    public DmDwg toDmDwg(Dwg dwg) {
        if ( dwg == null ) {
            return null;
        }

        DmDwg dmDwg = new DmDwg();

        dmDwg.setDwgCd( dwg.getDwgCd() );
        dmDwg.setDwgDscrpt( dwg.getDwgDscrpt() );
        dmDwg.setSno( dwg.getSno() );

        return dmDwg;
    }

    @Override
    public void updateDesignReview(CreateUpdateDsgn updateDsgn, DmDesignReview oldDesignReview) {
        if ( updateDsgn == null ) {
            return;
        }

        if ( updateDsgn.getDsgnNo() != null ) {
            oldDesignReview.setDsgnNo( updateDsgn.getDsgnNo() );
        }
        if ( updateDsgn.getCntrctNo() != null ) {
            oldDesignReview.setCntrctNo( updateDsgn.getCntrctNo() );
        }
        if ( updateDsgn.getDsgnPhaseNo() != null ) {
            oldDesignReview.setDsgnPhaseNo( updateDsgn.getDsgnPhaseNo() );
        }
        if ( updateDsgn.getTitle() != null ) {
            oldDesignReview.setTitle( updateDsgn.getTitle() );
        }
        if ( updateDsgn.getDsgnCd() != null ) {
            oldDesignReview.setDsgnCd( updateDsgn.getDsgnCd() );
        }
        if ( updateDsgn.getDocNo() != null ) {
            oldDesignReview.setDocNo( updateDsgn.getDocNo() );
        }
        if ( updateDsgn.getDwgNo() != null ) {
            oldDesignReview.setDwgNo( updateDsgn.getDwgNo() );
        }
        if ( updateDsgn.getDwgNm() != null ) {
            oldDesignReview.setDwgNm( updateDsgn.getDwgNm() );
        }
        if ( updateDsgn.getRvwOpnin() != null ) {
            oldDesignReview.setRvwOpnin( updateDsgn.getRvwOpnin() );
        }
        if ( updateDsgn.getIsuYn() != null ) {
            oldDesignReview.setIsuYn( updateDsgn.getIsuYn() );
        }
        if ( updateDsgn.getLesnYn() != null ) {
            oldDesignReview.setLesnYn( updateDsgn.getLesnYn() );
        }
        if ( updateDsgn.getRvwDwgNo() != null ) {
            oldDesignReview.setRvwDwgNo( updateDsgn.getRvwDwgNo() );
        }
        if ( updateDsgn.getChgDwgNo() != null ) {
            oldDesignReview.setChgDwgNo( updateDsgn.getChgDwgNo() );
        }
    }
}
