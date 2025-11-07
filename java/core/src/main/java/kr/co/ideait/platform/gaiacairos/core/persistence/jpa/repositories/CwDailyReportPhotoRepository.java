package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import java.util.List;

import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.transaction.annotation.Transactional;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwDailyReportPhoto;

@Repository
public interface CwDailyReportPhotoRepository extends JpaRepository<CwDailyReportPhoto, String>,
        JpaSpecificationExecutor<CwDailyReportPhoto>, JpaLogicalDeleteable<CwDailyReportPhoto> {

    List<CwDailyReportPhoto> findByCntrctNoAndDailyReportId(String cntrctNo, Long dailyReportId);

    List<CwDailyReportPhoto> findByCntrctNoAndDailyReportIdAndDltYn(String cntrctNo, Long dailyReportId, String dltYn);

    CwDailyReportPhoto findByCntrctNoAndDailyReportIdAndCnsttyPhtSno(String cntrctNo, Integer dailyReportId,
            Integer cnsttyPhtSno);

    @Query("SELECT COALESCE(MAX(c.cnsttyPhtSno), 0) FROM CwDailyReportPhoto c")
    Integer findMaxId();

    @Modifying
    @Transactional
    @Query("DELETE CwDailyReportPhoto cdrp WHERE cdrp.cntrctNo = :cntrctNo AND cdrp.dailyReportId = :dailyReportId")
    void deleteDailyReportPhoto(@Param("cntrctNo") String cntrctNo, @Param("dailyReportId") Long dailyReportId);
}