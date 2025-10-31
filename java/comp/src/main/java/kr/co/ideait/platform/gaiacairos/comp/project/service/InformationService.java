package kr.co.ideait.platform.gaiacairos.comp.project.service;

import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnAttachments;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnProject;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.CnAttachmentsRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.CnProjectRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.overview.information.InformationMybatisParam;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.overview.information.InformationMybatisParam.InformationListInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.overview.information.InformationMybatisParam.InformationOutput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InformationService extends AbstractGaiaCairosService {
    @Autowired
    CnProjectRepository cnProjectRepository;

    @Autowired
    CnAttachmentsRepository cnAttachmentsRepository;


    /**
     * 프로젝트 목록조회
     *
     * @param InformationMybatisParam.InformationListInput (input)
     * @return List<InformationOutput>
     * @throws
     */
    public List<InformationOutput> getInformationList(InformationMybatisParam.InformationListInput input) {

        String queryId = UserAuth.get(true).isAdmin()
                ? "kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.information.getInformationList"
                : "kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.information.getGAIAInformationList";

        return mybatisSession.selectList(queryId, input);
    }

    /**
     * 프로젝트 상세조회-1
     *
     * @param String (pjtNo)
     * @return CnProject
     * @throws
     */
    public CnProject getProject(String pjtNo) {
        return cnProjectRepository.findById(pjtNo).orElse(null);
    }

    /**
     * 프로젝트 상세조회-2
     *
     * @param InformationMybatisParam.InformationListInput
     * @return InformationMybatisParam.InformationOutput
     * @throws
     */
    public InformationOutput getProjectDetail(InformationListInput infoInput) {

        return  mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.information.getInformationDetail", infoInput);
    }

    /**
     * 프로젝트 정보 저장
     *
     * @param CnProject
     * @return CnProject
     * @throws
     */
    public CnProject saveProject(CnProject cnProject) {
        return cnProjectRepository.save(cnProject);
    }

    /**
     * 프로젝트 정보 삭제
     *
     * @param CnProject
     * @return CnProject
     * @throws
     */
    public void deleteProject(InformationMybatisParam.InformationDeleteInput deleteInput) {
        mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.information.updateDeleteInformation", deleteInput);
    }

    /**
     * pjtNo생성
     *
     * @param String (plcReqtype)
     * @param String (yearMonth)
     * @return String
     * @throws
     */
    public String generatePjtNo(String plcReqtype, String yearMonth) {

        String maxSerialNumberOpt = "P".equals(plcReqtype) ? cnProjectRepository.findMaxPgaiaSerialByYearMonth(yearMonth) : cnProjectRepository.findMaxGaiaSerialByYearMonth(yearMonth);

        return "P" + yearMonth + maxSerialNumberOpt;
    }

    /**
     * 첨부파일 FileNo생성
     *
     * @return
     */
    public Integer generateFileNo() {
        Integer maxFileNo = cnAttachmentsRepository.findMaxFileNo();
        return (maxFileNo == null ? 1 : maxFileNo + 1);
    }

    /**
     * 첨부파일 FileNo생성
     *
     * @return
     */
    public Integer generateSno(Integer fileNo) {
        Integer maxSno = cnAttachmentsRepository.findMaxSnoByFileNo(fileNo);
        return (maxSno == null ? 1 : maxSno + 1);
    }

    /**
     * 첨부파일 리스트 저장
     *
     * @param cnAttachments
     */
    public void saveCnAttachment(CnAttachments cnAttachments) {
        cnAttachmentsRepository.save(cnAttachments); // 파일 저장
    }

    /**
     * 첨부파일 목록조회
     *
     * @param fileNo
     * @return
     */
    public CnAttachments getFile(int fileNo) {
        return cnAttachmentsRepository.findOneByFileNoAndDltYn(fileNo, "N");
    }

    /**
     * 첨부파일 삭제
     *
     * @param fileNo
     * @return
     */
    public void deleteAttachment(CnAttachments cnAttachment, String usrId) {
        MybatisInput input = new MybatisInput().add("fileNo", cnAttachment.getFileNo()).add("sno",cnAttachment.getSno()).add("usrId", usrId);
        mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.project.updateDeleteCnAttachments",input);
    }

    /**
     * Pgaia(공공), Gaia(민간) 판별
     * @param pjtNo
     * @return
     */
    public boolean isPgaia(String pjtNo) {
        return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.information.checkPgaiaPjt", pjtNo);
    }

    /**
     * 계약번호를 통한
     * Pgaia(공공), Gaia(민간) 판별
     * @param cntrctNo
     * @return
     */
    public boolean isPgaiaCheckByCntrctNo(String cntrctNo) {
        return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.information.checkPgaiaPjtByCntrctNo", cntrctNo);
    }

    /**
     * 현장개설요청를 통한 진행시
     * 공사진행상태: 개설중(0601) -> 시공(0602) 상태변경
     *
     * 연관 공통코드: 공사진행상태(d5b531ad-6c9d-c2e6-f9b7-7c1aca9d4711)
     * @param plcReqNo
     * @param status
     * @return
     */
    public void updateStatus(String plcReqNo,String status) {
        if ("0704".equals(status)) {
            mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.information.updateInformationConPstats", plcReqNo);
        }else{
            throw new GaiaBizException(ErrorType.BAD_REQUEST, "프로젝트가 개설중이 아닙니다.");
        }
    }

}
