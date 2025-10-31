package kr.co.ideait.platform.gaiacairos.comp.construction.service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
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
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwCntqltyCheckList;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwMainmtrl;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwMainmtrlReqfrm;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwMainmtrlReqfrmPhoto;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwQualityActivity;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwQualityCheckList;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwQualityInspection;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwQualityPhoto;
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

    /**
     * pgaia API통신 -> 통신받은 리소스 저장
     */
    @Transactional
    public void insertResourcesToApi(CwMainmtrlReqfrm mainmtrlReqfrm,
            List<CwMainmtrl> mainmtrls,
            List<CwMainmtrlReqfrmPhoto> photos,
            List<Map<String, Object>> mainmtrlReqfrmFileInfo) {

        mainmtrlReqfrmRepository.save(mainmtrlReqfrm);

        // 자재 저장
        if(mainmtrls != null && !mainmtrls.isEmpty()) {
            for (CwMainmtrl mainmtrl : mainmtrls) {
                mainmtrlRepository.save(mainmtrl);
            }
        }

        // 사진 데이터 저장
        if(photos != null && !photos.isEmpty()){
            for (CwMainmtrlReqfrmPhoto photo : photos) {
                photoRepository.save(photo);
            }
        }

        // 파일 정보에서 주요자재검수요청서 첨부파일들 가져오기
        log.info("##### Received mainmtrlReqfrmFileInfo: {}", mainmtrlReqfrmFileInfo != null ? mainmtrlReqfrmFileInfo.size() : 0);
        if (mainmtrlReqfrmFileInfo != null && !mainmtrlReqfrmFileInfo.isEmpty()) {
            log.info("##### Processing {} mainmtrlReqfrmFileInfo attachment file info", mainmtrlReqfrmFileInfo.size());
            insertMainmtrlFileInfoToApiForInspection(mainmtrlReqfrm, mainmtrlReqfrmFileInfo);
        } else {
            log.info("##### No mainmtrlReqfrm attachment file info received");
        }
    }

    private void insertMainmtrlFileInfoToApiForInspection(CwMainmtrlReqfrm mainmtrlReqfrm,
            List<Map<String, Object>> fileInfo) {

        CwMainmtrlReqfrm cwMainmtrlReqfrm = mainmtrlReqfrmRepository.findByCntrctNoAndReqfrmNoAndDltYn(
                mainmtrlReqfrm.getCntrctNo(), mainmtrlReqfrm.getReqfrmNo(), "N");

        if (cwMainmtrlReqfrm == null) {
            log.error("cwMainmtrlReqfrm not found for: {}", cwMainmtrlReqfrm.getReqfrmNo());
            return;
        }

        insertFileInfoToApi(cwMainmtrlReqfrm.getCntrctNo(), fileInfo,
                cwMainmtrlReqfrm.getAtchFileNo(), cwMainmtrlReqfrm.getRgstrId());
    }

    /**
     * pgaia에 보낼 주요자재검수요청서 데이터 조회
     */
    public Map<String, Object> selectMainmtrlReqfrmByApDocId(String apDocId) {
        Map<String, Object> returnMap = new HashMap<>();

        // 전자결재 Id로 검수요청서 조회
        CwMainmtrlReqfrm cwMainmtrlReqfrm = mainmtrlReqfrmRepository.findByApDocId(apDocId).orElse(null);
        if (cwMainmtrlReqfrm != null) {
            returnMap.put("report", cwMainmtrlReqfrm);
            returnMap.put("resources", selectMainmtrlReqfrmResource(cwMainmtrlReqfrm));
        }
        return returnMap;
    }

    /**
     * pgaia에 보낼 주요자재검수요청서 리소스 조회
     */
    public Map<String, Object> selectMainmtrlReqfrmResource(CwMainmtrlReqfrm mainmtrlReqfrm) {
        Map<String, Object> returnMap = new HashMap<>();

        // 자재
        List<CwMainmtrl> cwMainmtrls = mainmtrlRepository.findByCntrctNoAndReqfrmNoAndDltYn(mainmtrlReqfrm.getCntrctNo(), mainmtrlReqfrm.getReqfrmNo(), "N");

        // 사진 데이터
        List<CwMainmtrlReqfrmPhoto> cwMainmtrlReqfrmPhotos = photoRepository.findByCntrctNoAndReqfrmNoAndDltYn(mainmtrlReqfrm.getCntrctNo(), mainmtrlReqfrm.getReqfrmNo(), "N");

        // 첨부파일 데이터
        List<Map<String, Object>> mainmtrlReqfrmFileInfo = Collections.emptyList();
        if (mainmtrlReqfrm != null && mainmtrlReqfrm.getAtchFileNo() != null) {
            log.info("##### Found mainmtrlReqfrm attachment fileNo: {}", mainmtrlReqfrm.getAtchFileNo());
            List<CwAttachments> mainmtrlReqfrmFiles = cwAttachmentsRepository.findByFileNoAndDltYn(
                    mainmtrlReqfrm.getAtchFileNo(), "N");
            log.info("##### Found {} mainmtrlReqfrm attachment files", mainmtrlReqfrmFiles.size());
            mainmtrlReqfrmFileInfo = convertToFileInfo(mainmtrlReqfrmFiles);
        } else {
            log.info("##### No mainmtrlReqfrm attachment fileNo found");
        }

        returnMap.put("mainmtrls", cwMainmtrls);
        returnMap.put("photo", cwMainmtrlReqfrmPhotos);
        returnMap.put("mainmtrlReqfrmFileInfo", mainmtrlReqfrmFileInfo);

        return returnMap;
    }

    // 파일 정보 변환 헬퍼 메소드 (JSON 직렬화 가능) - 파일 내용 포함
    public List<Map<String, Object>> convertToFileInfo(List<CwAttachments> attachments) {
        log.info("##### Converting {} attachments to file info", attachments != null ? attachments.size() : 0);

        if (attachments == null || attachments.isEmpty()) {
            log.info("##### No attachments to convert");
            return Collections.emptyList();
        }

        List<Map<String, Object>> fileInfoList = new ArrayList<>();

        for (CwAttachments attachment : attachments) {
            if (attachment == null || attachment.getFileNm() == null) {
                log.warn("##### Invalid attachment data: {}", attachment);
                continue;
            }

            // 파일 경로가 없는 경우 건너뛰기
            if (attachment.getFileDiskPath() == null || attachment.getFileDiskNm() == null) {
                log.warn("##### Physical file path not found for attachment: {}", attachment.getFileNm());
                continue;
            }

            Path filePath = Paths.get(attachment.getFileDiskPath(), attachment.getFileDiskNm());
            if (!Files.exists(filePath)) {
                log.warn("##### File not found: {}", filePath);
                continue;
            }

            try {
                log.info("##### Reading file: {}", filePath);
                // 파일 내용을 Base64로 인코딩
                byte[] fileContent = Files.readAllBytes(filePath);
                String base64Content = Base64.getEncoder().encodeToString(fileContent);

                Map<String, Object> fileInfo = new HashMap<>();
                fileInfo.put("fileNo", attachment.getFileNo());
                fileInfo.put("sno", attachment.getSno());
                fileInfo.put("fileDiv", attachment.getFileDiv());
                fileInfo.put("fileNm", attachment.getFileNm());
                fileInfo.put("fileDiskNm", attachment.getFileDiskNm());
                fileInfo.put("fileDiskPath", attachment.getFileDiskPath());
                fileInfo.put("fileSize", attachment.getFileSize());
                fileInfo.put("fileHitNum", attachment.getFileHitNum());
                fileInfo.put("rgstrId", attachment.getRgstrId());
                fileInfo.put("chgId", attachment.getChgId());
                fileInfo.put("dltYn", attachment.getDltYn());
                fileInfo.put("fileContent", base64Content); // Base64로 인코딩된 파일 내용

                if (fileInfo.containsKey("fileDiv")) {
                    log.info("fileDiv is present with value: {}", fileInfo.get("fileDiv"));
                } else {
                    log.info("fileDiv is missing in fileInfo map");
                }

                fileInfoList.add(fileInfo);
                log.info("##### File info created for: {} (Size: {} bytes, Base64 length: {})",
                        attachment.getFileNm(), fileContent.length, base64Content.length());
            } catch (IOException e) {
                log.error("##### Error reading file {}: {}", filePath, e.getMessage());
                // 파일 읽기 실패 시 건너뛰기
                continue;
            }
        }

        log.info("##### Successfully converted {} attachments to file info", fileInfoList.size());
        return fileInfoList;
    }

    @Transactional
    public void insertFileInfoToApi(String cntrctNo, List<Map<String, Object>> files, Integer fileNo, String rgstrId) {
        List<CwAttachments> cwAttachmentsList = new ArrayList<>();

        log.info("##### FILE PROCESSING START - Type: {}, TargetId: {}, FileCount: {}, FileNo: {}",
                files != null ? files.size() : 0, fileNo);

        if (files != null && !files.isEmpty()) {
            log.info("##### Starting {} file processing for {} - TargetId: {}, FileCount: {}", files.size());

            // 파일 저장 경로 설정
            String fullPath = Path.of(uploadPath, getUploadPathByWorkType(FileUploadType.MAINMTRL_REQFRM, cntrctNo))
                    .toString()
                    .replace("\\", "/");

            log.info("##### {} file storage path configured - BaseDir: {}, DatePath: {}, FullPath: {}", fullPath);

            for (Map<String, Object> fileInfo : files) {
                String fileName = (String) fileInfo.get("fileNm");
                log.info("##### Processing {} file info - FileName: {}, FileInfo keys: {}", fileName,
                        fileInfo.keySet());

                String fileDiv = (String) fileInfo.get("fileDiv");

                // 파일 이름이 비어있거나 null인 경우 건너뛰기
                if (fileName == null || fileName.trim().isEmpty()) {
                    log.warn("##### Skipping {} file with empty name");
                    continue;
                }

                log.info("##### Processing {} file: {} (Size: {} bytes)", fileName, fileInfo.get("fileSize"));

                try {
                    // Base64로 인코딩된 파일 내용을 디코딩
                    String base64Content = (String) fileInfo.get("fileContent");
                    if (base64Content == null || base64Content.isEmpty()) {
                        log.warn("##### No file content found for {} file: {}", fileName);
                        continue;
                    }

                    log.info("##### Base64 content length for {} file {}: {}", fileName,
                            base64Content.length());

                    byte[] fileContent = Base64.getDecoder().decode(base64Content);
                    log.info("##### Decoded {} file content: {} bytes", fileContent.length);

                    // 파일을 디스크에 저장
                    String savedFileName = generateUniqueFileName(fileName);
                    Path savedFilePath = Paths.get(fullPath, savedFileName);

                    log.info("##### Saving {} file to: {}", savedFilePath);

                    // 디렉토리가 없으면 생성
                    try {
                        Files.createDirectories(savedFilePath.getParent());
                    } catch (IOException e) {
                        throw new GaiaBizException(ErrorType.INTERNAL_SERVER_ERROR, e);
                    }
                    log.info("##### Created directory: {}", savedFilePath.getParent());

                    // 파일 저장
                    try {
                        Files.write(savedFilePath, fileContent);
                    } catch (IOException e) {
                        throw new GaiaBizException(ErrorType.INTERNAL_SERVER_ERROR, e);
                    }
                    log.info("##### {} file saved to disk: {}", savedFilePath);

                    CwAttachments cwAttachments = new CwAttachments();
                    cwAttachments.setFileNo(fileNo);
                    cwAttachments.setFileDiv(fileDiv);
                    cwAttachments.setFileNm(fileName);
                    cwAttachments.setFileDiskNm(savedFileName);
                    cwAttachments.setFileDiskPath(fullPath);
                    cwAttachments.setFileSize(((Number) fileInfo.get("fileSize")).intValue());
                    cwAttachments.setDltYn("N");

                    // fileHitNum 안전한 타입 변환
                    Object fileHitNumObj = fileInfo.get("fileHitNum");
                    if (fileHitNumObj != null) {
                        if (fileHitNumObj instanceof Integer) {
                            cwAttachments.setFileHitNum((Integer) fileHitNumObj);
                        } else if (fileHitNumObj instanceof Short) {
                            cwAttachments.setFileHitNum(((Short) fileHitNumObj).intValue());
                        } else if (fileHitNumObj instanceof BigDecimal) {
                            BigDecimal decimalValue = (BigDecimal) fileHitNumObj;
                            try {
                                cwAttachments.setFileHitNum(decimalValue.intValueExact());
                            } catch (GaiaBizException e) {
                                log.warn("##### fileHitNum has decimal part for {} file: {}, value: {}",
                                        fileName, decimalValue);
                                cwAttachments.setFileHitNum(0);
                            }
                        } else if (fileHitNumObj instanceof Number) {
                            // Catch-all for Long, Double, Float, etc.
                            cwAttachments.setFileHitNum(((Number) fileHitNumObj).intValue());
                        } else {
                            log.warn("##### Unexpected fileHitNum type for {} file: {}, value: {}", fileName,
                                    fileHitNumObj);
                            cwAttachments.setFileHitNum(0);
                        }
                    } else {
                        cwAttachments.setFileHitNum(0);
                    }

                    cwAttachments.setRgstrId(rgstrId);
                    cwAttachments.setChgId(rgstrId);

                    cwAttachmentsList.add(cwAttachments);
                    log.info("##### {} attachment object created for file: {} - FileNo: {}, FileSize: {}",
                            fileName, fileNo, cwAttachments.getFileSize());
                } catch (GaiaBizException e) {
                    log.error("##### Error processing {} file {}: {}", fileName, e.getMessage(), e);
                }
            }

            if (!cwAttachmentsList.isEmpty()) {
                try {
                    log.info("##### Saving {} {} attachments to database", cwAttachmentsList.size());
                    Integer savedFileNo = createCwAttachmentsList(cwAttachmentsList);
                    log.info("##### Successfully saved {} {} attachments with FileNo: {}", cwAttachmentsList.size(),
                            savedFileNo);
                } catch (GaiaBizException e) {
                    log.error("##### Error saving {} attachments to database: {}", e.getMessage(), e);
                }
            } else {
                log.warn("##### No valid {} attachments to save");
            }
        } else {
            log.info("##### No {} files to process for {} - TargetId: {}");
        }

        log.info("##### FILE PROCESSING END - Type: {}, TargetId: {}, ProcessedCount: {}", cwAttachmentsList.size());
    }

    // 고유한 파일명 생성 메서드
    private String generateUniqueFileName(String originalFileName) {
        String extension = "";
        String nameWithoutExtension = originalFileName;

        int lastDotIndex = originalFileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            extension = originalFileName.substring(lastDotIndex);
            nameWithoutExtension = originalFileName.substring(0, lastDotIndex);
        }

        String timestamp = String.valueOf(System.currentTimeMillis());
        return nameWithoutExtension + "_" + timestamp + extension;
    }
}
