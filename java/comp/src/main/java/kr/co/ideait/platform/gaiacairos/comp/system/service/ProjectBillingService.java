package kr.co.ideait.platform.gaiacairos.comp.system.service;

import java.util.List;
import java.util.Map;

import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import kr.co.ideait.platform.gaiacairos.core.persistence.DaoUtils;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmProjectBilling;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.SmProjectBillingRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.ProjectBillingMybatisParam.ProjectBillingListInput;

@Slf4j
@Service
public class ProjectBillingService extends AbstractGaiaCairosService {

    @Autowired
    SmProjectBillingRepository smPjtBillRepository;

    /**
     * cmis project list 가져오기
     */
    public List<Map<String, Object>> getCmisProjectList() {
        log.debug("==============================================");
		log.debug("getCmisProjectList");
    	log.debug("==============================================");
        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.project_billing.selectCmisProjectList");
    }

    /**
     * 프로젝트의 유료 기능 목록 조회
     */
    public List<Map<String, ?>> getProjectuBillingList(String cntrctNo)
    {
        log.debug("==============================================");
		log.debug("getProjectuBillingList : " + cntrctNo);
    	log.debug("==============================================");

        ProjectBillingListInput input = new ProjectBillingListInput();
        input.setCntrctNo(cntrctNo);

        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.project_billing.selectProjectuBillingList",
                input);
    }

    /**
     * 프로젝트의 유료 기능 목록 저장
     */
    public SmProjectBilling createProjectBilling(SmProjectBilling smBill) {
        smBill.setDltYn("N");
        return smPjtBillRepository.save(smBill);
    }
    /**
     * 프로젝트의 유료 기능 삭제
     */
    @Transactional
    public void deleteProjectBilling(List<Integer> pjbBilNoList) {
        smPjtBillRepository.findAllById(pjbBilNoList).forEach(billingNo -> {
            smPjtBillRepository.updateDelete(billingNo);
        });
    }
}
