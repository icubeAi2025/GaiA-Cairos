package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmBoardReception;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface SmBoardReceptionRepository
        extends JpaRepository<SmBoardReception, Integer>, JpaSpecificationExecutor<SmBoardReception>,
        JpaLogicalDeleteable<SmBoardReception> {

    List<SmBoardReception> findByBoardCdAndDltYn(@Param("boardCd") String boardCd, @Param("dltYn") String dltYn);

    @Modifying
    @Transactional
    @Query("UPDATE SmBoardReception r SET r.dltYn = 'Y', r.dltId = :usrId, r.dltDt = CURRENT_TIMESTAMP WHERE r.receSeq = :receSeq")
    void softDeleteByReceSeq(@Param("receSeq") Integer receSeq, @Param("usrId") String usrId);

}
