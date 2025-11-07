package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.ApShare;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApShareRepository 
		extends JpaRepository<ApShare, Integer>, JpaSpecificationExecutor<ApShare>, JpaLogicalDeleteable<ApShare>{

	List<ApShare> findByApDocNoAndApDocIdAndDltYn(Integer apDocNo, String apDocId, String string);

	boolean existsByApDocIdAndApCnrsIdAndDltYn(String apDocId, String apCnrsId, String string);

    List<ApShare> findByApDocIdAndDltYn(String apDocId, String dltYn);
}
