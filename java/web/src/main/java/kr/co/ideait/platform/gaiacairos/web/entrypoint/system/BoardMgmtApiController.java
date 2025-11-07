package kr.co.ideait.platform.gaiacairos.web.entrypoint.system;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.comp.system.BoardComponent;
import kr.co.ideait.platform.gaiacairos.comp.system.service.BoardService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmAttachments;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmBoard;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmBoardReception;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmPopupMsg;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.GridResult;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.board.BoardDto;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.board.BoardForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.board.BoardMybatisParam.BoardListInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.board.BoardMybatisParam.BoardOutput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.board.BoardWithFilesDTO;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/system/board")
public class BoardMgmtApiController extends AbstractController {

    @Autowired
    BoardService boardService;

    @Autowired
    BoardComponent boardComponent;

    @Autowired
    BoardForm boardForm;

    @Autowired
    BoardDto boardDto;

    /**
     * 게시판 목록
     */
    @GetMapping("/list")
    @Description(name = "게시판 목록 조회", description = "게시판관리의 게시글 목록 조회", type = Description.TYPE.MEHTOD)
    public GridResult getBoardList(CommonReqVo commonReqVo, @Valid BoardForm.BoardListGet boardListGet,
                                   HttpServletRequest request) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("게시판 목록 조회");
        systemLogComponent.addUserLog(userLog);

        String[] userParam = commonReqVo.getUserParam();

        BoardListInput input = boardForm.toBoardListInput(boardListGet);
        input.setUserId(userParam[3]);
        input.setUserType(userParam[1]);

        return GridResult
                .ok(boardService.getBoardList(input));
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
                .put("updateBoardData", dto)
                .put("popupData", popupData)
                .put("receptionList", boardService.getSmBoardReceptionList(smboard.getBoardCd()));
    }

    /**
     * 게시글 생성
     */
    @PostMapping("/create")
    @Description(name = "게시글 추가", description = "게시글 추가", type = Description.TYPE.MEHTOD)
    public Result createBoard(CommonReqVo commonReqVo, @RequestPart("data") BoardForm.Board board,
                              @RequestPart(value = "files", required = false) List<MultipartFile> files, HttpServletRequest request) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("게시글 추가");
        systemLogComponent.addUserLog(userLog);

        if(boardComponent.createBoard(board, files, commonReqVo)){
            return Result.ok();
        }

        return Result.nok(ErrorType.ETC,"Logical Issue");
    }

    /**
     * 게시글 수정
     */
    @PostMapping("/update")
    @Description(name = "게시글 수정", description = "게시글 수정", type = Description.TYPE.MEHTOD)
    public Result updateBoard(CommonReqVo commonReqVo, @RequestPart("data") BoardForm.Board board,
                              @RequestPart(value = "files", required = false) List<MultipartFile> files, HttpServletRequest request) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("게시글 수정");
        systemLogComponent.addUserLog(userLog);

        String[] userParam = commonReqVo.getUserParam();

        SmBoard smBoard = boardService.getBoard(board.getBoardNo());
        List<SmBoardReception> receptionList = new ArrayList<>();
        List<SmPopupMsg> popupList = new ArrayList<>();
        String preShareYn = "";

        if (smBoard != null) { // 데이터가 있는지 확인
            preShareYn = smBoard.getShareYn();
            boardForm.toUpdateBoard(board, smBoard);

            if ("1".equals(board.getBoardDiv())) {
                SmBoardReception smBoardReception = boardForm.toBoardReception(board);
                smBoardReception.setPjtType(userParam[2]);
                smBoardReception.setBoardView(0);
                receptionList.add(smBoardReception);

                if (("Y").equals(board.getPopupYn())) {
                    SmPopupMsg smPopupMsg = boardForm.toBoardPopupMsg(board);
                    smPopupMsg.setPopDiv(board.getBoardDiv());
                    smPopupMsg.setPjtType(userParam[2]);
                    popupList.add(smPopupMsg);
                }
            } else {
                for (BoardForm.ReceptionItem item : board.getReceptionList()) {
                    SmBoardReception smBoardReception = boardForm.toBoardReception(board);
                    smBoardReception.setPjtType(userParam[2]);
                    smBoardReception.setBoardView(0);
                    smBoardReception.setPjtNo(item.getPjtNo());
                    if ("3".equals(board.getBoardDiv())) {
                        smBoardReception.setCntrctNo(item.getCntrctNo());
                    }
                    receptionList.add(smBoardReception);

                    if (("Y").equals(board.getPopupYn())) {
                        SmPopupMsg smPopupMsg = boardForm.toBoardPopupMsg(board);
                        smPopupMsg.setPopDiv(board.getBoardDiv());
                        smPopupMsg.setPjtType(userParam[2]);
                        smPopupMsg.setPjtNo(item.getPjtNo());
                        if ("3".equals(board.getBoardDiv())) {
                            smPopupMsg.setCntrctNo(item.getCntrctNo());
                        }
                        popupList.add(smPopupMsg);
                    }
                }
            }
        }

        return Result.ok()
                .put("board", boardComponent.updateBoard(smBoard, receptionList, popupList, files, preShareYn, commonReqVo.getPjtDiv() ,commonReqVo.getApiYn())
                        .map(boardDto::fromSmBoard));
    }

    /**
     * 프로젝트 List 가져오기
     */
    @PostMapping("/projectList")
    @Description(name = "프로젝트 목록 조회", description = "계정별 프로젝트 목록 조회", type = Description.TYPE.MEHTOD)
    public Result getProjectList(CommonReqVo commonReqVo, HttpServletRequest request) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("프로젝트 목록 조회");
        systemLogComponent.addUserLog(userLog);

        String[] userParam = commonReqVo.getUserParam();

        MybatisInput input = new MybatisInput();
        input.add("userId", userParam[3]);

        return Result.ok()
                .put("projectList", boardService.getProjectList(userParam[1], input));
    }

    /**
     * 계약 List 가져오기
     */
    @PostMapping("/cntrctList")
    @Description(name = "계약 목록 조회", description = "프로젝트의 계약 목록 조회", type = Description.TYPE.MEHTOD)
    public Result getCntrctList(CommonReqVo commonReqVo, @RequestParam(value = "pjtNoList", required = false) List<String> pjtNoList,
                                HttpServletRequest request) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("계약 목록 조회");
        systemLogComponent.addUserLog(userLog);

        String[] userParam = commonReqVo.getUserParam();

        MybatisInput input = new MybatisInput();
        input.add("userId", userParam[3]);

        return Result.ok().put("cntrctList",
                boardService.getCntrctList(userParam[1], pjtNoList, input));
    }

    /*
     * 게시글 삭제
     */
    @PostMapping("/delete")
    @Description(name = "게시글 삭제", description = "게시글 삭제", type = Description.TYPE.MEHTOD)
    public Result deleteBoard(CommonReqVo commonReqVo, @RequestBody @Valid BoardForm.BoardList boardList) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("게시글 삭제");
        systemLogComponent.addUserLog(userLog);

        if(boardComponent.deleteBoard(boardList.getBoardList(), commonReqVo.getPjtDiv(), commonReqVo.getUserId(), commonReqVo.getApiYn())){
            return Result.ok();
        }
        return Result.nok(ErrorType.ETC,"Logical Issue");
    }
}
