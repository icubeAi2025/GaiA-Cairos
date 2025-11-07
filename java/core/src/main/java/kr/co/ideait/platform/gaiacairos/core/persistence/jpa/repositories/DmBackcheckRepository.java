package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DmBackcheck;

public interface DmBackcheckRepository extends JpaRepository<DmBackcheck, Integer>,
        JpaSpecificationExecutor<DmBackcheck>, JpaLogicalDeleteable<DmBackcheck> {

	Optional<DmBackcheck> findByDsgnNoAndRgstrIdAndDltYn(String dsgnNo, String usrId, String dltYn);

	Optional<List<DmBackcheck>> findAllByDsgnNoAndRgstrIdAndDltYn(String dsgnNo, String usrId, String dltYn);

	@Query("SELECT COALESCE(max(rgstOrdr), 0) AS rgstOrdr FROM DmBackcheck db WHERE db.dsgnNo = :dsgnNo AND db.dltYn = 'N'")
	Short findByMaxRgstOrderByDsgnNo(@Param("dsgnNo") String dsgnNo);

	Optional<DmBackcheck> findByBackSeqAndDltYn(String backSeq, String dltYn);

}
