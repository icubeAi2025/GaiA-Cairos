package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DcAuthority;

@Repository
public interface DcAuthorityRepository
        extends JpaRepository<DcAuthority, Integer>, JpaSpecificationExecutor<DcAuthority>,
        JpaLogicalDeleteable<DcAuthority> {

    DcAuthority findByRghtNoAndDltYn(Integer rghtNo, String dltYn);

    List<DcAuthority> findByIdAndDltYn(String id, String dltYn);

	List<DcAuthority> findAllByIdAndNoAndDltYn(String id, Integer no, String dltYn);
}
