package kr.co.ideait.platform.gaiacairos.comp.construction;

import kr.co.ideait.platform.gaiacairos.comp.construction.service.DailyReportAsyncService;
import kr.co.ideait.platform.gaiacairos.comp.construction.service.DailyreportService;
import kr.co.ideait.platform.gaiacairos.comp.eapproval.DraftComponent;
import kr.co.ideait.platform.gaiacairos.comp.eapproval.helper.EapprovalHelper;
import kr.co.ideait.platform.gaiacairos.comp.eapproval.service.ApprovalRequestService;
import kr.co.ideait.platform.gaiacairos.comp.project.service.InformationService;
import kr.co.ideait.platform.gaiacairos.comp.system.service.CommonCodeService;
import kr.co.ideait.platform.gaiacairos.comp.system.service.DocumentManageService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractComponent;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.*;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.construction.DailyreportMybatisParam;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.construction.dailyreport.DailyreportForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.util.restclient.DocumentServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
public class DailyreportComponent extends AbstractComponent {

    @Autowired
    DailyreportService dailyreportService;

    @Autowired
    ApprovalRequestService approvalRequestService;

    @Autowired
    InformationService informationService;

    @Autowired
    DailyReportAsyncService dailyReportAsyncService;

    @Autowired
    DraftComponent draftComponent;

    @Autowired
    DocumentServiceClient documentServiceClient;

    /**
     * 작업 일지 리스트 가져오기
     *
     * @param cntrctNo
     * @param year
     * @param month
     * @param status
     * @param searchText
     * @return
     */
    public List selectDailyreportList(String cntrctNo, String year, String month, String status, String searchText) {
        return dailyreportService.selectDailyreportList(cntrctNo, year, month, status, searchText);
    }

    /**
     * 작업 일지 추가
     *
     * @param dailyreportInsert
     * @return
     */
    @Transactional
    public CwDailyReport addDailyReport(CwDailyReport dailyreportInsert) {
        CwDailyReport result = dailyreportService.addDailyReport(dailyreportInsert);
        CwDailyReportActivity activity = new CwDailyReportActivity();

        activity.setDailyReportId(result.getDailyReportId());
        activity.setCntrctNo(result.getCntrctNo());

        activity.setWorkDtType("TM");

        String dt = result.getDailyReportDate();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date formatDate = null;
        String strNewFormatDate = null;
        try {
            formatDate = formatter.parse(dt);
            Calendar cal = Calendar.getInstance();
            cal.setTime(formatDate);
            cal.add(Calendar.DATE, 1);

            strNewFormatDate = formatter.format(cal.getTime());
        } catch (ParseException e) {
            log.error("SimpleDateFormat formating failed : {}",e.getMessage());
        }


        dailyreportService.addDefaultActivity(activity, strNewFormatDate);

        activity.setWorkDtType("TD");
        dailyreportService.addDefaultActivity(activity, result.getDailyReportDate());
        dailyreportService.addQdb(activity);
        dailyreportService.addResource(activity, strNewFormatDate);

        dailyreportService.updateRate(result);

        return result;
    }

    /**
     * 작업 일지 보고서 번호 중복 확인
     *
     * @param cntrctNo
     * @param reportNo
     * @return
     */
    public boolean chkReportNo(String cntrctNo, String reportNo) {
        return dailyreportService.chkReportNo(cntrctNo, reportNo);
    }

    /**
     * 작업 일지 보고일자 중복 확인
     *
     * @param cntrctNo
     * @param dailyReportDate
     * @return
     */
    public boolean chkDailyReportDate(String cntrctNo, String dailyReportDate) {
        return dailyreportService.chkDailyReportDate(cntrctNo, dailyReportDate);
    }

    /**
     * 작업일지 개요 조회
     *
     * @param cntrctNo
     * @param dailyReportId
     * @return
     */
    public CwDailyReport getSummary(String cntrctNo, Long dailyReportId) {
        return dailyreportService.getDailyReports(cntrctNo, dailyReportId);
    }

    /**
     * 작업일지 Activity 조회
     *
     * @param cntrctNo
     * @param dailyReportId
     * @param dailyReportDate
     * @param workDtType
     * @return
     */
    public List<Map<String,Object>> getActivity(String cntrctNo, Long dailyReportId, String dailyReportDate, String workDtType) {
        log.info("getActivity: 액티비티 조회. cntrctNo = {}, dailyReportId = {}, dailyReportDate = {}, worworkDtType = {}", cntrctNo, dailyReportId, dailyReportDate, workDtType);

        List<Map<String,Object>> activity = dailyreportService.getActivity(cntrctNo, dailyReportId, dailyReportDate, workDtType);
        log.info("getActivity: 일반 액티비티 조회 건 수 = {}", activity.size());

        List resultList = mergeAndFilterActivities(activity, cntrctNo, dailyReportId, workDtType);

        return resultList;
    }

    /**
     * 작업일지 자재, 노무, 경비 조회
     *
     * @param cntrctNo
     * @param dailyReportId
     * @param rsceTpCd
     * @return
     */
    public List<Map<String,Object>> getResource(String cntrctNo, Long dailyReportId, String rsceTpCd) {
        // 수동 추가한 자원 조회
        List<Map<String,Object>> accumulated = dailyreportService.getResourceManualAccumulatedResourc(cntrctNo, dailyReportId, rsceTpCd);

        // 일반 자원 조회
        List<Map<String,Object>> resource = dailyreportService.getResource(cntrctNo, dailyReportId, rsceTpCd);

        // 최종 리스트, 전체 자원 우선 할당
        List<Map<String,Object>> merged = new ArrayList<>(resource);

        // resource를 rsce_cd 기준으로 Map화 (빠른 접근용)
        Map<String, Map<String, Object>> mergedMap = merged.stream()
                .collect(Collectors.toMap(
                        r -> (String) r.get("rsce_cd"),
                        r -> r,
                        (existing, replacement) -> existing // 같은 key 나오면 덮어쓰기(existing: resource, replacement: accumulated)
                ));
        log.info("getResource:  mergedMap = {}", mergedMap);

        // 전체(resource)에서 수동(accumulated) 데이터 덮어쓰기 (rsce_nm, spec_nm, unit만)
        for (Map<String,Object> acc : accumulated) {
            String rsceCd = (String) acc.get("rsce_cd");
            if (rsceCd == null) continue;
            Map<String, Object> target = mergedMap.get(rsceCd);
            if (target != null) {
                target.put("rsce_nm", acc.get("rsce_nm"));
                target.put("spec_nm", acc.get("spec_nm"));
                target.put("unit", acc.get("unit"));
            } else {
                // 겹치는게 없으면 그대로 추가
                merged.add(acc);
                mergedMap.put(rsceCd, acc);
            }
        }
        log.info("getResource: 현재 자원에서 수동 자원 필터링한 결과 ({})건. result= {}", merged.size(), merged);


        return merged;
    }

