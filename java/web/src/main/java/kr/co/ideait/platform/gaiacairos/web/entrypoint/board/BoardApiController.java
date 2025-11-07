package kr.co.ideait.platform.gaiacairos.web.entrypoint.board;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.comp.common.service.MainBoardService;
import kr.co.ideait.platform.gaiacairos.comp.system.BoardComponent;
import kr.co.ideait.platform.gaiacairos.comp.system.service.BoardService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmAttachments;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.GridResult;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.board.BoardDto;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.board.BoardForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.board.BoardMybatisParam.BoardListInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.board.BoardMybatisParam.BoardOutput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.board.BoardMybatisParam.BoardViewInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.board.BoardWithFilesDTO;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping({"/api/board"})
public class BoardApiController extends AbstractController {

    @Autowired
    MainBoardService boardService;

    @Autowired
    BoardForm boardForm;

    @Autowired
    BoardDto boardDto;

    /**
     * 메인화면 faq게시판 목록
     */
    @GetMapping("/mainFaqList")
    @Description(name = "FAQ 목록 조회", description = "메인화면의 FAQ 목록 조회", type = Description.TYPE.MEHTOD)
    public Result getMainFaqBoardList(CommonReqVo commonReqVo, @Valid BoardForm.BoardListGet boardListGet,
                                      HttpServletRequest request) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("FAQ 목록 조회");
        systemLogComponent.addUserLog(userLog);


        String[] userParam = commonReqVo.getUserParam();

        BoardListInput input = boardForm.toBoardListInput(boardListGet);
        input.setUserId(userParam[3]);
        input.setUserType(userParam[1]);
        input.setSystemType(userParam[2]);

        return Result
                .ok().put("boardList", boardService.getMainBoardList(input));
    }

    /**
     * 메인화면 게시판 목록
     */
    @GetMapping("/mainBoardList")
    @Description(name = "게시판 목록 조회", description = "메인화면의 게시판 목록 조회", type = Description.TYPE.MEHTOD)
    public GridResult getMainNoticeBoardList(CommonReqVo commonReqVo, @Valid BoardForm.BoardListGet boardListGet,
                                             HttpServletRequest requests) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("게시판 목록 조회");
        systemLogComponent.addUserLog(userLog);


        String[] userParam = commonReqVo.getUserParam();
        BoardListInput input = boardForm.toBoardListInput(boardListGet);
        input.setUserId(userParam[3]);
        input.setUserType(userParam[1]);
        input.setSystemType(userParam[2]);
        input.setPageType("list");

        return GridResult
                .ok(boardService.getMainBoardReadList(input));
    }

    // 게시글 조회 기본 데이터
    @GetMapping("/updateData/{boardNo}")
    @Description(name = "게시글 상세조회", description = "게시글 상세조회", type = Description.TYPE.MEHTOD)
    public Result getUpdateData(CommonReqVo commonReqVo, @PathVariable("boardNo") Integer boardNo) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("게시글 상세조회");
        systemLogComponent.addUserLog(userLog);

        BoardOutput smboard = boardService.getUpdateData(boardNo);
        BoardWithFilesDTO dto = new BoardWithFilesDTO();
        dto.setBoard(boardDto.toBoard(smboard));

        Map<String, Object> popupData = new HashMap<>();
        popupData.put("popupYn", smboard.getPopupYn());
        popupData.put("popStartDt", smboard.getPopStartDt());
        popupData.put("popEndDt", smboard.getPopEndDt());

        List<SmAttachments> attachments = boardService.getSmAttachments(smboard.getBoardCd());
        dto.setAttachments(boardDto.toboardAttachments(attachments));

        return Result.ok()
                .put("updateBoardData", dto);
    }

    /*
     * 게시글 조회수 증가
     */
    @PostMapping("/updateView")
    @Description(name = "게시글 조회수 증가", description = "게시글 조회수 증가", type = Description.TYPE.MEHTOD)
    public Result updateView(CommonReqVo commonReqVo, @RequestBody @Valid BoardForm.BoardView boardView) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("게시글 조회수 증가");
        systemLogComponent.addUserLog(userLog);

        BoardViewInput input = boardForm.toBoardViewInput(boardView);
        boardService.updateView(input);
        return Result.ok();
    }

}
