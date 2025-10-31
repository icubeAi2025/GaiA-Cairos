package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DmDwg;

@Repository
public interface DmDwgRepository
                extends JpaRepository<DmDwg, Short>, JpaSpecificationExecutor<DmDwg>, JpaLogicalDeleteable<DmDwg> {

    DmDwg findByDwgNoAndDltYn(String dwgNo, String dltYn);

    @Query("SELECT COALESCE(MAX(A.dwgNo), 0) FROM DmDwg A")
    Integer findMaxDwgNo();

	DmDwg findByDwgNoAndDwgCdAndDltYn(String dwgNo, String dwgCd, String string);

}
