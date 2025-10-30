package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnSubcontractChange;

public interface CnSubcontractChangeRepository
                extends JpaRepository<CnSubcontractChange, String>, JpaSpecificationExecutor<CnSubcontractChange>,
                JpaLogicalDeleteable<CnSubcontractChange> {

        @Query("SELECT COALESCE(MAX(c.cntrctChgId), 0) FROM CnSubcontractChange c WHERE c.cntrctNo = :cntrctNo AND c.scontrctCorpId = :scontrctCorpId")
        Long findMaxCntrctChgNoByCntrctNoAndScontrctCorpId(@Param("cntrctNo") String cntrctNo,
                        @Param("scontrctCorpId") Long scontrctCorpId);

        Optional<CnSubcontractChange> findByCntrctNoAndScontrctCorpIdAndCntrctChgId(String cntrctNo,
                        Long scontrctCorpId, Long cntrctChgId);

        @Query("SELECT c.cntrctChgNo " +
                        "FROM CnSubcontractChange c " +
                        "WHERE c.cntrctNo = :cntrctNo " +
                        "AND c.scontrctCorpId = :scontrctCorpId AND c.dltYn = 'N'")
        List<String> findAllCntrctChgNoByScontrctCorpIdAnd(@Param("cntrctNo") String cntrctNo,
                        @Param("scontrctCorpId") Long scontrctCorpId);
                        

}
