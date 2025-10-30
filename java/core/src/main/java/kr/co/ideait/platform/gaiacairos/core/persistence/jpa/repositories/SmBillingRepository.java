package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmBilling;

@Repository
public interface SmBillingRepository
        extends JpaRepository<SmBilling, Integer>, JpaSpecificationExecutor<SmBilling>,
        JpaLogicalDeleteable<SmBilling> {

    List<SmBilling> findByMenuNoAndDltYn(int menuNo, String dltYn);
    SmBilling findByMenuNoAndDltYnAndMenuCdAndBilCode(int menuNo, String dltYn, String menuCd, String bilCode);
}
