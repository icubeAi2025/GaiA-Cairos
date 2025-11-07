package kr.co.ideait.platform.gaiacairos.comp.safety;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import kr.co.ideait.iframework.EtcUtil;
import kr.co.ideait.platform.gaiacairos.comp.common.CommonUtilComponent;
import kr.co.ideait.platform.gaiacairos.comp.construction.DailyreportComponent;
import kr.co.ideait.platform.gaiacairos.comp.eapproval.DraftComponent;
import kr.co.ideait.platform.gaiacairos.comp.eapproval.helper.EapprovalHelper;
import kr.co.ideait.platform.gaiacairos.comp.eapproval.service.ApprovalRequestService;
import kr.co.ideait.platform.gaiacairos.comp.safety.service.SafetyDiaryIntegrationService;
import kr.co.ideait.platform.gaiacairos.comp.safety.service.SafetyDiaryService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractComponent;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.ApDoc;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwAttachments;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.CwAttachmentsRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.safety.report.SafetyDiaryForm.safetyDiaryParam;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.safety.report.SafetyDiaryRequest;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.util.UtilForm;
import kr.co.ideait.platform.gaiacairos.core.type.FileUploadType;
import kr.co.ideait.platform.gaiacairos.core.util.FileService;
import kr.co.ideait.platform.gaiacairos.core.util.restclient.DocumentServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class SafetyDiaryComponent extends AbstractComponent {

    @Autowired
    CwAttachmentsRepository cwAttachmentsRepository;

    @Autowired
    DailyreportComponent dailyreportComponent;

    @Autowired
    CommonUtilComponent commonutilComponent;

    @Autowired
    DraftComponent draftComponent;

    @Autowired
    SafetyDiaryService safetyDiaryService;

    @Autowired
    SafetyDiaryIntegrationService safetyDiaryIntegrationService;

    @Autowired
    ApprovalRequestService approvalRequestService;

    @Autowired
    FileService fileService;

    @Autowired
    DocumentServiceClient documentServiceClient;

    public List<?> getSafetyDiaryList(CommonReqVo commonReqVo, safetyDiaryParam param) {
//        Map<String, Object> map = EtcUtil.convertObjectToMap(param);
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map = mapper.convertValue(param, Map.class);
        List<Map<String, Object>> list = safetyDiaryService.getSafetyDiaryList(map);
        return list;
    }

    /**
     * 안전일지 중복검사
     * @param cntrctNo
     * @param dailyReportDate
     * @return
     */
    public boolean checkDuplicateSafeDiary(String cntrctNo, String dailyReportDate) {
        Map map = new HashMap();
        map.put("cntrctNo", cntrctNo);
        map.put("dailyReportDate", dailyReportDate);
        boolean isRegistrable = safetyDiaryService.checkDuplicateSafeDiary(map);

        return isRegistrable;
    }

    /**
     * 안전일지 복사
     * @param commonReqVo
     * @param paramMap
     * @return
     */
    @Transactional
    public String copyDiary(CommonReqVo commonReqVo, Map<String, Object> paramMap) {

        String newSafeDiaryId = UUID.randomUUID().toString();
        String copyDate = paramMap.get("dailyReportDate").toString();           // YYYYMMDD
        String copyDateIso = paramMap.get("dailyReportDateIso").toString();     // YYYY-MM-DD
        String usrId = commonReqVo.getUserId();
        paramMap.put("newSafeDiaryId", newSafeDiaryId);
        paramMap.put("copyDate", copyDate);
        paramMap.put("usrId", usrId);


        // 1. 안전일지 (본문) 복사
        log.info("1. copyDiary - 안전일지 복사 (본문) ");

        // 본문 - 기상현황
        UtilForm.KmaWeather kmaWeather = new UtilForm.KmaWeather();
        kmaWeather.setPjtNo(commonReqVo.getPjtNo());
        kmaWeather.setTm(copyDate); // yyyyMMdd

        Map<String, Object> kma = null;
        try {
            kma = commonutilComponent.getKmaWeather(kmaWeather);
        } catch (IOException e) {
            throw new GaiaBizException(ErrorType.INTERFACE,"날씨 정보 조회 실패 : {}",e.getMessage());
        }
        paramMap.put("forcAm", kma.get("am_wf"));   // 날씨 (오전)
        paramMap.put("forcPm", kma.get("pm_wf"));   // 날씨 (오후)
        paramMap.put("taMin", kma.get("ta_min"));   // 날씨 (최저기온)
        paramMap.put("taMax", kma.get("ta_max"));   // 날씨 (최고기온)

        // 본문 - 근로자 수
        String cntrctNo = paramMap.get("cntrctNo").toString();
        Map<String, Object> dailyReport = this.checkDailyReportExists(cntrctNo, copyDateIso);

        boolean isExistsDailyReport = false;
        if (dailyReport.get("isExists") != null) {
            isExistsDailyReport = Boolean.parseBoolean(dailyReport.get("isExists").toString());
        }
        log.info("1. copyDiary - 안전일지 복사 (본문/근로자 데이터) - 일자: {} 안전일지 존재: {}", copyDateIso, isExistsDailyReport);
        if (isExistsDailyReport) {
            // 보고일 기준 사무직 근로자 수
            if (dailyReport.get("LS") != null) {
                paramMap.put("officeM", dailyReport.get("LS"));
            } else {
                paramMap.put("officeM", 0);
            }
            // 보고일 기준 노무직 근로자 수
            if (dailyReport.get("L") != null) {
                paramMap.put("laborM", dailyReport.get("L"));
            } else {
                paramMap.put("laborM", 0);
            }
            // 보고일 기준 장비인원 근로자 수
            if (dailyReport.get("E") != null) {
                paramMap.put("equipM", dailyReport.get("E"));
            } else {
                paramMap.put("equipM", 0);
            }
        } else {
            paramMap.put("officeM", 0);
            paramMap.put("laborM", 0);
            paramMap.put("equipM", 0);
        }

        // 본문 - 무재해운동
        Map<String, Object> campaign = this.getZeroAccidentCampaignData(cntrctNo, copyDate);
        log.info("1. copyDiary - 안전일지 복사 (무재해운동) - 일자: {} 무재해운동: {}", copyDateIso, campaign);
        if (campaign != null) {
            // 무재해운동 목표시간
            if (campaign.get("acc_fre_targ_tm") != null) { paramMap.put("accFreTargTm", campaign.get("acc_fre_targ_tm")); }
            // 무재해운동 전일시간
            if (campaign.get("acc_fre_current_tm") != null) { paramMap.put("accFreYdayTm", campaign.get("acc_fre_current_tm")); }
        }
        // 무재해운동 금일시간
        paramMap.put("accFreTdayTm", 8);

        safetyDiaryService.copySafetyDiary(paramMap);

        // 2. 안전일지 작업현황 복사
        log.info("2. copyDiary - 안전일지 복사 (작업현황) ");
        List<Map<String, Object>> workList = safetyDiaryService.getSafetyDiaryWorkStatusList(paramMap);
        for (Map work : workList) {
            String workId = work.get("work_id").toString();
            String newWorkId = UUID.randomUUID().toString();

            paramMap.put("workId", workId);
            paramMap.put("newWorkId", newWorkId);
            safetyDiaryService.copySafetyDiaryWorkStatus(paramMap);
        }

        // 3. 안전일지 순찰/점검결과 복사
        log.info("3. copyDiary - 안전일지 복사 (순찰/점검결과) ");
        List<Map<String, Object>> safetyList = safetyDiaryService.getSafetyDiaryPatrolStatusList(paramMap);
        for (Map patrol : safetyList) {
            String checkId = patrol.get("check_id").toString();
            String newCheckId = UUID.randomUUID().toString();

            paramMap.put("checkId", checkId);
            paramMap.put("newCheckId", newCheckId);
            safetyDiaryService.copySafetyDiaryPatrolStatus(paramMap);
        }

        return newSafeDiaryId;
    }

    /**
     * 안전일지 추가/수정 - 개요 - 해당 날짜의 작업일보 ID 조회
     * @param cntrctNo
     * @param dailyReportDate
     * @return
     */
    public Map<String, Object> checkDailyReportExists(String cntrctNo, String dailyReportDate) {
        // 1. 계약 해당일자에 작업일보 존재 여부 확인
        Map map = new HashMap();
        map.put("cntrctNo", cntrctNo);
        map.put("dailyReportDate", dailyReportDate);
        Long dailyReportId = safetyDiaryService.checkDailyReportExists(map);

        Map rtnMap = new HashMap();
        if (dailyReportId != null) {
            rtnMap.put("isExists", true);

            // 2-1. 작업일보 노무자 정보 조회
            List<Map<String, Object>> laborList = dailyreportComponent.getResource(cntrctNo, dailyReportId, "L");
            // 2-2. 작업일보 노무자 정보 분류 (사무직, 노무직)
            if (laborList.size() != 0) {
//                Map<Object, Long> laborCount = laborList.stream()
//                        .map(m -> (String) m.get("rsce_tp_cd"))
//                        .filter(Objects::nonNull)
//                        .collect(Collectors.groupingBy(s -> s, Collectors.counting()));

                Map<String, Long> laborSum = laborList.stream()
                        .filter(m -> m.get("rsce_tp_cd") != null)       // 1. rsce_tp_cd null 제외
                        .collect(Collectors.groupingBy(
                                m -> (String) m.get("rsce_tp_cd"),      // 2. 그룹핑 기준: rsce_tp_cd ('L', 'LS')
                                Collectors.collectingAndThen(
                                        Collectors.summingDouble(m -> {
                                            Object qty = m.get("actual_qty");
                                            return qty == null ? 0.0 : Double.parseDouble(qty.toString());
                                        }),
                                        d -> (long) Math.ceil(d)        // 3. 최종 결과를 올림(Long 변환)
                                )
                        ));

                rtnMap.putAll(laborSum);
            }
            // 2-3. 작업일보 노무자 정보 분류 (장비직)
            List<Map<String, Object>> equipList = dailyreportComponent.getResource(cntrctNo, dailyReportId, "E");

            long equipSum = (long) Math.ceil(
                    equipList.stream()
                            .filter(m -> m.get("actual_qty") != null)       // 1. null 제외
                            .mapToDouble(m -> ((BigDecimal) m.get("actual_qty")).doubleValue()) // BigDecimal → double
                            .filter(qty -> qty != 0)                        // 3. 수량 0 제외
                            .sum()
            );

            // 2-4. 반환 값 가공
            rtnMap.put("E", equipSum);

        } else {
            rtnMap.put("isExists", false);
        }

        return rtnMap;
    }

    /**
     * 안전일지 교육현황 조회
     * @param cntrctNo
     * @param dailyReportDate
     * @return
     */
    public List<Map<String, Object>> getEducationStatus(String cntrctNo, String dailyReportDate) {
        Map map = new HashMap();
        map.put("cntrctNo", cntrctNo);
        map.put("dailyReportDate", dailyReportDate);

        return safetyDiaryService.getEducationStatus(map);
    }

    /**
     * 안전일지 재해현황 조회
     * @param cntrctNo
     * @param dailyReportDate
     * @return
     */
    public Map<String, Object> getDisasterStatus(String cntrctNo, String dailyReportDate) {
        Map map = new HashMap();
        map.put("cntrctNo", cntrctNo);
        map.put("dailyReportDate", dailyReportDate);

        return safetyDiaryService.getDisasterStatus(map);
    }

    /**
     * 안전일지 보고일자 기준전 최신 누계 값 조회
     * @param cntrctNo
     * @param dailyReportDate (YYYYMMDD)
     * @return
     */
    public Long getPrevCusum(String cntrctNo, String dailyReportDate) {
        Map map = new HashMap();
        map.put("cntrctNo", cntrctNo);
        map.put("dailyReportDate", dailyReportDate);
        return safetyDiaryService.getPrevCusum(map);
    }

    /**
     * 안전일지 무재해현황 데이터 조회
     * @param cntrctNo
     * @param dailyReportDate (YYYYMMDD)
     * @return
     */
    public Map<String, Object> getZeroAccidentCampaignData(String cntrctNo, String dailyReportDate) {
        Map map = new HashMap();
        map.put("cntrctNo", cntrctNo);
        map.put("dailyReportDate", dailyReportDate);

        Map zeroAccidentCampaignData = safetyDiaryService.getZeroAccidentCampaignData(map);

        // 완료일 JSON 변환 시 숫자 timestamp 직렬화로 인한 포맷팅 처리
        if (zeroAccidentCampaignData != null && zeroAccidentCampaignData.get("ccmplt_date") != null) {
            Date ccmpltDate = (Date) zeroAccidentCampaignData.get("ccmplt_date");
            zeroAccidentCampaignData.put("ccmplt_date", new SimpleDateFormat("yyyy-MM-dd").format(ccmpltDate));
        }
        return zeroAccidentCampaignData;
    }

    /**
     * 안전일지 추가
     * @param report
     * @param commonReqVo
     * @return
     */
    @Transactional
    public void addSafetyDiary(SafetyDiaryRequest report, CommonReqVo commonReqVo) {

        String usrId = commonReqVo.getUserId();
        String safeDiaryId = UUID.randomUUID().toString();

        Map<String, Object> safetyDiary = EtcUtil.convertObjectToMap(report.getSafetyDiary());
        List<Map<String, Object>> safetyList = EtcUtil.convertListToMapList(report.getSafetyList());
        List<Map<String, Object>> workList = EtcUtil.convertListToMapList(report.getWorkList());

        // 1. 안전일지 추가
        log.info("1. addSafetyDiary - 안전일지 추가 (본문) ");
        safetyDiary.put("safeDiaryId", safeDiaryId);
        safetyDiary.put("usrId", usrId);
        safetyDiaryService.addSafetyDiary(safetyDiary);

        // 2. 작업현황 추가
        log.info("2. addSafetyDiary - 안전일지 추가 (작업현황) ");
        for (Map work : workList) {
            // 작업 아이디 SET (UUID)
            work.put("workId", UUID.randomUUID().toString());
            work.put("safeDiaryId", safeDiaryId);
            work.put("usrId", usrId);
            safetyDiaryService.addSafetyDiaryWorkStatus(work);
        }

        // 3. 순회/점검 결과 추가
        log.info("3. addSafetyDiary - 안전일지 추가 (순회/점검) ");
        for (Map patrol : safetyList) {
            // 순회점검 아이디 SET (UUID)
            patrol.put("checkId", UUID.randomUUID().toString());
            patrol.put("safeDiaryId", safeDiaryId);
            patrol.put("usrId", usrId);
            safetyDiaryService.addSafetyDiaryPatrolStatus(patrol);
        }

        // 4. 첨부파일 로직 시작
        List<FileService.FileMeta> fileList = report.getFileList();
        if (fileList != null && fileList.size() > 0) {
            FileService.FileMeta newMeta = null;
            String cntrctNo = report.getSafetyDiary().getCntrctNo();
            String savePath = String.format("%s/%s", uploadPath, getUploadPathByWorkType(FileUploadType.SAFETY, cntrctNo));

            Integer sno = 1;
            Integer fileNo = safetyDiaryService.getSafetyDiaryAttachmentMaxFileNo() + 1;
            safetyDiary.put("atchFileNo", fileNo);

            // 안전일지 첨부파일번호 업데이트
            safetyDiaryService.updateSafetyDiaryAttachmentFileNo(safetyDiary);
            for (FileService.FileMeta meta : fileList) {

                String metaString = null;
                FileService.FileMeta tempRvwPhotoFileMeta = null;
                try {
                    metaString = objectMapper.writeValueAsString(meta);
                    newMeta = fileService.build(metaString, savePath);             // 실제 물리파일 처리
                    tempRvwPhotoFileMeta = objectMapper.readValue(metaString, FileService.FileMeta.class);
                } catch (JsonProcessingException e) {
                    throw new GaiaBizException(ErrorType.INTERNAL_SERVER_ERROR,"JsonParsing failed : {}",e.getMessage());
                }


                CwAttachments cwFile = new CwAttachments();
                cwFile.setFileNo(fileNo);
                cwFile.setSno(sno);
                cwFile.setFileNm(newMeta.getOriginalFilename());
                cwFile.setFileDiskNm(newMeta.getFileName());
                cwFile.setFileDiskPath(newMeta.getDirPath());
                cwFile.setFileSize(newMeta.getSize());
                cwFile.setFileDiv("F");
                cwFile.setRgstrId(usrId);
                cwFile.setChgId(usrId);
                safetyDiaryService.addSafetyDiaryAttachment(cwFile);            // 메타 테이블 INSERT
                fileService.moveFile(tempRvwPhotoFileMeta.getFilePath(), newMeta.getFilePath()); // Temp ->  리얼폴더로 이동
                sno += 1;
            }
        }


    }

    /**
     * 안전일지 상세조회
     * @param cntrctNo
     * @param diaryId
     * @return
     */
    public Map<String, Object> getSafetyDiary(String cntrctNo, String diaryId) {

        Map<String, Object> rtnMap = new HashMap<>();
        Map<String, Object> param = new HashMap<>();

        param.put("cntrctNo", cntrctNo);
        param.put("safeDiaryId", diaryId);

        // 1. 안전일지 - 본문
        Map<String, Object> safeDiary = safetyDiaryService.getSafetyDiaryDetail(param);

        // 2. 안전일지 - 작업현황
        List<Map<String, Object>> workList = safetyDiaryService.getSafetyDiaryWorkStatusList(param);

        // 3. 안전일지 - 안전현황
        List<Map<String, Object>> patrolList = safetyDiaryService.getSafetyDiaryPatrolStatusList(param);

        // 4. 첨부파일 목록
        if (safeDiary != null && safeDiary.get("atch_file_no") != null) {
            param.put("fileNo", safeDiary.get("atch_file_no"));
            List <CwAttachments> fileList = safetyDiaryService.getSafetyDiaryAttachments(param);

            rtnMap.put("fileList", fileList);
        }


        rtnMap.put("safeDiary", safeDiary);
        rtnMap.put("workList", workList);
        rtnMap.put("patrolList", patrolList);
        if (safeDiary != null) {
            rtnMap.put("dailyReportDate", safeDiary.get("daily_report_date"));
        }
        return rtnMap;
    }

    /**
     * 안전일지 수정
     * @param report
     * @param commonReqVo
     */
    @Transactional
    public void modifySafetyDiary(SafetyDiaryRequest report, CommonReqVo commonReqVo) {

        String usrId = commonReqVo.getUserId();
        String safeDiaryId = report.getSafetyDiary().getSafeDiaryId();

        Map<String, Object> safetyDiary = EtcUtil.convertObjectToMap(report.getSafetyDiary());
        List<Map<String, Object>> safetyList = EtcUtil.convertListToMapList(report.getSafetyList());
        List<Map<String, Object>> workList = EtcUtil.convertListToMapList(report.getWorkList());
        List<Map<String, Object>> deletedSafetyList = EtcUtil.convertListToMapList(report.getDeletedSafetyList());
        List<Map<String, Object>> deletedWorkList = EtcUtil.convertListToMapList(report.getDeletedWorkList());

        // 1. 안전일지 수정
        log.info("1. modifySafetyDiary - 안전일지 수정 (본문) ");
        safetyDiary.put("usrId", usrId);
        safetyDiaryService.updateSafetyDiary(safetyDiary);

        // 2-1. 작업현황 삭제
        if (deletedWorkList.size() > 0) {
            log.info("2. modifySafetyDiary - 안전일지 삭제 (작업현황) ");

            List<String> deletedWorkIds = deletedWorkList.stream()
                    .map(elem -> elem.get("workId").toString())
                    .collect(Collectors.toList());
            safetyDiaryService.deleteSafetyDiaryWorkStatus(Map.of(
                    "safeDiaryId", safeDiaryId,
                    "usrId", usrId,
                    "deletedWorkIds", deletedWorkIds
            ));
        }

        // 2-2. 작업현황 추가/수정
        log.info("2. modifySafetyDiary - 안전일지 추가/수정 (작업현황) ");
        for (Map work : workList) {

            if (work.get("workId") != null) {
                // 수정
                work.put("safeDiaryId", safeDiaryId);
                work.put("usrId", usrId);
                safetyDiaryService.updateSafetyDiaryWorkStatus(work);
            } else {
                // 추가
                work.put("workId", UUID.randomUUID().toString());
                work.put("safeDiaryId", safeDiaryId);
                work.put("usrId", usrId);
                safetyDiaryService.addSafetyDiaryWorkStatus(work);
            }
        }


        // 3-1. 순회/점검 결과 삭제
        if (deletedSafetyList.size() > 0) {
            log.info("3. modifySafetyDiary - 안전일지 삭제 (순회/점검) ");
            List<String> deletedSafetyIds = deletedSafetyList.stream()
                    .map(elem -> elem.get("checkId").toString())
                    .collect(Collectors.toList());
            safetyDiaryService.deleteSafetyDiaryPatrolStatus(Map.of(
                    "safeDiaryId", safeDiaryId,
                    "usrId", usrId,
                    "deletedSafetyIds", deletedSafetyIds
            ));
        }

        // 3-2. 순회/점검 결과 추가/수정
        log.info("3. modifySafetyDiary - 안전일지 추가/수정 (순회/점검) ");
        for (Map patrol : safetyList) {
            if (patrol.get("checkId") != null) {
                // 수정
                patrol.put("safeDiaryId", safeDiaryId);
                patrol.put("usrId", usrId);
                safetyDiaryService.updateSafetyDiaryPatrolStatus(patrol);
            } else {
                // 추가
                patrol.put("checkId", UUID.randomUUID().toString());
                patrol.put("safeDiaryId", safeDiaryId);
                patrol.put("usrId", usrId);
                safetyDiaryService.addSafetyDiaryPatrolStatus(patrol);
            }
        }

        // 4. 첨부파일 로직 시작
        log.info("4. modifySafetyDiary - 첨부파일 로직 시작 ");
        List<FileService.FileMeta> fileList = report.getFileList();
        if (fileList != null && fileList.size() > 0) {
            FileService.FileMeta newMeta = null;
            String cntrctNo = report.getSafetyDiary().getCntrctNo();
            String savePath = String.format("%s/%s", uploadPath, getUploadPathByWorkType(FileUploadType.SAFETY, cntrctNo));

            // 안전일지 - 첨부문서 존재 확인
            Integer atchFileNo = report.getSafetyDiary().getAtchFileNo();
            if (atchFileNo == null) {
                // 기존 파일첨부번호가 없다면 신규 채번 + 업데이트처리
                atchFileNo = safetyDiaryService.getSafetyDiaryAttachmentMaxFileNo() + 1;
                Map<String, Object> param = new HashMap<>();
                param.put("cntrctNo", cntrctNo);
                param.put("atchFileNo", atchFileNo);
                param.put("safeDiaryId", safeDiaryId);
                param.put("usrId", usrId);

                safetyDiaryService.updateSafetyDiaryAttachmentFileNo(param);
            }
            Integer sno = safetyDiaryService.getSafetyDiaryAttachmentMaxSno(Map.of("fileNo", atchFileNo));

            for (FileService.FileMeta meta : fileList) {

                if ("C".equals(meta.getMode())) {
                    // 구분 "입력" 일경우 - INSERT
                    String metaString = null;
                    FileService.FileMeta tempRvwPhotoFileMeta = null;
                    try {
                        metaString = objectMapper.writeValueAsString(meta);
                        newMeta = fileService.build(metaString, savePath);             // 실제 물리파일 처리
                        tempRvwPhotoFileMeta =objectMapper.readValue(metaString, FileService.FileMeta.class);
                    } catch (JsonProcessingException e) {
                        throw new GaiaBizException(ErrorType.INTERNAL_SERVER_ERROR,"JsonParsing failed : {}",e.getMessage());
                    }

                    CwAttachments cwFile = new CwAttachments();
                    cwFile.setFileNo(atchFileNo);
                    cwFile.setSno(sno);
                    cwFile.setFileNm(newMeta.getOriginalFilename());
                    cwFile.setFileDiskNm(newMeta.getFileName());
                    cwFile.setFileDiskPath(newMeta.getDirPath());
                    cwFile.setFileSize(newMeta.getSize());
                    cwFile.setFileDiv("F");
                    cwFile.setRgstrId(usrId);
                    cwFile.setChgId(usrId);
                    safetyDiaryService.addSafetyDiaryAttachment(cwFile);            // 메타 테이블 INSERT
                    fileService.moveFile(tempRvwPhotoFileMeta.getFilePath(), newMeta.getFilePath()); // Temp ->  리얼폴더로 이동
                    log.info("4. modifySafetyDiary {} - 첨부파일 추가 :{}", atchFileNo, cwFile.getFileDiskNm());
                } else if ("D".equals(meta.getMode())) {

                    // 구분 "삭제" 일경우 - 메타 테이블 DELETE
                    Map<String, Object> delMap = new HashMap<>();
                    delMap.put("fileName", meta.getFileName());
                    delMap.put("fileNo", atchFileNo);
                    delMap.put("usrId", usrId);

                    safetyDiaryService.deleteSafetyDiaryAttachment(delMap);
                    log.info("4. modifySafetyDiary {} - 첨부파일 삭제 :{}", atchFileNo, meta.getFileName());
                }

                sno += 1;
            }
        }

    }

    /**
     * 안전일지 삭제
     * @param commonReqVo
     * @param diaryList
     */
    @Transactional
    public void deleteSafetyDiary(CommonReqVo commonReqVo, List<Map<String, Object>> diaryList) {
        String usrId = commonReqVo.getUserId();

        if (diaryList.size() > 0) {
            for (Map<String, Object> diary : diaryList) {
                diary.put("usrId", usrId);

                // 안전일지 - 본문
                safetyDiaryService.deleteSafetyDiary(diary);

                // 안전일지 - 작업현황
                safetyDiaryService.deleteSafetyDiaryWorkStatus(diary);

                // 안전일지 - 안전현황
                safetyDiaryService.deleteSafetyDiaryPatrolStatus(diary);

                // 안전일지 - 첨부파일 논리삭제
                if (diary.get("atchFileNo") != null) {

                    diary.put("fileNo", Integer.parseInt(MapUtils.getString(diary, "atchFileNo")));
                    safetyDiaryService.deleteSafetyDiaryAttachmentsByFileNo(diary);
                }

                // 전자결재 문서 및 삭제
                if (diary.get("apDocId") != null) {
                    approvalRequestService.deleteApDoc(MapUtils.getString(diary, "apDocId"));
                }
            }
        }

    }

    /**
     * 안전일지 전자결재 - 승인요청
     * @param commonReqVo
     * @param diaryList
     */
    @Transactional
    public void requestApprovalSafetyDiary(CommonReqVo commonReqVo, List<Map<String, Object>> diaryList) {

        // 1. 공통 파라미터 SET
        String usrId = commonReqVo.getUserId();
        String pjtNo = commonReqVo.getPjtNo();
        String cntrctNo = MapUtils.getString(diaryList.getFirst(), "cntrctNo");

        Map<String, Object> checkParams = new HashMap<>();
        checkParams.put("pjtNo", pjtNo);
        checkParams.put("cntrctNo", cntrctNo);
        checkParams.put("apType", "14");


        // 2. 순회 하며 수행
        for (Map<String, Object> diary : diaryList) {
            String safetyDiaryId = diary.get("safetyDiaryId").toString();

            // 3. 결재상태 업데이트
            Map<String, Object> apMap = new HashMap<>();
            apMap.put("apprvlStats", "E");
            safetyDiaryIntegrationService.updateApprovalStatus(safetyDiaryId, cntrctNo, usrId, apMap);

            // 4. 안전일지/현황 조회
            Map<String, Object> findSafetyDiary = this.getSafetyDiary(cntrctNo, safetyDiaryId);

            // 전결 전달 - 안전일지
            Map<String, Object> safetyDiary = (Map<String, Object>) findSafetyDiary.get("safeDiary");
            if (safetyDiary == null) {
                throw new GaiaBizException(ErrorType.NOT_FOUND, "안전일지 정보를 찾을 수 없습니다.");
            }

            // 전결 전달 - 승인요청 시 필요 데이터
            safetyDiary.put("pjtNo", pjtNo);
            safetyDiary.put("cntrctNo", cntrctNo);
            safetyDiary.put("isApiYn", commonReqVo.getApiYn());
            safetyDiary.put("pjtDiv", commonReqVo.getPjtDiv());
            safetyDiary.put("usrId", usrId);


            // 전결 전달 - 안전현황, 작업현황, 교육현황, 재해현황
            Map<String, Object> resourceMap = new HashMap<>();
            // 안전일지 - 작업 현황
            if (findSafetyDiary.get("workList") != null) resourceMap.put("workList", findSafetyDiary.get("workList"));
            // 안전일지 - 점검 현황
            if (findSafetyDiary.get("patrolList") != null) resourceMap.put("patrolList", findSafetyDiary.get("patrolList"));

            checkParams.put("repoDt", MapUtils.getString(safetyDiary, "repo_dt"));
            // 교육일지 - 현황 및 참석인원
            Map<String, Object> eduStatus = safetyDiaryIntegrationService.selectEducationDiaryByRepoDt(checkParams);
            if (eduStatus != null) {
                if (eduStatus.get("eduList") != null) resourceMap.put("eduList", eduStatus.get("eduList"));
                if (eduStatus.get("eduPersonList") != null) resourceMap.put("eduPersonList", eduStatus.get("eduPersonList"));
            }

            // 재해일지 - 현황 및 참석인원
            Map<String, Object> disasterStatus = safetyDiaryIntegrationService.selectDisasterDiaryByRepoDt(checkParams);
            if (disasterStatus != null) {
                if (disasterStatus.get("disasterList") != null) resourceMap.put("disasterList", disasterStatus.get("disasterList"));
                if (disasterStatus.get("disasterPersonList") != null) resourceMap.put("disasterPersonList", disasterStatus.get("disasterPersonList"));
            }

            // 5. 전달
            approvalRequestService.insertSafetyDiaryDoc(safetyDiary, cntrctNo, resourceMap);
        }

    }

    /**
     * 안전일지 전자결재 - 승인 취소
     *  1. 전자결재 승인 취소처리
     *  2. 통합문서 삭제 처리
     * @param commonReqVo
     * @param diaryList
     */
    @Transactional
    public void cancelSafetyDiaryApproval(CommonReqVo commonReqVo, List<Map<String, Object>> diaryList) {
        String usrId = commonReqVo.getUserId();
        try {
            // 1-1. 전자결재 승인취소 파라미터 준비
            List<String> apDocIds = diaryList
                    .stream().map(elem -> (String) elem.get("apDocId"))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            log.info("안전일지 전자결재 승인 취소 AP_DOC_ID 목록 = {}", apDocIds);
            if (apDocIds.isEmpty()) {
                log.warn("안전일지 전자결재 승인 취소 AP_DOC_ID가 없습니다.");
                return;
            }
            Map<String, Object> reqVoMap = new HashMap<>();
            reqVoMap.put("apiYn", commonReqVo.getApiYn());
            reqVoMap.put("pjtDiv", commonReqVo.getPjtDiv());

            List<ApDoc> deleteList = apDocIds.stream()
                    .filter(Objects::nonNull) // null 값 필터링
                    .map(apDocId -> {
                        ApDoc apDoc = new ApDoc();
                        apDoc.setApDocId(apDocId);
                        apDoc.setApType(EapprovalHelper.SAFETY_DIARY_DOC); // 안전일지 문서 타입 설정
                        return apDoc;
                    })
                    .collect(Collectors.toList());

            // 1-2. 전자결재 승인 취소
            if (!deleteList.isEmpty()) {
                draftComponent.setDeleteList(deleteList, reqVoMap);
            }

            // 2-1. 통합문서 삭제 파라미터 준비
            List<String> docIds = diaryList
                    .stream().map(elem -> (String) elem.get("docId"))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            log.info("안전일지 통합문서 초기화 DOC_ID 목록 = {}", docIds);
            if (docIds.isEmpty()) {
                log.warn("안전일지 통합문서 초기화 DOC_ID가 없습니다.");
                return;
            } else {
                // 2-1. 통합문서 삭제
                Map<String, Object> docParam = new HashMap<>();
                docParam.put("docIds", docIds);
                docParam.put("usrId", usrId);
                Result docResult = documentServiceClient.removeDocument(docParam);

                if (!docResult.isOk()) throw new GaiaBizException(ErrorType.INTERNAL_SERVER_ERROR, "안전일지 통합문서 초기화 중 에러발생");
            }

        } catch (GaiaBizException e) {
            log.error("안전일지 승인 취소 중 오류 발생 error = {}", e.getMessage());
        }

    }

    /**
     * 안전일지 첨부파일 다운로드
     * @param fileNo
     * @param sno
     * @return
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
}
