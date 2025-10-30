package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import org.springframework.transaction.annotation.Transactional;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwQualityActivity;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CwQualityActivityRepository
                extends JpaRepository<CwQualityActivity, String>, JpaSpecificationExecutor<CwQualityActivity>,
                JpaLogicalDeleteable<CwQualityActivity> {
        List<CwQualityActivity> findByQltyIspIdAndDltYn(String qltyIspId, String dltYn);

    @Transactional
    @Modifying
    @Query("DELETE CwQualityActivity cqa WHERE cqa.qltyIspId = :qltyIspId")
    void deleteyActivityByQltyIspId(@Param("qltyIspId") String qltyIspId);
}
