package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DtAttachments;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DtAttachmentsRepository extends JpaRepository<DtAttachments, Short>, JpaSpecificationExecutor<DtAttachments>, JpaLogicalDeleteable<DtAttachments> {
        @Query("SELECT COALESCE(MAX(c.fileNo), 0) FROM DtAttachments c")
        Integer findMaxFileNo();

        DtAttachments findByFileNoAndSno(Integer fileNo, Short Sno);

        @Query("SELECT COALESCE(MAX(c.sno), 0) FROM DtAttachments c WHERE c.fileNo = :fileNo")
        Integer findMaxSnoByFileNo(@Param("fileNo") Integer fileNo);

        List<DtAttachments> findByFileNoAndDltYn(Integer fileNo, String dltYn);
}
