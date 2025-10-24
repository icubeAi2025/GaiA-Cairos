package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import org.springframework.transaction.annotation.Transactional;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.PrMonthlyReport;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PrMonthlyReportRepository extends JpaRepository<PrMonthlyReport, Integer>, JpaSpecificationExecutor<PrMonthlyReport>, JpaLogicalDeleteable<PrMonthlyReport>{

	Optional<PrMonthlyReport> findByCntrctChgIdAndMonthlyReportId(String cntrctChgId, Long monthlyReportId);

	@Query("SELECT COALESCE(MAX(monthlyReportId), 0) FROM PrMonthlyReport WHERE cntrctChgId LIKE %:cntrctNo%")
	Long findMaxMonthlyReportIdByCntrctNo(@Param("cntrctNo") String cntrctNo);

	@Query("SELECT COUNT(pm) FROM PrMonthlyReport pm WHERE pm.cntrctChgId IN (SELECT ccc.cntrctChgId FROM CnContractChange ccc WHERE ccc.cntrctNo = :cntrctNo AND ccc.dltYn = 'N') AND pm.reportYm = :reportYm AND pm.dltYn = 'N'")
	int existsByCntrctNoAndReportYm(@Param("cntrctNo") String cntrctNo,  @Param("reportYm") String reportYm);

	Optional<PrMonthlyReport> findByApDocId(String apDocId);

	@Transactional
	@Modifying
	@Query("UPDATE PrMonthlyReport pm SET pm.apprvlStats = :apprvlStats, pm.apprvlReqId = :apprvlReqId, pm.apprvlReqDt = :apprvlReqDt, pm.apDocId = :apDocId, pm.chgId = :usrId, pm.chgDt = CURRENT_TIMESTAMP WHERE pm.cntrctChgId = :cntrctChgId AND pm.monthlyReportId = :monthlyReportId")
	void updateApprovalStausCancel(@Param("apprvlStats") String apprvlStats
			, @Param("apprvlReqId") String apprvlReqId
			, @Param("apprvlReqDt") LocalDateTime apprvlReqDt
			, @Param("apDocId") String apDocId
			, @Param("cntrctChgId") String cntrctChgId
			, @Param("monthlyReportId") Long monthlyReportId
			, @Param("usrId") String usrId);
}