    /**
     * 공정 사진 조회
     *
     * @param cntrctNo
     * @param dailyReportId
     * @return
     */
    public List<?> getPhoto(String cntrctNo, Long dailyReportId) {
        return dailyreportService.getPhoto(cntrctNo, dailyReportId);
    }

    /**
     * 작업일지 수정
     *
     * @param dailyReportActivity
     * @param dailyReportResource
     * @param dailyReportData
     * @return
     */
    @Transactional
    public void updateDailyReport(List<CwDailyReportActivity> dailyReportActivity,
            List<CwDailyReportResource> dailyReportResource, CwDailyReport dailyReportData) {
//         this.updateDailyReportActivity(dailyReportActivity);

        // null → BigDecimal.ZERO
        if (dailyReportResource != null) {
            dailyReportResource.forEach(res -> {
                if (res.getTotalQty() == null)  res.setTotalQty(BigDecimal.ZERO);
                if (res.getActualQty() == null) res.setActualQty(BigDecimal.ZERO);
                if (res.getAcmtlQty() == null)  res.setAcmtlQty(BigDecimal.ZERO);
                if (res.getRemndrQty() == null) res.setRemndrQty(BigDecimal.ZERO);
            });
        }
        dailyreportService.updateDailyReportResource(dailyReportResource);
        this.updateDailyReportSummary(dailyReportData);
    }

    /**
     * 작업일지 개요 수정
     *
     * @param cwDailyReport
     * @return
     */
    @Transactional
    public void updateDailyReportSummary(CwDailyReport cwDailyReport) {
        dailyreportService.updateRate(cwDailyReport);
    }

    /**
     * 작업일지 Activity 수정
     * 작업일지 상세 화면에서 저장 버튼 클릭 시는 액티비티의 종료날짜, 진행상태 값 수정
     * 작업일지 상세 -> 금일실적 변경에서 액티비티 추가 삭제 시는 액티비티 추가/삭제(dlt_yn) 및 종료날짜, 진행상태 값 수정
     * @param dailyReportActivity
     */
    @Transactional
    public void updateDailyReportActivity(List<CwDailyReportActivity> dailyReportActivity) {

        try {
            log.info("updateDailyReportActivity: 액티비티 업데이트 진행");

            dailyReportActivity.forEach(id -> {
                // TM이면 체크, activityId가 동일한 TD 데이터가 0101(완료)인 경우 TM도 업데이트 해주기 때문에
                // TM을 또 업데이트할 필요 없음
                if ("TM".equals(id.getWorkDtType())) {
                    boolean hasTDwithDone = dailyReportActivity.stream()
                            .anyMatch(td ->
                                    td.getActivityId().equals(id.getActivityId()) &&
                                            "TD".equals(td.getWorkDtType()) &&
                                            "0101".equals(td.getPstats())
                            );

                    if (hasTDwithDone) {
                        log.info("updateDailyReportActivity: TM({})  대응되는 TD가 완료(0101)라서 무시", id.getActivityId());
                        return;
                    }
                }
                Map<String, Object> resultMap = dailyreportService.selectActivityByCntrctNoAndDailyReportIdAndDailyActivityId(id);

                if (resultMap != null) {
                    dailyreportService.updateActivityData(id, "TDTM");

                } else if (resultMap == null && id.getDltYn().toString().equals("N")) {
                    /* 금일실적 변경 - 액티비티 추가 경우 */
                    // 금일 추가할 경우 명일도 추가
                    id.setRgstrId(UserAuth.get(true).getUsrId());
                    id.setChgId(UserAuth.get(true).getUsrId());

                    dailyreportService.addDailyReportActivity(id);
                }

                // TM(명일 액티비티) TD(금일) 데이터와 맞추기 위해 pstats, actualBgnDate, actualEndDate 업데이트
                if("TD".equals(id.getWorkDtType()) && "0101".equals(id.getPstats())) {
                    dailyreportService.updateActivityData(id, "TM");

                    // 이전, 이후에 등록되어 있는 액티비티 완료처리
                   dailyreportService.updateActivityWithPreviousReports(id);
                }
                if(resultMap != null && "TD".equals(id.getWorkDtType()) && !"0101".equals(id.getPstats()) && "N".equals(id.getDltYn())) {
                    // 완료 처리했던 금일 액티비티 진행중으로 바꾼 경우
                    // 금일 액티비티 삭제한 경우 명일 액티비티도 삭제
                    dailyreportService.updateActivityData(id, "TM");
                }

            });
        } catch (RuntimeException e) {
            throw new GaiaBizException(ErrorType.ETC, "액티비티 업데이트 중 알 수 없는 오류 발생. error = {}", e.getMessage(), e);

        }
    }

