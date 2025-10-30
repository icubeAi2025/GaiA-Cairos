package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;



import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.transaction.annotation.Transactional;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwDailyReport;

@Repository
public interface CwDailyReportRepository extends JpaRepository<CwDailyReport, String>, JpaSpecificationExecutor<CwDailyReport>, JpaLogicalDeleteable<CwDailyReport> {

    @Query("SELECT c.dailyReportId FROM CwDailyReport c WHERE c.cntrctNo = :cntrctNo AND c.reportNo = :reportNo AND c.dltYn='N'")
    Long findReportNoByCntrctNo(@Param("cntrctNo") String cntrctNo, @Param("reportNo") String reportNo);


    @Query("SELECT c.dailyReportId FROM CwDailyReport c WHERE c.cntrctNo = :cntrctNo AND c.dailyReportDate = :dailyReportDate AND c.dltYn='N'")
    Long findDailyReportDateByCntrctNo(@Param("cntrctNo") String cntrctNo, @Param("dailyReportDate") String dailyReportDate);

    @Query("SELECT COALESCE(MAX(c.dailyReportId), 0) FROM CwDailyReport c")
    Long findMaxIdByCntrctNo();

    CwDailyReport findByCntrctNoAndDailyReportId(String cntrctNo, Long dailyReportId);

    CwDailyReport findByCntrctNoAndDailyReportIdAndDltYn(String cntrctNo, Long dailyReportId, String dltYn);

    @Modifying
    @Transactional
    @Query("UPDATE CwDailyReport cdr SET cdr.apprvlStats = :apprvlStats, cdr.apprvlReqId = :apprvlReqId, cdr.apprvlReqDt  = :apprvlReqDt WHERE cdr.cntrctNo = :cntrctNo AND cdr.dailyReportId = :dailyReportId")
    void updateByCntrctNoAndDailyReportId(@Param("apprvlStats") String apprvlStats, @Param("apprvlReqId") String apprvlReqId,
                                          @Param("apprvlReqDt") LocalDateTime apprvlReqDt, @Param("cntrctNo") String cntrctNo, @Param("dailyReportId") Long dailyReportId);

    @Modifying
    @Transactional
    @Query("UPDATE CwDailyReport cdr SET cdr.apprvlStats = :apprvlStats, cdr.apprvlId = :apprvlId, cdr.apprvlDt  = :apprvlDt WHERE cdr.cntrctNo = :cntrctNo AND cdr.dailyReportId = :dailyReportId")
    void updateByCntrctNoAndDailyReportIdAnyType(@Param("apprvlStats") String apprvlStats, @Param("apprvlId") String apprvlId,
                                                 @Param("apprvlDt") LocalDateTime apprvlDt, @Param("cntrctNo") String cntrctNo, @Param("dailyReportId") Long dailyReportId);

    Optional<CwDailyReport> findByApDocId(String apDocId);

    CwDailyReport findByCntrctNoAndDailyReportDateAndDltYn(String cntrctNo, String dailyReportDate, String dltYn);

    @Modifying
    @Transactional
    @Query("UPDATE CwDailyReport cdr SET cdr.apprvlStats = :apprvlStats, cdr.apprvlReqId = :apprvlReqId, cdr.apprvlReqDt  = :apprvlReqDt, cdr.apDocId = :apDocId, cdr.chgId = :usrId, cdr.chgDt = CURRENT_TIMESTAMP  WHERE cdr.cntrctNo = :cntrctNo AND cdr.dailyReportId = :dailyReportId")
    void updateApprovalStausCancel(@Param("apprvlStats") String apprvlStats
            , @Param("apprvlReqId") String apprvlReqId
            , @Param("apprvlReqDt") LocalDateTime apprvlReqDt
            , @Param("apDocId") String apDocId
            , @Param("cntrctNo") String cntrctNo
            , @Param("dailyReportId") Long dailyReportId
            , @Param("usrId") String usrId);

    @Modifying
    @Transactional
    @Query("DELETE FROM CwDailyReport cdr WHERE cdr.cntrctNo = :cntrctNo AND cdr.dailyReportId = :dailyReportId")
    void deleteDailyReport(@Param("cntrctNo") String cntrctNo, @Param("dailyReportId") Long dailyReportId);

    /*
    @Query("SELECT COALESCE(SUM(c.ppaymnyAmt), 0) FROM CwDailyReport c WHERE c.cntrctNo = :cntrctNo AND c.payType = :payType AND c.dltYn='N'")
    Long getSumPpaymnyAmt(@Param("cntrctNo") String cntrctNo, @Param("payType") String payType);


    @Query("SELECT COALESCE(SUM(c.ppaymnyAmt), 0) FROM CwDailyReport c WHERE c.cntrctNo = :cntrctNo AND c.payType = :payType AND c.dltYn='N' AND c.ppaymnySno < :ppaymnySno")
    Long getSumUpdatePpaymnyAmt(@Param("cntrctNo") String cntrctNo, @Param("payType") String payType, @Param("ppaymnySno") Long ppaymnySno);



    @Modifying
    @Transactional
    @Query("UPDATE CwDailyReport cfm SET cfm.acmtlPpaymnyAmt = cfm.acmtlPpaymnyAmt - :acmtlPpaymnyAmt WHERE cfm.payType = :payType AND cfm.dltYn='N' AND cfm.ppaymnySno > :ppaymnySno")
    void updateDelAcmtlPpaymnyAmt(@Param("acmtlPpaymnyAmt") Long acmtlPpaymnyAmt, @Param("payType") String payType, @Param("ppaymnySno") Long ppaymnySno);


    @Modifying
    @Transactional
    @Query("UPDATE CwDailyReport cfm SET cfm.acmtlPpaymnyAmt = cfm.acmtlPpaymnyAmt + :acmtlPpaymnyAmt WHERE cfm.payType = :payType AND cfm.dltYn='N' AND cfm.ppaymnySno > :ppaymnySno")
    void updateCalcAcmtlPpaymnyAmt(@Param("acmtlPpaymnyAmt") Long acmtlPpaymnyAmt, @Param("payType") String payType, @Param("ppaymnySno") Long ppaymnySno);

    @Modifying
    @Transactional
    @Query("UPDATE CwDailyReport cfm SET cfm.apprvlStats = :apprvlStats, cfm.apprvlReqId = :apprvlReqId, cfm.apprvlReqDt  = :apprvlReqDt WHERE cfm.cntrctNo = :cntrctNo AND cfm.ppaymnySno = :ppaymnySno")
    void updateByCntrctNoAndPpaymnySno(@Param("apprvlStats") String apprvlStats, @Param("apprvlReqId") String apprvlReqId,
    		@Param("apprvlReqDt") LocalDateTime apprvlReqDt, @Param("cntrctNo") String cntrctNo, @Param("ppaymnySno") Long ppaymnySno);

    @Modifying
    @Transactional
    @Query("UPDATE CwDailyReport cfm SET cfm.apprvlStats = :apprvlStats, cfm.apprvlId = :apprvlId, cfm.apprvlDt  = :apprvlDt WHERE cfm.cntrctNo = :cntrctNo AND cfm.ppaymnySno = :ppaymnySno")
    void updateByCntrctNoAndPpaymnySnoAnyType(@Param("apprvlStats") String apprvlStats, @Param("apprvlId") String apprvlId,
    		@Param("apprvlDt") LocalDateTime apprvlDt, @Param("cntrctNo") String cntrctNo, @Param("ppaymnySno") Long ppaymnySno);
*/


}