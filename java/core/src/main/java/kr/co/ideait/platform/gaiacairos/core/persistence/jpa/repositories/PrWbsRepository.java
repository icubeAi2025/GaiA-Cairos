package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.PrWbs;

@Repository
public interface PrWbsRepository
		extends JpaRepository<PrWbs, String>, JpaSpecificationExecutor<PrWbs>, JpaLogicalDeleteable<PrWbs> {


}
