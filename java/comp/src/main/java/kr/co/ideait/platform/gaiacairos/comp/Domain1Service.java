package kr.co.ideait.platform.gaiacairos.comp;

import java.util.List;

import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmComCodeGroup;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.SmComCodeGroupRepository;

@Service
public class Domain1Service extends AbstractGaiaCairosService {

    @Autowired
    SmComCodeGroupRepository smComCodeGroupRepository;

    public SmComCodeGroup save(SmComCodeGroup smComCodeGroup) {
        return smComCodeGroupRepository.save(smComCodeGroup);
    }

    public List<SmComCodeGroup> findAll() {
        return smComCodeGroupRepository.findAll();
    }

    public List<SmComCodeGroup> findAllJpa() {
        return null; // daoService.jpa(SmComCodeGroup.class).findAll();
    }
}
