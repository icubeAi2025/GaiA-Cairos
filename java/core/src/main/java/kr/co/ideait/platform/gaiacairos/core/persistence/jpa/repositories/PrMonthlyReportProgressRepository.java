package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.PrMonthlyReportProgress;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface PrMonthlyReportProgressRepository extends JpaRepository<PrMonthlyReportProgress, String>, JpaSpecificationExecutor<PrMonthlyReportProgress>, JpaLogicalDeleteable<PrMonthlyReportProgress>{

	List<PrMonthlyReportProgress> findByCntrctChgIdAndMonthlyReportIdAndDltYn(String cntrctChgId, Long monthlyReportId,	String dltYn);

	PrMonthlyReportProgress findByMonthlyCnsttyId(Integer monthlyCnsttyId);

}
