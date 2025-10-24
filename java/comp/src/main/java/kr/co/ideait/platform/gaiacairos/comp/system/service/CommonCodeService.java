package kr.co.ideait.platform.gaiacairos.comp.system.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Maps;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import kr.co.ideait.platform.gaiacairos.core.constant.CommonCodeConstants;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmComCode;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmComCodeGroup;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.SmComCodeGroupRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.SmComCodeRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.document.DocumentForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.commoncode.CommonCodeMybatisParam.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
public class CommonCodeService extends AbstractGaiaCairosService {

    @Autowired
    SmComCodeGroupRepository smComCodeGroupRepository;

    @Autowired
    SmComCodeRepository smComCodeRepository;

    // 그룹코드
    // -----------------------------------------------------------------------------------------------------

    public List<CommonCodeGroupListOutput> getCommonCodeGroupList() {
        log.debug("commonCodeGroupNo={}");
        List<CommonCodeGroupListOutput> codeGroup = mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.commoncode.getCommonCodeGroupList");

        return codeGroup;
    }

    public SmComCodeGroup getCommonCodeGroup(int commonCodeGroupNo) {
        return smComCodeGroupRepository.findById(commonCodeGroupNo).orElse(null);
    }

    public boolean existCommonCodeGroup(Integer upCmnGrpNo, String cmnCd) {
        return smComCodeGroupRepository.existsByCmnCdAndUpCmnGrpNoAndDltYn(cmnCd, upCmnGrpNo, "N");
    }

    @Transactional
    public SmComCodeGroup createCommonCodeGroup(SmComCodeGroup smComCodeGroup) {
        return createCommonCodeGroup(smComCodeGroup, null);
    }
    @Transactional
    public SmComCodeGroup createCommonCodeGroup(SmComCodeGroup smComCodeGroup, String userId) {
        Short maxDisplayOrder = smComCodeGroupRepository.maxCdDsplyOrdrByUpCmnGrpNo(smComCodeGroup.getUpCmnGrpNo());
        if (maxDisplayOrder == null) {
            maxDisplayOrder = (short) 1;
        } else {
            maxDisplayOrder++;
        }

        if (userId == null) {
            smComCodeGroup.setCmnGrpCd(UUID.randomUUID().toString());
            smComCodeGroup.setRgstrId(UserAuth.get(true).getUsrId());
            smComCodeGroup.setChgId(UserAuth.get(true).getUsrId());
        } else {
            smComCodeGroup.setRgstrId(userId);
            smComCodeGroup.setChgId(userId);
        }

        smComCodeGroup.setDltYn("N");
        smComCodeGroup.setCmnCdDsplyOrdr(maxDisplayOrder);

        return smComCodeGroupRepository.save(smComCodeGroup);
    }

    @Transactional
    public SmComCodeGroup updateCommonCodeGroup(SmComCodeGroup smComCodeGroup) {
        return updateCommonCodeGroup(smComCodeGroup, null);
    }
    @Transactional
    public SmComCodeGroup updateCommonCodeGroup(SmComCodeGroup smComCodeGroup, String userId) {
        if (userId == null) {
            smComCodeGroup.setRgstrId(UserAuth.get(true).getUsrId());
            smComCodeGroup.setChgId(UserAuth.get(true).getUsrId());
        } else {
            smComCodeGroup.setRgstrId(userId);
            smComCodeGroup.setChgId(userId);
        }

//        SmComCodeGroup returnData = smComCodeGroupRepository.save(smComCodeGroup);
        SmComCodeGroup returnData = smComCodeGroupRepository.findByCmnGrpCd(smComCodeGroup.getCmnGrpCd());
        mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.commoncode.updateSmComCodeGroup", smComCodeGroup);

        return returnData;
    }

    @Transactional
    public void deleteCommonCodeGroup(List<String> groupCodeNoList) {
        deleteCommonCodeGroup(groupCodeNoList, null);
    }
    @Transactional
    public void deleteCommonCodeGroup(List<String> groupCodeNoList, String userId) {
//        smComCodeGroupRepository.findAllById(groupCodeNoList).forEach(smComCodeGroup -> {
        List<SmComCodeGroup> list = mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.commoncode.selectListByCmnGrpCd", groupCodeNoList);
        list.forEach(smComCodeGroup -> {
            int cmnLevel = smComCodeGroup.getCmnLevel();

            if (cmnLevel > 0) {
//                smComCodeGroupRepository.updateDelete(smComCodeGroup);
                if (userId == null) {
                    smComCodeGroup.setDltId(UserAuth.get(true).getUsrId());
                } else {
                    smComCodeGroup.setDltId(userId);
                }
                mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.commoncode.updateDeleteMarkUpBySmComCodeGroup", smComCodeGroup);

                if (smComCodeGroup.getCmnLevel() == 1) {
                    smComCodeGroupRepository.findByUpCmnGrpCd(smComCodeGroup.getCmnGrpCd())
                            .forEach(subSmComCodeGroup -> {
//                                smComCodeGroupRepository.updateDelete(subSmComCodeGroup);
                                if (userId == null) {
                                    subSmComCodeGroup.setDltId(UserAuth.get(true).getUsrId());
                                } else {
                                    subSmComCodeGroup.setDltId(userId);
                                }
                                mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.commoncode.updateDeleteMarkUpBySmComCodeGroup", subSmComCodeGroup);
                            });
                }
            } else {
                // not delete 0 level
            }
        });
    }

