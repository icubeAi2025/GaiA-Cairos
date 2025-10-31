package kr.co.ideait.platform.gaiacairos.core.persistence.vo.eapproval.draft;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.ApDoc;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.ApLine;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:18+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class DraftFormImpl implements DraftForm {

    @Override
    public ApDoc toApDoc(DraftDoc draftDoc) {
        if ( draftDoc == null ) {
            return null;
        }

        ApDoc apDoc = new ApDoc();

        apDoc.setRgstrId( draftDoc.getRgstrId() );
        apDoc.setChgId( draftDoc.getChgId() );
        if ( draftDoc.getChgDt() != null ) {
            apDoc.setChgDt( LocalDateTime.parse( draftDoc.getChgDt() ) );
        }
        apDoc.setDltId( draftDoc.getDltId() );
        if ( draftDoc.getDltDt() != null ) {
            apDoc.setDltDt( LocalDateTime.parse( draftDoc.getDltDt() ) );
        }
        apDoc.setApDocNo( draftDoc.getApDocNo() );
        apDoc.setApDocId( draftDoc.getApDocId() );
        apDoc.setFrmNo( draftDoc.getFrmNo() );
        apDoc.setFrmId( draftDoc.getFrmId() );
        apDoc.setPjtNo( draftDoc.getPjtNo() );
        apDoc.setCntrctNo( draftDoc.getCntrctNo() );
        apDoc.setPjtType( draftDoc.getPjtType() );
        apDoc.setApDocTitle( draftDoc.getApDocTitle() );
        apDoc.setApDocEdtr( draftDoc.getApDocEdtr() );
        apDoc.setApDocTxt( draftDoc.getApDocTxt() );
        apDoc.setApUsrId( draftDoc.getApUsrId() );
        apDoc.setApLoginId( draftDoc.getApLoginId() );
        if ( draftDoc.getApAppDt() != null ) {
            apDoc.setApAppDt( LocalDateTime.parse( draftDoc.getApAppDt() ) );
        }
        if ( draftDoc.getApCmpltDt() != null ) {
            apDoc.setApCmpltDt( LocalDateTime.parse( draftDoc.getApCmpltDt() ) );
        }
        apDoc.setApDocStats( draftDoc.getApDocStats() );
        apDoc.setDltYn( draftDoc.getDltYn() );

        return apDoc;
    }

    @Override
    public DraftMybatisParam.SearchTemporaryList toTemporaryApDocSearch(TemporaryApDocSearch temporaryApDocSearch) {
        if ( temporaryApDocSearch == null ) {
            return null;
        }

        DraftMybatisParam.SearchTemporaryList searchTemporaryList = new DraftMybatisParam.SearchTemporaryList();

        searchTemporaryList.setFrmNo( temporaryApDocSearch.getFrmNo() );
        searchTemporaryList.setFrmId( temporaryApDocSearch.getFrmId() );
        searchTemporaryList.setApUsrId( temporaryApDocSearch.getApUsrId() );
        searchTemporaryList.setApLoginId( temporaryApDocSearch.getApLoginId() );
        searchTemporaryList.setPjtNo( temporaryApDocSearch.getPjtNo() );
        searchTemporaryList.setCntrctNo( temporaryApDocSearch.getCntrctNo() );
        searchTemporaryList.setPjtType( temporaryApDocSearch.getPjtType() );

        return searchTemporaryList;
    }

    @Override
    public ApLine toApLine(DraftApLine draftApLine) {
        if ( draftApLine == null ) {
            return null;
        }

        ApLine apLine = new ApLine();

        apLine.setRgstrId( draftApLine.getRgstrId() );
        apLine.setChgId( draftApLine.getChgId() );
        if ( draftApLine.getChgDt() != null ) {
            apLine.setChgDt( LocalDateTime.parse( draftApLine.getChgDt() ) );
        }
        apLine.setApNo( draftApLine.getApNo() );
        apLine.setApId( draftApLine.getApId() );
        apLine.setApDocNo( draftApLine.getApDocNo() );
        apLine.setApDocId( draftApLine.getApDocId() );
        apLine.setApOrder( (short) draftApLine.getApOrder() );
        apLine.setApDiv( draftApLine.getApDiv() );
        apLine.setApStats( draftApLine.getApStats() );
        apLine.setApUsrId( draftApLine.getApUsrId() );
        apLine.setApLoginId( draftApLine.getApLoginId() );
        apLine.setApUsrOpnin( draftApLine.getApUsrOpnin() );

        return apLine;
    }

    @Override
    public DraftMybatisParam.SearchAppLine toDraftLineSearch(DrafLineSearch drafLineSearch) {
        if ( drafLineSearch == null ) {
            return null;
        }

        DraftMybatisParam.SearchAppLine searchAppLine = new DraftMybatisParam.SearchAppLine();

        searchAppLine.setPjtNo( drafLineSearch.getPjtNo() );
        searchAppLine.setCntrctNo( drafLineSearch.getCntrctNo() );
        searchAppLine.setSearchText( drafLineSearch.getSearchText() );
        searchAppLine.setApCnrsRng( drafLineSearch.getApCnrsRng() );
        searchAppLine.setDeptType( drafLineSearch.getDeptType() );

        return searchAppLine;
    }

    @Override
    public List<ApLine> toApLineList(List<DraftApLine> apLineList) {
        if ( apLineList == null ) {
            return null;
        }

        List<ApLine> list = new ArrayList<ApLine>( apLineList.size() );
        for ( DraftApLine draftApLine : apLineList ) {
            list.add( toApLine( draftApLine ) );
        }

        return list;
    }

    @Override
    public List<DraftMybatisParam.DeleteTemporary> toDeleteList(List<TemporaryApDoc> deleteList) {
        if ( deleteList == null ) {
            return null;
        }

        List<DraftMybatisParam.DeleteTemporary> list = new ArrayList<DraftMybatisParam.DeleteTemporary>( deleteList.size() );
        for ( TemporaryApDoc temporaryApDoc : deleteList ) {
            list.add( temporaryApDocToDeleteTemporary( temporaryApDoc ) );
        }

        return list;
    }

    @Override
    public DraftMybatisParam.DraftFormTypeSelectInput toDraftFormTypeSelectInput(DraftMainFormList draftMainFormList) {
        if ( draftMainFormList == null ) {
            return null;
        }

        DraftMybatisParam.DraftFormTypeSelectInput draftFormTypeSelectInput = new DraftMybatisParam.DraftFormTypeSelectInput();

        draftFormTypeSelectInput.setPjtNo( draftMainFormList.getPjtNo() );
        draftFormTypeSelectInput.setCntrctNo( draftMainFormList.getCntrctNo() );
        draftFormTypeSelectInput.setPjtType( draftMainFormList.getPjtType() );

        return draftFormTypeSelectInput;
    }

    @Override
    public DraftMybatisParam.DraftFormListSelectInput toDraftFormListSelectInput(DraftMainFormList draftMainFormList) {
        if ( draftMainFormList == null ) {
            return null;
        }

        DraftMybatisParam.DraftFormListSelectInput draftFormListSelectInput = new DraftMybatisParam.DraftFormListSelectInput();

        draftFormListSelectInput.setPjtNo( draftMainFormList.getPjtNo() );
        draftFormListSelectInput.setCntrctNo( draftMainFormList.getCntrctNo() );
        draftFormListSelectInput.setPjtType( draftMainFormList.getPjtType() );
        draftFormListSelectInput.setUsrId( draftMainFormList.getUsrId() );

        return draftFormListSelectInput;
    }

    @Override
    public DraftMybatisParam.LatestDraftFormAndBokkmarkListSelectInput toLatestDraftFormAndBokkmarkListSelectInput(DraftMainFormList draftMainFormList) {
        if ( draftMainFormList == null ) {
            return null;
        }

        DraftMybatisParam.LatestDraftFormAndBokkmarkListSelectInput latestDraftFormAndBokkmarkListSelectInput = new DraftMybatisParam.LatestDraftFormAndBokkmarkListSelectInput();

        latestDraftFormAndBokkmarkListSelectInput.setPjtNo( draftMainFormList.getPjtNo() );
        latestDraftFormAndBokkmarkListSelectInput.setCntrctNo( draftMainFormList.getCntrctNo() );
        latestDraftFormAndBokkmarkListSelectInput.setPjtType( draftMainFormList.getPjtType() );
        latestDraftFormAndBokkmarkListSelectInput.setUsrId( draftMainFormList.getUsrId() );

        return latestDraftFormAndBokkmarkListSelectInput;
    }

    @Override
    public DraftMybatisParam.DraftFormListSelectInput toDraftFormListSelectInput(DraftFormList draftFormList) {
        if ( draftFormList == null ) {
            return null;
        }

        DraftMybatisParam.DraftFormListSelectInput draftFormListSelectInput = new DraftMybatisParam.DraftFormListSelectInput();

        draftFormListSelectInput.setPjtNo( draftFormList.getPjtNo() );
        draftFormListSelectInput.setCntrctNo( draftFormList.getCntrctNo() );
        draftFormListSelectInput.setPjtType( draftFormList.getPjtType() );
        draftFormListSelectInput.setUsrId( draftFormList.getUsrId() );
        draftFormListSelectInput.setSearchText( draftFormList.getSearchText() );
        draftFormListSelectInput.setSearchCheckBox( draftFormList.getSearchCheckBox() );

        return draftFormListSelectInput;
    }

    @Override
    public DraftMybatisParam.SelectBookmarkListInput toSelectBookmarkListInput(BookmarkList bookmarkList) {
        if ( bookmarkList == null ) {
            return null;
        }

        DraftMybatisParam.SelectBookmarkListInput selectBookmarkListInput = new DraftMybatisParam.SelectBookmarkListInput();

        selectBookmarkListInput.setPjtNo( bookmarkList.getPjtNo() );
        selectBookmarkListInput.setCntrctNo( bookmarkList.getCntrctNo() );
        selectBookmarkListInput.setUsrId( bookmarkList.getUsrId() );

        return selectBookmarkListInput;
    }

    protected DraftMybatisParam.DeleteTemporary temporaryApDocToDeleteTemporary(TemporaryApDoc temporaryApDoc) {
        if ( temporaryApDoc == null ) {
            return null;
        }

        DraftMybatisParam.DeleteTemporary deleteTemporary = new DraftMybatisParam.DeleteTemporary();

        deleteTemporary.setApDocId( temporaryApDoc.getApDocId() );
        deleteTemporary.setUserId( temporaryApDoc.getUserId() );
        deleteTemporary.setApType( temporaryApDoc.getApType() );

        return deleteTemporary;
    }
}