    /**
     * 공정사진 데이터 저장
     *
     * @param cwAttachments
     * @param cwDailyReportPhoto
     * @param seq
     */
    @Transactional
    public void createDailyReportPhoto(CwAttachments cwAttachments, CwDailyReportPhoto cwDailyReportPhoto, Integer seq) {
        log.info("createDailyReportPhoto: 공정 사진 추가, cwAttachments = {}, cwDailyReportPhoto = {}", cwAttachments,
                cwDailyReportPhoto);
        // 현재 cw_attachments 에 저장되어 있는 file_no max로 초기화
        Integer fileNo = dailyreportService.findMaxFileNo() + 1;
        Integer sno = 1;
        // cwAttachments.setFileNo(maxFileNo + 1);

        // 이전에 저장한 공정사진 유무 확인, 있으면 이전 파일과 동일한 file_no으로 초기화
        List<CwDailyReportPhoto> cwDailyReportPhotoOld = dailyreportService.getDailyReportPhotos(
                cwDailyReportPhoto.getCntrctNo(), Long.valueOf(cwDailyReportPhoto.getDailyReportId()));

        if (cwDailyReportPhotoOld.isEmpty() == false) {
            log.info("createDailyReportPhoto: 이전에 저장한 공정 사진 유무 확인, cwDailyReportPhotoOld = {}", cwDailyReportPhotoOld);
            fileNo = cwDailyReportPhotoOld.get(0).getAtchFileNo();

            // max sno 찾기, sno: 이미지 파일이 여러개일 경우 sno으로 카운팅함
            sno = dailyreportService.selectMaxSno(fileNo);
            log.info("createDailyReportPhoto: 이전에 저장한 공정 사진 sno = {}", sno);
            sno = (sno != null) ? sno + 1 : 1;
        }

        cwAttachments.setFileNo(fileNo);
        cwAttachments.setSno(sno);
        // CwAttachments cwAttachmentsData =
        // cwAttachmentsRepository.findByFileNoAndSno(cwAttachments.getFileNo(),
        // cwAttachments.getSno());
        //
        // if (cwAttachmentsData != null) {
        // cwAttachmentsRepository.updateDelete(cwAttachments);
        // } else {
        // CwAttachments r = cwAttachmentsRepository.save(cwAttachments);
        // cwDailyReportPhoto.setAtchFileNo(r.getFileNo());
        // }

        CwAttachments r = dailyreportService.saveCwAttachments(cwAttachments);
        log.info("createDailyReportPhoto: cwAttachments 저장 성공, cwAttachments= {}", cwAttachments);
        cwDailyReportPhoto.setAtchFileNo(r.getFileNo());

        // cnstty_pht_sno 공종 사진 순번 max 값 찾기
        // Integer maxId = cwDailyReportPhotoRepository.findMaxId();

        CwDailyReportPhoto cwDailyReportPhotoData = dailyreportService.findByCntrctNoAndDailyReportIdAndCnsttyPhtSno(
                cwDailyReportPhoto.getCntrctNo(),
                cwDailyReportPhoto.getDailyReportId(), cwDailyReportPhoto.getCnsttyPhtSno()
        );

        if (cwDailyReportPhotoData != null) {
            log.info("createDailyReportPhoto: cwDailyReportPhotoData 조회 성공, cwDailyReportPhotoData = {}");
            dailyreportService.updateDeleteCwDailyReportPhoto(cwDailyReportPhoto);
        } else {
            log.info("createDailyReportPhoto: cwDailyReportPhotoData 조회 결과 없음",
                    cwDailyReportPhotoData);
            cwDailyReportPhoto.setCnsttyPhtSno(sno);
            cwDailyReportPhoto.setRgstDt(LocalDateTime.now());
            cwDailyReportPhoto.setRgstrId(UserAuth.get(true).getUsrId());
            dailyreportService.saveCwDailyReportPhoto(cwDailyReportPhoto);
            log.info("createDailyReportPhoto: cwDailyReportPhoto 저장 성공, cwDailyReportPhoto = {}", cwDailyReportPhoto);
        }

//        dailyreportService.createDailyReportPhoto(cwAttachments, cwDailyReportPhoto, seq);
    }

    /**
     * 공정사진 삭제
     *
     * @param cwAttachments
     * @param cwDailyReportPhoto
     */
    @Transactional
    public void deleteDailyReportPhoto(CwAttachments cwAttachments, CwDailyReportPhoto cwDailyReportPhoto) {


        DailyreportMybatisParam.dailyreportAttAchmentsDataOutput attachmentsData =
                dailyreportService.selectAttachmentsByFileNo(cwAttachments);

        CwDailyReportPhoto cwDailyReportPhotoData =
                dailyreportService.findByCntrctNoAndDailyReportIdAndCnsttyPhtSno(cwDailyReportPhoto.getCntrctNo(),
                        cwDailyReportPhoto.getDailyReportId(), cwDailyReportPhoto.getCnsttyPhtSno());

        if (attachmentsData != null) {
            log.info("deleteDailyReportPhoto: attachmentsData 조회 성공, attachmentsData.getFileNo = {}",
                    attachmentsData.getFileNo());

            dailyreportService.updateDeleteAttachments(attachmentsData.getFileNo());
        }
        if (cwDailyReportPhotoData != null) {
            log.info("deleteDailyReportPhoto: cwDailyReportPhotoData 조회 성공, cwDailyReportPhotoData.getActivityId = {}",
                    cwDailyReportPhotoData.getActivityId());
            cwDailyReportPhoto.setActivityId(cwDailyReportPhotoData.getActivityId());
            cwDailyReportPhoto.setTitlNm(cwDailyReportPhotoData.getTitlNm());
            cwDailyReportPhoto.setDscrpt(cwDailyReportPhotoData.getDscrpt());
            cwDailyReportPhoto.setShotDate(cwDailyReportPhotoData.getShotDate());
            cwDailyReportPhoto.setRgstDt(cwDailyReportPhotoData.getRgstDt());
            cwDailyReportPhoto.setRgstrId(cwDailyReportPhotoData.getRgstrId());
            dailyreportService.updateDeleteCwDailyReportPhoto(cwDailyReportPhoto);
        }

//        dailyreportService.deleteDailyReportPhoto(cwAttachments, cwDailyReportPhoto);
    }

