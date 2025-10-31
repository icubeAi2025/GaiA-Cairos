package kr.co.ideait.platform.gaiacairos.comp.system.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import org.springframework.stereotype.Service;

import kr.co.ideait.platform.gaiacairos.core.util.UtilMybatisParam.ComCodeSelectInput;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ComUtilService extends AbstractGaiaCairosService {
	
    /**
     * 공통코드 가져오기
     * @param String (공통코드 그룹코드)
     * @return List
     * @throws 
     */
    public List selectComCodeList(String cmnGrpCd, String orderByCol, String orderByType){
    	ComCodeSelectInput comCodeSelectInput = new ComCodeSelectInput();
    	comCodeSelectInput.setCmnGrpCd(cmnGrpCd);
    	comCodeSelectInput.setOrderByCol(orderByCol);
    	comCodeSelectInput.setOrderByType(orderByType);
    	
        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.comUtil.selectComCodeList", comCodeSelectInput);
    }
    
    /**
     * selectBox, radio 상자 만들 데이터 가져오기
     * @param String (공통코드 그룹코드)
     * @return List
     * @throws 
     */
    public List selectMakeDataListUsingCondition(String col1, String col2, String tName, String[] param, String orderByCol, String orderByType){
    	Map map = new HashMap();
        map.put("col1", col1);
        map.put("col2", col2);
        map.put("tName", tName);
        map.put("param1", param[0]);
        map.put("param2", Integer.parseInt(param[1]));
        map.put("orderByCol", orderByCol);
    	
        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.comUtil.selectMakeDataListUsingCondition", map);
    }

}
