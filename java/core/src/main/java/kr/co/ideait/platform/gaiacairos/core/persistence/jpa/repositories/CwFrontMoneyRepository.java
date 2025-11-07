package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;


import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwFrontMoney;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CwFrontMoneyRepository extends JpaRepository<CwFrontMoney, String>, JpaSpecificationExecutor<CwFrontMoney>, JpaLogicalDeleteable<CwFrontMoney> {

    @Query("SELECT COALESCE(MAX(c.ppaymnySno), 0) FROM CwFrontMoney c WHERE c.cntrctNo = :cntrctNo")
    Long findMaxSnoByCntrctNo(@Param("cntrctNo") String cntrctNo);

    @Query("SELECT COALESCE(SUM(c.ppaymnyAmt), 0) FROM CwFrontMoney c WHERE c.cntrctNo = :cntrctNo AND c.payType = :payType AND c.dltYn='N'")
    Long getSumPpaymnyAmt(@Param("cntrctNo") String cntrctNo, @Param("payType") String payType);


    @Query("SELECT COALESCE(SUM(c.ppaymnyAmt), 0) FROM CwFrontMoney c WHERE c.cntrctNo = :cntrctNo AND c.payType = :payType AND c.dltYn='N' AND c.ppaymnySno < :ppaymnySno")
    Long getSumUpdatePpaymnyAmt(@Param("cntrctNo") String cntrctNo, @Param("payType") String payType, @Param("ppaymnySno") Long ppaymnySno);
    
    Optional<CwFrontMoney> findByCntrctNoAndPpaymnySno(String cntrctNo, Long ppaymnySno);

    @Modifying
    @Transactional
    @Query("UPDATE CwFrontMoney cfm SET cfm.acmtlPpaymnyAmt = cfm.acmtlPpaymnyAmt - :acmtlPpaymnyAmt WHERE cfm.payType = :payType AND cfm.dltYn='N' AND cfm.ppaymnySno > :ppaymnySno")
    void updateDelAcmtlPpaymnyAmt(@Param("acmtlPpaymnyAmt") Long acmtlPpaymnyAmt, @Param("payType") String payType, @Param("ppaymnySno") Long ppaymnySno);


    @Modifying
    @Transactional
    @Query("UPDATE CwFrontMoney cfm SET cfm.acmtlPpaymnyAmt = cfm.acmtlPpaymnyAmt + :acmtlPpaymnyAmt WHERE cfm.payType = :payType AND cfm.dltYn='N' AND cfm.ppaymnySno > :ppaymnySno")
    void updateCalcAcmtlPpaymnyAmt(@Param("acmtlPpaymnyAmt") Long acmtlPpaymnyAmt, @Param("payType") String payType, @Param("ppaymnySno") Long ppaymnySno);

    @Modifying
    @Transactional
    @Query("UPDATE CwFrontMoney cfm SET cfm.apprvlStats = :apprvlStats, cfm.apprvlReqId = :apprvlReqId, cfm.apprvlReqDt  = :apprvlReqDt WHERE cfm.cntrctNo = :cntrctNo AND cfm.ppaymnySno = :ppaymnySno")
    void updateByCntrctNoAndPpaymnySno(@Param("apprvlStats") String apprvlStats, @Param("apprvlReqId") String apprvlReqId,
    		@Param("apprvlReqDt") LocalDateTime apprvlReqDt, @Param("cntrctNo") String cntrctNo, @Param("ppaymnySno") Long ppaymnySno);

    @Modifying
    @Transactional
    @Query("UPDATE CwFrontMoney cfm SET cfm.apprvlStats = :apprvlStats, cfm.apprvlId = :apprvlId, cfm.apprvlDt  = :apprvlDt WHERE cfm.cntrctNo = :cntrctNo AND cfm.ppaymnySno = :ppaymnySno")
    void updateByCntrctNoAndPpaymnySnoAnyType(@Param("apprvlStats") String apprvlStats, @Param("apprvlId") String apprvlId,
    		@Param("apprvlDt") LocalDateTime apprvlDt, @Param("cntrctNo") String cntrctNo, @Param("ppaymnySno") Long ppaymnySno);

	Optional<CwFrontMoney> findByApDocId(String apDocId);

    List<CwFrontMoney> findByCntrctNoAndPayprceSnoAndDltYn(String cntrctNo, Long payprceSno, String dltYn);

    @Modifying
    @Transactional
    @Query("UPDATE CwFrontMoney cfm SET cfm.apprvlStats = :apprvlStats, cfm.apprvlReqId = :apprvlReqId, cfm.apprvlReqDt = :apprvlReqDt, cfm.apDocId = :apDocId, cfm.chgId = :usrId, cfm.chgDt = CURRENT_TIMESTAMP WHERE cfm.cntrctNo = :cntrctNo AND cfm.ppaymnySno = :ppaymnySno")
    void updateApprovalStausCancel(@Param("apprvlStats") String apprvlStats
            , @Param("apprvlReqId") String apprvlReqId
            , @Param("apprvlReqDt") LocalDateTime apprvlReqDt
            , @Param("apDocId") String apDocId
            , @Param("cntrctNo") String cntrctNo
            , @Param("ppaymnySno") Long ppaymnySno
            , @Param("usrId") String usrId);
}