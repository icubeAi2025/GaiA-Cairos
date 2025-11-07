package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DtDeficiencyReply;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface DtDeficiencyReplyRepository extends JpaRepository<DtDeficiencyReply, String>,
        JpaSpecificationExecutor<DtDeficiencyReply>, JpaLogicalDeleteable<DtDeficiencyReply> {

    Optional<DtDeficiencyReply> findByReplySeqAndDfccyNoAndDltYn(Integer replySeq, String dfccyNo, String dltYn);

    List<DtDeficiencyReply> findByDfccyNoAndCntrctNoAndDltYn(String dfccyNo, String cntrctNo, String dltYn);
}
