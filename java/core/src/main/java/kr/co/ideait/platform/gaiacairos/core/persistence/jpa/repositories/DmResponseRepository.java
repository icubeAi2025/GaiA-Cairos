package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DmResponse;

public interface DmResponseRepository extends JpaRepository<DmResponse, String>, JpaSpecificationExecutor<DmResponse>,
        JpaLogicalDeleteable<DmResponse> {

    Optional<DmResponse> findByResSeqAndDsgnNoAndDltYn(String resSeq, String dsgnNo, String dltYn);

    Optional<DmResponse> findByResSeqAndDsgnNo(String resSeq, String dsgnNo);

}
