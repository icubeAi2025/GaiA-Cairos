package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DmDesignPhase;

public interface DmDesignPhaseRepository extends JpaRepository<DmDesignPhase, String>, JpaSpecificationExecutor<DmDesignPhase>, JpaLogicalDeleteable<DmDesignPhase>{

	@Query("SELECT COALESCE(MAX(dsplyOrdr), 0) FROM DmDesignPhase WHERE cntrctNo = :cntrctNo AND dltYn = :dltYn")
	Short findMaxDsplyOrdrByCntrctNo(@Param("cntrctNo")String cntrctNo, @Param("dltYn") String dltYn);

	Optional<DmDesignPhase> findByDsgnPhaseNoAndDltYn(String dsgnPhaseNo, String dltYn);

	List<DmDesignPhase> findByCntrctNoAndDltYnOrderByDsplyOrdrAsc(String cntrctNo, String dltYn);

	@Modifying
	@Query("UPDATE DmDesignPhase ddp SET ddp.dltYn='Y', ddp.dltId = :dltId, ddp.dltDt = CURRENT_TIMESTAMP, ddp.chgDt = CURRENT_TIMESTAMP, ddp.chgId = :dltId WHERE ddp.dsgnPhaseNo = :dsgnPhaseNo")
	void deleteDesignPhase(String dsgnPhaseNo, String dltId);
}
