package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.ApLineSet;

@Repository
public interface ApLineSetRepository 
		extends JpaRepository<ApLineSet, Integer>, JpaSpecificationExecutor<ApLineSet>, JpaLogicalDeleteable<ApLineSet>{

	List<ApLineSet> findByApLineNoAndDltYn(Integer apLineNo, String dltYn);

}
