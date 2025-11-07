package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import io.lettuce.core.dynamic.annotation.Param;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmBoard;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface SmBoardRepository extends JpaRepository<SmBoard, Integer>, JpaSpecificationExecutor<SmBoard>,
        JpaLogicalDeleteable<SmBoard> {
        @Query("SELECT COALESCE(MAX(A.boardNo), 0) FROM SmBoard A")
        Integer findMaxBoard();

        Optional<SmBoard> findByBoardNo(@Param("boardNo") Integer boardNo);

        @Query("SELECT b FROM SmBoard b WHERE b.boardCd = :boardCd and b.dltYn = 'N'")
        SmBoard findByBoardCd(@Param("boardCd") String boardCd);

        @Modifying
        @Transactional
        @Query("UPDATE SmBoard sb SET sb.dltYn = 'Y' , sb.dltId = :usrId, sb.dltDt = CURRENT_TIMESTAMP WHERE sb.boardCd = :boardCd")
        void softDeleteByBoardCd(@Param("boardCd") String boardCd, @Param("usrId") String usrId);

        // @Modifying
        // @Query("UPDATE SmBoard A SET A.boardView = A.boardView + 1 WHERE A.boardNo =
        // :boardNo")
        // void updateView(@Param("boardNo") Integer boardNo);

}
