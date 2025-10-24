package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmComCodeGroup;

@Repository
public interface SmComCodeGroupRepository
        extends JpaRepository<SmComCodeGroup, Integer>, JpaSpecificationExecutor<SmComCodeGroup>,
        JpaLogicalDeleteable<SmComCodeGroup> {

    SmComCodeGroup findByCmnGrpCd(String cmnGrpCd);

    List<SmComCodeGroup> findAllByCmnGrpCd(String cmnGrpCd);

    List<SmComCodeGroup> findByDltYnOrderByCmnCdDsplyOrdr(String dltYn);

    List<SmComCodeGroup> findByUpCmnGrpNo(int upCmnGrpNo);

    List<SmComCodeGroup> findByUpCmnGrpCd(String upCmnGrpCd);

    boolean existsByCmnCdAndUpCmnGrpNoAndDltYn(String cmnCd, int upCmnGrpNo, String dltYn);

    boolean existsByCmnCdAndUpCmnGrpCdAndDltYn(String cmnCd, String upCmnGrpCd, String dltYn);

    @Query(value = "SELECT MAX(c.cmnCdDsplyOrdr) FROM SmComCodeGroup c WHERE c.upCmnGrpNo = :upCmnGrpNo AND c.dltYn = 'N'")
    Short maxCdDsplyOrdrByUpCmnGrpNo(@Param("upCmnGrpNo")int upCmnGrpNo);

}
