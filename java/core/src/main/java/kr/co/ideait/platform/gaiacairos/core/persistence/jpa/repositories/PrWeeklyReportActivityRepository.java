package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.PrWeeklyReportActivity;

public interface PrWeeklyReportActivityRepository extends JpaRepository<PrWeeklyReportActivity, String>, JpaSpecificationExecutor<PrWeeklyReportActivity>, JpaLogicalDeleteable<PrWeeklyReportActivity> {

	List<PrWeeklyReportActivity> findByCntrctChgIdAndWeeklyReportIdAndDltYn(String cntrctChgId, Long weeklyReportId, String dltYn);

}