    /**
     * 금일실적 변경 > pr_activity 목록 가져오기
     *
     * @param cntrctNo
     * @param dailyReportId
     * @param workDtType
     * @param planStart
     * @param planFinish
     * @param searchText
     * @return
     */
    public List selectPrActivityList(String cntrctNo, Long dailyReportId, String workDtType, String planStart,
                                     String planFinish, String searchText) {
        DailyreportMybatisParam.DailyreportFormTypeSelectInput dailyreportFormTypeSelectInput = new DailyreportMybatisParam.DailyreportFormTypeSelectInput();

        dailyreportFormTypeSelectInput.setCntrctNo(cntrctNo);
        dailyreportFormTypeSelectInput.setDailyReportId(dailyReportId);
        dailyreportFormTypeSelectInput.setWorkDtType(workDtType);
        dailyreportFormTypeSelectInput.setPlanStart(planStart);
        dailyreportFormTypeSelectInput.setPlanFinish(planFinish);
        dailyreportFormTypeSelectInput.setSearchText(searchText);


        CwDailyReport cwDailyReport = dailyreportService.getDailyReports(cntrctNo, dailyReportId);

        if (workDtType.equals("TD")) {
            dailyreportFormTypeSelectInput.setDailyReportDate(cwDailyReport.getDailyReportDate());
        } else {
            String dt = cwDailyReport.getDailyReportDate();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
            Date formatDate = null;
            try {
                formatDate = formatter.parse(dt);

                Calendar cal = Calendar.getInstance();
                cal.setTime(formatDate);
                cal.add(Calendar.DATE, 1);
                String strNewFormatDate = formatter.format(cal.getTime());

                dailyreportFormTypeSelectInput.setDailyReportDate(strNewFormatDate);
            } catch (ParseException e) {
                log.error("SimpleDateFormat formating failed : {}",e.getMessage());
            }
        }

        // 일반 pr액티비티 조회
        List<Map<String,Object>> prActivity = dailyreportService.selectPrActivityList(dailyreportFormTypeSelectInput);

        // 승인 완료 처리된 액티비티
        List<Map<String,Object>> finishActivity = dailyreportService.getFinishActivity(cntrctNo, null);
        log.info("selectPrActivityList: 승인 완료된 액티비티 조회 건 수 = {}", finishActivity.size());

        // finishActivity 에서 제외할 activity_id 수집
        Set<String> finishedIds = finishActivity.stream()
                .map(m -> Objects.toString(m.get("activity_id"), null))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // prActivity 리스트에서 제거
        prActivity.removeIf(m -> finishedIds.contains(Objects.toString(m.get("activity_id"), null)));
        return prActivity;
    }

    /**
     * 금일실적 변경 > 금일 실적 activity 가져오기
     *
     * @param cntrctNo
     * @param dailyReportId
     * @param workDtType
     * @return
     */
    public List selectDailyReportActivityListforChange(String cntrctNo, Long dailyReportId, String workDtType) {
        List<Map<String,Object>> changeActivity = dailyreportService.selectDailyReportActivityListforChange(cntrctNo, dailyReportId, workDtType);

        List resultList = mergeAndFilterActivities(changeActivity, cntrctNo, dailyReportId, workDtType);
        return resultList;
    }

    // 기존 액티비티 + 이전 보고서에 저장된 진행중인 액티비티 병합 및 승인 완료된 액티비티 제거하는 메서드
    private List mergeAndFilterActivities(List<Map<String,Object>> activity, String cntrctNo, Long dailyReportId, String workDtType) {
        // 이전 보고서에 등록된 진행중인 액티비티
        List<Map<String,Object>> accumulatedActivity = dailyreportService.getAccumulatedActivity(cntrctNo, dailyReportId, workDtType);
        log.info("mergeAndFilterActivities: 누적 액티비티 조회 건 수 = {}", accumulatedActivity.size());

        // 승인 완료 처리된 액티비티
        List<Map<String,Object>> finishActivity = dailyreportService.getFinishActivity(cntrctNo, dailyReportId);
        log.info("mergeAndFilterActivities: 승인 완료된 액티비티 조회 건 수 = {}", finishActivity.size());

        // finishActivity 에서 제외할 activity_id 수집
        Set<String> finishedIds = finishActivity.stream()
                .map(m -> Objects.toString(m.get("activity_id"), null))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // 기존 액티비티 + 누적 액티비티
        Map<String, Map<String, Object>> mergedMap =
                Stream.concat(accumulatedActivity.stream(), activity.stream() )
                        .collect(Collectors.toMap(
                                a -> String.valueOf(a.get("activity_id")),
                                a -> a, (oldVal, newVal) -> newVal // 중복 발생 시 activity(뒤쪽) 데이터 유지
                        ));
        log.info("mergeAndFilterActivities: 병합 건 수 = {}", mergedMap.size());


        // finishActivity(승인 완료된) 데이터 제거
//        mergedMap.entrySet().removeIf(e -> finishedIds.contains(e.getKey()));
//        log.info("mergeAndFilterActivities: 필터링 후 건 수 = {}", mergedMap.size());

        return new ArrayList<>(mergedMap.values());
    }

    /**
     * 금일실적 변경 > 금일 실적 activity 가져오기
     *
     * @param cntrctNo
     * @param dailyReportId
     * @param workDtType
     * @return
     */
    public List selectTodayDailyReportQdbList(String cntrctNo, Long dailyReportId, String workDtType) {
        return dailyreportService.selectTodayDailyReportQdbList(cntrctNo, dailyReportId, workDtType);
    }

    /**
     * 금일실적 변경 > 금일 실적 activity 가져오기
     *
     * @param cntrctNo
     * @param dailyReportId
     * @return
     */
    public List selectTodayDailyReportResourceList(String cntrctNo, Long dailyReportId) {
        return dailyreportService.selectTodayDailyReportResourceList(cntrctNo, dailyReportId);
    }

    /**
     * 작업일지 상태 업데이트
     *
     * @param cwDailyReports
     */
    @Transactional
    public void updateDailyReportList(List<CwDailyReport> cwDailyReports, String isApiYn, String pjtDiv) {
        cwDailyReports.forEach(id -> {
            CwDailyReport cwDailyReport = dailyreportService.getDailyReports(id.getCntrctNo(), id.getDailyReportId());

            if (cwDailyReport != null) {
                if (id.getApprvlStats().equals("E")) {

                    // 첫번째 결재자가 pgaia 사용자인지 체크
//                    boolean isPgaiaUser = dailyreportService.checkPgaiaFirstApprover(cwDailyReport);

                    // pgaia사용자 + 카이로스 플랫폼 + pgaia 프로젝트면 API통신 진행
//                    boolean toApi = isPgaiaUser && "cairos".equals(platform) && "Y".equals(isApiYn) && "P".equals(pjtDiv);

                    // 전자결재에서 호출
                    // dailyreportService.updateApprovalStatus(cwDailyReport, UserAuth.get(true).getUsrId(), "E");

                    // 승인요청 시 필요 데이터
			        Map<String, Object> requestMap = new HashMap<>();
			        requestMap.put("pjtNo", UserAuth.get(true).getPjtNo());
			        requestMap.put("cntrctNo", id.getCntrctNo());
			        requestMap.put("isApiYn", isApiYn);
			        requestMap.put("pjtDiv", pjtDiv);
			        requestMap.put("usrId", UserAuth.get(true).getUsrId());

                    Map<String, Object> resourceMap = dailyreportService.selectDailyreportResource(cwDailyReport.getCntrctNo(),
                                cwDailyReport.getDailyReportId());


                    approvalRequestService.insertDailyReportDoc(cwDailyReport, requestMap, resourceMap);
                }
                if (id.getApprvlStats().equals("A") || id.getApprvlStats().equals("R")) {
                    dailyreportService.updateByCntrctNoAndDailyReportIdAnyType(id);
                }
            }
        });
    }




