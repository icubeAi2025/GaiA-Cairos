package kr.co.ideait.platform.gaiacairos.web.entrypoint.project;

import jakarta.validation.Valid;
import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.comp.project.ContractStatusComponent;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnContractChange;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnContractCompany;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.GridResult;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.contract.contractstatus.ContractChangeForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.contract.contractstatus.ContractCompanyForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.contract.contractstatus.ContractstatusForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("api/project/contractstatus")
public class ContractstatusApiController extends AbstractController {

    @Autowired
    ContractStatusComponent contractComponent;

    @Autowired
    ContractstatusForm contractstatusForm;

    @Autowired
    ContractCompanyForm contractCompanyForm;

    @Autowired
    ContractChangeForm contractChangeForm;

    /* ==================================================================================================================
     *
     * 계약 현황 - 계약
     *
     * ==================================================================================================================
     */

    /**
     * 계약 목록 조회
     *
     */
    @GetMapping("/list")
    @Description(name = "계약목록 조회", description = "전체 계약 데이터 조회", type = Description.TYPE.MEHTOD)
    public GridResult getContractList(CommonReqVo commonReqVo, @Valid ContractstatusForm.ContractListGet contractListGet) {

        return GridResult.ok(contractComponent.getList(contractstatusForm.toContractstatusListInput(contractListGet)));

    }

    /**
     * 계약 상세조회
     *
     */
    @Description(name = "계약 조회", description = "특정 계약의 데이터 조회", type = Description.TYPE.MEHTOD)
    @GetMapping("/get/{cntrctNo}/{type}")
    public Result getContract(CommonReqVo commonReqVo, @PathVariable(name = "cntrctNo") String cntrctNo) {
        
            return Result.ok().put("contract", contractComponent.getCntrctDetail(cntrctNo));

    }

    /**
     * 계약 추가(도급-대표계약자, 변경-1차수도 자동 추가)
     *
     */
    @PostMapping("/create")
    @Description(name = "계약 추가", description = "계약, 대표 도급사, (1차수) 1회차 계약변경 추가", type = Description.TYPE.MEHTOD)
    public Result createContract(CommonReqVo commonReqVo, @RequestBody @Valid ContractstatusForm.CreateContract contract) {
        return contractComponent.createContractFullProcess(
                contract,
                commonReqVo.getPjtDiv(),
                commonReqVo.getApiYn(),
                commonReqVo.getUserId()
        );
    }

    /**
     * 계약 수정
     * 
     */
    @PostMapping("/update/{cntrctNo}")
    @Description(name = "계약 수정", description = "계약, 대표 도급사, (1차수) 1회차 계약변경 수정", type = Description.TYPE.MEHTOD)
    public Result updateContract(CommonReqVo commonReqVo, @PathVariable(value = "cntrctNo") String cntrctNo,
            @RequestBody @Valid ContractstatusForm.ContractUpdate contractUpdate) {

        return contractComponent.updateContractFullProcess(
                cntrctNo,contractUpdate,
                commonReqVo.getPjtDiv(),
                commonReqVo.getApiYn(),
                commonReqVo.getUserId()
        );

    }

    /**
     * 계약 삭제
     *
     */
    @PostMapping("/delete")
    @Description(name = "계약 삭제", description = "계약 삭제", type = Description.TYPE.MEHTOD)
    public Result deleteContract(CommonReqVo commonReqVo, @RequestBody @Valid ContractstatusForm.ContractList contractList) {

        return contractComponent.deleteContracts(contractList,commonReqVo.getPjtDiv(),commonReqVo.getApiYn());

    }

    /**
     * 계약 전부 삭제
     */
    @PostMapping("/deleteAll")
    @Description(name = "계약 전부 삭제", description = "계약 전부 삭제", type = Description.TYPE.MEHTOD)
    public Result deleteContractAll(CommonReqVo commonReqVo, @RequestBody ContractstatusForm.PjtNoList pjtNoList) {

        contractComponent.deleteAllContract(pjtNoList.getPjtNoList(), commonReqVo.getUserId());
        return Result.ok();

    }

    /* ==================================================================================================================
     *
     * 계약 현황 - 도급사
     *
     * ==================================================================================================================
     */

    /**
     * 도급 목록
     *
     */
    @GetMapping("/company/list")
    @Description(name = "계약 도급 목록", description = "계약 전체 계약 데이터 조회", type = Description.TYPE.MEHTOD)
    public GridResult getCompanyList(CommonReqVo commonReqVo, @Valid ContractCompanyForm.ContractCompanyListGet companyListGet) {

        return GridResult.ok(contractComponent.getCompanyList(contractCompanyForm.toContractcompanyListInput(companyListGet)));

    }

    /**
     * 도급 상세 조회
     *
     */
    @GetMapping("/company/get/{cntrctId}/{cntrctNo}")
    @Description(name = "계약 도급 조회", description = "계약 도급 수정, 일반 화면 계약 도급 데이터 조회", type = Description.TYPE.MEHTOD)
    public Result getCompany(CommonReqVo commonReqVo, @PathVariable("cntrctId") Long cntrctId, @PathVariable("cntrctNo") String cntrctNo) {

        return Result.ok().put("company", contractComponent.getContractCompany(cntrctId, cntrctNo));

    }

