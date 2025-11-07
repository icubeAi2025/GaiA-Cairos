package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwSafetyInspectionPhoto;

public interface CwSafetyPhotoPhotoRepository
                extends JpaRepository<CwSafetyInspectionPhoto, String>,
                JpaSpecificationExecutor<CwSafetyInspectionPhoto>,
                JpaLogicalDeleteable<CwSafetyInspectionPhoto> {

        List<CwSafetyInspectionPhoto> findByCntrctNoAndInspectionNoAndDltYn(String cntrctNo, String inspectionNo,
                        String dltyn);
        
        CwSafetyInspectionPhoto findByCntrctNoAndInspectionNoAndPhtSnoAndDltYn(String cntrctNo, String inspectionNo, int phtSno, String dltYn);

        @Query("SELECT COALESCE(MAX(cwp.phtSno), 0) FROM CwSafetyInspectionPhoto cwp WHERE cwp.cntrctNo = :cntrctNo AND cwp.inspectionNo = :inspectionNo AND cwp.dltYn = 'N'")
        int getMaxPhtSno(@Param("cntrctNo") String cntrctNo, @Param("inspectionNo") String inspectionNo);
}