    /**
     * 작업일지 삭제
     *
     * @param cwdailyreport
     */
    @Transactional
    public void delDailyReport(List<CwDailyReport> cwdailyreport) {
        cwdailyreport.forEach(id -> {
            CwDailyReport cwDailyReport = dailyreportService.getDailyReports(id.getCntrctNo(), id.getDailyReportId());

            // 작업일지 연계데이터 삭제
            dailyreportService.delDailyReportRefData(id.getCntrctNo(), id.getDailyReportId());

            /* 작업 일지 승인 요청 이후에는 삭제 불가 , 불 필요한 로직 by soulers. after 2025-07-10 delete
            if (cwDailyReport.getApDocId() != null) {
                approvalRequestService.deleteApDoc(cwDailyReport.getApDocId());
            }*/

            if (cwDailyReport != null) {
                dailyreportService.updateDeleteForDailyReport(cwDailyReport);
            }
        });
    }



    /**
     * 작업일지 QDB 추가
     *
     * @param dailyReportActivity
     */
    @Transactional
    public void insertDailyReportQdb(List<CwDailyReportActivity> dailyReportActivity) {
        dailyreportService.insertDailyReportQdb(dailyReportActivity);
    }

    /**
     * QDB 삭제
     *
     * @param dailyReportActivity
     */
    @Transactional
    public void deleteDailyReportTodayQdb(List<CwDailyReportActivity> dailyReportActivity) {
        dailyreportService.deleteDailyReportTodayQdb(dailyReportActivity);
    }

    /**
     * 작업일지 금일 명일 실적 변경
     *
     * @param dailyReportActivity
     * @param dailyReportResource
     * @param prActivity
     * @param cntrctNo
     * @param dailyReportId
     * @param dailyReportDate
     * @param workDtType
     */
    @Transactional
    public void updateDailyReportChage(List<CwDailyReportActivity> dailyReportActivity,
                                       List<CwDailyReportResource> dailyReportResource, List<CwDailyReportActivity> prActivity, String cntrctNo,
                                       Long dailyReportId, String dailyReportDate, String workDtType) {

        this.updateDailyReportActivity(dailyReportActivity);

        if (workDtType.equals("TD")) {
            this.setQdb(dailyReportActivity, prActivity);
            this.setResource(dailyReportResource, dailyReportActivity, cntrctNo, dailyReportId, dailyReportDate);

            CwDailyReport cwDailyReport = dailyreportService.getDailyReports(cntrctNo, dailyReportId);
            dailyreportService.updateRate(cwDailyReport);
        }
        // 금일실적에서 삭제해도 pr은 건드리지 않기로함.
        // dailyreportService.updatePrActivity(dailyreportInsert.dailyReportActivity);
    }

    /**
     * 금일실적 변경 내 QDB 수정.
     *
     * @param dailyReportActivity
     * @param prActivity
     * @return void
     * @throws
     */
    private void setQdb(List<CwDailyReportActivity> dailyReportActivity, List<CwDailyReportActivity> prActivity) {
        this.deleteDailyReportTodayQdb(dailyReportActivity);

        // pr 추가 건 QDB 저장.
        if (prActivity != null) {
            this.insertDailyReportQdb(prActivity);
        }
    }

    /**
     * 금일실적 변경 내 리소스 수정.
     *
     * @param dailyReportResource
     * @param dailyReportActivity
     * @param cntrctNo
     * @param dailyReportId
     * @param dailyReportDate
     */
    private void setResource(List<CwDailyReportResource> dailyReportResource,
                             List<CwDailyReportActivity> dailyReportActivity, String cntrctNo, Long dailyReportId,
                             String dailyReportDate) {
        // 수동 추가한 자원 제외하고 모든 자원 물리 삭제
        dailyreportService.deleteDailyReportResource(cntrctNo, dailyReportId);
//        cwDailyReportResourceRepository.deleteDailyReportResource(cntrctNo, dailyReportId);

        // 리소스 추가
        CwDailyReportActivity activity = new CwDailyReportActivity();
        activity.setCntrctNo(cntrctNo);
        activity.setDailyReportId(dailyReportId);

        dailyreportService.addResource(activity, dailyReportDate);

        // 작업일지 리소스 표시여부 업데이트
        if (dailyReportResource != null) {
            dailyReportResource.forEach(id -> {
                dailyreportService.updateDsplyByCntrctNoAndDailyReportIdAndRsceCd(id);
            });
        }
    }

    /**
     * 첨부파일 리스트 저장
     *
     */
    @Transactional
    public int createCwAttachmentsList(List<CwAttachments> cwAttachmentsList) {
        return dailyreportService.createCwAttachmentsList(cwAttachmentsList);
    }

    /**
     * selectBox, radio 상자 만들 데이터 가져오기
     *
     * @param
     * @return List
     * @throws
     */
    public List selectMakeDataListUsingCondition(String col1, String col2, String tName, String[] param,
                                                 String orderByCol, String orderByType) {
        return dailyreportService.selectMakeDataListUsingCondition(col1, col2, tName, param, orderByCol, orderByType);
    }

    // 현장 작업자 인력 정보 조회
    public List getDailyReportSiteLaborList(String cntrctNo, String dailyReportId, String searchText) {
        return dailyreportService.getDailyReportSiteLaborList(cntrctNo, dailyReportId, searchText);
    }

