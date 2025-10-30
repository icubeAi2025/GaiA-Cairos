package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DmDesignSchedule;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DmDesignScheduleId;

public interface DmDesignScheduleRepository extends JpaRepository<DmDesignSchedule, DmDesignScheduleId>, JpaSpecificationExecutor<DmDesignSchedule>, JpaLogicalDeleteable<DmDesignSchedule> {

	Optional<DmDesignSchedule> findByDsgnPhaseNoAndDsgnPhaseCdAndDltYn(String dsgnPhaseNo, String dsgnPhaseCd, String string);

	@Modifying
    @Query("UPDATE DmDesignSchedule dds SET dds.dltYn = 'Y', dds.chgId = :userId, dds.chgDt = CURRENT_TIMESTAMP, dds.dltId = :userId, dds.dltDt = CURRENT_TIMESTAMP WHERE dds.dsgnPhaseNo = :dsgnPhaseNo AND dds.dltYn = :dltYn")
	void deleteDesignSchedule(String dsgnPhaseNo, String userId, String dltYn);

}
