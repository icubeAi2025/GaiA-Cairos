package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DtDeficiencyPhase;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DtDeficiencyPhaseRepository extends JpaRepository<DtDeficiencyPhase, String>, JpaSpecificationExecutor<DtDeficiencyPhase>, JpaLogicalDeleteable<DtDeficiencyPhase> {

	@Query("SELECT COALESCE(MAX(dsplyOrdr), 0) FROM DtDeficiencyPhase WHERE cntrctNo = :cntrctNo AND dltYn = :dltYn")
	Short findMaxDsplyOrdrByCntrctNo(@Param("cntrctNo") String cntrctNo, @Param("dltYn") String dltYn);

	Optional<DtDeficiencyPhase> findByDfccyPhaseNoAndDltYn(String dfccyPhaseNo, String string);

	List<DtDeficiencyPhase> findByCntrctNoAndDltYnOrderByDsplyOrdrAsc(String cntrctNo, String dltYn);

	
}
