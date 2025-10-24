package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwSafetyInspection;

public interface CwSafetyInspectionRepository
                extends JpaRepository<CwSafetyInspection, String>, JpaSpecificationExecutor<CwSafetyInspection>,
                JpaLogicalDeleteable<CwSafetyInspection> {
        CwSafetyInspection findByCntrctNoAndInspectionNoAndDltYn(String cntrctNo, String inspectionNo, String dltYn);

        Optional<CwSafetyInspection> findByApDocId(String apDocId);

        Optional<CwSafetyInspection> findByRepApDocId(String repApDocId);

        @Modifying
        @Query("UPDATE CwSafetyInspection cs SET cs.apReqId = NULL, cs.apReqDt = NULL, cs.apDocId = NULL, cs.apprvlStats = NULL, cs.chgId =:chgId, cs.chgDt = CURRENT_TIMESTAMP WHERE cs.apDocId = :apDocId")
        void updateByApDocId(@Param("apDocId") String apDocId, @Param("chgId") String chgId);

        @Modifying
        @Query("UPDATE CwSafetyInspection cs SET cs.apReqId = NULL, cs.apReqDt = NULL, cs.repApDocId = NULL, cs.apprvlStats = NULL, cs.chgId =:chgId, cs.chgDt = CURRENT_TIMESTAMP WHERE cs.repApDocId = :repApDocId")
        void updateByRepApDocId(@Param("repApDocId") String repApDocId, @Param("chgId") String chgId);
}
