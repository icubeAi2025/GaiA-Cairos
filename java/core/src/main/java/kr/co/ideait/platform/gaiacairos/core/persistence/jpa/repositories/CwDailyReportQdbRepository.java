package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwDailyReportQdb;

@Repository
public interface CwDailyReportQdbRepository extends JpaRepository<CwDailyReportQdb, String>,
        JpaSpecificationExecutor<CwDailyReportQdb>, JpaLogicalDeleteable<CwDailyReportQdb> {

    List<CwDailyReportQdb> findByCntrctNoAndDailyReportId(String cntrctNo, Long dailyReportId);
    List<CwDailyReportQdb> findByCntrctNoAndDailyReportIdAndDltYn(String cntrctNo, Long dailyReportId, String dltYn);
}