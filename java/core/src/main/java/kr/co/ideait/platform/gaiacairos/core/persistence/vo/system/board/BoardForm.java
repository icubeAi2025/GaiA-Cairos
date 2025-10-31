package kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.board;

import kr.co.ideait.platform.gaiacairos.core.config.GlobalMapperConfig;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmBoard;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmBoardReception;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmPopupMsg;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.CommonForm;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(config = GlobalMapperConfig.class)
public interface BoardForm {

    BoardMybatisParam.BoardListInput toBoardListInput(BoardListGet boardListGet);

    BoardMybatisParam.BoardViewInput toBoardViewInput(BoardView boardView);

    SmBoard toBoard(Board Board);

    SmBoardReception toBoardReception(Board Board);

    SmPopupMsg toBoardPopupMsg(Board Board);

    // 수정용
    void toUpdateBoard(Board board, @MappingTarget SmBoard smBoard);

    // 검색용
    @Data
    @EqualsAndHashCode(callSuper = true)
    public class BoardListGet extends CommonForm {
        String pjtNo;
        String cntrctNo;
        String boardType;
        String boardCategory;
        String boardTitle;
        String boardTxt;
    }

    // 추가,수정용
    @Data
    class Board {
        Integer boardNo;
        String boardCd;
        String boardType;
        String boardCategory;
        String boardTitle;
        String boardTxt;
        String shareYn;

        // 게시글 수신
        String boardDiv;
        String pjtType;
        List<ReceptionItem> receptionList;
        String toDept;

        // 팝업창
        String popupYn;
        String popStartDt;
        String popEndDt;

        String dltYn;
        Integer boardView;
    }

    @Data
    public static class ReceptionItem {
        private String pjtNo;
        private String cntrctNo;
    }

    // 삭제용
    @Data
    class BoardList {
        List<String> boardList;
    }

    // 조회수 증가용
    @Data
    class BoardView {
        String boardCd;
        String boardDiv;
        String pjtType;
        String pjtNo;
        String cntrctNo;
    }
}
