package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnContractCompany;
import org.springframework.data.repository.query.Param;

public interface CnContractCompanyRepository
                extends JpaRepository<CnContractCompany, String>, JpaSpecificationExecutor<CnContractCompany>,
                JpaLogicalDeleteable<CnContractCompany> {

        @Query("SELECT COALESCE(MAX(c.cntrctId), 0) FROM CnContractCompany c WHERE c.cntrctNo = :cntrctNo")
        Long findCntrctIdByCntrctNo(@Param("cntrctNo") String cntrctNo);

        CnContractCompany findByCntrctIdAndCntrctNo(Long cntrctId, String cntrctNo);

        @Modifying
        @Query("UPDATE CnContractCompany c SET c.rprsYn = 'N' WHERE c.cntrctNo = :cntrctNo AND c.cntrctId <> :cntrctId")
        void updateRprsYn(@Param("cntrctNo") String cntrctNo,
                        @Param("cntrctId") Long cntrctId);

        Optional<CnContractCompany> findByCntrctNoAndCntrctId(@Param("cntrctNo") String cntrctNo,
                        @Param("cntrctId") int cntrctId);
}
