package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DmAttachments;

@Repository
public interface DmAttachmentsRepository extends JpaRepository<DmAttachments, Short>,
                JpaSpecificationExecutor<DmAttachments>, JpaLogicalDeleteable<DmAttachments> {

//        DmAttachments findByFileNoAndSno(String fileNo, Short sno);

        DmAttachments findByFileNoAndFileKey(String fileNo, String fileKey);

        @Query("SELECT COALESCE(MAX(c.sno), 0) FROM DmAttachments c WHERE c.fileNo = :fileNo")
        Short findMaxSnoByFileNo(@Param("fileNo") String fileNo);

        // @Modifying
        // @Transactional
        // @Query(value="DELETE FROM dm_attachments ca WHERE ca.file_no IN (SELECT
        // atch_file_no FROM cw_daily_report_photo cdrp WHERE cdrp.cntrct_no = :cntrctNo
        // AND cdrp.daily_report_id = :dailyReportId)", nativeQuery = true)
        // void deleteCwAttachments(@Param("cntrctNo") String cntrctNo,
        // @Param("dailyReportId") Long dailyReportId);

        List<DmAttachments> findByFileNoAndDltYn(String fileNo, String dltYn);

        DmAttachments findByFileNoAndSnoAndDltYn(String fileNo, Short sno, String dltYn);
}
