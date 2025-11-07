package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.ApFavorites;

@Repository
public interface ApFavoritesRepository
extends JpaRepository<ApFavorites, Integer>, JpaSpecificationExecutor<ApFavorites>, JpaLogicalDeleteable<ApFavorites> {

	void deleteByFrmNoAndFvrtsDivAndUsrId(Integer frmNo, String fvrtsDiv, String loginId);

	ApFavorites findByFrmNoAndFvrtsDivAndUsrId(Integer frmNo, String fvrtsDiv, String loginId);

}
