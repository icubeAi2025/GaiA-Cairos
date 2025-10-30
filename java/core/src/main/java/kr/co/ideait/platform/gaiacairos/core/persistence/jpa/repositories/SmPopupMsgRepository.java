package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;


import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmPopupMsg;
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
public interface SmPopupMsgRepository extends JpaRepository<SmPopupMsg, Integer>, JpaSpecificationExecutor<SmPopupMsg>,
        JpaLogicalDeleteable<SmPopupMsg> {

        List<SmPopupMsg> findByPopMsgCdAndDltYn(@Param("popMsgCd") String popMsgCd, @Param("dltYn") String dltYn);

        @Modifying
        @Transactional
        @Query("UPDATE SmPopupMsg spm SET spm.dltYn = 'Y' , spm.dltId = :usrId, spm.dltDt = CURRENT_TIMESTAMP WHERE spm.popMsgCd = :popMsgCd")
        void softDeleteByBoardCd(@Param("popMsgCd") String boardCd, @Param("usrId") String usrId);

}

