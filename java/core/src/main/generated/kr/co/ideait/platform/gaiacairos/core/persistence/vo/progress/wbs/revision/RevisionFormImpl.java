package kr.co.ideait.platform.gaiacairos.core.persistence.vo.progress.wbs.revision;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.PrRevision;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:18+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class RevisionFormImpl implements RevisionForm {

    @Override
    public RevisionMybatisParam.RevisionListInput toRevisionListInput(RevisionList revisionList) {
        if ( revisionList == null ) {
            return null;
        }

        RevisionMybatisParam.RevisionListInput revisionListInput = new RevisionMybatisParam.RevisionListInput();

        revisionListInput.setPjtNo( revisionList.getPjtNo() );
        revisionListInput.setCntrctNo( revisionList.getCntrctNo() );
        revisionListInput.setSearchText( revisionList.getSearchText() );

        return revisionListInput;
    }

    @Override
    public List<RevisionMybatisParam.DeleteRevisionInput> toDeleteRevisionInput(List<RevisionDelete> revisionDeleteList) {
        if ( revisionDeleteList == null ) {
            return null;
        }

        List<RevisionMybatisParam.DeleteRevisionInput> list = new ArrayList<RevisionMybatisParam.DeleteRevisionInput>( revisionDeleteList.size() );
        for ( RevisionDelete revisionDelete : revisionDeleteList ) {
            list.add( revisionDeleteToDeleteRevisionInput( revisionDelete ) );
        }

        return list;
    }

    @Override
    public void updatePrRevision(RevisionUpdate revisionUpdate, PrRevision PrRevision) {
        if ( revisionUpdate == null ) {
            return;
        }

        PrRevision.setCntrctChgId( revisionUpdate.getCntrctChgId() );
        PrRevision.setRevisionId( revisionUpdate.getRevisionId() );
        PrRevision.setEpsId( revisionUpdate.getEpsId() );
        PrRevision.setEpsNm( revisionUpdate.getEpsNm() );
        PrRevision.setP6ProjectId( revisionUpdate.getP6ProjectId() );
        PrRevision.setP6ProjectNm( revisionUpdate.getP6ProjectNm() );
        PrRevision.setLastRevisionYn( revisionUpdate.getLastRevisionYn() );
        PrRevision.setRmrk( revisionUpdate.getRmrk() );
        PrRevision.setChgId( revisionUpdate.getChgId() );
        PrRevision.setChgDt( revisionUpdate.getChgDt() );
        if ( revisionUpdate.getP6ProjectObjId() != null ) {
            PrRevision.setP6ProjectObjId( Integer.parseInt( revisionUpdate.getP6ProjectObjId() ) );
        }
        else {
            PrRevision.setP6ProjectObjId( null );
        }
    }

    protected RevisionMybatisParam.DeleteRevisionInput revisionDeleteToDeleteRevisionInput(RevisionDelete revisionDelete) {
        if ( revisionDelete == null ) {
            return null;
        }

        RevisionMybatisParam.DeleteRevisionInput deleteRevisionInput = new RevisionMybatisParam.DeleteRevisionInput();

        deleteRevisionInput.setCntrctChgId( revisionDelete.getCntrctChgId() );
        deleteRevisionInput.setRevisionId( revisionDelete.getRevisionId() );
        deleteRevisionInput.setDltId( revisionDelete.getDltId() );

        return deleteRevisionInput;
    }
}
