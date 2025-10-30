package kr.co.ideait.platform.gaiacairos.comp.system.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import kr.co.ideait.platform.gaiacairos.core.constant.CommonCodeConstants;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmMenuAuthorityGroup;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.SmMenuAuthorityGroupRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisOutput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.MenuAuthorityGorupMybatisParam.MenuAuthorityGroupListInput;

@Slf4j
@Service
public class MenuAuthorityGroupService extends AbstractGaiaCairosService {

    @Autowired
    SmMenuAuthorityGroupRepository smMenuAuthorityGroupRepository;

    // 해당 권한그룹이 회사/ 부서/ 사용자/ 롤 인지에 따라서 조회해 오는 내용이 상이함. (추후 확인 필요)
    public List<MybatisOutput> getMenuAuthorityGroupList(MenuAuthorityGroupListInput input) {

        log.debug("==============================================");
		log.debug("selectMenuAuthorityGroupList : " + input.getCntrctNo() + " // " + input.getSystemType());
    	log.debug("==============================================");

        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.menu_authority_group.selectMenuAuthorityGroupList", input);
    }

    /**
     * 메뉴 권한 그룹 조회 - 그리드 페이징 처리
     * @param input
     * @return
     */
    @Transactional
    public Page<MybatisOutput> getMenuAuthorityGroupPage(MenuAuthorityGroupListInput input) {

        log.debug("==============================================");
		log.debug("selectMenuAuthorityGroupList : " + input.getCntrctNo() + " // " + input.getSystemType());
    	log.debug("==============================================");
        List<MybatisOutput> resultList = mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.menu_authority_group.selectMenuAuthorityGroupList", input);
        
        Long totalCount = (long) 0;
        
        if(!resultList.isEmpty()) {
        	totalCount = (Long) resultList.get(0).get("cnt");
        }

        // Page 객체 생성하여 반환
        return new PageImpl<>(resultList, input.getPageable(), totalCount);
    }

    /**
     * 메뉴권한 추가 > 권한 그룹 리스트 조회
     * @param input
     * @return
     */
    public List<MybatisOutput> getAuthorityGroupList(MenuAuthorityGroupListInput input){

        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.menu_authority_group.selectAuthorityGroupList", input);
    }
    
    /**
     * 메뉴권한관리 > 메뉴권한 추가&수정 시 선택 메뉴의 설정 권한 전체 목록 조회
     * @param input
     * @return
     */
    public List<Map<String, Object>> getMenuAuthorityList(String menuCd){
    	
    	MybatisInput input = MybatisInput.of().add("menuCd", menuCd).add("cmnGrpCd", CommonCodeConstants.KIND_CODE_GROUP_CODE);

        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.menu_authority_group.selectMenuAuthorityList", input);
    }

    /**
     * 메뉴 권한 그룹 리스트 추가
     * @param smMenuAuthorityGroupList
     * @return
     */
    @Transactional
    public List<SmMenuAuthorityGroup> createMenuAuthorityGroup(List<SmMenuAuthorityGroup> smMenuAuthorityGroupList) {
        return smMenuAuthorityGroupRepository.saveAll(smMenuAuthorityGroupList);
    }

    /**
     * 메뉴 권한 그룹 리스트 삭제
     * @param
     */
    @Transactional
    public void deleteMenuAuthorityGroupList(List<Integer> menuRghtNoList) {
        smMenuAuthorityGroupRepository.findAllById(menuRghtNoList).forEach(menuAuthorityGroup -> {
            smMenuAuthorityGroupRepository.updateDelete(menuAuthorityGroup);
        });
    }

    /**
     * 메뉴권한 조회
     * @param menuRghtNo
     * @return
     */
	public SmMenuAuthorityGroup getAuthorityGroup(Integer menuRghtNo) {
        return smMenuAuthorityGroupRepository.findById(menuRghtNo).orElse(null);
	}

	
	public SmMenuAuthorityGroup updateMenuAuthorityGroup(SmMenuAuthorityGroup smMenuAuthorityGroup) {
		return smMenuAuthorityGroupRepository.save(smMenuAuthorityGroup);
	}
	
	/**
     * 그룹에 메뉴권한 입력
     * @param menuCd
     * @param rghtGrpCd
     * @param rghtKind
     * @return
     */
	public void insertMenuAuthorityGroupRghtKind(String menuCd, String rghtGrpCd, String rghtKind, String rgstrId) {
        MybatisInput input = MybatisInput.of().add("menuCd", menuCd).add("rghtGrpCd", rghtGrpCd).add("rghtKind", rghtKind).add("rgstrId",rgstrId);
		mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.menu_authority_group.insertMenuAuthorityGroupRghtKind", input);
	}

	/**
     * 그룹에 메뉴권한 삭제(수정으로 삭제)
     * @param menuCd
     * @param rghtGrpCd
     * @param rghtKind
     * @return
     */
	public void deleteMenuAuthorityGroupRghtKind(String menuCd, String rghtGrpCd, String rghtKind, String rgstrId) {

		MybatisInput input = MybatisInput.of().add("menuCd", menuCd).add("rghtGrpCd", rghtGrpCd).add("rghtKind", rghtKind).add("rgstrId", rgstrId);
		mybatisSession.delete("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.menu_authority_group.deleteMenuAuthorityGroupRghtKind", input);
	}
	
	/**
     * 메뉴에 그룹권한 삭제 (그리드에서 체크박스 또는 휴지통으로 삭제)
     * @param menuCd
     * @param rghtGrpCd
     * @param rghtKind
     * @return
     */
	public void deleteMenuAuthorityGroup(String menuCd, String rghtGrpCd, String rgstrId) {

		MybatisInput input = MybatisInput.of().add("menuCd", menuCd).add("rghtGrpCd", rghtGrpCd).add("rgstrId", rgstrId);
		mybatisSession.delete("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.menu_authority_group.deleteMenuAuthorityGroup", input);
	}

}
