package kr.co.ideait.platform.gaiacairos.comp.construction.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Maps;

import kr.co.ideait.iframework.file.CustomMultipartFile;
import kr.co.ideait.platform.gaiacairos.comp.document.service.DocumentService;
import kr.co.ideait.platform.gaiacairos.comp.system.service.CommonCodeService;
import kr.co.ideait.platform.gaiacairos.comp.system.service.DocumentManageService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import kr.co.ideait.platform.gaiacairos.core.config.wrapper.MultipartFileWrapper;
import kr.co.ideait.platform.gaiacairos.core.constant.CommonCodeConstants;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.ApDoc;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwAttachments;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwMainmtrl;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwMainmtrlReqfrm;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwMainmtrlReqfrmPhoto;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DcStorageMain;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmComCode;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.CwAttachmentsRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.CwMainmtrlRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.CwMainmtrlReqfrmPhotoRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.CwMainmtrlReqfrmRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.construction.MainmtrlReqfrmMybatisParam;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.document.DocumentForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.document.CbgnPropertyDto;
import kr.co.ideait.platform.gaiacairos.core.type.FileUploadType;
import kr.co.ideait.platform.gaiacairos.core.util.PdfUtil;
import kr.co.ideait.platform.gaiacairos.core.util.restclient.DocumentServiceClient;
import kr.co.ideait.platform.gaiacairos.core.util.restclient.UbiReportClient;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MainmtrlReqfrmService extends AbstractGaiaCairosService {

    @Autowired
    CommonCodeService commonCodeService;

    @Autowired
    DocumentManageService documentmanageService;

    @Autowired
    DocumentService documentService;

    @Autowired
    CwMainmtrlReqfrmRepository mainmtrlReqfrmRepository;

    @Autowired
    CwMainmtrlReqfrmPhotoRepository photoRepository;

    @Autowired
    CwMainmtrlRepository mainmtrlRepository;

    @Autowired
    CwAttachmentsRepository cwAttachmentsRepository;

    @Autowired
    DocumentServiceClient documentServiceClient;

    @Autowired
    private UbiReportClient ubiReportClient;

    // 주요자재 검수요청서
    public List<MainmtrlReqfrmMybatisParam.MainmtrlReqfrmOutput> getMainmtrlReqfrmList(
            MainmtrlReqfrmMybatisParam.MainmtrlReqfrmInput input) {
        return mybatisSession
                .selectList(
                        "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.mainmtrlreqfrm.getMainmtrlReqfrmList",
                        input);
    }

    public MainmtrlReqfrmMybatisParam.MainmtrlReqfrmOutput getMainmtrlReqfrm(Map<String, Object> params) {
        return mybatisSession
                .selectOne(
                        "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.mainmtrlreqfrm.getMainmtrlReqfrm",
                        params);
    }

    public CwMainmtrlReqfrm getMainmtrlReqfrm(String cntrctNo, String reqfrmNo) {
        return mainmtrlReqfrmRepository.findByCntrctNoAndReqfrmNoAndDltYn(cntrctNo, reqfrmNo, "N");
    }

    public CwMainmtrlReqfrm createMainmtrlReqfrm(CwMainmtrlReqfrm mainmtrlReqfrm) {
        if (mainmtrlReqfrm.getReqfrmNo() == null || mainmtrlReqfrm.getReqfrmNo().isBlank()) {
            mainmtrlReqfrm.setReqfrmNo(UUID.randomUUID().toString());
        }
        return mainmtrlReqfrmRepository.save(mainmtrlReqfrm);
    }

    /**
     * 요청(퀵메뉴) 테이블에 데이터 저장
     */
    public void insertRequestItem(Map<String, Object> insertParam) {
        mybatisSession.insert(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.mainmtrlreqfrm.insertRequestItem",
                insertParam);
    }
    /**
     * 요청(퀵메뉴) 테이블에서 삭제
     */
    public void deleteReqItem(String reqfrmNo) {
        mybatisSession.delete(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.mainmtrlreqfrm.deleteReqItem",
                reqfrmNo);
    }
    /**
     * 요청(퀵메뉴) 테이블에서 논리삭제
     */
    public void updateDeleteReqItem(MybatisInput input) {
        mybatisSession.delete(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.mainmtrlreqfrm.updateDeleteReqItem",
                input);
    }

    public void updateMainmtrlList(MybatisInput input) {
        mybatisSession.update(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.mainmtrlreqfrm.updateMainmtrlList",
                input);
    }

    public List<MainmtrlReqfrmMybatisParam.MainmtrlReqfrmOutput> getSupervisionList(MybatisInput input) {
        return mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.mainmtrlreqfrm.getSupervisionList",
                input);
    }

    public void deleteMainmtrlReqfrm(MybatisInput input) {
        mybatisSession.update(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.mainmtrlreqfrm.deleteMainmtrlReqfrm",
                input);
    }

    // 자재
    public void createMainmtrl(CwMainmtrl mainmtrl) {
        mainmtrlRepository.save(mainmtrl);
    }

    public List<MainmtrlReqfrmMybatisParam.MainmtrlOutput> getMainmtrlList(
            MainmtrlReqfrmMybatisParam.MainmtrlReqfrmInput input) {
        return mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.mainmtrlreqfrm.getMainmtrlList", input);
    }

    public List<MainmtrlReqfrmMybatisParam.MainmtrlReqfrmOutput> getAddMainmtrlList(MybatisInput input) {
        return mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.mainmtrlreqfrm.getAddMainmtrlList",
                input);
    }

    public List<String> getGnrlexpnsCdList(MybatisInput input) {
        return mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.mainmtrlreqfrm.getGnrlexpnsCdList",
                input);
    }

    public void deleteMainmtrl(MybatisInput input) {
        mybatisSession.update(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.mainmtrlreqfrm.deleteMainmtrlList",
                input);
    }

    public List<CwMainmtrl> getMainmtrlList(String cntrctNo, String reqfrmNo) {
        return mainmtrlRepository.findByCntrctNoAndReqfrmNoAndDltYn(cntrctNo, reqfrmNo, "N");
    }

    // 사진
    public CwMainmtrlReqfrmPhoto getPhoto(String cntrctNo, String reqfrmNo, int phtSno) {
        return photoRepository.findByCntrctNoAndReqfrmNoAndPhtSnoAndDltYn(cntrctNo, reqfrmNo, phtSno, "N");
    }

    public List<CwMainmtrlReqfrmPhoto> getPhotoList(String cntrctNo, String reqfrmNo) {
        return photoRepository.findByCntrctNoAndReqfrmNoAndDltYn(cntrctNo, reqfrmNo, "N");
    }

    public void createPhoto(CwMainmtrlReqfrmPhoto photo) {
        photoRepository.save(photo);
    }

    public void deletePhoto(CwMainmtrlReqfrmPhoto mainmtrlReqfrmPhoto) {
        photoRepository.updateDelete(mainmtrlReqfrmPhoto);
    }

    // 첨부파일
    public CwAttachments getAttachments(int fileNo, int sno) {
        return cwAttachmentsRepository.findByFileNoAndSno(fileNo, sno);
    }

    public List<CwAttachments> getFileList(int fileNo) {
        return cwAttachmentsRepository.findByFileNoAndFileDivAndDltYn(fileNo, "F", "N");
    }

    public List<CwAttachments> getImgFileList(int fileNo) {
        return cwAttachmentsRepository.findByFileNoAndFileDivAndDltYn(fileNo, "I", "N");
    }

    public void deleteAttachment(int fileNo, int sno) {
        cwAttachmentsRepository.updateDelete(cwAttachmentsRepository.findByFileNoAndSno(fileNo, sno));
    }

    public int createCwAttachmentsList(List<CwAttachments> cwAttachmentsList) {
        Integer fileNo = generateFileNo(); // 가장 큰 fileNo 값을 기반으로 새 fileNo 생성
        int sno = 1; // sno는 1부터 시작

        for (CwAttachments cwAttachments : cwAttachmentsList) { // 파일 새로 추가
            if (cwAttachments.getFileNo() == null) { // 기존 fileNo 없을시
                cwAttachments.setFileNo(fileNo); // 파일들에 동일한 fileNo 설정
                cwAttachments.setSno(sno); // 각 파일에 대해 순차적인 sno 설정
                sno++; // 다음 파일의 sno 값 증가
            } else { // 파일 수정
                cwAttachments.setSno(cwAttachmentsRepository.findMaxSnoByFileNo(cwAttachments.getFileNo()) + 1);
            }
            cwAttachmentsRepository.save(cwAttachments); // 파일 저장
        }
        return fileNo;
    }

    public Integer generateFileNo() {
        Integer maxFileNo = cwAttachmentsRepository.findMaxFileNo();
        return (maxFileNo == null ? 1 : maxFileNo + 1);
    }

    @Transactional
    public void updateMainmtrlReqfrmFileNo(String reqfrmNo, Integer fileNo, String usrId) {
        mainmtrlReqfrmRepository.updateFileNo(
                reqfrmNo,
                fileNo,
                usrId,
                LocalDateTime.now());
    }

    public List<CwAttachments> findAttachment(Integer fileNo) {
        return cwAttachmentsRepository.findByFileNoAndDltYn(fileNo, "N");
    }

    // 전자결재
    @Transactional
    public void updateApprovalStatus(CwMainmtrlReqfrm mainmtrlReqfrm, String usrId, String apprvlStats, String apOpnin,
            String type) {
        mainmtrlReqfrm.setApprvlStats(apprvlStats);

        if ("E".equals(apprvlStats)) { // 결재 요청 시(요청자 데이터 세팅)
            mainmtrlReqfrm.setApReqId(usrId);
            mainmtrlReqfrm.setApReqDt(LocalDateTime.now());
        } else { // 결재요청 승인/반려 시(승인자 데이터 세팅)
            mainmtrlReqfrm.setApprvlId(usrId);
            mainmtrlReqfrm.setApprvlDt(LocalDateTime.now());
        }

        if (!StringUtils.hasText(mainmtrlReqfrm.getApOpnin())) {
            mainmtrlReqfrm.setApOpnin(apOpnin);
        }

        mainmtrlReqfrmRepository.saveAndFlush(mainmtrlReqfrm);

        // 승인 완료되면 PDF 문서화 (순차 실행 보장)
        if (apprvlStats.equals("A")) {
            this.makeMainmtrlReqPdf(mainmtrlReqfrm);

            try {
                // 1초 정도 딜레이 — 상황에 맞게 조정
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("PDF 생성 대기 중 인터럽트 발생", e);
            }
        }
    }

    @Transactional
    public void updateMainmtrlReqfrmReqCancel(List<ApDoc> apDocList, String usrId, boolean toApi) {
        apDocList.forEach(apDoc -> {
            CwMainmtrlReqfrm mainmtrlReqfrm = mainmtrlReqfrmRepository.findByApDocId(apDoc.getApDocId())
                    .orElse(null);
            if (mainmtrlReqfrm == null)
                return;
            mainmtrlReqfrmRepository.updateByApDocId(apDoc.getApDocId(), usrId, LocalDateTime.now());
        });
    }

    public List<String> getApDocIds(Map<String, Object> idsMap) {
        return mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.mainmtrlreqfrm.getApDocIds",
                idsMap);
    }

    @Transactional
    public void updateMainmtrlReqfrmByApDocId(String apDocId, String apUsrId, String apDocStats, String apOpnin,
            String type) {
        CwMainmtrlReqfrm cwMainmtrlReqfrm = mainmtrlReqfrmRepository.findByApDocId(apDocId).orElse(null);
        if (cwMainmtrlReqfrm != null) {
            String apStats = "C".equals(apDocStats) ? "A" : "R";
            updateApprovalStatus(cwMainmtrlReqfrm, apUsrId, apStats, apOpnin, type);
        }
    }

    // 유비리포트/통합문서
    public void makeMainmtrlReqPdf(CwMainmtrlReqfrm mainmtrlReqfrm) {
        try {
            String[] reportIds = {
                    "mainmtrl-reqfrm-report/mainmtrl_reqfrm_report.jrf" };

            Map<String, String> reportParams = new HashMap<>();
            reportParams.put("reqfrmNo", mainmtrlReqfrm.getReqfrmNo());
            reportParams.put("cntrctNo", mainmtrlReqfrm.getCntrctNo());
            reportParams.put("apprvlId", mainmtrlReqfrm.getApprvlId());
            reportParams.put("imgDir", previewPath.replaceAll("(upload[/\\\\]?).*$", ""));
            reportParams.put("baseUrl", apiCairosDomain.replaceAll("/+$", "") + "/");

            Map<String, String> callbackInfo = new HashMap<>();
            callbackInfo.put("reqKey", "reqfrmNo");
            callbackInfo.put("reqValue", mainmtrlReqfrm.getReqfrmNo());
            callbackInfo.put("pdfName", "pdf_doc");

            String apiUrl = "";
            if (("local".equals(activeProfile)) || ("dev".equals(activeProfile))) {
                apiUrl = "http://wjddns.idea-platform.net:8091";
            } else {
                if ("GAIA".equals(platform.toUpperCase())) {
                    apiUrl = apiGaiaDomain;
                } else if ("PGAIA".equals(platform.toUpperCase())) {
                    apiUrl = apiPGaiaDomain;
                } else if ("CAIROS".equals(platform.toUpperCase())) {
                    apiUrl = apiCairosDomain;
                }
            }

            callbackInfo.put("callbackUrl", apiUrl +
                    "/interface/mainmtrlReportDoc/callback-result");

            log.info("makeMainmtrlReqPdf: 데이터 세팅 완료. reqfrmNo = {}, reportParams = {},callbackInfo = {}",
                    mainmtrlReqfrm,
                    reportParams, callbackInfo);
            ubiReportClient.export(reportIds, reportParams, callbackInfo);
        } catch (GaiaBizException e) {
            throw new RuntimeException("PDF 생성 실패", e);
        }
    }

    /**
     * 완성된 주요자재 검수요청서 문서 DISK 저장 및 DB 업데이트
     * 1. DB에 저장된 pdf 첨부파일 모으기
     * 2. 콜백받은 pdf파일과 병합
     * 3. 병합된 pdf파일 통합문서관리 저장
     * 4. 주요자재 검수요청서에 docId 업데이트
     * 
     * 통합문서관리 저장 시 선행작업
     * 기초데이터 관리 > 정보문서관리 > 네비게이션 폴더 종류에 코드 추가 및 해당코드에 속성 1,2,3 추가
     */
    public Map<String, String> updateDiskFileInfo(List<MultipartFile> pdfFile,
            String reqfrmNo, String accessToken)
            throws IOException {

        Map<String, String> result = new HashMap<>();

        // 기본 정보 조회(계약번호, 프로젝트 번호)
        Map<String, Object> resultMap = this.getCntrctNoAndPjtNo(reqfrmNo);

        // 주요자재 검측요청서 조회
        CwMainmtrlReqfrm mainmtrlreqfrm = this.getMainmtrlReqfrm(MapUtils.getString(resultMap,
                "cntrct_no"), reqfrmNo);

        // 주요자재검수요청에 첨부파일 중 PDF파일 있을 시 콜백받은 PDF파일과 병합
        Integer atchFileNo = mainmtrlreqfrm.getAtchFileNo();
        List<File> dbPdfFiles = new ArrayList<>();

        if (atchFileNo != null) {
            List<CwAttachments> mainmtrlAttchs = this.findAttachment(atchFileNo);

            List<CwAttachments> pdfAttachments = mainmtrlAttchs.stream()
                    .filter(att -> {
                        String name = att.getFileNm();
                        return name != null && name.toLowerCase().endsWith(".pdf");
                    })
                    .toList();

            for (CwAttachments att : pdfAttachments) {
                Path fullPath = Paths.get(att.getFileDiskPath(), att.getFileDiskNm());
                File file = fullPath.toFile();
                if (!file.exists()) {
                    continue;
                }
                dbPdfFiles.add(file);
            }
        }

        // 1) MultipartFile → File 변환 (UbiReport에서 생성된 PDF)
        MultipartFile firstPdf = pdfFile.get(0);
        File ubiReportPdf = null;
        try {
            ubiReportPdf = File.createTempFile("ubi-", ".pdf");
            firstPdf.transferTo(ubiReportPdf);
        } catch (IOException e) {
            log.error("PDF 파일 처리 중 오류가 발생했습니다: {}", e.getMessage());
            throw new GaiaBizException(ErrorType.INTERNAL_SERVER_ERROR, e);
        }

        List<File> mergeFiles = new ArrayList<>();
        mergeFiles.add(ubiReportPdf);

        // 2) DB에 저장된 로컬 PDF 파일 추가
        mergeFiles.addAll(dbPdfFiles);

        // // 병합될 PDF파일 이름(문서번호 + .pdf)
        String DocNm = mainmtrlreqfrm.getDocNo() + ".pdf";

        // 병합된 PDF파일 byte 배열로 변환
        byte[] pdfBytes = PdfUtil.mergeToBytes(mergeFiles,
                String.format("%s/%s", uploadPath, getUploadPathByWorkType(FileUploadType.TEMP)), DocNm);

        // byte[] → MultipartFile 변환
        List<MultipartFile> files = new ArrayList<>();

        MultipartFile mergedFile = new MultipartFileWrapper(
                new CustomMultipartFile(pdfBytes),
                DocNm,
                DocNm,
                "multipart/form-data");
        files.add(mergedFile);

        // 속성 코드 조회(주요자재검수요청: 9)
        SmComCode smComCode = commonCodeService
                .getCommonCodeByGrpCdAndCmnCd(CommonCodeConstants.DOCUMENT_NAVI_FOLDER_TYPE_GROUP_CODE,
                        "9");

        final String navId = String.format("nav_%s_%s_01",
                MapUtils.getString(resultMap, "cntrct_no"),
                smComCode.getAttrbtCd3());

        List<DocumentForm.PropertyCreate> param = commonCodeService
                .createPropertyListForCommonCode(CommonCodeConstants.DOCUMENT_NAVI_FOLDER_TYPE_GROUP_CODE, "9", navId);

        if (properties != null) {
            // 속성 데이터 세팅
            List<DocumentForm.PropertyData> propertyData = this.savePdfPropertyDataToDoc(param, mainmtrlreqfrm);

            DocumentForm.DocCreateEx requestParams = new DocumentForm.DocCreateEx();
            requestParams.setNaviId(navId);
            requestParams.setNaviDiv("01"); // 01: 통합문서관리
            requestParams.setPjtNo(MapUtils.getString(resultMap, "pjt_no"));
            requestParams.setCntrctNo(MapUtils.getString(resultMap, "cntrct_no"));
            requestParams.setNaviPath("주요자재 검수요청서");
            requestParams.setNaviNm("주요자재 검수요청서");
            requestParams.setUpNaviNo(0);
            requestParams.setUpNaviId("");
            requestParams.setNaviLevel((short) 1);
            requestParams.setNaviType("FOLDR");
            requestParams.setNaviFolderType("9");
            requestParams.setNaviFolderKind(smComCode.getAttrbtCd3());
            requestParams.setProperties(param); // 네비게이션 생성
            requestParams.setPropertyData(propertyData);
            requestParams.setRgstrId(mainmtrlreqfrm.getRgstrId());
            requestParams.setDocNm(DocNm);

            Map<String, String> newHeaders = Maps.newHashMap();
            newHeaders.put("x-auth", accessToken);

            List<DcStorageMain> createFileResultList = documentServiceClient.createFile(requestParams, files,
                    newHeaders);

            // 주요자재에 docId 업데이트
            DcStorageMain dcStorageMain = null;
            if (createFileResultList != null && !createFileResultList.isEmpty()) {
                dcStorageMain = createFileResultList.get(0);
                if(dcStorageMain != null){
                    result.put("cntrctNo", MapUtils.getString(resultMap, "cntrct_no"));
                    result.put("reqfrmNo", reqfrmNo);
                    result.put("docId", dcStorageMain.getDocId());
                }
            }
            else{
                log.error("문서가 존재하지 않습니다.");
            }
        }
        return result;
    }

    public List<DocumentForm.PropertyData> savePdfPropertyDataToDoc(List<DocumentForm.PropertyCreate> properties,
            CwMainmtrlReqfrm mainmtrlReqfrm) {
        List<DocumentForm.PropertyData> insertList = new ArrayList<>();
        try {
            for (DocumentForm.PropertyCreate property : properties) {
                String attrbtCd = property.getAttrbtCd();

                if (attrbtCd != null) {
                    String attrbtCntnts = null;

                    switch (attrbtCd) {
                        case "docNo":
                            attrbtCntnts = mainmtrlReqfrm.getDocNo(); // 문서번호
                            break;

                        case "reqDt":
                            if (mainmtrlReqfrm.getReqDt() != null) {
                                attrbtCntnts = mainmtrlReqfrm.getReqDt()
                                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd")); // 검수요청일자
                            }
                            break;

                        case "apprvlStatsTxt":
                            String apprvlStats = mainmtrlReqfrm.getApprvlStats();
                            if ("A".equals(apprvlStats)) {
                                attrbtCntnts = "결재완료";
                            } else if ("E".equals(apprvlStats)) {
                                attrbtCntnts = "결재요청";
                            } else {
                                attrbtCntnts = apprvlStats;
                            }
                            break;

                        default:
                            attrbtCntnts = null;
                            break;
                    }

                    if (attrbtCntnts != null && !attrbtCntnts.isBlank()) {
                        DocumentForm.PropertyData row = new DocumentForm.PropertyData();
                        row.setAttrbtCd(attrbtCd);
                        row.setAttrbtCntnts(attrbtCntnts);
                        row.setRgstrId(mainmtrlReqfrm.getRgstrId());
                        row.setChgId(mainmtrlReqfrm.getChgId());

                        insertList.add(row);
                    }
                }
            }
            log.info("savePdfPropertyDataToDoc: 데이터 저장 결과 = {}", insertList);

            return insertList;
        } catch (GaiaBizException e) {
            log.warn("savePdfPropertyDataToDoc: 통합문서관리 속성 데이터 저장 중 오류 발생 메세지 = {}", e.getMessage());
        }
        return insertList;
    }

    public Map<String, String> updateMainmtrlDocId(String cntrctNo, String reqfrmNo, String docId) {
        Map<String, String> result = new HashMap<>();
        result.put("result", "fail");

        Map map = new HashMap();
        map.put("cntrctNo", cntrctNo);
        map.put("reqfrmNo", reqfrmNo);
        map.put("docId", docId);

        mybatisSession.update(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.mainmtrlreqfrm.updateMainmtrlDocId",
                map);

        result.put("result", "success");
        return result;
    }

    public Map<String, Object> getCntrctNoAndPjtNo(String reqfrmNo) {
        return mybatisSession.selectOne(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.mainmtrlreqfrm.getCntrctNoAndPjtNo",
                reqfrmNo);

    }

    public List<String> getDocIds(Map<String, Object> mainmtrlMap) {
        return mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.mainmtrlreqfrm.getDocIds",
                mainmtrlMap);
    }
}
