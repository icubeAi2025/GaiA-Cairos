package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;


import java.util.List;

import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.transaction.annotation.Transactional;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwPayDetail;

@Repository
public interface CwPayDetailRepository extends JpaRepository<CwPayDetail, String>, JpaSpecificationExecutor<CwPayDetail>, JpaLogicalDeleteable<CwPayDetail> {

    List<CwPayDetail> findByCntrctNoAndPayprceSno(String cntrctNo, Long payprceSno);
    

    @Modifying
    @Transactional
    @Query("UPDATE CwPayDetail cpm SET cpm.thtmAcomQty = :thtmAcomQty WHERE cpm.cntrctNo = :cntrctNo AND cpm.payprceSno = :payprceSno AND cpm.rsceCd = :rsceCd")
    void updateByCntrctNoAndPayprceSno(@Param("thtmAcomQty") Float thtmAcomQty, @Param("cntrctNo") String cntrctNo, @Param("payprceSno") Long payprceSno, @Param("rsceCd") String rsceCd);

}