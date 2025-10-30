package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import java.util.List;

import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.transaction.annotation.Transactional;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnAttachments;

@Repository
public interface CnAttachmentsRepository
                extends JpaRepository<CnAttachments, Integer>, JpaSpecificationExecutor<CnAttachments>,
                JpaLogicalDeleteable<CnAttachments> {
        @Query("SELECT COALESCE(MAX(c.fileNo), 0) FROM CnAttachments c")
        Integer findMaxFileNo();

        @Query("SELECT COALESCE(MAX(c.sno), 0) FROM CnAttachments c WHERE c.fileNo = :fileNo")
        Integer findMaxSnoByFileNo(@Param("fileNo") Integer fileNo);

        List<CnAttachments> findByFileNoAndDltYn(@Param("fileNo") double fileNo, @Param("dltYn") String dltYn);

        CnAttachments findOneByFileNoAndDltYn(@Param("fileNo") double fileNo, @Param("dltYn") String dltYn);

        CnAttachments findByFileNoAndSno(@Param("fileNo") Integer fileNo, @Param("sno") Integer sno);

        @Modifying
        @Transactional
        @Query("UPDATE CnAttachments c SET c.fileNm = :fileNm, c.fileDiskNm = :fileDiskNm, c.fileDiskPath = :fileDiskPath, c.fileSize = :fileSize WHERE c.fileNo = :fileNo AND c.sno = :sno")
        void updateByFileNoAndSno(@Param("fileNm") String fileNm, @Param("fileDiskNm") String fileDiskNm,
                        @Param("fileDiskPath") String fileDiskPath, @Param("fileSize") int fileSize,
                        @Param("fileNo") int fileNo,
                        @Param("sno") int sno);

        @Modifying
        @Transactional
        @Query("UPDATE CnAttachments c SET c.dltYn = 'Y' WHERE c.fileNo = :fileNo AND c.sno = :sno")
        void dltByFileNoAndSno(@Param("fileNo") Integer fileNo, @Param("sno") Integer sno);

}
