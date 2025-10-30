package kr.co.ideait.platform.gaiacairos.web.entrypoint.construction;

import jakarta.validation.Valid;
import kr.co.ideait.platform.gaiacairos.comp.construction.service.MainphotoService;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.construction.mainphoto.MainphotoForm;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/construction")
public class MainphotoApiController extends AbstractController {

    @Autowired
    MainphotoService mainphotoService;

    @Autowired
    MainphotoForm mainphotoForm;

    private String pjtType = "CMIS";

    /**
     * 공정사진 계약코드 조회
     * 
     * @param mainphotoMain
     * @return
     */
    @PostMapping("/mainphoto/contract-list")
    @Description(name = "주요 공정 사진 계약 목록 조회", description = "프로젝트의 계약 목록 조회", type = Description.TYPE.MEHTOD)
    public Result getContractList(CommonReqVo commonReqVo, @RequestBody @Valid MainphotoForm.MainphotoMain mainphotoMain) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("주요 공정 사진 계약코드 조회");
        systemLogComponent.addUserLog(userLog);

        return Result.ok()
                .put("contractList", mainphotoService.selectContractList(mainphotoMain.getPjtNo()));
    }

    /**
     * 공정사진 목록 조회
     * 
     * @param mainphotoMain
     * @return
     */
    @PostMapping("/mainphoto/mainphoto-list")
    @Description(name = "주요 공정 사진 목록 조회", description = "계약별 WBS 하위 주요 공정 사진 목록 조회", type = Description.TYPE.MEHTOD)
    public Result getMainphotoList(CommonReqVo commonReqVo, @RequestBody @Valid MainphotoForm.MainphotoMain mainphotoMain) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("주요 공정 사진 목록 조회");
        systemLogComponent.addUserLog(userLog);

        return Result.ok()
                .put("mainphotoList", mainphotoService.selectMainphotoList(mainphotoMain.getCntrctNo(),
                        mainphotoMain.getWbsCd(), mainphotoMain.getSearchText()));
    }

}