    public Boolean checkDailyReportExists(String cntrctNo, String dailyReportDate) {
        return dailyreportService.checkDailyReportExists(cntrctNo, dailyReportDate);
    }

    @Transactional
    public Map<String, String> saveResourceSummaryManually(List<DailyreportForm.ManualDailyReportResource> resourceList) {
        log.info("saveResourceSummaryManually: 작업일지  진행");
        Map<String, String> result = new HashMap<>();
        String usrId = UserAuth.get(true).getUsrId();

        List<Map<String, Object>> insertList = new ArrayList<>();
        List<Map<String, Object>> updateList = new ArrayList<>();
        
        // 일위대가 자원 추가
        List<Map<String, Object>> insertCbsList = new ArrayList<>(); 
        
        // cntrctChgId 조회
        String cntrctChgId = "";
        for (DailyreportForm.ManualDailyReportResource item : resourceList) {
            
            cntrctChgId = dailyreportService.selectCntrctChgIdByCntrctNo(item.getCntrctNo());
            log.info("saveResourceSummaryManually: cntrctChgId 조회 성공. {}", cntrctChgId);

            // 데이터 존재 여부 확인
            List<Map<String, Object>> rsceSnoList = dailyreportService.selectRsceSnoByCntrctNoAndDailyReportIdAndRsceCdAndRsceTpCd(item);
            log.info("saveResourceSummaryManually: rsceSnoList 조회 성공. {}", rsceSnoList);

            Map<String, Object> row = new HashMap<>();
            row.put("cntrctNo",item.getCntrctNo());
            row.put("dailyReportId",item.getDailyReportId());
            row.put("rsceTpCd",item.getRsceTpCd());
            row.put("rsceCd",item.getRsceCd());
            // total_qty null 방어 코드
            BigDecimal totalQty = item.getTotalQty();
            if (totalQty == null) {
                log.warn("saveResourceSummaryManually: totalQty가 null입니다. rsceCd = {}, rsceTpCd = {}", 
                        item.getRsceCd(), item.getRsceTpCd());
                totalQty = BigDecimal.ZERO;
            }
            
            // actual_qty null 방어 코드
            BigDecimal actualQty = item.getActualQty();
            if (actualQty == null) {
                log.warn("saveResourceSummaryManually: actualQty가 null입니다. rsceCd = {}, rsceTpCd = {}", 
                        item.getRsceCd(), item.getRsceTpCd());
                actualQty = BigDecimal.ZERO;
            }
            
            // acmtl_qty null 방어 코드
            BigDecimal acmtlQty = item.getAcmtlQty();
            if (acmtlQty == null) {
                log.warn("saveResourceSummaryManually: acmtlQty가 null입니다. rsceCd = {}, rsceTpCd = {}", 
                        item.getRsceCd(), item.getRsceTpCd());
                acmtlQty = BigDecimal.ZERO;
            }
            
            // remndr_qty null 방어 코드
            BigDecimal remndrQty = item.getRemndrQty();
            if (remndrQty == null) {
                log.warn("saveResourceSummaryManually: remndrQty가 null입니다. rsceCd = {}, rsceTpCd = {}", 
                        item.getRsceCd(), item.getRsceTpCd());
                remndrQty = BigDecimal.ZERO;
            }
            
            row.put("totalQty", totalQty);
            row.put("actualQty", actualQty);
            row.put("acmtlQty", acmtlQty);
            row.put("remndrQty", remndrQty);
            row.put("govsplyMtrlYn",
                    (item.getGovsplyMtrlYn() == null || item.getGovsplyMtrlYn().toString().isEmpty())
                            ? "N"
                            : item.getGovsplyMtrlYn()
            );
            row.put("cntrctChgId", cntrctChgId);
            row.put("dltYn", item.getDltYn());
            row.put("manualYn", item.getManualYn());
            row.put("rgstrId", usrId);
            row.put("chgId", usrId);

            boolean isUpdate = false;
            for (Map<String, Object> rsce : rsceSnoList) {
                row.put("rsceSno", rsce.get("rsce_sno"));
                updateList.add(row);
                isUpdate = true;
                break;
            }


            if (!isUpdate) {
                insertList.add(row); // INSERT용

                if(item.getCbsInsertYn() != null && item.getCbsInsertYn().equals("Y")) { // 일위대가 자원 추가 여부 확인
                    
                    row.put("rsceNm", item.getRsceNm()); // 자원명
                    row.put("specNm", item.getSpecNm()); // 규격명
                    row.put("unit", item.getUnit()); // 단위
                    
                    insertCbsList.add(row);
                }
            }
        }

        if (!updateList.isEmpty()) {
            dailyreportService.updateResourceSummaryManually(updateList);
        }

        if (!insertList.isEmpty()) {
            dailyreportService.insertResourceSummaryManually(insertList);
        }

        if (!insertCbsList.isEmpty()) {
            this.saveManualResourceWithCbs(insertCbsList);    
        }

        return result;
    }

    // 작업일지 공종자원 조회
    public List getCbsResourceSummaryList(String cntrctNo, String rsceTpCd, String dailyReportId, String searchText) {
        return dailyreportService.getCbsResourceSummaryList(cntrctNo, rsceTpCd, dailyReportId, searchText);
    }

