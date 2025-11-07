package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwQualityInspection;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;

public interface CwQualityInspectionRepository
                extends JpaRepository<CwQualityInspection, String>, JpaSpecificationExecutor<CwQualityInspection>,
                JpaLogicalDeleteable<CwQualityInspection> {
        CwQualityInspection findByCntrctNoAndQltyIspIdAndDltYn(String cntrctNo, String qltyIspId, String dltYn);

		CwQualityInspection findByQltyIspIdAndDltYn(String qltyIspId, String dltYn);

		Optional<CwQualityInspection> findByApDocId(String apDocId);

		Optional<CwQualityInspection> findByIspApDocId(String apDocId);

		@Transactional
		@Modifying
		@Query("UPDATE CwQualityInspection cqi SET cqi.cqcId = NULL, cqi.rsltDocNo = NULL, cqi.rsltDt = NULL, cqi.rsltCd = NULL, cqi.ordeOpnin = NULL, cqi.ispApDocId = NULL, cqi.chgId = :chgId, cqi.chgDt = :chgDt WHERE cqi.ispApDocId = :ispApDocId")
		void updateByIspApDocId(@Param("ispApDocId") String ispApDocId, @Param("chgId") String chgId, @Param("chgDt") LocalDateTime chgDt);

		@Transactional
		@Modifying
		@Query("UPDATE CwQualityInspection cqi SET cqi.apReqId = NULL, cqi.apReqDt = NULL, cqi.apDocId = NULL, cqi.apprvlId = NULL, cqi.apprvlDt = NULL, cqi.apprvlStats = NULL, cqi.apOpnin = NULL, cqi.chgId = :chgId, cqi.chgDt = :chgDt WHERE cqi.apDocId = :apDocId")
		void updateByApDocId(@Param("apDocId") String apDocId, @Param("chgId") String chgId, @Param("chgDt") LocalDateTime chgDt);

		@Modifying
		@Query("UPDATE CwQualityInspection c SET c.atchFileNo = :fileNo, c.chgId = :chgId, c.chgDt = :chgDt " +
			"WHERE c.qltyIspId = :qltyIspId")
		void updateFileNo(@Param("qltyIspId") String qltyIspId,
						@Param("fileNo") Integer fileNo,
						@Param("chgId") String chgId,
						@Param("chgDt") LocalDateTime chgDt);

}
