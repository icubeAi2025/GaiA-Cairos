package kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.board;

import kr.co.ideait.platform.gaiacairos.core.config.GlobalMapperConfig;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmAttachments;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmBoard;
import lombok.Data;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = GlobalMapperConfig.class)
public interface BoardDto {

    // 수정용
    Board fromSmBoard(SmBoard smBoard);

    // 조회용
    Board toBoard(BoardMybatisParam.BoardOutput smboard);

    List<boardAttachMent> toboardAttachments(List<SmAttachments> attachments);

    @Data
    class Board {
        Integer boardNo;
        String boardType;
        String boardCategory;
        String boardTitle;
        String shareYn;
        String boardTxt;
        String dltYn;
        String rgstDt;

        Integer boardView;
        String boardDiv;
        List<String> pjtNoList;
        List<String> cntrctNoList;
        String toDept;

        String usrNm;
        String loginId;
        Long totalNum;
    }

    @Data
    class boardAttachMent {
        Integer fileNo;
        Integer boardNo;
        String fileOrgNm;
        String fileDiskNm;
        String fileDiskPath;
        Integer fileSize;
        String dltYn;
    }

}
