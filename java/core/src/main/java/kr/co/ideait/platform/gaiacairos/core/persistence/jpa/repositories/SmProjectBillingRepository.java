package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmProjectBilling;

@Repository
public interface SmProjectBillingRepository
                extends JpaRepository<SmProjectBilling, Integer>, JpaSpecificationExecutor<SmProjectBilling>,
                JpaLogicalDeleteable<SmProjectBilling> {


}
