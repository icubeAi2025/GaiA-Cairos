package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwCfInspectionReport;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;

public interface CwCfInspectionReportRepository
        extends JpaRepository<CwCfInspectionReport, String>, JpaSpecificationExecutor<CwCfInspectionReport>, JpaLogicalDeleteable<CwCfInspectionReport> {

    @Query("SELECT e.dailyReportId FROM CwCfInspectionReport e WHERE e.cntrctNo = :cntrctNo AND e.dailyReportDate = :dailyReportDate AND e.dltYn = 'N'")
    Long findDailyReportIdByCntrctNoAndDailyReportDate(@Param("cntrctNo") String cntrctNo, @Param("dailyReportDate") String dailyReportDate);

    CwCfInspectionReport findByCntrctNoAndDailyReportDateAndDltYn(@Param("cntrctNo") String cntrctNo, @Param("dailyReportDate") String dailyReportDate, @Param("dltYn") String dltYn);

    CwCfInspectionReport findByCntrctNoAndDailyReportId(@Param("cntrctNo") String cntrctNo,@Param("dailyReportId") Long dailyReportId);

    @Query("SELECT COALESCE(MAX(c.dailyReportId), 0) FROM CwCfInspectionReport c")
    Long findMaxDailyReportId();
}
