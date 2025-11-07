package kr.co.ideait.platform.gaiacairos.comp.system.service;

import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.ProjectDto;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.pjinstall.PjInstallCriteria;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
public class ProjectInstallManageService extends AbstractGaiaCairosService {

    /**
     * 개설 요청 목록 조회
     *
     * @param cri {@link PjInstallCriteria} 검색하고자 하는 조건
     * @return {@link List}<{@link ProjectDto.ProjectInstall}> 
     */
    public List<ProjectDto.ProjectInstall> getPjInstallList(PjInstallCriteria cri) {
        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.pjt_install.selectProjectInstallList",cri);
    }

    /**
     * 주 공종 목록 조회
     *
     * @return {@link List}<{@link HashMap}>
     */
    public List<HashMap<String,String>> getDminsttTypeList() {
        List<HashMap<String,String>> result = new ArrayList<>();
        HashMap<String,String> obj = new HashMap<>();
        obj.put("name","공공");
        obj.put("value","P");
        result.add(obj);
        return result;
    }

    /**
     * 개설 요청 데이터 조회
     *
     * @param plcReqNo {@link String} 조회하고자 하는 개설 요청의 코드(pk)
     * @return {@link ProjectDto.ProjectInstall}
     */
    public ProjectDto.ProjectInstall getPjInstall(String plcReqNo) {
        return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.pjt_install.selectProjectInstall",plcReqNo);
    }

    /**
     * 개설 요청의 개설 상태 수정
     *
     * @param plcReqNo {@link String} 수정하고자 하는 개설 요청의 코드(pk)
     * @param openPstats {@link String} 새로운 개설 상태(01:개설요청, 02:개설요청확인, 03:개설중, 04:개설완료)
     * @return <em>boolean</em>
     */
    public boolean updatePjInstallOpenPstats(String plcReqNo,String openPstats) {
        HashMap<String,String> datas = new HashMap<>();
        datas.put("plcReqNo",plcReqNo);
        datas.put("openPstats",openPstats);
        return mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.pjt_install.updatePjInstallOpenPstats",datas) != 0;
    }

    /**
     * 개설 요청의 프로젝트번호 수정
     *
     * @param plcReqNo {@link String} 수정하고자 하는 개설 요청의 코드(pk)
     * @param pjtNo {@link String} 프로젝트번호.
     * @return <em>boolean</em>
     */
    public boolean updateProjectNoByPlcReqNo(String plcReqNo, String pjtNo) {
        HashMap<String,String> datas = new HashMap<>();
        datas.put("plcReqNo",plcReqNo);
        datas.put("pjtNo",pjtNo);
        return mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.pjt_install.updateProjectNoByPlcReqNo",datas) != 0;
    }
}
