package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DtDeficiencyActivity;

public interface DtDeficiencyActivityRepository
                extends JpaRepository<DtDeficiencyActivity, String>, JpaSpecificationExecutor<DtDeficiencyActivity>,
                JpaLogicalDeleteable<DtDeficiencyActivity> {
        List<DtDeficiencyActivity> findByDfccyNoAndDltYn(String dfccyNo, String dltYn);
}
