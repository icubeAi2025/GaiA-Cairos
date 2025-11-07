package kr.co.ideait.platform.gaiacairos.comp.construction.service;
 
import java.util.List;

import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.CwAttachmentsRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.CwDailyReportActivityRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.CwDailyReportPhotoRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.CwDailyReportRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.CwDailyReportResourceRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.PrActivityRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.construction.MainphotoMybatisParam.MainphotoFormTypeSelectInput;
import kr.co.ideait.platform.gaiacairos.comp.eapproval.service.ApprovalService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MainphotoService extends AbstractGaiaCairosService {

    @Autowired
    CwDailyReportRepository cwDailyReportRepository;
    
    @Autowired
    CwDailyReportActivityRepository cwDailyReportActivityRepository;

    @Autowired
    CwDailyReportResourceRepository cwDailyReportResourceRepository;

    @Autowired
    CwDailyReportPhotoRepository cwDailyReportPhotoRepository;

    @Autowired
    CwAttachmentsRepository cwAttachmentsRepository;
    
    @Autowired
    PrActivityRepository prActivityRepository;
    
    @Autowired
    ApprovalService approvalService;
    

    /**
     * 계약 리스트 가져오기
     * @param String (프로젝트 타입)
     * @return List
     * @throws 
     */
    public List selectContractList(String pjtNo){
    	MainphotoFormTypeSelectInput mainphotoFormTypeSelectInput = new MainphotoFormTypeSelectInput();
    	
    	mainphotoFormTypeSelectInput.setPjtNo(pjtNo);
    	
        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.mainphoto.selectContractListForMainphoto", mainphotoFormTypeSelectInput);
    }
    
    /**
     * 공정사진 리스트 가져오기
     * @param String (프로젝트 타입)
     * @return List
     * @throws 
     */
    public List selectMainphotoList(String cntrctNo, String wbsCd, String searchText){
    	MainphotoFormTypeSelectInput mainphotoFormTypeSelectInput = new MainphotoFormTypeSelectInput();
    	
    	mainphotoFormTypeSelectInput.setCntrctNo(cntrctNo);
    	mainphotoFormTypeSelectInput.setWbsCd(wbsCd);
    	mainphotoFormTypeSelectInput.setSearchText(searchText);
    	
        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.construction.mainphoto.selectMainphotoList", mainphotoFormTypeSelectInput);
    }
    
    
    
}
