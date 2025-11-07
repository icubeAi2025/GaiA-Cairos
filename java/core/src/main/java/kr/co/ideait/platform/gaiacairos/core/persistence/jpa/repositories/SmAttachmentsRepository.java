package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import io.lettuce.core.dynamic.annotation.Param;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmAttachments;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface SmAttachmentsRepository
        extends JpaRepository<SmAttachments, Integer>, JpaSpecificationExecutor<SmAttachments>,
        JpaLogicalDeleteable<SmAttachments> {

    List<SmAttachments> findByBoardCdAndDltYn(String boardCd, String dltYn);

    @Modifying
    @Transactional
    @Query("UPDATE SmAttachments a SET a.dltYn = 'Y' , a.dltId = :usrId, a.dltDt = CURRENT_TIMESTAMP WHERE a.fileNo = :fileNo")
    void softDeleteByFileNo(@Param("fileNo") Integer fileNo, @Param("usrId") String usrId);

}