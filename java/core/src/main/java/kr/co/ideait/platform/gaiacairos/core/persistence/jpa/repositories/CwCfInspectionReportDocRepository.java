package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwCfInspectionReportDoc;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CwCfInspectionReportDocRepository
        extends JpaRepository<CwCfInspectionReportDoc, String>, JpaSpecificationExecutor<CwCfInspectionReportDoc>, JpaLogicalDeleteable<CwCfInspectionReportDoc> {

    List <CwCfInspectionReportDoc> findByCntrctNoAndDailyReportIdAndDltYn(@Param("cntrctNo") String cntrctNo,@Param("dailyReportId") Long dailyReportId, @Param("dltYn") String dltYn);

    CwCfInspectionReportDoc findByCntrctNoAndDailyReportIdAndDocId(@Param("cntrctNo") String cntrctNo,@Param("dailyReportId") Long dailyReportId,@Param("docId") Integer docId);

    @Query("SELECT COALESCE(MAX(c.docId), 0) FROM CwCfInspectionReportDoc c WHERE c.cntrctNo = :cntrctNo AND c.dailyReportId = :dailyReportId")
    Integer findMaxDocIdByCntrctNoAndDailyReportId(@Param("cntrctNo") String cntrctNo,@Param("dailyReportId") Long dailyReportId);
}
