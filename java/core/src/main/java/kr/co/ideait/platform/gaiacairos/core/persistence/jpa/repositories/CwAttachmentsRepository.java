package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwAttachments;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;

@Repository
public interface CwAttachmentsRepository extends JpaRepository<CwAttachments, Integer>, JpaSpecificationExecutor<CwAttachments>, JpaLogicalDeleteable<CwAttachments> {
        @Query("SELECT COALESCE(MAX(c.fileNo), 0) FROM CwAttachments c")
        Integer findMaxFileNo();


        CwAttachments findByFileNoAndSno(Integer fileNo, Integer Sno);
        CwAttachments findByFileNo(Integer fileNo);

        
        @Query("SELECT COALESCE(MAX(c.sno), 0) FROM CwAttachments c WHERE c.fileNo = :fileNo")
        Integer findMaxSnoByFileNo(@Param("fileNo") Integer fileNo);


        @Modifying
        @Transactional
        @Query(value="DELETE FROM cw_attachments ca WHERE ca.file_no IN (SELECT atch_file_no FROM cw_daily_report_photo cdrp WHERE cdrp.cntrct_no = :cntrctNo AND cdrp.daily_report_id = :dailyReportId)", nativeQuery = true)
        void deleteCwAttachments(@Param("cntrctNo") String cntrctNo, @Param("dailyReportId") Long dailyReportId);

        
        List<CwAttachments> findByFileNoAndDltYn(int fileNo, String dltYn);

        List<CwAttachments> findByFileNoAndFileDivAndDltYn(int fileNo, String FileDiv, String dltYn);

        @Modifying
        @Transactional
        @Query("DELETE CwAttachments ca WHERE ca.fileNo = :fileNo")
        void deleteAttachmentsByFileNo(@Param("fileNo") Integer fileNo);

        // String 타입용
        List<CwAttachments> findByFileNoInAndDltYn(Collection<String> fileNos, String dltYn);

        // Integer 타입용
        @Query("SELECT c FROM CwAttachments c WHERE c.fileNo IN :fileNos AND c.dltYn = :dltYn")
        List<CwAttachments> findByFileNoInAndDltYnInt(@Param("fileNos") Collection<Integer> fileNos, @Param("dltYn") String dltYn);
}

