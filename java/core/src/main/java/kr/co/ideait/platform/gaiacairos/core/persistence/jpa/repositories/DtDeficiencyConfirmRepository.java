package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DtDeficiencyConfirm;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DtDeficiencyConfirmRepository extends JpaRepository<DtDeficiencyConfirm, String>, JpaSpecificationExecutor<DtDeficiencyConfirm>, JpaLogicalDeleteable<DtDeficiencyConfirm> {

	@Query("SELECT COALESCE(max(rgstOrdr), 0) AS rgstOrdr FROM DtDeficiencyConfirm ddc WHERE ddc.dfccyNo = :dfccyNo AND dltYn = 'N'")
	Short findByMaxRgstOrderByDfccyNo(@Param("dfccyNo") String dfccyNo);

	Optional<DtDeficiencyConfirm> findByDfccySeqAndDltYn(String dfccySeq, String dltYn);

	List<DtDeficiencyConfirm> findByDfccyNoAndRgstrIdAndDltYn(String dfccyNo, String usrId, String dltYn);

}