    /**
     * 작업일지 승인 취소
     * 로직 흐름
     * 1. 전자 결제 승인 취소 호출 -> 전자 결제 문서 취소, 작업일지 결제 승인 정보 초기화 || 파라미터 : reportId, cntrctNo, apiYn, pjtDiv || 일괄 처리
     * 2. 실적 초기화 대상 액티비티 리스트 조회 : pr_activity + cw_daily_report_activity 조인 || 조건 : pstats(완료) or actual_end_date not null
     * 3. 작업일지 액티 비티 초기화 || 일괄 처리
     * 4. PR_ACTIVITY 초기화 || 일괄 처리
     * 5. P6 비동기 호출 || 일괄 처리
     * 
     * @param paramMap
     */
    @Transactional
    public void cancelDailyReportApproval(Map<String, Object> paramMap) {
  
        @SuppressWarnings("unchecked")
        List<CwDailyReport> dailyReportList = (List<CwDailyReport>) paramMap.get("dailyReportList");
        String usrId = UserAuth.get(true).getUsrId();
        List<String> docIds = null;
        Map<String, Object> requestParams = null;

        try {


            List<Long> rIdList = dailyReportList.stream().map(CwDailyReport::getDailyReportId).collect(Collectors.toList());
            log.info("작업일지 승인 취소 ID 목록 = {}", rIdList);

            if (rIdList == null || rIdList.isEmpty()) {
                log.warn("작업일지 승인 취소 ID가 없습니다.");
                return;
            }

            // 안전한 계약번호 추출
            String cntrctNo = null;
            if (!dailyReportList.isEmpty()) {
                cntrctNo = dailyReportList.get(0).getCntrctNo();
            } else {
                log.warn("작업일지 승인 취소: 계약번호를 찾을 수 없습니다.");     
                return;
            }
            
            Map<String, Object> ridsMap = new HashMap<>();
            ridsMap.put("rIdList", rIdList);
            ridsMap.put("cntrctNo", cntrctNo);     

            List<String> apDocIds = this.getApDocIds(ridsMap);
            log.info("작업일지 승인 취소 AP_DOC_ID 목록 = {}", apDocIds);
            if (apDocIds == null || apDocIds.isEmpty()) {
                log.warn("작업일지 승인 취소 AP_DOC_ID가 없습니다.");
                return;
            }

            Map<String, Object> reqVoMap = new HashMap<>();
            reqVoMap.put("apiYn", (String) paramMap.get("apiYn"));
            reqVoMap.put("pjtDiv", (String) paramMap.get("pjtDiv"));

            List<ApDoc> deleteList = apDocIds.stream()
                    .filter(Objects::nonNull) // null 값 필터링
                    .map(apDocId -> {
                        ApDoc apDoc = new ApDoc();
                        apDoc.setApDocId(apDocId);
                        apDoc.setApType(EapprovalHelper.DAILY_DOC); // 작업일지 문서 타입 설정
                        return apDoc;
                    })
                    .collect(Collectors.toList());
            
            // 1. 전자결제 승인 취소 및 작업일지 승인 상태값 초기화
            if (!deleteList.isEmpty()) {
                draftComponent.setDeleteList(deleteList, reqVoMap); 
            }

            // 2. 실적 초기화 대상 액티비티 리스트 조회
            List<Map<String, Object>> resetActivityList = this.getReseetP6(ridsMap);
            log.info("작업일지 승인 취소 실적 초기화 대상 액티비티 리스트 = {}", resetActivityList);

            // 3. CW_DAILY_REPORT_ACTIVITY 초기화 진행 : 진행중 변경, 실행소요일수 초기화, 실행종료일자 초기화
            if (!resetActivityList.isEmpty()) {
                try {
                    Map<String, Object> resetParam = new HashMap<>();
                    resetParam.put("cntrctNo", cntrctNo);
                    resetParam.put("rIdList", rIdList);
                    resetParam.put("chgId", usrId);
                    
                    int resetCount = dailyreportService.resetDailyReportActivity(resetParam);
                    log.info("CW_DAILY_REPORT_ACTIVITY 초기화 완료 건수 = {}", resetCount);
                } catch (GaiaBizException e) {
                    log.error("CW_DAILY_REPORT_ACTIVITY 초기화 중 오류 발생 error = {}", e.getMessage());
                }
            }

            // 4. PR_ACTIVITY 초기화 진행 : 실행종료일자 초기화
            if (!resetActivityList.isEmpty()) {
                try {
                    // activity_id 리스트 추출
                    List<String> activityIdList = resetActivityList.stream()
                            .map(activity -> (String) activity.get("activity_id"))
                            .filter(Objects::nonNull)
                            .distinct()
                            .collect(Collectors.toList());
                    
                    if (!activityIdList.isEmpty()) {
                        Map<String, Object> resetPrParam = new HashMap<>();
                        resetPrParam.put("cntrctNo", cntrctNo);
                        resetPrParam.put("activityIdList", activityIdList);
                        resetPrParam.put("chgId", usrId);
                        
                        int resetPrCount = dailyreportService.resetPrActivity(resetPrParam);
                        log.info("PR_ACTIVITY 초기화 완료 건수 = {}", resetPrCount);
                    }
                } catch (GaiaBizException e) {
                    log.error("PR_ACTIVITY 초기화 중 오류 발생 error = {}", e.getMessage());
                }
            }

            // 5. P6 비동기 호출 : p6_activity_obj_id
            if (!resetActivityList.isEmpty()) {
                try {
                    log.info("P6 초기화 연동 총 액티비티 건수 = {}", resetActivityList.size());
                     // p6_activity_obj_id 리스트 추출 (Integer를 String으로 안전하게 변환)
                     List<String> p6ActivityObjIdList = resetActivityList.stream()
                     .map(activity -> {
                         Object p6ObjId = activity.get("p6_activity_obj_id");
                         if (p6ObjId == null) {
                             return null;
                         }
                         // Integer 또는 String 모두 처리
                         return p6ObjId.toString();
                     })
                     .filter(Objects::nonNull)
                     .distinct()
                     .collect(Collectors.toList());
             
                    log.info("P6 초기화 연동 대상 p6_activity_obj_id 리스트 = {}", p6ActivityObjIdList);
                    
                    if (!p6ActivityObjIdList.isEmpty()) {
                        Map<String, Object> resetP6Param = new HashMap<>();
                        resetP6Param.put("p6ActivityObjIdList", p6ActivityObjIdList);
                        resetP6Param.put("chgId", usrId);
                        
                        log.info("P6 초기화 연동 호출 시작 - 파라미터 = {}", resetP6Param);
                        dailyReportAsyncService.updateP6FromCanceledDailyReport(resetP6Param);
                        log.info("P6 초기화 연동 호출 완료");
                    } else {
                        log.warn("P6 초기화 연동 대상이 없습니다.");
                    }    
                } catch (GaiaBizException e) {
                    log.error("P6 초기화 연동 중 오류 발생 error = {}", e.getMessage());
                }
            }

            // 6. 통합문서관리의 PDF 문서 삭제
            // key(docId) 수집
            docIds = dailyReportList.stream()
                    .map(CwDailyReport::getDocId)
                    .filter(Objects::nonNull)
                    .toList();
            log.info("cancelDailyReportApproval: 문서 삭제 대상 docId = {}", docIds);
            requestParams = new HashMap<>();
            requestParams.put("docIds", docIds.toArray(new String[0]));
            requestParams.put("usrId", usrId);
            log.info("cancelDailyReportApproval: 문서 삭제 대상 전달 parmas = {}", requestParams);
            documentServiceClient.removeDocument(requestParams);
        } catch (GaiaBizException e) {
            log.error("작업일지 승인 취소 중 오류 발생 error = {}", e.getMessage());
        }
    }
     
