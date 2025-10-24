package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwCfInspectionReportActivity;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CwCfInspectionReportActivityRepository
        extends JpaRepository<CwCfInspectionReportActivity, String>, JpaSpecificationExecutor<CwCfInspectionReportActivity>, JpaLogicalDeleteable<CwCfInspectionReportActivity> {

    CwCfInspectionReportActivity findByCntrctNoAndDailyReportIdAndDailyActivityIdAndDltYn(@Param("cntrctNo") String cntrctNo,@Param("dailyReportId") Long dailyReportId,@Param("dailyActivityId") Integer dailyActivityId,@Param("dltYn") String dltYn);

    List <CwCfInspectionReportActivity> findByCntrctNoAndDailyReportId(@Param("cntrctNo") String cntrctNo,@Param("dailyReportId") Long dailyReportId);
}
