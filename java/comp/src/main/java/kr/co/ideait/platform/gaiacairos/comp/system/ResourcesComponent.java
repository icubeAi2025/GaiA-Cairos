package kr.co.ideait.platform.gaiacairos.comp.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.co.ideait.platform.gaiacairos.comp.system.service.ResourcesService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractComponent;
import kr.co.ideait.platform.gaiacairos.core.constant.CommonCodeConstants;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.resources.ResourcesForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.resources.ResourcesMybatisParam.ResourcesInfoInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.resources.ResourcesMybatisParam.ResourcesInsertInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.resources.ResourcesMybatisParam.ResourcesListInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.resources.ResourcesMybatisParam.ResourcesListOutput;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Maps;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResourcesComponent extends AbstractComponent {
	
	private final ResourcesService resourcesService;
	
	private final ResourcesForm resourcesForm;
	
	/**
     * 프로그램 목록조회
     * @param ResourcesForm.ResourcesListForm
     * @return Page<ResourcesListOutput>
     */
	public Page<ResourcesListOutput> getResourcesList(ResourcesForm.ResourcesListForm resourcesListForm) {
		
		ResourcesListInput input = resourcesForm.toResourcesListInput(resourcesListForm);
        input.setCmnGrpCd(CommonCodeConstants.KIND_CODE_GROUP_CODE);  

        List<ResourcesListOutput> resourcesList = resourcesService.selectResourcesList(input);
        
        
        long totalCount = resourcesList.size();
 		if (totalCount != 0) {
 			totalCount = resourcesList.get(0).getCnt();
 			
 			for(ResourcesListOutput obj:resourcesList) {
 				if(obj.getMenuDepth() != null && !"".equals(obj.getMenuDepth())) {
 	 				String menuNm[] = obj.getMenuDepth().replace("\"", "").replace("{", "").replace("}", "").split(",");
 	 				
 	 				String fullMenuNm = String.join(" <", menuNm);
 	 				
 	 				obj.setMenuNm(fullMenuNm); 					
 				}else {
 					obj.setMenuNm("공통");
 				}
 			}
 		}

        return new PageImpl<>(resourcesList, resourcesListForm.getPageable(), totalCount);
    }
	
	/**
     * 프로그팸 정보 상세조회
     * @param ResourcesForm.ResourcesReadForm
     * @return Map<String, Object>
     */
	public Map<String, Object> getResourcesInfo(ResourcesForm.ResourcesReadForm resourcesReadForm) {
		
		ResourcesInfoInput input = resourcesForm.toResourcesInfoInput(resourcesReadForm);
        input.setCmnGrpCd(CommonCodeConstants.KIND_CODE_GROUP_CODE);  

        return resourcesService.selectResourcesInfo(input);
    }
	
	/**
     * 프로그램 정보 등록
     * @param ResourcesForm.ResourcesInsertForm
     * @return int
     */
	@Transactional
	public int createResourcesInfo(ResourcesForm.ResourcesInsertForm resourcesInsertForm, String apiYn) {
		
		ResourcesInsertInput resourcesInsertInput = resourcesForm.toResourcesInsertInput(resourcesInsertForm);
		resourcesInsertInput.setUsrId(UserAuth.get(true).getUsrId());
		
		int addCount = resourcesService.insertResourcesInfo(resourcesInsertInput);
		
		if("Y".equals(apiYn) && addCount > 0) {		
			Map<String, Object> invokeParams = Maps.newHashMap();			
	        invokeParams.put("resourcesInsertInput", resourcesInsertInput);
			
	        Map response = invokeCairos2Pgaia("ociTOncp1001", invokeParams);
	        
	        if (!"00".equals(MapUtils.getString(response, "resultCode"))) {
                throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(response, "resultMsg"));
            }
		}

        return addCount;
    }
	
	/**
     * 프로그램 아이디 중복검사
     * @param ResourcesForm.ResourcesExistForm
     * @return String
     */
	public String getResourcesIdExist(ResourcesForm.ResourcesExistForm resourcesExistForm) {

        return resourcesService.selectResourcesIdExist(resourcesExistForm.getExistParam());
    }
	
	/**
     * 프로그램 URI 중복검사
     * @param ResourcesForm.ResourcesExistForm
     * @return String
     */
	public String getResourcesUrlExist(ResourcesForm.ResourcesExistForm resourcesExistForm) {

        return resourcesService.selectResourcesUrlExist(resourcesExistForm.getExistParam());
    }
	
	/**
     * 프로그램 정보 수정
     * @param ResourcesForm.ResourcesInsertForm
     * @return int
     */
	@Transactional
	public int updateResourcesInfo(ResourcesForm.ResourcesInsertForm resourcesInsertForm, String apiYn) {
		
		ResourcesInsertInput resourcesInsertInput = resourcesForm.toResourcesInsertInput(resourcesInsertForm);
		resourcesInsertInput.setUsrId(UserAuth.get(true).getUsrId());
		
		int updateCount = resourcesService.updateResourcesInfo(resourcesInsertInput);
		
		if (!StringUtils.isEmpty(resourcesInsertInput.getMenuCd())) {
			resourcesInsertInput.setCmnGrpCd(CommonCodeConstants.KIND_CODE_GROUP_CODE);
			resourcesService.deleteAuthorityMapping(resourcesInsertInput);
		}
		
		if("Y".equals(apiYn)) {		
			Map<String, Object> invokeParams = Maps.newHashMap();			
	        invokeParams.put("resourcesInsertInput", resourcesInsertInput);
			
	        Map response = invokeCairos2Pgaia("ociTOncp1002", invokeParams);
	        
	        if (!"00".equals(MapUtils.getString(response, "resultCode"))) {
                throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(response, "resultMsg"));
            }
		}
		
        return updateCount;
    }
	
	/**
     * 프로그램 정보 삭제
     * @param ResourcesForm.ResourcesInsertForm
     * @return int
     */
	@Transactional
	public int deleteResourcesInfo(ResourcesForm.RescIdList rescIdList, String apiYn) {
		
		List<Map<String, Object>> rescDelList = new ArrayList<Map<String, Object>>();
        
        for(String rescId:rescIdList.getRescIdList()) {
        	Map<String, Object> map = new HashMap<String, Object>();
        	map.put("rescId", rescId);
        	map.put("usrId", UserAuth.get(true).getUsrId());
        	rescDelList.add(map);
        }
        
        int delCount = resourcesService.deleteResourcesInfo(rescDelList);
        
        if("Y".equals(apiYn) && delCount > 0) {		
			Map<String, Object> invokeParams = Maps.newHashMap();			
	        invokeParams.put("rescDelList", rescDelList);
			
	        Map response = invokeCairos2Pgaia("ociTOncp1003", invokeParams);
	        
	        if (!"00".equals(MapUtils.getString(response, "resultCode"))) {
                throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(response, "resultMsg"));
            }
		}

        return delCount;
    }
	
	/**
     * 프로그램관리 API 통신 처리
     * @param 	String (MID 메시지아디디)
     * @param 	Map	   (처리 할 데이터)	
     * @return Map
     */
	@Transactional
    public Map<String, Object> receiveInterfaceService(String transactionId, Map<String, Object> params) {
        Map<String, Object> result = Maps.newHashMap();
        result.put("resultCode", "00");

        try{
            // 처리 대상 transactionId만 허용            
            if ("ociTOncp1001".equals(transactionId)) { // 프로그램 등록
        
            	ResourcesInsertInput resourcesInsertInput = objectMapper.convertValue(params.get("resourcesInsertInput"), new TypeReference<ResourcesInsertInput>() {});
		
            	int addCount = resourcesService.insertResourcesInfo(resourcesInsertInput);

                if (addCount < 1) {
                    result.put("resultCode", "01");
                    result.put("resultMsg", "프로그램을 등록하지 못했습니다.");
                    return result;
                }
                
                result.put("resultMsg", "프로그램을 성공적으로 등록했습니다.");
            } else if("ociTOncp1002".equals(transactionId)){ // 프로그램 수정
            	ResourcesInsertInput resourcesInsertInput = objectMapper.convertValue(params.get("resourcesInsertInput"), new TypeReference<ResourcesInsertInput>() {});
            	
            	int updateCount = resourcesService.updateResourcesInfo(resourcesInsertInput);
        		
        		if (!StringUtils.isEmpty(resourcesInsertInput.getMenuCd())) {
        			resourcesInsertInput.setCmnGrpCd(CommonCodeConstants.KIND_CODE_GROUP_CODE);
        			resourcesService.deleteAuthorityMapping(resourcesInsertInput);
        		}
            	
        		if (updateCount < 1) {
                    result.put("resultCode", "01");
                    result.put("resultMsg", "프로그램을 수정하지 못했습니다.");
                    return result;
                }
                
                result.put("resultMsg", "프로그램을 성공적으로 수정했습니다.");
            	
            } else if("ociTOncp1003".equals(transactionId)){ // 프로그램 삭제
            	List<Map<String, Object>> rescDelList = objectMapper.convertValue(params.get("rescDelList"), new TypeReference<List<Map<String, Object>>>() {});

            	int delCount = resourcesService.deleteResourcesInfo(rescDelList);

            	if (delCount < 1) {
                    result.put("resultCode", "01");
                    result.put("resultMsg", "프로그램을 삭제하지 못했습니다.");
                    return result;
                }
            	
            	result.put("resultMsg", "프로그램을 성공적으로 삭제했습니다.");
            }
        } catch (GaiaBizException e) {
            log.error(e.getMessage(), e);
            result.put("resultCode", "01");
            result.put("resultMsg", String.format("오류가 발생했습니다!! 오류 메시지는 [%s] 입니다.",e.getMessage()));
        }

        return result;
    }
}
