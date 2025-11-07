package kr.co.ideait.platform.gaiacairos.web.entrypoint.project;

import jakarta.validation.Valid;
import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.comp.project.SubcontractComponent;
import kr.co.ideait.platform.gaiacairos.comp.project.service.ContractstatusService;
import kr.co.ideait.platform.gaiacairos.comp.project.service.SubcontractService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnSubcontract;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnSubcontractChange;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.GridResult;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.contract.contractstatus.ContractstatusMybatisParam.ContractcompanyOutput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.contract.subcontract.SubcontractForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.contract.subcontract.SubcontractMybatisParam.SubcontractChangeInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.contract.subcontract.SubcontractMybatisParam.SubcontractChangeListInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.contract.subcontract.SubcontractMybatisParam.SubcontractListInput;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("api/project/subcontract")
public class SubcontractApiController extends AbstractController {

    @Autowired
    SubcontractComponent subcontractComponent;

    @Autowired
    SubcontractForm subcontractForm;

    /*
     * 계약 이름 조회
     */
    @GetMapping("/{cntrctNo}")
    @Description(name = "계약 이름 조회", description = "프로젝트의 계약별 이름 조회", type = Description.TYPE.MEHTOD)
    public Result getSubcontract(CommonReqVo commonReqVo, @PathVariable("cntrctNo") String cntrctNo) {

        return Result.ok().put("contractNm", subcontractComponent.getByCntrctNm(cntrctNo));
    }

    /*
     * 원도급 조회
     */
    @GetMapping("/company/{cntrctNo}")
    @Description(name = "원도급 조회", description = "프로젝트의 계약별 원도급 조회", type = Description.TYPE.MEHTOD)
    public Result getCompany(@PathVariable("cntrctNo") String cntrctNo) {

        return Result.ok().put("company", subcontractComponent.getSubcontractCompanyList(cntrctNo));
    }

    // ---------------------------------------------------------------------------------------------------------------------------------

    /*
     * 하도급 목록 조회
     */
    @GetMapping("/subList")
    @Description(name = "하도급 목록 조회", description = "프로젝트의 계약별 하도급 목록 조회", type = Description.TYPE.MEHTOD)
    public GridResult getSubcontractList(CommonReqVo commonReqVo, @Valid SubcontractForm.SubcontractListGet subcontractListGet) {

        SubcontractListInput input = subcontractForm.toSubcontractListInput(subcontractListGet);

        return GridResult.ok(subcontractComponent.getSubcontractList(input));
    }

    /**
     * 하도급 조회 및 하도급계약변경추가 기본데이터
     */
    @GetMapping("/{cntrctNo}/{scontrctCorpId}")
    @Description(name = "하도급 상세조회", description = "프로젝트의 계약별 하도급 상세조회", type = Description.TYPE.MEHTOD)
    public Result getSubcontract(CommonReqVo commonReqVo, @PathVariable("cntrctNo") String cntrctNo, @PathVariable("scontrctCorpId") Long scontrctCorpId) {

        Map<String,Object> result = subcontractComponent.getSubcontract(cntrctNo,scontrctCorpId);

        return Result.ok().put("subcontract", result.get("getSubcontract")).put("cntrctChgNo", result.get("cntrctChgNo"));
    }

    /*
     * 하도급 업체 생성
     */
    @PostMapping("/subCreate")
    @Description(name = "하도급 추가", description = "프로젝트의 계약별 하도급 추가", type = Description.TYPE.MEHTOD)
    public Result subcontractCreate(CommonReqVo commonReqVo, @RequestBody @Valid SubcontractForm.CreateSubcontract subcontract) {

        log.debug("Received CreateSubcontract: {}", subcontract);
        CnSubcontract saveCnSubcontract = subcontractComponent.subContractCreate(subcontractForm.toCreateSubcontract(subcontract),commonReqVo.getPjtDiv(),commonReqVo.getApiYn());

        if(saveCnSubcontract != null){
            return Result.ok().put("subcontract", saveCnSubcontract);
        }
        throw new GaiaBizException(ErrorType.NO_DATA);

    }

    /*
     * 하도급 삭제
     */
    @PostMapping("/subDelete")
    @Description(name = "하도급 삭제", description = "프로젝트의 계약별 하도급 삭제", type = Description.TYPE.MEHTOD)
    public Result subcontractDelete(CommonReqVo commonReqVo, @RequestBody @Valid SubcontractForm.SubcontractList subcontractList) {

        subcontractComponent.subcontractDelete(subcontractList.getSubcontractList(),commonReqVo.getPjtDiv(),commonReqVo.getApiYn());

        return Result.ok();
    }

