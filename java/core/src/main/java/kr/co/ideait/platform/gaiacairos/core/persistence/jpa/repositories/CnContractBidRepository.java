package kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.JpaLogicalDeleteable;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnContractBid;

public interface CnContractBidRepository extends JpaRepository<CnContractBid, String>, JpaSpecificationExecutor<CnContractBid>,
                JpaLogicalDeleteable<CnContractBid> {
        List<CnContractBid> findByCntrctNoAndDltYn(String cntrctNo, String dltYn);
}
