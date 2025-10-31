package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DcNavigation;

@Repository
public interface DcNavigationRepository
        extends JpaRepository<DcNavigation, Integer>, JpaSpecificationExecutor<DcNavigation>,
        JpaLogicalDeleteable<DcNavigation> {

    DcNavigation findByNaviIdAndDltYn(String naviId, String dltYn);

    DcNavigation findByNaviNoAndDltYn(Integer naviNo, String dltYn);

    // naviId를 기준으로 존재 여부를 확인하는 메서드
    boolean existsByNaviIdAndUpNaviIdAndDltYn(String naviId, String upNaviId, String dltYn);

    @Query(value = "SELECT MAX(c.dsplyOrdr) FROM DcNavigation c WHERE c.upNaviId = :upNaviId AND c.dltYn = 'N'")
	Short maxMenuDsplyOrdrByUpNaviId(@Param("upNaviId")String upNaviId);
}
