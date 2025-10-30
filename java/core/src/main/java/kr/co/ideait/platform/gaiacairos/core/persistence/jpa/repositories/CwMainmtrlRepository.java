package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwMainmtrl;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;

public interface CwMainmtrlRepository
        extends JpaRepository<CwMainmtrl, String>, JpaSpecificationExecutor<CwMainmtrl>,
        JpaLogicalDeleteable<CwMainmtrl> {
        List<CwMainmtrl> findByCntrctNoAndReqfrmNoAndDltYn(@Param("cntrctNo") String cntrctNo, @Param("reqfrmNo") String reqfrmNo, @Param("dltYn") String dltYn);
}
