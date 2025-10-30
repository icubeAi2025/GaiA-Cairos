package kr.co.ideait.platform.gaiacairos.web.entrypoint.projectcost;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.comp.projectcost.DepositComponent;
import kr.co.ideait.platform.gaiacairos.comp.projectcost.service.DepositService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractController;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwFrontMoney;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.projectcost.deposit.DepositForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.util.UtilForm;
import kr.co.ideait.platform.gaiacairos.core.type.LogType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/projectcost")
public class DepositApiController extends AbstractController {

    @Autowired
    DepositComponent depositComponent;

    @Autowired
    DepositService depositService;

    @Autowired
    DepositForm depositForm;

//    @Autowired
//    DailyreportService dailyreportService;

    // private String pjtType = "CMIS";

    /**
     * 선급금 및 공제금 목록 조회
     * 
     * @param depositMain
     * @return
     */
    @PostMapping("/deposit/deposit-list")
    @Description(name = "선급금 및 공제금 목록 조회", description = "선급금 및 공제금 전체 데이터 조회", type = Description.TYPE.MEHTOD)
    public Result getDepositList(CommonReqVo commonReqVo, @RequestBody @Valid DepositForm.ProjectcostMain depositMain) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("선급금 및 공제금 목록 조회");
        systemLogComponent.addUserLog(userLog);

