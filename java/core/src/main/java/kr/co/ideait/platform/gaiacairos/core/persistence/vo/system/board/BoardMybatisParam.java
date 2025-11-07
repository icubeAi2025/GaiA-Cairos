package kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.board;

import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisPageable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

import java.time.LocalDateTime;
import java.util.List;

public interface BoardMybatisParam {

    @Data
    @Alias("boardListInput")
    @EqualsAndHashCode(callSuper = true)
    public class BoardListInput extends MybatisPageable {
        String boardType;
        String boardCategory;
        String boardTitle;
        String boardTxt;

        String searchType;
        String searchText;
        String userId;
        String userType;
        String systemType;
        String pjtNo;
        String cntrctNo;
        String pageType;

        List<String> pjtNoList;
        List<String> cntrctNoList;
    }

    @Data
    @Alias("boardInput")
    public class BoardInput {
        Integer boardNo;
        String boardCd;
        String boardType;
        String boardCategory;
        String boardTitle;
        String boardTxt;
        String shareYn;

        String boardDiv;
        String pjtType;
        List<String> pjtNoList;
        List<String> cntrctNoList;
        String toDept;

        String dltYn;
        Integer boardView;

    }

    @Data
    @Alias("boardViewInput")
    public class BoardViewInput {
        String boardCd;
        String boardDiv;
        String pjtType;
        String pjtNo;
        String cntrctNo;
    }

    @Data
    @Alias("boardOutput")
    @EqualsAndHashCode(callSuper = true)
    public class BoardOutput extends MybatisPageable {
        Integer boardNo;
        String boardCd;
        String boardType;
        String boardCategory;
        String boardTitle;
        String shareYn;
        String boardTxt;
        String dltYn;
        String rgstDt;

        Integer boardView;
        String boardDiv;
        String pjtType;
        String pjtNo;
        String cntrctNo;
        String toDept;

        String usrNm;
        String loginId;
        Long totalNum;

        Integer rownum;

        // 팝업창
        String popupYn;
        LocalDateTime popStartDt;
        LocalDateTime popEndDt;
    }
}
