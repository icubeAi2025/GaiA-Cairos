package kr.co.ideait.platform.gaiacairos.comp.document;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import kr.co.ideait.platform.gaiacairos.comp.chaggonggye.helper.ChagGongGyeDocHelper;
import kr.co.ideait.platform.gaiacairos.comp.document.helper.DocumentSearcher;
import kr.co.ideait.platform.gaiacairos.comp.document.service.DocumentService;
import kr.co.ideait.platform.gaiacairos.comp.portal.service.PortalService;
import kr.co.ideait.platform.gaiacairos.comp.system.service.CommonCodeService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractComponent;
import kr.co.ideait.platform.gaiacairos.core.constant.CommonCodeConstants;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.*;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.document.DocumentDto;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.document.DocumentForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.document.DocumentForm.SetAuthority;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.document.ConstructionBeginsDocDto;
import kr.co.ideait.platform.gaiacairos.core.type.DocumentType;
import kr.co.ideait.platform.gaiacairos.core.type.FileUploadType;
import kr.co.ideait.platform.gaiacairos.core.type.PlatformType;
import kr.co.ideait.platform.gaiacairos.core.util.FileService;
import kr.co.ideait.platform.gaiacairos.core.util.restclient.DocumentServiceClient;
import kr.co.ideait.platform.gaiacairos.core.util.restclient.ICubeClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class DocumentComponent extends AbstractComponent {
    private final ICubeClient iCubeClient;

    @Autowired
    FileService fileService;

    @Autowired
    DocumentService documentService;

    @Autowired
    DocumentForm documentForm;

    @Autowired
    DocumentDto documentDto;

    @Autowired
    DocumentSearcher documentSearcher;

    @Autowired
    ChagGongGyeDocHelper chagGongGyeDocHelper;

    @Autowired
    private final CommonCodeService commonCodeService;

    @Autowired
    private DocumentServiceClient documentServiceClient;

    @Autowired
    PortalService portalService;

    public Map<String, Object> search(Map<String, Object> params) {
        String refSysKey = MapUtils.getString(params, "refSysKey");
        String keyword = MapUtils.getString(params, "keyword");

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("result", "success");

        if (!StringUtils.hasText(refSysKey)) {
            resultMap.put("result", "fail");
            resultMap.put("message", "시스템 키는 필수입니다.");
            return resultMap;
        }

//        if (StringUtils.hasText(naviKey)) {
//            resultMap.put("result", "fail");
//            resultMap.put("message", "네비게이션 키는 필수입니다.");
//            return resultMap;
//        }

        if (!StringUtils.hasText(keyword)) {
            resultMap.put("totalCnt", 0);
            resultMap.put("result", "fail");
            resultMap.put("message", "검색어를 입력해주세요.");
            return resultMap;
        }

        int pageIndex = 1;
        int pageSize = 30;

        Integer pageParam = (Integer) params.get("page");
        Integer perPageParam = (Integer) params.get("perPage");

        if (pageParam != null) {
            pageIndex = pageParam;
        }

        if (perPageParam != null) {
            pageSize = perPageParam;
        }

        Map<String, Object> searchResult = documentSearcher.search(params, pageIndex, pageSize);
        List<Object> contents = (List<Object>) searchResult.get("data");
        long totalCnt = (long) searchResult.get("totalCnt");
        Pageable pageable = (Pageable) searchResult.get("pageable");

        resultMap.put("searchResult", new PageImpl<>(contents, pageable, totalCnt));

        return resultMap;
    }

    // dc_search 테이블의 전체 데이터 조회
    public Map<String, Object> getDcSearchDataAll(Map<String, Object> params) throws Exception {
        if (!PlatformType.PGAIA.getName().equals(platform)) {
            throw new GaiaBizException(ErrorType.BAD_REQUEST, "허용되지 않은 플랫폼입니다.");
        }

        Map<String, Object> resultMap = new HashMap<>();

        List<DcSearch> dcSearchDataList = documentService.getDcSearchAllList(params);

        resultMap.put("items", dcSearchDataList);

        return resultMap;
    }


    @Transactional
    public Result setNaviAuthorityList(List<SetAuthority> setAuthorityList, String allYn, String authType) {
        if (setAuthorityList.size() > 0) {
            String upId = setAuthorityList.getFirst().getId(); // 변경되는 상위 id
            Integer upNo = Integer.valueOf(setAuthorityList.getFirst().getNo()); // 변경되는 상위 no

            for (DocumentForm.SetAuthority setAuthority : setAuthorityList) {
                if (setAuthority.getActionType().equals("ADD")) {
                    documentService.createAuthority(documentForm.toDcAuthority(setAuthority));
                } else if (setAuthority.getActionType().equals("UPDATE")) {
                    documentService.updateAuthority(documentForm.toDcAuthority(setAuthority));
                } else if (setAuthority.getActionType().equals("DEL")) {
                    documentService.deleteAuthority(documentForm.toDcAuthority(setAuthority));
                }
            }

            if("Y".equals(allYn)) {
                List<DcStorageMain> subDocList = new ArrayList<>();
                List<String> subDocIdList = new ArrayList<>();

                //1. 하위 문서 리스트 조회
                // 네비게이션인 경우,
                if("navi".equals(authType)) {
                    subDocList = documentService.getDcStorageMainListByNaviId(upId);

                }
                // 상위 문서(폴더)인 경우,
                else if("doc".equals(authType)) {
                    subDocList = documentService.getSubDocumentListByUpDocId(upId);
                }
                else{
                    throw new GaiaBizException(ErrorType.BAD_REQUEST, "잘못된 타입 값입니다.");
                }

                if(subDocList.size() > 0) {
                    subDocList.forEach(subDoc -> {subDocIdList.add(subDoc.getDocId());});
                }

                //2. 기존 문서 권한 삭제
                documentService.deleteSubDocAuthorityList(subDocIdList);

                //3. 상위 권한으로 새로 삽입.
                Map<String, Object> params = new HashMap<>();
                params.put("upId", upId);
                params.put("upNo", upNo);
                params.put("usrId", UserAuth.get(true).getUsrId());

                for(DcStorageMain subDoc : subDocList) {
                    params.put("id", subDoc.getDocId());
                    params.put("no", subDoc.getDocNo());
                    documentService.createSubDocAuthorityList(params);
                }
            }
        }
        else{
            return Result.nok(ErrorType.BAD_REQUEST, "변경할 권한 정보가 존재하지 않습니다.");
        }

        return Result.ok();
    }
    

    public Map<String, Object> sendDoc24(Map<String, Object> params) {
        List<String> docIds = (List<String>) params.get("docIds");

        if (docIds == null) {
            docIds = Lists.newArrayList();
        }
//        params.remove("docIds");

        // 요청 키
        // 필수: Y
        // 유형: ,
        // 설명: 요청에 대한 CAIROS 내 기본키 값(상태 저장시 API에 해당 키를 포함하여 전송)
        params.put("req_cd", UUID.randomUUID().toString());

        // 로그인 방법
        // 필수: Y
        // 유형: 영문코드
        // 설명: IDPW: 계정입력(디폴트) 이 외 옵션은 추후 협의
        params.put("log_vrf_ty", "IDPW");

        if (PlatformType.CAIROS.getName().equals(platform)) {
            if ("staging".equals(activeProfile)) {
                params.put("cllbck_url", "http://stg.idea-platform.net:8091/webApi/gdoc-send-result");
            } else if ("prod".equals(activeProfile)) {
                params.put("cllbck_url", "https://www.idea-platform.net:8102/webApi/gdoc-send-result");
            }
        }

        List<Map<String, Object>> files = new ArrayList<>();

        for (String docId : docIds) {
            DcStorageMain dcStorageMain = documentService.getDcStorageMain(docId);

            if (dcStorageMain != null) {
                String docNm = dcStorageMain.getDocNm();

                // 경로 탐색 및 특수문자 검증: "..", "/", "\" 등이 포함되면 예외를 발생.
                if (docNm.contains("..") || docNm.contains("/") || docNm.contains("\\")) {
                    throw new GaiaBizException(ErrorType.BAD_REQUEST, "Invalid file name.");
                }

                String filePath = dcStorageMain.getDocDiskPath();
                File file = new File(filePath);

                if (!file.exists()) {
                    throw new GaiaBizException(ErrorType.BAD_REQUEST, "Invalid file name.");
                }

                Map<String, Object> fileMap = new HashMap<>();
                fileMap.put("name", docNm);
                fileMap.put("resource",
                        fileService.getFile(dcStorageMain.getDocDiskPath(), dcStorageMain.getDocDiskNm()));

                files.add(fileMap);
            }
        }


        Map<String, Object> result = iCubeClient.ifCairosIcube001(MapUtils.getString(params, "usr_id"), params, files);

        return result;
    }

    @Transactional
    public Map<String,Object> createDocSharedHistory(DocumentDto.ApprovalRequestData params) {
        return documentServiceClient.createDocSharedHistory(params);
    }


    /**
     * 착공계 관리로부터 --> 착공계 문서 생성 (기본 데이터가 입력된 문서)
     */
    @Transactional
    public Map<String, Object> createConstructDocument(String pjtNo, String cntrctNo,
                                                       List<ConstructionBeginsDocDto> constructionDocDtoList) {
        // 착공계 문서 경로 및 문서 생성 (DcNavigation, DcStorageMain)
        DcStorageMain saveStorageMain = documentService.createConstructNaviAndDoc(pjtNo, cntrctNo,
                constructionDocDtoList);

        if (saveStorageMain == null) {
            throw new GaiaBizException(ErrorType.BAD_REQUEST, "착공계 문서 생성에 실패했습니다.");
        }

        if (saveStorageMain.getDocId() == null || saveStorageMain.getNaviId() == null) {
            throw new GaiaBizException(ErrorType.NOT_FOUND, "착공계 문서 생성에 필요한 타입, 폼이 없습니다.");
        }

        Map<String, Object> addData = new HashMap<>();

        // 착공계 문서 생성 후, 윈도우 서버에 전송
        Map<String, Object> result = chagGongGyeDocHelper.makeChagGongGyeDoc(addData, saveStorageMain.getNaviId(), saveStorageMain.getDocId());

        if("02".equals(result.get("resultCode"))){
           throw new GaiaBizException(ErrorType.NOT_FOUND, (String)result.get("resultMsg"));
        }
        else if("03".equals(result.get("resultCode"))){
            throw new GaiaBizException(ErrorType.INTERNAL_SERVER_ERROR, (String)result.get("resultMsg"));
        }

        return result;
    }

    /**
     * 착공계 관리로부터 --> 착공계 문서 생성 (엑셀 문서)
     */
    @Transactional
    public Map<String, Object> createConstructDocumentExcel(String pjtNo, String cntrctNo,
                                                       List<ConstructionBeginsDocDto> constructionDocDtoList, String cntrctNm) {
        Map<String, Object> result = null;

        // 착공계 문서용 네비게이션 및 저장 메타 생성 (폴더 + 파일 생성)
        DcStorageMain saveStorageMain = documentService.createConstructNaviAndDoc(pjtNo, cntrctNo,
                constructionDocDtoList);

        // 생성 실패 시 예외 처리
        if (saveStorageMain == null) {
            throw new GaiaBizException(ErrorType.BAD_REQUEST, "착공계 문서 생성에 실패했습니다.");
        }

        if (saveStorageMain.getDocId() == null || saveStorageMain.getNaviId() == null) {
            throw new GaiaBizException(ErrorType.NOT_FOUND, "착공계 문서 생성에 필요한 타입, 폼이 없습니다.");
        }

        String cbgnDocType = saveStorageMain.getDocId().split("_")[1]; // 문서 타입 코드 추출 (예: C0001은 계약내역서)

        result = chagGongGyeDocHelper.makeChagGongGyeDocToExcel(saveStorageMain, cbgnDocType, cntrctNo, cntrctNm);

        return result;
    }

    /**
     * 문서 속성 정보 데이터 수정
     *
     * @param propData
     * @return
     */
    @Transactional
    public Map<String, Object> updatePropertyData(DocumentForm.PropertyDataUpdate propData, List<MultipartFile> newFiles,
                                                  List<MultipartFile> newSubFiles,
                                                  List<String> subFileAttrbtCds,
                                                  List<Integer> removedFileNos,
                                                  List<Integer> removedSubFileNos,
                                                  UserAuth user) {
        Map<String, Object> result = documentService.updatePropertyData(propData, newFiles, newSubFiles, subFileAttrbtCds, removedFileNos,
                removedSubFileNos, user);

        if ("04".equals(propData.getDocumentType())) {
            String naviId = propData.getNaviId();
            String docId = propData.getDocId();
            String cntrctNo = propData.getNaviId().split("_")[0];

            log.info("naviId: {}", naviId);
            log.info("docId: {}", docId);
            log.info("cntrctNo: {}", cntrctNo);

            Map<String, Object> addData = new HashMap<>();

            result = chagGongGyeDocHelper.makeChagGongGyeDoc(addData, naviId, docId);

//            List<Map<String, Object>> mergeAttachment = documentService.selectMergeAttachmentList(docId);
//
//            // pdf 문서 생성에 성공하고, 첨부문서가 있는 경우, pdf 병합처리
//            if(!mergeAttachment.isEmpty()) {
//                // 이미지, pdf 파일 병합
//                chagGongGyeDocClient.mergeChagGongGyeDoc(docId, naviId);
//            }

        }
        return result;
    }

    /**
     * 착공계 관리에서 등록했던 문서 양식 파일을 삭제. (기본 착공계 문서 삭제)
     * @return 삭제 성공에 대한 여부
     */
    @Transactional
    public boolean deleteConstructDocument(List<String> docIdList, UserAuth user) {
        documentService.deleteConstructDocument(docIdList, user);

        return true;
    }
    @Transactional
    public boolean uploadFilesByFolder(List<MultipartFile> files, List<DcStorageMain> dcStorageMainList, CommonReqVo commonReqVo) {

        dcStorageMainList = documentService.changeDocIdToRealUuid(dcStorageMainList);

        List<String> availableExt = documentService.getAvailableFileExt();

        boolean hasValidFile = false;

        List<DcStorageMain> documentList = new ArrayList<>();

        int index = 0;
        if (files != null && !files.isEmpty()) {
            for(DcStorageMain dcStorageMain : dcStorageMainList){
                if("FILE".equals(dcStorageMain.getDocType())){
                    MultipartFile file = files.get(index++);

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

                    // 파일 크기 제한 (500MB)
                    if (file.getSize() > 500 * 1024 * 1024) {
                        log.warn("파일 크기가 너무 큽니다: {} bytes", file.getSize());
                        throw new GaiaBizException(ErrorType.INVAILD_INPUT_DATA,
                                "파일 사이즈가 너무 큽니다. : " + file.getSize() + " bytes");
                    }

                    hasValidFile = true;

                    // 파일 저장
                    FileService.FileMeta fileMeta = fileService.save(getUploadPathByWorkTypeForDocument(FileUploadType.DOCUMENT, platform, commonReqVo.getCntrctNo()), file);

                    dcStorageMain.setDocNm(originalFileName);
                    dcStorageMain.setDocDiskNm(fileMeta.getFileName());
                    dcStorageMain.setDocDiskPath(fileMeta.getDirPath());
                    dcStorageMain.setDocSize(fileMeta.getSize());

                    // 생성된 엔티티 리스트에 추가.
                    documentList.add(dcStorageMain);
                }
                else{
                    documentService.createDcStorageMain(dcStorageMain);
                }
            }

            if (hasValidFile) { // 유효한 파일이 있을 때만 실행
                documentService.createDocumentList(documentList);
            }

        }
        return documentService.setUpDocNoOfList(dcStorageMainList);
    }



    /**
     * 폴더 생성
     *
     * @param dcStorageMain 문서관리 테이블 엔티티
     * @return DcStorageMain 문서관리 테이블 엔티티
     * @throws
     */
    @Transactional
    public DcStorageMain createDcStorageMain(DcStorageMain dcStorageMain) {
        return documentService.createDcStorageMain(dcStorageMain);
    }

    public DcStorageMain getLastestDcStorageMainByFolderType(Map<String, String> params) {
        return documentService.getLastestDcStorageMainByFolderType(params);
    }

    /**
     * 업로드를 통한 문서 추가 (문서-폴더형)
     *
     * @param files
     */
    @Transactional
    public List<DcStorageMain> addDocumentListEx(DocumentForm.DocCreateEx doc, List<MultipartFile> files) {
        DcNavigation navigation = documentService.getNavigation(doc.getNaviId());

        if (navigation == null) {
            Map<String, Object> rootNavigation = documentService.getNavigationByRoot(doc.getCntrctNo(), doc.getNaviDiv());
            String naviId = MapUtils.getString(rootNavigation, "navi_id");

            DocumentForm.CheckHasFolderType inputParam = new DocumentForm.CheckHasFolderType();
            inputParam.setUpNaviId(naviId);
            inputParam.setNaviFolderType(doc.getNaviFolderType());

            String hasYn = documentService.checkHasNavigationType(inputParam);

            if ("N".equals(hasYn)) {
                doc.setNaviId(String.format("nav_%s_%s_%s", doc.getCntrctNo(), doc.getNaviFolderKind(), doc.getNaviDiv()));
                doc.setUpNaviNo(MapUtils.getInteger(rootNavigation, "navi_no"));
                doc.setUpNaviId(naviId);

                navigation = this.createNavigation(documentForm.toNavigationCreate(doc));
            } else {
                navigation = documentService.getNavigation(naviId);
            }
        }

        String docId = doc.getDocId();
        DcStorageMain dcStorageMain = documentForm.toDcStorageMain(doc);
        dcStorageMain.setNaviNo(navigation.getNaviNo());
        dcStorageMain.setNaviId(navigation.getNaviId());
        dcStorageMain.setDocType(DocumentType.FILE.toString());
        dcStorageMain.setDocPath(navigation.getNaviNm());
        dcStorageMain.setUpDocId("#");
        dcStorageMain.setUpDocNo(0);
        dcStorageMain.setDltYn("N");

        if(docId != null){
            //기존거 존재
            DcStorageMain existDcStorageMain = documentService.getDcStorageMainByDocId(docId);
            fileService.deleteFile(existDcStorageMain.getDocDiskPath(),existDcStorageMain.getDocDiskNm());
            if(documentService.deletePropertyDatasByDocId(docId)){
                documentService.deleteDocument(dcStorageMain.getDocId(), "SYSTEM");
            }
        }
        dcStorageMain.setDocId(java.util.UUID.randomUUID().toString());

        List<DcStorageMain> result = this.addDocumentList(dcStorageMain, files, doc.getCntrctNo(), doc.getRgstrId(), doc.getPropertyData());

        return result;
    }

    /**
     * 업로드를 통한 문서 추가 (문서-폴더형)
     *
     * @param files
     */
    @Transactional
    public List<DcStorageMain> addDocumentList(DcStorageMain docForm, List<MultipartFile> files, String cntrctNo) {
        return this.addDocumentList(docForm, files, cntrctNo, null, null);
    }

    @Transactional
    public List<DcStorageMain> addDocumentList(DcStorageMain docForm, List<MultipartFile> files, String cntrctNo, String usrId, List<DocumentForm.PropertyData> propertyDataList) {
        List<String> availableExt = documentService.getAvailableFileExt();

        boolean hasValidFile = false;

        List<DcStorageMain> documentList = new ArrayList<>();

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

                // 파일 크기 제한 (500MB)
                if (file.getSize() > 500 * 1024 * 1024) {
                    log.warn("파일 크기가 너무 큽니다: {} bytes", file.getSize());
                    throw new GaiaBizException(ErrorType.INVAILD_INPUT_DATA,
                            "파일 사이즈가 너무 큽니다. : " + file.getSize() + " bytes");
                }

                hasValidFile = true;

                // 파일 저장
                FileService.FileMeta fileMeta = fileService.save(getUploadPathByWorkTypeForDocument(FileUploadType.DOCUMENT, platform, cntrctNo), file);

                DcStorageMain saveDoc = new DcStorageMain();
                saveDoc.setDocId(java.util.UUID.randomUUID().toString());
                saveDoc.setDocType(docForm.getDocType());
                saveDoc.setDltYn(docForm.getDltYn());
                saveDoc.setDocPath(docForm.getDocPath());
                saveDoc.setNaviId(docForm.getNaviId());
                saveDoc.setNaviNo(docForm.getNaviNo());
                saveDoc.setUpDocId(docForm.getUpDocId());
                saveDoc.setUpDocNo(docForm.getUpDocNo());

                saveDoc.setDocNm(originalFileName);
                saveDoc.setDocDiskNm(fileMeta.getFileName());
                saveDoc.setDocDiskPath(fileMeta.getDirPath());
                saveDoc.setDocSize(fileMeta.getSize());

                // 생성된 엔티티 리스트에 추가.
                documentList.add(saveDoc);
            }

            if (hasValidFile) { // 유효한 파일이 있을 때만 실행
                documentService.createDocumentList(documentList, usrId, propertyDataList);
            }
        }

        return documentList;
    }

    /**
     * 문서 네비게이션 생성 (다중)
     * @param saveNaviList
     */
    @Transactional
    public void createNavigationList(List<DocumentForm.NavigationCreate> saveNaviList) {
        for (DocumentForm.NavigationCreate navigation : saveNaviList) {
            this.createNavigation(navigation);
        }
    }

    @Transactional
    public DcNavigation createNavigation(DocumentForm.NavigationCreate navigation) {
        DcNavigation dcNavigation = documentForm.toDcNavigation(navigation);
        dcNavigation.setDltYn("N");
        // RgstrId, ChgId가 null, "", " " 인 경우만 SYSTEM 세팅
        if (!StringUtils.hasText(dcNavigation.getRgstrId())) {
            dcNavigation.setRgstrId("SYSTEM");
        }
        if (!StringUtils.hasText(dcNavigation.getChgId())) {
            dcNavigation.setChgId("SYSTEM");
        }

        DcNavigation newNavigation = documentService.createNavigation(dcNavigation);

        if(newNavigation != null) {
            // 폴더 종류가 기본('0')이면 속성 초기화 스킵
            String folderType = dcNavigation.getNaviFolderType(); // 이미 toDcNavigation에서 세팅됐다고 가정
            if (folderType.isBlank() || "0".equals(folderType)) {
                return newNavigation;
            }

            // 속성 원천 확보: 요청에 실려왔는지 체크 → 없으면 공통코드로 생성 시도
            List<DocumentForm.PropertyCreate> props = navigation.getProperties();
            if (props == null || props.isEmpty()) {
                props = commonCodeService.createPropertyListForCommonCode(
                        CommonCodeConstants.DOCUMENT_NAVI_FOLDER_TYPE_GROUP_CODE,
                        folderType,
                        newNavigation.getNaviId()
                );
            }

            // 여전히 없으면 이후 로직 전부 스킵
            if (props == null || props.isEmpty()) {
                return newNavigation; // defaultProperties도 없고, 입력도 없으면 종료
            }

            // 변환 및 저장
            List<DcProperty> dcProperties = documentForm.toDcProperties(props);
            if (dcProperties == null || dcProperties.isEmpty()) {
                return newNavigation;
            }

            for (DcProperty dcProperty : dcProperties) {
                DcProperty existProp = documentService.getPropertyByNaviIdAndAttrbtCd(newNavigation.getNaviId(), dcProperty.getAttrbtCd());

                if (existProp == null) {
                    dcProperty.setNaviNo(newNavigation.getNaviNo());
                    documentService.createProperty(dcProperty);
                }

            }
        }
        else{
            throw new GaiaBizException(ErrorType.INTERNAL_SERVER_ERROR, "네비게이션이 생성되지 않았습니다.");
        }

        return newNavigation;
    }

    // 전자 결재 요청 완료 시, 공유 이력 수정.
    public int updateDocSharedHistory(Map<String, String> requestParams) {
        // 해당 공유 이력 수정
        int result = documentService.updateDocSharedHistory(requestParams);

        return result;
    }

    /**
     * 문서 네비게이션 목록 조회
     * @param isAdmin
     * @param naviId
     * @param loginId
     * @return
     */
    public List<Map<String, ?>> getDocumentNavigationList(boolean isAdmin, String naviId, String loginId) {
       return documentService.getDocumentNavigationList(isAdmin, naviId, loginId);
    }


    public HashMap<String, Object> getDocumentMainData(DocumentForm.@Valid NavigationList inputParam, CommonReqVo commonReqVo) {
        String naviId = null;
        String pjtNo = commonReqVo.getPjtNo();
        String cntrctNo = commonReqVo.getCntrctNo();
        String loginId = commonReqVo.getLoginId();

        if (inputParam.getCntrctNo() != null && !inputParam.getCntrctNo().equals("NON")) {
            if (inputParam.getDocumentType() != null) {
                // documentType이 "05" 라면 시스템관리 > 착공계 관리 에 있는 네비게이션
                if (inputParam.getDocumentType().equals("05")) {
                } else {
                    naviId = inputParam.getDocumentType() + "_" + inputParam.getCntrctNo();
                }
            }
        } else {
            naviId = inputParam.getDocumentType() + "_" + cntrctNo;
        }
        String cmnGrpCd = CommonCodeConstants.AKIND_CODE_GROUP_CODE;

        List<Map<String, Object>> contractList = new ArrayList<>();

//        String pjtType = "";
//        if("PGAIA".equals(platform.toUpperCase())) {
//            pjtType = PlatformType.GAIA.getName().toUpperCase();
//        }

        HashMap<String,Object> response = documentServiceClient.getDocumentMainData(cmnGrpCd, pjtNo,cntrctNo, platform.toUpperCase(), inputParam.getMenuId(), loginId, commonReqVo.getAdmin(), naviId);
        if (commonReqVo.getAdmin()) {
            if (platform.equals("cairos")) {
                // documentType이 "05" 라면 시스템관리 > 착공계 관리 에 있는 네비게이션 이므로
                // 계약 리스트가 아닌 착공계 문서 양식 리스트를 긁어가야 함
                if (inputParam.getDocumentType().equals("05")) {

                } else {
//                    contractList = contractService.selectContractList(user.getPjtNo());
                    MybatisInput input = MybatisInput.of().add("pjtNo", pjtNo)
                            .add("cmnGrpCd", CommonCodeConstants.WORKTYPE_CODE_GROUP_CODE);
                    contractList = portalService.selectContractList(input);
                }
            }

        } else {
//            naviAuthority = documentService.getDocumentNavigationListAuthority(cmnGrpCd, pjtNo,
//                    cntrctNo, platform.toUpperCase(), inputParam.getMenuId(), loginId);

        }

        HashMap<String,Object> result = new HashMap<>();


        result.put("contractList", contractList);

        if (response != null) {
            List<Map<String, ?>> naviAuthority = objectMapper.convertValue(response.get("naviAuthority"), new TypeReference<List<Map<String, ?>>>() {});
            List<String> availableFileExt = objectMapper.convertValue(response.get("availableFileExt"), new TypeReference<List<String>>() {});

            List<Map<String,Object>> navigationList = objectMapper.convertValue(response.get("navigationList"), new TypeReference<List<Map<String,Object>>>() {});

            result.put("navigationList",navigationList);
            result.put("naviAuthority", naviAuthority);
            result.put("availableFileExt", availableFileExt);
        }

        return result;
    }

    @Transactional
    public Result removeDocument(Map<String, Object> params) {
        List<String> docIds = (List<String>)params.get("docIds");
        String usrId = params.get("usrId").toString();

        docIds.forEach(docId -> {
            DcStorageMain dcStorageMain = documentService.getDcStorageMain(docId);

            if (dcStorageMain == null) {
                throw new GaiaBizException(ErrorType.NO_DATA, "데이터가 존재하지 않습니다.");
            }

            String docType = dcStorageMain.getDocType(); //FOLDR: 폴더형, FILE: 파일, ITEM: 아이템형

            if (documentService.deletePropertyDatasByDocId(docId)) {
                List<Integer> attachFileNos = documentService.getDcAttachmentList(docId).stream()
                        .map(dcAttachments -> dcAttachments.getFileNo())
                        .collect(Collectors.toList());

                documentService.deleteAttachmentList(attachFileNos, dcStorageMain.getDocNo());

                documentService.deleteDocument(docId, usrId);
            }
        });

        return Result.ok();
    }

    /**
     * 외부에서 승인 취소 시, 문서 삭제 롤백 처리.
     * @param params
     * @return
     */
    @Transactional
    public Result rollbackDocument(Map<String, Object> params) {
        List<String> docIds = (List<String>)params.get("docIds");

        docIds.forEach(docId -> {
//            List<Integer> attachFileNos = documentService.getDcAttachmentList(docId).stream()
//                    .map(dcAttachments -> dcAttachments.getFileNo())
//                    .collect(Collectors.toList());

            documentService.rollbackDeletedDcAttachmentByDocId(docId);

            documentService.rollbackDeletedDocumentByDocId(docId);
        });

        return Result.ok();
    }


    /**
     * 승인 완료된 결재 문서 생성
     * @param requestParams
     */
    @Transactional
    public void createApprovalDocument(Map<String, Object> requestParams) {
        documentService.createApprovalDocument(requestParams);

//        String naviId = (String)requestParams.get("naviId");
//        String docNm = (String)requestParams.get("apDocTitle");
//        String cntrctNo = (String)requestParams.get("cntrctNo");
//        String pjtNo = (String)requestParams.get("pjtNo");
//        String usrId = (String)requestParams.get("usrId");
//        String apDocId = (String)requestParams.get("apDocId");
//
//        // 1. 문서 네비게이션 조회
//        DcNavigation apNavi = documentService.getNavigation(naviId);
//
//        if(apNavi == null){
//            log.debug("naviId = {}", naviId);
//            throw new GaiaBizException(ErrorType.NOT_FOUND, "네비게이션이 존재하지 않습니다.");
//        }
//
//        // 2. 문서 생성
//        DcStorageMain saveApDoc = new DcStorageMain();
//        saveApDoc.setDocId(apDocId);
//        saveApDoc.setDocNm(docNm);
//        saveApDoc.setNaviId(naviId);
//        saveApDoc.setNaviNo(apNavi.getNaviNo());
//        saveApDoc.setUpDocId("#");
//        saveApDoc.setUpDocNo(0);
//        saveApDoc.setDocPath(apNavi.getNaviNm());
//        saveApDoc.setDltYn("N");
//        saveApDoc.setRgstrId(usrId);
//        saveApDoc.setChgId(usrId);
//
//        // 문서 네비게이션 type에 따라 설정 (FILE / ITEM)
//        if("FOLDR".equals(apNavi.getNaviType())){
//            saveApDoc.setDocType("FILE");
//        }else if("ITEM".equals(apNavi.getNaviType())){
//            saveApDoc.setDocType("ITEM");
//        }
//
//        DcStorageMain savedApDoc = documentService.createDcStorageMain(saveApDoc);
//
//        // 3. 속성 데이터 생성 (결재 정보)
//        String docId = savedApDoc.getDocId();
//        Integer docNo = savedApDoc.getDocNo();
//        String frmNo = String.valueOf(requestParams.get("frmNo"));
//        String draftId = (String)requestParams.get("apUsrId");
//        String draftDt = (String)requestParams.get("apAppDt");
//        String apprerId = (String)requestParams.get("apCmpltUsrId");
//        String apprerDt = (String)requestParams.get("apCmpltDt");
//
//        String apLink = String.format(
//                "/eapproval/approval/detail?type=closed&frmNo=%s&apDocId=%s&pjtNo=%s&cntrctNo=%s",
//                frmNo, apDocId, pjtNo, cntrctNo);
//
//        List<DcPropertyData> propertyDataList = new ArrayList<>();
//
//        final String[] apAttrbtCdList = new String[]{"ap_link", "draft_id", "draft_dt", "apprer_id", "apprer_dt"};
//
//        Map<String, String> mapToAttrbtData = new HashMap<>();
//        mapToAttrbtData.put("ap_link", apLink);
//        mapToAttrbtData.put("draft_id", draftId);
//        mapToAttrbtData.put("draft_dt", draftDt);
//        mapToAttrbtData.put("apprer_id", apprerId);
//        mapToAttrbtData.put("apprer_dt", apprerDt);
//
//        for (String attrCd : apAttrbtCdList) {
//            String val = mapToAttrbtData.get(attrCd);
//            if (val == null || val.isBlank()) continue; // 값 없으면 스킵(정책에 맞게 처리)
//            DcPropertyData data = new DcPropertyData();
//            data.setDocId(docId);
//            data.setDocNo(docNo);
//            data.setAttrbtCd(attrCd);
//            data.setAttrbtCntnts(val);
//            propertyDataList.add(data);
//        }
//
//        documentService.createPropertyDataList(propertyDataList);
    }


    public String checkFolderExist(@NotBlank String naviId, @NotBlank String upDocId, @NotBlank String docNm) {
        return documentServiceClient.checkFolderExist(naviId, upDocId, docNm);
    }

    public String checkHasNavigationType(DocumentForm.@Valid CheckHasFolderType inputParam) {
        return documentServiceClient.checkHasNavigationType(inputParam);
    }
}