        return Result.ok()
                .put("depositList", depositService.selectDepositList(depositMain.getCntrctNo()));
    }

    /**
     * 선급금 및 공제금 추가
     * 
     * @param deposit
     * @return
     */
    @PostMapping("/deposit/add-deposit")
    @Description(name = "선급금 및 공제금 추가", description = "선급금 및 공제금 데이터 추가", type = Description.TYPE.MEHTOD)
    public Result addDeposit(CommonReqVo commonReqVo, @RequestBody @Valid DepositForm.DepositInsert deposit) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("선급금 및 공제금 추가");
        systemLogComponent.addUserLog(userLog);

        deposit.setDltYn("N");

        depositService.addDeposit(depositForm.DepositInsert(deposit));

        return Result.ok();
    }

    /**
     * 선급금 및 공제금 수정
     * 
     * @param deposit
     * @return
     */
    @PostMapping("/deposit/update-deposit")
    @Description(name = "선급금 및 공제금 수정", description = "선급금 및 공제금 데이터 수정", type = Description.TYPE.MEHTOD)
    public Result updateDeposit(CommonReqVo commonReqVo, @RequestBody @Valid DepositForm.DepositInsert deposit) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("선급금 및 공제금 수정");
        systemLogComponent.addUserLog(userLog);

        CwFrontMoney depositData = depositService.getDepositData(deposit.getCntrctNo(),
                deposit.getPpaymnySno());
        if (depositData != null) {
            deposit.setDltYn("N");
            depositForm.toUpdateCwFrontMoney(deposit, depositData);
            depositService.updateDeposit(depositData, deposit.getDiffAmt(), deposit.getOriType(), deposit.getOriAmt());
        }

        return Result.ok();
    }

    /**
     * 선급금 및 공제금 상세조회
     * 
     * @param commonReqVo
     * @param depositMain
     * @return
     */
    @PostMapping("/deposit/deposit-detail")
    @Description(name = "선급금 및 공제금 조회", description = "선급금 및 공제금 데이터 조회", type = Description.TYPE.MEHTOD)
    public Result getDepositDetail(CommonReqVo commonReqVo,
            @RequestBody @Valid DepositForm.ProjectcostDetailGet depositMain) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("선급금 및 공제금 조회");
        systemLogComponent.addUserLog(userLog);

        return Result.ok()
                .put("depositDetail",
                        depositService.getDeposit(depositMain.getCntrctNo(), depositMain.getPpaymnySno()));
    }

    /**
     * 선급금 및 공제금 삭제
     * 
     * @param commonReqVo
     * @param deposit
     * @return
     */
    @PostMapping("/deposit/del-deposit")
    @Description(name = "선급금 및 공제금 삭제", description = "선급금 및 공제금 데이터 삭제", type = Description.TYPE.MEHTOD)
    public Result delDeposit(CommonReqVo commonReqVo, @RequestBody @Valid DepositForm.DepositList deposit) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("선급금 및 공제금 삭제");
        systemLogComponent.addUserLog(userLog);

        depositComponent.delDeposit(deposit.getDepositList());

        return Result.ok();
    }

    /**
     * 선급금 및 공제금 승인 상태 업데이트
     * 
     * @param deposit
     * @return
     */
    @PostMapping("/deposit/update-status-deposit")
    @Description(name = "선급금 및 공제금 상태 업데이트", description = "선급금 및 공제금 상태 업데이트", type = Description.TYPE.MEHTOD)
    public Result updateStatusDeposit(CommonReqVo commonReqVo, @RequestBody @Valid DepositForm.DepositList deposit) {

        Log.SmUserLogDto userLog = commonReqVo.toSmUserLogDto();
        userLog.setLogType(LogType.FUNCTION.name());
        userLog.setExecType("선급금 및 공제금 상태 업데이트");
        systemLogComponent.addUserLog(userLog);

        depositComponent.updateDepositList(deposit.getDepositList(), commonReqVo.getApiYn(), commonReqVo.getPjtDiv());

        return Result.ok();
    }

    /**
     * 기성회차 가져오기  -- 기성에 맞게 수정해야됨
     */
    @PostMapping("/get/payprceTmnum")
    @Description(name = "기성회차 가져오기", description = "기성회차 가져오기", type = Description.TYPE.MEHTOD)
    public Result getCommonBoxList(CommonReqVo commonReqVo,
            @RequestBody @Valid List<UtilForm.CommonBoxGet> commonBoxGet) {

        Map<String, Object> returnMap = new HashMap<String, Object>();
    	if(commonBoxGet.size() > 0) {
    		
			log.debug("=====================================================================================");
			log.debug("commonBoxGet.size() : " +commonBoxGet.size());
			log.debug("=====================================================================================");
  
    		for(UtilForm.CommonBoxGet makeBox : commonBoxGet) {
    			String orderByCol = (makeBox.getOrderByCol() == null || makeBox.getOrderByCol().isBlank()) ? makeBox.getCol2() : makeBox.getOrderByCol(); 
    			String orderByType = (makeBox.getOrderByType() == null || makeBox.getOrderByType().isBlank()) ? "ASC" : makeBox.getOrderByType(); 
    			String ckeckValue = (makeBox.getCkeckedValue() == null || makeBox.getCkeckedValue().isBlank()) ? "" : makeBox.getCkeckedValue(); 
    			String initType = (makeBox.getInitText() == null || makeBox.getInitText().isBlank()) ? "noInit" :"init"; 
    			String addSql = (makeBox.getAddSql() == null || makeBox.getAddSql().isBlank()) ? "" :makeBox.getAddSql(); 
    			
    			log.debug("=====================================================================================");
    			log.debug("                                   파라미터 확인                                     ");
    			log.debug("makeBox.getMakeId() 			: " + makeBox.getMakeId()                       		 );
    			log.debug("makeBox.getCol1() 			: " + makeBox.getCol1()                       			 );
    			log.debug("makeBox.getCol2()	 		: " + makeBox.getCol2()                       			 );
    			log.debug("makeBox.getTableName() 		: " + makeBox.getTableName()                   			 );
    			log.debug("makeBox.getAddSql() 			: " + makeBox.getAddSql()                      			 );
    			log.debug("makeBox.getOrderByCol() 		: " + makeBox.getOrderByCol()                 			 );
    			log.debug("makeBox.getOrderByType() 	: " + makeBox.getOrderByType()              			 );
    			log.debug("makeBox.getBoxType() 		: " + makeBox.getBoxType()                    			 );
    			log.debug("makeBox.getCkeckedValue() 	: " + makeBox.getCkeckedValue()               			 );
    			log.debug("makeBox.getInitText() 		: " + makeBox.getInitText()                    			 );
    			log.debug("makeBox.getParamNm() 		: " + makeBox.getParamNm()                     			 );
    			log.debug("=====================================================================================");

    			String [] param = makeBox.getAddSql().split(",");

    			@SuppressWarnings("unchecked")
				List<Map<String, Object>> selectSrcList = depositService.getPayprceTmnum(makeBox.getCol1(), makeBox.getCol2(), makeBox.getTableName(), param, orderByCol, orderByType);
    			
    			StringBuilder makItem = new StringBuilder();
    			
    			if("selectBox".equals(makeBox.getBoxType())) {
    				
    				makItem.append("<select id='").append(makeBox.getMakeId());
    				if(makeBox.getFunName() != null && !makeBox.getFunName().isBlank()) {
    					makItem.append("' ").append(makeBox.getFuntype()).append("='").append(makeBox.getFunName()).append("(").append(makeBox.getFunParam()).append(")");
        			}
    				makItem.append("'>");
    				
        			if("init".equals(initType)) {
        				makItem.append("<option value=''>").append(makeBox.getInitText()).append("</option>");
        			}
        			for(Map<String, Object> selectSrc : selectSrcList){

        				log.debug("selectBoxSrc : " + selectSrc);
        				log.debug("selectBoxSrc.toString() : " + selectSrc.toString());
            			String selectedAttribute = (selectSrc.get("item_value").equals(ckeckValue)) ? " selected" : "";
            			makItem.append("<option value='").append(selectSrc.get("item_value")).append("'").append(selectedAttribute).append(">").append(selectSrc.get("item_text")).append("</option>");
        			}
        			makItem.append("</select>");    				
    			}else if("radioBox".equals(makeBox.getBoxType())) {
    				
        			for(Map<String, Object> selectSrc : selectSrcList){
        				log.debug("selectBoxSrc : " + selectSrc);
        				log.debug("selectBoxSrc.toString() : " + selectSrc.toString());
            			String selectedAttribute = (selectSrc.get("item_value").equals(ckeckValue)) ? " selected" : "";
            			makItem.append("<input type='radio' id='").append(makeBox.getMakeId()).append("' value='").append(selectSrc.get("item_value")).append("' ").append(selectedAttribute).append(">").append(selectSrc.get("item_text"));
        			}
    			}    			
    			
    			log.debug("=====================================================================================");
    			log.debug("                                  셀렉트 박스 내용                                   ");
    			log.debug(makItem.toString());
    			log.debug("=====================================================================================");
    			log.debug("변수 명 : " + makeBox.getParamNm());
    			log.debug("=====================================================================================");
    			
    			returnMap.put(makeBox.getParamNm(), makItem.toString());
    		}    		
    	}
    	
    	return Result.ok().put("returnMap", returnMap);
    }
}
