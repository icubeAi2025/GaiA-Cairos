package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.DcAttachments;

@Repository
public interface DcAttachmentsRepository
        extends JpaRepository<DcAttachments, Integer>, JpaSpecificationExecutor<DcAttachments>,
        JpaLogicalDeleteable<DcAttachments> {

	DcAttachments findByFileNoAndDocNoAndDltYn(int fileNo, Integer docNo, String dltYn);

	List<DcAttachments> findAllByDocIdAndDltYn(String itemDocId, String dltYn);

}
