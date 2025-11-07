package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DcPropertyData;

@Repository
public interface DcPropertyDataRepository
        extends JpaRepository<DcPropertyData, Integer>, JpaSpecificationExecutor<DcPropertyData>,
        JpaLogicalDeleteable<DcPropertyData> {

	DcPropertyData findByAttrbtCdAndDocNo(String attrbtCd, Integer docNo);

}
