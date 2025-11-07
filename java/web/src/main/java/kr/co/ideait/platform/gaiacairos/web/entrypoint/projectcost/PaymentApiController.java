package kr.co.ideait.platform.gaiacairos.web.entrypoint.projectcost;


import jakarta.validation.Valid;
import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.comp.projectcost.PaymentComponent;
import kr.co.ideait.platform.gaiacairos.comp.projectcost.service.ContractService;
import kr.co.ideait.platform.gaiacairos.comp.projectcost.service.PaymentService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwPayMng;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.projectcost.payment.PaymentForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/projectcost")
public class PaymentApiController extends AbstractController {
	
	@Autowired
	PaymentService paymentService;
	
	@Autowired
	ContractService contractService;
	
	@Autowired
    PaymentForm paymentForm;

    @Autowired
    PaymentComponent paymentComponent;

    private String pjtType = "CMIS";


    /**
     * 기성 목록 조회
     * @param paymentMain
     * @return
     */
    @PostMapping("/payment/payment-list")
    @Description(name = "기성 목록 조회", description = "계약에 따른 기성 목록 조회", type = Description.TYPE.MEHTOD)
    public Result getPaymentChangeList(CommonReqVo commonReqVo, @RequestBody @Valid PaymentForm.ProjectcostMain paymentMain) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("기성 관리 목록 조회");
        systemLogComponent.addUserLog(userLog);

