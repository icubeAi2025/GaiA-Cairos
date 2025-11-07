package kr.co.ideait.platform.gaiacairos.comp.system.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.resources.ResourcesMybatisParam.ResourcesInfoInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.resources.ResourcesMybatisParam.ResourcesInsertInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.resources.ResourcesMybatisParam.ResourcesListInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.resources.ResourcesMybatisParam.ResourcesListOutput;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ResourcesService extends AbstractGaiaCairosService {
	
	/**
	 * 프로그램 목록 조회
	 * 
	 * @param 
	 * @return 
	 * @throws
	 */
	public List<ResourcesListOutput> selectResourcesList(ResourcesListInput resourcesListInput) {
        
        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.resources.selectResourcesList", resourcesListInput);
	}
	
	/**
	 * 프로그램 상세조회
	 * 
	 * @param ResourcesInsertInput
	 * @return int
	 * @throws
	 */
	public Map<String, Object> selectResourcesInfo(ResourcesInfoInput resourcesInfoInput) {

		return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.resources.selectResourcesInfo", resourcesInfoInput);
	}
	
	/**
	 * 프로그램 등록
	 * 
	 * @param ResourcesInsertInput
	 * @return int
	 * @throws
	 */
	public int insertResourcesInfo(ResourcesInsertInput resourcesInfo) {

		return mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.resources.insertResourcesInfo", resourcesInfo);
	}
	
	/**
	 * 프로그램 아이디 중복검사
	 * 
	 * @param 
	 * @return 
	 * @throws
	 */
	public String selectResourcesIdExist(String rescId) {

		Map<String, Object> result = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.resources.rescIdExist", rescId);

		return (String) result.get("use_yn");
	}
	
	/**
	 * 프로그램 URL 중복검사
	 * 
	 * @param 
	 * @return 
	 * @throws
	 */
	public String selectResourcesUrlExist(String rescUrl) {

		Map<String, Object> result = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.resources.rescUrlExist", rescUrl);

		return (String) result.get("use_yn");
	}
	
	/**
	 * 프로그램 수정
	 * 
	 * @param ResourcesInsertInput
	 * @return int
	 * @throws
	 */
	public int updateResourcesInfo(ResourcesInsertInput resourcesInfo) {

		return mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.resources.updateResourcesInfo", resourcesInfo);
	}
	
	/**
	 * 권한 매핑 정보 삭제 (역할 수정시 없는 역할 삭제)
	 * 
	 * @param ResourcesInsertInput
	 * @return int
	 * @throws
	 */
	public int deleteAuthorityMapping(ResourcesInsertInput resourcesInfo) {

		return mybatisSession.delete("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.resources.deleteAuthorityMapping", resourcesInfo);
	}
	
	/**
	 * 프로그램 삭제
	 * 
	 * @param ResourcesInsertInput
	 * @return int
	 * @throws
	 */
	public int deleteResourcesInfo(List<Map<String, Object>> delResourcesList) {

		return mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.resources.deleteResourcesInfo", delResourcesList);
	}
}
