package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import java.util.List;

import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.ApLine;

@Repository
public interface ApLineRepository 
		extends JpaRepository<ApLine, Integer>, JpaSpecificationExecutor<ApLine>, JpaLogicalDeleteable<ApLine> {

//	ApLine findByApDocNoAndApUsrId(Integer apDocNo, String usrId);

	ApLine findByApDocNoAndApDocIdAndApUsrId(Integer apDocNo, String apDocId, String usrId);

	List<ApLine> findByApDocId(String apDocId);

	@Query("SELECT al.apUsrId, al.apUsrOpnin FROM ApLine al WHERE al.apDocId = :apDocId AND al.apDiv = 'A' AND al.apStats = :apStats ORDER BY al.apOrder DESC LIMIT 1")
	ApLine findByApDocIdAndApStats(@Param("apDocId") String apDocId, @Param("apStats") String apStats);

}