    /**
     * 계약명 조회
     *
     */
    @GetMapping("/company/get/cntrctNm/{cntrctNo}")
    @Description(name = "도급의 계약명 조회", description = "계약 도급 추가 화면 계약명 데이터 조회", type = Description.TYPE.MEHTOD)
    public Result getContractName(CommonReqVo commonReqVo, @PathVariable("cntrctNo") String cntrctNo) {

        return Result.ok().put("cntrctNm", contractComponent.findCntrctNm(cntrctNo));

    }

    /**
     * 도급 추가
     * 
     * @param company
     */
    @PostMapping("/company/create")
    @Description(name = "계약 도급 추가", description = "계약 도급 추가", type = Description.TYPE.MEHTOD)
    public Result createCompany(CommonReqVo commonReqVo, @RequestBody @Valid ContractCompanyForm.ContractCompany company) {

        CnContractCompany cnCompany = contractCompanyForm.toCnContractCompany(company);
        CnContractCompany savedCompany = contractComponent.createCompany(cnCompany,commonReqVo.getPjtDiv(),commonReqVo.getApiYn());

        return Result.ok().put("cnCompany",savedCompany);
    }

    /**
     * 도급 추가 > 드롭박스 목록 가져오기
     */
    @GetMapping("/company/types")
    @Description(name = "계약 도급 추가화면 공종구분", description = "계약 도급 추가 화면 공종구분 데이터 조회", type = Description.TYPE.MEHTOD)
    public Result getCnsttyCdList(CommonReqVo commonReqVo) {

        return Result.ok().put("cnsttyCd", contractComponent.getCnsttyCdList());

    }

    /**
     * 도급 수정
     * 
     * @param cntrctId
     * @param cntrctNo
     * @param companyUpdate
     * @return CnContractCompany
     */
    @PostMapping("/company/update/{cntrctId}/{cntrctNo}")
    @Description(name = "계약 도급 수정", description = "계약 도급 데이터 수정", type = Description.TYPE.MEHTOD)
    public Result updateCompany(CommonReqVo commonReqVo, @PathVariable(value = "cntrctId") Long cntrctId, @PathVariable String cntrctNo,
            @RequestBody @Valid ContractCompanyForm.CompanyUpdate companyUpdate) {

        return Result.ok().put("updateCompany",contractComponent.updateCompany(cntrctId,cntrctNo,companyUpdate,commonReqVo.getPjtDiv(),commonReqVo.getApiYn()));
    }

    /**
     * 도급 삭제
     * 
     * @param companyList
     */
    @PostMapping("/company/delete")
    @Description(name = "계약 도급 삭제", description = "계약 도급 데이터 삭제", type = Description.TYPE.MEHTOD)
    public Result deleteCompany(CommonReqVo commonReqVo, @RequestBody @Valid ContractCompanyForm.CompanyList companyList) {
        contractComponent.deleteCompany(companyList.getCompanyList(),companyList.getCntrctNo(),commonReqVo.getPjtDiv(),commonReqVo.getApiYn());
        return Result.ok();
    }

    /* ==================================================================================================================
     *
     * 계약 현황 - 계약 변경
     *
     * ==================================================================================================================
     */

    /**
     * 변경 목록
     *
     */
    @GetMapping("/change/list")
    @Description(name = "계약 변경 목록", description = "전체 계약 변경 데이터 조회", type = Description.TYPE.MEHTOD)
    public GridResult getChangeList(CommonReqVo commonReqVo, @Valid ContractChangeForm.ContractChangeListGet changeListGet) {

        return GridResult.ok(contractComponent.getContractChangeList(contractChangeForm.toContractchangeListInput(changeListGet)));

    }

    /**
     * 변경 조회
     *
     */
    @GetMapping("/change/get/{cntrctChgId}/{cntrctNo}")
    @Description(name = "계약 변경 조회", description = "수정, 조회 화면 계약 변경 데이터 조회", type = Description.TYPE.MEHTOD)
    public Result getChange(CommonReqVo commonReqVo, @PathVariable("cntrctChgId") String cntrctChgId,
            @PathVariable("cntrctNo") String cntrctNo) {

        return Result.ok().put("change",contractComponent.getContractChangeDetail(cntrctChgId, cntrctNo));

    }

    @GetMapping("/change/getAdd/{cntrctNo}/{cntrctPhase}")
    @Description(name = "계약 변경 조회", description = "추가 화면 계약 변경 데이터 조회", type = Description.TYPE.MEHTOD)
    public Result getChangeAdd(CommonReqVo commonReqVo, @PathVariable("cntrctNo") String cntrctNo,
            @PathVariable("cntrctPhase") int cntrctPhase) {

        return Result.ok().put("changeAdd",contractComponent.getContractChangeAdd(cntrctNo, cntrctPhase));

    }

