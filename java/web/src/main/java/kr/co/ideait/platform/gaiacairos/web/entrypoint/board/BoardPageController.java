package kr.co.ideait.platform.gaiacairos.web.entrypoint.board;

import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 포탈 로그인 페이지 (테스트용/ 삭제예정)
 */
@Slf4j
@Controller
@RequestMapping("/board")
public class BoardPageController extends AbstractController {


    @GetMapping("/readMain")
    @Description(name = "메인화면 게시글 조회 화면", description = "메인화면의 공지사항,FAQ 조회 화면 반환.", type = Description.TYPE.MEHTOD)
    public String boardread(CommonReqVo commonReqVo) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("메인화면 게시글 조회 화면 접속");
        systemLogComponent.addUserLog(userLog);
        return "page/board/mainboard";
    }

    @GetMapping("/listReadMain")
    @Description(name = "게시판관리 목록 조회 화면", description = "메인화면의 공지사항,FAQ 목록 조회 화면 반환.", type = Description.TYPE.MEHTOD)
    public String boardListRead(CommonReqVo commonReqVo) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("메인화면 게시글 목록 화면 접속");
        systemLogComponent.addUserLog(userLog);
        return "page/board/mainboardList";
    }

}
