package kr.co.ideait.platform.gaiacairos.comp.project.service;

import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnAttachments;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnProjectInstall;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.CnAttachmentsRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.CnProjectInstallRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.ProjectMybatisParam.ProjectInstallInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.ProjectMybatisParam.ProjectInstallOutput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@Transactional
public class ProjectService extends AbstractGaiaCairosService {

    @Autowired
    CnProjectInstallRepository cnProjectInstallRepository;

    @Autowired
    CnAttachmentsRepository cnAttachmentsRepository;

    /**
    * api 요청 처리
    * 
    * @param pjtNo
    * @return CnProjectInstall
    */
   public Map<String, Object> apiData(Map<String, Object> content) {
		// 임시
		Map<String, Object> resuilt = Map.of("테스트", "ok", "hohoho", "hahahaha");
		
		if(content.get("workType").equals("0001")) {
			log.debug("여기서 웹 api 로직을 처리하면 됩니다. ");
		}
		
    	
		return resuilt;
   }

    /**
     * 현장 개설 조회
     * 
     * @param pjtNo
     * @return CnProjectInstall
     */
    public CnProjectInstall getProjectInstall(String pjtNo) {
        return cnProjectInstallRepository.findByPlcReqNoAndDltYn(pjtNo, "N");
    }

    /**
     * 현장 개설 목록 조회
     * 
     * @param ProjectInstallInput
     * @return List<CnProjectInstall>
     */
    public List<ProjectInstallOutput> getProjectInstallList(ProjectInstallInput input) {
        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.project.getProjectInstallList",input);
    }

    /**
     * 현장 개설 저장
     * 
     * @param project
     */
    @Transactional
    public CnProjectInstall saveProject(CnProjectInstall project) {
        return cnProjectInstallRepository.save(project);
    }

    /**
     * 현장 개설 삭제
     */
    @Transactional
    public void deleteProject(String plcReqNo,String usrId) {
        MybatisInput input = new MybatisInput().add("plcReqNo", plcReqNo).add("usrId", usrId);
        mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.project.updateDeleteProjectInstall",input);
    }

    /**
     * 첨부 파일 목록 조회
     * 
     * @param fileNo
     * @return
     */
    public List<CnAttachments> getFileList(int fileNo) {
        return cnAttachmentsRepository.findByFileNoAndDltYn(fileNo, "N");
    }

    /**
     * 첨부파일 리스트 저장
     * 
     * @param cnAttachmentsList
     */
    @Transactional
    public void saveCnAttachmentsList(CnAttachments cnAttachment) {
        cnAttachmentsRepository.save(cnAttachment);
    }

    /**
     * 첨부파일 삭제
     * 
     * @param attachment
     */
    @Transactional
    public void deleteAttachment(int fileNo, int sno,String usrId) {
        if(usrId == null) {
            cnAttachmentsRepository.updateDelete(cnAttachmentsRepository.findByFileNoAndSno(fileNo, sno));
        }else{
            MybatisInput input = new MybatisInput().add("fileNo", fileNo).add("sno",sno).add("usrId", usrId);
            mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.project.updateDeleteCnAttachments",input);
        }
    }

    /**
     * 현장 개설 요청 번호 생성
     * 
     * @return 생성된 plcReqNo
     */
    public Optional<Integer> maxSerialNumber(String yearMonth, String plcReqType) {
            return cnProjectInstallRepository.findMaxSerialNumberByYearAndMonth(yearMonth,plcReqType);
    }

    /**
     * FileNo생성
     * 
     * @return
     */
    public Integer generateFileNo() {
        Integer maxFileNo = cnAttachmentsRepository.findMaxFileNo();
        return (maxFileNo == null ? 1 : maxFileNo + 1);
    }

    /**
     * FileNo생성
     *
     * @return
     */
    public Integer generateSNo(Integer fileNo) {
        return cnAttachmentsRepository.findMaxSnoByFileNo(fileNo) + 1;
    }

    /**
     * 유저가 속한 프로젝트 제외 프로젝트 목록 가져오기
     */
    public List<Map<String, ?>> getGAIAProjectList(String userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("pjtType", platform.toUpperCase());
        params.put("userId", userId);
        return mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.project.getProjectList",
                params);
    }

    /**
     * 유저가 속한 계약 제외 계약 목록 가져오기
     */
    public List<Map<String, ?>> getCAIROSContractList(String userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("pjtType", platform.toUpperCase());
        params.put("userId", userId);
        return mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.project.getContractList",
                params);
    }
}
