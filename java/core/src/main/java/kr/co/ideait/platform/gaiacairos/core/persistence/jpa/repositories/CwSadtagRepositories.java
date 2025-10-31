package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwSadtag;

public interface CwSadtagRepositories extends JpaRepository<CwSadtag, String>, JpaSpecificationExecutor<CwSadtag>,
                JpaLogicalDeleteable<CwSadtag> {
        CwSadtag findByCntrctNoAndSadtagNoAndDltYn(String cntrctNo, String sadtagNo, String dltYn);

        Optional<CwSadtag> findByApDocId(String apDocId);

        @Modifying
        @Query("UPDATE CwSadtag cs SET cs.apReqId = NULL, cs.apReqDt = NULL, cs.apDocId = NULL, cs.apprvlStats = NULL, cs.chgId =:chgId, cs.chgDt = CURRENT_TIMESTAMP"
        		+ " WHERE cs.apDocId = :apDocId")
		void updateByApDocId(@Param("apDocId") String apDocId, @Param("chgId") String chgId);
}
