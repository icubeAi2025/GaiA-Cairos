package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnPinstallMail;

@Repository
public interface CnPinstallMailRepository extends JpaRepository<CnPinstallMail, String>{
    
}
