package kr.co.ideait.platform.gaiacairos.web.entrypoint.construction;

import kr.co.ideait.platform.gaiacairos.comp.construction.ConstructResourceComponent;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.construction.resource.ResourceForm;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/construction")
public class ResourceApiController extends AbstractController {

    @Autowired
    ConstructResourceComponent resourceComponent;

    @Autowired
    ResourceForm resourceForm;

    @RequestMapping("/fail")
    public String falil() {
        throw new DataAccessResourceFailureException("강제 DB 예외 발생");
    }

    /**
     * 자원투입현황 (Monthly) 조회
     * @param resourceMain
     * @return
     */
    @PostMapping("/resource/resource-list")
    @Description(name = "자원 투입 현황 목록", description = "메인 화면 자원 투입 현황(QDB 자원) 목록을 조회한다.", type = Description.TYPE.MEHTOD)
    public Result getResourceList(CommonReqVo commonReqVo, @RequestBody ResourceForm.ResourceMain resourceMain) {

        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("자원투입현황 목록 조회");
        systemLogComponent.addUserLog(userLog);

        // 비즈니스 로직 시작
        String cntrctNo = resourceMain.getCntrctNo();
        String currentMonth = resourceMain.getCurrentMonth();
        String currentDay = resourceMain.getCurrentDay();
        String searchText = resourceMain.getSearchText();

        Map<String, Object> resourceMap
                = resourceComponent.selectResourceList(cntrctNo, currentMonth, currentDay, searchText);
        return Result.ok().put("resourceList", resourceMap);
    }



    /**
     * 자원투입현황 상세화면 (Monthly) 조회
     * @param resourceMain
     * @return
     */
    @PostMapping("/resource/detail/resource-monthly-list")
    @Description(name = "자원 투입 현황 월별 상세현황", description = "사용자가 선택한 월별 자원 투입 현황(QDB 자원) 상세를 조회한다.", type = Description.TYPE.MEHTOD)
    public Result getResourceMonthlyDetail(CommonReqVo commonReqVo, @RequestBody ResourceForm.ResourceMain resourceMain) {

        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("자원투입현황 상세(월별) 조회");
        systemLogComponent.addUserLog(userLog);

        // 비즈니스 로직 시작
        String cntrctNo = resourceMain.getCntrctNo();
        String rsceTpCd = resourceMain.getRsceTpCd();
        String currentMonth = resourceMain.getCurrentMonth();
        String searchText = resourceMain.getSearchText();

        Map<String, Object> resourceMap
                = resourceComponent.selectMonthlyResourceDetail(cntrctNo, rsceTpCd, currentMonth, searchText);
        return Result.ok().put("resourceList", resourceMap);

    }

    /**
     * 자원투입현황 상세화면 (Daily) 조회
     * @param resourceMain
     * @return
     */
    @PostMapping("/resource/detail/resource-daily-list")
    @Description(name = "자원 투입 현황 일별 상세현황", description = "사용자가 선택한 자원의 자원 투입 현황(QDB 자원) 상세를 조회한다.", type = Description.TYPE.MEHTOD)
    public Result getResourceDailyDetail(CommonReqVo commonReqVo, @RequestBody ResourceForm.ResourceMain resourceMain) {

        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("자원투입현황 상세(일별) 조회");
        systemLogComponent.addUserLog(userLog);

        // 비즈니스 로직 시작
        String cntrctNo = resourceMain.getCntrctNo();
        String rsceCd = resourceMain.getRsceCd();
        String searchText = resourceMain.getSearchText();

        Map<String, Object> resourceMap =
                resourceComponent.selectDailyResourceDetail(cntrctNo, rsceCd, searchText);
        return Result.ok().put("resourceList", resourceMap);
    }
}