    /**
     * rId 리스트로 ap_doc_id 조회
     *
     * @param rIdList
     * @return
     */
    public List<String> getApDocIds(Map<String, Object> paramMap) {
        return dailyreportService.getApDocIds(paramMap);
    }

    /**
     *  완료된 activity 조회
     *
     * @param paramMap
     * @return
     */
    public List<Map<String, Object>> getReseetP6(Map<String, Object> paramMap) {
        return dailyreportService.getReseetP6(paramMap);
    }

    /**
     * 4. CW_DAILY_REPORT_ACTIVITY 초기화 진행
     *
     * @param paramMap
     * @return
     */
    public int resetDailyReportActivity(Map<String, Object> paramMap) {
        return dailyreportService.resetDailyReportActivity(paramMap);
    }

    /**
     * 5. PR_ACTIVITY 초기화 진행
     *
     * @param paramMap
     * @return
     */
    public int resetPrActivity(Map<String, Object> paramMap) {
        return dailyreportService.resetPrActivity(paramMap);
    }

    /**
     * 일위대가 자원 수동 등록
     * CT_CBS_RESOURCE 데이터 추가 진행
     * 
     * @param resourceList
     * @param cntrctNo
     * @return
     */
    @Transactional
    public void saveManualResourceWithCbs(List<Map<String, Object>> resourceList) {   
        dailyreportService.insertCtCbsResourceFromManualResource(resourceList);
    }

    // 액티비티명 조회
    public List<Map<String,Object>> getActivityNm(String cntrctNo, Long dailyReportId, String workDtType) {
        log.info("getActivityNm: 액티비티명 조회. cntrctNo = {}, dailyReportId = {}, worworkDtType = {}", cntrctNo, dailyReportId, workDtType);

        return dailyreportService.getActivityNm(cntrctNo, dailyReportId, workDtType);
    }

    // 금일 액티비티 조회
    public List selectTodayActivityList(String cntrctNo, Long dailyReportId, String workDtType) {
        log.info("selectTodayActivityList: 금일 액티비티 조회. cntrctNo = {}, dailyReportId = {}, workDtType = {}", cntrctNo, dailyReportId, workDtType);
        // 이전 게시물이 직전일자인지 확인
        Boolean exist = dailyreportService.selectPrevDailyReportExist(cntrctNo, dailyReportId);
        List<Map<String, Object>> todayActivity = new ArrayList<>();
        if(exist) {
            // 이전 게시물이 직전일자이면 그 게시물의 명일 액티비티를 조회함
            todayActivity = dailyreportService.selectTodayActivityList(cntrctNo, dailyReportId, workDtType);
        } else {
            // 직전 일자 게시물이 없으면(휴무/공휴일) pr_activity 조회함
            todayActivity = dailyreportService.selectTodayActivityFromPrActivity(cntrctNo, dailyReportId);
        }
        return todayActivity;
    }

    // QDB 리스트 조회
    public List getQdbList(String cntrctNo, String activityId, BigDecimal todayBohal, String searchText) {
        log.info("getQdbList: QDB 조회. cntrctNo = {}, activityId = {}, todayBohal = {}, searchText = {}", cntrctNo, activityId, todayBohal, searchText);

        return dailyreportService.selectQdbList(cntrctNo, activityId, todayBohal, searchText);
    }

    // 액티비티 자원 리스트 조회
    public List getResourceByActivity(String cntrctNo, Long dailyReportId, List<Map<String, Object>> activityIdAndTodayBohal) {
        log.info("getResourceByActivity: 자원 조회. cntrctNo = {}, dailyReportId = {}, activityId & todayBohal = {}", cntrctNo, dailyReportId, activityIdAndTodayBohal);

        return dailyreportService.selectResourceByActivity(cntrctNo, dailyReportId, activityIdAndTodayBohal);
    }

    // 금일 액티비티 조회
    public List getTodayResourceList(String cntrctNo, Long dailyReportId) {
        log.info("getTodayResourceList: 현재 작업일지에 등록된 자원 조회. cntrctNo = {}, dailyReportId = {}", cntrctNo, dailyReportId);

        return dailyreportService.selectTodayResourceList(cntrctNo, dailyReportId);
    }

    @Transactional
    public CwDailyReport addTodayDailyReport(CwDailyReport dailyreportInsert) {
        CwDailyReport result = dailyreportService.addDailyReport(dailyreportInsert);
        CwDailyReportActivity activity = new CwDailyReportActivity();

        activity.setDailyReportId(result.getDailyReportId());
        activity.setCntrctNo(result.getCntrctNo());

        activity.setWorkDtType("TM".toString());

        String dt = result.getDailyReportDate();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date formatDate = null;

        try {
            formatDate = formatter.parse(dt);
        } catch (ParseException e) {
            result = null;
        }

        if(result == null) {
            return result;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(formatDate);
        // cal.add(Calendar.DATE, 1);

        String strNewFormatDate = formatter.format(cal.getTime());
        // dailyreportService.addDefaultActivity(activity, strNewFormatDate);

        // activity.setWorkDtType("TD".toString());
        // dailyreportService.addDefaultActivity(activity, result.getDailyReportDate());
        // dailyreportService.addQdb(activity);
        dailyreportService.addTodayResource(activity, strNewFormatDate);

        // dailyreportService.updateRate(result);

        return result;
    }
    // 명일 액티비티 조회
    public List selectTomorrowActivityList(String cntrctNo, Long dailyReportId) {
        log.info("selectTomorrowActivityList: 명일 액티비티 조회. cntrctNo = {}, dailyReportId = {}", cntrctNo, dailyReportId);

        return dailyreportService.selectTodayActivityFromPrActivity(cntrctNo, dailyReportId);
    }
}