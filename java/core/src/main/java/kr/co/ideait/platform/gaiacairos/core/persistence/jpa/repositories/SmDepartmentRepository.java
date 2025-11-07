package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmDepartment;

@Repository
public interface SmDepartmentRepository
        extends JpaRepository<SmDepartment, Integer>, JpaSpecificationExecutor<SmDepartment>,
        JpaLogicalDeleteable<SmDepartment> {

    List<SmDepartment> findByCntrctNoAndDltYn(String contractNo, String dltYn);

    SmDepartment findByDeptNoAndDltYn(int deptNo, String dltYn);

    boolean existsByDeptIdAndDltYn(String deptCd, String dltYn);

	SmDepartment findByCntrctNoAndDeptLvlAndDltYn(String contractNo, int deptLvl, String dltYn);

    List<SmDepartment> findAllByDeptIdIn(List<String> deptIdList);
}
