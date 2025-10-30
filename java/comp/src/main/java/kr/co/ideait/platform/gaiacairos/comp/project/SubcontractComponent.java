package kr.co.ideait.platform.gaiacairos.comp.project;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Maps;
import kr.co.ideait.platform.gaiacairos.comp.project.service.ContractstatusService;
import kr.co.ideait.platform.gaiacairos.comp.project.service.SubcontractService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractComponent;
import kr.co.ideait.platform.gaiacairos.core.constant.CommonCodeConstants;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.*;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.*;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.contract.contractstatus.ContractstatusMybatisParam;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.contract.subcontract.SubcontractForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.contract.subcontract.SubcontractMybatisParam;
import kr.co.ideait.platform.gaiacairos.core.type.PlatformType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubcontractComponent extends AbstractComponent {

    @Autowired
    SubcontractService subcontractService;

    @Autowired
    SubcontractForm subcontractForm;

    @Autowired
    CnSubcontractRepository cnsubContractRepository;

    @Autowired
    CnSubcontractChangeRepository cnSubcontractChangeRepository;

    @Autowired
    ContractstatusService contractService;

    /**
     * 계약 이름 조회
     * 조회페이지용, 수정페이지용
     */
    public String getByCntrctNm(String cntrctNo) { // 수정페이지
        return contractService.getByCntrctNm(cntrctNo);
    }

    /**
     * 도급 목록 조회
     */
    public List<ContractstatusMybatisParam.ContractcompanyOutput> getSubcontractCompanyList(String cntrctNo) {
        return contractService.getSubcontractCompanyList(cntrctNo);
    }


    // 하도급 --------------------------------------------------------------------------------------------------------------------------------------------
    // 하도급 목록 조회
    public List<SubcontractMybatisParam.SubcontractListOutput> getSubcontractList(SubcontractMybatisParam.SubcontractListInput input) {
        input.setCmnGrpCdWorkType(CommonCodeConstants.WORKTYPE_CODE_GROUP_CODE);
        input.setCmnGrpCdIndstryty(CommonCodeConstants.INDSTRYTY_CODE_GROUP_CODE);
        return subcontractService.getSubcontractList(input);
    }

    // 하도급 상세 조회
    public Map<String,Object> getSubcontract(String cntrctNo,Long scontrctCorpId) {

        SubcontractMybatisParam.SubcontractInput input = subcontractForm.toSubcontractInput(cntrctNo, scontrctCorpId);
        input.setCmnGrpCdWorkType(CommonCodeConstants.WORKTYPE_CODE_GROUP_CODE);
        input.setCmnGrpCdIndstryty(CommonCodeConstants.INDSTRYTY_CODE_GROUP_CODE);

        Map<String,Object> result = new HashMap<>();
        result.put("cntrctChgNo",subcontractService.generateCntrctChgNo(cntrctNo, scontrctCorpId));
        result.put("getSubcontract",subcontractService.getLoadData(input));

        return result;
    }

    // 하도급 추가
    @Transactional
    public CnSubcontract subContractCreate(CnSubcontract cnSubcontract,String pjtDiv,String apiYn) {

        cnSubcontract.setScontrctCorpId(subcontractService.generateScontrctCorpId(cnSubcontract.getCntrctNo()));
        cnSubcontract.setDltYn("N");

        CnSubcontract saveSubcontract = subcontractService.subContractCreate(cnSubcontract);
        CnSubcontractChange saveSubcontractChange =  subcontractService.subContractChangeCreate(cnSubcontract);

        // TODO API 연동 2 - 사업이 공공(P)일 경우 CAIROS -> PGAIA
        if("Y".equals(apiYn)) {
            if (PlatformType.CAIROS.getName().equals(platform) && "P".equals(pjtDiv)) {
                Map<String, Object> invokeParams = Maps.newHashMap();

                invokeParams.put("cnSubcontract", saveSubcontract);
                invokeParams.put("cnSubcontractChange", saveSubcontractChange);

                Map response;

                response = invokeCairos2Pgaia("CAGA1016", invokeParams);

                if (!"00".equals(MapUtils.getString(response, "resultCode"))) {
                    throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(response, "resultMsg"));
                }
            }
        }
        return saveSubcontract;
    }

    // 하도급 수정
    @Transactional
    public CnSubcontract subcontractUpdate(SubcontractForm.UpdateSubcontract subcontract,String pjtDiv,String apiYn) {

        CnSubcontract cnSubcontract = subcontractService.getSubcontract(subcontract.getCntrctNo(),
                subcontract.getScontrctCorpId());

        subcontractForm.toUpdateSubcontract(subcontract, cnSubcontract);

        CnSubcontract saveSubcontract = subcontractService.subcontractUpdate(cnSubcontract);

        // TODO API 연동 2 - 사업이 공공(P)일 경우 CAIROS -> PGAIA
        if("Y".equals(apiYn)) {
            if (PlatformType.CAIROS.getName().equals(platform) && "P".equals(pjtDiv)) {

                Map<String, Object> invokeParams = Maps.newHashMap();

                invokeParams.put("cnSubcontract", saveSubcontract);

                Map response;

                response = invokeCairos2Pgaia("CAGA1017", invokeParams);


                if (!"00".equals(MapUtils.getString(response, "resultCode"))) {
                    throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(response, "resultMsg"));
                }
            }
        }
        return saveSubcontract;
    }

    // 하도급 삭제
    @Transactional
    public void subcontractDelete(List<CnSubcontract> cnSubcontracts,String pjtDiv,String apiYn) {

        subcontractService.subcontractDelete(cnSubcontracts);

        List<CnSubcontract> cnSubcontractList = new ArrayList<>();

        for(CnSubcontract cnSubcontract : cnSubcontracts){
            cnSubcontractList.add(subcontractService.getSubcontract(cnSubcontract.getCntrctNo(),cnSubcontract.getScontrctCorpId()));
        }

        // TODO API 연동 2 - 사업이 공공(P)일 경우 CAIROS -> PGAIA
        if("Y".equals(apiYn)) {
            if (PlatformType.CAIROS.getName().equals(platform) && "P".equals(pjtDiv)) {

                Map<String, Object> invokeParams = Maps.newHashMap();

                invokeParams.put("cnSubcontractList", cnSubcontractList);

                Map response;

                response = invokeCairos2Pgaia("CAGA1018", invokeParams);


                if (!"00".equals(MapUtils.getString(response, "resultCode"))) {
                    throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(response, "resultMsg"));
                }
            }
        }
    }

    // 하도급 목록 조회
    public List<SubcontractMybatisParam.SubcontractChangeListOutput> getSubcontractChangeList(SubcontractMybatisParam.SubcontractChangeListInput input) {

        return subcontractService.getSubcontractChangeList(input);
    }

    // 하도급 상세 조회
    public SubcontractMybatisParam.SubcontractChangeOutput getSubcontractChange(SubcontractMybatisParam.SubcontractChangeInput input) {

        return subcontractService.getSubcontractChange(input);
    }

    // 하도급계약변경 추가
    @Transactional
    public CnSubcontractChange subContractChangeAdd(CnSubcontractChange cnSubcontractChange,String pjtDiv,String apiYn) {

        CnSubcontractChange saveSubcontractChange = subcontractService.subContractChangeAdd(cnSubcontractChange);

        // TODO API 연동 2 - 사업이 공공(P)일 경우 CAIROS -> PGAIA
        if("Y".equals(apiYn)) {
            if (PlatformType.CAIROS.getName().equals(platform) && "P".equals(pjtDiv)) {
                Map<String, Object> invokeParams = Maps.newHashMap();

                invokeParams.put("cnSubcontractChange", saveSubcontractChange);

                Map response;

                response = invokeCairos2Pgaia("CAGA1019", invokeParams);

                if (!"00".equals(MapUtils.getString(response, "resultCode"))) {
                    throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(response, "resultMsg"));
                }
            }
        }
        return saveSubcontractChange;
    }

    // 하도급계약변경 수정
    @Transactional
    public CnSubcontractChange subcontractChangeUpdate(SubcontractForm.UpdateSubcontractChange cnSubcontractChange,String pjtDiv,String apiYn) {

        CnSubcontractChange subcontractChange = subcontractService.getSubcontractChange(
                cnSubcontractChange.getCntrctNo(), cnSubcontractChange.getScontrctCorpId(), cnSubcontractChange.getCntrctChgId());

        subcontractForm.toUpdateSubcontractChange(cnSubcontractChange, subcontractChange);
        CnSubcontractChange saveSubcontractChange = subcontractService.subcontractChangeUpdate(subcontractChange);

        // TODO API 연동 2 - 사업이 공공(P)일 경우 CAIROS -> PGAIA
        if("Y".equals(apiYn)) {
            if (PlatformType.CAIROS.getName().equals(platform) && "P".equals(pjtDiv)) {

                Map<String, Object> invokeParams = Maps.newHashMap();

                invokeParams.put("cnSubcontractChange", saveSubcontractChange);

                Map response;
                response = invokeCairos2Pgaia("CAGA1020", invokeParams);


                if (!"00".equals(MapUtils.getString(response, "resultCode"))) {
                    throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(response, "resultMsg"));
                }
            }
        }
        return saveSubcontractChange;
    }

    // 하도급 삭제
    @Transactional
    public void subcontractChangeDelete(List<CnSubcontractChange> subcontractChanges,String pjtDiv,String apiYn) {

        subcontractService.subcontractChangeDelete(subcontractChanges);

        List<CnSubcontractChange> cnSubcontractChangeList = new ArrayList<>();

        for(CnSubcontractChange cnSubcontractChange : subcontractChanges){
            cnSubcontractChangeList.add(subcontractService.getSubcontractChange(cnSubcontractChange.getCntrctNo(),cnSubcontractChange.getScontrctCorpId(),cnSubcontractChange.getCntrctChgId()));
        }

        // TODO API 연동 2 - 사업이 공공(P)일 경우 CAIROS -> PGAIA
        if("Y".equals(apiYn)) {
            if (PlatformType.CAIROS.getName().equals(platform) && "P".equals(pjtDiv)) {

                Map<String, Object> invokeParams = Maps.newHashMap();

                invokeParams.put("cnSubcontractChangeList", cnSubcontractChangeList);

                Map response;

                response = invokeCairos2Pgaia("CAGA1021", invokeParams);


                if (!"00".equals(MapUtils.getString(response, "resultCode"))) {
                    throw new GaiaBizException(ErrorType.INTERFACE, MapUtils.getString(response, "resultMsg"));
                }
            }
        }
    }




    // ----------------------------------------API통신--------------------------------------------

    /**
     * API 수신 처리 메서드
     *
     * CAGA1016=하도급추가
     * CAGA1017=하도급수정
     * CAGA1018=하도급삭제
     * CAGA1019=하도급계약변경추가
     * CAGA1020=하도급계약변경수정
     * CAGA1021=하도급계약변경삭제
     *
     * @param transactionId
     * @param params
     * @return
     */
    @Transactional
    public Map receiveInterfaceService(String transactionId, Map params) {
        log.info("receiveInterfaceService - {}", transactionId);
        Map<String, Object> result = Maps.newHashMap();
        result.put("resultCode", "00");
        result.put("resultMsg", "정상 처리되었습니다/");

        try {
            if ("CAGA1016".equals(transactionId)) {             // 하도급 추가

                CnSubcontract cnSubcontract = objectMapper.convertValue(params.get("cnSubcontract"), CnSubcontract.class);
                CnSubcontractChange cnSubcontractChange = objectMapper.convertValue(params.get("cnSubcontractChange"), CnSubcontractChange.class);

                subcontractService.subContractCreate(cnSubcontract);
                cnsubContractRepository.flush();

                cnSubcontractChangeRepository.save(cnSubcontractChange);

            } else if ("CAGA1017".equals(transactionId)) {      // 하도급 수정

                CnSubcontract cnSubcontract = objectMapper.convertValue(params.get("cnSubcontract"), CnSubcontract.class);
                subcontractService.subcontractUpdate(cnSubcontract);

            } else if ("CAGA1018".equals(transactionId)) {      // 하도급 삭제

                List<CnSubcontract> cnSubcontractList = objectMapper.convertValue(params.get("cnSubcontractList"), new TypeReference<List<CnSubcontract>>() {});
                cnsubContractRepository.saveAll(cnSubcontractList);

            } else if ("CAGA1019".equals(transactionId)) {      // 하도급계약변경 추가

                CnSubcontractChange cnSubcontractChange = objectMapper.convertValue(params.get("cnSubcontractChange"), CnSubcontractChange.class);
                subcontractService.subcontractChangeUpdate(cnSubcontractChange);

            } else if ("CAGA1020".equals(transactionId)) {      // 하도급계약변경 수정

                CnSubcontractChange cnSubcontractChange = objectMapper.convertValue(params.get("cnSubcontractChange"), CnSubcontractChange.class);
                subcontractService.subcontractChangeUpdate(cnSubcontractChange);

            } else if ("CAGA1021".equals(transactionId)) {      // 하도급계약변경 삭제

                List<CnSubcontractChange> cnSubcontractChangeList = objectMapper.convertValue(params.get("cnSubcontractChangeList"), new TypeReference<List<CnSubcontractChange>>() {});
                cnSubcontractChangeRepository.saveAll(cnSubcontractChangeList);

            }

        }  catch (GaiaBizException ex) {
            result.put("resultCode", "01");
            result.put("resultMsg", ex.getMessage());
        }

        return result;
    }

}
