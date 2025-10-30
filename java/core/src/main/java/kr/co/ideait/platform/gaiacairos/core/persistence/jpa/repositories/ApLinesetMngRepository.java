package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.ApLinesetMng;

@Repository
public interface ApLinesetMngRepository extends JpaRepository<ApLinesetMng, Integer>, JpaSpecificationExecutor<ApLinesetMng>, JpaLogicalDeleteable<ApLinesetMng>{

	Optional<ApLinesetMng> findByApLineNo(Integer apLineNo);

}
