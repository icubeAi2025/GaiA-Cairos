package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.PrRevision;

@Repository
public interface PrRevisionRepository 
		extends JpaRepository<PrRevision, Integer>, JpaSpecificationExecutor<PrRevision>, JpaLogicalDeleteable<PrRevision>{

	PrRevision findByCntrctChgIdAndRevisionId(String cntrctChgId, String revisionId);

	@Modifying
	@Transactional
	@Query("UPDATE PrRevision r SET r.lastRevisionYn = 'N' WHERE r.cntrctChgId = :cntrctChgId")
	void updateByCntrctChgId(@Param("cntrctChgId") String cntrctChgId);
}
