package kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.board;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmAttachments;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmBoard;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:16+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class BoardDtoImpl implements BoardDto {

    @Override
    public Board fromSmBoard(SmBoard smBoard) {
        if ( smBoard == null ) {
            return null;
        }

        Board board = new Board();

        board.setBoardNo( smBoard.getBoardNo() );
        board.setBoardType( smBoard.getBoardType() );
        board.setBoardCategory( smBoard.getBoardCategory() );
        board.setBoardTitle( smBoard.getBoardTitle() );
        board.setShareYn( smBoard.getShareYn() );
        board.setBoardTxt( smBoard.getBoardTxt() );
        board.setDltYn( smBoard.getDltYn() );
        if ( smBoard.getRgstDt() != null ) {
            board.setRgstDt( DateTimeFormatter.ISO_LOCAL_DATE_TIME.format( smBoard.getRgstDt() ) );
        }

        return board;
    }

    @Override
    public Board toBoard(BoardMybatisParam.BoardOutput smboard) {
        if ( smboard == null ) {
            return null;
        }

        Board board = new Board();

        board.setBoardNo( smboard.getBoardNo() );
        board.setBoardType( smboard.getBoardType() );
        board.setBoardCategory( smboard.getBoardCategory() );
        board.setBoardTitle( smboard.getBoardTitle() );
        board.setShareYn( smboard.getShareYn() );
        board.setBoardTxt( smboard.getBoardTxt() );
        board.setDltYn( smboard.getDltYn() );
        board.setRgstDt( smboard.getRgstDt() );
        board.setBoardView( smboard.getBoardView() );
        board.setBoardDiv( smboard.getBoardDiv() );
        board.setToDept( smboard.getToDept() );
        board.setUsrNm( smboard.getUsrNm() );
        board.setLoginId( smboard.getLoginId() );
        board.setTotalNum( smboard.getTotalNum() );

        return board;
    }

    @Override
    public List<boardAttachMent> toboardAttachments(List<SmAttachments> attachments) {
        if ( attachments == null ) {
            return null;
        }

        List<boardAttachMent> list = new ArrayList<boardAttachMent>( attachments.size() );
        for ( SmAttachments smAttachments : attachments ) {
            list.add( smAttachmentsToboardAttachMent( smAttachments ) );
        }

        return list;
    }

    protected boardAttachMent smAttachmentsToboardAttachMent(SmAttachments smAttachments) {
        if ( smAttachments == null ) {
            return null;
        }

        boardAttachMent boardAttachMent = new boardAttachMent();

        boardAttachMent.setFileNo( smAttachments.getFileNo() );
        boardAttachMent.setFileOrgNm( smAttachments.getFileOrgNm() );
        boardAttachMent.setFileDiskNm( smAttachments.getFileDiskNm() );
        boardAttachMent.setFileDiskPath( smAttachments.getFileDiskPath() );
        boardAttachMent.setFileSize( smAttachments.getFileSize() );
        boardAttachMent.setDltYn( smAttachments.getDltYn() );

        return boardAttachMent;
    }
}
