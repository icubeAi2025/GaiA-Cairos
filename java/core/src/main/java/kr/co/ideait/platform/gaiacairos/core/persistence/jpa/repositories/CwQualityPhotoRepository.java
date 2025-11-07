package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import org.springframework.transaction.annotation.Transactional;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwQualityPhoto;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CwQualityPhotoRepository
        extends JpaRepository<CwQualityPhoto, String>, JpaSpecificationExecutor<CwQualityPhoto>,
        JpaLogicalDeleteable<CwQualityPhoto> {
    List<CwQualityPhoto> findByCntrctNoAndQltyIspIdAndDltYn(String cntrctNo, String qltyIspId, String dltyn);

    CwQualityPhoto findByCntrctNoAndQltyIspIdAndPhtSnoAndDltYn(String cntrctNo, String qltyIspId, int phtSno, String dltyn);

    @Query("SELECT MAX(c.phtSno) FROM CwQualityPhoto c WHERE c.cntrctNo = :cntrctNo AND c.qltyIspId = :qltyIspId")
    Integer findMaxPhtSnoByCntrctNoAndQltyIspId(@Param("cntrctNo") String cntrctNo,
            @Param("qltyIspId") String qltyIspId);

    @Transactional
    @Modifying
    @Query("DELETE CwQualityActivity cqa WHERE cqa.qltyIspId = :qltyIspId")
    void deletePhotoByQltyIspId(@Param("qltyIspId") String qltyIspId);
}
