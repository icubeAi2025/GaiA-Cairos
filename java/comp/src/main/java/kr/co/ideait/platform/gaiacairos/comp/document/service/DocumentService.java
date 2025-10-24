package kr.co.ideait.platform.gaiacairos.comp.document.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Maps;
import kr.co.ideait.platform.gaiacairos.comp.system.service.CommonCodeService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import kr.co.ideait.platform.gaiacairos.core.constant.CommonCodeConstants;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.*;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.*;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.document.DocumentForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.document.CbgnPropertyDto;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.document.ConstructionBeginsDocDto;
import kr.co.ideait.platform.gaiacairos.core.type.FileUploadType;
import kr.co.ideait.platform.gaiacairos.core.util.FileService;
import kr.co.ideait.platform.gaiacairos.core.util.FileService.FileMeta;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
public class DocumentService extends AbstractGaiaCairosService {

    @Autowired
    DcNavigationRepository navigationRepository;

    @Autowired
    DcAuthorityRepository authorityRepository;

    @Autowired
    DcPropertyRepository propertyRepository;

    @Autowired
    DcStorageMainRepository storageMainRepository;

    @Autowired
    DcPropertyDataRepository propertyDataRepository;

    @Autowired
    DcAttachmentsRepository attachmentsRepository;

    @Autowired
    FileService fileService;

    @Autowired
    CommonCodeService commonCodeService;

    //(임시)
    static Map<String, Integer> documentSystemKeyMap = new HashMap<>();

    public static final Integer SYSTEM_NO_PGAIA = 1;
    public static final Integer SYSTEM_NO_GAIA = 2;
    public static final Integer SYSTEM_NO_CAIROS = 3;

    static {
        documentSystemKeyMap.put("pgaia", SYSTEM_NO_PGAIA);
        documentSystemKeyMap.put("gaia", SYSTEM_NO_GAIA);
        documentSystemKeyMap.put("cairos", SYSTEM_NO_CAIROS);
    }

    // zipPath를 저장하는 ConcurrentHashMap (key: rootNaviId, value: zipPath)
    private static final ConcurrentHashMap<String, Path> ZIP_FILE_MAP = new ConcurrentHashMap<>();

    public List<String> getAvailableFileExt() {
        List<SmComCode> comCodeList = commonCodeService
                .getCommonCodeListByGroupCode(CommonCodeConstants.DOCFI_CODE_GROUP_CODE);
        List<String> availableExt = new ArrayList<>();
        for (SmComCode code : comCodeList) {
            availableExt.add(code.getCmnCd());
        }

        return availableExt;
    }


