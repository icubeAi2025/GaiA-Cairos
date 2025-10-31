package kr.co.ideait.platform.gaiacairos.core.persistence.vo.eapproval.approval;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.ApLine;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.ApShare;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:17+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class ApprovalFormImpl implements ApprovalForm {

    @Override
    public ApprovalMybatisParam.ApprovalListInput toApprovalListInput(ApprovalList approvalList) {
        if ( approvalList == null ) {
            return null;
        }

        ApprovalMybatisParam.ApprovalListInput approvalListInput = new ApprovalMybatisParam.ApprovalListInput();

        approvalListInput.setPageable( approvalList.getPageable() );
        approvalListInput.setData( approvalList.getData() );
        approvalListInput.setKeyword( approvalList.getKeyword() );
        approvalListInput.setApUsrId( approvalList.getApUsrId() );
        approvalListInput.setApLoginId( approvalList.getApLoginId() );
        approvalListInput.setApDocTitle( approvalList.getApDocTitle() );
        approvalListInput.setApDocTxt( approvalList.getApDocTxt() );
        approvalListInput.setStartAppDt( approvalList.getStartAppDt() );
        approvalListInput.setEndAppDt( approvalList.getEndAppDt() );
        approvalListInput.setStartCmpltDt( approvalList.getStartCmpltDt() );
        approvalListInput.setEndCmpltDt( approvalList.getEndCmpltDt() );
        approvalListInput.setPjtNo( approvalList.getPjtNo() );
        approvalListInput.setCntrctNo( approvalList.getCntrctNo() );
        approvalListInput.setPjtType( approvalList.getPjtType() );
        List<String> list = approvalList.getSelectedApType();
        if ( list != null ) {
            approvalListInput.setSelectedApType( new ArrayList<String>( list ) );
        }
        approvalListInput.setSelectedStatus( approvalList.getSelectedStatus() );
        approvalListInput.setSelectedForm( approvalList.getSelectedForm() );

        return approvalListInput;
    }

    @Override
    public ApprovalMybatisParam.ApproveListInput toApproveListInput(ApproveList approveList) {
        if ( approveList == null ) {
            return null;
        }

        ApprovalMybatisParam.ApproveListInput approveListInput = new ApprovalMybatisParam.ApproveListInput();

        approveListInput.setApStats( approveList.getApStats() );
        approveListInput.setApproveDocList( approveDocListToApLineUpdateList( approveList.getApproveDocList() ) );

        return approveListInput;
    }

    @Override
    public ApprovalMybatisParam.ApproveOneInput toApproveOneInput(ApproveOne approveOne) {
        if ( approveOne == null ) {
            return null;
        }

        ApprovalMybatisParam.ApproveOneInput approveOneInput = new ApprovalMybatisParam.ApproveOneInput();

        approveOneInput.setApStats( approveOne.getApStats() );
        approveOneInput.setApLine( apLineToApLineUpdate( approveOne.getApLine() ) );
        List<ApShare> list = approveOne.getApShareList();
        if ( list != null ) {
            approveOneInput.setApShareList( new ArrayList<ApShare>( list ) );
        }
        List<ApShare> list1 = approveOne.getDelShareList();
        if ( list1 != null ) {
            approveOneInput.setDelShareList( new ArrayList<ApShare>( list1 ) );
        }

        return approveOneInput;
    }

    protected ApprovalMybatisParam.ApLineUpdate approveDocToApLineUpdate(ApproveDoc approveDoc) {
        if ( approveDoc == null ) {
            return null;
        }

        ApprovalMybatisParam.ApLineUpdate apLineUpdate = new ApprovalMybatisParam.ApLineUpdate();

        apLineUpdate.setApDocNo( approveDoc.getApDocNo() );
        apLineUpdate.setApDocId( approveDoc.getApDocId() );
        apLineUpdate.setApUsrId( approveDoc.getApUsrId() );
        apLineUpdate.setApStats( approveDoc.getApStats() );
        apLineUpdate.setApDocStats( approveDoc.getApDocStats() );
        apLineUpdate.setApUsrOpnin( approveDoc.getApUsrOpnin() );
        apLineUpdate.setApType( approveDoc.getApType() );

        return apLineUpdate;
    }

    protected List<ApprovalMybatisParam.ApLineUpdate> approveDocListToApLineUpdateList(List<ApproveDoc> list) {
        if ( list == null ) {
            return null;
        }

        List<ApprovalMybatisParam.ApLineUpdate> list1 = new ArrayList<ApprovalMybatisParam.ApLineUpdate>( list.size() );
        for ( ApproveDoc approveDoc : list ) {
            list1.add( approveDocToApLineUpdate( approveDoc ) );
        }

        return list1;
    }

    protected ApprovalMybatisParam.ApLineUpdate apLineToApLineUpdate(ApLine apLine) {
        if ( apLine == null ) {
            return null;
        }

        ApprovalMybatisParam.ApLineUpdate apLineUpdate = new ApprovalMybatisParam.ApLineUpdate();

        apLineUpdate.setApDocNo( apLine.getApDocNo() );
        apLineUpdate.setApDocId( apLine.getApDocId() );
        apLineUpdate.setApUsrId( apLine.getApUsrId() );
        apLineUpdate.setApStats( apLine.getApStats() );
        apLineUpdate.setApUsrOpnin( apLine.getApUsrOpnin() );

        return apLineUpdate;
    }
}
