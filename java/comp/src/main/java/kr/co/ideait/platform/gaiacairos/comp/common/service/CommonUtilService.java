package kr.co.ideait.platform.gaiacairos.comp.common.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import kr.co.ideait.platform.gaiacairos.core.constant.CommonCodeConstants;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.util.UtilMybatisParam.ComCodeSelectInput;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CommonUtilService extends AbstractGaiaCairosService {
	
    /**
     * 공통코드 가져오기
     * @param  ComCodeSelectInput
     * @return List<Map<String, Object>>
     * @throws 
     */
    public List<Map<String, Object>> selectComCodeList(ComCodeSelectInput comCodeSelectInput){
    	
        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.common.selectComCodeList", comCodeSelectInput);
    }
    
    /**
     * 전체 메뉴 목록 가져오기
     * @return List<Map<String, Object>>
     * @throws 
     */
    public List<Map<String, Object>> selectAllMenuList(){
    	
        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.common.selectAllMenuList");
    }
    
    /**
     * 프로젝트 지역코드 가져오기
     * @return String
     * @throws 
     */
    public String selectProjectRgnCd(String pjtNo){
    	
        return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.common.selectProjectRgnCd", pjtNo);
    }
    
    /**
     * 요청 일자의 프로젝트 지역 일기 및 예보 가져오기
     * @return Map<String, Object>
     * @throws 
     */
    public Map<String, Object> selectProjectWeatherInfo(MybatisInput input){
    	
        return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.common.selectProjectWeatherInfo", input);
    }
    
    /**
     * 전체 예보 지점 및 지역코드 가져오기
     * @return List<Map<String, Object>>
     * @throws 
     */
    public List<Map<String, Object>> selectAllWeatherCode(){
    	
        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.common.selectAllWeatherCode", CommonCodeConstants.KMA_CODE_GROUP_CODE);
    }
    
    /**
	 * 일기 등록
	 * 
	 * @param ResourcesInsertInput
	 * @return int
	 * @throws
	 */
	public int insertSmWeatherInfo(List<Map<String, Object>> insertSmWeatherList) {

		return mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.common.insertSmWeatherInfo", insertSmWeatherList);
	}
	
	/**
	 * 예보 등록
	 * 
	 * @param ResourcesInsertInput
	 * @return int
	 * @throws
	 */
	public int insertSmForecastInfo(List<Map<String, Object>> insertSmForecastList) {

		return mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.common.insertSmForecastInfo", insertSmForecastList);
	}
	
	/**
     * 다운로드 받을 파일 정보 조회
     * @return Map<String, Object>
     * @throws 
     */
    public Map<String, Object> selectDownloadFileInfo(MybatisInput input){
    	
        return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.common.selectDownloadFileInfo", input);
    }
}