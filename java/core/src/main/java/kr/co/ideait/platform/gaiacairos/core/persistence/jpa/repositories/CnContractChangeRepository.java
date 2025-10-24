package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnContractChange;
import org.springframework.data.repository.query.Param;

public interface CnContractChangeRepository
                extends JpaRepository<CnContractChange, String>, JpaSpecificationExecutor<CnContractChange>,
                JpaLogicalDeleteable<CnContractChange> {

        @Query("SELECT c.cntrctChgId FROM CnContractChange c WHERE c.cntrctNo = :cntrctNo ORDER BY c.cntrctChgId DESC")
        List<String> findCntrctChgIds(@Param("cntrctNo") String cntrctNo);

        @Query("SELECT COALESCE(MAX(c.cntrctChgNo), '0') FROM CnContractChange c WHERE c.cntrctNo = :cntrctNo")
        String findMaxChgNoByCntrctNo(@Param("cntrctNo") String cntrctNo);

        CnContractChange findByCntrctChgIdAndCntrctNo(String cntrctChgId, String cntrctNo);

        @Query("SELECT c.cntrctChgId FROM CnContractChange c WHERE c.cntrctNo = :cntrctNo ORDER BY lastChgYn DESC LIMIT 1")
        String findCntrctChgIdByCntrctNo(@Param("cntrctNo") String cntrctNo);

        @Query("SELECT c FROM CnContractChange c WHERE c.cntrctNo = :cntrctNo AND c.cntrctChgId != :cntrctChgId")
        List<CnContractChange> findAllByCntrctNoExcludingChgId(
                        @Param("cntrctNo") String cntrctNo,
                        @Param("cntrctChgId") String cntrctChgId);

        CnContractChange findByCntrctChgId(String cntrctChgId);

        List<CnContractChange> findByCntrctNoAndDltYn(@Param("cntrctNo") String cntrctNo, @Param("dltYn") String dltYn);

        CnContractChange findByCntrctNoAndCntrctChgNoAndCntrctPhaseIsNullAndDltYn(@Param("cntrctNo") String cntrctNo,@Param("cntrctChgNo") String cntrctChgNo, @Param("dltYn") String dltYn);

        CnContractChange findByCntrctNoAndCntrctChgNoAndCntrctPhaseAndDltYn(@Param("cntrctNo") String cntrctNo, @Param("cntrctChgNo") String cntrctChgNo, @Param("cntrctPhase") int cntrctPhase, @Param("dltYn") String dltYn);
}
