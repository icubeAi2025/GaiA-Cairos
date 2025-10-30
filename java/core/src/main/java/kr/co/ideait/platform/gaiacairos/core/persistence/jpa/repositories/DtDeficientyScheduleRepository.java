package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DtDeficientySchedule;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DtDeficientyScheduleId;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface DtDeficientyScheduleRepository extends JpaRepository<DtDeficientySchedule, DtDeficientyScheduleId>, JpaSpecificationExecutor<DtDeficientySchedule>, JpaLogicalDeleteable<DtDeficientySchedule> {

	Optional<DtDeficientySchedule> findByDfccyPhaseNoAndDfccyPhaseCdAndDltYn(String dfccyPhaseNo, String dfccyPhaseCd, String dltYn);

	@Modifying
    @Transactional
    @Query("UPDATE DtDeficientySchedule dds SET dds.dltYn = 'Y', dds.chgId = :userId, dds.chgDt = CURRENT_TIMESTAMP, dds.dltId = :userId, dds.dltDt = CURRENT_TIMESTAMP WHERE dds.dfccyPhaseNo = :dfccyPhaseNo AND dds.dltYn = :dltYn")
	void deleteDeficientySchedule(String dfccyPhaseNo, String userId, String dltYn);

}
