package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.PrRevision;

@Repository
public interface SmCommentRepository extends JpaRepository<PrRevision, Integer>, JpaSpecificationExecutor<PrRevision>,
        JpaLogicalDeleteable<PrRevision> {

}