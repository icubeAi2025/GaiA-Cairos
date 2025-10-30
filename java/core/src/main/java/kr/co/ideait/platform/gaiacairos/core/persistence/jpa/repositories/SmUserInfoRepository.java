package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import java.util.List;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmUserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;

@Repository
public interface SmUserInfoRepository
        extends JpaRepository<SmUserInfo, String>, JpaSpecificationExecutor<SmUserInfo>,
        JpaLogicalDeleteable<SmUserInfo> {

    List<SmUserInfo> findByDltYn(String dltYn);

    boolean existsByLoginId(String loginId);

	SmUserInfo findByUsrIdAndDltYn(String usrId, String dltYn);

}
