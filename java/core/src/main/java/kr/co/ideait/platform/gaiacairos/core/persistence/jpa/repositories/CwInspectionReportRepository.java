package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwInspectionReport;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;

public interface CwInspectionReportRepository
                extends JpaRepository<CwInspectionReport, String>, JpaSpecificationExecutor<CwInspectionReport>,
                JpaLogicalDeleteable<CwInspectionReport> {

        @Query("SELECT c.dailyReportId FROM CwInspectionReport c WHERE c.cntrctNo = :cntrctNo AND c.reportNo = :reportNo AND c.dltYn='N'")
        Long findReportNoByCntrctNo(@Param("cntrctNo") String cntrctNo, @Param("reportNo") String reportNo);

        CwInspectionReport findByCntrctNoAndDailyReportIdAndDltYn(@Param("cntrctNo") String cntrctNo,
                        @Param("dailyReportId") Long dailyReportId, @Param("dltYn") String dltYn);

        Optional<CwInspectionReport> findByApDocId(String apDocId);

        @Modifying
        @Query("UPDATE CwInspectionReport cir SET cir.apprvlStats = :apprvlStats, cir.apprvlReqId = :apprvlReqId, cir.apprvlReqDt  = :apprvlReqDt, cir.apDocId = :apDocId, cir.chgId = :usrId, cir.chgDt = CURRENT_TIMESTAMP  WHERE cir.cntrctNo = :cntrctNo AND cir.dailyReportId = :dailyReportId")
        void updateApprovalStausCancel(@Param("apprvlStats") String apprvlStats,
                        @Param("apprvlReqId") String apprvlReqId, @Param("apprvlReqDt") LocalDateTime apprvlReqDt,
                        @Param("apDocId") String apDocId, @Param("cntrctNo") String cntrctNo,
                        @Param("dailyReportId") Long dailyReportId, @Param("usrId") String usrId);

        List<CwInspectionReport> findByCntrctNoAndDailyReportId(@Param("cntrctNo") String cntrctNo,
                        @Param("dailyReportId") Long dailyReportId);

        List<CwInspectionReport> findByCntrctNoAndDailyReportDateAndDltYn(@Param("cntrctNo") String cntrctNo,
                                                                @Param("dailyReporDate") String dailyReporDate, @Param("dltYn") String dltYn);

        CwInspectionReport findByDailyReportDateAndCntrctNoAndWorkCdAndRgstrIdAndDltYn(
                        @Param("dailyReportDate") String dailyReportDate, @Param("cntrctNo") String cntrctNo,
                        @Param("workCd") String workCd, @Param("rgstrId") String rgstrId, @Param("dltYn") String dltYn);

        @Query("SELECT MAX(c.dailyReportId) FROM CwInspectionReport c ")
        Long findMaxDailyReportIdBy();

}
