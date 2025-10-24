package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmMenu;

@Repository
public interface SmMenuRepository
                extends JpaRepository<SmMenu, Integer>, JpaSpecificationExecutor<SmMenu>, JpaLogicalDeleteable<SmMenu> {

        boolean existsByMenuCdAndDltYn(String menuCd, String ditYn);

        List<SmMenu> findAllByMenuCdIn(List<String> menuCdList);

        List<SmMenu> findByDltYnOrderByMenuDsplyOrdr(String dltYn);
        
        SmMenu findByMenuCdAndDltYn(String menuCd, String dltYn);

        @Query(value = "SELECT MAX(c.menuDsplyOrdr) FROM SmMenu c WHERE c.upMenuCd = :upMenuCd AND c.dltYn = 'N'")
	Short maxMenuDsplyOrdrByUpMenuCd(@Param("upMenuCd") String upMenuCd);
}
