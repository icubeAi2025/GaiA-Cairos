package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DtDeficiency;

public interface DtDeficiencyRepository extends JpaRepository<DtDeficiency, String>, JpaSpecificationExecutor<DtDeficiency>, JpaLogicalDeleteable<DtDeficiency> {

	// 날짜로 시작하는 가장 큰 dfccyNo 찾기
    @Query("SELECT MAX(d.dfccyNo) FROM DtDeficiency d WHERE d.dfccyNo LIKE :datePrefix")
    String findMaxDfccyNoByDate(@Param("datePrefix")String datePrefix);

	DtDeficiency findByCntrctNoAndDfccyNoAndDltYn(String cntrctNo, String dfccyNo, String dltYn);

	Optional<DtDeficiency> findByDfccyNoAndDltYn(String dfccyNo, String dltYn);

	@Query("SELECT d FROM DtDeficiency d WHERE d.cntrctNo = :cntrctNo " + "AND d.dfccyNo IN :dfccyNoList " + "AND d.dltYn = 'N'")
	List<DtDeficiency> findByCntrctNoAndDfccyNoList(String cntrctNo, List<String> dfccyNoList);
	
}
