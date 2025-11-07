package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DmEvaluation;

public interface DmEvaluationRepository extends JpaRepository<DmEvaluation, Integer>, JpaSpecificationExecutor<DmEvaluation>, JpaLogicalDeleteable<DmEvaluation> {

	@Query("SELECT COALESCE(max(rgstOrdr), 0) AS rgstOrdr FROM DmEvaluation de WHERE de.dsgnNo = :dsgnNo AND de.dltYn = 'N'")
	Short findByMaxRgstOrderByDsgnNo(@Param("dsgnNo") String dsgnNo);

	Optional<DmEvaluation> findByEvaSeqAndDltYn(String evaSeq, String dltYn);

	Optional<List<DmEvaluation>> findByDsgnNoAndDltYn(String dsgnNo, String dltYn);

	Optional<DmEvaluation> findByDsgnNoAndRgstrIdAndDltYn(String dsgnNo, String usrId, String dltYn);

}
