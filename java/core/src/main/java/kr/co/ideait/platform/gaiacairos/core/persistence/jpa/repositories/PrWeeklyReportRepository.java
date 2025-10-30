package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import org.springframework.transaction.annotation.Transactional;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.PrWeeklyReport;
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
public interface PrWeeklyReportRepository extends JpaRepository<PrWeeklyReport, Integer>, JpaSpecificationExecutor<PrWeeklyReport>, JpaLogicalDeleteable<PrWeeklyReport> {

	Optional<PrWeeklyReport> findByCntrctChgIdAndWeeklyReportId(String cntrctChgId, Long weeklyReportId);

	@Query("SELECT COUNT(pw) FROM PrWeeklyReport pw WHERE pw.cntrctChgId IN (SELECT ccc.cntrctChgId FROM CnContractChange ccc WHERE ccc.cntrctNo = :cntrctNo AND ccc.dltYn = 'N') AND pw.reportDate = :reportDate AND pw.dltYn = 'N' AND pw.apprvlStats IS NULL")
	int existsByCntrctNoAndReportDate(@Param("cntrctNo") String cntrctNo, @Param("reportDate") String reportDate);

	@Query("SELECT COALESCE(MAX(weeklyReportId), 0) FROM PrWeeklyReport WHERE cntrctChgId LIKE %:cntrctNo%")
	Long findMaxWeeklyReportIdByCntrctNo(@Param("cntrctNo") String cntrctNo);

	Optional<PrWeeklyReport> findByApDocId(String apDocId);

	@Transactional
	@Modifying
	@Query("UPDATE PrWeeklyReport pw SET pw.apprvlStats = :apprvlStats, pw.apprvlReqId = :apprvlReqId, pw.apprvlReqDt = :apprvlReqDt, pw.apDocId = :apDocId, pw.chgId = :usrId, pw.chgDt = CURRENT_TIMESTAMP WHERE pw.cntrctChgId = :cntrctChgId AND pw.weeklyReportId = :weeklyReportId")
	void updateApprovalStausCancel(@Param("apprvlStats") String apprvlStats
			, @Param("apprvlReqId") String apprvlReqId
			, @Param("apprvlReqDt") LocalDateTime apprvlReqDt
			, @Param("apDocId") String apDocId
			, @Param("cntrctChgId") String cntrctChgId
			, @Param("weeklyReportId") Long weeklyReportId
			, @Param("usrId") String usrId
	);
}
