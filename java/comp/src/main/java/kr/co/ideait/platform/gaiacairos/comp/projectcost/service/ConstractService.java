package kr.co.ideait.platform.gaiacairos.comp.projectcost.service;
 
import java.util.List;
import java.util.Map;

import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.ApAttachmentsRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.ApDocRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.ApLineRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.projectcost.ConstractMybatisParam.ConstractFormTypeSelectInput;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ConstractService extends AbstractGaiaCairosService {
	
    @Autowired
    ApDocRepository apDocRepository;
    
    @Autowired
    ApLineRepository apLineRepository;
    
    @Autowired
    ApAttachmentsRepository apAttachmentsRepository;
    
    /**
     * 계약 변경 리스트 가져오기
     * @param String (프로젝트 타입)
     * @return List
     * @throws 
     */
    public List selectContractChangeList(){
    	ConstractFormTypeSelectInput constractFormTypeSelectInput = new ConstractFormTypeSelectInput();
//    	constractFormTypeSelectInput.setPjtType(pjtType);
//    	constractFormTypeSelectInput.setPjtNo(UserAuth.get(true).getPjtNo());
//    	constractFormTypeSelectInput.setCntrctNo(UserAuth.get(true).getCntrctNo());
    	//draftFormTypeSelectInput.setPjtNo("G202405006");
    	//draftFormTypeSelectInput.setCntrctNo("P202405008");
    	
        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.core.persistence.mybatis.projectcost.constract.selectContractChangeList", constractFormTypeSelectInput);
    }

    /**
     * 공종 목록 가져오기
     * @param String (프로젝트 타입)
     * @return List
     * @throws 
     */
    public List selectCbsList(String cntrctId, String chgId){
    	ConstractFormTypeSelectInput constractFormTypeSelectInput = new ConstractFormTypeSelectInput();
//    	constractFormTypeSelectInput.setPjtType(pjtType);
//    	constractFormTypeSelectInput.setPjtNo(UserAuth.get(true).getPjtNo());
//    	constractFormTypeSelectInput.setCntrctNo(UserAuth.get(true).getCntrctNo());
    	//draftFormTypeSelectInput.setPjtNo("G202405006");
    	//draftFormTypeSelectInput.setCntrctNo("P202405008");
    	
    	constractFormTypeSelectInput.setCntrctId(cntrctId);
		constractFormTypeSelectInput.setChgId(chgId);
        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.core.persistence.mybatis.projectcost.constract.selectCbsList", constractFormTypeSelectInput);
    }

    /**
     * 내역서 가져오기
     * @param cntrctNo 
     * @param String (프로젝트 타입)
     * @return List
     * @throws 
     */
    public List<Map<String, ?>> selectCbsDetailList(String cntrctId, String chgId, List<Integer> cnsttySnList, String searchText){
    	ConstractFormTypeSelectInput constractFormTypeSelectInput = new ConstractFormTypeSelectInput();
//    	constractFormTypeSelectInput.setPjtType(pjtType);
//    	constractFormTypeSelectInput.setPjtNo(UserAuth.get(true).getPjtNo());
//    	constractFormTypeSelectInput.setCntrctNo(UserAuth.get(true).getCntrctNo());
    	//draftFormTypeSelectInput.setPjtNo("G202405006");
    	//draftFormTypeSelectInput.setCntrctNo("P202405008");

    	constractFormTypeSelectInput.setCntrctId(cntrctId);
		constractFormTypeSelectInput.setChgId(chgId);
    	constractFormTypeSelectInput.setCnsttySnList(cnsttySnList);
    	constractFormTypeSelectInput.setSearchText(searchText);
    	
        return mybatisSession.selectList(
        		"kr.co.ideait.platform.gaiacairos.core.persistence.mybatis.projectcost.constract.selectCbsDetailList",
        		constractFormTypeSelectInput);
    }
}
