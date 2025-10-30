package kr.co.ideait.platform.gaiacairos.comp.construction.service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Maps;

import kr.co.ideait.iframework.file.CustomMultipartFile;
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
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwQualityActivity;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwQualityCheckList;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwQualityInspection;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwQualityPhoto;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DcStorageMain;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmComCode;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.CwAttachmentsRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.CwCntqltyCheckListRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.CwQualityActivityRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.CwQualityCheckListRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.CwQualityInspectionRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.CwQualityPhotoRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.construction.QualityinspectionMybatisParam.ActivityOutput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.construction.QualityinspectionMybatisParam.CheckListOutput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.construction.QualityinspectionMybatisParam.QualityOutPut;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.document.DocumentForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.document.CbgnPropertyDto;
import kr.co.ideait.platform.gaiacairos.core.type.FileUploadType;
import kr.co.ideait.platform.gaiacairos.core.util.FileService;
import kr.co.ideait.platform.gaiacairos.core.util.PdfUtil;
import kr.co.ideait.platform.gaiacairos.core.util.restclient.DocumentServiceClient;
import kr.co.ideait.platform.gaiacairos.core.util.restclient.UbiReportClient;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class QualityinspectionService extends AbstractGaiaCairosService {

    @Autowired
    CwQualityInspectionRepository cwQualityInspectionRepository;

    @Autowired
    CwQualityActivityRepository activityRepository;

    @Autowired
    CwQualityCheckListRepository checkListRepository;

    @Autowired
    CwAttachmentsRepository cwAttachmentsRepository;

    @Autowired
    CwCntqltyCheckListRepository cntqltyCheckListRepository;

    @Autowired
    CwQualityPhotoRepository cwQualityPhotoRepository;

    @Autowired
    FileService fileService;

    @Autowired
    CommonCodeService commonCodeService;

    @Autowired
    DocumentManageService documentmanageService;

    @Autowired
    DocumentServiceClient documentServiceClient;

    @Autowired
    private UbiReportClient ubiReportClient;

    /**
     * 품질검측 목록 조회
     */
    public List<Map<String, ?>> getQualityList(String cntrctNo, String searchValue, String selectedWorkType) {
        Map<String, Object> params = new HashMap<>();
        params.put("cntrctNo", cntrctNo);
        params.put("searchValue", searchValue);
        params.put("workcode", CommonCodeConstants.WORKTYPE_CODE_GROUP_CODE); // 공종 코드
        params.put("resultcode", CommonCodeConstants.RESULT_CODE_GROUP_CODE); // 검측결과 코드
        params.put("paymentcode", CommonCodeConstants.APPSTATUS_CODE_GROUP_CODE); // 결재결과 코드

        String workType = "";
        if ("work".equals(selectedWorkType)) {
            workType = "";
        } else {
            workType = selectedWorkType;
        }

        params.put("selectedWorkType", workType);
        return mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.qualityinspection.getQuality",
                params);
    }

    /**
     * 검측 조회
     */
    public CwQualityInspection getQuality(String cntrctNo, String qltyIspId) {
        return cwQualityInspectionRepository.findByCntrctNoAndQltyIspIdAndDltYn(cntrctNo, qltyIspId, "N");
    }

    public CwQualityInspection getQuality(String qltyIspId) {
        return cwQualityInspectionRepository.findByQltyIspIdAndDltYn(qltyIspId, "N");
    }

    public QualityOutPut getQualityByQuery(String cntrctNo, String qltyIspId) {
        Map<String, Object> params = new HashMap<>();
        params.put("cntrctNo", cntrctNo);
        params.put("qltyIspId", qltyIspId);
        params.put("paymentcode", CommonCodeConstants.PAYMENT_CODE_GROUP_CODE);
        params.put("workcode", CommonCodeConstants.WORKTYPE_CODE_GROUP_CODE);
        return mybatisSession.selectOne(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.qualityinspection.getQualityInspection",
                params);
    }

    /**
     * 품질 검측 액티비티 목록 조회
     */
    public List<ActivityOutput> getQualityActivityList(String cntrctNo, String qltyIspId) { // 화면 조회
        Map<String, Object> params = new HashMap<>();
        params.put("cntrctNo", cntrctNo);
        params.put("qltyIspId", qltyIspId);
        return mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.qualityinspection.getQualityActivity",
                params);
    }

    public List<CwQualityActivity> getOldActivity(String qltyIspId) {
        return activityRepository.findByQltyIspIdAndDltYn(qltyIspId, "N");
    }

    /**
     * 픔질 검측 체크리스트 조회
     */
    public List<CheckListOutput> getQualityCheckList(String cntrctNo, String qltyIspId) { // 화면 조회
        Map<String, Object> params = new HashMap<>();
        params.put("cntrctNo", cntrctNo);
        params.put("qltyIspId", qltyIspId);
        return mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.qualityinspection.getQualityCheckList",
                params);
    }

    /**
     * 품질검측 번호로 체크리스트 찾기
     */
    public List<CwQualityCheckList> getOldCheckList(String cntrctNo, String qltyIspId) {
        return checkListRepository.findByCntrctNoAndQltyIspIdAndDltYn(cntrctNo, qltyIspId, "N");
    }

    /**
     * 첨부 파일 조회
     */
    public List<CwAttachments> getFileList(int fileNo) {
        return cwAttachmentsRepository.findByFileNoAndFileDivAndDltYn(fileNo, "F", "N");
    }

    /**
     * 사진 첨부 파일 조회
     */
    public List<CwAttachments> getImgFileList(int fileNo) {
        return cwAttachmentsRepository.findByFileNoAndFileDivAndDltYn(fileNo, "I", "N");
    }

    /**
     * 계약별_품질검측 체크 리스트 조회
     */
    public List<CwCntqltyCheckList> getCntqltyCheckList() {
        return cntqltyCheckListRepository.findByDltYn("N");
    }

    /**
     * 품질 검측 삭제
     */
    @Transactional
    public void deleteQuality(CwQualityInspection quality, String usrId) {
        CwQualityInspection deleteQuality = cwQualityInspectionRepository
                .findByCntrctNoAndQltyIspIdAndDltYn(quality.getCntrctNo(), quality.getQltyIspId(), "N");

        // 품질검측 논리삭제
        cwQualityInspectionRepository.updateDelete(deleteQuality);

        Map map = new HashMap();
        map.put("qltyIspId", deleteQuality.getQltyIspId());
        map.put("dltId", usrId);

        // 검측요청한 경우 cw_request_item 테이블 논리 삭제
        mybatisSession.update(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.qualityinspection.deleteReqItemYn",
                map);
    }

    /**
     * 품질 검측 추가
     */
    public CwQualityInspection createQuality(CwQualityInspection inspection) {
        if (inspection.getQltyIspId() == null || inspection.getQltyIspId().isBlank()) {
            inspection.setQltyIspId(UUID.randomUUID().toString());
        }
        return cwQualityInspectionRepository.save(inspection);
    }

    /**
     * 품질 검측 첨부파일 번호 업데이트
     */
    @Transactional
    public void updateQualityFileNo(String qltyIspId, Integer fileNo, String usrId) {
        cwQualityInspectionRepository.updateFileNo(
                qltyIspId,
                fileNo,
                usrId,
                LocalDateTime.now());
    }

    /**
     * Activity저장
     */
    public void createActicity(CwQualityActivity activity) {
        activityRepository.save(activity);
    }

    /**
     * Activity 삭제
     */
    public void deleteActivity(List<CwQualityActivity> activity) {
        activityRepository.deleteAll(activity);
    }

    /**
     * 체크리스트 저장
     */
    public void createCheck(CwQualityCheckList checkList) {
        checkListRepository.save(checkList);
    }

    /**
     * 체크리스트 삭제
     */
    public void deleteCheck(List<CwQualityCheckList> checkList) {
        checkListRepository.deleteAll(checkList);
    }

    /**
     * 체크 리스트 목록
     * 추가 화면, 새창 화면
     */
    public List<Map<String, ?>> getAllCheckList(String cntrctNo, String cnsttyCd, String upCnsttyCd,
            String searchValue) {
        Map<String, Object> params = new HashMap<>();
        params.put("cntrctNo", cntrctNo);
        params.put("cnsttyCd", cnsttyCd);
        params.put(("upCnsttyCd"), upCnsttyCd);
        params.put("searchValue", searchValue);

        return mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.qualityinspection.getCheckList",
                params);
    }

    // public List<CwCntqltyCheckList> getAllCheckList_window(String cntrctNo) {
    // List<CwCntqltyCheckList> checkLists =
    // cntqltyCheckListRepository.findByCntrctNoAndCnsttyYnAndDltYn(cntrctNo,
    // "N", "N");
    // checkLists.sort(Comparator.comparingInt(CwCntqltyCheckList::getChklstSno));
    // return checkLists;
    // }

    /**
     * 셀렉트 박스
     * 공사, 공종
     */
    public List<Map<String, ?>> makeSelectBox(String cntrctNo, String upCnsttyCd) {
        Map<String, Object> params = new HashMap<>();
        params.put("cntrctNo", cntrctNo);
        params.put("upCnsttyCd", upCnsttyCd);
        return mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.qualityinspection.selectBox",
                params);
    }

    /**
     * Activity 목록
     * 조회, 검색조회
     */
    public List<Map<String, ?>> getActivityList(String cntrctNo, String searchValue) {
        Map<String, Object> params = new HashMap<>();
        params.put("cntrctNo", cntrctNo);
        params.put("searchValue", searchValue);
        return mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.qualityinspection.getActivity",
                params);
    }

    /**
     * 첨부파일 리스트 저장
     * 
     */
    @Transactional
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

    /**
     * 첨부파일 삭제
     */
    public void deleteAttachment(int fileNo, int sno) {
        cwAttachmentsRepository.updateDelete(cwAttachmentsRepository.findByFileNoAndSno(fileNo, sno));
    }

    /**
     * 사진 리스트 저장
     */
    public void createPhoto(CwQualityPhoto photo) {
        cwQualityPhotoRepository.save(photo);
    }

    /**
     * 사진 조회
     */
    public List<CwQualityPhoto> getPhotoList(String cntrctNo, String qltyIspId) {
        List<CwQualityPhoto> photoList = cwQualityPhotoRepository.findByCntrctNoAndQltyIspIdAndDltYn(cntrctNo,
                qltyIspId, "N");
        return photoList;
    }

    public CwQualityPhoto getPhoto(String cntrctNo, String qltyIspId, int phtSno) {
        return cwQualityPhotoRepository.findByCntrctNoAndQltyIspIdAndPhtSnoAndDltYn(cntrctNo,
                qltyIspId, phtSno, "N");
    }

    /**
     * 사진 삭제
     */
    public void deletePhoto(CwQualityPhoto photo) {
        cwQualityPhotoRepository.updateDelete(photo);
    }

    /**
     * 사진 순번 찾기
     */
    public Integer findMaxSno(String cntrctNo, String qltyIspId) {
        Integer maxSno = cwQualityPhotoRepository.findMaxPhtSnoByCntrctNoAndQltyIspId(cntrctNo, qltyIspId);
        return (maxSno != null) ? maxSno : 0;
    }

    /**
     * 첨부파일 순번 찾기
     */
    public Integer findMaxAttchSno(Integer fileNo) {
        return cwAttachmentsRepository.findMaxSnoByFileNo(fileNo);
    }

    /**
     * 조회
     */
    public CwCntqltyCheckList getCwCntqltyCheckList(String cntrctNo, String chklstId) {
        return cntqltyCheckListRepository.findByCntrctNoAndChklstIdAndDltYn(cntrctNo, chklstId, "N");
    }

    /**
     * 공종 코드 추가
     */
    public void createWork(CwCntqltyCheckList cwWork) {
        cntqltyCheckListRepository.save(cwWork);
    }

    /**
     * 공종 코드 중복체크
     */
    public CwCntqltyCheckList checkCode(String cnsttyCd) {
        return cntqltyCheckListRepository.findByCnsttyCdAndCnsttyYnAndDltYn(cnsttyCd, "Y", "N");
    }

    /**
     * 공종 삭제
     */
    public void deleteWork(String cntrctNo, String chklstId) {
        CwCntqltyCheckList deleteWork = cntqltyCheckListRepository.findByCntrctNoAndChklstIdAndDltYn(cntrctNo, chklstId,
                "N");
        cntqltyCheckListRepository.updateDelete(deleteWork);
    }

    /**
     * 검측 체크 리스트 조회(공종코드로 조회)
     */
    public List<CwCntqltyCheckList> getCheckLists(String cntrctNo, String cnsttyCd) {
        return cntqltyCheckListRepository.findByCntrctNoAndCnsttyCdAndCnsttyYn(cntrctNo, cnsttyCd, "N");
    }

    /**
     * 검측 체크 리스트 조회(체크리스트ID로 조회)
     */
    public CwCntqltyCheckList getCheck(String chklstId) {
        return cntqltyCheckListRepository.findByChklstId(chklstId);
    }

    /**
     * 검측 체크 리스트 추가
     */
    public void createCheck(CwCntqltyCheckList cwCheck) {
        if (cwCheck.getChklstSno() == null) {
            List<String> cntrctNos = Arrays.asList("CMIS", cwCheck.getCntrctNo());
            int max = cntqltyCheckListRepository.findMaxChklstSnoByCntrctNosAndCnsttyCd(cntrctNos,
                    cwCheck.getCnsttyCd());
            cwCheck.setChklstSno(max + 1);
        }
        cntqltyCheckListRepository.save(cwCheck);
    }

    /**
     * 검측 체크 리스트 삭제
     */
    public void deleteCheck(CwCntqltyCheckList cwCheck) {
        cntqltyCheckListRepository.updateDelete(cwCheck);
    }

    /**
     * 검측 체크 리스트 목록(그리드)
     */
    public List<Map<String, ?>> getGridList(String cntrctNo, String cnsttyCd, String searchValue, String useType) {
        Map<String, Object> params = new HashMap<>();
        params.put("cntrctNo", cntrctNo);
        params.put("cnsttyCd", cnsttyCd);
        params.put("code", CommonCodeConstants.CHECKLIST_CODE_GROUP_CODE);
        params.put("searchValue", searchValue);
        params.put("useType", useType);

        return mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.qualityinspection.getCheckGridList",
                params);

    }

    /**
     * 트리 목록 가져오기
     */
    public List<Map<String, ?>> getTreeList(String cntrctNo, String check) {
        Map<String, Object> params = new HashMap<>();
        params.put("code", CommonCodeConstants.WORKTYPE_CODE_GROUP_CODE);

        if ("common".equals(check)) {
            params.put("cntrctNo", cntrctNo);
            return mybatisSession.selectList(
                    "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.qualityinspection.getCheckTreeList",
                    params);
        } else {
            params.put("cntrctNo", cntrctNo);
            return mybatisSession.selectList(
                    "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.qualityinspection.getCheckTreeContractList",
                    params);
        }
    }

    /**
     * 감리목록 조회
     */
    public List<Map<String, ?>> getSupervisionList(String cntrctNo, String searchValue, String pjtNo, String platform) {
        Map<String, Object> params = new HashMap<>();
        params.put("cntrctNo", cntrctNo);
        params.put("searchValue", searchValue);
        params.put("pjtNo", pjtNo);
        params.put("pjtType", platform);
        return mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.qualityinspection.getSupervisionList",
                params);
    }

    /**
     * 요청(퀵메뉴) 테이블에 데이터 저장
     */
    public void insertRequestItem(Map<String, Object> insertParam) {
        mybatisSession.insert(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.qualityinspection.insertRequestItem",
                insertParam);
    }

    /**
     * pgaia 사용자 체크
     */
    public boolean checkPgaiaUser(CwQualityInspection qualityInspection, String isApiYn, String pjtDiv) {
        Map<String, Object> checkParams = new HashMap<>();
        checkParams.put("pjtNo", UserAuth.get(true).getPjtNo());
        checkParams.put("cntrctNo", qualityInspection.getCntrctNo());
        checkParams.put("apType", "07");
        boolean isPgaiaUser = mybatisSession.selectOne(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.approval.checkPgaiaFirstApprover",
                checkParams);

        boolean toApi = isPgaiaUser && "cairos".equals(platform) && "Y".equals(isApiYn) && "P".equals(pjtDiv);
        return toApi;
    }

    /**
     * 품질검측의 계약번호, 프로젝트 번호 조회
     */
    public Map<String, Object> getCntrctNoAndPjtNo(String qltyIspId) {
        return mybatisSession.selectOne(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.qualityinspection.getCntrctNoAndPjtNo",
                qltyIspId);

    }

    /**
     * FileNo생성
     */
    public Integer generateFileNo() {
        Integer maxFileNo = cwAttachmentsRepository.findMaxFileNo();
        return (maxFileNo == null ? 1 : maxFileNo + 1);
    }

    /**
     * 품질검측 승인상태 변경
     * 
     * @param qualityInspection
     * @param usrId
     * @param apprvlStats
     */
    @Transactional
    public void updateApprovalStatus(CwQualityInspection qualityInspection, String usrId, String apprvlStats,
            String apOpnin, String type) {
        if ("ISP".equals(type)) { // 검측요청일 경우
            if (!StringUtils.hasText(qualityInspection.getRsltCd())) {
                if ("A".equals(apprvlStats)) { // 승인일 경우 적합
                    qualityInspection.setRsltCd("01");
                } else if ("R".equals(apprvlStats)) { // 반려일 경우 부적합
                    qualityInspection.setRsltCd("02");
                }
            }

            if (qualityInspection.getRsltDt() == null) {
                qualityInspection.setRsltDt(LocalDateTime.now());
            }
            qualityInspection.setCqcId(usrId);
        } else {
            qualityInspection.setApprvlStats(apprvlStats);

            if ("E".equals(apprvlStats)) { // 결재 요청 시(요청자 데이터 세팅)
                qualityInspection.setApReqId(usrId);
                qualityInspection.setApReqDt(LocalDateTime.now());
            } else { // 결재요청 승인/반려 시(승인자 데이터 세팅)
                qualityInspection.setApprvlId(usrId);
                qualityInspection.setApprvlDt(LocalDateTime.now());
            }

            if (!StringUtils.hasText(qualityInspection.getApOpnin())) {
                qualityInspection.setApOpnin(apOpnin);
            }
        }
        cwQualityInspectionRepository.save(qualityInspection);

        // 승인 완료되면 PDF문서화
        if (apprvlStats.equals("A")) {
            // 품질검측관리 PDF 문서화
            this.makeQualityPdf(qualityInspection);

            try {
                // 1초 정도 딜레이 — 상황에 맞게 조정
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("PDF 생성 대기 중 인터럽트 발생", e);
            }
        }
    }

    /**
     * 품질검측 업데이트 BY apDocId
     * 
     * @param apDocId
     * @param apUsrId
     * @param apDocStats
     */
    @Transactional
    public void updateQualityInspectionByApDocId(String apDocId, String apUsrId, String apDocStats, String apOpnin,
            String type) {
        CwQualityInspection cwQualityInspection = new CwQualityInspection();
        if ("ISP".equals(type)) { // 검측요청일 시
            cwQualityInspection = cwQualityInspectionRepository.findByIspApDocId(apDocId).orElse(null);

            // '문서번호' 없을 시 기본값 자동 생성
            if (!StringUtils.hasText(cwQualityInspection.getRsltDocNo())) {
                String ispDocNo = cwQualityInspection.getIspDocNo();
                if (StringUtils.hasText(ispDocNo)) {
                    cwQualityInspection.setRsltDocNo(ispDocNo + " 검측 결과서");
                }
            }

            // 결재의견 있을 시 '지시 사항' 항목에 데이터 세팅
            if (!StringUtils.hasText(cwQualityInspection.getOrdeOpnin())) {
                cwQualityInspection.setOrdeOpnin(apOpnin);
            }

            // '승인'일 경우 체크박스 자동 전체 선택
            if ("C".equals(apDocStats)) {
                List<CwQualityCheckList> checkList = checkListRepository.findByCntrctNoAndQltyIspIdAndDltYn(
                        cwQualityInspection.getCntrctNo(), cwQualityInspection.getQltyIspId(), "N");

                for (int i = 0; i < checkList.size(); i++) {
                    checkList.get(i).setCqcYn("Y");
                    checkListRepository.save(checkList.get(i));
                }
            }
        } else { // 결재요청일 시
            cwQualityInspection = cwQualityInspectionRepository.findByApDocId(apDocId).orElse(null);
        }

        if (cwQualityInspection != null) {
            String apStats = "C".equals(apDocStats) ? "A" : "R";
            updateApprovalStatus(cwQualityInspection, apUsrId, apStats, apOpnin, type);
        }
    }

    /**
     * 품질검측 데이터 조회
     * 
     * @param apDocId
     * @return
     */
    public Map<String, Object> selectQualityInspectionByApDocId(String apDocId, String status) {
        Map<String, Object> returnMap = new HashMap<>();
        CwQualityInspection cwQualityInspection = cwQualityInspectionRepository.findByApDocId(apDocId).orElse(null);
        if (cwQualityInspection != null) {
            returnMap.put("report", cwQualityInspection);
            returnMap.put("resources", selectQualityInspectionResource(cwQualityInspection, status));
        }
        return returnMap;
    }

    /**
     * 품질검측 리소스 조회
     * 
     * @param qualityInspection
     * @return
     */
    public Map<String, Object> selectQualityInspectionResource(CwQualityInspection qualityInspection, String status) {
        Map<String, Object> returnMap = new HashMap<>();
        // 리소스 조회 로직
        List<CwQualityActivity> cwQualityActivities = activityRepository
                .findByQltyIspIdAndDltYn(qualityInspection.getQltyIspId(), "N");
        List<CwQualityCheckList> cwQualityCheckLists = checkListRepository.findByCntrctNoAndQltyIspIdAndDltYn(
                qualityInspection.getCntrctNo(), qualityInspection.getQltyIspId(), "N");
        List<CwQualityPhoto> cwQualityPhotos = cwQualityPhotoRepository.findByCntrctNoAndQltyIspIdAndDltYn(
                qualityInspection.getCntrctNo(), qualityInspection.getQltyIspId(), "N");
        List<CwCntqltyCheckList> cntqltyCheckLists = cntqltyCheckListRepository.findByDltYn("N");

        // 품질검측 첨부파일 (파일 객체 대신 메타데이터만 전송)
        List<Map<String, Object>> qualityFileInfo = Collections.emptyList();
        if (qualityInspection != null && qualityInspection.getAtchFileNo() != null) {
            log.info("##### Found safety attachment fileNo: {}", qualityInspection.getAtchFileNo());
            List<CwAttachments> qualityFiles = cwAttachmentsRepository.findByFileNoAndDltYn(
                    qualityInspection.getAtchFileNo(), "N");
            log.info("##### Found {} safety attachment files", qualityFiles.size());
            qualityFileInfo = convertToFileInfo(qualityFiles);
        } else {
            log.info("##### No safety attachment fileNo found");
        }

        returnMap.put("checkList", cwQualityCheckLists);
        returnMap.put("cntqltyLists", cntqltyCheckLists);
        if ("ISP".equals(status)) { // 검측 요청시
            returnMap.put("activity", cwQualityActivities);
            returnMap.put("photo", cwQualityPhotos);
            returnMap.put("qualityFileInfo", qualityFileInfo);
        }

        return returnMap;
    }

    /**
     * 검측요청 결재 삭제 -> 검측요청 컬럼 값 삭제 or 데이터 삭제
     * 
     * @param apDocList
     * @param usrId
     * @param toApi
     */
    @Transactional
    public void updateQualityIspApprovalReqCancel(List<ApDoc> apDocList, String usrId, boolean toApi) {
        apDocList.forEach(apDoc -> {
            CwQualityInspection cwQualityInspection = cwQualityInspectionRepository.findByIspApDocId(apDoc.getApDocId())
                    .orElse(null);

            if (cwQualityInspection == null)
                return;

            if (toApi) {
                // api 통신 true -> 데이터 삭제
                activityRepository.deleteyActivityByQltyIspId(cwQualityInspection.getQltyIspId());
                cwQualityPhotoRepository.deletePhotoByQltyIspId(cwQualityInspection.getQltyIspId());
                cwAttachmentsRepository.deleteAttachmentsByFileNo(cwQualityInspection.getAtchFileNo());
                checkListRepository.deleteCheckListByQltyIspId(cwQualityInspection.getQltyIspId());
                cwQualityInspectionRepository.delete(cwQualityInspection);
            } else {
                // api 통신 false -> 컬럼 값 변경
                cwQualityInspectionRepository.updateByIspApDocId(apDoc.getApDocId(), usrId, LocalDateTime.now());
                checkListRepository.updateByQltyIspId(cwQualityInspection.getQltyIspId(), usrId, LocalDateTime.now());
            }
        });
    }

    /**
     * 결재요청 결재 삭제 -> 결재요청 컬럼 값 삭제
     * 
     * @param apDocList
     * @param usrId
     * @param toApi
     */
    @Transactional
    public void updateQualityApprovalReqCancel(List<ApDoc> apDocList, String usrId, boolean toApi) {
        apDocList.forEach(apDoc -> {
            CwQualityInspection qualityInspection = cwQualityInspectionRepository.findByApDocId(apDoc.getApDocId())
                    .orElse(null);
            if (qualityInspection == null)
                return;
            cwQualityInspectionRepository.updateByApDocId(apDoc.getApDocId(), usrId, LocalDateTime.now());
        });
    }

    /**
     * 품질검측 API통신 -> 리소스 저장
     */
    @Transactional
    public void insertResourcesToApi(CwQualityInspection cwQualityInspection,
            List<CwQualityActivity> cwQualityActivities,
            List<CwQualityCheckList> cwQualityCheckLists,
            List<CwQualityPhoto> cwQualityPhotos,
            List<CwCntqltyCheckList> cwCntqltyCheckLists,
            List<Map<String, Object>> qualityFileInfo,
            List<Map<String, Object>> photoFileInfo) {

        cwQualityInspectionRepository.save(cwQualityInspection);

        if (cwQualityActivities != null && !cwQualityActivities.isEmpty()) {
            for (CwQualityActivity activity : cwQualityActivities) {
                activityRepository.save(activity);
            }
        }

        if (cwQualityCheckLists != null && !cwQualityCheckLists.isEmpty()) {
            for (CwQualityCheckList checklist : cwQualityCheckLists) {
                checkListRepository.save(checklist);
            }
        }

        if (cwQualityPhotos != null && !cwQualityPhotos.isEmpty()) {
            for (CwQualityPhoto photo : cwQualityPhotos) {
                cwQualityPhotoRepository.save(photo);
            }
        }

        // 파일 정보에서 품질검측 첨부파일들 가져오기
        log.info("##### Received qualityFileInfo: {}", qualityFileInfo != null ? qualityFileInfo.size() : 0);
        if (qualityFileInfo != null && !qualityFileInfo.isEmpty()) {
            log.info("##### Processing {} qualityInspection attachment file info", qualityFileInfo.size());
            insertQualityFileInfoToApiForInspection(cwQualityInspection, qualityFileInfo);
        } else {
            log.info("##### No qualityInspection attachment file info received");
        }

        log.info("##### Received photoFileInfo: {}", photoFileInfo != null ? photoFileInfo.size() : 0);
        if (photoFileInfo != null && !photoFileInfo.isEmpty()) {
            log.info("##### Processing {} photo attachment file info", photoFileInfo.size());
            insertQualityFileInfoToApiForPhoto(cwQualityInspection, cwQualityPhotos, photoFileInfo);
        } else {
            log.info("##### No photo attachment file info received");
        }
    }

    private void insertQualityFileInfoToApiForInspection(CwQualityInspection cwQualityInspection,
            List<Map<String, Object>> fileInfo) {

        CwQualityInspection inspection = cwQualityInspectionRepository.findByCntrctNoAndQltyIspIdAndDltYn(
                cwQualityInspection.getCntrctNo(), cwQualityInspection.getQltyIspId(), "N");

        if (inspection == null) {
            log.error("QualityInspection not found for: {}", cwQualityInspection.getQltyIspId());
            return;
        }

        insertFileInfoToApi(inspection.getCntrctNo(), fileInfo,
                inspection.getAtchFileNo(), inspection.getRgstrId());
    }

    private void insertQualityFileInfoToApiForPhoto(CwQualityInspection cwQualityInspection,
            List<CwQualityPhoto> photos,
            List<Map<String, Object>> fileInfo) {

        if (photos == null || photos.isEmpty()) {
            log.warn("No photo metadata provided for photo file upload");
            return;
        }

        Integer photoFileNo = photos.get(0).getAtchFileNo();
        if (photoFileNo == null) {
            log.warn("First photo has no attachment fileNo, skipping photo file upload");
            return;
        }

        insertFileInfoToApi(cwQualityInspection.getCntrctNo(), fileInfo,
                photoFileNo, cwQualityInspection.getRgstrId());
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
            String fullPath = Path.of(uploadPath, getUploadPathByWorkType(FileUploadType.QualityInspection, cntrctNo))
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

    /**
     * 품질검측 첨부파일 다운로드
     */
    public ResponseEntity<Resource> fileDownload(Integer fileNo, Integer sno) {
        // 1. 파일 메타정보 조회
        CwAttachments file = cwAttachmentsRepository.findByFileNoAndSno(fileNo, sno);
        if (file == null) {
            throw new GaiaBizException(ErrorType.NOT_FOUND, "첨부파일 정보가 없습니다.");
        }

        // 2. 실제 파일 경로
        Resource resource = fileService.getFile(file.getFileDiskPath(), file.getFileDiskNm());
        if (resource == null || !resource.exists()) {
            throw new GaiaBizException(ErrorType.NOT_FOUND, "파일이 존재하지 않습니다.");
        }

        // 3. 파일명 인코딩 (한글/공백/특수문자 대응)
        String fileNm = file.getFileNm();
        String encodedDownloadFile = URLEncoder.encode(fileNm, StandardCharsets.UTF_8);
        encodedDownloadFile = encodedDownloadFile.replaceAll("\\+", "%20");

        // 4. 다운로드 수 업데이트
        // MybatisInput input = MybatisInput.of().add("fileNo", fileNo).add("sno", sno);
        // updateDtAttachmentsViewCount(input);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedDownloadFile + "\"")
                .body(resource);
    }

    /**
     * 품질검측관리 PDF 문서화
     */
    public void makeQualityPdf(CwQualityInspection quality) {
        try {
            String[] reportIds = {
                    "quality-inspection-report/qualityinspection_report.jrf" };

            Map<String, String> reportParams = new HashMap<>();
            reportParams.put("qltyIspId", quality.getQltyIspId());
            reportParams.put("apprvlId", quality.getApprvlId());
            reportParams.put("imgDir", previewPath.replaceAll("(upload[/\\\\]?).*$", ""));
            reportParams.put("baseUrl", apiCairosDomain.replaceAll("/+$", "") + "/");

            Map<String, String> callbackInfo = new HashMap<>();
            callbackInfo.put("reqKey", "qltyIspId");
            callbackInfo.put("reqValue", quality.getQltyIspId());
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
                    "/interface/qualityReportDoc/callback-result");

            log.info("makeQualityPdf: 데이터 세팅 완료. qltyIspId = {}, reportParams = {},callbackInfo = {}", quality,
                    reportParams, callbackInfo);
            ubiReportClient.export(reportIds, reportParams, callbackInfo);
        } catch (GaiaBizException e) {
            throw new GaiaBizException(ErrorType.INTERNAL_SERVER_ERROR, e);
        }
    }

    /**
     * 완성된 품질검측 문서 DISK 저장 및 DB 업데이트
     * 1. DB에 저장된 pdf 첨부파일 모으기
     * 2. 콜백받은 pdf파일과 병합
     * 3. 병합된 pdf파일 통합문서관리 저장
     * 4. 품질검측에 docId 업데이트
     */
    public Map<String, String> updateDiskFileInfo(List<MultipartFile> pdfFile,
            String qltyIspId, String accessToken) {

        Map<String, String> result = new HashMap<>();

        // 기본 정보 조회(계약번호, 프로젝트 번호)
        Map<String, Object> resultMap = getCntrctNoAndPjtNo(qltyIspId);

        // 품질검측조회
        CwQualityInspection quality = getQuality(MapUtils.getString(resultMap,
                "cntrct_no"), qltyIspId);

        // 품질검측에 첨부파일 중 PDF파일 있을 시 콜백받은 PDF파일과 병합
        Integer atchFileNo = quality.getAtchFileNo();
        List<File> dbPdfFiles = new ArrayList<>();

        if (atchFileNo != null) {
            List<CwAttachments> qualityAttchs = findAttachment(atchFileNo);

            List<CwAttachments> pdfAttachments = qualityAttchs.stream()
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
        String DocNm = quality.getIspDocNo() + ".pdf";

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

        // 속성 코드 조회(품질검측: 7)
        SmComCode smComCode = commonCodeService
                .getCommonCodeByGrpCdAndCmnCd(CommonCodeConstants.DOCUMENT_NAVI_FOLDER_TYPE_GROUP_CODE,
                        "7");

        final String navId = String.format("nav_%s_%s_01",
                MapUtils.getString(resultMap, "cntrct_no"),
                smComCode.getAttrbtCd3());

        List<DocumentForm.PropertyCreate> param = commonCodeService
                .createPropertyListForCommonCode(CommonCodeConstants.DOCUMENT_NAVI_FOLDER_TYPE_GROUP_CODE, "7", navId);

        if (properties != null) {
            // 속성 데이터 세팅
            List<DocumentForm.PropertyData> propertyData = this.savePdfPropertyDataToDoc(param, quality);

            DocumentForm.DocCreateEx requestParams = new DocumentForm.DocCreateEx();
            requestParams.setNaviId(navId);
            requestParams.setNaviDiv("01"); // 01: 통합문서관리
            requestParams.setPjtNo(MapUtils.getString(resultMap, "pjt_no"));
            requestParams.setCntrctNo(MapUtils.getString(resultMap, "cntrct_no"));
            requestParams.setNaviPath("품질검측");
            requestParams.setNaviNm("품질검측");
            requestParams.setUpNaviNo(0);
            requestParams.setUpNaviId("");
            requestParams.setNaviLevel((short) 1);
            requestParams.setNaviType("FOLDR");
            requestParams.setNaviFolderType("7");
            requestParams.setNaviFolderKind(smComCode.getAttrbtCd3());
            requestParams.setProperties(param); // 네비게이션 생성
            requestParams.setPropertyData(propertyData);
            requestParams.setRgstrId(quality.getRgstrId());
            requestParams.setDocNm(DocNm);
            // requestParams.setDocId(quality.getDocId());
            Map<String, String> newHeaders = Maps.newHashMap();
            newHeaders.put("x-auth", accessToken);

            List<DcStorageMain> createFileResultList = documentServiceClient.createFile(requestParams, files,
                    newHeaders);

            // 품질검측에 docId 업데이트
            DcStorageMain dcStorageMain = null;
            if (createFileResultList != null) {
                dcStorageMain = createFileResultList.get(0);
            } else {
                throw new GaiaBizException(ErrorType.NO_DATA, "문서가 존재하지 않습니다.");
            }

            result.put("cntrctNo", MapUtils.getString(resultMap, "cntrct_no"));
            result.put("qltyIspId", qltyIspId);
            result.put("docId", dcStorageMain.getDocId());
        }

        return result;
    }

    /**
     * 통합문서관리의 속성 데이터 저장
     */
    public List<DocumentForm.PropertyData> savePdfPropertyDataToDoc(List<DocumentForm.PropertyCreate> properties,
            CwQualityInspection quality) {
        List<DocumentForm.PropertyData> insertList = new ArrayList<>();
        try {

            if (properties == null) {
                log.warn("savePdfPropertyDataToDoc: 조회된 속성 코드 없음");
            } else {
                for (DocumentForm.PropertyCreate property : properties) {
                    String attrbtCd = property.getAttrbtCd();

                    if (attrbtCd != null) {
                        String attrbtCntnts = attrbtCd.equals("ispDocNo") ? quality.getIspDocNo() : // 문서번호
                                attrbtCd.equals("ispReqDt") ? (quality.getIspReqDt() != null
                                        ? quality.getIspReqDt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                        : null) : // 검측요청일자
                                        attrbtCd.equals("apprvlStatsTxt")
                                                ? ("A".equals(quality.getApprvlStats()) ? "결재완료"
                                                        : "E".equals(quality.getApprvlStats()) ? "결재요청"
                                                                : quality.getApprvlStats())
                                                : null;

                        if (attrbtCntnts != null && !attrbtCntnts.isBlank()) {
                            DocumentForm.PropertyData row = new DocumentForm.PropertyData();
                            row.setAttrbtCd(attrbtCd);
                            row.setAttrbtCntnts(attrbtCntnts);
                            row.setRgstrId(quality.getRgstrId());
                            row.setChgId(quality.getChgId());

                            insertList.add(row);
                        }
                    }
                }
                log.info("savePdfPropertyDataToDoc: 데이터 저장 결과 = {}", insertList);
            }
            return insertList;
        } catch (GaiaBizException e) {
            log.warn("savePdfPropertyDataToDoc: 통합문서관리 속성 데이터 저장 중 오류 발생 메세지 = {}", e.getMessage());
        }
        return insertList;
    }

    /**
     * 품질검측 docId 업데이트
     */
    public Map<String, String> updateQualityDocId(String cntrctNo, String qltyIspId, String docId) {
        Map<String, String> result = new HashMap<>();
        result.put("result", "fail");

        Map map = new HashMap();
        map.put("cntrctNo", cntrctNo);
        map.put("qltyIspId", qltyIspId);
        map.put("docId", docId);

        mybatisSession.update(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.qualityinspection.updateIQualityDocId",
                map);

        result.put("result", "success");
        return result;
    }

    public List<CwAttachments> findAttachment(Integer fileNo) {
        return cwAttachmentsRepository.findByFileNoAndDltYn(fileNo, "N");
    }

    /**
     * 검측결과 작성한 항목의 cw_request_item 테이블 업데이트
     */
    public void updateReqItem(Map map) {
        // 검측요청한 경우 cw_request_item 테이블 논리 삭제
        mybatisSession.update(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.qualityinspection.updateReqItem", map);
    }

    /**
     * 검측요청 항목 삭제
     */
    public void deleteReqItem(String qltyIspId) {
        mybatisSession.delete(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.qualityinspection.deleteReqItem",
                qltyIspId);
    }

    /**
     * 유저 정보 조회
     */
    public Map<String, Object> getUser(String usrId, String cntrctNo) {
        Map map = new HashMap();
        map.put("cntrctNo", cntrctNo);
        map.put("usrId", usrId);

        return mybatisSession.selectOne(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.qualityinspection.getUser", map);
    }

    /**
     * ap_doc_id 조회
     */
    public List<String> getApDocIds(Map<String, Object> qualityMap) {
        return mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.qualityinspection.getApDocIds",
                qualityMap);
    }

    /**
     * doc_id 조회
     */
    public List<String> getDocIds(Map<String, Object> qualityMap) {
        return mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.qualityinspection.getDocIds",
                qualityMap);
    }
}
