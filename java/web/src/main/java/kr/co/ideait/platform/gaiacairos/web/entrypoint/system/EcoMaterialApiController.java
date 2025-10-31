package kr.co.ideait.platform.gaiacairos.web.entrypoint.system;

import jakarta.validation.Valid;
import kr.co.ideait.platform.gaiacairos.comp.system.EcoMaterialComponent;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.ecomaterial.EcoMaterialMybatisParam.EcoMaterialListInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.ecomaterial.EcoMaterialForm;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.GridResult;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("api/system/ecomaterial")
public class EcoMaterialApiController extends AbstractController {

    @Autowired
    EcoMaterialComponent ecoMaterialComponent;

    @Autowired
    EcoMaterialForm ecoMaterialForm;

    /*
     * 친환경 자재 목록 조회
     */
    @GetMapping("/list")
    @Description(name = "친환경 자재 목록 조회", description = "친환경 자재 목록 조회", type = Description.TYPE.MEHTOD)
    public GridResult getEcoMaterialList(CommonReqVo commonReqVo, @Valid EcoMaterialForm.EcoMaterialListGet ecoMaterialListGet) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("친환경 자재 목록 조회");
        systemLogComponent.addUserLog(userLog);


        EcoMaterialListInput input = ecoMaterialForm.toEcoMaterialListInput(ecoMaterialListGet);

        return GridResult
                .ok(ecoMaterialComponent.getEcoMaterialList(input));

    }

    /*
     * 추가 페이지 자재 목록 조회
     */
    @GetMapping("/materialList")
    @Description(name = "자재 목록 조회", description = "자재 목록 조회", type = Description.TYPE.MEHTOD)
    public GridResult getMaterialList(CommonReqVo commonReqVo, @Valid EcoMaterialForm.EcoMaterialListGet ecoMaterialListGet) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("자재 목록 조회");
        systemLogComponent.addUserLog(userLog);


        EcoMaterialListInput input = ecoMaterialForm.toEcoMaterialListInput(ecoMaterialListGet);

        return GridResult
                .ok(ecoMaterialComponent.getMaterialList(input));

    }

    /*
     * 친환경 자재 추가
     */
    @PostMapping("/create")
    @Description(name = "친환경 자재 추가", description = "프로젝트의 계약별 친환경 자재 추가", type = Description.TYPE.MEHTOD)
    public Result ecoMaterialCreate(CommonReqVo commonReqVo, @RequestBody @Valid EcoMaterialForm.CreateEcoMaterial ecoMaterial) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("친환경 자재 추가");
        systemLogComponent.addUserLog(userLog);


        ecoMaterialComponent.ecoMaterialCreate(ecoMaterial, commonReqVo);

        return Result.ok();
    }

    /*
     * 친환경 자재 수정 시 기본데이터
     */
    @GetMapping("/{ecoId}")
    @Description(name = "친환경 자재 조회", description = "프로젝트의 계약별 친환경 자재 조회", type = Description.TYPE.MEHTOD)
    public Result getEcoMaterial(CommonReqVo commonReqVo, @PathVariable String ecoId) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("친환경 자재 조회");
        systemLogComponent.addUserLog(userLog);

        return Result.ok().put("ecoMaterial", ecoMaterialComponent.getEcoMaterial(ecoId));
    }

    /*
     * 친환경 자재 수정
     */
    @PostMapping("/update")
    @Description(name = "친환경 자재 수정", description = "프로젝트의 계약별 친환경 자재 수정", type = Description.TYPE.MEHTOD)
    public Result ecoMaterialUpdate(CommonReqVo commonReqVo, @RequestBody @Valid EcoMaterialForm.UpdateEcoMaterial ecoMaterial) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("친환경 자재 수정");
        systemLogComponent.addUserLog(userLog);


        ecoMaterialComponent.ecoMaterialUpdate(ecoMaterial, commonReqVo);

        return Result.ok();
    }

    /*
     * 친환경 자재 삭제
     */
    @PostMapping("/delete")
    @Description(name = "친환경 자재 삭제", description = "프로젝트의 계약별 친환경 자재 삭제", type = Description.TYPE.MEHTOD)
    public Result orgDelete(CommonReqVo commonReqVo,
            @RequestBody @Valid EcoMaterialForm.EcoMaterialList ecoMaterialList) {
        // 공통로그
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("친환경 자재 삭제");
        systemLogComponent.addUserLog(userLog);

        ecoMaterialComponent.ecoMaterialDelete(ecoMaterialList.getEcoMaterialList(), commonReqVo);
        return Result.ok();
    }

}
