package kr.co.ideait.platform.gaiacairos.comp.project.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnSubcontract;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnSubcontractChange;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.CnSubcontractChangeRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.CnSubcontractRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.contract.subcontract.SubcontractMybatisParam.SubcontractChangeInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.contract.subcontract.SubcontractMybatisParam.SubcontractChangeListInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.contract.subcontract.SubcontractMybatisParam.SubcontractChangeListOutput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.contract.subcontract.SubcontractMybatisParam.SubcontractChangeOutput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.contract.subcontract.SubcontractMybatisParam.SubcontractInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.contract.subcontract.SubcontractMybatisParam.SubcontractListInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.contract.subcontract.SubcontractMybatisParam.SubcontractListOutput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.contract.subcontract.SubcontractMybatisParam.SubcontractOutput;
import kr.co.ideait.platform.gaiacairos.core.constant.CommonCodeConstants;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SubcontractService extends AbstractGaiaCairosService {
    @Autowired
    CnSubcontractRepository cnsubContractRepository;

    @Autowired
    CnSubcontractChangeRepository cnSubcontractChangeRepository;

    // 하도급 목록 조회
    public List<SubcontractListOutput> getSubcontractList(SubcontractListInput subcontractListInput) {
        return  mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.subcontract.getSubcontractList",subcontractListInput);
    }

    // 하도급 조회
    public CnSubcontract getSubcontract(String cntrctNo, Long scontrctCorpId) {
        return cnsubContractRepository.findByCntrctNoAndScontrctCorpId(cntrctNo, scontrctCorpId).orElse(null);
    }

    // 하도급 조회
    public SubcontractOutput getLoadData(SubcontractInput subcontractInput) {
        return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.subcontract.getSubcontract",subcontractInput);
    }

    // 하도급 업체 생성
    @Transactional
    public CnSubcontract subContractCreate(CnSubcontract cnSubcontract) {
        return cnsubContractRepository.save(cnSubcontract);
    }

    // 하도급 id 증가
    public Long generateScontrctCorpId(String cntrctNo) {
        Long maxScontrctCorpId = cnsubContractRepository.findMaxScontrctCorpIdByCntrctNo(cntrctNo);
        return (maxScontrctCorpId == null ? 1 : maxScontrctCorpId + 1);
    }

    /* 하도급 업체 삭제 */
    @Transactional
    public void subcontractDelete(List<CnSubcontract> subcontract) {
        subcontract.forEach(id -> {
            CnSubcontract cnSubcontract = cnsubContractRepository
                    .findByCntrctNoAndScontrctCorpId(id.getCntrctNo(), id.getScontrctCorpId()).orElse(null);
            if (subcontract != null) {
                cnsubContractRepository.updateDelete(cnSubcontract);
            }
        });
    }

    /* 하도급 업체 수정 */
    @Transactional
    public CnSubcontract subcontractUpdate(CnSubcontract cnSubcontract) {
        return cnsubContractRepository.save(cnSubcontract);
    }

    /**
     * 하도급 전체 삭제
     */
    public void deleteAllSubContract(MybatisInput input) {
        mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.subcontract.deleteAllSubContract", input);
    }

    // --------------------------------------------------------------------------------------------

    // 하도급 추가시 하도급 변경 생성
    @Transactional
    public CnSubcontractChange subContractChangeCreate(CnSubcontract cnSubcontract) {

        DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

        // 날짜 문자열을 LocalDate로 변환
        LocalDate startDate = LocalDate.parse(cnSubcontract.getCntrctBgnDate(), DATE_FORMATTER);
        LocalDate endDate = LocalDate.parse(cnSubcontract.getCntrctEndDate(), DATE_FORMATTER);

        // 기간 계산
        long date = ChronoUnit.DAYS.between(startDate, endDate);

        String cntrctNo = cnSubcontract.getCntrctNo();
        Long scontrctCorpId = cnSubcontract.getScontrctCorpId();

        // CnSubcontractChange 객체 설정
        CnSubcontractChange cnChange = new CnSubcontractChange();
        cnChange.setCntrctNo(cnSubcontract.getCntrctNo());
        cnChange.setScontrctCorpId(cnSubcontract.getScontrctCorpId());
        cnChange.setCntrctChgId((long) 1);
        cnChange.setCntrctChgNo("1");
        cnChange.setCntrctChgDate(cnSubcontract.getCntrctDate());
        cnChange.setChgCbgnDate(cnSubcontract.getCntrctEndDate());
        cnChange.setChgConPrd(date + 1);
        cnChange.setCntrctAmt(cnSubcontract.getScontrctCntrctAmt());
        cnChange.setRmrk(cnSubcontract.getRmrk());
        cnChange.setDltYn("N");

        log.debug("cnChange", cnChange);

        // 저장
        return cnSubcontractChangeRepository.save(cnChange);
    }

    // 하도급 계약변경 목록 조회
    public List<SubcontractChangeListOutput> getSubcontractChangeList(
            SubcontractChangeListInput subcontractChangeListInput) {

        String cntrctNo = subcontractChangeListInput.getCntrctNo();
        subcontractChangeListInput.setCmnGrpCdCntrctChgType(CommonCodeConstants.CNTRCT_CHG_TYPE);

        // 입력값 업데이트
        subcontractChangeListInput.setCntrctNo(cntrctNo);
        log.debug("CntrctNo={}", subcontractChangeListInput.getCntrctNo());

        // MyBatis를 통해 데이터 조회
        List<SubcontractChangeListOutput> subcontractChangeListOutputs = mybatisSession
                .selectList(
                        "kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.subcontract.getSubcontractChangeList",
                        subcontractChangeListInput);
        return subcontractChangeListOutputs;
    }

    // 하도급 계약변경 조회
    public SubcontractChangeOutput getSubcontractChange(SubcontractChangeInput subcontractChangeInput) {
        subcontractChangeInput.setCmnGrpCdWorkType(CommonCodeConstants.WORKTYPE_CODE_GROUP_CODE);
        subcontractChangeInput.setCmnGrpCdIndstryty(CommonCodeConstants.INDSTRYTY_CODE_GROUP_CODE);
        subcontractChangeInput.setCmnGrpCdCntrctChgType(CommonCodeConstants.CNTRCT_CHG_TYPE);
        // MyBatis를 통해 데이터 조회 (단일 결과)
        SubcontractChangeOutput subcontractChangeOutput = mybatisSession
                .selectOne(
                        "kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.subcontract.getSubcontractChange",
                        subcontractChangeInput);
        return subcontractChangeOutput;
    }

    // 하도급 계약변경 추가
    @Transactional
    public CnSubcontractChange subContractChangeAdd(CnSubcontractChange cnSubcontractChange) {
        Long cntrctChgId = generateCntrctChgId(cnSubcontractChange.getCntrctNo(),
                cnSubcontractChange.getScontrctCorpId());
        cnSubcontractChange.setCntrctChgId(cntrctChgId);

        if (cnSubcontractChange.getDltYn() == null) {
            cnSubcontractChange.setDltYn("N");
        }
        return cnSubcontractChangeRepository.save(cnSubcontractChange);
    }

    // 하도급 계약변경 id 증가
    @Transactional
    private Long generateCntrctChgId(String cntrctNo, Long scontrctCorpId) {
        Long maxCntrctChgId = cnSubcontractChangeRepository.findMaxCntrctChgNoByCntrctNoAndScontrctCorpId(cntrctNo,
                scontrctCorpId);
        return (maxCntrctChgId == null ? 1 : maxCntrctChgId + 1);
    }

    /* 하도급 계약변경 삭제 */
    @Transactional
    public void subcontractChangeDelete(List<CnSubcontractChange> subcontractChanges) {
        subcontractChanges.forEach(id -> {
            CnSubcontractChange cnSubcontractChange = cnSubcontractChangeRepository
                    .findByCntrctNoAndScontrctCorpIdAndCntrctChgId(id.getCntrctNo(), id.getScontrctCorpId(),
                            id.getCntrctChgId())
                    .orElse(null);
            if (subcontractChanges != null) {
                cnSubcontractChangeRepository.updateDelete(cnSubcontractChange);
            }
        });
    }

    /* 하도급 계약변경 수정 */
    @Transactional
    public CnSubcontractChange subcontractChangeUpdate(CnSubcontractChange cnSubcontractChange) {
        return cnSubcontractChangeRepository.save(cnSubcontractChange);
    }

    // 하도급 계약변경 조회
    public CnSubcontractChange getSubcontractChange(String cntrctNo, Long scontrctCorpId, Long cntrctChgId) {
        return cnSubcontractChangeRepository
                .findByCntrctNoAndScontrctCorpIdAndCntrctChgId(cntrctNo, scontrctCorpId, cntrctChgId)
                .orElse(null);
    }

    // 하도급 계약변경 차수증가
    @Transactional
    public String generateCntrctChgNo(String cntrctNo, Long scontrctCorpId) {
        List<String> cntrctChgNoList = cnSubcontractChangeRepository.findAllCntrctChgNoByScontrctCorpIdAnd(cntrctNo,
                scontrctCorpId);
        Integer maxCntrctChgNo = cntrctChgNoList.stream()
                .map(Integer::parseInt)
                .max(Integer::compare)
                .orElse(0);
        return String.valueOf(maxCntrctChgNo + 1);
    }

    /**
     * 하도급 전체 삭제
     */
    public void deleteAllSubChange(MybatisInput input) {
        mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.subcontract.deleteAllSubChange", input);
    }
}
