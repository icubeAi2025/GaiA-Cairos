package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;



import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.transaction.annotation.Transactional;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwDailyReportActivity;

@Repository
public interface CwDailyReportActivityRepository extends JpaRepository<CwDailyReportActivity, String>, JpaSpecificationExecutor<CwDailyReportActivity>, JpaLogicalDeleteable<CwDailyReportActivity> {

    List<CwDailyReportActivity> findByCntrctNoAndDailyReportId(String cntrctNo, Long dailyReportId);

    List<CwDailyReportActivity> findByCntrctNoAndDailyReportIdAndWorkDtType(String cntrctNo, Long dailyReportId, String type);

    @Query(value = "SELECT c.* from cw_daily_report_activity c WHERE c.cntrct_no = :cntrctNo AND c.daily_report_id = :dailyReportId AND c.work_dt_type = :workDtType",
            nativeQuery = true)
	List<CwDailyReportActivity> findByNativeQuery(@Param("cntrctNo") String cntrctNo, @Param("dailyReportId") Long dailyReportId, @Param("workDtType") String workDtType);

    Optional<CwDailyReportActivity> findByCntrctNoAndDailyReportIdAndDailyActivityId(String cntrctNo, Long dailyReportId, Integer dailyActivityId);

    List<CwDailyReportActivity> findByCntrctNoAndDailyReportIdAndDltYn(String cntrctNo, Long dailyReportId, String dltYn);

    @Modifying
    @Transactional
    @Query("DELETE CwDailyReportActivity cdra WHERE cdra.cntrctNo = :cntrctNo AND cdra.dailyReportId = :dailyReportId")
    void deleteDailyReportActivity(@Param("cntrctNo") String cntrctNo, @Param("dailyReportId") Long dailyReportId);
    
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM cw_daily_report_qdb c WHERE c.cntrct_no = :cntrctNo AND c.daily_report_id = :dailyReportId", nativeQuery = true)
    void deleteDailyReportQdb(@Param("cntrctNo") String cntrctNo, @Param("dailyReportId") Long dailyReportId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM cw_daily_report_qdb c WHERE c.cntrct_no = :cntrctNo AND c.daily_report_id = :dailyReportId AND c.activity_id = :activityId", nativeQuery = true)
    void deleteDailyReportQdbByActivityId(@Param("cntrctNo") String cntrctNo, @Param("dailyReportId") Long dailyReportId, @Param("activityId") String activityId);
    /*
    @Query("SELECT COALESCE(SUM(c.ppaymnyAmt), 0) FROM CwDailyReportActivity c WHERE c.cntrctNo = :cntrctNo AND c.payType = :payType AND c.dltYn='N' AND c.ppaymnySno < :ppaymnySno")
    Long getSumUpdatePpaymnyAmt(@Param("cntrctNo") String cntrctNo, @Param("payType") String payType, @Param("ppaymnySno") Long ppaymnySno);
    
    

    @Modifying
    @Transactional
    @Query("UPDATE CwDailyReportActivity cfm SET cfm.acmtlPpaymnyAmt = cfm.acmtlPpaymnyAmt - :acmtlPpaymnyAmt WHERE cfm.payType = :payType AND cfm.dltYn='N' AND cfm.ppaymnySno > :ppaymnySno")
    void updateDelAcmtlPpaymnyAmt(@Param("acmtlPpaymnyAmt") Long acmtlPpaymnyAmt, @Param("payType") String payType, @Param("ppaymnySno") Long ppaymnySno);


    @Modifying
    @Transactional
    @Query("UPDATE CwDailyReportActivity cfm SET cfm.acmtlPpaymnyAmt = cfm.acmtlPpaymnyAmt + :acmtlPpaymnyAmt WHERE cfm.payType = :payType AND cfm.dltYn='N' AND cfm.ppaymnySno > :ppaymnySno")
    void updateCalcAcmtlPpaymnyAmt(@Param("acmtlPpaymnyAmt") Long acmtlPpaymnyAmt, @Param("payType") String payType, @Param("ppaymnySno") Long ppaymnySno);

    @Modifying
    @Transactional
    @Query("UPDATE CwDailyReportActivity cfm SET cfm.apprvlStats = :apprvlStats, cfm.apprvlReqId = :apprvlReqId, cfm.apprvlReqDt  = :apprvlReqDt WHERE cfm.cntrctNo = :cntrctNo AND cfm.ppaymnySno = :ppaymnySno")
    void updateByCntrctNoAndPpaymnySno(@Param("apprvlStats") String apprvlStats, @Param("apprvlReqId") String apprvlReqId,
    		@Param("apprvlReqDt") LocalDateTime apprvlReqDt, @Param("cntrctNo") String cntrctNo, @Param("ppaymnySno") Long ppaymnySno);

    @Modifying
    @Transactional
    @Query("UPDATE CwDailyReportActivity cfm SET cfm.apprvlStats = :apprvlStats, cfm.apprvlId = :apprvlId, cfm.apprvlDt  = :apprvlDt WHERE cfm.cntrctNo = :cntrctNo AND cfm.ppaymnySno = :ppaymnySno")
    void updateByCntrctNoAndPpaymnySnoAnyType(@Param("apprvlStats") String apprvlStats, @Param("apprvlId") String apprvlId,
    		@Param("apprvlDt") LocalDateTime apprvlDt, @Param("cntrctNo") String cntrctNo, @Param("ppaymnySno") Long ppaymnySno);
*/
}