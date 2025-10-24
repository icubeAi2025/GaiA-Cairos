package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.PrActivity;

@Repository
public interface PrActivityRepository extends JpaRepository<PrActivity, String>, JpaSpecificationExecutor<PrActivity>, JpaLogicalDeleteable<PrActivity> {

    Optional<PrActivity> findByCntrctChgIdAndRevisionIdAndActivityId(String cntrctChgId, String revisionId, String activityId);

    Optional<PrActivity> findByCntrctChgIdAndRevisionIdAndActivityIdAndDltYn(String cntrctChgId, String revisionId, String activityId, String dltYn);


    @Modifying
    @Transactional
    @Query("UPDATE PrActivity pa SET pa.actualStart = :actualStart, pa.actualFinish = :actualEndDate WHERE pa.cntrctChgId = :cntrctChgId AND pa.revisionId = :revisionId AND pa.activityId = :activityId AND pa.dltYn='N'")
    void updateActualDate(@Param("cntrctChgId") String cntrctChgId, @Param("revisionId") String revisionId, @Param("activityId") String activityId, @Param("actualStart") String actualStart, @Param("actualEndDate") String actualEndDate);


}
