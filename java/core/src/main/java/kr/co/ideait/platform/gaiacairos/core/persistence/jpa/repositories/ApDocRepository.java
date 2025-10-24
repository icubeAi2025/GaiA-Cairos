package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.ApDoc;

@Repository
public interface ApDocRepository 
		extends JpaRepository<ApDoc, Integer>, JpaSpecificationExecutor<ApDoc>, JpaLogicalDeleteable<ApDoc> {

	ApDoc findByApDocNoAndApDocIdAndDltYn(Integer apDocNo, String apDocId, String string);

	ApDoc findByApDocId(String apDocId);

}
