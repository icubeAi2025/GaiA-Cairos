package kr.co.ideait.platform.gaiacairos.web.entrypoint.defecttracking;

import jakarta.validation.Valid;
import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.comp.defecttracking.DefectTrackingComponent;
import kr.co.ideait.platform.gaiacairos.comp.defecttracking.SummaryComponent;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.GridResult;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.report.summary.SummaryForm.SummaryListGet;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.defecttracking.tool.defecttracking.DefectTrackingForm.DfccyPhaseListGet;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("api/defecttracking/summary")
public class SummaryApiController extends AbstractController {

    @Autowired
    SummaryComponent summaryComponent;

    @Autowired
    DefectTrackingComponent defectTrackingComponent;


    /**
     * 결함요약 - 결함 목록 조회
     * @param commonReqVo
     * @param summaryListGet
     * @param langInfo
     * @return
     */
    @GetMapping("/summaryList")
    @Description(name = "결함 목록 조회", description = "계약의 결함 목록 조회", type = Description.TYPE.MEHTOD)
    public GridResult getSummaryList(CommonReqVo commonReqVo,
                                     @Valid SummaryListGet summaryListGet,
                                     @CookieValue(name = "lang", required = false, defaultValue = "ko") String langInfo) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("결함요약보고서 > 결함 목록 조회");

        systemLogComponent.addUserLog(userLog);

        MybatisInput input = MybatisInput.of().add("cntrctNo", summaryListGet.getCntrctNo())
                .add("summaryType", summaryListGet.getSummaryType())
                .add("dfccyPhaseNoList", summaryListGet.getDfccyPhaseNoList())
                .add("rgstrIdList", summaryListGet.getRgstrIdList())
                .add("myDfccy", UserAuth.get(true).getUsrId())
                .add("lang", langInfo)
                .add("usrId", UserAuth.get(true).getUsrId());

        return GridResult.ok(summaryComponent.getSummaryList(input));
    }


    /**
     * 결함단계 셀렉트박스
     * @param commonReqVo
     * @param dfccyPhaseListGet
     * @return
     */
    @GetMapping("/dfccyPhaseList")
    @Description(name = "결함 단계 조회", description = "계약의 결함 단계 조회", type = Description.TYPE.MEHTOD)
    public Result dfccyPhaseList(CommonReqVo commonReqVo, @Valid DfccyPhaseListGet dfccyPhaseListGet) {

        String dfccyPhaseCd = "0101";

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("결함요약보고서 > 결함 단계 조회");

        systemLogComponent.addUserLog(userLog);

        return Result.ok().put("dfccyPhaseList", defectTrackingComponent.getDfccyPhaseList(dfccyPhaseListGet.getCntrctNo(), dfccyPhaseCd));
    }


    /**
     * 작성자 셀렉트
     * @param commonReqVo
     * @param summaryListGet
     * @return
     */
    @GetMapping("/rgstrList")
    @Description(name = "작성자 조회", description = "계약의 작성자 목록 조회", type = Description.TYPE.MEHTOD)
    public Result rgstrList(CommonReqVo commonReqVo, @Valid SummaryListGet summaryListGet) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("결함요약보고서 > 작성자 목록 조회");

        systemLogComponent.addUserLog(userLog);

        return Result.ok().put("rgstrList", summaryComponent.getRgstrList(summaryListGet.getCntrctNo()));
    }

}
