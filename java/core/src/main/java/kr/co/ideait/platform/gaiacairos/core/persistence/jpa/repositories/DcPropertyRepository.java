package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DcProperty;

@Repository
public interface DcPropertyRepository
        extends JpaRepository<DcProperty, Integer>, JpaSpecificationExecutor<DcProperty>,
        JpaLogicalDeleteable<DcProperty> {
    DcProperty findByAttrbtNoAndDltYn(Integer attrbtNo, String dltYn);

    List<DcProperty> findByNaviIdAndDltYnOrderByAttrbtDsplyOrderAsc(String naviId, String dltYn);

    List<DcProperty> findByNaviNoAndDltYn(Integer naviNo, String dltYn);

	Optional<DcProperty> findByAttrbtCdAndNaviIdAndDltYn(String attrbtCd, String naviId, String dltYn);

	List<DcProperty> findAllByNaviIdAndNaviNoAndDltYn(String targetId, Integer targetNo, String dltYn);

}
