package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnContract;
import org.springframework.data.repository.query.Param;

public interface CnContractRepository extends JpaRepository<CnContract, String>, JpaSpecificationExecutor<CnContract>,
                JpaLogicalDeleteable<CnContract> {

        List<CnContract> findByPjtNoAndDltYnOrderByMajorCnsttyCd(String pjtNo, String dltYn);

        CnContract findByCntrctNo(String cntrctNo);

        @Query("SELECT c.ofclNm FROM CnContract c WHERE c.cntrctNo = :cntrctNo")
        String findOfclNmByCntrctNo(@Param("cntrctNo")String cntrctNo);

        @Query("SELECT MAX(CAST(SUBSTRING(c.cntrctNo, LENGTH(c.pjtNo) + 2, 2) AS int)) " +
                        "FROM CnContract c " +
                        "WHERE c.pjtNo = :pjtNo AND SUBSTRING(c.cntrctNo, LENGTH(c.pjtNo) + 1, 1) = :majorCnsttyCdPrefix")
        Integer findMaxSerialNumber(@Param("pjtNo") String pjtNo,
                        @Param("majorCnsttyCdPrefix") String majorCnsttyCdPrefix);

        @Query("SELECT MAX(SUBSTRING(c.cntrctNo, LENGTH(:prefix) + 1)) " +
                        "FROM CnContract c WHERE c.cntrctNo LIKE CONCAT(:prefix, '%')")
        String findMaxCntrctNoWithPrefix(@Param("prefix") String prefix);

        @Query("SELECT c.cntrctNm FROM CnContract c WHERE c.cntrctNo = :cntrctNo")
        String findCntrctNmByCntrctNo(@Param("cntrctNo") String cntrctNo);
}
