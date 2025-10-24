package kr.co.ideait.platform.gaiacairos.core.persistence.vo.safety;

import javax.annotation.processing.Generated;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwSadtag;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:17+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class SadtagFormImpl implements SadtagForm {

    @Override
    public CwSadtag toEntity(Sadtag sadTag) {
        if ( sadTag == null ) {
            return null;
        }

        CwSadtag cwSadtag = new CwSadtag();

        cwSadtag.setFindDt( SadtagForm.stringToLocalDateTime( sadTag.getFindDt() ) );
        cwSadtag.setActnTmlmt( SadtagForm.stringToLocalDateTime( sadTag.getActnTmlmt() ) );
        cwSadtag.setActnDt( SadtagForm.stringToLocalDateTime( sadTag.getActnDt() ) );
        cwSadtag.setCntrctNo( sadTag.getCntrctNo() );
        cwSadtag.setSadtagNo( sadTag.getSadtagNo() );
        cwSadtag.setSadtagDocNo( sadTag.getSadtagDocNo() );
        cwSadtag.setDfccyTyp( sadTag.getDfccyTyp() );
        cwSadtag.setTitle( sadTag.getTitle() );
        cwSadtag.setFindId( sadTag.getFindId() );
        cwSadtag.setDfccyCntnts( sadTag.getDfccyCntnts() );
        cwSadtag.setDfccyLct( sadTag.getDfccyLct() );
        cwSadtag.setPstats( sadTag.getPstats() );
        cwSadtag.setActnId( sadTag.getActnId() );
        cwSadtag.setActnRslt( sadTag.getActnRslt() );

        return cwSadtag;
    }

    @Override
    public void updateSadtag(Sadtag sadtag, CwSadtag cwSadtag) {
        if ( sadtag == null ) {
            return;
        }

        if ( sadtag.getFindDt() != null ) {
            cwSadtag.setFindDt( SadtagForm.stringToLocalDateTime( sadtag.getFindDt() ) );
        }
        if ( sadtag.getActnTmlmt() != null ) {
            cwSadtag.setActnTmlmt( SadtagForm.stringToLocalDateTime( sadtag.getActnTmlmt() ) );
        }
        if ( sadtag.getActnDt() != null ) {
            cwSadtag.setActnDt( SadtagForm.stringToLocalDateTime( sadtag.getActnDt() ) );
        }
        if ( sadtag.getCntrctNo() != null ) {
            cwSadtag.setCntrctNo( sadtag.getCntrctNo() );
        }
        if ( sadtag.getSadtagNo() != null ) {
            cwSadtag.setSadtagNo( sadtag.getSadtagNo() );
        }
        if ( sadtag.getSadtagDocNo() != null ) {
            cwSadtag.setSadtagDocNo( sadtag.getSadtagDocNo() );
        }
        if ( sadtag.getDfccyTyp() != null ) {
            cwSadtag.setDfccyTyp( sadtag.getDfccyTyp() );
        }
        if ( sadtag.getTitle() != null ) {
            cwSadtag.setTitle( sadtag.getTitle() );
        }
        if ( sadtag.getFindId() != null ) {
            cwSadtag.setFindId( sadtag.getFindId() );
        }
        if ( sadtag.getDfccyCntnts() != null ) {
            cwSadtag.setDfccyCntnts( sadtag.getDfccyCntnts() );
        }
        if ( sadtag.getDfccyLct() != null ) {
            cwSadtag.setDfccyLct( sadtag.getDfccyLct() );
        }
        if ( sadtag.getPstats() != null ) {
            cwSadtag.setPstats( sadtag.getPstats() );
        }
        if ( sadtag.getActnId() != null ) {
            cwSadtag.setActnId( sadtag.getActnId() );
        }
        if ( sadtag.getActnRslt() != null ) {
            cwSadtag.setActnRslt( sadtag.getActnRslt() );
        }
    }
}
