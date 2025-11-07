package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import java.util.Optional;

import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnSubcontract;

public interface CnSubcontractRepository
                extends JpaRepository<CnSubcontract, String>, JpaSpecificationExecutor<CnSubcontract>,
                JpaLogicalDeleteable<CnSubcontract> {

        @Query("SELECT COALESCE(MAX(c.scontrctCorpId), 0) FROM CnSubcontract c WHERE c.cntrctNo = :cntrctNo")
        Long findMaxScontrctCorpIdByCntrctNo(@Param("cntrctNo") String cntrctNo);

        Optional<CnSubcontract> findByCntrctNoAndScontrctCorpId(String cntrctNo, Long scontrctCorpId);

}