    /**
     * 네비게이션 리스트 조회
     *
     * @param isAdmin
     * @param naviId
     * @param loginId
     * @return List
     * @throws
     */
    public List<Map<String, ?>> getDocumentNavigationList(boolean isAdmin, String naviId, String loginId) {
        MybatisInput input = MybatisInput.of().add("naviId", naviId)
                .add("loginId", loginId);
        if (isAdmin) {
            return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.selectAdminDocumentNavigationList",
                    naviId);
        } else {
            return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.selectUserDocumentNavigationList",
                    input);
        }
    }

    /**
     * 네비게이션 리스트 권한 가져오기
     *
     * @param String (cmnGrpCd)
     * @param String (pjtNo)
     * @param String (cntrctNo)
     * @param String (pjtType)
     * @param String (menuCd)
     * @param String (loginId)
     * @return List
     * @throws
     */
    public List<Map<String, ?>> getDocumentNavigationListAuthority(String cmnGrpCd, String pjtNo, String cntrctNo,
                                                                   String pjtType, String menuCd, String loginId) {

        MybatisInput input = MybatisInput.of().add("cmnGrpCd", cmnGrpCd)
                .add("pjtNo", pjtNo)
                .add("cntrctNo", cntrctNo)
                .add("pjtType", pjtType)
                .add("menuCd", menuCd)
                .add("loginId", loginId);

        /* 기존코드(25.03.11) */
        // MybatisInput input = MybatisInput.of().add("cmnGrpCd", cmnGrpCd)
        // .add("pjtNo", pjtNo)
        // .add("cntrctNo", cntrctNo)
        // .add("pjtType", pjtType)
        // .add("menuCd", menuCd)
        // .add("loginId", loginId);

        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.selectUserDocumentNavigationListAuthority",
                input);
    }

    /**
     * 폴더명 중복체크
     *
     * @param String (naviId) 네비게이션 아이디
     * @param String (upDocId) 상위폴더 아이디
     * @param String (folderNm) 생성할 폴더명
     * @return String (Y or N)
     * @throws
     */
    public String checkFolderExist(String naviId, String upDocId, String folderNm) {
        // 폴더명 중복체크
        MybatisInput input = MybatisInput.of().add("naviId", naviId)
                .add("upDocId", upDocId)
                .add("folderNm", folderNm);

        Map<String, Object> result = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.checkFolderExist",
                input);

        return (String) result.get("name_exist");
    }

    /**
     * 네비게이션 경로명 중복체크
     *
     * @param String (naviDiv) 네비게이션 타입
     * @param String (upNaviId) 상위네비 아이디
     * @param String (naviNm) 생성할 네비명
     * @return String (Y or N)
     * @throws
     */
    public String addNaviExist(String naviDiv, String upNaviId, String naviNm) {
        // 폴더명 중복체크
        MybatisInput input = MybatisInput.of().add("naviDiv", naviDiv)
                .add("upNaviId", upNaviId)
                .add("naviNm", naviNm);

        Map<String, Object> result = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.addNaviExist", input);
        String nameExist = (String) result.get("name_exist");

        return nameExist;
    }

    /**
     * 폴더종류 중복체크
     *
     * @param String (naviId) 네비게이션 아이디
     * @param String (upDocId) 상위폴더 아이디
     * @param String (folderNm) 생성할 폴더종류
     * @return String (Y or N)
     * @throws
     */
    public String checkHasNavigationType(DocumentForm.CheckHasFolderType inputParam) {
        if ("0".equals(inputParam.getNaviFolderType())) {
            return "N";
        }

        return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.checkHasNavigationType", inputParam);
    }

    /**
     * 네비게이션 경로 생성 (JPA 사용)
     *
     * @param DcNavigation (navigation) 네비게이션 테이블 엔티티
     * @return DcNavigation (navigation) 네비게이션 테이블 엔티티
     * @throws
     */
    @Transactional
    public DcNavigation createNavigation(DcNavigation navigation) {
        //1. 네비게이션 순서(dsplyOrdr) 설정
        Short maxDisplayOrder = navigationRepository.maxMenuDsplyOrdrByUpNaviId(navigation.getUpNaviId());
        if (maxDisplayOrder == null) {
            maxDisplayOrder = (short) 1;
        } else {
            maxDisplayOrder++;
        }
        navigation.setDsplyOrdr(maxDisplayOrder);

        //2. 참조 시스템 키 설정
        Integer naviSysKey = documentSystemKeyMap.get(platform);
        navigation.setRefSysKey(naviSysKey);

        //TODO. 문서관리 시스템 분리 후 관리기능으로 전환 필요.
        if (!navigation.getNaviFolderType().isBlank() && !"0".equals(navigation.getNaviFolderType())) {
            SmComCode smComCode = commonCodeService.getCommonCodeByGrpCdAndCmnCd(CommonCodeConstants.DOCUMENT_NAVI_FOLDER_TYPE_GROUP_CODE, navigation.getNaviFolderType());

            navigation.setNaviId(smComCode.getAttrbtCd2().replaceAll("\\{cntrctNo\\}", navigation.getCntrctNo()).replaceAll("\\{folderKind\\}", smComCode.getAttrbtCd3()).replaceAll("\\{naviDiv\\}", navigation.getNaviDiv()));
        }

        //3. navi_path 설정
        String upNaviId = navigation.getUpNaviId();
        if (upNaviId == null || "#".equals(upNaviId)) {
            //"자동 생성되는 네비중에 최상위 네비 생성때"
            //암것도 안 함
        }
        else{
            //자동 생성 or 직접 생성
            //상위 네비가 있음
            DcNavigation upNavigation = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.selectOneNavigation",upNaviId);
            //1뎁스일때는 navi_path가 "프로젝트명"
            if(StringUtils.isEmpty(upNavigation.getUpNaviId())){
                navigation.setNaviPath(upNavigation.getNaviNm());
            }
            //그 외에는 navi_path가 ">" 포함된 경로
            else{
                navigation.setNaviPath(upNavigation.getNaviPath()+" > "+upNavigation.getNaviNm());
            }
        }

        mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.insertNavigation", navigation);
        DcNavigation dcNavigation = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.selectOneNavigation", navigation.getNaviId());

        // 네비게이션 생성 시, 수신자 / 승인자 / 결재링크 속성 정의 항목 자동 추가
        // 속성 정의 입력 값 생성. (수신자 / 승인자 / 결재링크)

        String naviId = dcNavigation.getNaviId();
        Integer naviNo = dcNavigation.getNaviNo();

        List<DcProperty> approvalPropertyList = makeApprovalPropertyList(naviId, naviNo);
        createPropertyList(approvalPropertyList);

        // 네비게이션 경로 생성 후 생성 경로의 상위 경로와 동일한 권한을 부여
        MybatisInput input = MybatisInput.of().add("id", dcNavigation.getNaviId())
                .add("no", dcNavigation.getNaviNo())
                .add("upId", dcNavigation.getUpNaviId())
                .add("upNo", dcNavigation.getUpNaviNo())
                .add("usrId", dcNavigation.getRgstrId());
        /**
         * log.debug("id : >>>>>> " + dcNavigation.getNaviId());
         * log.debug("no : >>>>>> " + dcNavigation.getNaviNo());
         * log.debug("upId : >>>>>> " + dcNavigation.getUpNaviId());
         * log.debug("upNo : >>>>>> " + dcNavigation.getUpNaviNo());
         * log.debug("usrId : >>>>>> " + dcNavigation.getRgstrId());
         */
        mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.insertAutoMyAuthority", input);

        return dcNavigation;
    }

    /**
     * 전자 결재 관련 property 객체 생성.
     * @param naviId
     * @param naviNo
     */
    private static List<DcProperty> makeApprovalPropertyList(String naviId, Integer naviNo) {
        List<DcProperty> propertyList = new ArrayList<>();

        //수신처
        DcProperty toProperty = new DcProperty();
        toProperty.setNaviId(naviId);
        toProperty.setNaviNo(naviNo);
        toProperty.setAttrbtCd("TO");
        toProperty.setAttrbtCdType("UDC");
        toProperty.setAttrbtType("TXT");
        toProperty.setAttrbtNmEng("TO");
        toProperty.setAttrbtNmKrn("수신처");
        toProperty.setAttrbtDsplyOrder((short)1);
        toProperty.setAttrbtDsplyYn("N");
        toProperty.setAttrbtChgYn("N");

        propertyList.add(toProperty);

        //발신처
        DcProperty fromProperty = new DcProperty();
        fromProperty.setNaviId(naviId);
        fromProperty.setNaviNo(naviNo);
        fromProperty.setAttrbtCd("FROM");
        fromProperty.setAttrbtCdType("UDC");
        fromProperty.setAttrbtType("TXT");
        fromProperty.setAttrbtNmEng("FROM");
        fromProperty.setAttrbtNmKrn("발신처");
        fromProperty.setAttrbtDsplyOrder((short)2);
        fromProperty.setAttrbtDsplyYn("N");
        fromProperty.setAttrbtChgYn("N");

        propertyList.add(fromProperty);

        //발신날짜
        DcProperty fromDtProperty = new DcProperty();
        fromDtProperty.setNaviId(naviId);
        fromDtProperty.setNaviNo(naviNo);
        fromDtProperty.setAttrbtCd("from_dt");
        fromDtProperty.setAttrbtCdType("UDC");
        fromDtProperty.setAttrbtType("TXT");
        fromDtProperty.setAttrbtNmEng("FROM_Date");
        fromDtProperty.setAttrbtNmKrn("발신날짜");
        fromDtProperty.setAttrbtDsplyOrder((short)3);
        fromDtProperty.setAttrbtDsplyYn("N");
        fromDtProperty.setAttrbtChgYn("N");

        propertyList.add(fromDtProperty);

        //결재링크
        DcProperty apLinkProperty = new DcProperty();
        apLinkProperty.setNaviId(naviId);
        apLinkProperty.setNaviNo(naviNo);
        apLinkProperty.setAttrbtCd("ap_link");
        apLinkProperty.setAttrbtCdType("UDC");
        apLinkProperty.setAttrbtType("TXT");
        apLinkProperty.setAttrbtNmEng("Ap_link");
        apLinkProperty.setAttrbtNmKrn("결재링크");
        apLinkProperty.setAttrbtDsplyOrder((short)4);
        apLinkProperty.setAttrbtDsplyYn("N");
        apLinkProperty.setAttrbtChgYn("N");

        propertyList.add(apLinkProperty);

        //기안자
        DcProperty draftNmProperty = new DcProperty();
        draftNmProperty.setNaviId(naviId);
        draftNmProperty.setNaviNo(naviNo);
        draftNmProperty.setAttrbtCd("draft_id");
        draftNmProperty.setAttrbtCdType("UDC");
        draftNmProperty.setAttrbtType("TXT");
        draftNmProperty.setAttrbtNmEng("TO");
        draftNmProperty.setAttrbtNmKrn("기안자");
        draftNmProperty.setAttrbtDsplyOrder((short)5);
        draftNmProperty.setAttrbtDsplyYn("N");
        draftNmProperty.setAttrbtChgYn("N");

        propertyList.add(draftNmProperty);

        //기안일
        DcProperty draftDtProperty = new DcProperty();
        draftDtProperty.setNaviId(naviId);
        draftDtProperty.setNaviNo(naviNo);
        draftDtProperty.setAttrbtCd("draft_dt");
        draftDtProperty.setAttrbtCdType("UDC");
        draftDtProperty.setAttrbtType("TXT");
        draftDtProperty.setAttrbtNmEng("TO_DATE");
        draftDtProperty.setAttrbtNmKrn("기안일");
        draftDtProperty.setAttrbtDsplyOrder((short)6);
        draftDtProperty.setAttrbtDsplyYn("N");
        draftDtProperty.setAttrbtChgYn("N");

        propertyList.add(draftDtProperty);

        //승인자
        DcProperty apprerProperty = new DcProperty();
        apprerProperty.setNaviId(naviId);
        apprerProperty.setNaviNo(naviNo);
        apprerProperty.setAttrbtCd("apprer_id");
        apprerProperty.setAttrbtCdType("UDC");
        apprerProperty.setAttrbtType("TXT");
        apprerProperty.setAttrbtNmEng("Approver");
        apprerProperty.setAttrbtNmKrn("승인자");
        apprerProperty.setAttrbtDsplyOrder((short)7);
        apprerProperty.setAttrbtDsplyYn("N");
        apprerProperty.setAttrbtChgYn("N");

        propertyList.add(apprerProperty);

        //승인일
        DcProperty apprerDtProperty = new DcProperty();
        apprerDtProperty.setNaviId(naviId);
        apprerDtProperty.setNaviNo(naviNo);
        apprerDtProperty.setAttrbtCd("apprer_dt");
        apprerDtProperty.setAttrbtCdType("UDC");
        apprerDtProperty.setAttrbtType("TXT");
        apprerDtProperty.setAttrbtNmEng("Approver_Date");
        apprerDtProperty.setAttrbtNmKrn("승인일");
        apprerDtProperty.setAttrbtDsplyOrder((short)8);
        apprerDtProperty.setAttrbtDsplyYn("N");
        apprerDtProperty.setAttrbtChgYn("N");

        propertyList.add(apprerDtProperty);

        return propertyList;
    }

    /**
     * 네비게이션 단일 데이터 조회
     *
     * @param Integer (navigationNo) 네비게이션 번호
     * @return DcNavigation (navigation) 네비게이션 테이블 엔티티
     * @throws
     */
    public DcNavigation getNavigation(Integer navigationNo) {
        return navigationRepository.findByNaviNoAndDltYn(navigationNo, "N");
    }

    /**
     * 네비게이션 단일 데이터 조회
     *
     * @param String (navigationId) 네비게이션 Id
     * @return DcNavigation (navigation) 네비게이션 테이블 엔티티
     * @throws
     */
    public DcNavigation getNavigation(String navigationId) {
        return navigationRepository.findByNaviIdAndDltYn(navigationId, "N");
    }

    public Map<String, Object> getNavigationByRoot(String cntrctNo, String naviDiv) {
        Map<String, Object> sqlParams = Maps.newHashMap();
        sqlParams.put("cntrctNo", cntrctNo);
        sqlParams.put("naviDiv", naviDiv);

        Map<String,Object> navigation = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.selectNavigationRoot", sqlParams);

        return navigation;
    }

    /**
     * 네비게이션 단일 데이터 수정
     *
     * @param Integer (navigationNo) 네비게이션 번호
     * @return DcNavigation (navigation) 네비게이션 테이블 엔티티
     * @throws
     */
    public DcNavigation updateNavigation(DcNavigation navigation) {
        // 네비명 중복체크
        /*
         * 2024-10-14 장프로 로직 화면에서 중복 검사로 수정으로 불필요
         * MybatisInput input = MybatisInput.of().add("upNaviId",
         * navigation.getUpNaviId())
         * .add("upNaviNo", navigation.getUpNaviNo())
         * .add("naviNm", navigation.getNaviNm())
         * .add("naviType", navigation.getNaviType());
         *
         * String uniqueNaviName = createUniqueNaviName(input);
         * navigation.setNaviNm(uniqueNaviName);
         */

        return navigationRepository.save(navigation);
    }

    /**
     * 폴더 생성
     *
     * @param DcStorageMain (document) 문서관리 테이블 엔티티
     * @return DcStorageMain (dcStorageMain) 문서관리 테이블 엔티티
     * @throws
     */
    public DcStorageMain createDcStorageMain(DcStorageMain document) {
        String upDocId = document.getUpDocId();

        if(!"#".equals(upDocId)){
            Map<String, Object> map = new HashMap<>();
            map.put("docId", document.getUpDocId());

            Map<String,Object> selectedDocument = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.selectOneDocument",map);
            document.setUpDocNo((Integer)selectedDocument.get("doc_no"));
        }

        DcStorageMain dcStorageMain = storageMainRepository.save(document);

        // 상위 문서가 가진 권한을 넣어줌
        MybatisInput input = MybatisInput.of().add("id", dcStorageMain.getDocId())
                .add("no", dcStorageMain.getDocNo())
                .add("usrId", dcStorageMain.getRgstrId());

        // 최상위 문서인 경우, 네비게이션 id, no로 세팅
        if ("#".equals(dcStorageMain.getUpDocId())) {
            input.add("upId", dcStorageMain.getNaviId());
            input.add("upNo", dcStorageMain.getNaviNo());
        } else {
            input.add("upId", dcStorageMain.getUpDocId());
            input.add("upNo", dcStorageMain.getUpDocNo());
        }

        mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.insertAutoMyAuthority", input);

        return dcStorageMain;
    }

    /**
     * 네비 권한설정 리스트 가져오기
     *
     * @param DcStorageMain (document) 문서관리 테이블 엔티티
     * @return List<Map<String, ?>> 권한리스트
     * @throws
     */
    public List<Map<String, ?>> getDocumentNavigationAuthorityList(String pjtType, int naviNo, String naviId,
                                                                   int upNaviNo, String upNaviId, String pjtNo, String cntrctNo, String naviLv) {

        MybatisInput input = MybatisInput.of().add("naviNo", naviNo)
                .add("naviId", naviId)
                .add("upNaviNo", upNaviNo)
                .add("upNaviId", upNaviId)
                .add("pjtNo", pjtNo)
                .add("cntrctNo", cntrctNo)
                .add("pjtType", pjtType);
        /* 기존 코드(25.03.11) */
        // MybatisInput input = MybatisInput.of().add("naviNo", naviNo)
        // .add("naviId", naviId)
        // .add("upNaviNo", upNaviNo)
        // .add("upNaviId", upNaviId)
        // .add("pjtNo", pjtNo)
        // .add("cntrctNo", cntrctNo)
        // .add("pjtType", pjtType);

        if (naviLv.equals("0")) {
            return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.firstNaviAuthority", input);
        } else {
            return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.naviAuthority", input);
        }
    }

    /**
     * 문서, 네비게이션 권한 설정
     *
     * @param addAuthorityList
     * @param delAuthorityList
     * @param updateAuthorityList
     */
    @Transactional
    public void setAuthorityList(List<DcAuthority> addAuthorityList, List<DcAuthority> delAuthorityList,
                                 List<DcAuthority> updateAuthorityList) {
        if (!addAuthorityList.isEmpty()) {
            createAuthorityList(addAuthorityList);
        } else if (!delAuthorityList.isEmpty()) {
            delAuthorityList.stream().forEach(authority -> {
                deleteAuthority(authority);
            });
        } else if (!updateAuthorityList.isEmpty()) {
            updateAuthorityList.stream().forEach(authority -> {
                updateAuthority(authority);
            });
        }
    }

    /**
     * 네비 권한설정 신규
     *
     * @param DcAuthority (authority) 문서관한 테이블 엔티티
     * @return DcAuthority 문서권한정보
     * @throws
     */
    public DcAuthority createAuthority(DcAuthority authority) {
        authority.setRghtNo(null);
        authority.setDltYn("N");

        return authorityRepository.save(authority);
    }

    /**
     * 상위 권한으로 하위 문서 권한 설정
     * @param input 상위 문서(폴더) or 네비게이션 id, no를 담은 Map
     * @return
     */
    public int createSubDocAuthorityList(Map<String, Object> input) {
        return mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.insertAutoMyAuthority", input);
    }

    @Transactional
    public void createAuthorityList(List<DcAuthority> authorityList) {
        authorityList.stream().forEach(authority -> {
            authority.setRghtNo(null);
            authority.setDltYn("N");
            authorityRepository.save(authority);
        });
    }

    /**
     * 네비 권한설정 수정
     *
     * @param DcAuthority (authority) 문서관한 테이블 엔티티
     * @return DcAuthority 문서권한정보
     * @throws
     */
    public void updateAuthority(DcAuthority authority) {
        MybatisInput input = MybatisInput.of().add("rghtTy", authority.getRghtTy())
                .add("usrId", UserAuth.get(true).getUsrId())
                .add("rghtNo", authority.getRghtNo());

        mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.updateNaviAuthority", input);
    }

    /**
     * 네비 권한설정 삭제
     *
     * @param DcAuthority (authority) 문서관한 테이블 엔티티
     * @return DcAuthority 문서권한정보
     * @throws 2
     */
    public void deleteAuthority(DcAuthority authority) {
        MybatisInput input = MybatisInput.of().add("usrId", UserAuth.get(true).getUsrId())
                .add("naviId", authority.getId())
                .add("naviNo", authority.getNo())
                .add("rghtGrpNo", authority.getRghtGrpNo())
                .add("rghtGrpCd", authority.getRghtGrpCd());

        mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.deleteNaviAuthority", input);
    }

    /**
     * 네비 권한설정 권한 사용자 조회
     *
     * @param int    (rghtGrpNo) 권한그룹 넘버
     * @param String (rghtGrpCd) 권한그룹 코드
     * @param String (pstnCd) 직책 공통 그룹 코드
     * @return List<Map<String, ?>> 권한 사용자리스트
     * @throws
     */
    public List<Map<String, ?>> getNaviAuthorityGroupUserList(int rghtGrpNo, String rghtGrpCd, String pstnCd) {
        MybatisInput input = MybatisInput.of().add("rghtGrpNo", rghtGrpNo)
                .add("rghtGrpCd", rghtGrpCd)
                .add("pstnCd", pstnCd);

        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.naviAuthorityGroupUserList", input);
    }

    /**
     * 문서 권한 삭제
     * @param subDocIdList 문서(폴더) or 네비게이션 하위의 문서 ids
     */
    public void deleteSubDocAuthorityList(List<String> subDocIdList) {
        mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.deleteSubDocAuthorityList", subDocIdList);
    }

    /**
     * 네비 속성 정의 - 속성리스트 조회
     *
     * @param naviId
     * @return
     */
    public List<DcProperty> getDocumentNavigationPropertyList(String naviId) {
        return propertyRepository.findByNaviIdAndDltYnOrderByAttrbtDsplyOrderAsc(naviId, "N");
    }

    /**
     * 네비 속성 코드 중복 체크
     *
     * @param attrbtCd
     * @param naviId
     * @return
     */
    public DcProperty attrbtCdExist(String attrbtCd, String naviId) {
        return propertyRepository.findByAttrbtCdAndNaviIdAndDltYn(attrbtCd, naviId, "N").orElse(null);
    }

    /**
     * 네비 속성 정보를 가지고 html 요소 생성
     *
     * @param naviId
     * @return
     */
    public List<Map<String, ?>> getPropertyToStringHtmlElements(MybatisInput input) {
        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.docItemCreatePropertyListToHtml", input);
    }

    /**
     * 문서 저장 - 아이템 형
     *
     * @param docParam
     * @param propertyDataList
     * @param files
     */
    @Transactional
    public void createDocItem(DcStorageMain docParam, List<DcPropertyData> propertyDataList, String cntrctNo,
                              List<MultipartFile> files, List<MultipartFile> newSubFiles, List<String> subFileAttrbtCds) {
        List<String> availableExt = getAvailableFileExt();

        //1. dc_storage_main 저장.
        DcStorageMain successDoc = storageMainRepository.save(docParam);

        if (successDoc.getDocId() == null) {
            throw new GaiaBizException(ErrorType.NO_DATA, "Not found Document Data.");
        }

        //2. dc_property_data 저장.
        propertyDataList.stream().map(propertyData -> {
            propertyData.setDocId(successDoc.getDocId());
            propertyData.setDocNo(successDoc.getDocNo());
            return propertyData;
        }).toList();

        createPropertyDataList(propertyDataList);

        //3. dc_attachment 저장.
        boolean hasValidFile = false;

        List<DcAttachments> dcAttachmentsList = new ArrayList<>();

        /**
         * 파일 업로드 경로
         */
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                String originalFileName = file.getOriginalFilename();

                // 파일 이름과 확장자 검증
                if (originalFileName == null || originalFileName.contains("..")) {
                    log.warn("잘못된 파일 이름: {}", originalFileName);
                    continue;
                }

                // 확장자 추출 및 유효성 검사
                String fileExtension = fileService.getFileExtension(originalFileName, false);
                if (!availableExt.contains(fileExtension.toLowerCase())) {
                    log.warn("허용되지 않은 파일 확장자: {}", fileExtension);
                    throw new GaiaBizException(ErrorType.INVAILD_INPUT_DATA,
                            "허용되지 않은 파일 확장자가 포함되어 있습니다: " + fileExtension);
                }

                // 파일 크기 제한 (100MB)
                if (file.getSize() > 100 * 1024 * 1024) {
                    log.warn("파일 크기가 너무 큽니다: {} bytes", file.getSize());
                    throw new GaiaBizException(ErrorType.INVAILD_INPUT_DATA,
                            "파일 사이즈가 너무 큽니다. : " + file.getSize() + " bytes");
                }

                hasValidFile = true;

                FileMeta fileMeta = fileService.save(getUploadPathByWorkTypeForDocument(FileUploadType.ITEM, platform, cntrctNo), file);

                DcAttachments dcAttachments = new DcAttachments();
                dcAttachments.setDocId(successDoc.getDocId());
                dcAttachments.setDocNo(successDoc.getDocNo());
                dcAttachments.setFileNm(file.getOriginalFilename());
                dcAttachments.setFileDiskNm(fileMeta.getFileName());
                dcAttachments.setFileDiskPath(fileMeta.getDirPath());
                dcAttachments.setFileSize(fileMeta.getSize());
                dcAttachments.setFileHitNum((short) 0);

                dcAttachments.setDltYn("N"); // DB에서 기본값 세팅되면 코드 삭제

                dcAttachmentsList.add(dcAttachments);
            }
            if (hasValidFile) { // 유효한 파일이 있을 때만 실행
                createDcAttachmentsList(dcAttachmentsList);
            }
        }

        //4. 속성 타입이 첨부파일인 데이터 저장.
        updateSubAttachmentList(null, successDoc.getDocNo(), successDoc.getDocId(), cntrctNo, newSubFiles, subFileAttrbtCds);


        // 문서 권한 설정
        // 네비게이션 경로 생성 후 생성 경로의 상위 경로와 동일한 권한을 부여
        MybatisInput input = MybatisInput.of().add("id", docParam.getDocId())
                .add("no", docParam.getDocNo())
                .add("usrId", docParam.getRgstrId());

        // 상위 폴더가 없을 경우, 네비게이션 Id, no
        if ("#".equals(docParam.getUpDocId())) {
            input.add("upId", docParam.getNaviId())
                    .add("upNo", docParam.getNaviNo());
        } else { // 있을 경우, 상위 문서의 docId, docNo
            input.add("upId", docParam.getUpDocId())
                    .add("upNo", docParam.getUpDocNo());
        }
        mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.insertAutoMyAuthority", input);

    }

    /**
     * 아이템형 문서 저장 시, 속성 데이터 추가
     *
     * @param propertyList
     * @return
     */
    public List<DcPropertyData> createPropertyDataList(List<DcPropertyData> propertyDataList) {

        return propertyDataRepository.saveAll(propertyDataList);
    }

    /**
     * 아이템형 문서 저장 시, 첨부파일 데이터 추가
     *
     * @param dcAttachmentsList
     * @return
     */
    public List<DcAttachments> createDcAttachmentsList(List<DcAttachments> dcAttachmentsList) {
        return attachmentsRepository.saveAllAndFlush(dcAttachmentsList);
    }

    /**
     * 권한 복사 전, 네비게이션 / 문서 권한 여부 체크
     *
     * @param copyAuthorityInput
     * @return
     */
    public List<DcAuthority> getAuthority(String targetId, Integer targetNo) {

        return authorityRepository.findAllByIdAndNoAndDltYn(targetId, targetNo, "N");
    }

    /**
     * 권한 붙여넣기 - 하위 네비게이션, 문서까지 적용.
     *
     * @param input
     */
    @Transactional
    public void pasteAuthority(MybatisInput copyAuthorityInput) {
        // 붙이고자 하는 네비게이션, 문서의 기존 권한 삭제.
        deleteAuthorityList(copyAuthorityInput);

        // 붙이고자 하는 권한을 새로 생성.
        mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.pasteAuthority", copyAuthorityInput);
    }

    /**
     * 권한 복사, 붙여넣기 전 기존 권한 삭제.
     *
     * @param input
     */
    public void deleteAuthorityList(MybatisInput copyAuthorityInput) {

        mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.deleteNaviAuthorityList", copyAuthorityInput);
    }

    /**
     * 네비게이션 속성 복사 전, 속성 존재여부 체크.
     *
     * @param targetId
     * @param targetNo
     * @return
     */
    public List<DcProperty> getNavigationProperty(String targetId, Integer targetNo) {
        return propertyRepository.findAllByNaviIdAndNaviNoAndDltYn(targetId, targetNo, "N");
    }

    /**
     * 속성 붙여넣기
     *
     * @param input
     */
    public void pasteNavigationProperty(MybatisInput copyPropertyInput) {
        // 기존 속성에 추가로 생성
        mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.insertPropertyList", copyPropertyInput);
    }

    /**
     * 문서리스트 조회 - JSON 형태로
     *
     * @param input
     * @return
     */
    @Transactional
    public Page<JsonNode> getDocumentList(MybatisInput input) {
        boolean isAdmin = (boolean) input.get("isAdmin");
        List<String> jsonResult;

        if (isAdmin) { // 관리자의 경우,
            jsonResult = mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.selectDocumentListAdmin", input);
        } else { // 일반 사용자의 경우
            jsonResult = mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.selectDocumentList", input);
        }

        // JSON 문자열 리스트를 JsonNode 리스트로 변환
        List<JsonNode> contents = jsonResult.stream().map(json -> {
                    try {
                        return objectMapper.readTree(json);
                    } catch (JsonProcessingException e) {
                        log.error("JSON 파싱 실패. 원본 데이터: {}", json, e);
                        throw new GaiaBizException(ErrorType.INTERNAL_SERVER_ERROR, "문서 목록 조회 중 JSON 파싱 오류 발생");
                    }
                })
                .collect(Collectors.toList());

        Long totalCount = getDocumentListCount(input, isAdmin);

        // Page 객체 생성하여 반환
        return new PageImpl<>(contents, input.getPageable(), totalCount);
    }

    /**
     * 해당 위치(네비, 문서)의 문서의 총 개수 조회
     *
     * @param input
     * @return
     */
    public Long getDocumentListCount(MybatisInput input, boolean isAdmin) {
        if (isAdmin) { // 관리자의 경우,
            return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.getDocumentListCountAdmin", input);
        } else { // 일반 사용자의 경우
            return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.getDocumentListCount", input);
        }
    }

    /**
     * 그리드 동적 속성(컬럼)리스트 조회
     *
     * @param naviId
     * @param langInfo
     * @return
     */
    public List<Map<String, ?>> getPropertyList(String naviId, String langInfo) {

        MybatisInput input = MybatisInput.of().add("naviId", naviId)
                .add("lang", langInfo);
        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.getPropertyList", input);
    }

    /**
     * 대시보드 문서 리스트 조회
     *
     * @param cntrctNo
     * @param loginId
     * @return
     */
    public List<Map<String, ?>> getDocListToDashboard(String cntrctNo, String loginId, UserAuth user) {
        String naviDiv = "01"; // 01 : 통합문서관리
        String itemId = naviDiv + "_" + cntrctNo;
        boolean isAdmin = user.isAdmin();

        MybatisInput input = MybatisInput.of().add("itemId", itemId)
                .add("loginId", loginId)
                .add("pjtNo", cntrctNo);

        if (isAdmin) { // 관리자의 경우,
            String pjtDiv = platform; // gaia / cairos 인지 구분
            input.add("pjtDiv", pjtDiv);

            return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.getDocListToDashboardAdmin", input);
        } else { // 일반 사용자의 경우
            return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.getDocListToDashboard", input);
        }
    }

    /**
     * 문서명 중복체크
     *
     * @param String (naviId) 네비게이션 아이디
     * @param String (upDocId) 상위폴더 아이디
     * @param String (docNm) 변경할 문서명
     * @return String (Y or N)
     * @throws
     */
    public String updateDocExist(String naviId, String upDocId, String docNm) {
        // 폴더명 중복체크
        MybatisInput input = MybatisInput.of().add("naviId", naviId)
                .add("upDocId", upDocId)
                .add("docNm", docNm);

        Map<String, Object> result = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.updateDocExist", input);
        String name_exist = (String) result.get("name_exist");

        return name_exist;
    }

    /**
     * 문서명 변경(문서 수정)
     *
     * @param document
     * @return
     */
    @Transactional
    public DcStorageMain updateDocument(DcStorageMain document, UserAuth user) {

        DcStorageMain updateDoc = storageMainRepository.saveAndFlush(document);

        // 이름 변경할 문서가 폴더인 경우 하위 문서의 navi 정보와 path 정보 수정
        if ("FOLDR".equals(updateDoc.getDocType())) {
            // 하위에 문서가 존재하는지 확인.
            Map<String, Long> counts = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.getFolderAndFileCounts", updateDoc.getDocId());
            Long fileCnt = counts.get("file_count");
            Long folderCnt = counts.get("folder_count");

            // 하위 문서가 존재하는 경우에만 실행
            if (fileCnt > 0 || folderCnt > 0) {
                MybatisInput updateMoveSubDocPathInput = MybatisInput.of().add("upDocId", updateDoc.getDocId())
                        .add("naviId", updateDoc.getNaviId())
                        .add("naviNo", updateDoc.getNaviNo())
                        .add("usrId", user.getUsrId());
                mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.updateMoveSubDocPath", updateMoveSubDocPathInput);
            }
        }

        return updateDoc;
    }

    /**
     * 문서의 속성 데이터 조회
     *
     * @param attrbtCd
     * @param docNo
     * @return
     */
    public DcPropertyData getPropertyData(String attrbtCd, Integer docNo) {
        return propertyDataRepository.findByAttrbtCdAndDocNo(attrbtCd, docNo);
    }

    /**
     * 문서 속성 정보 데이터 수정
     *
     * @param attrbtData
     * @return
     */
    @Transactional
    public Map<String, Object> updatePropertyData(DocumentForm.PropertyDataUpdate propData, List<MultipartFile> newFiles,
                                                  List<MultipartFile> newSubFiles,
                                                  List<String> subFileAttrbtCds,
                                                  List<Integer> removedFileNos,
                                                  List<Integer> removedSubFileNos,
                                                  UserAuth user) {

        Map<String, Object> result = new HashMap<>();

        // 1. 수정할 문서 가져오기
        DcStorageMain updateDoc = storageMainRepository.findByDocIdAndDltYn(propData.getDocId(), "N")
                .orElseThrow(() -> new GaiaBizException(ErrorType.NO_DATA, "Not Found update Document"));

        // 수정한 문서의 수정일자 변경
        updateDoc.setChgDt(LocalDateTime.now());
        updateDoc.setChgId(user.getUsrId());
        storageMainRepository.save(updateDoc);

        // 2. 속성 데이터 저장
        List<DcPropertyData> updatePropertyDataList = propData.getAttrbtDataList().stream()
                .map(attrbtData -> {
                    DcPropertyData updateDcPropertyData = getPropertyData(attrbtData.getAttrbtCd(),
                            propData.getDocNo());
                    if (updateDcPropertyData == null) {
                        updateDcPropertyData = new DcPropertyData();
                        updateDcPropertyData.setAttrbtCd(attrbtData.getAttrbtCd());
                        updateDcPropertyData.setDocId(propData.getDocId());
                        updateDcPropertyData.setDocNo(propData.getDocNo());
                    }
                    updateDcPropertyData.setAttrbtCntnts(attrbtData.getAttrbtCntnts());
                    return updateDcPropertyData;
                }).toList();

        propertyDataRepository.saveAll(updatePropertyDataList);
        propertyDataRepository.flush(); // 저장 직후 플러시

        // 3. 본문 파일 수정
        if ((newFiles != null && !newFiles.isEmpty()) || (removedFileNos != null && !removedFileNos.isEmpty())) {
            updateAttachmentList(removedFileNos, propData.getDocNo(), propData.getDocId(), propData.getCntrctNo(), newFiles);
        }

        // 4. 속성 파일 수정
        if ((newSubFiles != null && !newSubFiles.isEmpty())
                || (removedSubFileNos != null && !removedSubFileNos.isEmpty())) {
            updateSubAttachmentList(removedSubFileNos, propData.getDocNo(), propData.getDocId(), propData.getCntrctNo(), newSubFiles,
                    subFileAttrbtCds);
        }

        result.put("resultCode", "01");
        result.put("resultMsg", "속성정보가 수정되었습니다.");

        return result;

    }

    /**
     * 속성 데이터 수정(아이템형) - 첨부파일 삭제
     *
     * @param removedFileNos
     * @param removedDocNos
     */
    @Transactional
    public void deleteAttachmentList(List<Integer> removedFileNos, Integer docNo) {
        for (int fileNo : removedFileNos) {
            DcAttachments attachment = attachmentsRepository.findByFileNoAndDocNoAndDltYn(fileNo, docNo, "N");

            // 삭제 상태로 업데이트
            attachment.setDltYn("Y");
            attachment.setDltId(UserAuth.get(true).getUsrId());
            attachment.setDltDt(LocalDateTime.now());

            attachmentsRepository.saveAndFlush(attachment);
        }
    }

    /**
     * 속성 데이터 수정 - 첨부파일 데이터 수정
     *
     * @param removedFileNos
     * @param docNo
     * @param docId
     * @param newFiles
     */
    @Transactional
    public void updateAttachmentList(List<Integer> removedFileNos, Integer docNo, String docId, String cntrctNo,
                                     List<MultipartFile> newFiles) {
        List<String> availableExt = getAvailableFileExt();

        if (removedFileNos != null) {
            // 2. 기존 파일 중 삭제 처리
            deleteAttachmentList(removedFileNos, docNo);
        }

        // 3. 새 파일 추가
        boolean hasValidFile = false;

        List<DcAttachments> dcAttachmentsList = new ArrayList<>();

        if (newFiles != null && !newFiles.isEmpty()) {
            for (MultipartFile file : newFiles) {
                String originalFileName = file.getOriginalFilename();

                // 파일 이름과 확장자 검증
                if (originalFileName == null || originalFileName.contains("..")) {
                    log.warn("잘못된 파일 이름: {}", originalFileName);
                    continue;
                }

                // 확장자 추출 및 유효성 검사
                String fileExtension = fileService.getFileExtension(originalFileName, false);
                if (!availableExt.contains(fileExtension.toLowerCase())) {
                    log.warn("허용되지 않은 파일 확장자: {}", fileExtension);
                    throw new GaiaBizException(ErrorType.INVAILD_INPUT_DATA,
                            "허용되지 않은 파일 확장자가 포함되어 있습니다: " + fileExtension);
                }

                // 파일 크기 제한 (100MB)
                if (file.getSize() > 100 * 1024 * 1024) {
                    log.warn("파일 크기가 너무 큽니다: {} bytes", file.getSize());
                    throw new GaiaBizException(ErrorType.INVAILD_INPUT_DATA,
                            "파일 사이즈가 너무 큽니다. : " + file.getSize() + " bytes");
                }

                hasValidFile = true;

                FileMeta fileMeta = fileService.save(getUploadPathByWorkTypeForDocument(FileUploadType.ITEM, platform, cntrctNo), file);

                DcAttachments dcAttachments = new DcAttachments();
                dcAttachments.setDocId(docId);
                dcAttachments.setDocNo(docNo);
                dcAttachments.setFileNm(file.getOriginalFilename());
                dcAttachments.setFileDiskNm(fileMeta.getFileName());
                dcAttachments.setFileDiskPath(fileMeta.getDirPath());
                dcAttachments.setFileSize(fileMeta.getSize());
                dcAttachments.setFileHitNum((short) 0);

                dcAttachments.setDltYn("N"); // DB에서 기본값 세팅되면 코드 삭제

                dcAttachmentsList.add(dcAttachments);
            }
            if (hasValidFile) { // 유효한 파일이 있을 때만 실행
                createDcAttachmentsList(dcAttachmentsList);
            }
        }
    }

    /**
     * 속성 데이터 수정 - 단일 속성 정보 첨부파일 데이터 수정
     *
     * @param removedFileNos
     * @param docNo
     * @param docId
     * @param newFiles
     * @param updatePropertyDataList
     * @param subFileAttrbtCds
     */
    @Transactional
    public void updateSubAttachmentList(List<Integer> removedFileNos, Integer docNo, String docId, String cntrctNo,
                                        List<MultipartFile> newFiles, List<String> newFilesAttrbtCdList) {
        List<String> availableExt = getAvailableFileExt();

        if (removedFileNos != null) {
            // 2. 기존 파일 중 삭제 처리
            deleteAttachmentList(removedFileNos, docNo);
        }

        /**
         * 파일 업로드 경로
         */

        if (newFiles != null && !newFiles.isEmpty()) {
            for (int i = 0; i < newFiles.size(); i++) {
                MultipartFile file = newFiles.get(i);
                String attrbtCd = newFilesAttrbtCdList.get(i); // attrbtCd 매칭

                String originalFileName = file.getOriginalFilename();

                // 파일 이름과 확장자 검증
                if (originalFileName == null || originalFileName.contains("..")) {
                    log.warn("잘못된 파일 이름: {}", originalFileName);
                    continue;
                }

                // 확장자 추출 및 유효성 검사
                String fileExtension = fileService.getFileExtension(originalFileName, false);
                if (!availableExt.contains(fileExtension.toLowerCase())) {
                    log.warn("허용되지 않은 파일 확장자: {}", fileExtension);
                    throw new GaiaBizException(ErrorType.INVAILD_INPUT_DATA,
                            "허용되지 않은 파일 확장자가 포함되어 있습니다: " + fileExtension);
                }

                // 파일 크기 제한 (100MB)
                if (file.getSize() > 100 * 1024 * 1024) {
                    log.warn("파일 크기가 너무 큽니다: {} bytes", file.getSize());
                    throw new GaiaBizException(ErrorType.INVAILD_INPUT_DATA,
                            "파일 사이즈가 너무 큽니다. : " + file.getSize() + " bytes");
                }

                FileMeta fileMeta = fileService.save(getUploadPathByWorkTypeForDocument(FileUploadType.ITEM, platform, cntrctNo), file);

                DcAttachments dcAttachments = new DcAttachments();
                dcAttachments.setDocId(docId);
                dcAttachments.setDocNo(docNo);
                dcAttachments.setFileNm(file.getOriginalFilename());
                dcAttachments.setFileDiskNm(fileMeta.getFileName());
                dcAttachments.setFileDiskPath(fileMeta.getDirPath());
                dcAttachments.setFileSize(fileMeta.getSize());
                dcAttachments.setFileHitNum((short) 0);

                dcAttachments.setDltYn("N"); // DB에서 기본값 세팅되면 코드 삭제

                // 파일 저장 후
                DcAttachments savedFile = attachmentsRepository.saveAndFlush(dcAttachments);

                // 저장된 파일번호(fileNo)를 attrbtCntnts로 세팅
                DcPropertyData propertyData = propertyDataRepository.findByAttrbtCdAndDocNo(attrbtCd, docNo);
                if (propertyData == null) {
                    propertyData = new DcPropertyData();
                    propertyData.setDocNo(docNo);
                    propertyData.setDocId(docId);
                    propertyData.setAttrbtCd(attrbtCd);
                }
                propertyData.setAttrbtCntnts(String.valueOf(savedFile.getFileNo()));
                propertyDataRepository.saveAndFlush(propertyData);
            }
        }
    }

    /**
     * 문서 권한 리스트 조회
     *
     * @param pjtType
     * @param docNo
     * @param docId
     * @param naviNo
     * @param naviId
     * @param upDocNo
     * @param upDocId
     * @param pjtNo
     * @param cntrctNo
     * @return
     */
    public List<Map<String, ?>> getDocumentAuthorityList(String pjtType, int docNo, String docId, int naviNo,
                                                         String naviId, int upDocNo, String upDocId, String pjtNo, String cntrctNo) {

        MybatisInput input = MybatisInput.of().add("docNo", docNo)
                .add("docId", docId)
                .add("pjtNo", pjtNo)
                .add("cntrctNo", cntrctNo)
                .add("pjtType", pjtType);

        // 최상위 문서인 경우, upDocNo, upDocId => naviNo, naviId
        if ("#".equals(upDocId)) {
            input.add("upDocId", naviId);
            input.add("upDocNo", naviNo);
        } else {
            input.add("upDocId", upDocId);
            input.add("upDocNo", upDocNo);
        }

        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.docAuthority", input);
    }

    /**
     * 문서 DB 저장
     *
     * @param documentList
     */
    public void createDocumentList(List<DcStorageMain> documentList) {
        this.createDocumentList(documentList, null, null);
    }
    public void createDocumentList(List<DcStorageMain> documentList, String usrId, List<DocumentForm.PropertyData> propertyDataList) {
        for (DcStorageMain document : documentList) {
            Map<String, Object> map = new HashMap<>();
            String upDocId = document.getUpDocId();
            if(!"#".equals(upDocId)){
                map.put("docId", document.getUpDocId());
                Map<String,Object> selectedDocument = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.selectOneDocument",map);
                document.setUpDocNo((Integer)selectedDocument.get("doc_no"));
            }

            if (!StringUtils.isEmpty(usrId)) {
                document.setRgstrId(usrId);
                document.setChgId(usrId);
            }

            document = storageMainRepository.save(document);

            // 상위 경로의 권한으로 문서 권한 생성.
            MybatisInput input = MybatisInput.of().add("id", document.getDocId())
                    .add("no", document.getDocNo())
                    .add("usrId", document.getRgstrId());

            // 상위 폴더가 없을 경우, 네비게이션 Id, no
            if ("#".equals(document.getUpDocId())) {
                input.add("upId", document.getNaviId())
                        .add("upNo", document.getNaviNo());
            } else { // 있을 경우, 상위 문서의 docId, docNo
                input.add("upId", document.getUpDocId())
                        .add("upNo", document.getUpDocNo());
            }
            mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.insertAutoMyAuthority", input);

            if (propertyDataList != null && !propertyDataList.isEmpty()) {
                for (DocumentForm.PropertyData propertyData : propertyDataList) {
                    propertyData.setDocNo(document.getDocNo());
                    propertyData.setDocId(document.getDocId());
                    propertyData.setRgstrId(document.getRgstrId());
                    propertyData.setChgId(document.getRgstrId());
                }
            }
        }

        if (propertyDataList != null && !propertyDataList.isEmpty()) {
            mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.insertPropertyData", propertyDataList);
        }
    }

    /**
     * 문서 조회(DcStorageMain) - id
     *
     * @param docId
     * @return
     */
    public DcStorageMain getDcStorageMain(String docId) {
        return storageMainRepository.findByDocIdAndDltYn(docId, "N").orElse(null);
    }
    public DcStorageMain getDcStorageMainByDocId(String docId){
        return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.selectDcStorageMainByDocId", docId);

    }

    /**
     * 문서 조회(DcStorageMain) - no
     *
     * @param docNo
     * @return
     */
    public DcStorageMain getDcStorageMain(Integer docNo) {
        return storageMainRepository.findByDocNoAndDltYn(docNo, "N").orElse(null);
    }

    public DcStorageMain getLastestDcStorageMainByFolderType(Map<String, String> params) {
        return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.selectLastestDcStorageMainByFolderType", params);
    }

    /**
     * 문서리스트 조회(DcStorageMain) - id
     *
     * @param docId
     * @return
     */
    public List<DcStorageMain> getDcStorageMainList(List<String> docIdList) {
        List<DcStorageMain> dcStorageMainList = new ArrayList<>();

        for (String docId : docIdList) {
            DcStorageMain dcStorageMain = getDcStorageMain(docId);
            if (dcStorageMain != null) {
                dcStorageMainList.add(dcStorageMain);
            } else {
                throw new GaiaBizException(ErrorType.NO_DATA);
            }

        }
        return dcStorageMainList;
    }

    /**
     * 문서 리스트 조회 by navi_id
     * @param naviId
     * @return
     */
    public List<DcStorageMain> getDcStorageMainListByNaviId(String naviId) {
        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.selectDcStorageMainListByNaviId", naviId);
    }

    /**
     * 하위 문서 리스트 조회 by up_doc_id
     * @param upDocId
     * @return
     */
    public List<DcStorageMain> getSubDocumentListByUpDocId(String upDocId) {
        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.selectSubDocumentListByUpDocId", upDocId);
    }

    /**
     * 아이템형 첨부파일 정보 리스트 조회
     *
     * @param itemDocId
     * @return
     */
    public List<DcAttachments> getDcAttachmentList(String itemDocId) {
        return attachmentsRepository.findAllByDocIdAndDltYn(itemDocId, "N");
    }

    /**
     * 하위 문서 존재 여부 및 파일, 폴더 개수 확인
     *
     * @param docId
     * @return
     */
    public Map<String, Long> getFileFolderCounts(String docId) {
        // getFolderAndFileCounts를 사용하여 값 가져오기
        Map<String, Long> fileFoldercountsMap = new HashMap<>();

        Map<String, Long> counts = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.getFolderAndFileCounts", docId);

        fileFoldercountsMap.put("files", counts.get("file_count"));
        fileFoldercountsMap.put("folders", counts.get("folder_count"));

        return fileFoldercountsMap;
    }

    /**
     * 문서 삭제
     *
     * @param docIdList
     * @param user
     */
    @Transactional
    public void deleteDocument(List<String> docIdList, UserAuth user) {
        // 폴더를 삭제 시 하위 데이터도 삭제 처리
        docIdList.forEach(docId -> {
            // 각 docId마다 MybatisInput 생성
            MybatisInput input = MybatisInput.of()
                    .add("docId", docId)
                    .setUser(user);

            //Dc_attachment에 파일 존재하면 삭제 처리.(dlt_yn = 'Y')
            mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.deleteAttachments", input);

            //TODO: dc_property_data가 있으면 데이터 삭제 처리..?
//            mybatisSession.delete("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.deletePropertyData", input);

            // MyBatis 파라미터로 전달하여 처리
            mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.deleteDocumentList", input);
        });
    }
    public void deleteDocument(String docId, String usrId) {
        Map<String, Object> params = new HashMap<>();
        params.put("docId", docId);
        params.put("usrId", usrId);
//        mybatisSession.delete("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.hardDeleteDocument", docId);
        mybatisSession.delete("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.logicalDeleteDocument", params);
    }

    /**
     * 네비 + 문서 full 경로 리스트 조회
     *
     * @param topNaviId
     * @param user
     * @return
     */
    public List<Map<String, ?>> getNaviDocTreeList(String topNaviId, List<String> docFolderIdList, UserAuth user) {
        MybatisInput input = MybatisInput.of()
                .add("topNaviId", topNaviId)
                .add("isAdmin", user.isAdmin())
                .setUser(user);

        // 선택된 폴더 문서 id 리스트 (조회리스트에서 제외.)
        if (docFolderIdList != null) {
            input.add("docFolderIdList", docFolderIdList);
        }

        boolean isAdmin = (boolean) input.get("isAdmin");

        if (isAdmin) { // 관리자의 경우,
            return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.getNaviDocTreeListAdmin", input);
        } else { // 일반 사용자의 경우
            return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.getNaviDocTreeList", input);
        }
    }

    /**
     * 문서 이동
     *
     * @param sourceItemId
     * @param sourceItemNo
     * @param sourceItemKind
     * @param targetDocIdList
     */
    @Transactional
    public List<Map<String, ?>> moveDocument(String sourceItemId, Integer sourceItemNo, String sourceItemKind,
                                             String sourceItemPath,
                                             List<String> targetDocIdList, UserAuth user) {

        List<DcStorageMain> targetDcStorageMainList = getDcStorageMainList(targetDocIdList);
        Map<String, String> result = new HashMap<>();
        String naviId = "";
        String upDocId = "";

        // source 정보를 기반으로 문서 이동.
        // TODO: doc_url_path 변경 추가 필요.
        for (DcStorageMain moveDoc : targetDcStorageMainList) {
            // source 권한 정보를 기반으로 문서 권한 변경.
            MybatisInput copyAuthorityInput = MybatisInput.of().add("targetId", moveDoc.getDocId())
                    .add("targetNo", moveDoc.getDocNo())
                    .add("sourceId", sourceItemId)
                    .add("sourceNo", sourceItemNo)
                    .add("usrId", user.getUsrId());

            // 이동 위치가 최상위 문서인 경우
            if ("NAVI".equals(sourceItemKind)) {
                DcNavigation sourceDcNavigation = getNavigation(sourceItemId);
                moveDoc.setUpDocId("#");
                moveDoc.setUpDocNo(0);
                moveDoc.setNaviId(sourceDcNavigation.getNaviId());
                moveDoc.setNaviNo(sourceDcNavigation.getNaviNo());
                moveDoc.setDocPath(sourceItemPath);

                // 이동 위치가 네비게이션 하위의 폴더 문서인 경우
            } else if ("DOC".equals(sourceItemKind)) {
                DcStorageMain sourceDcStorageMain = getDcStorageMain(sourceItemId);
                moveDoc.setUpDocId(sourceDcStorageMain.getDocId());
                moveDoc.setUpDocNo(sourceDcStorageMain.getDocNo());
                moveDoc.setNaviId(sourceDcStorageMain.getNaviId());
                moveDoc.setNaviNo(sourceDcStorageMain.getNaviNo());
                moveDoc.setDocPath(sourceItemPath);
            }

            DcStorageMain successMoveDoc = storageMainRepository.saveAndFlush(moveDoc); // 이동한 위치에 문서 저장.(즉시 DB에 반영)

            // 이동할 문서가 폴더인 경우 하위 문서의 navi 정보와 path 정보 수정
            if ("FOLDR".equals(moveDoc.getDocType())) {
                // 하위에 문서가 존재하는지 확인.
                Map<String, Long> counts = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.getFolderAndFileCounts",
                        successMoveDoc.getDocId());
                Long fileCnt = counts.get("file_count");
                Long folderCnt = counts.get("folder_count");

                // 하위 문서가 존재하는 경우에만 실행
                if (fileCnt > 0 || folderCnt > 0) {
                    MybatisInput updateMoveSubDocPathInput = MybatisInput.of().add("upDocId", successMoveDoc.getDocId())
                            .add("naviId", successMoveDoc.getNaviId())
                            .add("naviNo", successMoveDoc.getNaviNo())
                            .add("usrId", user.getUsrId());
                    mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.updateMoveSubDocPath", updateMoveSubDocPathInput);
                }
            }

            pasteAuthority(copyAuthorityInput); // 이동한 문서들의 권한을 이동한 위치의 권한으로 변경.

            naviId = successMoveDoc.getNaviId();
            upDocId = successMoveDoc.getUpDocId();

        }
        ;

        if (!naviId.isEmpty() && !upDocId.isEmpty()) {
            result.put("navi_id", naviId);

            // upDocId가 "#"이면 selectList를 호출하지 않고 naviId만 반환
            if ("#".equals(upDocId)) {
                List<Map<String, ?>> simpleResult = new ArrayList<>();
                simpleResult.add(result);
                return simpleResult; // naviId만 포함된 리스트 반환
            } else {
                result.put("upDocId", upDocId);
                return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.getUpDocInfoList", upDocId);
            }
        } else {
            throw new GaiaBizException(ErrorType.NO_DATA, "SERVER ERROR : please try again.");
        }

    }

    /**
     * 문서 복사
     *
     * @param sourceItemId
     * @param sourceItemNo
     * @param sourceItemKind
     * @param targetDocIdList
     */
    @Transactional
    public List<Map<String, ?>> copyDocument(String sourceItemId, Integer sourceItemNo, String sourceItemKind,
                                             String sourceItemPath,
                                             List<String> targetDocIdList, UserAuth user) {

        DcStorageMain sourceDcStorageMain = getDcStorageMain(sourceItemId);
        DcNavigation sourceDcNavigation = getNavigation(sourceItemId);

        List<DcStorageMain> targetDcStorageMainList = getDcStorageMainList(targetDocIdList);

        Map<String, String> result = new HashMap<>();
        String naviId = "";
        String upDocId = "";

        // source 정보를 기반으로 문서 복사.
        // TODO: doc_url_path 변경 추가 필요.
        for (DcStorageMain copyDocSource : targetDcStorageMainList) {

            // 복사 객체 생성.
            DcStorageMain copyDoc = new DcStorageMain();

            // 바뀌는 데이터
            // 최상위 문서인 경우
            if ("NAVI".equals(sourceItemKind)) {
                copyDoc.setUpDocId("#");
                copyDoc.setUpDocNo(0);
                copyDoc.setNaviId(sourceDcNavigation.getNaviId());
                copyDoc.setNaviNo(sourceDcNavigation.getNaviNo());
                copyDoc.setDocPath(sourceItemPath);

                // 네비게이션 하위의 폴더 문서인 경우
            } else if ("DOC".equals(sourceItemKind)) {
                copyDoc.setUpDocId(sourceDcStorageMain.getDocId());
                copyDoc.setUpDocNo(sourceDcStorageMain.getDocNo());
                copyDoc.setNaviId(sourceDcStorageMain.getNaviId());
                copyDoc.setNaviNo(sourceDcStorageMain.getNaviNo());
                copyDoc.setDocPath(sourceItemPath);
            }

            String newCopyDocNm = createUniqueDocName(copyDocSource.getDocNm(), copyDoc.getUpDocId(),
                    copyDoc.getNaviId());
            copyDoc.setDocNm(newCopyDocNm);

            // 문서가 파일인 경우
            if ("FILE".equals(copyDocSource.getDocType())) {
                // 바뀌지 않을 데이터
                copyDoc.setDocDiskNm(copyDocSource.getDocDiskNm());
                copyDoc.setDocDiskPath(copyDocSource.getDocDiskPath());
                copyDoc.setDocType(copyDocSource.getDocType());
                copyDoc.setDocSize(copyDocSource.getDocSize());
                copyDoc.setDltYn(copyDocSource.getDltYn());

                copyDoc.setDocId(java.util.UUID.randomUUID().toString());

                DcStorageMain successCopyDoc = storageMainRepository.save(copyDoc); // 복사한 위치에 문서 저장.

                // 새로 권한 생성(복사할 위치의 권한 정보를 기반으로)
                List<DcAuthority> sourceAuthorities = getAuthority(sourceItemId, sourceItemNo);
                sourceAuthorities.stream().forEach(sourceAuthority -> {
                    DcAuthority createAuthority = new DcAuthority();
                    createAuthority.setId(successCopyDoc.getDocId());
                    createAuthority.setNo(successCopyDoc.getDocNo());
                    createAuthority.setRghtGrpCd(sourceAuthority.getRghtGrpCd());
                    createAuthority.setRghtGrpNo(sourceAuthority.getRghtGrpNo());
                    createAuthority.setRghtTy(sourceAuthority.getRghtTy());
                    createAuthority.setDltYn("N");

                    authorityRepository.save(createAuthority);
                });

                naviId = successCopyDoc.getNaviId();
                upDocId = successCopyDoc.getUpDocId();

                // 문서가 폴더인 경우
            } else if ("FOLDR".equals(copyDocSource.getDocType())) {
                // 하위에 문서가 존재하는지 확인.
                Map<String, Long> counts = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.getFolderAndFileCounts", copyDocSource.getDocId());
                Long fileCnt = counts.get("file_count");
                Long folderCnt = counts.get("folder_count");

                // 하위 문서가 존재하는 경우에만 실행
                if (fileCnt > 0 || folderCnt > 0) {
                    String newCopyDocId = java.util.UUID.randomUUID().toString();
                    MybatisInput insertSubDocCopyInput = MybatisInput.of().add("newDocNm", copyDoc.getDocNm())
                            .add("newUpDocId", copyDoc.getUpDocId())
                            .add("newUpDocNo", copyDoc.getUpDocNo())
                            .add("newDocId", newCopyDocId)
                            .add("sourceDocId", copyDocSource.getDocId())
                            .add("docPath", copyDoc.getDocPath())
                            .add("naviId", copyDoc.getNaviId())
                            .add("naviNo", copyDoc.getNaviNo())
                            .add("usrId", user.getUsrId());

                    mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.insertCopiedSubDocuments", insertSubDocCopyInput);

                    DcStorageMain successRootCopyDoc = getDcStorageMain(newCopyDocId);

                    // 새로 권한 생성(복사할 위치의 권한 정보를 기반으로)
                    // source 권한 정보를 기반으로 문서 권한 변경.
                    MybatisInput copyAuthorityInput = MybatisInput.of().add("targetId", successRootCopyDoc.getDocId())
                            .add("targetNo", successRootCopyDoc.getDocNo())
                            .add("sourceId", copyDoc.getUpDocId())
                            .add("sourceNo", copyDoc.getUpDocNo())
                            .add("usrId", user.getUsrId());

                    pasteAuthority(copyAuthorityInput);

                    naviId = successRootCopyDoc.getNaviId();
                    upDocId = successRootCopyDoc.getUpDocId();

                }
            }

        }
        ;

        if (!naviId.isEmpty() && !upDocId.isEmpty()) {
            result.put("navi_id", naviId);

            // upDocId가 "#"이면 selectList를 호출하지 않고 naviId만 반환
            if ("#".equals(upDocId)) {
                List<Map<String, ?>> simpleResult = new ArrayList<>();
                simpleResult.add(result);
                return simpleResult; // naviId만 포함된 리스트 반환
            } else {
                result.put("upDocId", upDocId);
                return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.getUpDocInfoList", upDocId);
            }

        } else {
            throw new GaiaBizException(ErrorType.NO_DATA, "SERVER ERROR : please try again.");
        }

    }

    private String createUniqueDocName(String originalName, String upDocId, String naviId) {
        Map<String, String> fileInfo = splitFileNameAndExtension(originalName);
        String baseName = fileInfo.get("baseName");
        String extension = fileInfo.get("extension");

        String newName = baseName + "-복사본" + extension;
        int copyIndex = 1;

        // 중복된 이름 확인 및 새 이름 생성
        while (storageMainRepository.existsByDocNmAndUpDocIdAndNaviIdAndDltYn(newName, upDocId, naviId, "N")) {
            // newName = baseName + "-복사본" + (copyIndex > 1 ? " (" + copyIndex + ")" : "") +
            // extension;
            newName = baseName + "-복사본" + " (" + copyIndex + ")" + extension;
            copyIndex++;
        }

        return newName;
    }

    // 파일명과 확장자 분리 메서드
    private Map<String, String> splitFileNameAndExtension(String originalName) {
        Map<String, String> fileInfo = new HashMap<>();
        String baseName = originalName;
        String extension = "";
        int dotIndex = originalName.lastIndexOf('.');

        if (dotIndex > 0) {
            baseName = originalName.substring(0, dotIndex);
            extension = originalName.substring(dotIndex);
        }

        fileInfo.put("baseName", baseName);
        fileInfo.put("extension", extension);

        return fileInfo;
    }

    /**
     * 네비게이션 속성 삭제
     *
     * @param attrbtNoList
     */
    public void deleteProperty(List<Integer> attrbtNoList) {
        attrbtNoList.stream()
                .forEach(attrbtNo -> propertyRepository.findById(attrbtNo).ifPresent(propertyRepository::updateDelete));
    }

    /**
     * 네비게이션 속성 추가 (다중)
     * @param dcPropertyList
     */
    @Transactional
    public void createPropertyList(List<DcProperty> dcPropertyList) {
        for (DcProperty dcProperty : dcPropertyList) {
            this.createProperty(dcProperty);
        }
    }

    /**
     * 네비게이션 속성 추가
     *
     * @param property
     * @return
     */
    @Transactional
    public DcProperty createProperty(DcProperty property) {
        property.setDltYn("N");
        mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.createProperty", property);
        Integer attrbtNo = property.getAttrbtNo();

        // 삽입된 속성 정의 조회
        DcProperty savedProperty = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.selectOneProperty", attrbtNo);

        return savedProperty;
        // propertyRepository.save(property);
    }

    /**
     * 네비게이션 속성 수정
     *
     * @param property
     * @return
     */
    public DcProperty updateProperty(DcProperty property) {
        // 속성 타입이 콤보박스나, HTML이 아니면 attrbtTypeSel값 제거.
        if (!property.getAttrbtType().equals("SEL") && !property.getAttrbtType().equals("HTML")) {
            property.setAttrbtTypeSel(null);
        }
        return propertyRepository.save(property);
    }

    /**
     * 네비게이션 속성 조회
     *
     * @param attrbtNo
     * @return
     */
    public DcProperty getProperty(Integer attrbtNo) {
        return propertyRepository.findById(attrbtNo).orElse(null);
    }

    /**
     * DcProperty 조회 by navi_id, attrbt_cd
     * @param naviId
     * @param attrbtCd
     * @return
     */
    public DcProperty getPropertyByNaviIdAndAttrbtCd(String naviId, String attrbtCd) {
        Map<String, Object> params = new HashMap<>();
        params.put("naviId", naviId);
        params.put("attrbtCd", attrbtCd);
        return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.selectPropertyByNaviIdAndAttrbtCd", params);
    }

    /**
     * 네비게이션 단일 삭제
     *
     * @param navigation
     * @param userId
     */
    public void deleteNavigation(DcNavigation navigation, String userId) {
        DcNavigation dcNavigation = navigationRepository.findOne(Example.of(navigation)).orElse(null);
        if (dcNavigation != null) {
            // navigationRepository.deleteById(navigation.getNaviNo());
            dcNavigation.setDltYn("Y");
            dcNavigation.setDltId(userId);
            dcNavigation.setDltDt(LocalDateTime.now());
            navigationRepository.save(dcNavigation);

            List<DcProperty> propertyList = getDocumentNavigationPropertyList(dcNavigation.getNaviId());
            List<Integer> attrbtNoList = propertyList.stream().map(DcProperty::getAttrbtNo).collect(Collectors.toList());

            deleteProperty(attrbtNoList);
        } else {
            throw new GaiaBizException(ErrorType.NO_DATA, "No Navigation data.");
        }
    }

    /**
     * 네비게이션 삭제 (하위 문서 포함)
     *
     * @param naviId
     * @param usrId
     */
    @Transactional
    public void deleteNavigationAndSubDocumentList(String naviId, String usrId) {

        DcNavigation dcNavigation = getNavigation(naviId);
        if (dcNavigation != null) {
            MybatisInput input = MybatisInput.of().add("usrId", usrId)
                    .add("naviId", naviId);
            deleteNavigation(dcNavigation, usrId);
            mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.deleteNaviSubDocList", input);

            List<DcProperty> propertyList = getDocumentNavigationPropertyList(dcNavigation.getNaviId());
            List<Integer> attrbtNoList = propertyList.stream().map(DcProperty::getAttrbtNo).collect(Collectors.toList());

            deleteProperty(attrbtNoList);
        } else {
            throw new GaiaBizException(ErrorType.NO_DATA);
        }
    }

    /**
     * 경로 위로 이동
     *
     * @param moveUpNaviForm
     * @return
     */
    @Transactional
    public boolean upNaviDsplyOrdr(DcNavigation moveUpNaviForm) {
        MybatisInput input = MybatisInput.of().add("dsplyOrdr", moveUpNaviForm.getDsplyOrdr())
                .add("naviLevel", moveUpNaviForm.getNaviLevel())
                .add("upNaviId", moveUpNaviForm.getUpNaviId());

        DcNavigation upNavi = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.moveUpNavi", input);

        if (upNavi == null) {
            return false;
        }

        short tmp;
        tmp = moveUpNaviForm.getDsplyOrdr();
        moveUpNaviForm.setDsplyOrdr(upNavi.getDsplyOrdr());
        upNavi.setDsplyOrdr(tmp);

        mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.updateNaviDsplyOrdr", moveUpNaviForm);
        mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.updateNaviDsplyOrdr", upNavi);

        return true;
    }

    /**
     * 경로 아래로 이동
     *
     * @param moveDownNaviForm
     * @return
     */
    @Transactional
    public boolean downNaviDsplyOrdr(DcNavigation moveDownNaviForm) {
        MybatisInput input = MybatisInput.of().add("dsplyOrdr", moveDownNaviForm.getDsplyOrdr())
                .add("naviLevel", moveDownNaviForm.getNaviLevel())
                .add("upNaviId", moveDownNaviForm.getUpNaviId());

        DcNavigation downNavi = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.moveDownNavi", input);

        if (downNavi == null) {
            return false;
        }

        short tmp;
        tmp = moveDownNaviForm.getDsplyOrdr();
        moveDownNaviForm.setDsplyOrdr(downNavi.getDsplyOrdr());
        downNavi.setDsplyOrdr(tmp);

        mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.updateNaviDsplyOrdr", moveDownNaviForm);
        mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.updateNaviDsplyOrdr", downNavi);

        return true;
    }

    /**
     * 휴지통 문서 리스트 조회
     *
     * @param cntrctNo
     * @param user
     * @return
     */
    public List<Map<String, ?>> getTrashDocumentList(String cntrctNo, String columnNm, String searchText, UserAuth user,
                                                     String documentType) {
        String topNaviId = documentType + "_" + cntrctNo;
        boolean isAdmin = user.isAdmin();

        MybatisInput input = MybatisInput.of().add("topNaviId", topNaviId)
                .add("usrId", user.getUsrId());

        // 검색 조건이 있을 경우
        if (columnNm != null && searchText != null) {
            input.add("columnNm", columnNm);
            input.add("searchText", searchText);
        }

        if (isAdmin) {
            return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.getTrashDocListAdmin", input);
        } else {
            return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.getTrashDocList", input);
        }

    }

    /**
     * 휴지통 문서 복원
     *
     * @param trashDocIdList
     * @param user
     * @return
     */
    @Transactional
    public Integer recoverTrashDocList(List<String> trashDocIdList, UserAuth user) {
        Integer totalUpdate = 0;
        MybatisInput input = MybatisInput.of().add("usrId", user.getUsrId());
        for (String docId : trashDocIdList) {
            input.add("docId", docId);

            Integer docCnt = mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.recoverDocument", input);
            Integer naviCnt = mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.recoverNavigation", input);

            totalUpdate += docCnt + naviCnt;
        }

        return totalUpdate;
    }

    /**
     * 휴지통 문서 영구 삭제
     *
     * @param trashDocIdList
     * @param user
     */
    @Transactional
    public void removeTrashDocumentList(List<String> trashDocIdList, UserAuth user) {

        MybatisInput input = MybatisInput.of().add("usrId", user.getUsrId());

        for (String docId : trashDocIdList) {
            DcStorageMain trashDoc = storageMainRepository.findByDocIdAndDltYn(docId, "Y").orElse(null);

            if (trashDoc == null) {
                throw new GaiaBizException(ErrorType.NOT_FOUND, "Not found Document.");
            }

            // 영구 삭제 상태로 변경.
            input.add("docId", docId);

            mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.removeTrashDocument", input);
        }

    }

    /**
     * 휴지통 문서 비우기(전체 삭제)
     *
     * @param trashDocIdList
     * @param user
     */
    @Transactional
    public void removeAllTrashDocumentList(List<String> trashDocIdList, UserAuth user) {
        // 휴지통 문서 리스트 조회
        List<DcStorageMain> trashDocList = storageMainRepository.findAllByDocIdInAndDltYn(trashDocIdList, "Y");

        // 휴지통 문서 리스트와 문서 id 리스트 크기 비교로 휴지통 문서 대상 확인.
        if (trashDocList.size() != trashDocIdList.size()) {
            throw new GaiaBizException(ErrorType.NOT_FOUND, "One or more documents not found.");
        }

        // 영구 삭제 상태로 변경.
        MybatisInput input = MybatisInput.of().add("usrId", user.getUsrId())
                .add("trashDocIdList", trashDocIdList);

        mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.removeAllTrashDocument", input);
    }

    /**
     * 착공계 통합문서 전체 압축파일 다운로드
     *
     * @param rootNaviId
     * @return
     * @throws IOException
     */
    @Transactional
    public Path constructionDocumentTotalDownload(String rootNaviId) throws IOException {
        // 임시 디렉토리 생성
        Path tempDir = Files.createTempDirectory("navigation_download");

        // 1. 문서 네비게이션 계층 조회
        List<DcNavigation> navHierarchy = mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.getNavigationTree", rootNaviId);

        if (navHierarchy == null || navHierarchy.isEmpty()) {
            throw new GaiaBizException(ErrorType.NO_DATA, "Not found Navigation Data.");
        }

        // 네비게이션 ID 리스트 추출
        List<String> naviIdList = navHierarchy.stream().map(DcNavigation::getNaviId).collect(Collectors.toList());

        // 네비게이션 하위 모든 문서를 가져오기
        List<DcStorageMain> storageHierarchy = mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.getAllDocuments", naviIdList);

        // 3. 문서 데이터를 네비게이션 ID 기준으로 맵핑
        Map<String, List<DcStorageMain>> documentMap = storageHierarchy.stream()
                .collect(Collectors.groupingBy(DcStorageMain::getNaviId));

        Map<Integer, Path> navigationPaths = new HashMap<>(); // navi_no -> 생성된 Path 매핑
        Map<Integer, Path> documentPaths = new HashMap<>(); // navi_no -> 생성된 Path 매핑

        // 문서 네비게이션 폴더 계층 생성
        for (DcNavigation node : navHierarchy) {
            // 부모 폴더 찾기
            Path parentFolder = (node.getUpNaviNo() == 0)
                    ? tempDir // 최상위 폴더
                    : navigationPaths.get(node.getUpNaviNo()); // 부모 폴더 찾기

            // 올바른 폴더 경로 생성 (부모 폴더 아래에 위치해야 함)
            String naviPathNm = (node.getUpNaviNo() == 0) ? node.getNaviNm()
                    : node.getDsplyOrdr() + "." + node.getNaviNm();
            Path folderPath = parentFolder.resolve(naviPathNm);
            if (!folderPath.startsWith(tempDir)) {
                throw new SecurityException("잘못된 경로 접근 시도 감지됨.");
            }

            Files.createDirectories(folderPath);
            navigationPaths.put(node.getNaviNo(), folderPath); // 현재 폴더 경로 저장
        }

        for (DcNavigation node : navHierarchy) {
            Path naviParentFolder = navigationPaths.get(node.getNaviNo()); // 해당 네비게이션 폴더 위치
            List<DcStorageMain> documents = documentMap.get(node.getNaviId());

            if (documents != null) {
                for (DcStorageMain storageNode : documents) {
                    // 부모 폴더 찾기
                    Path parentFolder = (storageNode.getUpDocNo() == 0)
                            ? naviParentFolder // 최상위 폴더 (navi경로)
                            : documentPaths.get(storageNode.getUpDocNo()); // 부모 폴더 찾기

                    Path storagePath = parentFolder.resolve(storageNode.getDocNm());
                    if (!storagePath.startsWith(tempDir)) {
                        throw new SecurityException("잘못된 파일 경로 접근 시도 감지됨.");
                    }

                    if ("FOLDR".equals(storageNode.getDocType())) {
                        // 폴더라면 생성
                        Files.createDirectories(storagePath);
                        documentPaths.put(storageNode.getDocNo(), storagePath); // 현재 폴더 경로 저장
                    } else {
                        // 파일 다운로드 (경로 탐색 방지)
                        Path sourcePath = Paths.get(storageNode.getDocDiskPath(), storageNode.getDocDiskNm())
                                .normalize();
                        if (!sourcePath.startsWith(Paths.get(storageNode.getDocDiskPath()).normalize())) {
                            throw new SecurityException("파일 경로 접근 시도 감지됨.");
                        }

                        Files.copy(sourcePath, storagePath, StandardCopyOption.REPLACE_EXISTING);
                    }

                }
            }
        }

        // 3. 압축 파일 생성
        Path zipPath = Files.createTempFile("navigation", ".zip");
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipPath))) {
            Files.walk(tempDir).forEach(path -> {
                try {
                    String entryName = tempDir.relativize(path).toString();

                    // 빈 폴더 감지: 폴더인 경우 / 추가
                    if (Files.isDirectory(path)) {
                        if (Files.list(path).findAny().isEmpty()) { // 폴더가 비어있는 경우
                            entryName += "/"; // ZIP 내 폴더로 인식되도록 처리
                            ZipEntry zipEntry = new ZipEntry(entryName);
                            zos.putNextEntry(zipEntry);
                            zos.closeEntry();
                        }
                    } else {
                        addToZipFile(path, entryName, zos);
                    }

                } catch (IOException e) {
                    log.error("압축파일 생성 중 오류 발생 = {}" , e.getMessage());
                    throw new GaiaBizException(ErrorType.INTERNAL_SERVER_ERROR, "압축파일 생성 중 오류가 발생하였습니다.");
                }
            });
        }

        // 생성된 zipPath를 저장 (rootNaviId 기반)
        ZIP_FILE_MAP.put(rootNaviId, zipPath);

        // 5. 임시 파일 삭제 처리
        deleteDirectory(tempDir.toFile());

        return zipPath;

    }

    private void addToZipFile(Path filePath, String entryName, ZipOutputStream zos) throws IOException {
        byte[] buffer = new byte[8192]; // 8KB 버퍼 사용
        try (InputStream fis = Files.newInputStream(filePath)) {
            zos.putNextEntry(new ZipEntry(entryName));
            int len;
            while ((len = fis.read(buffer)) > 0) {
                zos.write(buffer, 0, len);
            }
            zos.closeEntry();
        }
    }

    // === 임시 파일 삭제 S===//
    private void deleteDirectory(File directory) throws IOException {
        if (directory.exists()) {
            deleteDirectoryRecursively(directory);
        }
    }

    private void deleteDirectoryRecursively(File file) throws IOException {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    deleteDirectoryRecursively(f);
                }
            }
        }
        Files.delete(file.toPath());
    }

    //=== 임시 파일 삭제 E===//

    public String createUniqueNaviName(MybatisInput input) {
        // 폴더명에서 최대 숫자 값을 조회
        Map<String, Object> result = mybatisSession
                .selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.selectExistsNaviName", input);

        Integer maxSuffix = (Integer) result.get("maxsuffix");
        String nameExists = (String) result.get("nameexists");

        String naviName = (String) input.get("naviNm");

        // 중복된 네비명이 없다면 원래 네비명을 반환하고, 있다면 숫자를 증가시켜서 반환
        if (nameExists != null && nameExists.equals("Y")) {
            return (maxSuffix == null) ? naviName + "(" + 1 + ")" : naviName + "(" + (maxSuffix + 1) + ")";
        } else {
            return naviName;
        }
    }

    public Path getZipPath(String rootNaviId) {
        return ZIP_FILE_MAP.get(rootNaviId);
    }

    public void removeZipPath(String rootNaviId) {
        ZIP_FILE_MAP.remove(rootNaviId);
    }

    /**
     * 문서 속성 html 폼 리스트 조회
     *
     * @return
     */
    public List<Map<String, Object>> getHtmlformList() {
        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.getHtmlformList");
    }

    // =================착공계 문서관리 관련========================

    /**
     * 착공계 문서 네비게이션 및 문서 생성
     */
    @Transactional
    public DcStorageMain createConstructNaviAndDoc(String pjtNo, String cntrctNo,
                                                   List<ConstructionBeginsDocDto> docDtoList) {
        DcStorageMain returnStorageMain = null;

        String naviDiv = "04"; // 문서 구분: 착공계(04)

        // 착공계 최상위 네비게이션 조회
        DcNavigation upNavigation = getNavigation(naviDiv + "_" + cntrctNo);

        if (docDtoList.isEmpty()) {
            throw new GaiaBizException(ErrorType.NOT_FOUND, "착공계 문서 정보가 존재하지 않습니다.");
        }

        for (ConstructionBeginsDocDto navigation : docDtoList) {
            // 네비게이션 생성
            String naviId = cntrctNo + "_" + navigation.getCbgnNo();
            String upNaviId = upNavigation.getNaviId();

            // 네비게이션이 존재하는 경우, 생성 하지 않음
            DcNavigation existingNavigation = navigationRepository.findByNaviIdAndDltYn(naviId, "N");
            if (existingNavigation != null) {
                log.info("NaviId: {} already exists.", naviId);
                // 네비게이션이 존재하는 경우, 상위 네비게이션 업데이트
                upNavigation = existingNavigation;
                continue;
            }

            // 실제 문서 데이터인 경우, dcStroageMain에 저장
            if ("Y".equals(navigation.getDocYn())) {
                String baseDocId = navigation.getCbgnNo() + "_" + navigation.getCbgnDocType() + "_"
                        + navigation.getCbgnDocForm() + "_" + cntrctNo;
                String docType = navigation.getCbgnDocType().substring(0, 1);

                // 문서 생성
                DcStorageMain saveStorageMain = createConstructDocument(cntrctNo, baseDocId, navigation.getCbgnNm(),
                        upNavigation, docType, navigation);

                // 문서 반환
                returnStorageMain = saveStorageMain;
                continue;
            }

            // 최상위 네비게이션인 경우
            if (navigation.getCbgnLevel() == 2) {
                upNaviId = naviDiv + "_" + cntrctNo;
            }

            // dcNavigation에 네비게이션 저장
            DcNavigation saveNavigation = new DcNavigation();
            saveNavigation.setCntrctNo(cntrctNo);
            saveNavigation.setPjtNo(pjtNo);
            saveNavigation.setNaviId(naviId);
            saveNavigation.setNaviNm(navigation.getCbgnNm());
            saveNavigation.setUpNaviNo(upNavigation.getNaviNo());
            saveNavigation.setUpNaviId(upNaviId);
            saveNavigation.setNaviDiv(naviDiv);
            saveNavigation.setNaviType(navigation.getNaviType());
            saveNavigation.setDsplyOrdr((short) navigation.getDsplyOrdr());
            saveNavigation.setNaviLevel((short) (navigation.getCbgnLevel() - 1));
            saveNavigation.setDltYn("N");
            saveNavigation.setNaviPath(navigation.getCbgnNm());
            saveNavigation.setRefSysKey(upNavigation.getRefSysKey());

            // 네비게이션 저장
            navigationRepository.save(saveNavigation);

            // 네비게이션의 속성 정의 등록 (착공계 관리에서 정의한 속성 기반으로)
            createDcPropertyToCbgnProperties(pjtNo, cntrctNo, navigation, naviId, saveNavigation);

            // 네비게이션 경로 생성 후 생성 경로의 상위 경로와 동일한 권한을 부여
            MybatisInput authInput = MybatisInput.of().add("id", saveNavigation.getNaviId())
                    .add("no", saveNavigation.getNaviNo())
                    .add("upId", saveNavigation.getUpNaviId())
                    .add("upNo", saveNavigation.getUpNaviNo())
                    .add("usrId", saveNavigation.getRgstrId());

            mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.insertAutoMyAuthority", authInput);

            upNavigation = saveNavigation; // 상위 네비게이션 업데이트
        }

        log.info("docDtoList: {}", docDtoList);

        return returnStorageMain;

    }

    private void createDcPropertyToCbgnProperties(String pjtNo, String cntrctNo, ConstructionBeginsDocDto navigation, String naviId, DcNavigation saveNavigation) {
        // 네비게이션에 해당하는 속성 정의 저장.
        MybatisInput propInput = MybatisInput.of()
                .add("naviId", naviId)
                .add("cbgnNo", navigation.getCbgnNo())
                .add("usrId", saveNavigation.getRgstrId());

        // 착공계 관리에 등록된 속성 정의 리스트 조회
        List<CbgnPropertyDto> cbgnPropertyList = mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.getCbgnPropertyList", propInput);

        if (cbgnPropertyList != null && !cbgnPropertyList.isEmpty()) {
            for (CbgnPropertyDto cbgnProperty : cbgnPropertyList) {
                // DcProperty가 존재하면 새로 생성하지 않음.
                MybatisInput checkInput = MybatisInput.of()
                        .add("naviId", saveNavigation.getNaviId())
                        .add("attrbtCd", cbgnProperty.getAttrbtCd());

                Integer exists = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.checkDcPropertyExists", checkInput);

                if (exists != null && exists > 0) {
                    // 이미 존재하면 skip
                    continue;
                }

                DcProperty property = new DcProperty();

                if("HTML".equals(cbgnProperty.getAttrbtType())){
                    // 1. cbgnHtml 양식을 dcHtml에 저장
                    MybatisInput htmlInput = MybatisInput.of()
                                    .add("formNo", Long.parseLong(cbgnProperty.getAttrbtTypeSel()))
                                    .add("cntrctNo", cntrctNo)
                                    .add("pjtNo", pjtNo);
                    mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.insertDcHtmlForm", htmlInput);

                    Long formNo = (Long) htmlInput.get("generatedFormNo");

                    // 2. property에는 Dc_html_form의 시퀀스(formNo) 사용
                    property.setAttrbtTypeSel(String.valueOf(formNo));
                }else{
                    // HTML이 아닌 경우 기존 값 사용
                    property.setAttrbtTypeSel(cbgnProperty.getAttrbtTypeSel());
                }

                property.setNaviNo(saveNavigation.getNaviNo());
                property.setNaviId(saveNavigation.getNaviId());
                property.setAttrbtCd(cbgnProperty.getAttrbtCd());
                property.setAttrbtCdType(cbgnProperty.getAttrbtCdType());
                property.setAttrbtType(cbgnProperty.getAttrbtType());
                property.setAttrbtNmEng(cbgnProperty.getAttrbtNmEng());
                property.setAttrbtNmKrn(cbgnProperty.getAttrbtNmKrn());
                property.setAttrbtDsplyOrder(cbgnProperty.getAttrbtDsplyOrder().shortValue());
                property.setAttrbtDsplyYn(cbgnProperty.getAttrbtDsplyYn());
                property.setAttrbtChgYn(cbgnProperty.getAttrbtChgYn());
                property.setDltYn("N");

                // 속성 저장
                createProperty(property);
            }
        }
    }

    /**
     * 착공계 문서 생성
     * docType
     * - C => .xlsx
     * - D => .pdf
     */
    private DcStorageMain createConstructDocument(String cntrctNo, String docId, String docNm, DcNavigation upNavigation, String docType, ConstructionBeginsDocDto cbgnDto) {
        // doc_disk_path 생성
        String fullPath = getUploadPathByWorkTypeForDocument(FileUploadType.DOCUMENT, platform, cntrctNo); // 전체 경로 생성
        String docDiskPath = Path.of(uploadPath, fullPath).toString(); // 실제 저장 경로
        String docExt = "C".equals(docType) ? ".xlsx" : ".pdf";
        String originalExt = cbgnDto.getOrgnlDocDiskNm().substring(cbgnDto.getOrgnlDocDiskNm().lastIndexOf("."));

        // doc_disk_nm 생성
        String fileNm = java.util.UUID.randomUUID().toString(); // UUID로 파일명 생성
        String originalDocDiskNm = fileNm + originalExt; // 원본물리파일명을 생성되는 문서물리파일명과 동일하게 설정.
        String docDiskNm = fileNm + docExt;

        if(!"C".equals(docType)){
            // 착공계 관리 원본 파일(.hwpx) 리소스 저장
            // 원본 파일 경로
            Path sourcePath = Path.of(cbgnDto.getOrgnlDocDiskPath(), cbgnDto.getOrgnlDocDiskNm());
            // 복사할 대상 경로 (파일명 변경 포함)
            Path targetPath = Path.of(docDiskPath, originalDocDiskNm);

            // 파일 복사
            try {
                // 디렉토리 생성 (존재하지 않으면 생성, 이미 있으면 무시)
                Path parent = targetPath.toAbsolutePath().getParent();
                if(parent != null){
                    Files.createDirectories(parent);
                }

                Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING); // 파일이 존재하면 덮어씀.
            } catch (IOException e) {
                log.debug("파일 복사 실패!! = {}", e.getMessage());
                throw new GaiaBizException(ErrorType.INTERNAL_SERVER_ERROR, "파일 복사에 실패하였습니다.");
            }
        }

        // 문서명에서 기존 확장자를 자르고 pdf 확장자로 이름변경(pdf)
        String docNmWithoutExt = docNm.substring(0, docNm.lastIndexOf('.'));
        docNm = docNmWithoutExt + docExt;

        DcStorageMain saveStorageMain = new DcStorageMain();
        saveStorageMain.setDocId(docId);
        saveStorageMain.setNaviNo(upNavigation.getNaviNo());
        saveStorageMain.setNaviId(upNavigation.getNaviId());
        saveStorageMain.setDocNm(docNm);
        saveStorageMain.setUpDocId("#");
        saveStorageMain.setUpDocNo(0);
        saveStorageMain.setDocPath(docNm);
        saveStorageMain.setDocDiskNm(docDiskNm);
        saveStorageMain.setDocDiskPath(docDiskPath);
        saveStorageMain.setDocType("FILE");
        saveStorageMain.setDltYn("N");
        saveStorageMain.setCbgnKey(cbgnDto.getCbgnNo()); // 착공계 관리 cbgnNo 매핑.

        // DcStorageMain에 문서 저장
        storageMainRepository.save(saveStorageMain);

        // 상위 경로의 권한으로 문서 권한 생성.
        MybatisInput input = MybatisInput.of().add("id", saveStorageMain.getDocId())
                .add("no", saveStorageMain.getDocNo())
                .add("usrId", saveStorageMain.getRgstrId());

        // 상위 폴더가 없을 경우, 네비게이션 Id, no
        if ("#".equals(saveStorageMain.getUpDocId())) {
            input.add("upId", saveStorageMain.getNaviId())
                    .add("upNo", saveStorageMain.getNaviNo());
        } else { // 있을 경우, 상위 문서의 docId, docNo
            input.add("upId", saveStorageMain.getUpDocId())
                    .add("upNo", saveStorageMain.getUpDocNo());
        }

        mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.insertAutoMyAuthority", input);

        return saveStorageMain;
    }

    /**
     * 착공계 관리에서 등록했던 문서 양식 파일을 삭제. (기본 착공계 문서 삭제)
     *
     * @param docIdList 문서 id 리스트
     * @param user 사용자 정보
     * @return 삭제 성공에 대한 여부
     */
    public boolean deleteConstructDocument(List<String> docIdList, UserAuth user) {
        int resultCount = 0;

        for (String docId : docIdList) {
            Map<String, Object> input = new HashMap<>();
            input.put("docId", docId);
            input.put("userId", user.getUsrId());

            // 엔티티 조회
            Map<String, Object> document = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.selectOneDocument", input);

            log.info("문서 객체={}", document);

            // 첨부파일 삭제
            mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.deleteAttachments", input);

            // 속성 데이터 삭제
            mybatisSession.delete("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.deletePropertyData", input);

            // 문서 메인 삭제
            int updated = mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.deleteConstructDocument", input);
            resultCount += updated;

            // 착공계 문서 네비게이션 삭제
            String naviId = document.get("navi_id").toString();
            deleteConstructNavigationRecursive(naviId, user.getUsrId());

            // 1. 해당 네비게이션에 문서가 존재하는지 체크.

            // 1-1. 있으면 네비게이션 삭제 X

            // 1-2. 없으면 해당 네비게이션과 속성을 삭제하고 upNaviId 저장.

            // 2. upNaviId가 공백이 아니면 upNaviId를 가지고 형제 네비게이션이 존재하는지 체크.

            // 2-1. 형제 네비게이션이 존재하면 상위 네비게이션 삭제 X

            // 2-2. 없으면, 다시 1번 수행.

//            // 문서 속성 삭제
//            mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.deleteProperty", input);
//
//            // 문서 네비게이션 삭제
//            mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.deleteNavigation", input);
        }

        return true;
    }

    /**
     * 착공계 문서 네비게이션 삭제
     * @param naviId
     * @param userId
     */
    private void deleteConstructNavigationRecursive(String naviId, String userId) {
        String currentNaviId = naviId;

        while (currentNaviId != null && !currentNaviId.isEmpty()) {
            Map<String, Object> param = Map.of("naviId", currentNaviId);

            // 1. 현재 naviId에 속한 문서가 있는지 확인
            int docCount = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.countDocumentsByNaviId", param);
            if (docCount > 0) {
                break; // 문서가 존재하면 삭제 중단
            }

            // 2. 현재 네비게이션 및 속성 정보 논리 삭제 처리
            Map<String, Object> input = new HashMap<>();
            input.put("naviId", currentNaviId);
            input.put("userId", userId);
            mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.deletePropertyByNaviId", input);
            mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.deleteNavigationByNaviId", input);

            // 3. 상위 네비게이션 ID 조회
            String upNaviId = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.selectUpNaviIdByNaviId", param);
            if (upNaviId == null || upNaviId.isEmpty()) {
                break;
            }

            // 4. 상위 네비게이션의 하위(형제) 노드 확인
            Map<String, Object> siblingParam = new HashMap<>();
            siblingParam.put("upNaviId", upNaviId);
            siblingParam.put("excludeNaviId", currentNaviId);
            int siblingCount = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.countSiblingsByUpNaviId", siblingParam);

            if (siblingCount > 0) {
                break; // 형제가 있다면 재귀 종료
            }

            // 다음 루프를 상위로
            currentNaviId = upNaviId;
        }
    }


    // 머지할 첨부 문서 조회
    public List<Map<String, Object>> selectMergeAttachmentList(String docId) {
        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.selectMergeAttachmentList", docId);
    }


    // ================= 친환경 문서관리 관련========================

    /**
     * 친환경 문서 네비게이션 생성
     */
    @Transactional
    public DcNavigation createEvrfrndNavi(String pjtNo,String usrId) {

        String naviDiv = "01";

        // 통합문서관리 최상위 네비게이션 조회
        DcNavigation upNavigation = getNavigation(naviDiv + "_" + pjtNo);

        // 프로젝트관리 네비게이션 생성
        String naviId = naviDiv + "_PM_" + pjtNo;
        String upNaviId = upNavigation.getNaviId();

        // 프로젝트관리 네비게이션이 존재하는 경우, 생성 하지 않음
        DcNavigation existingNavigation = navigationRepository.findByNaviIdAndDltYn(naviId, "N");

        if (existingNavigation != null) {
            log.info("NaviId: {} already exists.", naviId);
            // 네비게이션이 존재하는 경우
            upNavigation = existingNavigation;
        } else {
            // dcNavigation에 네비게이션 저장
            DcNavigation saveNavigation = new DcNavigation();
            saveNavigation.setCntrctNo(pjtNo);
            saveNavigation.setPjtNo(pjtNo);
            saveNavigation.setNaviId(naviId);
            saveNavigation.setNaviNm("프로젝트 관리");
            saveNavigation.setUpNaviNo(upNavigation.getNaviNo());
            saveNavigation.setUpNaviId(upNaviId);
            saveNavigation.setNaviDiv(naviDiv);
            saveNavigation.setNaviType("FOLDR");
            saveNavigation.setDsplyOrdr(navigationRepository.maxMenuDsplyOrdrByUpNaviId(upNaviId) != null
                    ? navigationRepository.maxMenuDsplyOrdrByUpNaviId(upNaviId)
                    : (short) 0);
            saveNavigation.setNaviLevel((short) (upNavigation.getNaviLevel() + 1));
            saveNavigation.setDltYn("N");
            saveNavigation.setNaviPath("프로젝트 관리");
            if(usrId != null && !usrId.isEmpty()) {
                saveNavigation.setRgstrId(usrId);
                saveNavigation.setChgId(usrId);
            }
            saveNavigation.setNaviFolderType("0");
            saveNavigation.setNaviKey(String.format("%s::%s", pjtNo, pjtNo));

            Integer refSysKey = documentSystemKeyMap.get(platform);
            saveNavigation.setRefSysKey(refSysKey);

            // 네비게이션 저장
            navigationRepository.save(saveNavigation);

            // 네비게이션 경로 생성 후 생성 경로의 상위 경로와 동일한 권한을 부여
//            MybatisInput authInput = MybatisInput.of().add("id", saveNavigation.getNaviId())
//                    .add("no", saveNavigation.getNaviNo())
//                    .add("upId", saveNavigation.getUpNaviId())
//                    .add("upNo", saveNavigation.getUpNaviNo())
//                    .add("usrId", saveNavigation.getRgstrId());
//
//            mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.insertAutoMyAuthority", authInput);

            upNavigation = saveNavigation; // 상위 네비게이션 업데이트
        }

        // 프로젝트관리 > 친환경 네비게이션 생성
        naviId = naviDiv + "_ENV_" + pjtNo;
        upNaviId = upNavigation.getNaviId();

        // 친환경 네비게이션이 존재하는 경우, 생성 하지 않음
        existingNavigation = navigationRepository.findByNaviIdAndDltYn(naviId, "N");

        if (existingNavigation != null) {
            log.info("NaviId: {} already exists.", naviId);
            // 네비게이션이 존재하는 경우
            upNavigation = existingNavigation;
            return existingNavigation;
        } else {
            // dcNavigation에 네비게이션 저장
            DcNavigation saveNavigation = new DcNavigation();
            saveNavigation.setCntrctNo(pjtNo);
            saveNavigation.setPjtNo(pjtNo);
            saveNavigation.setNaviId(naviId);
            saveNavigation.setNaviNm("친환경");
            saveNavigation.setUpNaviNo(upNavigation.getNaviNo());
            saveNavigation.setUpNaviId(upNaviId);
            saveNavigation.setNaviDiv(naviDiv);
            saveNavigation.setNaviType("FOLDR");
            saveNavigation.setDsplyOrdr(navigationRepository.maxMenuDsplyOrdrByUpNaviId(upNaviId) != null
                    ? navigationRepository.maxMenuDsplyOrdrByUpNaviId(upNaviId)
                    : (short) 0);
            saveNavigation.setNaviLevel((short) (upNavigation.getNaviLevel() + 1));
            saveNavigation.setDltYn("N");
            saveNavigation.setNaviPath("친환경");
            if(usrId != null && !usrId.isEmpty()) {
                saveNavigation.setRgstrId(usrId);
                saveNavigation.setChgId(usrId);
            }
            saveNavigation.setNaviFolderType("0");
            saveNavigation.setNaviKey(String.format("%s::%s", pjtNo, pjtNo));

            Integer refSysKey = documentSystemKeyMap.get(platform);
            saveNavigation.setRefSysKey(refSysKey);

            // 네비게이션 저장
            navigationRepository.save(saveNavigation);

            // 네비게이션 경로 생성 후 생성 경로의 상위 경로와 동일한 권한을 부여
//            MybatisInput authInput = MybatisInput.of().add("id", saveNavigation.getNaviId())
//                    .add("no", saveNavigation.getNaviNo())
//                    .add("upId", saveNavigation.getUpNaviId())
//                    .add("upNo", saveNavigation.getUpNaviNo())
//                    .add("usrId", saveNavigation.getRgstrId());
//
//            mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.insertAutoMyAuthority", authInput);
            return saveNavigation;
        }

    }

    /**
     * 친환경 문서를 통합문서관리 > 프로젝트관리> 친환경에 INSERT
     */
    @Transactional
    public List<DcStorageMain> createEvrfrndDoc(String pjtNo, String pjtNm,
                                                Map<MultipartFile, String> fileDocIdMap, String upNaviId, Integer upNaviNo, String usrId) {

        List<DcStorageMain> savedStorageList = new ArrayList<>();

        for (Map.Entry<MultipartFile, String> entry : fileDocIdMap.entrySet()) {
            MultipartFile file = entry.getKey();
            String docId = entry.getValue();

            FileService.FileMeta fileMeta = fileService.save(getUploadPathByWorkTypeForDocument(FileUploadType.DOCUMENT,platform,pjtNo), file);

            DcStorageMain storage = new DcStorageMain();
            storage.setDocId(docId);
            storage.setNaviId(upNaviId);
            storage.setNaviNo(upNaviNo);
            storage.setDocNm(fileMeta.getOriginalFilename());
            storage.setUpDocId("#");
            storage.setUpDocNo(0);
            storage.setDocPath(pjtNm + " > 프로젝트 관리 > 친환경");
            storage.setDocDiskNm(fileMeta.getFileName());
            storage.setDocDiskPath(fileMeta.getDirPath());
            storage.setDocSize(fileMeta.getSize());
            storage.setDocType("FILE");
            storage.setDocTrashYn("N");
            storage.setDltYn("N");
            if(usrId != null && !usrId.isEmpty()) {
                storage.setRgstrId(usrId);
                storage.setChgId(usrId);
            }

            storageMainRepository.save(storage);
            savedStorageList.add(storage);
        }

        return savedStorageList;
    }

    @Transactional
    public void deleteEvrfrndDoc(String docId,String usrId) {
        MybatisInput input = new MybatisInput().add("docId",docId).add("usrId",usrId);
        mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.updateDeleteStorageMain", input);
    }

    @Transactional
    public List<DcStorageMain> changeDocIdToRealUuid(List<DcStorageMain> dcStorageMainList){
        Map<String,String> map = new HashMap<>();
        for(DcStorageMain dcStorageMain : dcStorageMainList){
            String fakeId = dcStorageMain.getDocId();
            String uuid = UUID.randomUUID().toString();

            map.put(fakeId,uuid);
            dcStorageMain.setDocId(uuid);
        }

        for(int i=0;i<dcStorageMainList.size();i++){
            DcStorageMain dcStorageMain = dcStorageMainList.get(i);
            String fakeUpId = dcStorageMain.getUpDocId();
            if(!(fakeUpId.length() > 20 || "#".equals(fakeUpId))){
                String realParentId = map.get(fakeUpId);
                dcStorageMain.setUpDocId(realParentId);
            }
        }
        return dcStorageMainList;
    }

    public boolean setUpDocNoOfList(List<DcStorageMain> dcStorageMainList) {
        return mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.updateUpDocNoOfList",dcStorageMainList) != 0;
    }

    /**
     * dc_search 데이터 전체 조회
     * @param params
     * @return
     */
    public List<DcSearch> getDcSearchAllList(Map<String, Object> params) {
        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.selectAllDcSearchData", params);
    }

    /**
     * dc_shared_history 전자결재로부터 응답받은 정보로 수정
     * @param requestParams
     * @return
     */
    @Transactional
    public int updateDocSharedHistory(Map<String, String> requestParams) {
        return mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.updateDocSharedHistory",requestParams);
    }

    @Transactional
    public HashMap<String,Object> createDocSharedHistory(Map<String,Object> requestParams) {
        List<Map<String,Object>> files = objectMapper.convertValue(requestParams.get("files"), new TypeReference<List<Map<String, Object>>>() {});

        String uuid = UUID.randomUUID().toString();

        // null/중복 방지도 가능
        final String docIds = files.stream()
                .map(m -> (String) m.get("docId"))
                .filter(Objects::nonNull)
                .collect(Collectors.joining(","));

        String shareType = objectMapper.convertValue(requestParams.get("shareType"),String.class);
        String shareStatus = "1";
        String shareReqData = "";
        try {
            shareReqData = objectMapper.writeValueAsString(requestParams);
        }
        catch (JsonProcessingException e) {
            log.error("JSON serialize failed={}", e.getMessage());
            throw new GaiaBizException(ErrorType.ETC,"Logical Issue");
        }

        String rgstrId = objectMapper.convertValue(requestParams.get("rgstrId"),String.class);

        HashMap<String,Object> mybatisParam = new HashMap<>();
        mybatisParam.put("uuid",uuid);
        mybatisParam.put("docIds",docIds);
        mybatisParam.put("shareType",shareType);
        mybatisParam.put("shareStatus",shareStatus);
        mybatisParam.put("shareReqData",shareReqData);
        mybatisParam.put("shareUsrId",rgstrId);

        List<DcStorageMain> sharedDcStorageMainList = null;
        if(mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.createDocSharedHistory",mybatisParam) == 1){
            sharedDcStorageMainList = new ArrayList<>();
            for(Map<String,Object> file : files){
                sharedDcStorageMainList.add(getDcStorageMain((String)file.get("docId")));
            }
        }
        Map<String,Object> apForm = objectMapper.convertValue(requestParams.get("apForm"), new TypeReference<Map<String, Object>>() {});

        HashMap<String,Object> result = new HashMap<>();
        result.put("sharedDcStorageMainList",sharedDcStorageMainList);
        result.put("uuid",uuid);
        result.put("frmId",apForm.get("frmId"));

        return result;
    }

    /**
     * 승인 완료된 결재 문서 생성
     * @param requestParams
     */
    @Transactional
    public void createApprovalDocument(Map<String, Object> requestParams) {

        Map<String, Object> apDoc = objectMapper.convertValue(requestParams.get("apDoc"), new TypeReference<Map<String, Object>>() {});

        String naviId = (String) apDoc.get("naviId");
        String docNm = (String)apDoc.get("apDocTitle");
        String cntrctNo = (String)apDoc.get("cntrctNo");
        String pjtNo = (String)apDoc.get("pjtNo");
        String apDocId = (String)apDoc.get("apDocId");
        String usrId = (String)requestParams.get("usrId");

        // 1. 문서 네비게이션 조회
        DcNavigation apNavi = this.getNavigation(naviId);

        if(apNavi == null){
            log.debug("naviId = {}", naviId);
            throw new GaiaBizException(ErrorType.NOT_FOUND, "네비게이션이 존재하지 않습니다.");
        }

        // 2. 문서 생성
        DcStorageMain saveApDoc = new DcStorageMain();
        saveApDoc.setDocId(apDocId);
        saveApDoc.setDocNm(docNm);
        saveApDoc.setNaviId(naviId);
        saveApDoc.setNaviNo(apNavi.getNaviNo());
        saveApDoc.setUpDocId("#");
        saveApDoc.setUpDocNo(0);
        saveApDoc.setDocPath(apNavi.getNaviNm());
        saveApDoc.setDltYn("N");
        saveApDoc.setRgstrId(usrId);
        saveApDoc.setChgId(usrId);

        // 문서 네비게이션 type에 따라 설정 (FILE / ITEM)
        if("FOLDR".equals(apNavi.getNaviType())){
            saveApDoc.setDocType("FILE");
        }else if("ITEM".equals(apNavi.getNaviType())){
            saveApDoc.setDocType("ITEM");
        }

        DcStorageMain savedApDoc = this.createDcStorageMain(saveApDoc);

        // 3. 속성 데이터 생성 (결재 정보)
        String docId = savedApDoc.getDocId();
        Integer docNo = savedApDoc.getDocNo();
        String frmId = (String)apDoc.get("frmId");
        String to = (String)apDoc.get("recipientNm");
        String from = (String)apDoc.get("senderNm");
        String draftId = (String)requestParams.get("apUsrNm");
        String draftDt = formatDt((String)apDoc.get("apAppDt"));
        String apprerId = (String)requestParams.get("apCmpltUsrNms");
        String apprerDt = formatDt((String)apDoc.get("apCmpltDt"));

        String apLink = String.format(
                "/eapproval/approval/detail?type=closed&frmId=%s&apDocId=%s&pjtNo=%s&cntrctNo=%s&page=p",
                frmId, apDocId, pjtNo, cntrctNo);

        List<DcPropertyData> propertyDataList = new ArrayList<>();

        final String[] apAttrbtCdList = new String[]{"TO", "FROM", "from_dt", "ap_link", "draft_id", "draft_dt", "apprer_id", "apprer_dt"};

        Map<String, String> mapToAttrbtData = new HashMap<>();
        mapToAttrbtData.put("TO", to);
        mapToAttrbtData.put("FROM", from);
        mapToAttrbtData.put("from_dt", draftDt);
        mapToAttrbtData.put("ap_link", apLink);
        mapToAttrbtData.put("draft_id", draftId);
        mapToAttrbtData.put("draft_dt", draftDt);
        mapToAttrbtData.put("apprer_id", apprerId);
        mapToAttrbtData.put("apprer_dt", apprerDt);

        for (String attrCd : apAttrbtCdList) {
            String val = mapToAttrbtData.get(attrCd);
            if (val == null || val.isBlank()) continue; // 값 없으면 스킵(정책에 맞게 처리)
            DcPropertyData data = new DcPropertyData();
            data.setDocId(docId);
            data.setDocNo(docNo);
            data.setRgstrId(usrId);
            data.setChgId(usrId);
            data.setAttrbtCd(attrCd);
            data.setAttrbtCntnts(val);
            propertyDataList.add(data);
        }

        this.createPropertyDataList(propertyDataList);
    }

    // 공통 포맷터 (출력용)
    private static final DateTimeFormatter OUT_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static String formatDt(String raw) {
        if (raw == null || raw.isBlank()) return null;
        LocalDateTime dt = LocalDateTime.parse(raw); // "2025-09-10T18:51:18.323085" → LocalDateTime
        return dt.format(OUT_FMT);                   // → "2025-09-10 18:51:18"
    }

    public boolean deletePropertyDatasByDocId(String docId) {
        return mybatisSession.delete("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.deletePropertyData", docId) != 0;
    }

    /**
     * 현재 네비게이션을 기준으로 상위 네비게이션 목록 조회
     * @param naviId
     * @return List<DcNavigation>
     */
    public List<DcNavigation> getNavigationAncestorsById (String naviId){
        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.selectNavigationAncestorsById", naviId);
    }

    /**
     * [Dc_storage_main]외부에서 삭제 시도 실패 시, 삭제 여부 롤백 처리
     * @param docId
     */
    public void rollbackDeletedDocumentByDocId(String docId){
        mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.rollbackDeleteDocumentByDocId", docId);
    }

    /**
     * [Dc_attachments]외부에서 삭제 시도 실패 시, 삭제 여부 롤백 처리
     * @param docId
     */
    public void rollbackDeletedDcAttachmentByDocId(String docId){
        mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.document.document.rollbackDeleteDcAttachmentByDocId", docId);
    }

}