package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.ApAttachments;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.ApDoc;

import java.util.List;

@Repository
public interface ApAttachmentsRepository
		extends JpaRepository<ApAttachments, Integer>, JpaSpecificationExecutor<ApDoc>, JpaLogicalDeleteable<ApDoc> {

    List<ApAttachments> findByApDocIdAndDltYn(String apDocId, String dltYn);
}
