package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.PrMonthlyReportActivity;

@Repository
public interface PrMonthlyReportActivityRepository extends JpaRepository<PrMonthlyReportActivity, String>, JpaSpecificationExecutor<PrMonthlyReportActivity>, JpaLogicalDeleteable<PrMonthlyReportActivity>{

	PrMonthlyReportActivity findByCntrctChgIdAndMonthlyReportIdAndActivityIdAndWorkDtType(String cntrctChgId, Long monthlyReportId, String activityId, String workDtType);

	List<PrMonthlyReportActivity> findByCntrctChgIdAndMonthlyReportId(String cntrctChgId, Long monthlyReportId);

	List<PrMonthlyReportActivity> findByCntrctChgIdAndMonthlyReportIdAndDltYn(String cntrctChgId, Long monthlyReportId, String dltYn);


}