    /**
     * 변경 추가
     *
     */
    @PostMapping("/change/create")
    @Description(name = "계약 변경 추가", description = "계약 변경 추가", type = Description.TYPE.MEHTOD)
    public Result createChange(CommonReqVo commonReqVo, @RequestBody @Valid ContractChangeForm.ChangeCreate change) {
        CnContractChange cnChange = contractChangeForm.toContractChange(change);
        contractComponent.createChange(cnChange,commonReqVo.getPjtDiv(),commonReqVo.getApiYn(), commonReqVo.getUserId());
        return Result.ok();
    }

    /**
     * 변경 수정
     *
     */
    @PostMapping("/change/update/{cntrctChgId}/{cntrctNo}")
    @Description(name = "계약 변경 수정", description = "계약 변경 데이터 수정", type = Description.TYPE.MEHTOD)
    public Result updateChange(CommonReqVo commonReqVo, @PathVariable(value = "cntrctChgId") String cntrctChgId, @PathVariable String cntrctNo,
            @RequestBody @Valid ContractChangeForm.ChangeUpdate changeUpdate) {

        CnContractChange cnChange =  contractComponent.updateChange(cntrctChgId,cntrctNo,changeUpdate,commonReqVo.getPjtDiv(),commonReqVo.getApiYn(), commonReqVo.getUserId());

        return Result.ok().put("updateChange",cnChange);
    }

    /**
     * 변경 삭제
     *
     */
    @PostMapping("/change/delete")
    @Description(name = "계약 변경 삭제", description = "계약 변경 데이터 삭제", type = Description.TYPE.MEHTOD)
    public Result deleteChange(CommonReqVo commonReqVo, @RequestBody @Valid ContractChangeForm.ChangeList changeList) {
        contractComponent.deleteChange(changeList.getChangeList(),commonReqVo.getPjtDiv(),commonReqVo.getApiYn());
        return Result.ok();
    }

    /* ==================================================================================================================
     *
     * 계약 현황 - 계약내역
     *
     * ==================================================================================================================
     */

    /**
     * Eureca 및 PCCS 연계로 인한 Deprecated 처리
     * 계약 내역서 조회(직접비 합계금액 포함)
     * 변경: 2024-11-13 계약내역서 등록에 필요한 {type} parameter 추가
     */
    @Deprecated
    @PostMapping("/bid/contractBidList/{cntrctNo}")
    @Description(name = "계약 내역서 조회", description = "계약 내역서 데이터 조회", type = Description.TYPE.MEHTOD)
    public Result getContractBidList(CommonReqVo commonReqVo, @PathVariable(value = "cntrctNo") String cntrctNo,
            @RequestParam(defaultValue = "bid", name = "type") String type) {
        return Result.ok()
                .put("contractBidList", contractComponent.getContractBidList(cntrctNo, type))
                .put("bidTotalCost", contractComponent.getContractBidCost(cntrctNo));
    }

    /**
     * Eureca 및 PCCS 연계로 인한 Deprecated 처리
     * 계약 내역서 검색
     *
     */
    @Deprecated
    @PostMapping("/bid/contractBidSearch")
    @Description(name = "계약 내역서 검색", description = "계약 내역서 검색 데이터 조회", type = Description.TYPE.MEHTOD)
    public Result getContractBidSearch(CommonReqVo commonReqVo, @RequestBody Map<String, Object> param) {
        String cntrctNo = (String) param.get("cntrctNo");
        String searchValue = (String) param.get("searchValue");
        String type = "bid";

        // 20250211 검색조건 추가
        if (!ObjectUtils.isEmpty(param.get("type"))) {
            type = (String) param.get("type");
        }

        return Result.ok().put("contractBidSearch", contractComponent.getContractBidSearch(cntrctNo, searchValue, type));
    }

    /**
     * Eureca 및 PCCS 연계로 인한 Deprecated 처리
     * 원가 계산서 조회
     *
     */
    @Deprecated
    @PostMapping("bid/calculatorList/{cntrctNo}")
    @Description(name = "원가 계산서", description = "원가 계산서 데이터 조회", type = Description.TYPE.MEHTOD)
    public Result getCostCalculatorList(CommonReqVo commonReqVo, @PathVariable(value = "cntrctNo") String cntrctNo) {
        return Result.ok()
                .put("calculatorList", contractComponent.getCalculatorList(cntrctNo));
    }

    /**
     * Eureca 및 PCCS 연계로 인한 Deprecated 처리
     * 계약내역서 등록. bid 파일로 계약내역서를 등록한다.
     *
     */
    @Deprecated
    @PostMapping("bid/registCnContractBid")
    @Description(name = "계약내역서 등록", description = "Bid 파일로 계약내역서 등록", type = Description.TYPE.MEHTOD)
    public Result createContractBid(CommonReqVo commonReqVo, @RequestPart(value = "bidFile") MultipartFile bidFile,
            @RequestParam("cntrctNo") String cntrctNo, @RequestParam("type") String type)
            throws Exception {

        boolean target = contractComponent.registCtrDtlstt(bidFile, cntrctNo, type);

        if (target)
            return Result.ok();
        else
            return Result.nok(ErrorType.INTERNAL_SERVER_ERROR);
    }

}
