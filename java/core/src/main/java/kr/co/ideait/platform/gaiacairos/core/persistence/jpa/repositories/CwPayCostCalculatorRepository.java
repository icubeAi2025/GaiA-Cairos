package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwPayCostCalculator;

@Repository
public interface CwPayCostCalculatorRepository extends JpaRepository<CwPayCostCalculator, String>, JpaSpecificationExecutor<CwPayCostCalculator>, JpaLogicalDeleteable<CwPayCostCalculator> {

    List<CwPayCostCalculator> findByCntrctNoAndPayprceSno(String cntrctNo, Long payprceSno);
    
}