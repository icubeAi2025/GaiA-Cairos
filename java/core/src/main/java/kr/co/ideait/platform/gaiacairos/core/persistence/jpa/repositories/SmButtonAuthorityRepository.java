package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmButtonAuthority;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;

public interface SmButtonAuthorityRepository 
		extends JpaRepository<SmButtonAuthority, Integer>, JpaSpecificationExecutor<SmButtonAuthority>,
        JpaLogicalDeleteable<SmButtonAuthority> {

	Optional<SmButtonAuthority> findByMenuCdAndRghtKindAndBtnIdAndDltYn(String menuCd, String rghtKind, String btnId, String dltYn);
	
	boolean existsByBtnIdAndDltYn(String btnId, String dltYn);

	void deleteByBtnIdAndRghtKindAndDltYn(String btnId, String rghtKind, String dltYn);

	SmButtonAuthority findByBtnNo(Integer btnNo);

	SmButtonAuthority findByBtnIdAndRghtKindAndDltYn(String btnId, String rghtKind, String dltYn);
}