    @Transactional
    public boolean upGroup(SmComCodeGroup smComCodeGroup) {
        int updatedRows = mybatisSession
                .update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.commoncode.upCmnCdDsplyOrdr", smComCodeGroup);
        if (updatedRows > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Transactional
    public boolean downGroup(SmComCodeGroup smComCodeGroup) {
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
    public Page<CommonCodeListOutput> getCommonCodeListGrid(CommonCodeListInput commonCodeListInput) {
        List<CommonCodeListOutput> commonCodeListOutputs = mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.commoncode.selectCommonCodeListGrid",
                commonCodeListInput);

        if (commonCodeListOutputs == null || commonCodeListOutputs.isEmpty()) {
            commonCodeListOutputs = new ArrayList<>();
        }

        Long totalCount = 0L;
        if (commonCodeListOutputs != null && !commonCodeListOutputs.isEmpty()) {
            totalCount = commonCodeListOutputs.get(0).getTotalNum();
        }

        return new PageImpl<>(commonCodeListOutputs, commonCodeListInput.getPageable(), totalCount);
    }

    public List<Map<String, ?>> getCommonCodeList(CommonCodeListInput commonCodeListInput) {
        return mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.commoncode.selectCommonCodeList",
                commonCodeListInput);
    }

    //사용 중
    //DesignResponsesComponent.getDsgnCode
    //ProjectInstallManageComponent.getDetailPageData
    //DocumentService.getAvailableFileExt
    public List<SmComCode> getCommonCodeListByGroupCode(String groupCode) {
        return smComCodeRepository.findByCmnGrpCdAndDltYnOrderByCmnCdDsplyOrder(groupCode, "N");
    }

    public CodeOutput getCommonCodeLoadData(String cmnCdNo) {
        log.debug("cmnCdNo={}", cmnCdNo);
        CodeOutput Code = mybatisSession.selectOne(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.commoncode.getCommonCode",
                cmnCdNo);

        return Code;
    }

    public SmComCode getCommonCode(String cmnCdNo, String cmnCd) {
        return smComCodeRepository.findByCmnCdNoAndCmnCd(cmnCdNo, cmnCd);
    }

    public SmComCode getCommonCodeByGrpCdAndCmnCd(String cmnGrpCd, String cmnCd) {
        Map<String, String> params = Maps.newHashMap();
        params.put("cmnGrpCd", cmnGrpCd);
        params.put("cmnCd", cmnCd);

        return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.commoncode.selectCommonCode", params);
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
        return smComCodeRepository.findByCmnGrpNoAndCmnCd(groupCodeNo, cmnCd);
    }

    public boolean existCommonCode(String groupCodeCd, String code) {
        return smComCodeRepository.existsByCmnGrpCdAndCmnCdAndDltYn(groupCodeCd, code, "N");
    }

    @Transactional
    public SmComCode createCommonCode(SmComCode smComCode) {
        return createCommonCode(smComCode, null);
    }
    @Transactional
    public SmComCode createCommonCode(SmComCode smComCode, String userId) {
        smComCode.setDltYn("N");

        if (userId == null) {
            smComCode.setCmnCdNo(UUID.randomUUID().toString());
            smComCode.setRgstrId(UserAuth.get(true).getUsrId());
            smComCode.setChgId(UserAuth.get(true).getUsrId());
        } else {
            smComCode.setRgstrId(userId);
            smComCode.setChgId(userId);
        }

        return smComCodeRepository.save(smComCode);
    }

    @Transactional
    public SmComCode updateCommonCode(SmComCode smComCode) {
        return updateCommonCode(smComCode, null);
    }
    @Transactional
    public SmComCode updateCommonCode(SmComCode smComCode, String userId) {
        if (userId == null) {
            smComCode.setChgId(UserAuth.get(true).getUsrId());
        } else {
            smComCode.setChgId(userId);
        }

//        return smComCodeRepository.save(smComCode);
        mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.commoncode.updateCmnCode", smComCode);
        return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.commoncode.selectCommonCode", smComCode);
    }

    @Transactional
    public void updateCommonCodeOrder(List<SmComCode> codeList) {
        for (int i = 0; i < codeList.size(); i++) {
            SmComCode id = codeList.get(i);
            SmComCode smComCode = smComCodeRepository.findByCmnCdNoAndCmnCd(id.getCmnCdNo(), id.getCmnCd());
            if (smComCode != null) {
                smComCode.setCmnCdDsplyOrder((short) i);
                smComCodeRepository.save(smComCode);
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
            SmComCode smComCode = smComCodeRepository.findByCmnGrpCdAndCmnCdNo(id.getCmnGrpCd(), id.getCmnCdNo());
            if (smComCode != null) {
                if (userId == null) {
                    smComCodeRepository.updateDelete(smComCode);
                } else {
                    smComCodeRepository.updateDelete(smComCode, userId);
                }
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
            CmnGrpCdInput input = new CmnGrpCdInput();
            input.setCmnGrpCd(groupCode);
            input.setLang(langInfo);

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
		return smComCodeRepository.findByCmnGrpCdAndCmnCdAndDltYn(CommonCodeConstants.DOC_NAVI_DIV_GROUP_CODE, naviDiv, "N");
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

                SmComCode smComCode = new SmComCode();
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
                    mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.commoncode.insertCmnCode", smComCode);
                } else {
                    mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.commoncode.updateCmnCode", smComCode);
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
}
