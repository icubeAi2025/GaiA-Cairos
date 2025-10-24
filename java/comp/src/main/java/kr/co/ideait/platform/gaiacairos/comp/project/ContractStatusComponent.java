package kr.co.ideait.platform.gaiacairos.comp.project;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Maps;
import kr.co.ideait.iframework.BaseUtil;
import kr.co.ideait.iframework.BizException;
import kr.co.ideait.iframework.EtcUtil;
import kr.co.ideait.iframework.FormatUtil;
import kr.co.ideait.platform.gaiacairos.comp.eapproval.service.DraftService;
import kr.co.ideait.platform.gaiacairos.comp.progress.service.RevisionService;
import kr.co.ideait.platform.gaiacairos.comp.project.helper.ProjectInitializer;
import kr.co.ideait.platform.gaiacairos.comp.project.service.ContractstatusService;
import kr.co.ideait.platform.gaiacairos.comp.project.service.InformationService;
import kr.co.ideait.platform.gaiacairos.comp.project.service.SubcontractService;
import kr.co.ideait.platform.gaiacairos.comp.projectcost.service.C3RService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractComponent;
import kr.co.ideait.platform.gaiacairos.core.constant.CommonCodeConstants;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.*;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.CnContractChangeRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.CnContractCompanyRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.CnContractRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Result;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.contract.contractstatus.ContractChangeForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.contract.contractstatus.ContractCompanyForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.contract.contractstatus.ContractstatusForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.contract.contractstatus.ContractstatusMybatisParam;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.contract.contractstatus.ContractstatusMybatisParam.ContractInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.log.Log;
import kr.co.ideait.platform.gaiacairos.core.components.log.SystemLogComponent;
import kr.co.ideait.platform.gaiacairos.core.type.PlatformType;
import kr.co.ideait.platform.gaiacairos.core.util.restclient.EurecaClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContractStatusComponent extends AbstractComponent {

    private final InformationService informationService;

    private final EurecaClient eurecaClient;

    @Autowired
    ContractstatusService contractService;

    @Autowired
    CnContractRepository contractRepository;

    @Autowired
    CnContractCompanyRepository companyRepository;

    @Autowired
    CnContractChangeRepository changeRepository;

    @Autowired
    ContractstatusForm contractstatusForm;

    @Autowired
    ContractCompanyForm contractCompanyForm;

    @Autowired
    ContractChangeForm contractChangeForm;

    @Autowired
    C3RService c3RService;

    @Autowired
    RevisionService revisionService;

    @Autowired
    SubcontractService subcontractService;

    @Autowired
    ProjectInitializer projectInitializer;

    @Autowired
    DraftService draftService;

    @Autowired
    SystemLogComponent systemLogComponent;

    // [DEV] EURECA - 공사담당자 ID 기본 값
    private static final String DEV_EURECA_OFCL_ID = "0000000476";

    // [PROD] EURECA - 공사담당자 ID 기본 값
    private static final String PROD_EURECA_OFCL_ID = "0000000446";

    /* ==================================================================================================================
     *
     * 계약 현황 - 계약
     *
     * ==================================================================================================================
     */

    /**
     * 계약 목록 조회
     * 일반 조회
     * CMIS 조회
     */
    public List<ContractstatusMybatisParam.ContractstatusOutput> getList(ContractstatusMybatisParam.ContractstatusListInput input) {
        input.setCode(CommonCodeConstants.WORKTYPE_CODE_GROUP_CODE);
        return contractService.getList(input);
    }

    /**
     * 계약 상세 조회
     */
    public Map<String, Object> getCntrctDetail(String cntrctNo) { // 조회페이지
        Map<String, Object> params = new HashMap<>();
        params.put("cntrctNo", cntrctNo);
        params.put("workcode", CommonCodeConstants.WORKTYPE_CODE_GROUP_CODE);
        params.put("cntrctcode", CommonCodeConstants.CNTRCT_TYPE_GROUP_CODE);
        params.put("contractcode", CommonCodeConstants.CONTRACT_TYPE_GROUP_CODE);
        return contractService.getCntrctDetail(params);
    }


    /**
     * 계약 추가
     * @param contractForm
     * @return
     */
    @Transactional
    public Result createContractFullProcess(
            ContractstatusForm.CreateContract contractForm,
            String pjtDiv,
            String apiYn,
            String userId) {
        // 1. 계약 정보 생성
        CnContract cnContract = contractstatusForm.toCnContract(contractForm);
        cnContract.setCntrctNo(contractService.generateContractNumber(contractForm.getPjtNo(), contractForm.getMajorCnsttyCd()));
        cnContract.setDltYn("N");

        // 2. 프로시저 전달용
        String pInserttype = "ADD";
        String pPjttype = "C";
        String pPjtno = cnContract.getPjtNo();
        String pCntrctno = cnContract.getCntrctNo();
        String pItemname = cnContract.getCntrctNm();
        String pItemdesc = (cnContract.getCntrctNm() != null) ? cnContract.getCntrctNm() : "0";
        String pCorpno = (cnContract.getCorpNo() != null) ? cnContract.getCorpNo() : "0";

        ContractInput input = new ContractInput();

        input.setPCntrctno(pCntrctno);
        input.setPCorpno(pCorpno);
        input.setPInserttype(pInserttype);
        input.setPItemdesc(pItemdesc);
        input.setPItemname(pItemname);
        input.setPPjtno(pPjtno);
        input.setPPjttype(pPjttype);

        // 3. 계약 추가
        cnContract = contractService.saveContract(cnContract);
        projectInitializer.addProject(input.getPCorpno(), input.getPPjttype(), input.getPPjtno(), input.getPCntrctno(), input.getPItemname(), input.getPItemdesc());

        // 4. 대표 도급 등록
        CnContractCompany defaultCompany = contractstatusForm.toCnCompany(contractForm);
        defaultCompany.setCnsttyCd(cnContract.getMajorCnsttyCd());
        defaultCompany.setCntrctNo(cnContract.getCntrctNo());
        defaultCompany.setDltYn("N");
        defaultCompany.setRprsYn("Y"); // 대표자 체크
        defaultCompany = contractService.createCompany(defaultCompany);

        // 5. 1회차 변경 등록
        CnContractChange cnContractChange = contractService.createDefaultChange(cnContract);

        // 6. 전자결재 기본 서식 생성
        List<ApDraftForm> apDraftForm = draftService.createDefaultApForm(cnContract.getPjtNo(), cnContract.getCntrctNo(), cnContract.getRgstrId());

        if (apiYn == null || pjtDiv == null) {
            throw new GaiaBizException(ErrorType.BAD_REQUEST, "잠시 후 다시 시도 바랍니다.");
        }

        log.info("API 연동여부: {}", apiYn);
        if("Y".equals(apiYn)) {
            // API 연동 1 - 사업이 공공(P)일 경우 CAIROS -> PGAIA
            Log.SmApiLogDto smApiLog1 = null;
            try {
                if (PlatformType.CAIROS.getName().equals(platform) && "P".equals(pjtDiv)) {
                    /* LOG 작성 (CAIROS -> PGAIA)  */
                    smApiLog1 = new Log.SmApiLogDto();
                    smApiLog1.setApiId("CAGA1007");
                    smApiLog1.setApiType("OUT");
                    smApiLog1.setServiceType("createContract");
                    smApiLog1.setServiceUuid(UUID.randomUUID().toString());
                    smApiLog1.setSourceSystemCode("CAIROS");
                    smApiLog1.setTargetSystemCode("PGAIA");
                    smApiLog1.setReqMethod("POST");
                    smApiLog1.setReqData("계약추가");
                    smApiLog1.setReqDt(FormatUtil.formatDate(new Date(), "yyyyMMddHHmmss"));
                    smApiLog1.setRgstrId(userId);
                    smApiLog1.setChgId(userId);
                    smApiLog1.setResultCode(200);
                    smApiLog1.setErrorYn("N");

                    Map result = invokeCairos2Pgaia("CAGA1007", Map.of(
                            "cnContract", cnContract,
                            "input", input,
                            "defaultCompany", defaultCompany,
                            "apDraftForm", apDraftForm
                    ));

                    String resultCode = result.get("resultCode").toString();
                    if (!"00".equals(resultCode)) {
                        throw new GaiaBizException(ErrorType.INTERFACE, "Cairos2Pgaia - CAGA1007");
                    }
                }
            } catch (GaiaBizException e) {
                log.error("createContractFullProcess - Cairos2Pgaia API 연동 실패: {}", e.getMessage(), e);
                if (smApiLog1 != null) {
                    smApiLog1.setResultCode(500);
                    smApiLog1.setErrorYn("Y");
                    smApiLog1.setErrorReason(e.getMessage());
                    smApiLog1.setResDt(FormatUtil.formatDate(new Date(), "yyyyMMddHHmmss"));
                }
            } catch (Exception e) {
                log.error("createContractFullProcess - Cairos2Pgaia API 연동 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
                if (smApiLog1 != null) {
                    smApiLog1.setResultCode(500);
                    smApiLog1.setErrorYn("Y");
                    smApiLog1.setErrorReason(e.getMessage());
                    smApiLog1.setResDt(FormatUtil.formatDate(new Date(), "yyyyMMddHHmmss"));
                }
            } finally {
                if (smApiLog1 != null) {
                    systemLogComponent.asyncAddApiLog(smApiLog1);
                }
            }

            // API 연동 2 - CAIROS -> EURECA
            try{
                if (PlatformType.CAIROS.getName().equals(platform)) {
                    // 공사-최초계약 유레카 전송
                    CnProject cnProject = informationService.getProject(cnContract.getPjtNo());
                    Map<String, String> eurecaParams = this.getProjectInfoEurecaParams(cnProject, cnContract);   //공사정보 조회
                    eurecaParams.putAll(this.getContractInfoByEurecaParams(cnContract, cnContractChange));       //계약정보 조회

                    Map<String, Object> eurecaResult = eurecaClient.registerCnstwk(eurecaParams, userId);

                    if ( !"00".equals( MapUtils.getString(eurecaResult, "resultCode") ) ) {
                        throw new GaiaBizException(ErrorType.INTERFACE, "유레카 연동실패");
                    }

                    if ("0103".equals(cnContract.getCntrctDivCd())) {
                        // 20250819 계약구분 - "장기계속계약_총체" 일 경우 "장기계속계약_차수별" 1벌 더 전송처리
                        eurecaParams.put("cntrctDivCd", "04");
                        eurecaParams.put("lngtmCntnuCntrctOrd", "1");
                        Map<String, Object> eurecaResult2 = eurecaClient.registerCnstwk(eurecaParams, userId);

                        if ( !"00".equals( MapUtils.getString(eurecaResult2, "resultCode") ) ) {
                            throw new GaiaBizException(ErrorType.INTERFACE, "유레카 연동실패");
                        }
                    } else if ("0104".equals(cnContract.getCntrctDivCd())) {
                        // 계약구분 - "장기계속계약_차수별" 일 경우 계약변경을 유레카에 전송
                        Map<String, Object> eurecaResult3 = eurecaClient.registerCntrct(eurecaParams, userId);

                        if ( !"00".equals( MapUtils.getString(eurecaResult3, "resultCode") ) ) {
                            throw new GaiaBizException(ErrorType.INTERFACE, "유레카 연동실패");
                        }
                    }
                }
            } catch (GaiaBizException e) {
                log.error("createContractFullProcess - Eureca API 연동 실패: {}", e.getMessage(), e);

            } catch (Exception e) {
                log.error("createContractFullProcess - Eureca API 연동 중 예상치 못한 오류 발생: {}", e.getMessage(), e);

            }
        }


        return Result.ok();
    }

    /**
     * 계약 수정
     * @param cntrctNo
     * @param contractUpdate
     * @return
     */
    @Transactional
    public Result updateContractFullProcess(
            String cntrctNo,
            ContractstatusForm.ContractUpdate contractUpdate,
            String pjtDiv,
            String apiYn,
            String userId) {
        CnContract cnContract = contractService.getByCntrctNo(cntrctNo); // 기존 계약
        contractstatusForm.updateContract(contractUpdate, cnContract); // 기존 계약에 수정된 내용 덮어쓰기

        // 최초도급 수정
        CnContractCompany rprsCompany = contractService.getRprsCompany(cntrctNo);
        rprsCompany.setBsnsmnNo(contractUpdate.getBsnsmnNo());
        rprsCompany.setCorpNm(contractUpdate.getCorpNm());
        rprsCompany.setCorpNo(contractUpdate.getCorpNo());
        rprsCompany.setCorpAdrs(contractUpdate.getCorpAdrs());
        rprsCompany.setCorpCeo(contractUpdate.getCorpCeo());
        rprsCompany.setTelNo(contractUpdate.getTelNo());
        rprsCompany.setFaxNo(contractUpdate.getFaxNo());
        contractService.updateCompany(rprsCompany);

        // 1회차 변경 수정 & 유레카 API 연동
        CnContractChange firstChange = contractService.getFirstChange(cntrctNo,cnContract.getCntrctDivCd());
        firstChange.setChgConPrd(contractUpdate.getConPrd());
        firstChange.setChgThisConPrd(contractUpdate.getThisConPrd());
        firstChange.setChgCbgnDate(cnContract.getCcmpltDate());
        firstChange.setChgThisCbgnDate(cnContract.getThisCcmpltDate());
        CnContractChange cnContractChange = contractService.updateChange(firstChange, 0);

        String pInserttype = "UPDATE";
        String pPjttype = "C";
        String pPjtno = cnContract.getPjtNo();
        String pCntrctno = cnContract.getCntrctNo();
        String pItemname = contractUpdate.getCntrctNm();
        String pItemdesc = (contractUpdate.getCntrctNm() != null) ? contractUpdate.getCntrctNm() : "0";
        String pCorpno = (cnContract.getCorpNo() != null) ? cnContract.getCorpNo() : "0"; // 추후에 업체번호 가져오기로 수정된 업체번호 등록

        ContractInput input = new ContractInput();
        input.setPCntrctno(pCntrctno);
        input.setPCorpno(pCorpno);
        input.setPInserttype(pInserttype);
        input.setPItemdesc(pItemdesc);
        input.setPItemname(pItemname);
        input.setPPjtno(pPjtno);
        input.setPPjttype(pPjttype);

        projectInitializer.modifyProject(input.getPCorpno(), input.getPPjttype(), input.getPPjtno(), input.getPCntrctno(), input.getPItemname(), input.getPItemdesc());
        contractService.saveContract(cnContract);
        log.info("API 연동여부: {}", apiYn);
        if("Y".equals(apiYn)) {
            // API 연동 1 - 사업이 공공(P)일 경우 CAIROS -> PGAIA
            Log.SmApiLogDto smApiLog1 = null;
            try {
                if (PlatformType.CAIROS.getName().equals(platform) && "P".equals(pjtDiv)) {
                    /* LOG 작성 (CAIROS -> PGAIA)  */
                    smApiLog1 = new Log.SmApiLogDto();
                    smApiLog1.setApiId("CAGA1008");
                    smApiLog1.setApiType("OUT");
                    smApiLog1.setServiceType("updateContract");
                    smApiLog1.setServiceUuid(UUID.randomUUID().toString());
                    smApiLog1.setSourceSystemCode("CAIROS");
                    smApiLog1.setTargetSystemCode("PGAIA");
                    smApiLog1.setReqMethod("POST");
                    smApiLog1.setReqData("계약수정");
                    smApiLog1.setReqDt(FormatUtil.formatDate(new Date(), "yyyyMMddHHmmss"));
                    smApiLog1.setRgstrId(userId);
                    smApiLog1.setChgId(userId);
                    smApiLog1.setResultCode(200);
                    smApiLog1.setErrorYn("N");

                    Map result = invokeCairos2Pgaia("CAGA1008", Map.of(
                            "cnContract", cnContract,
                            "input", input,
                            "firstChange", firstChange,
                            "rprsCompany", rprsCompany
                    ));

                    String resultCode = result.get("resultCode").toString();
                    if (!"00".equals(resultCode)) {
                        throw new GaiaBizException(ErrorType.INTERFACE, "Cairos2Pgaia - CAGA1008");
                    }
                }
            } catch (GaiaBizException e) {
                log.error("updateContractFullProcess - Cairos2Pgaia API 연동 실패: {}", e.getMessage(), e);
                if (smApiLog1 != null) {
                    smApiLog1.setResultCode(500);
                    smApiLog1.setErrorYn("Y");
                    smApiLog1.setErrorReason(e.getMessage());
                    smApiLog1.setResDt(FormatUtil.formatDate(new Date(), "yyyyMMddHHmmss"));
                }
            } catch (Exception e) {
                log.error("updateContractFullProcess - Cairos2Pgaia API 연동 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
                if (smApiLog1 != null) {
                    smApiLog1.setResultCode(500);
                    smApiLog1.setErrorYn("Y");
                    smApiLog1.setErrorReason(e.getMessage());
                    smApiLog1.setResDt(FormatUtil.formatDate(new Date(), "yyyyMMddHHmmss"));
                }
            } finally {
                if (smApiLog1 != null) {
                    systemLogComponent.asyncAddApiLog(smApiLog1);
                }
            }

            // API 연동 2 - CAIROS -> EURECA
            try {
                if (PlatformType.CAIROS.getName().equals(platform)) {
                    // 계약구분 - "장기계속계약_차수" 일 경우 계약변경을 유레카에 전송
                    if ("0104".equals(cnContract.getCntrctDivCd())) {

                        // 공사-최초계약 유레카 전송
                        CnProject cnProject = informationService.getProject(cnContract.getPjtNo());
                        Map<String, String> eurecaParams = this.getProjectInfoEurecaParams(cnProject, cnContract); //공사정보 조회
                        eurecaParams.putAll(this.getContractInfoByEurecaParams(cnContract, cnContractChange));//계약정보 조회

                        Map<String, Object> eurecaResult2 = eurecaClient.registerCntrct(eurecaParams, userId);

                        if ( !"00".equals( MapUtils.getString(eurecaResult2, "resultCode") ) ) {
                            throw new GaiaBizException(ErrorType.INTERFACE, "유레카 연동실패");
                        }
                    }
                }
            } catch (GaiaBizException e) {
                log.error("updateContractFullProcess - Eureca API 연동 실패: {}", e.getMessage(), e);
            } catch (Exception e) {
                log.error("updateContractFullProcess - Eureca API 연동 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            }
        }
        return Result.ok();
    }

    /**
     * 계약 삭제
     * @param contractList
     * @return
     */
    @Transactional
    public Result deleteContracts(ContractstatusForm.ContractList contractList,String pjtDiv,String apiYn) {
        for (int i = 0; i < contractList.getContractList().size(); i++) {
            CnContract contract = contractRepository.findById(contractList.getContractList().get(i)).orElse(null);
            String pInserttype = "DEL";
            String pPjttype = "C";
            String pPjtno = contract.getPjtNo();
            String pCntrctno = contract.getCntrctNo();
            String pItemname = contract.getCntrctNm();
            String pItemdesc = (contract.getCntrctNm() != null) ? contract.getCntrctNm() : "0";
            String pCorpno = (contract.getCorpNo() != null) ? contract.getCorpNo() : "0";

            ContractInput input = new ContractInput();
            input.setPCntrctno(pCntrctno);
            input.setPCorpno(pCorpno);
            input.setPInserttype(pInserttype);
            input.setPItemdesc(pItemdesc);
            input.setPItemname(pItemname);
            input.setPPjtno(pPjtno);
            input.setPPjttype(pPjttype);

            projectInitializer.removeProject(input.getPPjttype(), input.getPPjtno(), input.getPCntrctno());

            MybatisInput cInput = new MybatisInput().add("cntrctNo", contract.getCntrctNo()).add("usrId",UserAuth.get(true).getUsrId());
            int cResult = contractService.deleteContract(cInput);
            if(cResult  > 0){
                contractService.deleteAllCompany(cInput);
                contractService.deleteAllChange(cInput);
                subcontractService.deleteAllSubContract(cInput);
                subcontractService.deleteAllSubChange(cInput);
                revisionService.deleteAllRevision(cInput);
            }
            draftService.deleteApForm(contract.getPjtNo(), contract.getCntrctNo(), UserAuth.get(true).getUsrId());
        }
        log.info("API 연동여부: {}", apiYn);
        if("Y".equals(apiYn)) {
            // API 연동 - 사업이 공공(P)일 경우 CAIROS -> PGAIA
            if ( PlatformType.CAIROS.getName().equals(platform) && "P".equals(pjtDiv)) {
                Map result = invokeCairos2Pgaia("CAGA1009", Map.of(
                        "contractList", contractList,
                        "usrId", UserAuth.get(true).getUsrId()
                ));

                String resultCode = result.get("resultCode").toString();
                if (!"00".equals(resultCode)) {
                    throw new GaiaBizException(ErrorType.INTERFACE, "Cairos2Pgaia - CAGA1009");
                }
            }
        }

        return Result.ok();
    }

    /**
     * 계약 전부 삭제
     */
    @Transactional
    public void deleteAllContract(List<String> pjtNoList,String usrId) {
        for (String pjtNo : pjtNoList) {
            ContractstatusMybatisParam.ContractDeleteInput deleteInput = new ContractstatusMybatisParam.ContractDeleteInput();
            deleteInput.setPjtNo(pjtNo);
            deleteInput.setUsrId(usrId);

            List<String> cntrctNoList = contractService.getCntrctNoList(pjtNo);
            int cResult = contractService.deleteAllContract(deleteInput);

            if(cResult > 0){
                for (String cntrctNo : cntrctNoList) {
                    MybatisInput cInput = new MybatisInput().add("cntrctNo", cntrctNo).add("usrId", usrId);
                    contractService.deleteAllCompany(cInput);
                    contractService.deleteAllChange(cInput);
                    subcontractService.deleteAllSubContract(cInput);
                    subcontractService.deleteAllSubChange(cInput);
                    revisionService.deleteAllRevision(cInput);
                }
            }
        }
    }

    /**
     * 최초 공사 정보 등록 시 유레카 전송용 데이터셋 조회
     * @param cnProject
     * @param contract
     * @return
     */
    public Map<String, String> getProjectInfoEurecaParams(CnProject cnProject, CnContract contract) {
        if (cnProject == null) {
            throw new GaiaBizException(ErrorType.NO_DATA, "프로젝트 정보가 없습니다.");
        }

        Map<String, String> eurecaParams = new HashMap<>();

        // 공사정보
        eurecaParams.put("pjtNm", cnProject.getPjtNm()); // 프로젝트명.
        eurecaParams.put("plcLctAdrs", StringUtils.defaultString( cnProject.getPlcLctAdrs() ) ); // 현장위치주소.
        eurecaParams.put("plcLctX", cnProject.getPlcLctX() != null ? String.valueOf( cnProject.getPlcLctX() ) : "" ); // 현장위치좌표X값.
        eurecaParams.put("plcLctY", cnProject.getPlcLctY() != null ? String.valueOf( cnProject.getPlcLctY() ) : "" ); // 현장위치좌표Y값.
        eurecaParams.put("acrarchlawUsgCd", StringUtils.defaultString( cnProject.getAcrarchlawUsgCd() ) ); // 건축법상용도코드.
        eurecaParams.put("totarVal", cnProject.getTotarVal() != null ? String.valueOf( cnProject.getTotarVal() ) : "" ); // 연먼적값.
        eurecaParams.put("lndAreaVal", cnProject.getLndAreaVal() != null ? String.valueOf( cnProject.getLndAreaVal() ) : "" ); // 대지면적값.
        eurecaParams.put("archtctAreaVal", cnProject.getArchtctAreaVal() != null ? String.valueOf( cnProject.getArchtctAreaVal() ) : "" ); // 건축면적값.
        eurecaParams.put("landarchtAreaVal", cnProject.getLandarchtAreaVal() != null ? String.valueOf( cnProject.getLandarchtAreaVal() ) : "" ); // 조경면적값.
        eurecaParams.put("bdtlRate", cnProject.getBdtlRate() != null ? String.valueOf( cnProject.getBdtlRate() ) : "" ); // 건폐율백분율.
        eurecaParams.put("measrmtRate", cnProject.getMeasrmtRate() != null ? String.valueOf( cnProject.getMeasrmtRate() ) : "" ); // 용적율백분율.
        eurecaParams.put("bssFloorHgVal", cnProject.getBssFloorHgVal() != null ? String.valueOf( cnProject.getBssFloorHgVal() ) : "" ); // 기준층높이값.
        eurecaParams.put("topHgVal", cnProject.getTopHgVal() != null ? String.valueOf( cnProject.getTopHgVal() ) : "" ); // 최고높이값.
        eurecaParams.put("parkngPsblNum", cnProject.getParkngPsblNum() != null ? String.valueOf( cnProject.getParkngPsblNum() ) : "" ); // 주차가능수.
        eurecaParams.put("cnstwkScleCntnts", StringUtils.defaultString( cnProject.getCnstwkScle() ) ); // 공사규모내용.
        eurecaParams.put("airvwAtchFileNo", StringUtils.defaultString( cnProject.getAirvwAtchFileNo() ) ); // 조감도첨부파일번호.
        eurecaParams.put("orgchrtAtchFileNo", StringUtils.defaultString( contract.getOrgchrtAtchFileNo() ) ); // 조직도첨부파일번호.

        // 20250618 공사담당자 ID 가 없는 경우 , 기본 담당자를 지정해서 전송
        String ofclId = StringUtils.defaultString(contract.getOfclId());
        if (StringUtils.isBlank(ofclId)) {
            ofclId = "prod".equals(activeProfile) ? PROD_EURECA_OFCL_ID : DEV_EURECA_OFCL_ID;
            eurecaParams.put("cnstwkOfclId", ofclId); // 공사담당자ID.
        } else {
            eurecaParams.put("cnstwkOfclId", ofclId); // 공사담당자ID.
        }


        return eurecaParams;
    }

    /**
     * 최초 계약 정보 등록 시 유레카 전송용 데이터셋 조회
     * @param contract
     * @param cnContractChange
     * @return
     */
    public Map<String, String> getContractInfoByEurecaParams(CnContract contract, CnContractChange cnContractChange) {
        Map<String, String> eurecaParams = new HashMap<>();

        // 계약정보
        eurecaParams.put("cntrctNo", contract.getCntrctNo()); // 계약번호.
        eurecaParams.put("cntrctDivCd", StringUtils.isEmpty( contract.getCntrctDivCd() ) ? "" : contract.getCntrctDivCd().substring(2, 4)); // 계약구분코드.
        eurecaParams.put("cntrctChgId", StringUtils.defaultString( cnContractChange.getCntrctChgId() ) ); // 계약변경ID.
        eurecaParams.put("cntrctChgType", StringUtils.defaultString( cnContractChange.getCntrctChgType() ) ); // 계약변경구분코드.
        eurecaParams.put("cntrctNm", contract.getCntrctNm()); // 계약명.
        eurecaParams.put("mngCntrctNo", StringUtils.defaultString( contract.getMngCntrctNo() ) ); // 관리계약번호.
        eurecaParams.put("cntrctChgDate", StringUtils.defaultString( contract.getCntrctDate() ) ); // 계약일자. 20240101
        eurecaParams.put("cntrctApprDate", StringUtils.defaultString( cnContractChange.getChgApprDate() ) ); // 계약승인일자. 20240101
//        eurecaParams.put("thisCntrctAmt", String.valueOf(contract.getThisCntrctCost())); // 금차계약금액.
//        eurecaParams.put("cntrctAmt", String.valueOf(contract.getCntrctCost())); // 총계약금액.
        eurecaParams.put("thisCcmpltDate", StringUtils.defaultString( contract.getThisCcmpltDate() ) ); // 금차준공일자. 20240101
        eurecaParams.put("ccmpltDate", StringUtils.defaultString( contract.getCcmpltDate() ) ); // 총준공일자.
        eurecaParams.put("cbgnDate", StringUtils.defaultString( contract.getCbgnDate() ) ); // 착공일자.

        String cntrctChgNo = StringUtils.defaultString(cnContractChange.getCntrctChgNo());


        eurecaParams.put("lngtmCntnuCntrctOrd",
                cnContractChange.getCntrctPhase() != null
                        ? String.valueOf(cnContractChange.getCntrctPhase())
                        : ""
        ); // 장기계약변경차수.

        // 20250618 계약변경 회차 - 1 처리하여 SET e.g. "1" -> 00 , "5" -> 04
        Integer eurecaChgNo = Integer.parseInt(cntrctChgNo) - 1;
        String paddedCntrctChgOrd = StringUtils.leftPad(eurecaChgNo.toString(), 2, "0");
        eurecaParams.put("cntrctChgOrd", paddedCntrctChgOrd); // 계약변경차수.

        return eurecaParams;
    }

    /* ==================================================================================================================
     *
     * 계약 현황 - 도급사
     *
     * ==================================================================================================================
     */

    /**
     * 도급 목록 조회
     */
    public List<ContractstatusMybatisParam.ContractcompanyOutput> getCompanyList(ContractstatusMybatisParam.ContractcompanyListInput contractcompanyListInput) {

        contractcompanyListInput.setCode(CommonCodeConstants.WORKTYPE_CODE_GROUP_CODE);
        return contractService.getCompanyList(contractcompanyListInput);

    }

    /**
     * 도급 상세 조회
     */
    public ContractstatusMybatisParam.ContractcompanyOutput getContractCompany(Long cntrctId, String cntrctNo) { // 화면 조회용
        Map<String, Object> params = new HashMap<>();
        params.put("cntrctId", cntrctId);
        params.put("cntrctNo", cntrctNo);
        params.put("code", CommonCodeConstants.WORKTYPE_CODE_GROUP_CODE);

        return contractService.getContractCompany(params);
    }

    /**
     * 계약명 조회
     */
    public String findCntrctNm(String cntrctNo) {
        return contractService.findCntrctNm(cntrctNo);
    }

    /**
     * 도급 공종코드 가져오기
     */
    public List<Map<String, ?>> getCnsttyCdList() {

        return contractService.getCnsttyCdList();

    }

    /**
     * 도급 추가
     * @param cnCompany
     * @return
     */
    @Transactional
    public CnContractCompany createCompany(CnContractCompany cnCompany,String pjtDiv,String apiYn) {
        cnCompany.setDltYn("N");
        CnContractCompany savedCompany = contractService.createCompany(cnCompany);
        if (savedCompany.getRprsYn().equals("Y")) { // 리턴 된 도급사의 rprsYn이 Y일 경우 기존 도급사들의 rprsYn -> n
            contractService.updateRprsYn(savedCompany.getCntrctNo(), savedCompany.getCntrctId());
        }

        // API 연동 - 사업이 공공(P)일 경우 CAIROS -> PGAIA
        log.info("API 연동여부: {}", apiYn);
        if("Y".equals(apiYn)) {
            if (PlatformType.CAIROS.getName().equals(platform) && "P".equals(pjtDiv)) {
                Map result = invokeCairos2Pgaia("CAGA1010", Map.of(
                        "cnCompany", savedCompany
                ));

                String resultCode = result.get("resultCode").toString();
                if (!"00".equals(resultCode)) {
                    throw new GaiaBizException(ErrorType.INTERFACE, "Cairos2Pgaia - CAGA1010");
                }
            }
        }
        return savedCompany;
    }

    /**
     * 도급 수정
     */
    @Transactional
    public CnContractCompany updateCompany(Long cntrctId,String cntrctNo,ContractCompanyForm.CompanyUpdate companyUpdate,String pjtDiv,String apiYn) {

        if (companyUpdate.getRprsYn().equals("Y")) { // 대표계약자 변경
            contractService.updateRprsYn(companyUpdate.getCntrctNo(), companyUpdate.getCntrctId());
        }
        CnContractCompany cnCompany = contractService.getByCntrctId(cntrctId, cntrctNo); // 기존 도급
        contractCompanyForm.updateContractCompany(companyUpdate, cnCompany); // 기존 도급에 수정된 내용 덮어쓰기
        cnCompany.setCntrctId(cntrctId);
        cnCompany.setCntrctNo(cntrctNo);

        CnContractCompany saveCnCompany = contractService.updateCompany(cnCompany);

        // TODO API 연동 - 사업이 공공(P)일 경우 CAIROS -> PGAIA
        log.info("API 연동여부: {}", apiYn);
        if("Y".equals(apiYn)) {
            if ( PlatformType.CAIROS.getName().equals(platform) && "P".equals(pjtDiv)) {
                Map result = invokeCairos2Pgaia("CAGA1011", Map.of(
                        "cnCompany", saveCnCompany
                ));

                String resultCode = result.get("resultCode").toString();
                if (!"00".equals(resultCode)) {
                    throw new GaiaBizException(ErrorType.INTERFACE, "Cairos2Pgaia - CAGA1011");
                }
            }
        }

        return saveCnCompany;
    }

    /**
     * 도급 삭제
     */
    @Transactional
    public void deleteCompany(List<CnContractCompany> companyList,String cntrctNo,String pjtDiv,String apiYn) {
        contractService.deleteCompany(companyList);

        // API 연동 - 사업이 공공(P)일 경우 CAIROS -> PGAIA
        log.info("API 연동여부: {}", apiYn);
        if("Y".equals(apiYn)) {
            if (PlatformType.CAIROS.getName().equals(platform) && "P".equals(pjtDiv)) {
                Map result = invokeCairos2Pgaia("CAGA1012", Map.of(
                        "companyList", companyList,
                        "usrId", UserAuth.get(true).getUsrId()
                ));

                String resultCode = result.get("resultCode").toString();
                if (!"00".equals(resultCode)) {
                    throw new GaiaBizException(ErrorType.INTERFACE, "Cairos2Pgaia - CAGA1012");
                }
            }
        }
    }

    /* ==================================================================================================================
     *
     * 계약 현황 - 계약 변경
     *
     * ==================================================================================================================
     */

    /**
     * 변경 목록 조회
     */
    public List<ContractstatusMybatisParam.ContractchangeOutputList> getContractChangeList(ContractstatusMybatisParam.ContractchangeListInput contractchangeListInput) {

        contractchangeListInput.setCode(CommonCodeConstants.CNTRCT_CHG_TYPE);
        return contractService.getContractChangeList(contractchangeListInput);

    }

    /**
     * 변경 조회
     */
    public ContractstatusMybatisParam.ContractchangeOutput getContractChangeDetail(String cntrctChgId, String cntrctNo) { // 일반 조회용
        Map<String, Object> params = new HashMap<>();
        params.put("cntrctChgId", cntrctChgId);
        params.put("cntrctNo", cntrctNo);
        params.put("majorcode", CommonCodeConstants.WORKTYPE_CODE_GROUP_CODE);
        params.put("chgcode", CommonCodeConstants.CNTRCT_CHG_TYPE);

        return contractService.getContractChangeDetail(params);
    }

    /**
     * 변경 추가화면 조회
     */
    public ContractstatusMybatisParam.ContractchangeAddOutput getContractChangeAdd(String cntrctNo, int cntrctPhase) {

        Map<String, Object> params = new HashMap<>();
        params.put("cntrctNo", cntrctNo);
        params.put("cntrctPhase", cntrctPhase);
        params.put("code", CommonCodeConstants.WORKTYPE_CODE_GROUP_CODE);
        return contractService.getContractChangeAdd(params);

    }

    /**
     * 계약변경 추가
     */
    @Transactional
    public void createChange(CnContractChange cnChange,String pjtDiv,String apiYn, String userId) {
        // 계약변경 추가
        CnContractChange saveCnChange = contractService.createChange(cnChange);

        // 20250716 (추가) 신규 계약변경 이전 계약변경 ID 조회
        String cntrctChgId = saveCnChange.getCntrctChgId();
        String prevCntrctChgId = "";
        int idx = cntrctChgId.lastIndexOf(".V");
        if (idx >= 0) {
            String prefix = cntrctChgId.substring(0, idx + 2);             // e.g. "P202507002.A2.V"
            String numStr = cntrctChgId.substring(idx + 2);    // e.g. "05"
            int num = Integer.parseInt(numStr);
            String prevNum = String.format("%02d", num - 1); // 두 자리 문자열
            prevCntrctChgId = prefix + prevNum;
        }

        // 초기화 정보 변수 vo
        Map<String, Object> initVO = new HashMap<>();
        initVO.put("PREV_CNTRCT_CHG_ID", prevCntrctChgId);
        initVO.put("CNTRCT_CHG_ID", saveCnChange.getCntrctChgId());
        initVO.put("CNTRCT_NO", saveCnChange.getCntrctNo());
        initVO.put("REVISION_ID", revisionService.getRevisionId(Map.of("CNTRCT_CHG_ID", prevCntrctChgId)));
        initVO.put("USR_ID", userId);

        /* 20250716 (추가) 계약변경이 생겼을때 - 원가, 공정 관련 테이블이 복사되어 추가되어야 한다. */

        // 공종 데이터 존재여부 확인
        boolean isExistsCostData = c3RService.checkCostDataExists(initVO);
        initVO.put("isExistsCostData", isExistsCostData);

        if (isExistsCostData) {
            c3RService.copyCostDataFromContract(initVO);      // 신규 계약변경 ID 기준 복사
            // 검토필요.
//            c3RService.initCostAndProcessFromContract(vo);      // 기존 계약변경 ID 기준 미사용 업데이트(DLT_YN = 'Y')
        }

        // 공정 데이터 존재여부 확인
        boolean isExistsProcessData = c3RService.checkProcessDataExists(initVO);
        initVO.put("isExistsProcessData", isExistsProcessData);
        if (isExistsProcessData) { c3RService.copyProcessDataFromContract(initVO); }


        log.info("API 연동여부: {}", apiYn);
        if("Y".equals(apiYn)) {
            // API 연동 - 사업이 공공(P)일 경우 CAIROS -> PGAIA
            Log.SmApiLogDto smApiLog1 = null;
            try {
                if (PlatformType.CAIROS.getName().equals(platform) && "P".equals(pjtDiv)) {
                    /* LOG 작성 (CAIROS -> PGAIA)  */
                    smApiLog1 = new Log.SmApiLogDto();
                    smApiLog1.setApiId("CAGA1013");
                    smApiLog1.setApiType("OUT");
                    smApiLog1.setServiceType("createChange");
                    smApiLog1.setServiceUuid(UUID.randomUUID().toString());
                    smApiLog1.setSourceSystemCode("CAIROS");
                    smApiLog1.setTargetSystemCode("PGAIA");
                    smApiLog1.setReqMethod("POST");
                    smApiLog1.setReqData("계약변경추가");
                    smApiLog1.setReqDt(FormatUtil.formatDate(new Date(), "yyyyMMddHHmmss"));
                    smApiLog1.setRgstrId(userId);
                    smApiLog1.setChgId(userId);
                    smApiLog1.setResultCode(200);
                    smApiLog1.setErrorYn("N");

                    Map result = invokeCairos2Pgaia("CAGA1013", Map.of(
                            "cnChange", saveCnChange,
                            "initVO", initVO
                    ));

                    String resultCode = result.get("resultCode").toString();
                    if (!"00".equals(resultCode)) {
                        throw new GaiaBizException(ErrorType.INTERFACE, "Cairos2Pgaia - CAGA1013");
                    }
                }
            } catch (GaiaBizException e) {
                log.error("createChange - Cairos2Pgaia API 연동 실패: {}", e.getMessage(), e);
                if (smApiLog1 != null) {
                    smApiLog1.setResultCode(500);
                    smApiLog1.setErrorYn("Y");
                    smApiLog1.setErrorReason(e.getMessage());
                    smApiLog1.setResDt(FormatUtil.formatDate(new Date(), "yyyyMMddHHmmss"));
                }
            } catch (Exception e) {
                log.error("createChange - Cairos2Pgaia API 연동 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
                if (smApiLog1 != null) {
                    smApiLog1.setResultCode(500);
                    smApiLog1.setErrorYn("Y");
                    smApiLog1.setErrorReason(e.getMessage());
                    smApiLog1.setResDt(FormatUtil.formatDate(new Date(), "yyyyMMddHHmmss"));
                }
            } finally {
                if (smApiLog1 != null) {
                    systemLogComponent.asyncAddApiLog(smApiLog1);
                }
            }

            try {
                if (PlatformType.CAIROS.getName().equals(platform)) {

                    // 계약정보 유레카 전송
                    CnContract cnContract = contractService.getByCntrctNo(saveCnChange.getCntrctNo());
                    Map<String, String> eurecaParams = this.getContractInfoByEurecaParams(cnContract, saveCnChange); //계약정보 조회


                    boolean isLongTermContractTotal = "0103".equals(cnContract.getCntrctDivCd());                       // 계약종류: 장기계속계약_총체
                    boolean isLongTermContractPhase = "0104".equals(cnContract.getCntrctDivCd());                       // 계약종류: 장기계속계약_차수별
                    boolean isSecondOrAbovePhase = EtcUtil.zeroConvertInt(saveCnChange.getCntrctPhase()) > 1;           // 장기계속 계약 차수가 큰지
                    if (isLongTermContractTotal) {
                        // 계약구분-장기계속계약_총체 인 경우 -> 계약구분-장기계속계약_1차 로 하여 전송
                        eurecaParams.put("cntrctDivCd", "04");
                        eurecaParams.put("lngtmCntnuCntrctOrd", "1");
                    } else if (isLongTermContractPhase && isSecondOrAbovePhase) {
                        // 1. 계약구분-장기계속계약_차수별 이고, 2. 장기차수가 2 이상인경우 -> 계약 횟수를 가공처리 set
                        String eurecaContractChangeNo = contractService.convertToEurecaContractChangeNo(Map.of("cntrctNo", saveCnChange.getCntrctNo()) );
                        eurecaParams.put("cntrctChgOrd", eurecaContractChangeNo);
                    }

                    Map<String, Object> eurecaResult = eurecaClient.registerCntrct(eurecaParams, userId);

                    if ( !"00".equals( MapUtils.getString(eurecaResult, "resultCode") ) ) {
                        throw new GaiaBizException(ErrorType.INTERFACE, "유레카 연동실패");
                    }
                }
            } catch (GaiaBizException e) {
                log.error("createChange - Eureca API 연동 실패: {}", e.getMessage(), e);
            } catch (Exception e) {
                log.error("createChange - Eureca API 연동 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            }
        }
    }

    /**
     * 계약변경 수정
     */
    @Transactional
    public CnContractChange updateChange(String cntrctChgId, String cntrctNo, ContractChangeForm.ChangeUpdate changeUpdate,String pjtDiv,String apiYn, String userId) {

        CnContractChange cnChange = contractService.getBycntrctChgId(cntrctChgId, cntrctNo); // 기존 변경

        int changeLast = 0;
        if (!cnChange.getLastChgYn().equals(changeUpdate.getLastChgYn())) { // 최종변경 수정 유무 판단
            changeLast = 1;
        }

        changeUpdate.setCntrctChgNo(changeUpdate.getCntrctChgNo().replace("회", "").trim());
        contractChangeForm.updateContractChange(changeUpdate, cnChange); // 기존 변경에 수정된 내용 덮어쓰기
        cnChange.setCntrctNo(cntrctNo);

        CnContractChange saveCnChange = contractService.updateChange(cnChange, changeLast);

        log.info("API 연동여부: {}", apiYn);
        if("Y".equals(apiYn)) {
            // API 연동 1 - 사업이 공공(P)일 경우 CAIROS -> PGAIA
            Log.SmApiLogDto smApiLog1 = null;
            try {
                if (PlatformType.CAIROS.getName().equals(platform) && "P".equals(pjtDiv)) {
                    /* LOG 작성 (CAIROS -> PGAIA)  */
                    smApiLog1 = new Log.SmApiLogDto();
                    smApiLog1.setApiId("CAGA1014");
                    smApiLog1.setApiType("OUT");
                    smApiLog1.setServiceType("updateChange");
                    smApiLog1.setServiceUuid(UUID.randomUUID().toString());
                    smApiLog1.setSourceSystemCode("CAIROS");
                    smApiLog1.setTargetSystemCode("PGAIA");
                    smApiLog1.setReqMethod("POST");
                    smApiLog1.setReqData("계약변경수정");
                    smApiLog1.setReqDt(FormatUtil.formatDate(new Date(), "yyyyMMddHHmmss"));
                    smApiLog1.setRgstrId(userId);
                    smApiLog1.setChgId(userId);
                    smApiLog1.setResultCode(200);
                    smApiLog1.setErrorYn("N");

                    Map result = invokeCairos2Pgaia("CAGA1014", Map.of(
                            "cnChange", saveCnChange,
                            "changeLast", changeLast
                    ));

                    String resultCode = result.get("resultCode").toString();
                    if (!"00".equals(resultCode)) {
                        throw new GaiaBizException(ErrorType.INTERFACE, "Cairos2Pgaia - CAGA1014");
                    }
                }
            } catch (GaiaBizException e) {
                log.error("updateChange - Cairos2Pgaia API 연동 실패: {}", e.getMessage(), e);
                if (smApiLog1 != null) {
                    smApiLog1.setResultCode(500);
                    smApiLog1.setErrorYn("Y");
                    smApiLog1.setErrorReason(e.getMessage());
                    smApiLog1.setResDt(FormatUtil.formatDate(new Date(), "yyyyMMddHHmmss"));
                }
            } catch (Exception e) {
                log.error("updateChange - Cairos2Pgaia API 연동 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
                if (smApiLog1 != null) {
                    smApiLog1.setResultCode(500);
                    smApiLog1.setErrorYn("Y");
                    smApiLog1.setErrorReason(e.getMessage());
                    smApiLog1.setResDt(FormatUtil.formatDate(new Date(), "yyyyMMddHHmmss"));
                }
            } finally {
                if (smApiLog1 != null) {
                    systemLogComponent.asyncAddApiLog(smApiLog1);
                }
            }

            /**
             * 계약구분이 "장기계속계약_차수별" 이 아니고,
             * 최초 변경계약에 대한 수정일 경우 전송하면 안된다. (유레카에 정보가 없음)
             */
            boolean eurecaApiCondition = true;
            CnContract cnContract = contractService.getByCntrctNo(saveCnChange.getCntrctNo());
            if ("1".equals(saveCnChange.getCntrctChgNo()) && !"0104".equals(cnContract.getCntrctDivCd())) { eurecaApiCondition = false; }

            // API 연동 2 - CAIROS -> EURECA
            try {
                if (PlatformType.CAIROS.getName().equals(platform) && eurecaApiCondition) {

                    // 계약정보 유레카 전송
                    Map<String, String> eurecaParams = this.getContractInfoByEurecaParams(cnContract, saveCnChange); //계약정보 조회

                    boolean isLongTermContractTotal = "0103".equals(cnContract.getCntrctDivCd());                       // 계약종류: 장기계속계약_총체
                    boolean isLongTermContractPhase = "0104".equals(cnContract.getCntrctDivCd());                       // 계약종류: 장기계속계약_차수별
                    boolean isSecondOrAbovePhase = EtcUtil.zeroConvertInt(saveCnChange.getCntrctPhase()) > 1;           // 장기계속 계약 차수가 큰지
                    if (isLongTermContractTotal) {
                        // 계약구분-장기계속계약_총체 인 경우 -> 계약구분-장기계속계약_1차 로 하여 전송
                        eurecaParams.put("cntrctDivCd", "04");
                        eurecaParams.put("lngtmCntnuCntrctOrd", "1");
                    } else if (isLongTermContractPhase && isSecondOrAbovePhase) {
                        // 1. 계약구분-장기계속계약_차수별 이고, 2. 장기차수가 2 이상인경우 -> 계약 횟수를 가공처리 set
                        String eurecaContractChangeNo = contractService.convertToEurecaContractChangeNo(Map.of("cntrctNo", saveCnChange.getCntrctNo()) );
                        eurecaParams.put("cntrctChgOrd", eurecaContractChangeNo);
                    }

                    Map<String, Object> eurecaResult = eurecaClient.registerCntrct(eurecaParams, userId);

                    if ( !"00".equals( MapUtils.getString(eurecaResult, "resultCode") ) ) {
                        throw new GaiaBizException(ErrorType.INTERFACE, "유레카 연동실패");
                    }
                }
            } catch (GaiaBizException e) {
                log.error("updateChange - Eureca API 연동 실패: {}", e.getMessage(), e);
            } catch (Exception e) {
                log.error("updateChange - Eureca API 연동 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
            }
        }



        return saveCnChange;
    }

    /**
     * 계약변경 삭제
     */
    @Transactional
    public void deleteChange(List<CnContractChange> changeList,String pjtDiv,String apiYn) {
        contractService.deleteChange(changeList);

        // API 연동 - 사업이 공공(P)일 경우 CAIROS -> PGAIA
        log.info("API 연동여부: {}", apiYn);
        if("Y".equals(apiYn)) {
            if (PlatformType.CAIROS.getName().equals(platform) && "P".equals(pjtDiv)) {
                Map result = invokeCairos2Pgaia("CAGA1015", Map.of(
                        "changeList", changeList,
                        "usrId", UserAuth.get(true).getUsrId()
                ));

                String resultCode = result.get("resultCode").toString();
                if (!"00".equals(resultCode)) {
                    throw new GaiaBizException(ErrorType.INTERFACE, "Cairos2Pgaia - CAGA1015");
                }
            }
        }
    }

    /**
     * Eureca 및 PCCS 연계로 인한 Deprecated 처리
     * 계약내역서 목록조회
     * 변경: 2024-11-13 계약내역서 등록에 필요한 {type} parameter 추가
     */
    @Deprecated
    public List<Map<String, ?>> getContractBidList(String cntrctNo, String type) {
        Map<String, Object> params = new HashMap<>();
        params.put("cntrctNo", cntrctNo);
        params.put("type", type); // 2024-11-13 추가

        return contractService.getContractBidList(params);
    }

    /**
     * Eureca 및 PCCS 연계로 인한 Deprecated 처리
     * 계약 내역서 직접비 합계금액
     */
    @Deprecated
    public Map<String, ?> getContractBidCost(String cntrctNo) {
        Map<String, Object> params = new HashMap<>();
        params.put("cntrctNo", cntrctNo);
        return contractService.getContractBidCost(params);
    }

    /**
     * Eureca 및 PCCS 연계로 인한 Deprecated 처리
     * 계약내역서 검색
     */
    @Deprecated
    public List<Map<String, ?>> getContractBidSearch(String cntrctNo, String searchValue, String type) {
        Map<String, Object> params = new HashMap<>();
        params.put("cntrctNo", cntrctNo);
        params.put("searchValue", searchValue);
        params.put("type", type);
        return contractService.getContractBidSearch(params);
    }

    /**
     * 원가계산서 목록조회
     */
    public List<Map<String, ?>> getCalculatorList(String cntrctNo) {
        return contractService.getCalculatorList(cntrctNo);
    }

    /**
     * Eureca 및 PCCS 연계로 인한 Deprecated 처리
     * 계약내역서 등록. bid 파일로 계약내역서를 등록한다.
     */
    @Deprecated
    @Transactional(rollbackFor = Exception.class)
    public boolean registCtrDtlstt(MultipartFile bidFile, String cntrctNo, String type) {

        // FILE 확장자명 검사
        String originalFileName = bidFile.getOriginalFilename();
        String fileExt = null;

        // 20250227 - 정적검사 수정 [Dodgy] NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE
        if (originalFileName != null) {
            fileExt = org.springframework.util.StringUtils.getFilenameExtension(originalFileName.toLowerCase());

            if (fileExt == null) {
                // msg.026 - 허용되지 않은 파일 형식입니다.
                String message = messageSource.getMessage("msg.026", null, LocaleContextHolder.getLocale());
                throw new GaiaBizException(ErrorType.INTERNAL_SERVER_ERROR, message);
            } else if (!fileExt.equals("bid")) {
                // msg.026 - 허용되지 않은 파일 형식입니다.
                String message = messageSource.getMessage("msg.026", null, LocaleContextHolder.getLocale());
                throw new GaiaBizException(ErrorType.INTERNAL_SERVER_ERROR, message);
            }
        }

        boolean result = true;
        File _bidFile = null;

        try {
            Path _bidFilePath = Files.createTempFile(null, bidFile.getOriginalFilename());
            _bidFile = _bidFilePath.toFile();

            // MultipartFile -> File 변환
            bidFile.transferTo(_bidFile);

            BaseUtil.checkNotEmpty(_bidFile, "No bidFileMap");
            String bidFileName = (String) bidFile.getName();
            BaseUtil.checkNotEmpty(bidFileName, "No bidFileName");
            BaseUtil.checkTrue(_bidFile.exists() && _bidFile.isFile(), "No bidFile");

            contractService.insertCtrDtlstt(cntrctNo, _bidFile, type);
        } catch (XMLStreamException e) {
            result = false;
            throw new RuntimeException(e);
        } catch (IOException e) {
            result = false;
            throw new RuntimeException(e);
        } finally {
            FileUtils.deleteQuietly(_bidFile);
        }

        // 합계금액조회 리스트 필요 ?
        // List<Map<String, Object>> amtList =
        // mybatisSession.selectList("kr.co.codefarm.svcm.cmis.gaia.ctrdtlsttreg.getAmtList",
        // paramMap); //공사개요 계약금액, 계약내역서 합계금액 비교
        // return BaseUtil.map("ok", true, "amtList", amtList);

        return result;
    }



    // ----------------------------------------API통신--------------------------------------------

    /**
     * API 수신 처리 메서드
     *
     * CAGA1007=계약추가
     * CAGA1008=계약수정
     * CAGA1009=계약삭제
     * CAGA1010=도급사추가
     * CAGA1011=도급사수정
     * CAGA1012=도급사삭제
     * CAGA1013=계약변경추가
     * CAGA1014=계약변경수정
     * CAGA1015=계약변경삭제
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
            if ("CAGA1007".equals(transactionId)) {             // 계약 추가
                CnContract cnContract = objectMapper.convertValue(params.get("cnContract"), CnContract.class);
                ContractInput input = objectMapper.convertValue(params.get("input"), ContractInput.class);
                CnContractCompany defaultCompany = objectMapper.convertValue(params.get("defaultCompany"), CnContractCompany.class);
                List<ApDraftForm> apDraftForm = objectMapper.convertValue(params.get("apDraftForm"), new TypeReference<List<ApDraftForm>>() {});

                // 1. 계약 추가 및 프로시져 실행
                // 20250822 PGAIA-GAIA 연동 사업생성시 부서 생성 임시 주석처리 jhkim
                // projectInitializer.addProject(input.getPCorpno(), input.getPPjttype(), input.getPPjtno(), input.getPCntrctno(), input.getPItemname(), input.getPItemdesc());
                cnContract = contractService.saveContract(cnContract);
                // 2. 최초 도급 등록
                contractService.createCompany(defaultCompany);
                // 3. 계약변경 1회차 등록
                contractService.createDefaultChange(cnContract);
                // 4. 전자결재 기본 서식 생성
                draftService.createApForm(apDraftForm);

            } else if ("CAGA1008".equals(transactionId)) {      // 계약 수정
                CnContract cnContract = objectMapper.convertValue(params.get("cnContract"), CnContract.class);
                CnContractChange firstChange = objectMapper.convertValue(params.get("firstChange"), CnContractChange.class);
                ContractInput input = objectMapper.convertValue(params.get("input"), ContractInput.class);
                CnContractCompany rprsCompany = objectMapper.convertValue(params.get("rprsCompany"), CnContractCompany.class);

                // 1. 최초 도급 수정
                contractService.updateCompany(rprsCompany);
                // 2. 계약변경 1회차 수정
                contractService.updateChange(firstChange, 0);
                // 3. 계약 수정
                // 20250822 PGAIA-GAIA 연동 사업생성시 부서 생성 임시 주석처리 jhkim
                // projectInitializer.modifyProject(input.getPCorpno(), input.getPPjttype(), input.getPPjtno(), input.getPCntrctno(), input.getPItemname(), input.getPItemdesc());
                contractService.saveContract(cnContract);

            } else if ("CAGA1009".equals(transactionId)) {      // 계약 삭제
                ContractstatusForm.ContractList contractList = objectMapper.convertValue(params.get("contractList"), ContractstatusForm.ContractList.class);
                String usrId = (String) params.get("usrId");

                for (int i = 0; i < contractList.getContractList().size(); i++) {
                    CnContract contract = contractRepository.findById(contractList.getContractList().get(i)).orElse(null);
                    String pInserttype = "DEL";
                    String pPjttype = "C";
                    String pPjtno = contract.getPjtNo();
                    String pCntrctno = contract.getCntrctNo();
                    String pItemname = contract.getCntrctNm();
                    String pItemdesc = (contract.getCntrctNm() != null) ? contract.getCntrctNm() : "0";
                    String pCorpno = (contract.getCorpNo() != null) ? contract.getCorpNo() : "0";

                    ContractInput input = new ContractInput();
                    input.setPCntrctno(pCntrctno);
                    input.setPCorpno(pCorpno);
                    input.setPInserttype(pInserttype);
                    input.setPItemdesc(pItemdesc);
                    input.setPItemname(pItemname);
                    input.setPPjtno(pPjtno);
                    input.setPPjttype(pPjttype);

                    // 20250822 PGAIA-GAIA 연동 사업생성시 부서 생성 임시 주석처리 jhkim
                    // projectInitializer.removeProject(input.getPPjttype(), input.getPPjtno(), input.getPCntrctno());
                    MybatisInput cInput = new MybatisInput().add("cntrctNo", contract.getCntrctNo()).add("usrId",usrId);
                    int cResult = contractService.deleteContract(cInput);
                    if(cResult  > 0){
                        contractService.deleteAllCompany(cInput);
                        contractService.deleteAllChange(cInput);
                        subcontractService.deleteAllSubContract(cInput);
                        subcontractService.deleteAllSubChange(cInput);
                        revisionService.deleteAllRevision(cInput);
                    }
                    draftService.deleteApForm(contract.getPjtNo(), contract.getCntrctNo(), usrId);
                }

            } else if ("CAGA1010".equals(transactionId)) {      // 도급사 추가
                CnContractCompany cnCompany = objectMapper.convertValue(params.get("cnCompany"), CnContractCompany.class);
                CnContractCompany savedCompany = contractService.createCompany(cnCompany);

                // 리턴 된 도급사의 rprsYn이 Y일 경우 기존 도급사들의 rprsYn -> n
                if (savedCompany.getRprsYn().equals("Y")) {
                    contractService.updateRprsYn(savedCompany.getCntrctNo(), savedCompany.getCntrctId());
                }

            } else if ("CAGA1011".equals(transactionId)) {      // 도급사 수정

                CnContractCompany cnCompany = objectMapper.convertValue(params.get("cnCompany"), CnContractCompany.class);
                if (cnCompany.getRprsYn().equals("Y")) { // 대표계약자 변경
                    contractService.updateRprsYn(cnCompany.getCntrctNo(), cnCompany.getCntrctId());
                }
                contractService.updateCompany(cnCompany);

            } else if ("CAGA1012".equals(transactionId)) {      // 도급사 삭제
                List<CnContractCompany> companyList = objectMapper.convertValue(params.get("companyList"),new TypeReference<List<CnContractCompany>>() {});

                for (int i = 0; i < companyList.size(); i++) {
                    CnContractCompany company = companyRepository.findByCntrctIdAndCntrctNo(companyList.get(i).getCntrctId(),
                            companyList.get(i).getCntrctNo());
                    String usrId = (String) params.get("usrId");

                    company.setDltYn("Y");
                    company.setDltDt(LocalDateTime.now());
                    company.setDltId(usrId);

                    contractService.updateCompany(company);
                }

            } else if ("CAGA1013".equals(transactionId)) {      // 계약변경 추가

                CnContractChange cnChange = objectMapper.convertValue(params.get("cnChange"), CnContractChange.class);
                Map<String, Object> initVO = objectMapper.convertValue(params.get("initVO"), Map.class);

                contractService.createChange(cnChange);

                /* 20250716 (추가) 계약변경이 생겼을때 - 원가, 공정 관련 테이블이 복사되어 추가되어야 한다. */
                boolean isExistsCostData = (boolean) initVO.get("isExistsCostData");
                if (isExistsCostData) { c3RService.copyCostDataFromContract(initVO); }

                boolean isExistsProcessData = (boolean) initVO.get("isExistsProcessData");
                if (isExistsProcessData) { c3RService.copyProcessDataFromContract(initVO); }

            } else if ("CAGA1014".equals(transactionId)) {      // 계약변경 수정
                CnContractChange cnChange = objectMapper.convertValue(params.get("cnChange"), CnContractChange.class);
                int changeLast = (int) params.get("changeLast");

                contractService.updateChange(cnChange, changeLast);

            } else if ("CAGA1015".equals(transactionId)) { // 계약변경 삭제
                List<CnContractChange> changeList = objectMapper.convertValue(params.get("changeList"),new TypeReference<List<CnContractChange>>() {});
                String usrId = (String) params.get("usrId");

                for (int i = 0; i < changeList.size(); i++) {
                    CnContractChange change = changeRepository.findByCntrctChgId(changeList.get(i).getCntrctChgId());

                    change.setDltYn("Y");
                    change.setDltDt(LocalDateTime.now());
                    change.setDltId(usrId);

                    contractService.updateChange(change, 0);
                }
            }

        }  catch (GaiaBizException ex) {
            result.put("resultCode", "01");
            result.put("resultMsg", ex.getMessage());
        }

        return result;
    }

}
