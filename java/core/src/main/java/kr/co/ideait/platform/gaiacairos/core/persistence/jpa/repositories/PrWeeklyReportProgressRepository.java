package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.PrWeeklyReportProgress;

public interface PrWeeklyReportProgressRepository extends JpaRepository<PrWeeklyReportProgress, String>, JpaSpecificationExecutor<PrWeeklyReportProgress>, JpaLogicalDeleteable<PrWeeklyReportProgress> {

	PrWeeklyReportProgress findByWeeklyCnsttyId(Long weeklyCnsttyId);

	List<PrWeeklyReportProgress> findByCntrctChgIdAndWeeklyReportIdAndDltYn(String cntrctChgId, Long weeklyReportId, String dltYn);

}
