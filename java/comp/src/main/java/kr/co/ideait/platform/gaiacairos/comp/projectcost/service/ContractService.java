package kr.co.ideait.platform.gaiacairos.comp.projectcost.service;

import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.ApAttachmentsRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.ApDocRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.ApLineRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.projectcost.ContractMybatisParam.ContractFormTypeSelectInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.projectcost.PaymentMybatisParam.PaymentFormTypeSelectInput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ContractService extends AbstractGaiaCairosService {
	
    @Autowired
    ApDocRepository apDocRepository;
    
    @Autowired
    ApLineRepository apLineRepository;
    
    @Autowired
    ApAttachmentsRepository apAttachmentsRepository;

    /**
     * 계약 리스트 가져오기
     * @param String (프로젝트 타입)
     * @return List
     * @throws 
     */
    public List selectContractList(String pjtNo){
    	PaymentFormTypeSelectInput paymentFormTypeSelectInput = new PaymentFormTypeSelectInput();
    	
    	paymentFormTypeSelectInput.setPjtNo(pjtNo);
    	
        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.projectcost.contract.selectContractList", paymentFormTypeSelectInput);
    }
    
    /**
     * 계약 변경 리스트 가져오기
     * @param String (프로젝트 타입)
     * @return List
     * @throws 
     */
    public List selectContractChangeList(String cntrctNo){
    	ContractFormTypeSelectInput contractFormTypeSelectInput = new ContractFormTypeSelectInput();
    	
    	contractFormTypeSelectInput.setCntrctNo(cntrctNo);
    	
        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.projectcost.contract.selectContractChangeList", contractFormTypeSelectInput);
    }

    /**
     * 공종 목록 가져오기
     * @param String (프로젝트 타입)
     * @return List
     * @throws 
     */
    public List selectCbsList(String cntrctId, String chgId){
    	ContractFormTypeSelectInput contractFormTypeSelectInput = new ContractFormTypeSelectInput();
    	
    	contractFormTypeSelectInput.setCntrctId(cntrctId);
		contractFormTypeSelectInput.setChgId(chgId);
        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.projectcost.contract.selectCbsList", contractFormTypeSelectInput);
    }

    /**
     * 내역서 리스트 가져오기
     * @param cntrctNo 
     * @param String (프로젝트 타입)
     * @return List
     * @throws 
     */
    public List<Map<String, ?>> selectCbsDetailList(String cntrctId, String chgId, List<Integer> cnsttySnList, String searchText){
    	ContractFormTypeSelectInput contractFormTypeSelectInput = new ContractFormTypeSelectInput();

    	contractFormTypeSelectInput.setCntrctId(cntrctId);
		contractFormTypeSelectInput.setChgId(chgId);
    	contractFormTypeSelectInput.setCnsttySnList(cnsttySnList);
    	contractFormTypeSelectInput.setSearchText(searchText);

    	
        return mybatisSession.selectList(
        		"kr.co.ideait.platform.gaiacairos.mybatis.mappers.projectcost.contract.selectCbsDetailList",
        		contractFormTypeSelectInput);
    }

    /**
     * 내역서 가져오기
     * @param cntrctNo 
     * @param String (프로젝트 타입)
     * @return List
     * @throws 
     */
    public List<Map<String, ?>> selectCbsDetail(String id, String cd, String type){
    	ContractFormTypeSelectInput contractFormTypeSelectInput = new ContractFormTypeSelectInput();

    	contractFormTypeSelectInput.setId(id);
    	contractFormTypeSelectInput.setCd(cd);
		contractFormTypeSelectInput.setType(type);
    	
        return mybatisSession.selectList(
        		"kr.co.ideait.platform.gaiacairos.mybatis.mappers.projectcost.contract.selectCbsDetail",
        		contractFormTypeSelectInput);
    }

    /**
     * 원가계산서 리스트 가져오기
     * @param cntrctNo 
     * @param String (프로젝트 타입)
     * @return List
     * @throws 
     */
    public List<Map<String, ?>> selectCostCalculatorList(String chgId, String cntrctId) {
        log.info("selectCostCalculatorList: 원가 계산서 리스트 가져오기 chgId = {} cntrctId = {}", chgId, cntrctId);

        ContractFormTypeSelectInput contractFormTypeSelectInput = new ContractFormTypeSelectInput();

        contractFormTypeSelectInput.setChgId(chgId);
        contractFormTypeSelectInput.setCntrctId(cntrctId);
        List<Map<String, ?>> result = null;
        try {
            result = mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.projectcost.contract.selectCostCalculatorList", contractFormTypeSelectInput);
            log.info("selectCostCalculatorList: 원가 계산서 리스트 조회 결과 result = {}", result);
        } catch (GaiaBizException e) {
            log.info("selectCostCalculatorList: 원가 계산서 리스트 미존재");
        }
        return result;


    }
}
