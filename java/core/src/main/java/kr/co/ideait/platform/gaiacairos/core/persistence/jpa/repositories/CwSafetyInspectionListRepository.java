package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwSafetyInspectionList;

public interface CwSafetyInspectionListRepository
                extends JpaRepository<CwSafetyInspectionList, String>, JpaSpecificationExecutor<CwSafetyInspectionList>,
                JpaLogicalDeleteable<CwSafetyInspectionList> {

        List<CwSafetyInspectionList> findByCntrctNoAndIspLstIdAndDltYn(@Param("cntrctNo") String cntrctNo, @Param("ispLstId") String ispLstId, @Param("dltYn") String dltYn);

        @Query("SELECT COALESCE(MAX(cwl.ispSno), 0) FROM CwSafetyInspectionList cwl WHERE cwl.cntrctNo = :cntrctNo")
        int findMaxSno(@Param("cntrctNo") String cntrctNo);

        @Query("SELECT COALESCE(MAX(cwl.ispLstNo), 0) FROM CwSafetyInspectionList cwl WHERE cwl.cntrctNo = :cntrctNo AND cwl.inspectionNo = :inspectionNo")
        int findLstNo(@Param("cntrctNo") String cntrctNo, @Param("inspectionNo") String inspectionNo);

        List<CwSafetyInspectionList> findByCntrctNoAndInspectionNoAndDltYn(@Param("cntrctNo") String cntrctNo, @Param("inspectionNo") String inspectionNo, @Param("dltYn") String dltYn);
}
