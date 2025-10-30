package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.transaction.annotation.Transactional;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmOrganization;

@Repository
public interface SmOrganizationRepository
                extends JpaRepository<SmOrganization, Integer>, JpaSpecificationExecutor<SmOrganization>,
                JpaLogicalDeleteable<SmOrganization> {

	List<SmOrganization> findByDeptNo(Integer deptNo);

    @Modifying
    @Transactional
    @Query("UPDATE SmOrganization s SET s.flag = :flag WHERE s.usrId = :usrId")
    void updateFlagByUsrId(@Param("flag") String flag, @Param("usrId") String usrId);
}
