package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwMainmtrlReqfrm;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;

public interface CwMainmtrlReqfrmRepository
                extends JpaRepository<CwMainmtrlReqfrm, String>, JpaSpecificationExecutor<CwMainmtrlReqfrm>,
                JpaLogicalDeleteable<CwMainmtrlReqfrm> {
        CwMainmtrlReqfrm findByCntrctNoAndReqfrmNoAndDltYn(@Param("cntrctNo") String cntrctNo,
                        @Param("reqfrmNo") String reqfrmNo,
                        @Param("dltYn") String dltYn);

        @Modifying
        @Query("UPDATE CwMainmtrlReqfrm c SET c.atchFileNo = :fileNo, c.chgId = :chgId, c.chgDt = :chgDt " +
                        "WHERE c.reqfrmNo = :reqfrmNo")
        void updateFileNo(@Param("reqfrmNo") String reqfrmNo,
                        @Param("fileNo") Integer fileNo,
                        @Param("chgId") String chgId,
                        @Param("chgDt") LocalDateTime chgDt);

        Optional<CwMainmtrlReqfrm> findByApDocId(String apDocId);

        @Modifying
        @Query("UPDATE CwMainmtrlReqfrm cmr SET cmr.apReqId = NULL, cmr.apReqDt = NULL, cmr.apDocId = NULL, cmr.apprvlId = NULL, cmr.apprvlDt = NULL, cmr.apprvlStats = NULL, cmr.apOpnin = NULL, cmr.cmId = NULL, cmr.cmDt = NULL, cmr.rsltCd = NULL,  cmr.rsltOpnin = NULL, cmr.chgId = :chgId, cmr.chgDt = :chgDt WHERE cmr.apDocId = :apDocId")
        void updateByApDocId(@Param("apDocId") String apDocId, @Param("chgId") String chgId, @Param("chgDt") LocalDateTime chgDt);
}
