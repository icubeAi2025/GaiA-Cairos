package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnContractOrg;

@Repository
public interface CnContractOrgRepository
                extends JpaRepository<CnContractOrg, String>, JpaSpecificationExecutor<CnContractOrg>,
                JpaLogicalDeleteable<CnContractOrg> {

        @Query("SELECT COALESCE(MAX(c.cntrctOrgId), 0) FROM CnContractOrg c WHERE c.cntrctNo = :cntrctNo")
        Integer findMaxCntrctOrgIdByCntrctNo(@Param("cntrctNo") String cntrctNo);

        @Query("SELECT p FROM CnContractOrg p WHERE p.dltYn = 'N' order by p.cntrctNo ASC")
        List<CnContractOrg> findAllNotDeleted();

        Optional<CnContractOrg> findByCntrctNoAndCntrctOrgId(String cntrctNo, Integer cntrctOrgId);

}
