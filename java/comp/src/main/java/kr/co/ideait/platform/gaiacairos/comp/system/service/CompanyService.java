package kr.co.ideait.platform.gaiacairos.comp.system.service;

import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmCompany;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.SmCompanyRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.company.CompanyMybatisParam.CompanyListInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.company.CompanyMybatisParam.CompanyOutput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.company.CompanyMybatisParam.IdeaCompanyOutput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.company.CompanyMybatisParam.PcesCompanyOutput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.company.CompanyMybatisParam.UserCompanyListInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.company.CompanyMybatisParam.UserCompanyOutput;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class CompanyService extends AbstractGaiaCairosService {
    @Autowired
    @Qualifier("pccsSqlSessionTemplate")
    SqlSessionTemplate oraMybatisSession;

    @Autowired
    SmCompanyRepository smCompanyRepository;

    public Page<SmCompany> getCompanyListAll(Pageable pageable) {
        return smCompanyRepository.findAllByDltYn("N", pageable);
    }

    public void createCompanyList(List<SmCompany> companyList) {
        for (SmCompany company : companyList) {
            company.setDltYn("N"); // DB에서 기본값 세팅되면 코드 삭제
            smCompanyRepository.save(company);
        }
    }

    public void createCompany(SmCompany company) {
        createCompany(company, null);
    }

    public void createCompany(SmCompany company, String userId) {
        company.setDltYn("N");// DB에서 기본값 세팅되면 코드 삭제

        if (userId == null) {
            company.setRgstrId(UserAuth.get(true).getUsrId());
            company.setChgId(UserAuth.get(true).getUsrId());
        } else {
            company.setRgstrId(userId);
            company.setChgId(userId);
        }

        log.debug("companyRegDtId={}", company.getRgstrId());
        log.debug("Smcompany={}", company);
        smCompanyRepository.save(company);
    }

    public Page<IdeaCompanyOutput> getCompanyListByIdea(CompanyListInput companyListInput) {
        List<IdeaCompanyOutput> outputs = oraMybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.company.selectCompanyListByIdea", companyListInput);
        Long totalCount = oraMybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.company.selectCompanyListCountByIdea", companyListInput);
        return new PageImpl<>(outputs, companyListInput.getPageable(), totalCount);
    }

    public Page<PcesCompanyOutput> getCompanyListByPces(CompanyListInput companyListInput) {
        List<PcesCompanyOutput> outputs = mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.company.selectCompanyListByPces", companyListInput);
        Long totalCount = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.company.selectCompanyListCountByPces", companyListInput);
        return new PageImpl<>(outputs, companyListInput.getPageable(), totalCount);
    }

    public Page<CompanyOutput> getCompanyList(CompanyListInput companyListInput) {
        List<CompanyOutput> companyOutputs = mybatisSession
                .selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.company.getCompanyList",
                        companyListInput);
        Long totalCount = mybatisSession.selectOne(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.company.getCompanyListCount",
                companyListInput);
        return new PageImpl<>(companyOutputs, companyListInput.getPageable(), totalCount);
    }

    public List<UserCompanyOutput> getUserCompanyList(UserCompanyListInput userCompanyListInput) {
        List<UserCompanyOutput> companyOutputs = mybatisSession
                .selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.company.getUserCompanyList",
                        userCompanyListInput);

        return companyOutputs;
    }

    public SmCompany getCompany(String corpNo) {
        return smCompanyRepository.findById(corpNo).orElse(null);
    }

    public boolean checkCorpNo(String corpNo) {
        return smCompanyRepository.existsByCorpNo(corpNo);
    }

    public void deleteCompanyList(List<String> corpNoList) {
        deleteCompanyList(corpNoList, null);
    }

    public void deleteCompanyList(List<String> corpNoList, String userId) {
        smCompanyRepository.findAllById(corpNoList).forEach(company -> {
            if (userId == null) {
                company.setRgstrId(UserAuth.get(true).getUsrId());
                company.setChgId(UserAuth.get(true).getUsrId());
            } else {
                company.setRgstrId(userId);
                company.setChgId(userId);
            }

            smCompanyRepository.updateDelete(company);
        });
    }

    public void deleteCompanyListApi(List<String> corpNoList, String userId) {
        for (int i = 0; i < corpNoList.size(); i++) {
            SmCompany company = smCompanyRepository.findByCorpNo(corpNoList.get(i));
            if (userId == null) {
                company.setDltId(UserAuth.get(true).getUsrId());
            } else {
                company.setDltId(userId);
            }
            company.setDltDt(LocalDateTime.now());
            company.setDltYn("Y");
            smCompanyRepository.save(company);
        }
    }

    public SmCompany updateCompany(SmCompany smCompany) {
        return updateCompany(smCompany, null);
    }

    public SmCompany updateCompany(SmCompany smCompany, String userId) {
        if (userId == null) {
            smCompany.setRgstrId(UserAuth.get(true).getUsrId());
            smCompany.setChgId(UserAuth.get(true).getUsrId());
        } else {
            smCompany.setRgstrId(userId);
            smCompany.setChgId(userId);
        }

        return smCompanyRepository.save(smCompany);
    }

    public SmCompany createCompany(String corpNo, String platform) {
        SmCompany company = null;
        //OCI(이데아)
        if("O".equals(platform)){
            company = oraMybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.company.selectCompanyByIdea",corpNo);
        }
        //TODO : API 통신으로 해결해야 함
        //NCP(PGAIA)
        else if("N".equals(platform)){
            company = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.company.selectCompanyByPces",corpNo);
        }
        if(company == null){
            return null;
        }
        SmCompany selectedSmCompany = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.company.selectCompanyByBsnsmnNo",company.getBsnsmnNo());
        boolean result = false;
        //첫 추가
        company.setChgId("SYSTEM");
        if(selectedSmCompany == null || "Y".equals(selectedSmCompany.getDltYn())){
            company.setCompGrpCd("EC");
            company.setRgstrId("SYSTEM");
            result = mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.company.insertCompany",company) == 1;
        }
        //아닌경우
        else{
            result = mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.company.updateCompany",company) == 1;
        }
        if(result){
            return company;
        }
        return null;
    }
}
