package kr.co.ideait.platform.gaiacairos.comp.construction.service;

import com.google.common.collect.Maps;
import jakarta.servlet.http.HttpServletRequest;
import kr.co.ideait.platform.gaiacairos.comp.project.service.InformationService;
import kr.co.ideait.platform.gaiacairos.comp.system.service.CommonCodeService;
import kr.co.ideait.platform.gaiacairos.comp.system.service.DocumentManageService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import kr.co.ideait.platform.gaiacairos.core.constant.CommonCodeConstants;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.*;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.*;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.construction.DailyreportMybatisParam;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.construction.DailyreportMybatisParam.DailyreportFormTypeSelectInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.construction.dailyreport.DailyreportForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.document.DocumentForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.document.CbgnPropertyDto;
import kr.co.ideait.platform.gaiacairos.core.type.FileUploadType;
import kr.co.ideait.platform.gaiacairos.core.type.KmaType;
import kr.co.ideait.platform.gaiacairos.core.util.restclient.DocumentServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DailyreportService extends AbstractGaiaCairosService {

    @Autowired
    CwDailyReportRepository cwDailyReportRepository;

    @Autowired
    CwDailyReportActivityRepository cwDailyReportActivityRepository;

    @Autowired
    CwDailyReportResourceRepository cwDailyReportResourceRepository;

    @Autowired
    CwDailyReportQdbRepository cwDailyReportQdbRepository;

    @Autowired
    CwDailyReportPhotoRepository cwDailyReportPhotoRepository;

    @Autowired
    CwAttachmentsRepository cwAttachmentsRepository;

    @Autowired
    PrActivityRepository prActivityRepository;

    @Autowired
    InformationService informationService;

    @Autowired
    DailyReportAsyncService dailyReportAsyncService;

    @Autowired
    CommonCodeService commonCodeService;

    @Autowired
    DocumentManageService documentManageService;

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
        DailyreportFormTypeSelectInput dailyreportFormTypeSelectInput = new DailyreportFormTypeSelectInput();

        dailyreportFormTypeSelectInput.setCntrctNo(cntrctNo);
        dailyreportFormTypeSelectInput.setYear(year);
        dailyreportFormTypeSelectInput.setMonth(month);
        dailyreportFormTypeSelectInput.setStatus(status);
        dailyreportFormTypeSelectInput.setSearchText(searchText);

        return mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.dailyreport.selectDailyreportList",
                dailyreportFormTypeSelectInput);
    }

    /**
     * 작업 일지 추가
     *
     * @param dailyreportInsert
     * @return
     */
    @Transactional
    public CwDailyReport addDailyReport(CwDailyReport dailyreportInsert) {

        Long maxId = this.generateDailyReportId();
        dailyreportInsert.setDailyReportId(maxId);

        CwDailyReport result = cwDailyReportRepository.saveAndFlush(dailyreportInsert);

        return result;
    }

    /**
     * 작업일지 공정현황 비율 저장.
     *
     * @param cwDailyReport
     * @return List
     * @throws
     */
    public void updateRate(CwDailyReport cwDailyReport) {

        Map rate = this.getRate(cwDailyReport);

        if (rate != null) {
            cwDailyReport.setTodayPlanBohalRate(Double.parseDouble(rate.get("to_plan_per").toString()));
            cwDailyReport.setTodayArsltBohalRate(Double.parseDouble(rate.get("to_actual_per").toString()));
            cwDailyReport.setTodayProcess(Double.parseDouble(rate.get("to_process").toString()));
            cwDailyReport.setAcmltPlanBohalRate(Double.parseDouble(rate.get("cum_plan_per").toString()));
            cwDailyReport.setAcmltArsltBohalRate(Double.parseDouble(rate.get("cum_actual_per").toString()));
            cwDailyReport.setAcmltProcess(Double.parseDouble(rate.get("cum_process").toString()));
            cwDailyReport.setRgstDt(cwDailyReport.getRgstDt());

            cwDailyReportRepository.save(cwDailyReport);
        }

    }

    /**
     * 작업일지 공정현황 비율 계산.
     *
     * @param dailyreportInsert
     * @return
     */
    public Map getRate(CwDailyReport dailyreportInsert) {
        return mybatisSession.selectOne(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.dailyreport.selectDailyreportRate",
                dailyreportInsert);
    }

    /**
     * 작업 일지 보고서 번호 중복 확인
     *
     * @param cntrctNo
     * @param reportNo
     * @return
     */
    public boolean chkReportNo(String cntrctNo, String reportNo) {
        Long ReportNo = cwDailyReportRepository.findReportNoByCntrctNo(cntrctNo, reportNo);
        return (ReportNo == null ? true : false);
    }

    /**
     * 작업 일지 보고일자 중복 확인
     *
     * @param cntrctNo
     * @param dailyReportDate
     * @return
     */
    public boolean chkDailyReportDate(String cntrctNo, String dailyReportDate) {
        Long DailyReportDate = cwDailyReportRepository.findDailyReportDateByCntrctNo(cntrctNo, dailyReportDate);
        return (DailyReportDate == null ? true : false);

    }

    /**
     * 작업일지 순번 증가
     *
     * @return
     */
    private Long generateDailyReportId() {
        Long maxId = cwDailyReportRepository.findMaxIdByCntrctNo();
        return (maxId == null ? 1 : maxId + 1);
    }

    /**
     * 작업 일지 초기저장 Activity
     *
     * @param cwDailyReportActivity
     * @throws
     */
    public void addDefaultActivity(CwDailyReportActivity cwDailyReportActivity, String dt) {
        Map map = new HashMap();
        map.put("cntrctNo", cwDailyReportActivity.getCntrctNo());
        map.put("dailyReportId", cwDailyReportActivity.getDailyReportId());
        map.put("workDtType", cwDailyReportActivity.getWorkDtType());
        map.put("dailyReportDate", dt);
        map.put("usrId", UserAuth.get(true).getUsrId());

        mybatisSession.update(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.dailyreport.addDefaultActivity", map);

    }

    /**
     * 작업 일지 초기저장 QDB
     *
     * @param cwDailyReportActivity
     * @throws
     */
    public void addQdb(CwDailyReportActivity cwDailyReportActivity) {
        Map map = new HashMap();
        map.put("cntrctNo", cwDailyReportActivity.getCntrctNo());
        map.put("dailyReportId", cwDailyReportActivity.getDailyReportId());
        map.put("workDtType", cwDailyReportActivity.getWorkDtType());
        map.put("usrId", UserAuth.get(true).getUsrId());

        mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.dailyreport.addQdb", map);

    }

    /**
     * 작업 일지 초기저장 RESOURCE
     *
     * @param cwDailyReportActivity
     * @throws
     */
    public void addResource(CwDailyReportActivity cwDailyReportActivity, String dt) {
        Map map = new HashMap();
        map.put("cntrctNo", cwDailyReportActivity.getCntrctNo());
        map.put("dailyReportId", cwDailyReportActivity.getDailyReportId());
        map.put("dailyReportDate", dt);
        map.put("usrId", UserAuth.get(true).getUsrId());

        mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.dailyreport.addResource",
                map);

    }

    /**
     * 작업일지 개요 조회
     *
     * @param cntrctNo
     * @param dailyReportId
     * @return
     */
    public CwDailyReport getDailyReports(String cntrctNo, Long dailyReportId) {
        return cwDailyReportRepository.findByCntrctNoAndDailyReportIdAndDltYn(cntrctNo, dailyReportId, "N");
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
    public List<Map<String,Object>> getActivity(String cntrctNo, Long dailyReportId, String dailyReportDate,
                                                   String workDtType) {
        DailyreportFormTypeSelectInput dailyreportFormTypeSelectInput = new DailyreportFormTypeSelectInput();

        dailyreportFormTypeSelectInput.setCntrctNo(cntrctNo);
        dailyreportFormTypeSelectInput.setDailyReportId(dailyReportId);
        dailyreportFormTypeSelectInput.setDailyReportDate(dailyReportDate);
        dailyreportFormTypeSelectInput.setWorkDtType(workDtType);

        return mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.dailyreport.selectActivityData",
                dailyreportFormTypeSelectInput);
    }

    public List<Map<String,Object>> getAccumulatedActivity(String cntrctNo, Long dailyReportId,
                                                String workDtType) {
        DailyreportFormTypeSelectInput dailyreportFormTypeSelectInput = new DailyreportFormTypeSelectInput();

        dailyreportFormTypeSelectInput.setCntrctNo(cntrctNo);
        dailyreportFormTypeSelectInput.setDailyReportId(dailyReportId);
        dailyreportFormTypeSelectInput.setWorkDtType(workDtType);

        return mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.dailyreport.selectAccumulatedActivity",
                dailyreportFormTypeSelectInput);
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
        log.info("getResource: 자원 조회 진행 cntrctNo = {}, dailyReportId = {}, rsceTpCd = {}", cntrctNo, dailyReportId, rsceTpCd);
        DailyreportFormTypeSelectInput dailyreportFormTypeSelectInput = new DailyreportFormTypeSelectInput();

        dailyreportFormTypeSelectInput.setCntrctNo(cntrctNo);
        dailyreportFormTypeSelectInput.setDailyReportId(dailyReportId);
        dailyreportFormTypeSelectInput.setRsceTpCd(rsceTpCd);

        // 자원 조회
        List<Map<String,Object>> resource = mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.dailyreport.selectResourceData",
                dailyreportFormTypeSelectInput);
        log.info("getResource: 현재 dailyReportId({}) 해당하는 자원 전체 조회 결과 ({})건. result = {}",dailyReportId, resource.size(), resource);

        return resource;
    }

    // 이전 보고서에서 추가한 수동 자원 조회
    public List<Map<String,Object>> getResourceManualAccumulatedResourc(String cntrctNo, Long dailyReportId, String rsceTpCd) {
        log.info("getResourceManualResource: 자원 조회 진행 cntrctNo = {}, dailyReportId = {}, rsceTpCd = {}", cntrctNo, dailyReportId, rsceTpCd);
        DailyreportFormTypeSelectInput dailyreportFormTypeSelectInput = new DailyreportFormTypeSelectInput();

        dailyreportFormTypeSelectInput.setCntrctNo(cntrctNo);
        dailyreportFormTypeSelectInput.setDailyReportId(dailyReportId);
        dailyreportFormTypeSelectInput.setRsceTpCd(rsceTpCd);

        // 이전 보고서에서 수동으로 추가한 데이터 조회
        List<Map<String,Object>> accumulated = mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.dailyreport.selectAccumulatedResource",
                dailyreportFormTypeSelectInput);
        log.info("getResourceManualResource: 이전 보고서에서 수동으로 추가한 자원 조회 결과 ({})건. result = {}", accumulated.size(), accumulated);
        return accumulated;
    }

    /**
     * 공정 사진 조회
     *
     * @param cntrctNo
     * @param dailyReportId
     * @return
     */
    public List<?> getPhoto(String cntrctNo, Long dailyReportId) {
        DailyreportFormTypeSelectInput dailyreportFormTypeSelectInput = new DailyreportFormTypeSelectInput();

        dailyreportFormTypeSelectInput.setCntrctNo(cntrctNo);
        dailyreportFormTypeSelectInput.setDailyReportId(dailyReportId);

        return mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.dailyreport.selectPhotoData",
                dailyreportFormTypeSelectInput);
        // return cwDailyReportActivityRepository.findByNativeQuery(cntrctNo,
        // dailyReportId, workDtType);
    }

    public Map<String, Object> selectActivityByCntrctNoAndDailyReportIdAndDailyActivityId(CwDailyReportActivity cwDailyReportActivity) {
        Map map = new HashMap();
        map.put("cntrctNo", cwDailyReportActivity.getCntrctNo());
        map.put("dailyReportId", cwDailyReportActivity.getDailyReportId());
        map.put("dailyActivityId",cwDailyReportActivity.getDailyActivityId());

        log.info("selectActivityByCntrctNoAndDailyReportIdAndDailyActivityId: 기존 액티비티 존재 여부 확인. param = {}", map);

        return mybatisSession.selectOne(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.dailyreport.selectActivityByCntrctNoAndDailyReportIdAndDailyActivityId"
                , map);
    }

    public void addDailyReportActivity(CwDailyReportActivity cwDailyReportActivity) {
        mybatisSession.insert(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.dailyreport.addDailyReportActivity",
                cwDailyReportActivity);
    }

