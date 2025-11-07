package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwStandardInspectionList;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;

public interface CwStandardInspectionListRepository
                extends JpaRepository<CwStandardInspectionList, String>,
                JpaSpecificationExecutor<CwStandardInspectionList>,
                JpaLogicalDeleteable<CwStandardInspectionList> {

        boolean existsByCnsttyCdAndCnsttyYnAndDltYn(String cnsttyCd, String cnsttyYn, String dltYn);

        CwStandardInspectionList findByCntrctNoAndCnsttyCdAndDltYn(String cntrctNo, String CnsttyCd, String dltYn);

        CwStandardInspectionList findByAndIspLstIdAndDltYn(String isplstId, String dltYn);

        @Query("SELECT DISTINCT cw.upCnsttyCd FROM CwStandardInspectionList cw WHERE cw.cntrctNo IN ('CMIS', :cntrctNo) AND cw.cnsttyCd = :cnsttyCd AND cw.dltYn = 'N'")
        String findUpCnsttyCd(@Param("cntrctNo") String cntrctNo, @Param("cnsttyCd") String cnsttyCd);

        @Query("SELECT COALESCE(MAX(c.ispLstSno), 0) FROM CwStandardInspectionList c WHERE c.cntrctNo IN ('CMIS', :cntrctNo) AND c.cnsttyCd = :cnsttyCd")
        int findMaxIspLstSno(@Param("cntrctNo") String cntrctNo, @Param("cnsttyCd") String cnsttyCd);


        @Query("SELECT DISTINCT cw.cnsttyCd FROM CwStandardInspectionList cw WHERE cw.cntrctNo = :cntrctNo AND cw.cnsttyNm = :cnsttyNm")
        String findCnsttyCd(@Param("cntrctNo") String cntrctNo, @Param("cnsttyNm") String cnsttyNm);

        @Query("SELECT s.ispLstId FROM CwStandardInspectionList s WHERE s.ispLstId IN :ids")
        List<String> findExistIspLstIds(@Param("ids") Set<String> ids);

        List<CwStandardInspectionList> findByDltYn(@Param("dltYn") String dltYn);

        boolean existsByIspLstIdAndDltYn(@Param("ispLstId") String ispLstId, @Param("dltYn") String dltYn);
}
