package kr.co.ideait.platform.gaiacairos.comp.system.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Maps;
import kr.co.ideait.platform.gaiacairos.comp.mail.MailComponent;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import kr.co.ideait.platform.gaiacairos.core.constant.CommonCodeConstants;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmComCode;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmComCodeGroup;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.SmComCodeGroupRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.SmComCodeRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.document.DocumentForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.commoncode.CommonCodeDto;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.commoncode.CommonCodeMybatisParam.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CommonCodeService extends AbstractGaiaCairosService {

    @Autowired
    SmComCodeGroupRepository smComCodeGroupRepository;

    @Autowired
    SmComCodeRepository smComCodeRepository;

    // 그룹코드
    // -----------------------------------------------------------------------------------------------------

    public List<CommonCodeDto.SmComCodeGroup> getCommonCodeGroupList() {
        log.debug("commonCodeGroupNo={}");
        return mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.commoncode.getCommonCodeGroupList");
    }

    public SmComCodeGroup getCommonCodeGroup(int cmnGrpNo) {
        CommonCodeDto.SmComCodeGroup smComCodeGroup = new CommonCodeDto.SmComCodeGroup();
        smComCodeGroup.setCmnGrpNo(cmnGrpNo);
        smComCodeGroup = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.commoncode.selectSmComCodeGroup",smComCodeGroup);
//        return smComCodeGroupRepository.findById(commonCodeGroupNo).orElse(null);
        return toggleSmComCodeGroup(smComCodeGroup);
    }

    public boolean existCommonCodeGroup(Integer upCmnGrpNo, String cmnCd) {
        CommonCodeDto.SmComCodeGroup smComCodeGroup = new CommonCodeDto.SmComCodeGroup();
        smComCodeGroup.setCmnCd(cmnCd);
        smComCodeGroup.setDltYn("N");
        CommonCodeDto.SmComCodeGroup result = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.commoncode.selectSmComCodeGroup",smComCodeGroup);
        return result != null;
    }

    @Transactional
    public SmComCodeGroup createCommonCodeGroup(SmComCodeGroup smComCodeGroup) {
        return createCommonCodeGroup(smComCodeGroup, null);
    }
    @Transactional
    public SmComCodeGroup createCommonCodeGroup(SmComCodeGroup smComCodeGroup, String userId) {
//        Short maxDisplayOrder = smComCodeGroupRepository.maxCdDsplyOrdrByUpCmnGrpCd(smComCodeGroup.getUpCmnGrpCd());
        Short maxDisplayOrder = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.commoncode.selectMaxCmnCdDsplyOrdrByUpCmnGrpCd",smComCodeGroup.getUpCmnGrpCd());
        if (maxDisplayOrder == null) {
            maxDisplayOrder = (short) 1;
        } else {
            maxDisplayOrder++;
        }
        CommonCodeDto.SmComCodeGroup mybatisParam = toggleSmComCodeGroup(smComCodeGroup);
        String cmnGrpCd = mybatisParam.getCmnGrpCd();
        if(cmnGrpCd == null || cmnGrpCd.isEmpty()){
            cmnGrpCd = UUID.randomUUID().toString();
            mybatisParam.setCmnGrpCd(cmnGrpCd);
        }
        mybatisParam.setRgstrId(userId);
        mybatisParam.setChgId(userId);
        mybatisParam.setDltYn("N");
        mybatisParam.setCmnCdDsplyOrdr(maxDisplayOrder);

        mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.commoncode.insertSmComCodeGroup",mybatisParam);
        mybatisParam.reset();
        mybatisParam.setCmnGrpCd(cmnGrpCd);
        CommonCodeDto.SmComCodeGroup result = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.commoncode.selectSmComCodeGroup",mybatisParam);
        return toggleSmComCodeGroup(result);
    }

    @Transactional
    public CommonCodeDto.SmComCodeGroup updateCommonCodeGroup(CommonCodeDto.SmComCodeGroup smComCodeGroup) {
        return updateCommonCodeGroup(smComCodeGroup, null);
    }
    @Transactional
    public CommonCodeDto.SmComCodeGroup updateCommonCodeGroup(CommonCodeDto.SmComCodeGroup smComCodeGroup, String userId) {

        smComCodeGroup.setChgId(userId);

        mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.commoncode.updateSmComCodeGroup", smComCodeGroup);
        return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.commoncode.selectSmComCodeGroup", smComCodeGroup);
    }

    @Transactional
    public void deleteCommonCodeGroup(List<String> groupCodeNoList) {
        deleteCommonCodeGroup(groupCodeNoList, null);
    }
    @Transactional
    public void deleteCommonCodeGroup(List<String> groupCodeNoList, String userId) {
        //특수 경우라 남겨둠(sql id는 특성을 바로 알아볼 수 있도록
        List<CommonCodeDto.SmComCodeGroup> list = mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.commoncode.selectSmComCodeGroupListByCmnGrpCd", groupCodeNoList);
        list.forEach(smComCodeGroup -> {
            //지우려는 그룹의 코드(pk)
            String cmnGrpCd = smComCodeGroup.getCmnGrpCd();
            //지우려는 그룹의 레벨
            int cmnLevel = smComCodeGroup.getCmnLevel();

            //최상위 코드그룹이 아니라면
            if (cmnLevel > 0) {
                //코드그룹 MyBatis 파라미터 셋팅(검색해온 리스트에서 꺼낸 객체 재사용)
                smComCodeGroup.setDltYn("Y");
                smComCodeGroup.setDltId(userId);
                //코드그룹 논리 삭제
                mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.commoncode.updateSmComCodeGroup", smComCodeGroup);

                //MyBatis 파라미터 초기화
                smComCodeGroup.reset();

                //코드 MyBatis 파라미터 셋팅
                CommonCodeDto.SmComCode smComCode = new CommonCodeDto.SmComCode();
                smComCode.setCmnGrpCd(cmnGrpCd);
                smComCode.setDltYn("N");
                //지워지지 않은 하위 코드 리스트 검색
                List<CommonCodeDto.SmComCode> smCmnCodeList = mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.commoncode.selectSmComCode",smComCode);
                //하위 코드들 삭제
                smCmnCodeList.forEach(code -> {
                    code.setDltYn("Y");
                    code.setDltId(userId);
                    mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.commoncode.updateSmComCode",code);
                });

                //하위 코드그룹이 있을수도 있는 경우
                if (cmnLevel == 1) {
                    //코드그룹 MyBatis 파라미터 초기화 및 셋팅
                    smComCodeGroup.reset();
                    smComCodeGroup.setUpCmnGrpCd(cmnGrpCd);
                    smComCodeGroup.setDltYn("N");
                    //하위 코드그룹 리스트 검색
                    List<CommonCodeDto.SmComCodeGroup> subSmComCodeGroupList = mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.commoncode.selectSmComCodeGroup",smComCodeGroup);
                    subSmComCodeGroupList.forEach(subSmComCodeGroup -> {
                        //하위 코드그룹의 코드(pk)
                        String subCmnGrpCd = subSmComCodeGroup.getCmnGrpCd();
                        //코드그룹 MyBatis 파라미터 셋팅(검색해온 리스트에서 꺼낸 객체 재사용)
                        subSmComCodeGroup.setDltYn("Y");
                        subSmComCodeGroup.setDltId(userId);
                        //하위 코드그룹 삭제
                        mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.commoncode.updateSmComCodeGroup", subSmComCodeGroup);

                        //하위 코드 MyBatis 파라미터 셋팅
                        CommonCodeDto.SmComCode subSmComCode = new CommonCodeDto.SmComCode();
                        subSmComCode.setCmnGrpCd(subCmnGrpCd);
                        subSmComCode.setDltYn("N");
                        //하위 코드그룹의 코드 리스트 검색
                        List<CommonCodeDto.SmComCode> subSmCmnCodeList = mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.commoncode.selectSmComCode",subSmComCode);

                        //하위 코드그룹의 코드들 삭제
                        subSmCmnCodeList.forEach(code -> {
                            code.setDltYn("Y");
                            code.setDltId(userId);
                            mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.commoncode.updateSmComCode",code);
                        });
                    });
                }
            } else {
                // not delete 0 level
            }
        });
    }

    @Transactional
    public boolean upGroup(CommonCodeDto.SmComCodeGroup smComCodeGroup,String userId) {
        smComCodeGroup.setChgId(userId);

        int updatedRows = mybatisSession
                .update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.commoncode.upCmnCdDsplyOrdr", smComCodeGroup);
        if (updatedRows > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Transactional
    public boolean downGroup(CommonCodeDto.SmComCodeGroup smComCodeGroup, String userId) {
        smComCodeGroup.setChgId(userId);

        int updatedRows = mybatisSession
                .update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.commoncode.downCmnCdDsplyOrdr", smComCodeGroup);
        if (updatedRows > 0) {
            return true;
        } else {
            return false;
        }
    }

    // 코드-----------------------------------------------------------------------------------------------------

    @SuppressWarnings("null")
    public Page<CommonCodeDto.SmComCode> getCommonCodeListGrid(CommonCodeListInput commonCodeListInput) {
        List<CommonCodeDto.SmComCode> commonCodeListOutputs = mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.commoncode.selectCommonCodeListGrid",
                commonCodeListInput);

        if (commonCodeListOutputs == null || commonCodeListOutputs.isEmpty()) {
            commonCodeListOutputs = new ArrayList<>();
        }

        Long totalCount = 0L;
        if (commonCodeListOutputs != null && !commonCodeListOutputs.isEmpty()) {
            totalCount = commonCodeListOutputs.get(0).getTotal();
        }

        return new PageImpl<>(commonCodeListOutputs, commonCodeListInput.getPageable(), totalCount);
    }

    public List<Map<String, ?>> getCommonCodeList(CommonCodeListInput commonCodeListInput) {
        return mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.commoncode.selectCommonCodeList",
                commonCodeListInput);
    }


    /**
     * 해당하는 groupCode에 속한 dlt_yn이 'N'인 모든 smComCode 검색
     *
     * @param groupCode
     * @return
     */
    //사용 중
    //DesignResponsesComponent.getDsgnCode
    //ProjectInstallManageComponent.getDetailPageData
    //DocumentService.getAvailableFileExt
    public List<SmComCode> getCommonCodeListByGroupCode(String groupCode) {
        CommonCodeDto.SmComCode smComCode = new CommonCodeDto.SmComCode();
        smComCode.setCmnGrpCd(groupCode);
        smComCode.setDltYn("N");

        smComCode.setOrder("cmnCdDsplyOrder","asc");
        smComCode.setOrder("rgstDt","desc");
        smComCode.setOrder("cmn_grp_cd","asc");


        smComCode.setLimit(20,0);

        List<CommonCodeDto.SmComCode> result = mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.commoncode.selectSmComCode",smComCode);

        return result.stream().map(this::toggleSmComCode).collect(Collectors.toList());
    }

    public CommonCodeDto.SmComCode getCommonCodeLoadData(String cmnCdNo) {
//        log.debug("cmnCdNo={}", cmnCdNo);
        CommonCodeDto.SmComCode smComCode = new  CommonCodeDto.SmComCode();
        smComCode.setCmnCdNo(cmnCdNo);
        smComCode = mybatisSession.selectOne(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.commoncode.selectSmComCode",
                smComCode);
        return smComCode;
    }

    public SmComCode getCommonCode(String cmnCdNo, String cmnCd) {
        CommonCodeDto.SmComCode smComCode = new CommonCodeDto.SmComCode();
        smComCode.setCmnCdNo(cmnCdNo);
        smComCode.setCmnCd(cmnCd);
        smComCode = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.commoncode.selectSmComCode",smComCode);
        return toggleSmComCode(smComCode);
    }

    public SmComCode getCommonCodeByGrpCdAndCmnCd(String cmnGrpCd, String cmnCd) {
        HashMap<String, Object> mybatisParams = new HashMap<>();
        mybatisParams.put("cmnGrpCd", cmnGrpCd);
        mybatisParams.put("cmnCd", cmnCd);

        CommonCodeDto.SmComCode smComCode = new  CommonCodeDto.SmComCode();
        smComCode.setCmnGrpCd(cmnGrpCd);
        smComCode.setCmnCd(cmnCd);

//        CommonCodeDto.SmComCode result = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.commoncode.selectSmComCode",mybatisParams);
//        return toggleSmComCode(result);

        smComCode = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.commoncode.selectSmComCode",smComCode);
        return toggleSmComCode(smComCode);
    }

    /**
     * 기초데이터(commoncode의 attrbt_cd1)의 데이터로 속성 정의 생성
     * @param navigation
     */
    public List<DocumentForm.PropertyCreate> createPropertyListForCommonCode(String cmnGrpCd, String cmnCd, String naviId) {
        List<DocumentForm.PropertyCreate> propertyList = new ArrayList<>();

        SmComCode smComCode = this.getCommonCodeByGrpCdAndCmnCd(cmnGrpCd, cmnCd);

        // 폴더 종류 정보 조회

        // property_data 처리
        Object propertyData = smComCode.getAttrbtCd1();

        if (propertyData != null && StringUtils.isNotBlank(propertyData.toString())) {
            try {
                propertyList = this.parsePropertyListFromJson(propertyData.toString(), naviId);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        return propertyList;
    }
    public List<DocumentForm.PropertyCreate> createPropertyListForCommonCode(String propertyData, String naviId) {
        List<DocumentForm.PropertyCreate> propertyList = new ArrayList<>();

        // property_data 처리
        if (propertyData != null && StringUtils.isNotBlank(propertyData)) {
            try {
                propertyList = this.parsePropertyListFromJson(propertyData.toString(), naviId);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        return propertyList;
    }

    /**
     * JSON문자열로 저장된 속성 정의 정보 DcProperty 객체로 변환.
     * @param json
     * @param navigation
     * @return
     * @throws JsonProcessingException
     */
    private List<DocumentForm.PropertyCreate> parsePropertyListFromJson(String json, String naviId) throws JsonProcessingException {
        List<DocumentForm.PropertyCreate> propertyList = new ArrayList<>();
        short order = 1;

        List<Map<String, Object>> rawList = objectMapper.readValue(json, new TypeReference<>() {});
        for (Map<String, Object> item : rawList) {
            DocumentForm.PropertyCreate property = new DocumentForm.PropertyCreate();
            property.setNaviId(naviId);
            property.setAttrbtCd((String) item.get("attrbtCd"));
            property.setAttrbtCdType((String) item.get("attrbtCdType"));
            property.setAttrbtType((String) item.get("attrbtType"));
            property.setAttrbtTypeSel((String) item.getOrDefault("attrbtTypeSel", null));
            property.setAttrbtNmEng((String) item.getOrDefault("attrbtNmEng", null));
            property.setAttrbtNmKrn((String) item.get("attrbtNmKrn"));
            property.setAttrbtDsplyOrder(order++);
            property.setAttrbtDsplyYn((String) item.getOrDefault("attrbtDsplyYn", "Y"));
            property.setAttrbtChgYn((String) item.getOrDefault("attrbtChgYn", "Y"));

            propertyList.add(property);
        }

        return propertyList;
    }

    public SmComCode getCommonCodeByGroupCodeAndCmnCd(Integer groupCodeNo, String cmnCd) {
        SmComCode mybatisParams = new SmComCode();
        mybatisParams.setCmnGrpNo(groupCodeNo);
        mybatisParams.setCmnCd(cmnCd);

        CommonCodeDto.SmComCode result = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.commoncode.selectSmComCode",mybatisParams);
        return toggleSmComCode(result);
    }

    public boolean existCommonCode(String groupCodeCd, String code) {
        CommonCodeDto.SmComCode smComCode = new CommonCodeDto.SmComCode();
        smComCode.setCmnGrpCd(groupCodeCd);
        smComCode.setCmnCd(code);
        smComCode.setDltYn("N");

        CommonCodeDto.SmComCode result = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.commoncode.selectSmComCode",smComCode);
        return result != null;
//        return smComCodeRepository.existsByCmnGrpCdAndCmnCdAndDltYn(groupCodeCd, code, "N");
    }

    @Transactional
    public CommonCodeDto.SmComCode createCommonCode(CommonCodeDto.SmComCode smComCode) {
        return createCommonCode(smComCode, null);
    }
    @Transactional
    public CommonCodeDto.SmComCode createCommonCode(CommonCodeDto.SmComCode smComCode, String userId) {
        String cmnCdNo = smComCode.getCmnCdNo();

        if (cmnCdNo == null || cmnCdNo.isEmpty()) {
            cmnCdNo = UUID.randomUUID().toString();
            smComCode.setCmnCdNo(cmnCdNo);
        }
        smComCode.setRgstrId(userId);
        smComCode.setChgId(userId);

        if(mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.commoncode.insertSmComCode",smComCode) > 0){
            smComCode.setCmnCdNo(cmnCdNo);
            return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.commoncode.selectSmComCode",smComCode);
        }
        return null;
    }

    @Transactional
    public CommonCodeDto.SmComCode updateCommonCode(CommonCodeDto.SmComCode smComCode) {
        return updateCommonCode(smComCode, null);
    }

    @Transactional
    public CommonCodeDto.SmComCode updateCommonCode(CommonCodeDto.SmComCode smComCode, String userId) {
        String cmnCdNo = smComCode.getCmnCdNo();

        smComCode.setChgId(userId);
        int result = mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.commoncode.updateSmComCode", smComCode);

        if(result == 1) {
            smComCode.reset();
            smComCode.setCmnCdNo(cmnCdNo);
            return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.commoncode.selectSmComCode", smComCode);
        }
        else{
            return null;
        }
    }

    @Transactional
    public void updateCommonCodeOrder(List<SmComCode> codeList, String userId) {
        SmComCode mybatisParams = null;

        for (int i = 0; i < codeList.size(); i++) {
            SmComCode id = codeList.get(i);
            mybatisParams = new SmComCode();
            mybatisParams.setCmnCdNo(id.getCmnCdNo());
            mybatisParams.setCmnCd(id.getCmnCd());
//            SmComCode smComCode = smComCodeRepository.findByCmnCdNoAndCmnCd(id.getCmnCdNo(), id.getCmnCd());
            CommonCodeDto.SmComCode result = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.commoncode.selectSmComCode",mybatisParams);
            if (result != null) {
                result.setCmnCdDsplyOrder((short) i);
                result.setChgId(userId);
                mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.commoncode.updateSmComCode",result);
            }
        }
    }

    @Transactional
    public void deleteCommonCode(List<SmComCode> codeList) {
        deleteCommonCode(codeList, null);
    }
    @Transactional
    public void deleteCommonCode(List<SmComCode> codeList, String userId) {
        codeList.forEach(id -> {
            HashMap<String, Object> mybatisParams = new HashMap<>();
            mybatisParams.put("cmnGrpCd", id.getCmnGrpCd());
            mybatisParams.put("cmnCdNo", id.getCmnCdNo());
            CommonCodeDto.SmComCode smComCode = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.commoncode.selectSmComCode",mybatisParams);
//            SmComCode smComCode = smComCodeRepository.findByCmnGrpCdAndCmnCdNo(id.getCmnGrpCd(), id.getCmnCdNo());
            if (smComCode != null) {
//                smComCodeRepository.updateDelete(smComCode, userId);
                smComCode.setChgId(userId);
                smComCode.setDltYn("Y");
                smComCode.setDltId(userId);
                mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.commoncode.updateSmComCode",smComCode);
            }
        });
    }

    /**
     * 코드 콤보 여러 개 조회
     * 
     * @return
     */
    public Map<String, List<Map<String, Object>>> getCommonCodeListByGroupCode(List<String> cmnGrpCdList,
            String langInfo) {
        Map<String, List<Map<String, Object>>> comCodeMap = new HashMap<>();
        cmnGrpCdList.forEach(groupCode -> {
            MybatisInput input = new MybatisInput();
            input.put("cmnGrpCd",groupCode);
            input.put("lang",langInfo);

            String key = "";
            if (groupCode.equals(CommonCodeConstants.PSTN_CODE_GROUP_CODE)) {
                key = "pstn";
            } else if (groupCode.equals(CommonCodeConstants.RANK_CODE_GROUP_CODE)) {
                key = "ratng";
            } else if (groupCode.equals(CommonCodeConstants.FLAG_CODE_GROUP_CODE)) {
                key = "flag";
            } else if (groupCode.equals(CommonCodeConstants.ATTBTKIND_CODE_GROUP_CODE)) {
                key = "attrbtCdType";
            } else if (groupCode.equals(CommonCodeConstants.ATTBTTYPE_CODE_GROUP_CODE)) {
                key = "attrbtType";
            }
            comCodeMap.put(key, mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.commoncode.selectCommonCodeInfoList",
                    input));
        });
        return comCodeMap;
    }

    /**
     * 문서관리 구분 데이터 조회
     * @param naviDiv
     * @return
     */
	public SmComCode getDocumentTypeInfo(String naviDiv) {
        CommonCodeDto.SmComCode smComCode = new CommonCodeDto.SmComCode();
        smComCode.setCmnGrpCd(CommonCodeConstants.DOC_NAVI_DIV_GROUP_CODE);
        smComCode.setCmnCd(naviDiv);
        smComCode.setDltYn("N");
        CommonCodeDto.SmComCode result = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.commoncode.selectSmComCode",smComCode);
		return toggleSmComCode(result);
	}

    @Async
    @Transactional
    public void asyncSaveOrgList(String parentOuCode, List<Map<String, Object>> list) {
        boolean isContinue = true;
        int i = 0;

        if (list != null && !list.isEmpty()) {
            while (isContinue) {
                //                {
                //                    "treeType": null,
                //                    "id": "1040152",
                //                    "text": "감사교육원",
                //                    "children": true,
                //                    "ucchieftitle": "감사원(감사교육원장)",
                //                    "icon": "/static/images/dir_sub.gif",
                //                    "state": {
                //                        "opened": false,
                //                        "selected": false,
                //                        "disabled": true
                //                    },
                //                    "target": null,
                //                    "fileUrl": null,
                //                    "fileName": null,
                //                    "asteriskIdx": null,
                //                    "statuteIdx": null,
                //                    "subLev": null,
                //                    "docTid": null,
                //                    "docTopIdx": null,
                //                    "docIdx": null,
                //                    "docName": null,
                //                    "docContent": null,
                //                    "docOrder": null,
                //                    "docType": null,
                //                    "delFg": null,
                //                    "fleId": null
                //                }

                Map<String, Object> org = list.get(i++);

                Map<String, Boolean> state = (Map<String, Boolean>)org.get("state");

                CodeOutput code = null;

                if (!"0000000".equals(parentOuCode)) {
                    Map<String, Object> params = new HashMap<>();
                    params.put("cmnCd", parentOuCode);
                    params.put("cmnGrpNo", 179);

                    code = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.commoncode.selectCommonCode", params);
                }

                SmComCode findCode = getCommonCodeByGroupCodeAndCmnCd(179, MapUtils.getString(org, "id"));

                CommonCodeDto.SmComCode smComCode = new CommonCodeDto.SmComCode();
                smComCode.setCmnCdNo(findCode == null ? UUID.randomUUID().toString() : findCode.getCmnCdNo());
                smComCode.setCmnGrpNo(179); //공통코드그룹_No
                smComCode.setCmnGrpCd("2d7fedd3-264b-e70d-becb-7b0f9f44fa85"); //공통코드그룹_code
                smComCode.setCmnCd(MapUtils.getString(org, "id")); //공통코드_code
                smComCode.setCmnCdNmEng(MapUtils.getString(org, "text")); //코드_이름_영어
                smComCode.setCmnCdNmKrn(MapUtils.getString(org, "text")); //코드_이름_한글
                smComCode.setCmnCdDsplyOrder((short) 1); //순번
                smComCode.setCmnCdDscrpt(MapUtils.getString(org, "ucchieftitle")); //공통코드설명

                if (findCode != null) {
                    smComCode.setAttrbtCd1(findCode.getAttrbtCd1()); //속성코드1. 부모코드
                } else {
                    smComCode.setAttrbtCd1(parentOuCode == null ? MapUtils.getString(org, "parentoucode") : parentOuCode); //속성코드1. 부모코드
                }

                smComCode.setAttrbtCd2(MapUtils.getString(org, "children")); //속성코드2. 자식노드 존재 여부

                if (code != null) {
                    smComCode.setAttrbtCd3(String.format("%s > %s", code.getAttrbtCd3(), MapUtils.getString(org, "text"))); //속성코드3
                } else {
                    smComCode.setAttrbtCd3(MapUtils.getString(org, "text")); //속성코드3
                }
//                    smComCode.setAttrbtCd4(MapUtils.getString(org, "cmnCd")); //속성코드4
//                    smComCode.setAttrbtCd5(MapUtils.getString(org, "cmnCd")); //속성코드5
                smComCode.setUseYn(MapUtils.getBoolean(state, "disabled") ? "N" : "Y"); //
                smComCode.setDltYn("N"); //
                smComCode.setRgstrId("SYSTEM");
                smComCode.setRgstDt(findCode != null ? findCode.getRgstDt() : null);
                smComCode.setChgId("SYSTEM");


//                if (MapUtils.getBoolean(org, "children")) {
//                    list.addAll(doc24Client.getOrganization(MapUtils.getString(org, "id")));
//                }

                if (i == list.size()) {
                    isContinue = false;
//                        throw new BizException("");
                }

                if (findCode == null) {
                    mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.commoncode.insertSmComCode", smComCode);
                } else {
                    mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.commoncode.updateSmComCode", smComCode);
                }
            }
        }
    }

    // 문서관리 > 속성 정의 추가 - 정보문서관리 하위 노드 리스트 조회
    public List<Map<String, Object>> getAttrbtTypeSelOptions(String langInfo) {
        String docCode = CommonCodeConstants.DOCUMENT_GROUP_CODE;
        MybatisInput input = MybatisInput.of().add("lang", langInfo)
                        .add("docCode", docCode);

        return mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.commoncode.selectAttrbtTypeSelList", input);
    }

    private SmComCodeGroup toggleSmComCodeGroup (CommonCodeDto.SmComCodeGroup smComCodeGroup) {
        if(smComCodeGroup == null) {
            return null;
        }
        SmComCodeGroup entity = new SmComCodeGroup();
        BeanUtils.copyProperties(smComCodeGroup, entity);
        return entity;
    }
    private CommonCodeDto.SmComCodeGroup toggleSmComCodeGroup (SmComCodeGroup entity) {
        if(entity == null) {
            return null;
        }
        CommonCodeDto.SmComCodeGroup param = new CommonCodeDto.SmComCodeGroup();
        BeanUtils.copyProperties(entity,param);
        return param;
    }
    private SmComCode toggleSmComCode (CommonCodeDto.SmComCode smComCode) {
        if(smComCode == null) {
            return null;
        }
        SmComCode entity = new SmComCode();
        BeanUtils.copyProperties(smComCode, entity);
        return entity;
    }
    private CommonCodeDto.SmComCode toggleSmComCode (SmComCode entity) {
        if(entity == null) {
            return null;
        }
        CommonCodeDto.SmComCode param = new CommonCodeDto.SmComCode();
        BeanUtils.copyProperties(entity,param);
        return param;
    }
}
