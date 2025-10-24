package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwMainmtrlReqfrmPhoto;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;

public interface CwMainmtrlReqfrmPhotoRepository extends JpaRepository<CwMainmtrlReqfrmPhoto, String>, JpaSpecificationExecutor<CwMainmtrlReqfrmPhoto>,
                JpaLogicalDeleteable<CwMainmtrlReqfrmPhoto> {
    List<CwMainmtrlReqfrmPhoto> findByCntrctNoAndReqfrmNoAndDltYn(@Param("cntrctNo") String cntrctNo,
            @Param("reqfrmNo") String reqfrmNo, @Param("dltYn") String dltYn);

    CwMainmtrlReqfrmPhoto findByCntrctNoAndReqfrmNoAndPhtSnoAndDltYn(@Param("cntrctNo") String cntrctNo,
            @Param("reqfrmNo") String reqfrmNo, @Param("phtSno") int phtSno, @Param("dltYn") String dltYn);
}
