package kr.co.ideait.platform.gaiacairos.comp.safety.service;

import kr.co.ideait.platform.gaiacairos.comp.eapproval.service.ApprovalRequestService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import kr.co.ideait.platform.gaiacairos.core.constant.CommonCodeConstants;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.*;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.*;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.safety.SafetymgmtMybatisParam.SafetyListOutPut;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.safety.SafetymgmtMybatisParam.SafetyOutput;
import kr.co.ideait.platform.gaiacairos.core.type.FileUploadType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class SafetymgmtService extends AbstractGaiaCairosService {

    @Autowired
    CwSafetyInspectionRepository inspectionRepository;

    @Autowired
    CwSafetyInspectionListRepository safetyListRepository;

    @Autowired
    CwStandardInspectionListRepository standardListRepository;

    @Autowired
    CwSafetyPhotoPhotoRepository photoRepository;

    @Autowired
    CwAttachmentsRepository cwAttachmentsRepository;

    @Autowired
    CwSadtagRepositories sadtagRepositories;

    // --------- 조회관련 -----------

    /**
     * 안전점검 목록 조회
     */
    public List<Map<String, ?>> getSafetyList(String cntrctNo, String selectedStatus, String searchValue,
            String selectedWorkType) { // Query
        Map<String, Object> params = new HashMap<>();
        params.put("cntrctNo", cntrctNo);
        params.put("searchValue", searchValue);
        params.put("apstscode", CommonCodeConstants.PAYMENT_CODE_GROUP_CODE);
        params.put("workcode", CommonCodeConstants.WORKTYPE_CODE_GROUP_CODE);

        String apprvlStats = "";
        if ("status".equals(selectedStatus) || "all".equals(selectedStatus)) {
            apprvlStats = "";
        } else {
            apprvlStats = selectedStatus;
        }

        String workType = "";
        if ("work".equals(selectedWorkType)) {
            workType = "";
        } else {
            workType = selectedWorkType;
        }

        params.put("selectedWorkType", workType);

        params.put("apprvlStats", apprvlStats);

        return mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.safety.safetymgmt.getSafety",
                params);

    }

    /**
     * 안전점검 단일 데이터 조회
     */
    public CwSafetyInspection getSafetyWithJpa(String cntrctNo, String inspectionNo) { // Jpa
        return inspectionRepository.findByCntrctNoAndInspectionNoAndDltYn(cntrctNo, inspectionNo, "N");
    }

    public SafetyOutput getSafetyWithQuery(String cntrctNo, String inspectionNo) { // Query
        Map<String, Object> params = new HashMap<>();
        params.put("cntrctNo", cntrctNo);
        params.put("inspectionNo", inspectionNo);
        params.put("workcode", CommonCodeConstants.WORKTYPE_CODE_GROUP_CODE);
        return mybatisSession.selectOne(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.safety.safetymgmt.getInspection",
                params);
    }

    /**
     * 안전점검 리스트 조회
     */
    public List<CwSafetyInspectionList> getSafetyListByJpa(String cntrctNo, String inspectionNo) {
        return safetyListRepository.findByCntrctNoAndInspectionNoAndDltYn(cntrctNo, inspectionNo, "N");
    }

    public List<SafetyListOutPut> getSafetyListByQuery(String cntrctNo, String inspectionNo) {
        Map<String, Object> params = new HashMap<>();
        params.put("cntrctNo", cntrctNo);
        params.put("inspectionNo", inspectionNo);
        return mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.safety.safetymgmt.getSafetyList",
                params);
    }

    /**
     * 대공종 조회
     */
    public List<Map<String, ?>> getCnsttyLvl1(String cntrctNo, String upCnsttyCd) {
        Map<String, Object> params = new HashMap<>();
        params.put("cntrctNo", cntrctNo);
        params.put("upCnsttyCd", upCnsttyCd);
        return mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.safety.safetymgmt.getCnsttyLvl1",
                params);
    }

    /**
     * 공종 조회
     */
    public List<Map<String, ?>> getCnsttyLvl2(String cntrctNo, String upCnsttyCd) {
        Map<String, Object> params = new HashMap<>();
        params.put("cntrctNo", cntrctNo);
        params.put("upCnsttyCd", upCnsttyCd);
        return mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.safety.safetymgmt.getCnsttyLvl2",
                params);
    }

    /**
     * 안전점검 점검항목 리스트 조회
     */
    public List<Map<String, ?>> getInspectionList(String cntrctNo, String cnsttyCd) {
        Map<String, Object> params = new HashMap<>();
        params.put("cntrctNo", cntrctNo);
        params.put("cnsttyCd", cnsttyCd);
        return mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.safety.safetymgmt.getInspectionList",
                params);
    }

    /**
     * 사진 조회
     */
    public List<CwSafetyInspectionPhoto> getPhotoList(String cntrctNo, String inspectionNo) {
        List<CwSafetyInspectionPhoto> photoList = photoRepository.findByCntrctNoAndInspectionNoAndDltYn(cntrctNo,
                inspectionNo, "N");
        return photoList;
    }

    public CwSafetyInspectionPhoto getPhoto(String cntrctNo, String inspectionNo, int phtSno) {
        return photoRepository.findByCntrctNoAndInspectionNoAndPhtSnoAndDltYn(cntrctNo, inspectionNo, phtSno, "N");
    }

    /**
     * 사진 파일 조회
     */
    public List<CwAttachments> getPhotoFileList(int fileNo) {
        return cwAttachmentsRepository.findByFileNoAndFileDivAndDltYn(fileNo, "I", "N");
    }

    /**
     * 안전점검 점검리스트 트리 목록 조회
     */
    public List<Map<String, ?>> getTreeList(String cntrctNo, String check) {
        Map<String, Object> params = new HashMap<>();
        params.put("code", CommonCodeConstants.WORKTYPE_CODE_GROUP_CODE);
        if ("common".equals(check)) {
            params.put("cntrctNo", cntrctNo);
            return mybatisSession.selectList(
                    "kr.co.ideait.platform.gaiacairos.mybatis.mappers.safety.safetymgmt.getListTree",
                    params);
        } else {
            params.put("cntrctNo", cntrctNo);
            return mybatisSession.selectList(
                    "kr.co.ideait.platform.gaiacairos.mybatis.mappers.safety.safetymgmt.getListContractTree",
                    params);
        }

    }

    /**
     * 안전점검 점검리스트 리스트 목록(그리드) 조회
     */
    public List<Map<String, ?>> getGridList(String cntrctNo, String cnsttyCd, String searchValue, String useType) {
        Map<String, Object> params = new HashMap<>();
        params.put("cntrctNo", cntrctNo);
        params.put("cnsttyCd", cnsttyCd);
        params.put("searchValue", searchValue);
        params.put("useType", useType);

        return mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.safety.safetymgmt.getSafeGridList",
                params);
    }

    /**
     * 안전점검 점검리스트 리스트 조회
     */
    public CwStandardInspectionList getList(String ispLstId) {
        return standardListRepository.findByAndIspLstIdAndDltYn(ispLstId, "N");
    }

    /**
     * 공종 조회(공종 코드로)
     */
    public CwStandardInspectionList getWorkByCnsttyCd(String cntrctNo, String CnsttyCd) {
        return standardListRepository.findByCntrctNoAndCnsttyCdAndDltYn(cntrctNo, CnsttyCd, "N");
    }

    /**
     * 안전지적서 목록 조회
     */
    public List<Map<String, ?>> getSadTagList(String cntrctNo, String searchValue, String selectedStatus) {
        Map<String, Object> params = new HashMap<>();
        String apprvlStats = "";
        params.put("cntrctNo", cntrctNo);
        params.put("searchValue", searchValue);

        if ("status".equals(selectedStatus) || "all".equals(selectedStatus)) {
            apprvlStats = "";
        } else {
            apprvlStats = selectedStatus;
        }

        params.put("apprvlStats", apprvlStats);

        return mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.safety.safetymgmt.getSadTagList", params);

    }

    /**
     * 안전지적서 단일 조회
     */
    public CwSadtag getSadTagData(String cntrctNo, String sadtagNo) {
        return sadtagRepositories.findByCntrctNoAndSadtagNoAndDltYn(cntrctNo, sadtagNo, "N");
    }

    public Map<String, ?> getSadtag(String cntrctNo, String sadtagNo) {
        Map<String, Object> params = new HashMap<>();
        params.put("cntrctNo", cntrctNo);
        params.put("sadtagNo", sadtagNo);
        return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.safety.safetymgmt.getSadTag",
                params);
    }

    /**
     * 안전점검 별 안전점검 항목 목록 조회
     */
    public List<CwSafetyInspectionList> findInspectionList(CwSafetyInspection safety) {
        return safetyListRepository
                .findByCntrctNoAndInspectionNoAndDltYn(safety.getCntrctNo(), safety.getInspectionNo(), "N");
    }

    /**
     * 안전점검 별 사진 데이터 목록 조회
     */
    public List<CwSafetyInspectionPhoto> findPhotoList(CwSafetyInspection safety) {
        return photoRepository
                .findByCntrctNoAndInspectionNoAndDltYn(safety.getCntrctNo(), safety.getInspectionNo(), "N");
    }

    /**
     * 점검리스트 조회
     */
    public List<CwStandardInspectionList> findStandardList(CwSafetyInspection safety) {
        return standardListRepository.findByDltYn("N");
    }

    /**
     * 안전점검 별 첨부파일 데이터 조회
     */
    public List<CwAttachments> findAttachment(CwSafetyInspection safety) {
        return cwAttachmentsRepository.findByFileNoAndDltYn(
                safety.getAtchFileNo(), "N");
    }

    /**
     * 공종 코드 중복체크
     */
    public boolean checkCode(String cnsttyCd) {
        return standardListRepository.existsByCnsttyCdAndCnsttyYnAndDltYn(cnsttyCd, "Y", "N");
    }

    /**
     * 기존 점검의 점검리스트들 조회
     */
    public List<CwSafetyInspectionList> getExistList(String cntrctNo, String inspectionNo) {
        return safetyListRepository.findByCntrctNoAndInspectionNoAndDltYn(cntrctNo, inspectionNo, "N");
    }

    /**
     * 점검 리스트 순번 조회
     */
    public int findSno(String cntrctNo) {
        return safetyListRepository.findMaxSno(cntrctNo);
    }

    /**
     * 부모 공종 조회
     */
    public String findUpCnsttyCd(String cntrctNo, String CnsttyCd) {
        return standardListRepository.findUpCnsttyCd(cntrctNo, CnsttyCd);
    }

    /**
     * 항목번호 조회
     */
    public int findLstNo(String cntrctno, String inspectionNo) {
        return safetyListRepository.findLstNo(cntrctno, inspectionNo);
    }

    /**
     * 사진순번 조회
     */
    public int getMaxPhtSno(String cntrctno, String inspectionNo) {
        return photoRepository.getMaxPhtSno(cntrctno, inspectionNo);
    }

    /**
     * 기존의 안전점검 리스트 번호 조회
     */
    public List<String> getExistIspLstIds(Set<String> ispLstIds) {
        return standardListRepository.findExistIspLstIds(ispLstIds);
    }

    /**
     * 점검결과작성 요청 문서번호로 안전점검 조회
     */
    public CwSafetyInspection findByRepApDocId(String apDocId) {
        return inspectionRepository.findByRepApDocId(apDocId).orElse(null);
    }

    /**
     * 전자결재 문서번호로 안전점검 조회
     */
    public CwSafetyInspection findByApDocId(String apDocId) {
        return inspectionRepository.findByApDocId(apDocId).orElse(null);
    }

    /**
     * 안전점검 데이터 조회(전자결재 문서로)
     *
     * @param apDocId
     * @return
     */
    public Map<String, Object> selectSafetyByApDocId(String apDocId, String status) {
        Map<String, Object> returnMap = new HashMap<>();
        CwSafetyInspection cwSafetyInspection = inspectionRepository.findByApDocId(apDocId).orElse(null);
        if (cwSafetyInspection != null) {
            returnMap.put("report", cwSafetyInspection);
            returnMap.put("resources", selectSafetyResource(cwSafetyInspection, status));
        }
        return returnMap;
    }

    // --------- 추가관련 -----------

    /**
     *
     * 안전점검 추가
     */
    public String createSafety(CwSafetyInspection safetyInspection) {
        if (safetyInspection.getInspectionNo() == null || safetyInspection.getInspectionNo().isBlank()) {
            safetyInspection.setInspectionNo(UUID.randomUUID().toString());
        }
        inspectionRepository.save(safetyInspection);
        return safetyInspection.getInspectionNo();
    }

    /**
     * 안전점검 리스트 추가
     */
    @Transactional
    public void createSafetyList(List<CwSafetyInspectionList> safetyLists) {
        for (int i = 0; i < safetyLists.size(); i++) {
            if (safetyLists.get(i).getDltYn() == null) {
                safetyLists.get(i).setDltYn("N");
            }
            safetyListRepository.save(safetyLists.get(i));
        }
    }

    /**
     * 사진 추가
     */
    public void createPhoto(CwSafetyInspectionPhoto photo) {
        photoRepository.save(photo);
    }

    /**
     * 점검 리스트 추가 findMaxIspLstId
     */
    public void createStandardList(CwStandardInspectionList cwList) {
        if (cwList.getIspLstId() != null) {
            String cleaned = cwList.getIspLstId().trim();
            cleaned = cleaned.replaceAll("\\p{C}", "");
            cleaned = cleaned.replaceAll("[^a-zA-Z0-9\\-]", "");

            cwList.setIspLstId(cleaned);

            if (cleaned.length() > 36) {
                throw new IllegalArgumentException("UUID가 36자를 초과합니다 (정제 후): " + cleaned);
            }

            int byteLength = cleaned.getBytes(StandardCharsets.UTF_8).length;
            if (byteLength > 36) {
                throw new IllegalArgumentException("UUID 바이트 수 초과 (정제 후): " + byteLength + " bytes, 값: " + cleaned);
            }
        }

        if (cwList.getIspLstSno() == null) {
            int max = standardListRepository.findMaxIspLstSno(
                    cwList.getCntrctNo(), cwList.getCnsttyCd());
            cwList.setIspLstSno(max + 1);
        }

        standardListRepository.save(cwList);
    }

    /**
     * 공종 코드 추가
     */
    public void createWork(CwStandardInspectionList cwWork) {
        standardListRepository.save(cwWork);
    }

    /**
     * 안전지적서 추가
     */
    public void createSadtag(CwSadtag cwSadtag) {
        if (cwSadtag.getSadtagNo() == null || cwSadtag.getSadtagNo().isBlank()) {
            cwSadtag.setSadtagNo(UUID.randomUUID().toString());
        }
        sadtagRepositories.save(cwSadtag);
    }

    // --------- 삭제관련 -----------

    /**
     * 안전점검 삭제(논리)
     */
    @Transactional
    public void deleteSafetyYn(CwSafetyInspection inspection) {
        inspectionRepository.updateDelete(inspection);
    }

    /**
     * 안전점검항목 삭제(로직)
     */
    public void deleteSafety(CwSafetyInspectionList delete) {
        safetyListRepository.delete(delete);
    }

    /**
     * 사진 삭제
     */
    public void deletePhoto(CwSafetyInspectionPhoto photo) {
        photoRepository.updateDelete(photo);
    }

    /**
     * 사진 파일 삭제
     */
    public void deleteAttachment(int fileNo, int sno) {
        cwAttachmentsRepository.updateDelete(cwAttachmentsRepository.findByFileNoAndSno(fileNo, sno));
    }

    /**
     * 공종 삭제
     */
    public void deleteWork(String cntrctNo, String ispLstId) {
        CwStandardInspectionList deleteWork = standardListRepository
                .findByAndIspLstIdAndDltYn(ispLstId, "N");
        standardListRepository.updateDelete(deleteWork);
    }

    /**
     * 점검 리스트 삭제
     */

    @Transactional
    public void deleteList(CwStandardInspectionList delete) {
        standardListRepository.updateDelete(delete);
    }

    /**
     * 안전지적서 삭제
     */
    @Transactional
    public void deleteList(CwSadtag delete) {
        sadtagRepositories.updateDelete(delete);
    }

    // --------- 기타 -----------

    /**
     * 화면에서 추가된 점점항목 디비에서 확인(By IspLstId)
     */
    public boolean checkStandard(String IspLstId) {
        return standardListRepository.existsByIspLstIdAndDltYn(IspLstId, "N");
    }

    /**
     * 첫번째 결재자가 pgaia 사용자인지 체크
     */
    public boolean checkPgaiaFirstApprover(Map<String, Object> checkParams) {
        return mybatisSession.selectOne(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.approval.checkPgaiaFirstApprover",
                checkParams);
    }

    /**
     * 안전점검 업데이트 BY apDocId
     *
     * @param apDocId
     * @param apUsrId
     * @param apDocStats
     * @param apOpnin
     */
    public void updateSafetyByApDocId(String apDocId, String apUsrId, String apDocStats, String apOpnin, String type) {
        CwSafetyInspection cwSafetyInspection = new CwSafetyInspection();
        if ("REP".equals(type)) { // 결과 작성 요청
            cwSafetyInspection = inspectionRepository.findByRepApDocId(apDocId).orElse(null);
            cwSafetyInspection.setRepApDocId(apDocId);
            inspectionRepository.save(cwSafetyInspection);

            if ("C".equals(apDocStats)) { // 점검요청 '승인'시 전부 '양호' 체크
                List<CwSafetyInspectionList> safetyList = safetyListRepository.findByCntrctNoAndInspectionNoAndDltYn(
                        cwSafetyInspection.getCntrctNo(), cwSafetyInspection.getInspectionNo(), "N");
                for (int i = 0; i < safetyList.size(); i++) {
                    safetyList.get(i).setGdFltyYn((short) 0);
                }
            }
        } else { // 승인 요청
            cwSafetyInspection = inspectionRepository.findByApDocId(apDocId).orElse(null);
            if (cwSafetyInspection != null) {
                String apStats = "C".equals(apDocStats) ? "A" : "R";
                updateSafetyApprovalStatus(cwSafetyInspection, apUsrId, apStats, apOpnin);
            }
        }
    }

    /**
     * 안전점검 승인상태 변경
     *
     * @param safety
     * @param usrId
     * @param apprvlStats
     */
    public CwSafetyInspection updateSafetyApprovalStatus(CwSafetyInspection safety, String usrId, String apprvlStats,
            String apOpnin) {
        safety.setApprvlStats(apprvlStats);
        if ("E".equals(apprvlStats)) {
            safety.setApReqId(usrId);
            safety.setApReqDt(LocalDateTime.now());
        } else {
            safety.setApprvlId(usrId);
            safety.setApprvlDt(LocalDateTime.now());
        }
        if (apOpnin != null) {
            safety.setApOpnin(apOpnin);
        }
        return inspectionRepository.save(safety);
    }

    /**
     * 안전점검 연계 데이터 조회
     * 
     * @param safety
     * @return
     */
    public Map<String, Object> selectSafetyResource(CwSafetyInspection safety, String status) {
        List<CwSafetyInspectionList> cwSafetyInspectionLists = safetyListRepository
                .findByCntrctNoAndInspectionNoAndDltYn(safety.getCntrctNo(), safety.getInspectionNo(), "N");
        List<CwSafetyInspectionPhoto> cwSafetyPhotos = photoRepository
                .findByCntrctNoAndInspectionNoAndDltYn(safety.getCntrctNo(), safety.getInspectionNo(), "N");
        List<CwStandardInspectionList> cwStandardInspectionLists = standardListRepository.findByDltYn("N");

        // 안전점검 첨부파일 (파일 객체 대신 메타데이터만 전송)
        List<Map<String, Object>> safetyFileInfo = Collections.emptyList();
        if (safety != null && safety.getAtchFileNo() != null) {
            log.info("##### Found safety attachment fileNo: {}", safety.getAtchFileNo());
            List<CwAttachments> safetyFiles = cwAttachmentsRepository.findByFileNoAndDltYn(
                    safety.getAtchFileNo(), "N");
            log.info("##### Found {} safety attachment files", safetyFiles.size());
            safetyFileInfo = convertToFileInfo(safetyFiles);
        } else {
            log.info("##### No safety attachment fileNo found");
        }

        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("inspectionlist", cwSafetyInspectionLists);
        returnMap.put("standardLists", cwStandardInspectionLists);

        if ("APP".equals(status)) { // 승인요청일 시 사진, 사진 데이터, 첨부 파일 추가
            returnMap.put("photo", cwSafetyPhotos);
            returnMap.put("safetyFileInfo", safetyFileInfo);
        }

        return returnMap;
    }

    /**
     * 안전지적서 승인상태 변경
     *
     * @param cwSadtag
     * @param usrId
     * @param apprvlStats
     * @param apOpnin
     */
    public void updateSadtagApprovalStatus(CwSadtag cwSadtag, String usrId, String apprvlStats, String apOpnin) {
        cwSadtag.setApprvlStats(apprvlStats);
        if ("E".equals(apprvlStats)) {
            cwSadtag.setApReqId(usrId);
            cwSadtag.setApReqDt(LocalDateTime.now());
        } else {
            cwSadtag.setApprvlId(usrId);
            cwSadtag.setApprvlDt(LocalDateTime.now());
        }
        if (apOpnin != null) {
            cwSadtag.setApOpnin(apOpnin);
        }
        sadtagRepositories.save(cwSadtag);
    }

    /**
     * 안전지적서 업데이트 BY apDocId
     *
     * @param apDocId
     * @param apUsrId
     * @param apDocStats
     * @param apOpnin
     */
    public void updateSadtagByApDocId(String apDocId, String apUsrId, String apDocStats, String apOpnin) {
        CwSadtag cwSadtag = sadtagRepositories.findByApDocId(apDocId).orElse(null);
        if (cwSadtag != null) {
            String apStats = "C".equals(apDocStats) ? "A" : "R";
            updateSadtagApprovalStatus(cwSadtag, apUsrId, apStats, apOpnin);
        }
    }

    /**
     * 안전지적서 데이터 조회
     *
     * @param apDocId
     * @return
     */
    public Map<String, Object> selectSadtagByApDocId(String apDocId) {
        Map<String, Object> returnMap = new HashMap<>();
        CwSadtag cwSadtag = sadtagRepositories.findByApDocId(apDocId).orElse(null);
        if (cwSadtag != null) {
            returnMap.put("report", cwSadtag);
        }
        return returnMap;
    }

    /**
     * 안전점검 결재요청 삭제 -> 컬럼 값 삭제 or 데이터 삭제
     *
     * @param apDocList
     * @param usrId
     * @param toApi
     */
    public void updateSafetyReportReqCancel(List<ApDoc> apDocList, String usrId, boolean toApi) {
        apDocList.forEach(apDoc -> {
            CwSafetyInspection cwSafetyInspection = inspectionRepository.findByRepApDocId(apDoc.getApDocId())
                    .orElse(null);

            if (cwSafetyInspection == null)
                return;

            if (toApi) {
                // api 통신 true -> 데이터 삭제
                inspectionRepository.delete(cwSafetyInspection);
            } else {
                // api 통신 false -> 컬럼 값 변경
                inspectionRepository.updateByRepApDocId(apDoc.getApDocId(), usrId);
            }
        });
    }

    /**
     * 안전점검 승인요청 삭제 -> 컬럼 값 삭제 or 데이터 삭제
     *
     * @param apDocId
     * @param usrId
     * @param toApi
     */
    public void updateSafetyApprovalReqCancel(String apDocId, String usrId, boolean toApi) {
        CwSafetyInspection cwSafetyInspection = inspectionRepository.findByApDocId(apDocId).orElse(null);

        if (cwSafetyInspection == null)
            return;

        if (toApi && cwSafetyInspection != null) {
            // api 통신 true -> 데이터 삭제
            inspectionRepository.delete(cwSafetyInspection);
        } else {
            // api 통신 false -> 컬럼 값 변경
            inspectionRepository.updateByApDocId(apDocId, usrId);
        }
    }

    /**
     * 안전지적서 결재요청 삭제 -> 컬럼 값 삭제 or 데이터 삭제
     *
     * @param apDocList
     * @param usrId
     * @param toApi
     */
    public void updateSadtagApprovalReqCancel(List<ApDoc> apDocList, String usrId, boolean toApi) {
        apDocList.forEach(apDoc -> {
            CwSadtag cwSadtag = sadtagRepositories.findByApDocId(apDoc.getApDocId()).orElse(null);

            if (cwSadtag == null)
                return;

            if (toApi) {
                // api 통신 true -> 데이터 삭제
                sadtagRepositories.delete(cwSadtag);
            } else {
                // api 통신 false -> 컬럼 값 변경
                sadtagRepositories.updateByApDocId(apDoc.getApDocId(), usrId);
            }
        });
    }

    /**
     * 안전점검 API통신 -> 리소스 조회
     */
    public void insertSafetyResourcesToApi(CwSafetyInspection cwSafetyInspection,
            List<CwSafetyInspectionPhoto> cwSafetyPhotos,
            List<CwSafetyInspectionList> inspectionlist,
            List<CwStandardInspectionList> standardLists,
            List<Map<String, Object>> safetyFileInfo) {

        // 안전 점검 데이터 저장
        inspectionRepository.save(cwSafetyInspection);

        // 점검 사진 데이터 저장
        if (cwSafetyPhotos != null && !cwSafetyPhotos.isEmpty()) {
            for (CwSafetyInspectionPhoto photo : cwSafetyPhotos) {
                photoRepository.save(photo);
            }
        }

        // 점검항목 데이터 저장
        if (inspectionlist != null && !inspectionlist.isEmpty()) {
            safetyListRepository.saveAll(inspectionlist);
        }

        // 점검 리스트 데이터 저장
        if (standardLists != null && !standardLists.isEmpty()) {
            standardListRepository.saveAll(standardLists);
        }

        // 파일 정보에서 안전점검 첨부파일들 가져오기
        log.info("##### Received safetyFileInfo: {}", safetyFileInfo != null ? safetyFileInfo.size() : 0);
        if (safetyFileInfo != null && !safetyFileInfo.isEmpty()) {
            log.info("##### Processing {} safety attachment file info", safetyFileInfo.size());
            insertSafetyFileInfoToApi(cwSafetyInspection, safetyFileInfo);
        } else {
            log.info("##### No safety attachment file info received");
        }
    }

    /**
     * 안전지적서 API통신 -> 리소스 조회
     */
    public void insertSadtagResourcesToApi(CwSadtag cwSadtag) {
        sadtagRepositories.save(cwSadtag);
    }

    private void insertSafetyFileInfoToApi(CwSafetyInspection cwSafetyInspection,
            List<Map<String, Object>> safetyFileInfo) {
        // 안전 점검 정보 조회
        CwSafetyInspection safetyInspection = inspectionRepository.findByCntrctNoAndInspectionNoAndDltYn(
                cwSafetyInspection.getCntrctNo(), cwSafetyInspection.getInspectionNo(), "N");

        if (safetyInspection == null) {
            log.error("Deficiency not found for Inspection");
            return;
        }

        insertFileInfoToApi(cwSafetyInspection.getCntrctNo(), safetyFileInfo, safetyInspection.getAtchFileNo(),
                safetyInspection.getRgstrId());
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

    /**
     * 공통 파일 처리 메서드
     *
     * @param files   처리할 파일 목록
     * @param fileNo  연결할 파일 번호
     * @param rgstrId 등록자 ID
     */
    @Transactional
    public void insertFileInfoToApi(String cntrctNo, List<Map<String, Object>> files, Integer fileNo, String rgstrId) {
        List<CwAttachments> cwAttachmentsList = new ArrayList<>();

        log.info("##### FILE PROCESSING START - Type: {}, TargetId: {}, FileCount: {}, FileNo: {}",
                files != null ? files.size() : 0, fileNo);

        if (files != null && !files.isEmpty()) {
            log.info("##### Starting {} file processing for {} - TargetId: {}, FileCount: {}", files.size());

            // 파일 저장 경로 설정
            String fullPath = Path.of(uploadPath, getUploadPathByWorkType(FileUploadType.SAFETY, cntrctNo)).toString()
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
                    Files.createDirectories(savedFilePath.getParent());
                    log.info("##### Created directory: {}", savedFilePath.getParent());

                    // 파일 저장
                    Files.write(savedFilePath, fileContent);
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
                            } catch (ArithmeticException e) {
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
                } catch (IOException e) {
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

    /**
     * API 연동후 PGAIA에서 첨부파일 저장
     *
     * @param cwAttachmentsList
     * @return
     */
    @Transactional
    public Integer createCwAttachmentsList(List<CwAttachments> cwAttachmentsList) {
        Integer fileNo = generateFileNo();
        Integer sno = 1;

        for (CwAttachments cwAttachments : cwAttachmentsList) {
            if (cwAttachments.getFileNo() == null) {
                cwAttachments.setFileNo(fileNo);
                cwAttachments.setSno(sno);
                sno++;
            } else {
                fileNo = cwAttachments.getFileNo();
                cwAttachments
                        .setSno((cwAttachmentsRepository.findMaxSnoByFileNo(cwAttachments.getFileNo()) + 1));
            }
            cwAttachmentsRepository.save(cwAttachments); // 파일 저장
        }
        return fileNo;
    }

    private Integer generateFileNo() {
        Integer maxFileNo = cwAttachmentsRepository.findMaxFileNo();
        return (maxFileNo == null ? 1 : maxFileNo + 1);
    }
}
