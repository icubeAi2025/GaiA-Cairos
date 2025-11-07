package kr.co.ideait.platform.gaiacairos.web.entrypoint.projectcost;

import jakarta.validation.Valid;
import kr.co.ideait.platform.gaiacairos.comp.projectcost.service.C3RService;
import kr.co.ideait.platform.gaiacairos.comp.projectcost.service.ContractService;
import kr.co.ideait.platform.gaiacairos.comp.projectcost.service.PaymentService;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.projectcost.contract.ContractForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.projectcost.payment.PaymentForm;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.util.FileService;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.iframework.annotation.Description;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/projectcost")
public class ContractApiController extends AbstractController {
	
	@Autowired
	ContractService contractService;

    @Autowired
    C3RService c3RService;

	@Autowired
	PaymentService paymentService;
	
	@Autowired
    ContractForm projectcostForm;
	
    @Autowired
    FileService fileService;

	// private String pjtType = "CMIS";
	

	/**
     * 계약 내역서 리스트 조회
     * @param paymentMain
     * @return
     */
    @PostMapping("/contract/contract-list")
    @Description(name = "계약 내역서 리스트 조회", description = "계약 내역서 리스트 데이터 조회", type = Description.TYPE.MEHTOD)
    public Result getContractList(CommonReqVo commonReqVo, @RequestBody @Valid PaymentForm.ProjectcostMain paymentMain) {
    	return Result.ok()
    			.put("contractList", contractService.selectContractList(paymentMain.getPjtNo()));
    }
    
    
	/**
     * 계약 변경 내역서가 변경된 차수 리스트 조회
     * @param contractMain
     * @return
     */
    @PostMapping("/contract/contract-change-list")
    @Description(name = "계약 변경 내역서가 변경된 차수 리스트 조회", description = "계약 변경 내역서가 변경된 차수 리스트 데이터 조회", type = Description.TYPE.MEHTOD)
    public Result getContractChangeList(CommonReqVo commonReqVo, @RequestBody @Valid ContractForm.ProjectcostMain contractMain) {
    	return Result.ok()
                .put("contractChangeList", contractService.selectContractChangeList(contractMain.getCntrctNo()));
    }
    
	/**
     * 계약 내역서의 공종 목록 트리 리스트 조회
     * @param contractMain
     * @return
     */
    @PostMapping("/contract/cbs-list")
    @Description(name = "계약 내역서의 공종 목록 트리 리스트 조회", description = "계약 내역서의 공종 목록 데이터 조회", type = Description.TYPE.MEHTOD)
    public Result getCbsList(CommonReqVo commonReqVo, @RequestBody @Valid ContractForm.ProjectcostMain contractMain) {
                
    	return Result.ok()
                .put("cbsList", contractService.selectCbsList(contractMain.getCntrctId(), contractMain.getChgId()));
    }
    
	/**
     * 계약 내역서 조회
     * @param contractMain
     * @return
     */
    @PostMapping("/contract/cbs-detail-list")
    @Description(name = "계약 내역서 조회", description = "계약 내역서 데이터 조회", type = Description.TYPE.MEHTOD)
    public Result getCbsDetailList(CommonReqVo commonReqVo, @RequestBody @Valid ContractForm.ProjectcostListGet contractMain) {

    	return Result.ok()
                .put("cbsDetailList", contractService.selectCbsDetailList(contractMain.getCntrctId(), contractMain.getChgId(), contractMain.getCnsttySnList(), contractMain.getSearchText()));
    }
    
	/**
     * 계약 내역서 상세조회
     * @param contractMain
     * @return
     */
    @PostMapping("/contract/cbs-detail")
    public Result getCbsDetail(@RequestBody @Valid ContractForm.ProjectcostDetailGet contractMain) {

    	return Result.ok()
                .put("cbsDetail", contractService.selectCbsDetail(contractMain.getId(), contractMain.getCd(), contractMain.getType()));
    }
    
	/**
     * 원가 계산서 조회
     * @param contractMain
     * @return
     */
    @PostMapping("/contract/cost-calculator-list")
    @Description(name = "원가 계산서 조회", description = "원가 계산서 데이터 조회", type = Description.TYPE.MEHTOD)
    public Result getCostCalculatorList(CommonReqVo commonReqVo, @RequestBody @Valid ContractForm.ProjectcostListGet contractMain) {
        log.info("getCostCalculatorList: 원가 계산서 조회 contractMain = {}", contractMain);

        List<Map<String, ?>> result = contractService.selectCostCalculatorList(contractMain.getChgId(), contractMain.getCntrctId());

        // 조회 결과 없을 경우 빈 리스트 전달
        if(result == null || result.isEmpty()) {
            result =  Collections.emptyList();
        }

    	return Result.ok()
                .put("costCalculatorList",result);
    }

    @PostMapping("/contract/registC3R")
    @Description(name = "설계내역서 등록", description = "c3r 파일로 설계내역서 등록", type = Description.TYPE.MEHTOD)
    public Result createC3R(CommonReqVo commonReqVo, @RequestPart(value = "c3rFileList") List<MultipartFile> c3rFileList
            , @RequestParam("cntrctNo") String cntrctNo
            , @RequestParam("cntrctChgId") String cntrctChgId
            , @RequestParam(value = "cd", required = false) String majorCnsttyCd
            , @RequestPart("checkFileListJson") MultipartFile checkFileListJson
    ) throws RuntimeException {

        boolean target;
        try {
            // 20250227 - 정적검사 수정 DM_DEFAULT_ENCODING
            String fileListJson = new String(checkFileListJson.getBytes(), StandardCharsets.UTF_8); // JSON을 문자열로 변환

            // C3R 등록
            target = c3RService.saveC3RFile(c3rFileList, cntrctNo, cntrctChgId, majorCnsttyCd, fileListJson);
        } catch (IOException e) {
            throw new GaiaBizException(ErrorType.INTERNAL_SERVER_ERROR, "[ID - 2] 설계내역서 등록중 오류가 발생했습니다.");
        }

        if (target) return Result.ok();
        else return Result.nok(ErrorType.INTERNAL_SERVER_ERROR, "설계내역서 등록중 오류가 발생했습니다.");
    }
}
