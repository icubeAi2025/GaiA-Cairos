package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwMainmtrl;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;

public interface CwMainmtrlRepository
        extends JpaRepository<CwMainmtrl, String>, JpaSpecificationExecutor<CwMainmtrl>,
        JpaLogicalDeleteable<CwMainmtrl> {
}
