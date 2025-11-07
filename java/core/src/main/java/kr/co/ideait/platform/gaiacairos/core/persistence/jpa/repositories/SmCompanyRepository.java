package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmCompany;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;

@Repository
public interface SmCompanyRepository extends JpaRepository<SmCompany, String>, JpaSpecificationExecutor<SmCompany>, JpaLogicalDeleteable<SmCompany> {

    Page<SmCompany> findAllByDltYn(String dltYn, Pageable pageable);

    SmCompany findByCorpNo(String corpNo);

    boolean existsByCorpNo(String corpNo);
}
