package kr.co.ideait.platform.gaiacairos.web.entrypoint.project;

import jakarta.validation.Valid;
import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.comp.project.InformationComponent;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnProject;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.GridResult;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.overview.information.InformationForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("api/project/information")
public class InformationApiController extends AbstractController {

    @Autowired
    private InformationComponent informationComponent;

    /**
     * 프로젝트 목록조회
     */
    @GetMapping("/list")
    @Description(name = "프로젝트 목록조회", description = "계정별 프로젝트 목록을 조회한다.", type = Description.TYPE.MEHTOD)
    public GridResult getProjectList(CommonReqVo commonReqVo, InformationForm.InformationListGet informationListGet) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("프로젝트 목록조회");

        return GridResult.ok(informationComponent.getInformationList(informationListGet));
    }

    /**
     * 프로젝트 상세조회
     */
    @GetMapping("/{pjtNo}")
    @Description(name = "프로젝트 상세조회", description = "계정별 프로젝트 상세정보를 조회한다.", type = Description.TYPE.MEHTOD)
    public Result getProject(CommonReqVo commonReqVo, @PathVariable("pjtNo") String pjtNo) {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("프로젝트 상세조회");

        Map<String, Object> returnMap = informationComponent.getInformationDetail(pjtNo);

        return Result.ok().put("project", returnMap.get("project"))
                .put("attachment", returnMap.get("attachment"))
                .put("greenLevelDoc", returnMap.get("greenLevelDoc"))
                .put("energyEffectLevelDoc", returnMap.get("energyEffectLevelDoc"))
                .put("zeroEnergyLevelDoc", returnMap.get("zeroEnergyLevelDoc"))
                .put("bfLevelDoc", returnMap.get("bfLevelDoc"));
    }

    /**
     * 프로젝트 등록
     */
    @PostMapping("/register")
    @Description(name = "프로젝트 추가", description = "계정별 프로젝트 정보를 추가한다.", type = Description.TYPE.MEHTOD)
    public Result registerProject(
            CommonReqVo commonReqVo,
            @RequestPart("projectData") InformationForm.RegisterInformation information,
            @RequestPart(value = "files", required = false) MultipartFile fileNm,
            @RequestPart(value = "greenLevelDoc", required = false) MultipartFile greenLevelDoc,
            @RequestPart(value = "energyEffectLevelDoc", required = false) MultipartFile energyEffectLevelDoc,
            @RequestPart(value = "zeroEnergyLevelDoc", required = false) MultipartFile zeroEnergyLevelDoc,
            @RequestPart(value = "bfLevelDoc", required = false) MultipartFile bfLevelDoc)
            throws IllegalStateException, IOException {
        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.VIEW.name());
        userLog.setExecType("프로젝트 추가");

        CnProject createProject = informationComponent.createInformation(information, fileNm, greenLevelDoc, energyEffectLevelDoc,
                zeroEnergyLevelDoc, bfLevelDoc,commonReqVo.getApiYn());

        return Result.ok().put("project", createProject.getPjtNo());
    }

    /**
     * 프로젝트 수정
     */
    @PostMapping("/update/{pjtNo}")
    @Description(name = "프로젝트 수정", description = "계정별 프로젝트 정보를 수정한다.", type = Description.TYPE.MEHTOD)
    public Result updateProject(CommonReqVo commonReqVo, @PathVariable(value = "pjtNo") String pjtNo,
                                @RequestPart(value = "projectData") InformationForm.InformationUpdate project,
                                @RequestPart(value = "files", required = false) MultipartFile newFile,
                                @RequestParam(value = "deleteFileYn", required = false) String deleteFileYn,
                                @RequestParam(value = "deleteEcoDocId", required = false) List<String> deleteEcoDocId,
                                @RequestPart(value = "greenLevelDoc", required = false) MultipartFile greenLevelDoc,
                                @RequestPart(value = "energyEffectLevelDoc", required = false) MultipartFile energyEffectLevelDoc,
                                @RequestPart(value = "zeroEnergyLevelDoc", required = false) MultipartFile zeroEnergyLevelDoc,
                                @RequestPart(value = "bfLevelDoc", required = false) MultipartFile bfLevelDoc)
            throws IllegalStateException, IOException {

        informationComponent.updateInformation(pjtNo, project, newFile, deleteFileYn, greenLevelDoc,
                energyEffectLevelDoc, zeroEnergyLevelDoc, bfLevelDoc, deleteEcoDocId, commonReqVo.getApiYn());

        return Result.ok();
    }

    /**
     * 프로젝트 삭제
     */
    @PostMapping("/delete")
    @Description(name = "프로젝트 삭제", description = "계정별 프로젝트 정보를 삭제한다.", type = Description.TYPE.MEHTOD)
    public Result deleteProject(CommonReqVo commonReqVo, @RequestBody @Valid InformationForm.InformationList informationList) {
        informationComponent.deleteInformation(informationList.getInformationList(),commonReqVo.getApiYn());

        return Result.ok();
    }

    /**
     * 수요기관 조회
     */
    @GetMapping("/dminstt/{cntrctNo}")
    @Description(name = "수요기관 조회", description = "프로젝트의 수요기관 정보를 조회한다.", type = Description.TYPE.MEHTOD)
    public Result getDminstt(CommonReqVo commonReqVo, @PathVariable String cntrctNo) {

        return Result.ok().put("dminstt", informationComponent.getDminstt(cntrctNo));
    }


}