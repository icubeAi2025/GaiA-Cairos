package kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.board;

import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.board.BoardDto.Board;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.board.BoardDto.boardAttachMent;
import lombok.Data;

import java.util.List;

@Data
public class BoardWithFilesDTO {
    private Board board;
    private List<boardAttachMent> attachments;
}