        return Result.ok()
                .put("cntrctChgInfo", paymentService.getCntrctChgInfo(paymentMain.getCntrctNo()))    // 20250217 차수정보 추가
                .put("paymentList", paymentService.selectPaymentList(paymentMain.getCntrctNo()));
    }


    /**
     * 기성 상세조회
     * @param commonReqVo
     * @param paymentMain
     * @return
     */
    @PostMapping("/payment/payment-detail")
    @Description(name = "기성 상세조회", description = "계약에 따른 기성 상세조회", type = Description.TYPE.MEHTOD)
    public Result getPaymentDetail(CommonReqVo commonReqVo, @RequestBody @Valid PaymentForm.ProjectcostDetailGet paymentMain) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("기성 관리 상세 조회");
        systemLogComponent.addUserLog(userLog);

        return Result.ok()
                .put("paymentDetail", paymentService.getPayment(paymentMain.getCntrctNo(), paymentMain.getPayprceSno()));
    }


    /**
     * 기성회차 시퀀스 조회
     * @param commonReqVo
     * @param paymentMain
     * @return
     */
    @PostMapping("/payment/payment-tmnum")
    @Description(name = "기성회차 시퀀스 조회", description = "계약에 따른 기성회차 시퀀스 조회", type = Description.TYPE.MEHTOD)
    public Result getPayprceTmnum(CommonReqVo commonReqVo, @RequestBody @Valid PaymentForm.ProjectcostDetailGet paymentMain) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("기성 회차 시퀀스 조회");
        systemLogComponent.addUserLog(userLog);

        return Result.ok()
                .put("paymentTmnum", paymentService.selectPayprceTmnum(paymentMain.getCntrctNo()));
    }


    /**
     * 기성 추가
     * @param payment
     * @return
     */
    @PostMapping("/payment/add-payment")
    @Description(name = "기성 추가", description = "계약에 따른 기성 데이터 저장", type = Description.TYPE.MEHTOD)
    public Result addPayment(CommonReqVo commonReqVo, @RequestBody @Valid PaymentForm.PaymentInsert payment) {
        log.info("addPayment: 기성추가 param = {}",payment);
        payment.setDltYn("N");
        String eurecaSendYn = "N";
        CwPayMng cwPayMng = null;
        cwPayMng = paymentService.addPayment(paymentForm.PaymentInsert(payment), payment.getDailyReportDate(), payment.getPrevPayprceYm());
//        paymentService.addPayment(paymentForm.PaymentInsert(payment), payment.getDailyReportDate());

        if(cwPayMng != null) {
            log.info("addPayment: 기성 생성 성공 여부 = {}",cwPayMng.getEurecaSendYn());
            eurecaSendYn = cwPayMng.getEurecaSendYn();
        }


        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("기성 추가");
        systemLogComponent.addUserLog(userLog);

        return Result.ok().put("eurecaSendYn", eurecaSendYn);
    }

    /**
     * 기성 수정
     * @param payment
     * @return
     */
    @PostMapping("/payment/update-payment")
    @Description(name = "기성 수정", description = "계약에 따른 기성 데이터 수정", type = Description.TYPE.MEHTOD)
    public Result updatePayment(CommonReqVo commonReqVo, @RequestBody @Valid PaymentForm.PaymentInsert payment) {
        log.info("updatePayment: 기성 수정 진행 payment = {}", payment);
        CwPayMng paymentData = paymentService.getPayment(payment.getCntrctNo(),payment.getPayprceSno());

        if (paymentData != null) {
            payment.setDltYn("N");
            paymentForm.toUpdateCwPayMng(payment, paymentData);
            try {
                log.info("updatePayment: 기성 수정 저장 시도 paymentData = {}", paymentData);
                CwPayMng cwPayMng = paymentService.updatePayment(paymentData);
            } catch (RuntimeException e) {
                log.info("updatePayment: 기성 수정 저장 시도 중 오류 발생, 오류 메세지 = {}", e.getMessage());
            }
        }

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("기성 수정");
        systemLogComponent.addUserLog(userLog);

        return Result.ok();

    }

    /**
     * 기성 삭제
     * @param payment
     * @return
     */
    @PostMapping("/payment/del-payment")
    @Description(name = "기성 삭제", description = "계약에 따른 기성 데이터 삭제", type = Description.TYPE.MEHTOD)
    public Result delPayment(CommonReqVo commonReqVo, @RequestBody @Valid PaymentForm.PaymentList payment) {
        paymentComponent.delPayment(payment.getPaymentList());

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("기성 삭제");
        systemLogComponent.addUserLog(userLog);

        return Result.ok();
    }


    /**
     * 기성 상태 업데이트 ( 승인 요청 )
     * @param payment
     * @return
     */
    @PostMapping("/payment/update-status-payment")
    @Description(name = "기성 상태 업데이트", description = "기성 상태 업데이트 - 승인 / 반려 / 미결", type = Description.TYPE.MEHTOD)
    public Result updateStatusPayment(CommonReqVo commonReqVo, @RequestBody @Valid PaymentForm.PaymentList payment) {
        log.info("updateStatusPayment: 기성 승인 요청 발생, API 실행 여부 = {}, 프로젝트 구분 = {}", commonReqVo.getApiYn(), commonReqVo.getPjtDiv());
        paymentComponent.updatePaymentList(payment.getPaymentList(), commonReqVo.getApiYn(), commonReqVo.getPjtDiv());

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("기성 승인 요청");
        systemLogComponent.addUserLog(userLog);


        return Result.ok();
    }


    /**
     * 기성 내역서의 공종 목록 트리 리스트 조회
     * @param payment
     * @return
     */
    @PostMapping("/payment/payment-cbs-list")
    @Description(name = "공종 목록 트리 리스트 조회", description = "기성 내역서의 공종 목록 트리 리스트 조회", type = Description.TYPE.MEHTOD)
    public Result getPaymentCbsList(@RequestBody @Valid PaymentForm.PaymentSelect payment) {

        return Result.ok()
                .put("paymentCbsList", paymentService.selectPaymentCbsList(payment.getCntrctNo(), payment.getPayprceSno(), payment.getCntrctChgId()));
    }


    /**
     * 기성 내역서 조회
     * @param payment
     * @return
     */
    @PostMapping("/payment/payment-history-list")
    @Description(name = "기성 내역서 조회", description = "기성 내역서 조회", type = Description.TYPE.MEHTOD)
    public Result getPaymentHistoryList(CommonReqVo commonReqVo, @RequestBody @Valid PaymentForm.PaymentSelect payment) {
        List<Map<String, ?>> result = paymentService.selectPaymentHistoryList(payment.getCntrctNo(), payment.getPayprceSno(), payment.getCntrctChgId(), payment.getCnsttySnList(), payment.getSearchText());

        // 조회 결과 없을 경우 빈 리스트 전달
        if(result == null || result.isEmpty()) {
            result =  Collections.emptyList();
        }


        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("기성 내역서 조회");
        systemLogComponent.addUserLog(userLog);

        return Result.ok()
                .put("paymentHistoryList", result);
    }


    /**
     * 원가 내역서 조회
     * @param payment
     * @return
     */
    @PostMapping("/payment/payment-cost-calculator-list")
    @Description(name = "원가 내역서 조회", description = "원가 내역서 조회", type = Description.TYPE.MEHTOD)
    public Result getPaymentCostCalculatorList(CommonReqVo commonReqVo, @RequestBody @Valid PaymentForm.PaymentSelect payment) {


        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("원가 승인 요청");
        systemLogComponent.addUserLog(userLog);

        return Result.ok()
                .put("paymentcostCalculatorList", paymentService.selectPaymentCostCalculatorList(payment.getCntrctNo(), payment.getPayprceSno(), payment.getCntrctChgId()));
    }



    /**
     * 기성 내역서 재집계 조회
     * @param payment
     * @return
     */
    @PostMapping("/payment/payment-history-recount-list")
    @Description(name = "기성 내역서 재집계 조회", description = "기성 내역서 재집계 조회", type = Description.TYPE.MEHTOD)
    public Result getPaymentHistoryRecountList(CommonReqVo commonReqVo, @RequestBody @Valid PaymentForm.PaymentSelect payment) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("기성 내역서 재집계 조회");
        systemLogComponent.addUserLog(userLog);

        return Result.ok()
                .put("paymentHistoryRecountList", paymentService.selectPaymentHistoryRecountList(payment.getCntrctNo(), payment.getPayprceSno(), payment.getCntrctChgId(), payment.getSearchText()));
    }

    /**
     * 기성 내역수량 업데이트
     * @param payment
     * @return
     */
    @PostMapping("/payment/update-recount-payment")
    @Description(name = "기성 내역수량 업데이트", description = "기성 내역수량 업데이트", type = Description.TYPE.MEHTOD)
    public Result updateRecountPayment(CommonReqVo commonReqVo, @RequestBody @Valid PaymentForm.PaymentRecountList payment) {
        String userId = commonReqVo.getUserId();
        paymentService.updateRecountPaymentList(payment.getPaymentRecountList(),userId);

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("기성 내역수량 업데이트");
        systemLogComponent.addUserLog(userLog);

        return Result.ok();
    }


    /**
     * 기성 내역수량 재집계 내역 가져오기 조회
     * @param payment
     * @return
     */
    @PostMapping("/payment/payment-history-new-list")
    @Description(name = "기성 내역수량 재집계 내역 가져오기 조회", description = "기성 내역수량 재집계 내역 가져오기 조회", type = Description.TYPE.MEHTOD)
    public Result selectPaymentHistoryNewList(CommonReqVo commonReqVo, @RequestBody @Valid PaymentForm.PaymentSelect payment) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("기성 내역수량 재집계 내역 가져오기 조회");
        systemLogComponent.addUserLog(userLog);

        return Result.ok()
                .put("paymentHistoryNewList", paymentService.selectPaymentHistoryNewList(payment.getCntrctNo(), payment.getPayprceSno(), payment.getCntrctChgId(), payment.getDailyReportDate(), payment.getPayprceYm()));
    }

}
