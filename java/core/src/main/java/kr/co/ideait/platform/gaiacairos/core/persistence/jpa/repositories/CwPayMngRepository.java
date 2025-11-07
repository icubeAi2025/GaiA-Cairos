package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;


import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwPayMng;

@Repository
public interface CwPayMngRepository extends JpaRepository<CwPayMng, String>, JpaSpecificationExecutor<CwPayMng>, JpaLogicalDeleteable<CwPayMng> {

    @Query("SELECT COALESCE(MAX(c.payprceSno), 0) FROM CwPayMng c WHERE c.cntrctNo = :cntrctNo")
    Long findMaxSnoByCntrctNo(@Param("cntrctNo") String cntrctNo);

    Optional<CwPayMng> findByCntrctNoAndPayprceSno(String cntrctNo, Long payprceSno);

    Optional<CwPayMng> findByCntrctChgIdAndPayprceTmnumAndDltYn(String cntrctChgId, Long payprceTmnum, String dltYn);


    @Modifying
    @Query("UPDATE CwPayMng cpm SET cpm.apprvlStats = :apprvlStats, cpm.apprvlReqId = :apprvlReqId, cpm.apprvlReqDt  = :apprvlReqDt WHERE cpm.cntrctNo = :cntrctNo AND cpm.payprceSno = :payprceSno")
    void updateByCntrctNoAndPayprceSno(
            @Param("apprvlStats") String apprvlStats
            , @Param("apprvlReqId") String apprvlReqId
            , @Param("apprvlReqDt") LocalDateTime apprvlReqDt
            , @Param("cntrctNo") String cntrctNo
            , @Param("payprceSno") Long payprceSno
    );

    @Modifying
    @Query("UPDATE CwPayMng cpm SET cpm.apprvlStats = :apprvlStats, cpm.apDocId = :apDocId, cpm.apprvlReqId = :apprvlReqId, cpm.apprvlReqDt  = :apprvlReqDt WHERE cpm.cntrctNo = :cntrctNo AND cpm.payprceSno = :payprceSno")
    void updateApprStatusByCntrctNoAndPayprceSno(
            @Param("apprvlStats") String apprvlStats
            , @Param("apDocId") String apDocId
            , @Param("apprvlReqId") String apprvlReqId
            , @Param("apprvlReqDt") LocalDateTime apprvlReqDt
            , @Param("cntrctNo") String cntrctNo
            , @Param("payprceSno") Long payprceSno
    );

    @Modifying
    @Query("UPDATE CwPayMng cpm SET cpm.apprvlStats = :apprvlStats, cpm.apprvlId = :apprvlId, cpm.apprvlDt  = :apprvlDt WHERE cpm.cntrctNo = :cntrctNo AND cpm.payprceSno = :payprceSno")
    void updateByCntrctNoAndPayprceSnoAnyType(
            @Param("apprvlStats") String apprvlStats
            , @Param("apprvlId") String apprvlId
            , @Param("apprvlDt") LocalDateTime apprvlDt
            , @Param("cntrctNo") String cntrctNo
            , @Param("payprceSno") Long payprceSno
    );

	Optional<CwPayMng> findByApDocId(String apDocId);

	@Query("SELECT c.payprceTmnum FROM CwPayMng c WHERE c.cntrctNo = :cntrctNo AND c.payprceSno = (SELECT payprceSno FROM CwFrontMoney cfm WHERE cfm.cntrctNo = :cntrctNo AND ppaymnySno = :ppaymnySno)")
	Optional<Long> findPayprceTmnumByCntrctNoAndPayprceSno(@Param("cntrctNo") String cntrctNo, @Param("ppaymnySno") Long ppaymnySno);
	
}