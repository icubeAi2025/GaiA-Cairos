package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwRequestItem;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;

public interface CwRequestItemRepository
        extends JpaRepository<CwRequestItem, String>, JpaSpecificationExecutor<CwRequestItem>,
        JpaLogicalDeleteable<CwRequestItem> {
        CwRequestItem findByReqInsIdAndDltYn(@Param("reqInsId") String reqInsId, @Param("dltYn") String dltYn);
}
