package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import org.springframework.transaction.annotation.Transactional;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwInspectionReportPhoto;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CwInspectionReportPhotoRepository extends JpaRepository<CwInspectionReportPhoto, String>,
        JpaSpecificationExecutor<CwInspectionReportPhoto>, JpaLogicalDeleteable<CwInspectionReportPhoto> {
    List<CwInspectionReportPhoto> findByCntrctNoAndDailyReportIdAndDltYn(String cntrctNo, Long dailyReportId,
                                                                         String dltYn);

    @Query("SELECT COALESCE(MAX(p.cnsttyPhtSno), 0) FROM CwInspectionReportPhoto p WHERE p.cntrctNo = :cntrctNo AND p.dailyReportId = :dailyReportId AND p.dltYn = 'N'")
    Long findMaxCnsttyPhtSno(@Param("cntrctNo") String cntrctNo, @Param("dailyReportId") Long dailyReportId);

    List<CwInspectionReportPhoto> findByCntrctNoAndDailyReportId(String cntrctNo, Long dailyReportId);

    @Transactional
    @Modifying
    @Query("DELETE CwInspectionReportPhoto cira WHERE cira.cntrctNo = :cntrctNo AND cira.dailyReportId = :dailyReportId")
    void deletePhotoByCntrctNoAndDailyReportId(@Param("cntrctNo") String cntrctNo, @Param("dailyReportId") Long dailyReportId);
}