package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmComCode;

@Repository
public interface SmComCodeRepository
        extends JpaRepository<SmComCode, String>, JpaSpecificationExecutor<SmComCode>,
        JpaLogicalDeleteable<SmComCode> {

    Page<SmComCode> findByCmnGrpNoAndDltYn(int cmnGrpNo, String dltYnm, Pageable pageable);

    boolean existsByCmnGrpNoAndCmnCdAndDltYn(int cmnGrpNo, String cmnCd, String dltYn);

    boolean existsByCmnGrpCdAndCmnCdAndDltYn(String groupCodeCd, String cmnCd, String dltYn);

    @Query(value = "SELECT MAX(c.cmnCdDsplyOrder) FROM SmComCode c WHERE c.cmnGrpNo = :cmnGrpNo AND c.dltYn = 'N'")
    Short maxCdDsplyOrderByCmnGrpNo(int cmnGrpNo);

    List<SmComCode> findByCmnGrpCdAndDltYnOrderByCmnCdDsplyOrder(String cmnGrpCd, String dltYn);

    List findByCmnGrpCdAndDltYn(String cmnGrpCd, String dltYn);

    @Query("SELECT MAX(s.cmnCdNo) FROM SmComCode s")
    String findMaxCmnCdNo();

    SmComCode findByCmnCdNoAndCmnCd(String cmnCdNo, String cmnCd);

    SmComCode findByCmnGrpNoAndCmnCd(Integer cmnGrpNo, String cmnCd);

    SmComCode findByCmnGrpCdAndCmnCdNo(String cmnGrpCd, String cmnCdNo);

	SmComCode findByCmnGrpCdAndCmnCdAndDltYn(String docNaviDivGroupCode, String naviDiv, String string);
}
