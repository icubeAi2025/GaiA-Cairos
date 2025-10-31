package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwPayActivity;

@Repository
public interface CwPayActivityRepository extends JpaRepository<CwPayActivity, String>, JpaSpecificationExecutor<CwPayActivity>, JpaLogicalDeleteable<CwPayActivity> {

    List<CwPayActivity> findByCntrctNoAndPayprceSno(String cntrctNo, Long payprceSno);
    
}