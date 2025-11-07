package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwCntqltyCheckList;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;

public interface CwCntqltyCheckListRepository
                extends JpaRepository<CwCntqltyCheckList, String>, JpaSpecificationExecutor<CwCntqltyCheckList>,
                JpaLogicalDeleteable<CwCntqltyCheckList> {

        List<CwCntqltyCheckList> findByDltYn(@Param("dltYn") String dltYn);
        
        CwCntqltyCheckList findByChklstId(@Param("dltYn") String chklstId);

        List<CwCntqltyCheckList> findByCntrctNoAndCnsttyYnAndDltYn(@Param("cntrctNo") String cntrctNo, @Param("cnsttyCd") String cnsttyCd, @Param("dltYn") String dltYn);

        CwCntqltyCheckList findByCntrctNoAndChklstIdAndDltYn(@Param("cntrctNo") String cntrctNo, @Param("chklstId") String chklstId, @Param("dltYn") String dltYn);

        CwCntqltyCheckList findByCnsttyCdAndCnsttyYnAndDltYn(@Param("CnsttyCd") String cnsttyCd, @Param("cnsttyyYn") String cnsttyyYn, @Param("dltYn") String dltYn);

        List<CwCntqltyCheckList> findByCntrctNoAndUpCnsttyCd(@Param("cntrctNo") String cntrctNo, @Param("upCnsttyCd") String upCnsttyCd);

        List<CwCntqltyCheckList> findByCntrctNoAndCnsttyCdAndCnsttyYn(@Param("cntrctNo") String cntrctNo, @Param("cnsttyCd") String cnsttyCd, @Param("cnsttyYn") String cnsttyYn);

       @Query("SELECT COALESCE(MAX(c.chklstSno), 0) " +
              "FROM CwCntqltyCheckList c " +
              "WHERE c.cntrctNo IN :cntrctNos AND c.cnsttyCd = :cnsttyCd")
        int findMaxChklstSnoByCntrctNosAndCnsttyCd(@Param("cntrctNos") List<String> cntrctNos,
                                           @Param("cnsttyCd") String cnsttyCd);

}
