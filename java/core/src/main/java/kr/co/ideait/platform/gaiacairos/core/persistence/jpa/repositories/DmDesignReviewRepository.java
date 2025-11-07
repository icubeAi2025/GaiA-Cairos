package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DmDesignReview;

public interface DmDesignReviewRepository extends JpaRepository<DmDesignReview, String>, JpaSpecificationExecutor<DmDesignReview>, JpaLogicalDeleteable<DmDesignReview> {

	// 날짜로 시작하는 가장 큰 dfccyNo 찾기
    @Query("SELECT MAX(d.dsgnNo) FROM DmDesignReview d WHERE d.dsgnNo LIKE :datePrefix")
    String findMaxDsgnNoByDate(@Param("datePrefix")String datePrefix);

	DmDesignReview findByCntrctNoAndDsgnNoAndDltYn(String cntrctNo, String dsgnNo, String string);

    @Query("SELECT MAX(d.dsgnSeq) FROM DmDesignReview d WHERE d.dltYn = 'N'")
    Integer findMaxDsgnSeq();
}