    /**
     * 하도급 수정
     * 
     */
    @PostMapping("/subUpdate")
    @Description(name = "하도급 수정", description = "프로젝트의 계약별 하도급 수정", type = Description.TYPE.MEHTOD)
    public Result subcontractUpdate(CommonReqVo commonReqVo, @RequestBody @Valid SubcontractForm.UpdateSubcontract subcontract) {


        CnSubcontract saveCnSubcontract = subcontractComponent.subcontractUpdate(subcontract,commonReqVo.getPjtDiv(),commonReqVo.getApiYn());

        if (saveCnSubcontract != null) { // 데이터가 있는지 확인
            return Result.ok().put("subcontract", saveCnSubcontract);
        }
        throw new GaiaBizException(ErrorType.NO_DATA);

    }

    // --------------------------------------------------------------------------------------------

    /*
     * 하도급 변경 목록 조회
     */
    @GetMapping("/changeList")
    @Description(name = "하도급 변경 목록 조회", description = "프로젝트의 계약별 하도급 변경 목록 조회", type = Description.TYPE.MEHTOD)
    public GridResult getSubcontractChangeList(CommonReqVo commonReqVo, 
            @Valid SubcontractForm.SubcontractChangeListGet subcontractChangeListGet) {

        SubcontractChangeListInput input = subcontractForm.toSubcontractChangeListInput(subcontractChangeListGet);

        return GridResult.ok(subcontractComponent.getSubcontractChangeList(input));
    }

    /**
     * 하도급 계약변경 수정 기본데이터
     */
    @GetMapping("/{cntrctNo}/{scontrctCorpId}/{cntrctChgId}")
    @Description(name = "하도급 변경 상세조회", description = "프로젝트의 계약별 하도급 변경 상세조회", type = Description.TYPE.MEHTOD)
    public Result getSubcontractChange(CommonReqVo commonReqVo, @PathVariable("cntrctNo") String cntrctNo,
            @PathVariable("scontrctCorpId") Long scontrctCorpId, @PathVariable("cntrctChgId") Long cntrctChgId) {

        SubcontractChangeInput input = subcontractForm.toSubcontractChangeInput(cntrctNo, scontrctCorpId, cntrctChgId);

        return Result.ok()
                .put("subcontractChange", subcontractComponent.getSubcontractChange(input));
    }

    /*
     * 하도급 변경 생성
     */
    @PostMapping("/changeCreate")
    @Description(name = "하도급 변경 생성", description = "프로젝트의 계약별 하도급 변경 생성", type = Description.TYPE.MEHTOD)
    public Result subContractChanageCreate(CommonReqVo commonReqVo, 
            @RequestBody @Valid SubcontractForm.CreateSubcontractChange subcontractChange) {

        log.debug("Received CreateSubcontractChange: {}", subcontractChange);

        CnSubcontractChange saveCnSubcontraChange = subcontractComponent.subContractChangeAdd(subcontractForm.toCreateSubcontractChange(subcontractChange),commonReqVo.getPjtDiv(),commonReqVo.getApiYn());

        if(saveCnSubcontraChange != null){
            return Result.ok().put("subcontractChange", saveCnSubcontraChange);
        }
        throw new GaiaBizException(ErrorType.NO_DATA);
    }

    /*
     * 하도급 변경 삭제
     */
    @PostMapping("/changeDelete")
    @Description(name = "하도급 변경 삭제", description = "프로젝트의 계약별 하도급 변경 삭제", type = Description.TYPE.MEHTOD)
    public Result subcontractChangeDelete(CommonReqVo commonReqVo, @RequestBody @Valid SubcontractForm.SubcontractChangeList subcontractChangeList) {
        subcontractComponent.subcontractChangeDelete(subcontractChangeList.getSubcontractChangeList(),commonReqVo.getPjtDiv(),commonReqVo.getApiYn());
        return Result.ok();
    }

    /**
     * 하도급 변경 수정
     */
    @PostMapping("/changeUpdate")
    @Description(name = "하도급 변경 수정", description = "프로젝트의 계약별 하도급 변경 수정", type = Description.TYPE.MEHTOD)
    public Result subcontractChangeUpdate(CommonReqVo commonReqVo, 
            @RequestBody @Valid SubcontractForm.UpdateSubcontractChange subcontractChange) {

        CnSubcontractChange saveCnSubcontraChange = subcontractComponent.subcontractChangeUpdate(subcontractChange, commonReqVo.getPjtDiv(),commonReqVo.getApiYn());

        if (saveCnSubcontraChange != null) { // 데이터가 있는지 확인
           // 전달된 내용만 매핑
            return Result.ok().put("cnSubcontraChange",saveCnSubcontraChange);
        }
        throw new GaiaBizException(ErrorType.NO_DATA);
    }

}
