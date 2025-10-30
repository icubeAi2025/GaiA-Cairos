package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwQualityCheckList;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;

public interface CwQualityCheckListRepository extends JpaRepository<CwQualityCheckList, String>,
                JpaSpecificationExecutor<CwQualityCheckList>, JpaLogicalDeleteable<CwQualityCheckList> {
        List<CwQualityCheckList> findByCntrctNoAndQltyIspIdAndDltYn(String cntrctNo, String qltyIspId, String dltYn);

        @Transactional
        @Modifying
        @Query("UPDATE CwQualityCheckList cqcl SET cqcl.cqcYn = NULL, cqcl.chgId = :chgId, cqcl.chgDt = :chgDt WHERE cqcl.qltyIspId = :qltyIspId")
		void updateByQltyIspId(@Param("qltyIspId") String qltyIspId, @Param("chgId") String chgId, @Param("chgDt") LocalDateTime chgDt);

        @Transactional
        @Modifying
        @Query("DELETE CwQualityCheckList cqc WHERE cqc.qltyIspId = :qltyIspId")
        void deleteCheckListByQltyIspId(@Param("qltyIspId") String qltyIspId);
}
