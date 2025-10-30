package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;


import org.springframework.transaction.annotation.Transactional;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwDailyReportActivity;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwInspectionReportActivity;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CwInspectionReportActivityRepository extends JpaRepository<CwInspectionReportActivity, String>, JpaSpecificationExecutor<CwDailyReportActivity>, JpaLogicalDeleteable<CwInspectionReportActivity> {
    List<CwInspectionReportActivity> findByCntrctNoAndDailyReportIdAndDltYn(String cntrctNo, Long dailyReportId, String dltYn);

    List<CwInspectionReportActivity> findByCntrctNoAndDailyReportId(String cntrctNo, Long dailyReportId);

    @Transactional
    @Modifying
    @Query("DELETE CwInspectionReportActivity cira WHERE cira.cntrctNo = :cntrctNo AND cira.dailyReportId = :dailyReportId")
    void deleteActivityByCntrctNoAndDailyReportId(@Param("cntrctNo") String cntrctNo, @Param("dailyReportId") Long dailyReportId);
}