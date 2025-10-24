package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnProjectInstall;

@Repository
public interface CnProjectInstallRepository
        extends JpaRepository<CnProjectInstall, String>, JpaLogicalDeleteable<CnProjectInstall> {
    @Query("SELECT MAX(CAST(SUBSTRING(p.plcReqNo, 9, 3) AS int)) FROM CnProjectInstall p WHERE SUBSTRING(p.plcReqNo, 3, 6) = :yearMonth and p.pltReqType = :pltReqType")
    Optional<Integer> findMaxSerialNumberByYearAndMonth(@Param("yearMonth") String yearMonth, @Param("pltReqType") String pltReqType);

    @Query("SELECT p FROM CnProjectInstall p WHERE p.dltYn = 'N' AND p.openPstats != '04' order by p.plcReqNo ASC")
    List<CnProjectInstall> findAllNotDeleted();

    CnProjectInstall findByPlcReqNoAndDltYn(String pjtNo, String dltYn);

    @Query("SELECT p.plcReqNo FROM CnProjectInstall p WHERE p.dltYn = 'N' ORDER BY p.plcReqNo DESC")
    List<String> findLatestPlcReqNo();
}
