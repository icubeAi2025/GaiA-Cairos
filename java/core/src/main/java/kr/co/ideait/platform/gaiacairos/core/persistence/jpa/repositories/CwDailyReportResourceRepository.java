package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import java.math.BigDecimal;
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
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwDailyReportResource;

@Repository
public interface CwDailyReportResourceRepository extends JpaRepository<CwDailyReportResource, String>,
        JpaSpecificationExecutor<CwDailyReportResource>, JpaLogicalDeleteable<CwDailyReportResource> {

    Optional<CwDailyReportResource> findByCntrctNoAndDailyReportIdAndRsceSno(String cntrctNo, Long dailyReportId,
            Integer rsceSno);

    Optional<CwDailyReportResource> findByCntrctNoAndDailyReportIdAndRsceSnoAndDltYn(String cntrctNo, Long dailyReportId,
            Integer rsceSno, String dltYn);

    Optional<CwDailyReportResource> findByCntrctNoAndDailyReportIdAndRsceCd(String cntrctNo, Long dailyReportId,
            String rsceCd);

    List<CwDailyReportResource> findByCntrctNoAndDailyReportId(String cntrctNo, Long dailyReportId);

    List<CwDailyReportResource> findByCntrctNoAndDailyReportIdAndDltYn(String cntrctNo, Long dailyReportId, String dltYn);

    @Modifying
    @Transactional
    @Query("UPDATE CwDailyReportResource cdrr SET actualQty = :actualQty, remndrQty = :remndrQty WHERE cdrr.cntrctNo = :cntrctNo AND cdrr.dailyReportId = :dailyReportId AND cdrr.rsceSno = :rsceSno")
    void updateQtyByCntrctNoAndDailyReportIdAndRsceSno(@Param("actualQty") BigDecimal actualQty,
            @Param("remndrQty") BigDecimal remndrQty, @Param("cntrctNo") String cntrctNo,
            @Param("dailyReportId") Long dailyReportId, @Param("rsceSno") Integer rsceSno);

    @Modifying
    @Transactional
    @Query("UPDATE CwDailyReportResource cdrr SET mainRsceDsply = :mainRsceDsply WHERE cdrr.cntrctNo = :cntrctNo AND cdrr.dailyReportId = :dailyReportId AND cdrr.rsceCd = :rsceCd")
    void updateDsplyByCntrctNoAndDailyReportIdAndRsceCd(@Param("mainRsceDsply") String mainRsceDsply,
            @Param("cntrctNo") String cntrctNo, @Param("dailyReportId") Long dailyReportId,
            @Param("rsceCd") String rsceCd);

    @Modifying
    @Transactional
    @Query("DELETE CwDailyReportResource cdrr WHERE cdrr.cntrctNo = :cntrctNo AND cdrr.dailyReportId = :dailyReportId")
    void deleteDailyReportResource(@Param("cntrctNo") String cntrctNo, @Param("dailyReportId") Long dailyReportId);
}