//    public Map<String, Object> selectCompletedTmActivity(CwDailyReportActivity cwDailyReportActivity) {
//
//        Map tmMap = new HashMap();
//        tmMap.put("cntrctNo", cwDailyReportActivity.getCntrctNo());
//        tmMap.put("dailyReportId", cwDailyReportActivity.getDailyReportId());
//        tmMap.put("activityId",cwDailyReportActivity.getActivityId());
//
//        return mybatisSession.selectOne(
//                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.dailyreport.selectCompletedTmActivity"
//                , tmMap);
//    }

    // 금일 액티비티 완료 처리한 경우 명일 액티비티도 완료 처리
    // type - TDTM: 액티비티 업데이트, TM: TD에 맞춰 TM 업데이트
    public void updateActivityData(CwDailyReportActivity activity, String type) {
        log.info("updateActivityData: type = {} param = {}", type, activity);

        Map map = new HashMap();

        map.put("cntrctNo", activity.getCntrctNo());
        map.put("dailyReportId", activity.getDailyReportId());
        map.put("workDtType", activity.getWorkDtType());
        map.put("activityId", activity.getActivityId());
        map.put("actualBgnDate", activity.getActualBgnDate());
        map.put("actualEndDate", activity.getActualEndDate());
        map.put("actualReqreDaynum", activity.getActualReqreDaynum() == null || activity.getActualReqreDaynum().toString().isBlank()
                ? "0"
                : activity.getActualReqreDaynum());
        map.put("pstats", activity.getPstats());
        map.put("chgId", UserAuth.get(true).getUsrId());
        map.put("dltYn", activity.getDltYn());
        if("TM".equals(type)) {
            // TD 데이터로 TM 업데이트
            map.put("workDtType", "TM");
        }


        mybatisSession.update(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.dailyreport.updateActivityData",
                map);
    }



    // 금일 액티비티 완료 처리한 경우 이전 보고서에 등록한 액티비도 완료 처리
    public void updateActivityWithPreviousReports(CwDailyReportActivity activity) {
        log.info("updateActivityData: param = {}", activity);

        Map map = new HashMap();

        map.put("cntrctNo", activity.getCntrctNo());
        map.put("dailyReportId", activity.getDailyReportId());
        map.put("workDtType", activity.getWorkDtType());
        map.put("activityId", activity.getActivityId());
        map.put("actualBgnDate", activity.getActualBgnDate());
        map.put("actualEndDate", activity.getActualEndDate());
        map.put("actualReqreDaynum", activity.getActualReqreDaynum() == null || activity.getActualReqreDaynum().toString().isBlank()
                ? "0"
                : activity.getActualReqreDaynum());
        map.put("pstats", activity.getPstats());
        map.put("chgId", UserAuth.get(true).getUsrId());
        map.put("dltYn", activity.getDltYn());


        mybatisSession.update(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.dailyreport.updateActivityWithPreviousReports",
                map);
    }


    /**
     * Pr Activity 수정
     *
     * @param dailyReportActivity
     */
    @Transactional
    public void updatePrActivity(List<CwDailyReportActivity> dailyReportActivity) {
        dailyReportActivity.forEach(id -> {
            PrActivity prActivity = prActivityRepository.findByCntrctChgIdAndRevisionIdAndActivityIdAndDltYn(
                    id.getCntrctChgId(), id.getRevisionId(), id.getActivityId(), "N").orElse(null);
            if (prActivity != null) {
                if (id.getWorkDtType().equals("TD")) {
                    String bgnDate = id.getActualBgnDate().toString();
                    String endDate = id.getActualEndDate().toString();

                    prActivityRepository.updateActualDate(id.getCntrctChgId(), id.getRevisionId(), id.getActivityId(),
                            bgnDate, endDate);
                }
            }
        });
    }

    /**
     * 작업일지 리소스 수량 업데이트
     *
     * @param Resource
     */
    @Transactional
    public void updateDailyReportResource(List<CwDailyReportResource> Resource) {
        if(Resource != null){
            Resource.forEach(id -> {
                CwDailyReportResource cwDailyReportResource = cwDailyReportResourceRepository
                        .findByCntrctNoAndDailyReportIdAndRsceSnoAndDltYn(id.getCntrctNo(), id.getDailyReportId(), id.getRsceSno(), "N")
                        .orElse(null);

            if (cwDailyReportResource != null) {
                cwDailyReportResourceRepository.updateQtyByCntrctNoAndDailyReportIdAndRsceSnoAndDltYn(id.getActualQty(),
                        id.getRemndrQty(), id.getCntrctNo(), id.getDailyReportId(), id.getRsceSno(), id.getDltYn());

                }
            });
        }
    }

    public Integer findMaxFileNo () {
        return cwAttachmentsRepository.findMaxFileNo();
    }
    public List<CwDailyReportPhoto> findByCntrctNoAndDailyReportId(String cntrctNo, Long dailyReportId) {
        return cwDailyReportPhotoRepository.findByCntrctNoAndDailyReportId(cntrctNo, dailyReportId);
    }
    public Integer selectMaxSno(Integer fileNo) {
        return mybatisSession.selectOne(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.dailyreport.selectMaxSno", fileNo);
    }
    public CwAttachments saveCwAttachments(CwAttachments cwAttachments) {
        return cwAttachmentsRepository.save(cwAttachments);
    }
    public CwDailyReportPhoto findByCntrctNoAndDailyReportIdAndCnsttyPhtSno(
            String cntrctNo, Integer dailyReportId, Integer cnsttyPhtSno) {
        return cwDailyReportPhotoRepository.findByCntrctNoAndDailyReportIdAndCnsttyPhtSno(cntrctNo, dailyReportId, cnsttyPhtSno);
    }
    public void updateDeleteCwDailyReportPhoto(CwDailyReportPhoto cwDailyReportPhoto) {
        cwDailyReportPhotoRepository.updateDelete(cwDailyReportPhoto);
    }
    public void saveCwDailyReportPhoto(CwDailyReportPhoto cwDailyReportPhoto) {
        cwDailyReportPhotoRepository.save(cwDailyReportPhoto);
    }

    public DailyreportMybatisParam.dailyreportAttAchmentsDataOutput selectAttachmentsByFileNo(CwAttachments cwAttachments) {
        Map map = new HashMap();
        map.put("fileNo", cwAttachments.getFileNo());
        map.put("sno", cwAttachments.getSno());
        return mybatisSession.selectOne(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.dailyreport.selectAttachmentsByFileNo", map);

    }

    public void updateDeleteAttachments(Integer fileNo) {
        Map<String, Object> param = new HashMap<>();
        param.put("fileNo", fileNo);
        param.put("dltId", UserAuth.get(true).getUsrId());

        mybatisSession.update(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.dailyreport.updateDeleteAttachments",
                param);
    }

    // 미사용 추후 수정 및 삭제예정
    public void updateDefaultActivity(CwDailyReport cwDailyReport) {
        mybatisSession.update("updateDefaultActivity", cwDailyReport);

    }

    public List selectPrActivityList(DailyreportMybatisParam.DailyreportFormTypeSelectInput dailyreportFormTypeSelectInput){
        return mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.dailyreport.selectPrActivityList",
                dailyreportFormTypeSelectInput);
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
        DailyreportFormTypeSelectInput dailyreportFormTypeSelectInput = new DailyreportFormTypeSelectInput();

        dailyreportFormTypeSelectInput.setCntrctNo(cntrctNo);
        dailyreportFormTypeSelectInput.setDailyReportId(dailyReportId);
        dailyreportFormTypeSelectInput.setWorkDtType(workDtType);

        return mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.dailyreport.selectDailyReportActivityListforChange",
                dailyreportFormTypeSelectInput);
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
        DailyreportFormTypeSelectInput dailyreportFormTypeSelectInput = new DailyreportFormTypeSelectInput();

        dailyreportFormTypeSelectInput.setCntrctNo(cntrctNo);
        dailyreportFormTypeSelectInput.setDailyReportId(dailyReportId);
        dailyreportFormTypeSelectInput.setWorkDtType(workDtType);

        return mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.dailyreport.selectTodayDailyReportQdbList",
                dailyreportFormTypeSelectInput);
    }

    /**
     * 금일실적 변경 > 금일 실적 activity 가져오기
     *
     * @param cntrctNo
     * @param dailyReportId
     * @return
     */
    public List selectTodayDailyReportResourceList(String cntrctNo, Long dailyReportId) {
        DailyreportFormTypeSelectInput dailyreportFormTypeSelectInput = new DailyreportFormTypeSelectInput();

        dailyreportFormTypeSelectInput.setCntrctNo(cntrctNo);
        dailyreportFormTypeSelectInput.setDailyReportId(dailyReportId);

        return mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.dailyreport.selectTodayDailyReportResourceList",
                dailyreportFormTypeSelectInput);
    }

    public boolean checkPgaiaFirstApprover(CwDailyReport cwDailyReport) {

        Map<String, Object> checkParams = new HashMap<>();
        checkParams.put("pjtNo", UserAuth.get(true).getPjtNo());
        checkParams.put("cntrctNo", cwDailyReport.getCntrctNo());
        checkParams.put("apType", "02");

        return mybatisSession.selectOne(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.eapproval.approval.checkPgaiaFirstApprover",
                checkParams);
    }

    public void updateByCntrctNoAndDailyReportIdAnyType(CwDailyReport id) {
        cwDailyReportRepository.updateByCntrctNoAndDailyReportIdAnyType(id.getApprvlStats(),
                UserAuth.get(true).getUsrId(), LocalDateTime.now(), id.getCntrctNo(),
                id.getDailyReportId());
    }



    public List<CwDailyReportActivity> getDailyReportActivities(String cntrctNo, Long dailyReportId) {
        return cwDailyReportActivityRepository.findByCntrctNoAndDailyReportIdAndDltYn(cntrctNo, dailyReportId, "N");
    }

    public List<CwDailyReportPhoto> getDailyReportPhotos(String cntrctNo, Long dailyReportId) {
        return cwDailyReportPhotoRepository.findByCntrctNoAndDailyReportIdAndDltYn(cntrctNo, dailyReportId, "N");
    }

    public List<CwDailyReportResource> getDailyReportResources(String cntrctNo, Long dailyReportId) {
        return cwDailyReportResourceRepository.findByCntrctNoAndDailyReportIdAndDltYn(cntrctNo, dailyReportId, "N");
    }

    public List<CwDailyReportQdb> getDailyReportQdb(String cntrctNo, Long dailyReportId) {
        return cwDailyReportQdbRepository.findByCntrctNoAndDailyReportIdAndDltYn(cntrctNo, dailyReportId, "N");
    }

    public List<CwAttachments> getAttachments(Set<Integer> atchFileNos) {
        return cwAttachmentsRepository.findByFileNoInAndDltYnInt(atchFileNos, "N");
    }



    public void updateDeleteForDailyReport(CwDailyReport cwDailyReport) {
        cwDailyReportRepository.updateDelete(cwDailyReport);
    }

    public void updateDeleteForDailyReportActivity(CwDailyReportActivity cwDailyReportActivity) {
        cwDailyReportActivityRepository.updateDelete(cwDailyReportActivity);
    }

    public void updateDeleteForDailyReportQdb(CwDailyReportQdb cwDailyReportQdb) {
        cwDailyReportQdbRepository.updateDelete(cwDailyReportQdb);
    }

    public void updateDeleteForDailyReportResource(CwDailyReportResource cwDailyReportResource) {
        cwDailyReportResourceRepository.updateDelete(cwDailyReportResource);
    }

    public void updateDeleteForDailyReportPhoto(CwDailyReportPhoto cwDailyReportPhoto) {
        cwDailyReportPhotoRepository.updateDelete(cwDailyReportPhoto);
    }

    public void updateDeleteForAttachments(CwAttachments cwDailyReport) {
        cwAttachmentsRepository.updateDelete(cwDailyReport);
    }

    /**
     * 작업일지 QDB 추가
     *
     * @param dailyReportActivity
     */
    @Transactional
    public void insertDailyReportQdb(List<CwDailyReportActivity> dailyReportActivity) {
        dailyReportActivity.forEach(id -> {
            Map map = new HashMap();
            map.put("cntrctNo", id.getCntrctNo());
            map.put("dailyReportId", id.getDailyReportId());
            map.put("workDtType", id.getWorkDtType());
            map.put("activity_id", id.getActivityId());
            map.put("usrId", UserAuth.get(true).getUsrId());

            mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.dailyreport.addQdb",
                    map);
        });
    }

    /**
     * QDB 삭제
     *
     * @param dailyReportActivity
     */
    @Transactional
    public void deleteDailyReportTodayQdb(List<CwDailyReportActivity> dailyReportActivity) {
        dailyReportActivity.forEach(id -> {
            if (id.getDltYn().equals("Y")) {
                cwDailyReportActivityRepository.deleteDailyReportQdbByActivityId(id.getCntrctNo(),
                        id.getDailyReportId(), id.getActivityId());
            }
        });
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
//        updateDailyReportActivity(dailyReportActivity);

        if (workDtType.equals("TD")) {
            setQdb(dailyReportActivity, prActivity);
            setResource(dailyReportResource, dailyReportActivity, cntrctNo, dailyReportId, dailyReportDate);

            CwDailyReport cwDailyReport = cwDailyReportRepository.findByCntrctNoAndDailyReportIdAndDltYn(cntrctNo,
                    dailyReportId, "N");
            updateRate(cwDailyReport);
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
    @Transactional
    protected void setQdb(List<CwDailyReportActivity> dailyReportActivity, List<CwDailyReportActivity> prActivity) {
        deleteDailyReportTodayQdb(dailyReportActivity);

        // pr 추가 건 QDB 저장.
        if (prActivity != null) {
            insertDailyReportQdb(prActivity);
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
        Map<String, Object> input = new HashMap<>();
        input.put("cntrctNo", cntrctNo);
        input.put("dailyReportId", dailyReportId);
        mybatisSession.delete("kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.dailyreport.deleteDailyReportResource", input);
//        cwDailyReportResourceRepository.deleteDailyReportResource(cntrctNo, dailyReportId);

        // 리소스 추가
        CwDailyReportActivity activity = new CwDailyReportActivity();
        activity.setCntrctNo(cntrctNo);
        activity.setDailyReportId(dailyReportId);

        addResource(activity, dailyReportDate);

        // 작업일지 리소스 표시여부 업데이트
        if (dailyReportResource != null) {
            dailyReportResource.forEach(id -> {
                cwDailyReportResourceRepository.updateDsplyByCntrctNoAndDailyReportIdAndRsceCd(id.getMainRsceDsply(),
                        id.getCntrctNo(), id.getDailyReportId(), id.getRsceCd());
            });
        }
    }

    public void deleteDailyReportResource(String cntrctNo, Long dailyReportId) {
        Map<String, Object> input = new HashMap<>();
        input.put("cntrctNo", cntrctNo);
        input.put("dailyReportId", dailyReportId);

        mybatisSession.delete("kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.dailyreport.deleteDailyReportResource", input);
    }

    public void updateDsplyByCntrctNoAndDailyReportIdAndRsceCd(CwDailyReportResource cwDailyReportResource) {
        cwDailyReportResourceRepository.updateDsplyByCntrctNoAndDailyReportIdAndRsceCd(cwDailyReportResource.getMainRsceDsply(),
                cwDailyReportResource.getCntrctNo(), cwDailyReportResource.getDailyReportId(), cwDailyReportResource.getRsceCd());
    }


    public void saveDailyReport(CwDailyReport cwDailyReport) {
        cwDailyReportRepository.save(cwDailyReport);
    }


    public CwDailyReport findByApDocId(String apDocId) {
        return cwDailyReportRepository.findByApDocId(apDocId).orElse(null);
    }


    public CwDailyReport findByCntrctNoAndDailyReportDateAndDltYn(CwDailyReport cwDailyReport) {
        return cwDailyReportRepository.findByCntrctNoAndDailyReportDateAndDltYn(cwDailyReport.getCntrctNo(), cwDailyReport.getDailyReportDate(), "N");
    }



    public void deleteDailyReport(CwDailyReport cwDailyReport) {
        cwDailyReportRepository.deleteDailyReport(cwDailyReport.getCntrctNo(), cwDailyReport.getDailyReportId());
    }

    public void updateApprovalStausCancel(CwDailyReport cwDailyReport, String usrId) {
        cwDailyReportRepository.updateApprovalStausCancel(null, null, null, null, cwDailyReport.getCntrctNo(),
                cwDailyReport.getDailyReportId(), usrId);
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
     * FileNo생성
     *
     */
    private Integer generateFileNo() {
        Integer maxFileNo = cwAttachmentsRepository.findMaxFileNo();
        return (maxFileNo == null ? 1 : maxFileNo + 1);
    }


    // 고유한 파일명 생성 메서드
    public String generateUniqueFileName(String originalFileName) {
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
     * selectBox, radio 상자 만들 데이터 가져오기
     *
     * @param
     * @return List
     * @throws
     */
    public List selectMakeDataListUsingCondition(String col1, String col2, String tName, String[] param,
                                                 String orderByCol, String orderByType) {
        Map map = new HashMap();
        map.put("col1", col1);
        map.put("col2", col2);
        map.put("tName", tName);
        map.put("param1", param[0]);
        map.put("param2", Integer.parseInt(param[1]));
        map.put("orderByCol", orderByCol);

        return mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.dailyreport.selectMakeDataListUsingCondition",
                map);
    }

    // 인력 목록 조회
    public List selectLaborList() {
        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.dailyreport.selectLaborList", "");

    }



    // 현장 작업자 인력 정보 조회
    public List getDailyReportSiteLaborList(String cntrctNo, String dailyReportId, String searchText) {
        log.info("getDailyReportSiteLaborList: 현장 작업자 인력 정보 조회 진행 cntrctNo = {}, dailyReportId = {}", cntrctNo, dailyReportId);
        Map map = new HashMap();
        map.put("cntrctNo", cntrctNo);
        map.put("dailyReportId", dailyReportId);
        map.put("searchText", searchText);
        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.dailyreport.selectSiteLaborList", map);
    }

    public Boolean checkDailyReportExists(String cntrctNo, String dailyReportDate) {
        log.info("checkDailyReportExists: 작업일지 게시물 존재 여부 확인");
        Map map = new HashMap();
        map.put("cntrctNo", cntrctNo);
        map.put("dailyReportDate", dailyReportDate);
        return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.dailyreport.selectDailyReportExists", map);
    }


    public String selectCntrctChgIdByCntrctNo(String cntrctNo) {
        return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.dailyreport.selectCntrctChgIdByCntrctNo", cntrctNo);
    }

    public List selectRsceSnoByCntrctNoAndDailyReportIdAndRsceCdAndRsceTpCd(DailyreportForm.ManualDailyReportResource manualDailyReportResource) {

        Map<String, Object> map = new HashMap<>();
        map.put("cntrctNo", manualDailyReportResource.getCntrctNo());
        map.put("dailyReportId", manualDailyReportResource.getDailyReportId());
        map.put("rsceCd", manualDailyReportResource.getRsceCd());
        map.put("rsceTpCd", manualDailyReportResource.getRsceTpCd());

        return mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.dailyreport.selectRsceSnoByCntrctNoAndDailyReportIdAndRsceCdAndRsceTpCd",
                map
        );
    }

    public void updateResourceSummaryManually(List<Map<String, Object>> updateList) {
        mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.dailyreport.updateResourceSummaryManually", updateList);
    }

    public void insertResourceSummaryManually(List<Map<String, Object>> insertList) {
        mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.dailyreport.insertResourceSummaryManually", insertList);
    }


    // 작업일지 공종자원 조회
    public List getCbsResourceSummaryList(String cntrctNo, String rsceTpCd, String dailyReportId, String searchText) {
        log.info("getCbsResourceSummaryList: 작업일지 공종자원 조회 진행");

        Map input = new HashMap();
        input.put("cntrctNo", cntrctNo);
        input.put("rsceTpCd", rsceTpCd);
        input.put("dailyReportId", dailyReportId);
        input.put("searchText", searchText);
        return mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.dailyreport.selectCbsResourceSummary", input);

    }

    public void saveDailyReportActivity(CwDailyReportActivity input) {
        cwDailyReportActivityRepository.save(input);
    }

    public void saveDailyReportQdb(CwDailyReportQdb input) {
        cwDailyReportQdbRepository.save(input);
    }

    public void saveDailyReportPhoto(CwDailyReportPhoto input) {
        cwDailyReportPhotoRepository.save(input);
    }

    public void saveDailyReportResource(CwDailyReportResource input) {
        cwDailyReportResourceRepository.save(input);
    }

    public Map<String, Object> selectDailyReportSimple(Long dailyReportId) {
        Map<String, Object> infoMap = new HashMap<>();
        infoMap.put("dailyReportId", dailyReportId);

        return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.dailyreport.selectDailyReportSimple", infoMap);

    }

    // daily report docId 업데이트
    public Map<String, String> updateDailyReportDocId(String cntrctNo, String dailyReportId, String docId) {
        log.info("updateDailyReportDocId: 작업일지 docId 업데이트 진행 cntrctNo = {}, dailyReportId = {}, docId = {}", cntrctNo, dailyReportId, docId);
        Map<String, String> result = new HashMap<>();
        result.put("result", "fail");

        Map map = new HashMap();
        map.put("cntrctNo", cntrctNo);
        map.put("dailyReportId", dailyReportId);
        map.put("docId", docId);
        log.info("updateDailyReportDocId: 작업일지 docId 업데이트 param = {}", map);
        mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.dailyreport.updateDailyReportDocId", map);

        result.put("result", "success");
        return result;

    }

    public void updateActualDate(List<Map<String, Object>> activityList) {
        mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.dailyreport.updateActualDate", activityList);
    }

    public Object selectUsersByDepartment(Map<String, Object> userMap) {
        return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.dailyreport.selectUsersByDepartment", userMap);
    }

    public List<Map<String, Object>> selectStmpInfoByCntrctNo(Map<String, Object> stampMap) {
        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.dailyreport.selectStmpInfoByCntrctNo", stampMap);
    }

    public Map<String, Object> selectDailyReportBasicInfo(Map<String, Object> infoMap) {
        return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.dailyreport.selectDailyReportBasicInfo", infoMap);
    }

    public List<Map<String,Object>> selectActivityData(DailyreportFormTypeSelectInput input) {
        return mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.dailyreport.selectActivityData",
                input
        );
    }
    public List<Map<String, Object>> selectActivityData(Map<String, Object> param) {
        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.dailyreport.selectActivityData",param);
    }

    public List<Map<String, Object>> selectDailyReportResources(Map<String, Object> resMap) {
        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.dailyreport.selectDailyReportResources",resMap);
    }

    public List<Map<String, Object>> selectDailyReportPhotoInfo(Map<String, Object> photoMap) {
        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.dailyreport.selectDailyReportPhotoInfo", photoMap);
    }

    public List<Map<String,Object>> selectActivityListForP6(Map<String, String> input) {
        return mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.dailyreport.selectActivityListForP6",
                input
        );
    }

    public List<Map<String,Object>> getFinishActivity(String cntrctNo, Long dailyReportId) {
        Map map = new HashMap();
        map.put("cntrctNo", cntrctNo);
        map.put("dailyReportId", dailyReportId);
        return mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.dailyreport.selectFinishActivity",
                map
        );
    }



    /**
     * 작업일보 승인상태 변경
     *
     * @param report
     * @param usrId
     * @param apprvlStats
     */
    @Transactional
    public void updateApprovalStatus(CwDailyReport report, String usrId, String apprvlStats) {
        if(report != null) {
            report.setApprvlStats(apprvlStats);
            if ("E".equals(apprvlStats)) {
                report.setApprvlReqId(usrId);
                report.setApprvlReqDt(LocalDateTime.now());
            } else {
                report.setApprvlId(usrId);
                report.setApprvlDt(LocalDateTime.now());
            }
            saveDailyReport(report);

            // 승인 완료되면
            if (apprvlStats.equals("A")) {
                // 작업일지 PDF 문서화
                try {
                    HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
                    dailyReportAsyncService.makeDailyreportPdf(report, request.getHeader("x-auth"), cookieService.getCookie(request, cookieVO.getTokenCookieName()));
                } catch (GaiaBizException e) {
                    log.error("updateApprovalStatus: 문서화 작업 중 오류 발생 error = {}", e.getMessage());
                }

                // pr_activity 업데이트
                try {
                    dailyReportAsyncService.updatePrActivityFromDailyReport(report);
                } catch (GaiaBizException e) {
                    log.error("updateApprovalStatus: pr_activity 업데이트 작업 중 오류 발생 error = {}", e.getMessage());
                }


                // p6 연동
                try {
                    dailyReportAsyncService.updateP6FromPrActivity(report.getCntrctNo(), report.getDailyReportId().toString(), report.getRgstrId());
                } catch (GaiaBizException e) {
                    log.error("updateApprovalStatus: P6 연동 작업 중 오류 발생 error = {}", e.getMessage());
                }

            }
        }
    }

    /**
     * 작업일보 가져오기
     *
     * @param apDocId
     * @param apUsrId
     * @param apDocStats
     */
    @Transactional
    public void updateDailyReportByApDocId(String apDocId, String apUsrId, String apDocStats) {
        CwDailyReport cwDailyReport = findByApDocId(apDocId);
        if (cwDailyReport != null) {
            String apStats = "C".equals(apDocStats) ? "A" : "R";
            updateApprovalStatus(cwDailyReport, apUsrId, apStats);
        }
    }


    /**
     * 작업일지 데이터 조회
     *
     * @param apDocId
     * @return
     */
    public Map<String, Object> selectDailyReportByApDocId(String apDocId) {
        Map<String, Object> returnMap = new HashMap<>();
        CwDailyReport cwDailyReport = findByApDocId(apDocId);
        if (cwDailyReport != null) {
            returnMap.put("report", cwDailyReport);
            returnMap.put("resources",
                    this.selectDailyreportResource(cwDailyReport.getCntrctNo(), cwDailyReport.getDailyReportId()));
        }
        return returnMap;
    }


     /**
     * 작업일지 PGAIA에 추가
     *
     */
    @Transactional
    public void insertPgaiaDailyReport(CwDailyReport cwDailyReport,
                                       List<CwDailyReportActivity> cwDailyReportActivity, List<CwDailyReportQdb> cwDailyReportQdb,
                                       List<CwDailyReportPhoto> cwDailyReportPhoto,
                                       List<Map<String, Object>> dailyReportFileInfo,
                                       List<CwDailyReportResource> cwDailyReportResource) {

        saveDailyReport(cwDailyReport);

        if (cwDailyReportActivity != null && !cwDailyReportActivity.isEmpty()) {
            cwDailyReportActivityRepository.saveAll(cwDailyReportActivity);
        }

        if (cwDailyReportQdb != null && !cwDailyReportQdb.isEmpty()) {
            cwDailyReportQdbRepository.saveAll(cwDailyReportQdb);
        }

        if (cwDailyReportPhoto != null && !cwDailyReportPhoto.isEmpty()) {
            cwDailyReportPhotoRepository.saveAll(cwDailyReportPhoto);
        }

        if (cwDailyReportResource != null && !cwDailyReportResource.isEmpty()) {
            cwDailyReportResourceRepository.saveAll(cwDailyReportResource);
        }

        // 파일 정보에서 작업일지 첨부파일들 가져오기
        log.info("##### Received inspectionFileInfo: {}", dailyReportFileInfo != null ? dailyReportFileInfo.size() : 0);
        if (dailyReportFileInfo != null && !dailyReportFileInfo.isEmpty()) {
            log.info("##### Processing {} safety attachment file info", dailyReportFileInfo.size());
            insertSafetyFileInfoToApi(cwDailyReport, dailyReportFileInfo, cwDailyReportPhoto);
        } else {
            log.info("##### No inspection attachment file info received");
        }
    }


    private void insertSafetyFileInfoToApi(CwDailyReport cwDailyReport,
                                           List<Map<String, Object>> dailyReportFileInfo, List<CwDailyReportPhoto> photoList) {
        // 작업 일지 정보 조회
        CwDailyReport dailyReport = findByCntrctNoAndDailyReportDateAndDltYn(cwDailyReport);

        // // 파일 번호를 가지고 있는 photo들
        // List<Integer> fileNoList = photoList.stream()
        // .map(CwDailyReportPhoto::getAtchFileNo)
        // .filter(Objects::nonNull)
        // .collect(Collectors.toList());

        if (dailyReport == null) {
            log.error("Deficiency not found for InspectionNo");
            return;
        }
        if(photoList != null) {
            if(photoList.getFirst() != null) {
                this.insertFileInfoToApi(dailyReportFileInfo, photoList.getFirst().getAtchFileNo(), dailyReport.getRgstrId(), cwDailyReport.getCntrctNo());
            }
        }
    }


    /**
     * 공통 파일 처리 메서드
     *
     * @param files   처리할 파일 목록
     * @param fileNo  연결할 파일 번호
     * @param rgstrId 등록자 ID
     */
    @Transactional
    public void insertFileInfoToApi(List<Map<String, Object>> files, Integer fileNo, String rgstrId, String cntrctNo) {
        List<CwAttachments> cwAttachmentsList = new ArrayList<>();

        log.info("##### FILE PROCESSING START - Type: {}, TargetId: {}, FileCount: {}, FileNo: {}",
                files != null ? files.size() : 0, fileNo);

        if (files != null && !files.isEmpty()) {
            log.info("##### Starting {} file processing for {} - TargetId: {}, FileCount: {}", files.size());

            // 파일 저장 경로 설정
            String fullPath = Path.of(uploadPath, getUploadPathByWorkType(FileUploadType.DailyReport, cntrctNo)).toString()
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

                    // Integer fileNo = (fileNoList != null && fileNoList.size() > i) ?
                    // fileNoList.get(i) : null;

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
                    log.info("##### attachment object created for file: {} - FileNo: {}, FileSize: {}",
                            fileName, fileNo, cwAttachments.getFileSize());
                } catch (GaiaBizException e) {
                    log.error("##### Error processing {} file {}", fileName, e.getMessage());
                } catch (IOException e) {
                    throw new GaiaBizException(ErrorType.INTERFACE,"DailyreportService.insertFileInfoToApi() [Api통신 오류] : "+e.getMessage(),e);
                }
            }

            if (!cwAttachmentsList.isEmpty()) {
                try {
                    log.info("##### Saving {} attachments to database", cwAttachmentsList.size());
                    Integer savedFileNo = createCwAttachmentsList(cwAttachmentsList);
                    log.info("##### Successfully saved {} attachments with FileNo: {}", cwAttachmentsList.size(),
                            savedFileNo);
                } catch (GaiaBizException e) {
                    log.error("##### Error saving attachments to database: {}", e.getMessage());
                }
            } else {
                log.warn("##### No valid {} attachments to save");
            }
        } else {
            log.info("##### No {} files to process for {} - TargetId: {}");
        }

        log.info("##### FILE PROCESSING END - Type: {}, TargetId: {}, ProcessedCount: {}", cwAttachmentsList.size());
    }


    /**
     * 결재요청 삭제 -> 작업일지 컬럼 값 삭제 or 데이터 삭제
     *
     * @param apDocList
     * @param usrId
     * @param toApi
     */
    public void updateDailyReportApprovalReqCancel(List<ApDoc> apDocList, String usrId, boolean toApi) {
        apDocList.forEach(apDoc -> {
            CwDailyReport cwDailyReport = findByApDocId(apDoc.getApDocId());

            if (cwDailyReport == null)
                return;

            if (toApi) {
                // api 통신 true -> 데이터 삭제
                delDailyReportRefData(cwDailyReport.getCntrctNo(), cwDailyReport.getDailyReportId());
                deleteDailyReport(cwDailyReport);

            } else {
                // api 통신 false -> 컬럼 값 변경
                updateApprovalStausCancel(cwDailyReport, usrId);
            }
        });
    }


    // 완성된 작업일지 문서 DISK 저장 및 DB 업데이트
    public Map<String, String> updateDiskFileInfo(List<MultipartFile> pdfFile, Long dailyReportId, String accessToken) {
        Map<String, String> result = new HashMap<>();

        log.info("updateDiskFileInfo: 작업일지 문서 DISK 저장 및 업데이트 dailyReportId = {}", dailyReportId);

        // 기본 정보 조회
        Map<String, Object> resultMap = selectDailyReportSimple(dailyReportId);
        log.info("updateDiskFileInfo: 작업일지 dailyReportId 로 cntrctNo, pjtNo 조회 성공 dailyreport = {}, result = {}", dailyReportId, resultMap);

        CwDailyReport report = getDailyReports(MapUtils.getString(resultMap, "cntrct_no"), dailyReportId);
        log.info("updateDiskFileInfo: 작업일지 조회 성공 result = {}", report);


        // 통합문서 관리 PdF 속성 데이터 저장
        // 속성 코드 조회
        SmComCode smComCode = commonCodeService.getCommonCodeByGrpCdAndCmnCd(CommonCodeConstants.DOCUMENT_NAVI_FOLDER_TYPE_GROUP_CODE, "1");

        // 속성 코드 조회
        HashMap<String,Object> docResult = documentManageService.getCbgnAndProperties("APP02");
        List<DocumentForm.PropertyData> propertyData = new ArrayList<>();
        List<CbgnPropertyDto> properties = new ArrayList<>();
        if(docResult.get("properties") != null) {
            // 속성 데이터 저장
            properties = (List<CbgnPropertyDto>) docResult.get("properties");
            propertyData = this.savePdfPropertyDataToDoc(properties, report);
        } else {
            throw new GaiaBizException(ErrorType.NO_DATA, "속성 코드가 존재하지 않습니다.");
        }

        final String navId = String.format("nav_%s_%s_01", MapUtils.getString(resultMap, "cntrct_no"), smComCode.getAttrbtCd3());

        DocumentForm.DocCreateEx requestParams = new DocumentForm.DocCreateEx();
        requestParams.setNaviId(navId);
        requestParams.setNaviDiv("01");
        requestParams.setPjtNo(MapUtils.getString(resultMap, "pjt_no"));
        requestParams.setCntrctNo(MapUtils.getString(resultMap, "cntrct_no"));
        requestParams.setNaviPath("작업일지");
        requestParams.setNaviNm("작업일지");
        requestParams.setUpNaviNo(0);
        requestParams.setUpNaviId("");
        requestParams.setNaviLevel((short) 1);
        requestParams.setNaviType("FOLDR");
        requestParams.setNaviFolderType("1");
        requestParams.setNaviFolderKind(smComCode.getAttrbtCd3());
        requestParams.setProperties(
                commonCodeService.createPropertyListForCommonCode(smComCode.getCmnGrpCd(), smComCode.getCmnCd(), navId));
        requestParams.setPropertyData(propertyData);
        requestParams.setRgstrId(report.getRgstrId());
        requestParams.setDocNm(report.getReportNo()+".pdf");

        Map<String, String> newHeaders = Maps.newHashMap();
        newHeaders.put("x-auth", accessToken);

        log.info("updateDiskFileInfo: 작업일지 PDF 문서 및 속성 저장 param = {}", requestParams);
        List<DcStorageMain> createFileResultList = documentServiceClient.createFile(requestParams, pdfFile, newHeaders);


        // 작업일지에 docId 업데이트
        if(createFileResultList != null) {
            DcStorageMain dcStorageMain = createFileResultList.get(0);
            result.put("cntrctNo", MapUtils.getString(resultMap, "cntrct_no"));
            result.put("dailyReportId", dailyReportId.toString());
            result.put("docId", dcStorageMain.getDocId());
        }
        return result;

    }


    // 통합문서관리의 속성 데이터 저장
    public List<DocumentForm.PropertyData> savePdfPropertyDataToDoc(List<CbgnPropertyDto> properties, CwDailyReport report) {
        log.info("savePdfPropertyDataToDoc: 통합문서관리 속성 데이터 저장 properties = {}, cwDailyReport = {}", properties, report);
        List<DocumentForm.PropertyData> insertList = new ArrayList<>();
        try {

            for (CbgnPropertyDto property : properties) {
                String attrbtCd = property.getAttrbtCd();
                String apprvlStatsTxt = "A".equals(report.getApprvlStats()) ? "승인" : "반려";
                if (attrbtCd != null) {
                    String attrbtCntnts =
                        attrbtCd.equals("workReportNo")         ? report.getReportNo() :
                        attrbtCd.equals("dailyReportDate")      ? report.getDailyReportDate() :
                        attrbtCd.equals("title")                ? report.getTitle() :
                        attrbtCd.equals("apprvlStatsTxt")       ? apprvlStatsTxt :
                        null;

                    if (attrbtCntnts != null && !attrbtCntnts.isBlank()) {
                        DocumentForm.PropertyData row = new DocumentForm.PropertyData();
                        row.setAttrbtCd(attrbtCd);
                        row.setAttrbtCntnts(attrbtCntnts);
                        row.setRgstrId(report.getRgstrId());
                        row.setChgId(report.getChgId());

                        insertList.add(row);
                    }
                }
            }
            log.info("savePdfPropertyDataToDoc: 데이터 저장 결과 = {}", insertList);

        } catch (GaiaBizException e) {
            log.warn("savePdfPropertyDataToDoc: 통합문서관리 속성 데이터 저장 중 오류 발생 메세지 = {}", e.getMessage());
            insertList = null;
        }
        return insertList;
    }


    /**
     * 작업일지 연계데이터 삭제
     *
     * @param cntrctNo
     * @param dailyReportId
     */
    public void delDailyReportRefData(String cntrctNo, Long dailyReportId) {
        // Activity 데이터 논리삭제
        List<CwDailyReportActivity> activities = getDailyReportActivities(cntrctNo, dailyReportId);
        activities.forEach(activity -> {
            updateDeleteForDailyReportActivity(activity);
        });

        // QDB 데이터 논리삭제
        List<CwDailyReportQdb> qdbList = getDailyReportQdb(cntrctNo, dailyReportId);
        qdbList.forEach(qdb -> {
            updateDeleteForDailyReportQdb(qdb);
        });

        // Resource 데이터 논리삭제
        List<CwDailyReportResource> resources = getDailyReportResources(cntrctNo, dailyReportId);
        resources.forEach(resource -> {
            updateDeleteForDailyReportResource(resource);
        });

        // Photo 데이터 논리삭제
        List<CwDailyReportPhoto> photos = getDailyReportPhotos(cntrctNo, dailyReportId);
        photos.forEach(photo -> {
            updateDeleteForDailyReportPhoto(photo);
        });

        // Attachments 데이터 논리삭제
        List<CwAttachments> attachments = getAttachments(
                photos.stream()
                        .map(CwDailyReportPhoto::getAtchFileNo)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet())
        );

        attachments.forEach(attachment -> {
            updateDeleteForAttachments(attachment);
        });
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
     * 작업일지 연계 데이터 조회
     *
     * @param cntrctNo
     * @param dailyReportId
     * @return
     */
    public Map<String, Object> selectDailyreportResource(String cntrctNo, Long dailyReportId) {
        List<CwDailyReportActivity> cwDailyReportActivity = getDailyReportActivities(cntrctNo, dailyReportId);

        List<CwDailyReportPhoto> cwDailyReportPhoto = getDailyReportPhotos(cntrctNo, dailyReportId);

        List<CwDailyReportResource> cwDailyReportResource = getDailyReportResources(cntrctNo, dailyReportId);

        List<CwDailyReportQdb> cwDailyReportQdb = getDailyReportQdb(cntrctNo, dailyReportId);

        // 작업일지 첨부파일 (파일 객체 대신 메타데이터만 전송)
        List<Map<String, Object>> dailyReportFileInfo = Collections.emptyList();
        Set<Integer> atchFileNos = cwDailyReportPhoto.stream()
                .map(CwDailyReportPhoto::getAtchFileNo)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (!atchFileNos.isEmpty()) {
            log.info("##### Found atchFileNo list in photos: {}", atchFileNos);

            List<CwAttachments> dailyReportFiles = getAttachments(atchFileNos);
            log.info("##### Found {} safety attachment files", dailyReportFileInfo.size());

            dailyReportFileInfo = this.convertToFileInfo(dailyReportFiles);
        } else {
            log.info("##### No atchFileNo found in inspection photos");
        }

        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("activity", cwDailyReportActivity);
        returnMap.put("photo", cwDailyReportPhoto);
        returnMap.put("qdb", cwDailyReportQdb);
        returnMap.put("resource", cwDailyReportResource);
        returnMap.put("dailyReportFileInfo", dailyReportFileInfo);
        return returnMap;
    }

    /**
     * rId 리스트로 ap_doc_id 조회 (MyBatis)
     *
     * @return
     */
    public List<String> getApDocIds(Map<String, Object> param) {
        return mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.dailyreport.getApDocIds", 
                param);
    }

    /**
     * 완료된 액티비티 조회
     *
     * @return
     */
    public List<Map<String, Object>> getReseetP6(Map<String, Object> param) {
                
        return mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.dailyreport.getReseetP6", 
                param);
    }

    /**
     * 4. CW_DAILY_REPORT_ACTIVITY 초기화 진행
     * 
     * @param paramMap
     * @return
     */
    public int resetDailyReportActivity(Map<String, Object> paramMap) {
        return mybatisSession.update(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.dailyreport.resetDailyReportActivity", 
                paramMap);
    }

    /**
     * 5. PR_ACTIVITY 초기화 진행
     * 
     * @param paramMap
     * @return
     */
    public int resetPrActivity(Map<String, Object> paramMap) {
        return mybatisSession.update(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.dailyreport.resetPrActivity", 
                paramMap);
    }

    /**
     * 일위대가 자원 등록 
     * ct_cbs_resource 데이터 생성
     * 
     * @param resourceList
     */
    public void insertCtCbsResourceFromManualResource(List<Map<String, Object>> resourceList) {
        mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.dailyreport.insertCtCbsResourceFromManual", resourceList);
    }

    // 액티비티명 조회
    public List<Map<String,Object>> getActivityNm(String cntrctNo, Long dailyReportId, String workDtType) {
        DailyreportFormTypeSelectInput dailyreportFormTypeSelectInput = new DailyreportFormTypeSelectInput();

        dailyreportFormTypeSelectInput.setCntrctNo(cntrctNo);
        dailyreportFormTypeSelectInput.setDailyReportId(dailyReportId);
        dailyreportFormTypeSelectInput.setWorkDtType(workDtType);

        return mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.dailyreport.selectActivityNm",
                dailyreportFormTypeSelectInput);
    }

    // 금일 액티비티 조회
    public List selectTodayActivityList(String cntrctNo, Long dailyReportId, String workDtType) {
        DailyreportFormTypeSelectInput dailyreportFormTypeSelectInput = new DailyreportFormTypeSelectInput();

        dailyreportFormTypeSelectInput.setCntrctNo(cntrctNo);
        dailyreportFormTypeSelectInput.setDailyReportId(dailyReportId);
        dailyreportFormTypeSelectInput.setWorkDtType(workDtType);

        return mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.dailyreport.selectTodayActivityList",
                dailyreportFormTypeSelectInput);
    }

    // 현재 게시물 이전 게시물이 직전일자인지 확인
    public Boolean selectPrevDailyReportExist(String cntrctNo, Long dailyReportId) {
        Map map = new HashMap();
        map.put("dailyReportId", dailyReportId);
        map.put("cntrctNo", cntrctNo);
        return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.dailyreport.selectPrevDailyReportExist", map);
    }

    // 금일 액티비티에 넣을 pr_activity 조회
    public List selectTodayActivityFromPrActivity(String cntrctNo, Long dailyReportId) {
        DailyreportFormTypeSelectInput dailyreportFormTypeSelectInput = new DailyreportFormTypeSelectInput();

        dailyreportFormTypeSelectInput.setCntrctNo(cntrctNo);
        dailyreportFormTypeSelectInput.setDailyReportId(dailyReportId);

        return mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.dailyreport.selectTodayActivityFromPrActivity",
                dailyreportFormTypeSelectInput);
    }

    public List selectQdbList(String cntrctNo, String activityId, BigDecimal todayBohal, String searchText){
        Map map = new HashMap();
        map.put("cntrctNo", cntrctNo);
        map.put("activityId", activityId);
        map.put("todayBohal", todayBohal);
        map.put("searchText", searchText);

        return mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.dailyreport.selectQdbList",
                map);
    }
    public List selectResourceByActivity(String cntrctNo, Long dailyReportId, List<Map<String, Object>> activityIdAndTodayBohal){
        Map map = new HashMap();
        map.put("cntrctNo", cntrctNo);
        map.put("dailyReportId", dailyReportId);
        map.put("activityIdAndTodayBohal", activityIdAndTodayBohal);        // activityId, todayBohal

        return mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.dailyreport.selectResourceByActivity",
                map);
    }
    public List selectTodayResourceList(String cntrctNo, Long dailyReportId){
        Map map = new HashMap();
        map.put("cntrctNo", cntrctNo);
        map.put("dailyReportId", dailyReportId);

        return mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.dailyreport.selectTodayResourceList",
                map);
    }

    public void addTodayResource(CwDailyReportActivity cwDailyReportActivity, String dt) {
        Map map = new HashMap();
        map.put("cntrctNo", cwDailyReportActivity.getCntrctNo());
        map.put("dailyReportId", cwDailyReportActivity.getDailyReportId());
        map.put("dailyReportDate", dt);
        map.put("usrId", UserAuth.get(true).getUsrId());

        mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.dailyreport.addTodayResource",
                map);

    }
}
