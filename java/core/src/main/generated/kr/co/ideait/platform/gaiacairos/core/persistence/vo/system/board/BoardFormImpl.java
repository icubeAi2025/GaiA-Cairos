package kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.board;

import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmBoard;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmBoardReception;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmPopupMsg;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:18+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class BoardFormImpl implements BoardForm {

    @Override
    public BoardMybatisParam.BoardListInput toBoardListInput(BoardListGet boardListGet) {
        if ( boardListGet == null ) {
            return null;
        }

        BoardMybatisParam.BoardListInput boardListInput = new BoardMybatisParam.BoardListInput();

        boardListInput.setPageable( boardListGet.getPageable() );
        boardListInput.setBoardType( boardListGet.getBoardType() );
        boardListInput.setBoardCategory( boardListGet.getBoardCategory() );
        boardListInput.setBoardTitle( boardListGet.getBoardTitle() );
        boardListInput.setBoardTxt( boardListGet.getBoardTxt() );
        boardListInput.setSearchType( boardListGet.getSearchType() );
        boardListInput.setSearchText( boardListGet.getSearchText() );
        boardListInput.setPjtNo( boardListGet.getPjtNo() );
        boardListInput.setCntrctNo( boardListGet.getCntrctNo() );

        return boardListInput;
    }

    @Override
    public BoardMybatisParam.BoardViewInput toBoardViewInput(BoardView boardView) {
        if ( boardView == null ) {
            return null;
        }

        BoardMybatisParam.BoardViewInput boardViewInput = new BoardMybatisParam.BoardViewInput();

        boardViewInput.setBoardCd( boardView.getBoardCd() );
        boardViewInput.setBoardDiv( boardView.getBoardDiv() );
        boardViewInput.setPjtType( boardView.getPjtType() );
        boardViewInput.setPjtNo( boardView.getPjtNo() );
        boardViewInput.setCntrctNo( boardView.getCntrctNo() );

        return boardViewInput;
    }

    @Override
    public SmBoard toBoard(Board Board) {
        if ( Board == null ) {
            return null;
        }

        SmBoard smBoard = new SmBoard();

        smBoard.setBoardNo( Board.getBoardNo() );
        smBoard.setBoardCd( Board.getBoardCd() );
        smBoard.setBoardType( Board.getBoardType() );
        smBoard.setBoardCategory( Board.getBoardCategory() );
        smBoard.setBoardTitle( Board.getBoardTitle() );
        smBoard.setBoardTxt( Board.getBoardTxt() );
        smBoard.setShareYn( Board.getShareYn() );
        smBoard.setDltYn( Board.getDltYn() );

        return smBoard;
    }

    @Override
    public SmBoardReception toBoardReception(Board Board) {
        if ( Board == null ) {
            return null;
        }

        SmBoardReception smBoardReception = new SmBoardReception();

        smBoardReception.setBoardCd( Board.getBoardCd() );
        smBoardReception.setBoardDiv( Board.getBoardDiv() );
        smBoardReception.setPjtType( Board.getPjtType() );
        smBoardReception.setToDept( Board.getToDept() );
        smBoardReception.setBoardView( Board.getBoardView() );
        smBoardReception.setDltYn( Board.getDltYn() );

        return smBoardReception;
    }

    @Override
    public SmPopupMsg toBoardPopupMsg(Board Board) {
        if ( Board == null ) {
            return null;
        }

        SmPopupMsg smPopupMsg = new SmPopupMsg();

        smPopupMsg.setPjtType( Board.getPjtType() );
        smPopupMsg.setToDept( Board.getToDept() );
        if ( Board.getPopStartDt() != null ) {
            smPopupMsg.setPopStartDt( LocalDateTime.parse( Board.getPopStartDt() ) );
        }
        if ( Board.getPopEndDt() != null ) {
            smPopupMsg.setPopEndDt( LocalDateTime.parse( Board.getPopEndDt() ) );
        }
        smPopupMsg.setShareYn( Board.getShareYn() );
        smPopupMsg.setDltYn( Board.getDltYn() );

        return smPopupMsg;
    }

    @Override
    public void toUpdateBoard(Board board, SmBoard smBoard) {
        if ( board == null ) {
            return;
        }

        if ( board.getBoardNo() != null ) {
            smBoard.setBoardNo( board.getBoardNo() );
        }
        if ( board.getBoardCd() != null ) {
            smBoard.setBoardCd( board.getBoardCd() );
        }
        if ( board.getBoardType() != null ) {
            smBoard.setBoardType( board.getBoardType() );
        }
        if ( board.getBoardCategory() != null ) {
            smBoard.setBoardCategory( board.getBoardCategory() );
        }
        if ( board.getBoardTitle() != null ) {
            smBoard.setBoardTitle( board.getBoardTitle() );
        }
        if ( board.getBoardTxt() != null ) {
            smBoard.setBoardTxt( board.getBoardTxt() );
        }
        if ( board.getShareYn() != null ) {
            smBoard.setShareYn( board.getShareYn() );
        }
        if ( board.getDltYn() != null ) {
            smBoard.setDltYn( board.getDltYn() );
        }
    }
}
