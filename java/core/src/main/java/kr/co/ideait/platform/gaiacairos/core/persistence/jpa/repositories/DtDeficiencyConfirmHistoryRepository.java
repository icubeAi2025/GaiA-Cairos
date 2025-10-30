package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DtDeficiencyConfirmHistory;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DtDeficiencyConfirmHistoryRepository extends JpaRepository<DtDeficiencyConfirmHistory, Integer>, JpaSpecificationExecutor<DtDeficiencyConfirmHistory>, JpaLogicalDeleteable<DtDeficiencyConfirmHistory> {

	@Query("SELECT COALESCE(max(rgstOrdr), 0) AS rgstOrdr FROM DtDeficiencyConfirmHistory ddch WHERE ddch.dfccyNo = :dfccyNo AND ddch.cnfrmDiv = :cnfrmDiv AND ddch.dltYn = 'N'")
	Short findByMaxRgstOrderByDfccyNo(@Param("dfccyNo") String dfccyNo, @Param("cnfrmDiv") String cnfrmDiv);

}
