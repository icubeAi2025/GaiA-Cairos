package kr.co.ideait.platform.gaiacairos.comp.system;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import jakarta.validation.Valid;
import kr.co.ideait.iframework.MaskingUtil;
import kr.co.ideait.platform.gaiacairos.comp.project.helper.ProjectInitializer;
import kr.co.ideait.platform.gaiacairos.comp.project.service.InformationService;
import kr.co.ideait.platform.gaiacairos.comp.system.service.AuthorityGroupService;
import kr.co.ideait.platform.gaiacairos.comp.system.service.CommonCodeService;
import kr.co.ideait.platform.gaiacairos.comp.system.service.DepartmentService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractComponent;
import kr.co.ideait.platform.gaiacairos.core.constant.CommonCodeConstants;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnProject;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmDepartment;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmOrganization;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.CommonReqVo;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.commoncode.CommonCodeDto;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.department.DepartmentDto;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.department.DepartmentForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.department.DepartmentMybatisParam;
import kr.co.ideait.platform.gaiacairos.core.type.PlatformType;
import kr.co.ideait.platform.gaiacairos.core.util.StringHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Stream;

@Slf4j
@Component
public class DepartmentComponent extends AbstractComponent {
    @Autowired
    private DepartmentService departmentService;

    @Autowired
    CommonCodeService commonCodeService;

    @Autowired
    DepartmentForm departmentForm;

    @Autowired
    DepartmentDto departmentDto;

    @Autowired
    CommonCodeDto commonCodeDto;

    @Autowired
    InformationService informationService;

    @Autowired
    ProjectInitializer projectInitializer;

    @Autowired
    AuthorityGroupService authorityGroupService;

    public HashMap<String,Object> getDepartmentDetailData(Integer deptNo, String langInfo){
        HashMap<String,Object> result = new HashMap<>();
        SmDepartment smDepartment = departmentService.getDepartment(deptNo);
        result.put("department",smDepartment.map(departmentDto::toDepartment));
        List<DepartmentDto.DepartmentEmploee> emploeeList = departmentService.getDepartmentEmploeeListByDeptNo(deptNo, langInfo).stream().map(departmentDto::toDepartmentEmploee).toList();

        for(DepartmentDto.DepartmentEmploee emploee : emploeeList){
            String maskedEmailAdrs = MaskingUtil.maskEmail(StringHelper.decodeSafeText((String)emploee.get("emailAdrs")));
            emploee.put("email_adrs",maskedEmailAdrs);
            String maskedLoginAdrs = MaskingUtil.maskEmail(StringHelper.decodeSafeText((String)emploee.get("loginId")));
            emploee.put("login_id",maskedLoginAdrs);
            String maskedUsrNm = MaskingUtil.maskName(StringHelper.decodeSafeText((String)emploee.get("usrNm")));
            emploee.put("usr_nm",maskedUsrNm);
            String maskedPhoneNo = MaskingUtil.maskPhoneNumber((String)emploee.get("phoneNo"));
            emploee.put("phone_no",maskedPhoneNo);
        }

        result.put("emploeeList", emploeeList);

        return result;
    }

    public Stream<DepartmentDto.DepartmentEmploee> getDepartmentEmploeeListByDeptNo(Integer deptNo, String langInfo, DepartmentForm.DepartmentGet pjtData){
        DepartmentMybatisParam.GetEmployeeListInput input = new DepartmentMybatisParam.GetEmployeeListInput();
        input.setDepartmentNo(deptNo);
        input.setColumnNm(pjtData.getColumnNm());
        input.setText(pjtData.getText());
        input.setLang(langInfo);

        return departmentService.getDepartmentEmploeeListByDeptNo(input).stream().map(departmentDto::toDepartmentEmploee);
    }

    public boolean isExistDepartment(String departmentId){
        return departmentService.existDepartment(departmentId);
    }

    @Transactional
    public String createDepartment(DepartmentForm.DepartmentCreate departmentCreate, String pjtDivDel, String apiYn){
        String userId = UserAuth.get(true).getUsrId();

        departmentCreate.setDeptUuid(UUID.randomUUID().toString());

        projectInitializer.addDepartment(departmentCreate, userId);

        boolean isAdminDepartment = "ADMIN".equals(departmentCreate.getPjtNo());
        CnProject cnProject = null;
        String pjtDiv = "";

        if(!isAdminDepartment){
            // pjtDiv 설정
            cnProject = informationService.getProject(departmentCreate.getPjtNo());
            pjtDiv = cnProject.getPjtDiv();
        }

        if("Y".equals(apiYn)){
            if(PlatformType.PGAIA.getName().equals(platform)){
                HashMap<String,Object> apiParams = new HashMap<>();
                apiParams.put("smDepartment",departmentCreate);
                apiParams.put("userId",userId);

                Map<String,Object> apiResult = invokePgaia2Cairos("GACAM07020101",apiParams);
                String resultCode = (String)apiResult.get("resultCode");

                if(!"00".equals(resultCode)){
                    throw new GaiaBizException(ErrorType.INTERFACE,(String)apiResult.get("resultMsg"));
                }
            }
            else if("P".equals(pjtDiv)){
                HashMap<String,Object> apiParams = new HashMap<>();
                apiParams.put("smDepartment",departmentCreate);
                apiParams.put("userId",userId);

                Map<String,Object> apiResult = invokeCairos2Pgaia("CAGAM07020101",apiParams);
                String resultCode = (String)apiResult.get("resultCode");

                if(!"00".equals(resultCode)){
                    throw new GaiaBizException(ErrorType.INTERFACE,(String)apiResult.get("resultMsg"));
                }
            }
        }
        return "success";
    }

    @Transactional
    public SmDepartment modifyDepartment(DepartmentForm.@Valid DepartmentUpdate department, String pjtDivDel, String apiYn) {
        String userId = UserAuth.get(true).getUsrId();
        SmDepartment smDepartment = departmentService.getDepartment(department.getDeptNo());
        if (smDepartment == null) {
            throw new GaiaBizException(ErrorType.NO_DATA,"조회된 부서 데이터 없음");
        }

        departmentForm.updateSmDepartment(department, smDepartment);
        smDepartment.setChgId(userId);

        boolean isChangeSvrType = department.getSvrType() != null && department.getSvrType().equals(smDepartment.getSvrType());

        boolean isAdminDepartment = "ADMIN".equals(smDepartment.getPjtNo());
        CnProject cnProject = null;
        String pjtDiv = "";

        if(!isAdminDepartment){
            // pjtDiv 설정
            cnProject = informationService.getProject(smDepartment.getPjtNo());
            pjtDiv = cnProject.getPjtDiv();
        }

        int serviceResult = departmentService.updateDepartment(smDepartment, isChangeSvrType);
        if(serviceResult == 1){
            if("Y".equals(apiYn)){
                if(PlatformType.PGAIA.getName().equals(platform)){
                    HashMap<String,Object> apiParams = new HashMap<>();
                    apiParams.put("smDepartment",smDepartment);
                    apiParams.put("isChangeSvrType",isChangeSvrType);

                    Map<String,Object> apiResult = invokePgaia2Cairos("GACAM07020102",apiParams);
                    String resultCode = (String)apiResult.get("resultCode");

                    if(!"00".equals(resultCode)){
                        throw new GaiaBizException(ErrorType.INTERFACE,(String)apiResult.get("resultMsg"));
                    }
                }
                else if("P".equals(pjtDiv)){
                    HashMap<String,Object> apiParams = new HashMap<>();
                    apiParams.put("smDepartment",smDepartment);
                    apiParams.put("isChangeSvrType",isChangeSvrType);

                    Map<String,Object> apiResult = invokeCairos2Pgaia("CAGAM07020102",apiParams);
                    String resultCode = (String)apiResult.get("resultCode");

                    if(!"00".equals(resultCode)){
                        throw new GaiaBizException(ErrorType.INTERFACE,(String)apiResult.get("resultMsg"));
                    }
                }
            }
            return smDepartment;
        }
        return null;
    }
    @Transactional
    public boolean removeDepartment(DepartmentForm.DepartmentDelete department, String pjtDivDel, String apiYn) {
        String userId = UserAuth.get(true).getUsrId();
        List<Integer> deptNoList = departmentService.deleteDepartment(department.getDeptIdList(),userId);
        // 해당 부서가 속한 권한 그룹의 사용자(부서) 정보 삭제
        authorityGroupService.deleteAuthorityGroupUsersByAuthNoAndRghtGrpUsrTy(deptNoList, "D");

        boolean isAdminDepartment = "ADMIN".equals(department.getPjtNo());
        CnProject cnProject = null;
        String pjtDiv = "";

        if(!isAdminDepartment){
            // pjtDiv 설정
            cnProject = informationService.getProject(department.getPjtNo());
            pjtDiv = cnProject.getPjtDiv();
        }

        if("Y".equals(apiYn)){
            if(PlatformType.PGAIA.getName().equals(platform)){
                HashMap<String,Object> apiParams = new HashMap<>();
                apiParams.put("department",department);
                apiParams.put("userId", userId);

                Map<String,Object> apiResult = invokePgaia2Cairos("GACAM07020103",apiParams);
                String resultCode = (String)apiResult.get("resultCode");

                if(!"00".equals(resultCode)){
                    throw new GaiaBizException(ErrorType.INTERFACE,(String)apiResult.get("resultMsg"));
                }
            }
            else if("P".equals(pjtDiv)){
                HashMap<String,Object> apiParams = new HashMap<>();
                apiParams.put("department",department);
                apiParams.put("userId", userId);

                Map<String,Object> apiResult = invokeCairos2Pgaia("CAGAM07020103",apiParams);
                String resultCode = (String)apiResult.get("resultCode");

                if(!"00".equals(resultCode)){
                    throw new GaiaBizException(ErrorType.INTERFACE,(String)apiResult.get("resultMsg"));
                }
            }
        }
        return true;
    }

    public HashMap<String,Object> getDepartmentUserListData(String langInfo, DepartmentForm.DepartmentGet pjtData, String isAdmin){
        HashMap<String,Object> result = new HashMap<>();

        if(pjtData.getCntrctNo()==null||pjtData.getPjtNo()==null||(pjtData.getDeptId()==null)){
            throw new GaiaBizException(ErrorType.NO_DATA, "No Parameter.");
        }

        DepartmentMybatisParam.GetOranizationEmployeeListInput input = new DepartmentMybatisParam.GetOranizationEmployeeListInput();
        input.setDeptId(pjtData.getDeptId());
        input.setLang(langInfo);
        input.setCntrctNo(pjtData.getCntrctNo());
        input.setPjtNo(pjtData.getPjtNo());

        //직원 검색 조회 파라미터 세팅
        if(pjtData.getColumnNm() != null && pjtData.getText() != null){
            input.setColumnNm(pjtData.getColumnNm());
            input.setText(pjtData.getText());
        }

        List<Map<String, Object>> employeeList = departmentService.getOrganizationEmployeeList(isAdmin, input);

        //직책, 직급, 근무상태 옵션 데이터 조회
        List<String> cmnCdList = new ArrayList<>();
        cmnCdList.add(CommonCodeConstants.PSTN_CODE_GROUP_CODE);
        cmnCdList.add(CommonCodeConstants.RANK_CODE_GROUP_CODE);
        cmnCdList.add(CommonCodeConstants.FLAG_CODE_GROUP_CODE);
        cmnCdList.add(CommonCodeConstants.COMP_GRP_CODE_GROUP_CODE);

        Map<String, Stream<CommonCodeDto.CommonCodeCombo>> optionDataMap = new HashMap<>();
        cmnCdList.forEach(code -> {
            String comCodeNm = ""; //맵의 키 값으로 설정.

            if (code.equals(CommonCodeConstants.PSTN_CODE_GROUP_CODE)) {
                comCodeNm = "pstn";
            } else if (code.equals(CommonCodeConstants.RANK_CODE_GROUP_CODE)) {
                comCodeNm = "ratng";
            } else if(code.equals(CommonCodeConstants.FLAG_CODE_GROUP_CODE)) {
                comCodeNm = "flag";
            } else if(code.equals(CommonCodeConstants.COMP_GRP_CODE_GROUP_CODE)) {
                comCodeNm = "compGrp";
            }

            Stream<CommonCodeDto.CommonCodeCombo> data = commonCodeService.getCommonCodeListByGroupCode(code).stream()
                    .map(smComCode -> {
                        CommonCodeDto.CommonCodeCombo codeCombo = commonCodeDto.fromSmComCodeToCombo(smComCode);
                        codeCombo.setCmnCdNm(langInfo.equals("en") ? smComCode.getCmnCdNmEng() : smComCode.getCmnCdNmKrn());
                        return codeCombo;
                    });
            optionDataMap.put(comCodeNm, data);
        });

        result.put("employeeList", employeeList);
        result.put("optionDataMap", optionDataMap);
        return result;
    }

    @Transactional
    public Stream<DepartmentDto.DepartmentUserCreateResult> registUserOfDepartment(DepartmentForm.DepartmentUserCreateList departmentUserCreateList, CommonReqVo commonReqVo){
        String userId = UserAuth.get(true).getUsrId();
        List<SmOrganization> organizationList = departmentForm
                .toSmOrganizationList(departmentUserCreateList.getDepartmentUserCreateList());
        SmDepartment smDepartment = null;
        for(int i=0;i<organizationList.size();i++){
            SmOrganization smOrganization =  organizationList.get(i);
            if(smOrganization != null){
                if(i == 0){
                    smDepartment = departmentService.getDepartment(smOrganization.getDeptNo());
                }
                if(smDepartment != null){
                    smOrganization.setDeptUuid(smDepartment.getDeptUuid());
                }
            }
        }
        List<SmOrganization> serviceResult = departmentService.createDepartmentUser(organizationList,userId);

        boolean isAdminDepartment = "ADMIN".equals(departmentUserCreateList.getPjtNo());
        CnProject cnProject = null;
        String pjtDiv = "";

        if(!isAdminDepartment){
            // pjtDiv 설정
            cnProject = informationService.getProject(departmentUserCreateList.getPjtNo());
            pjtDiv = cnProject.getPjtDiv();
        }

        if(!serviceResult.isEmpty()){
            if ("Y".equals(commonReqVo.getApiYn()) ) {
                if(PlatformType.PGAIA.getName().equals(platform) && !isAdminDepartment){
                    HashMap<String,Object> apiParams = new HashMap<>();
                    apiParams.put("organizationList",organizationList);
                    apiParams.put("userId",userId);

                    Map<String,Object> apiResult = invokePgaia2Cairos("GACAM07020104",apiParams);

                    String resultCode = (String)apiResult.get("resultCode");

                    if(!"00".equals(resultCode)){
                        throw new GaiaBizException(ErrorType.INTERFACE,(String)apiResult.get("resultMsg"));
                    }
                }
                else if(isAdminDepartment || "P".equals(pjtDiv)){
                    HashMap<String,Object> apiParams = new HashMap<>();
                    apiParams.put("organizationList",organizationList);
                    apiParams.put("userId",userId);

                    Map<String,Object> apiResult = invokeCairos2Pgaia("CAGAM07020104",apiParams);

                    String resultCode = (String)apiResult.get("resultCode");

                    if(!"00".equals(resultCode)){
                        throw new GaiaBizException(ErrorType.INTERFACE,(String)apiResult.get("resultMsg"));
                    }
                }
            }
            return serviceResult.stream().map(departmentDto::fromSmOrganization);
        }
        return null;
    }

    @Transactional
    public String modifyUserOfDepartment(DepartmentForm.DepartmentUserUpdate departmentUserUpdate, CommonReqVo commonReqVo){
        String userId =  UserAuth.get(true).getUsrId();
        SmOrganization smOrganization = departmentService.getDepartmentUser(departmentUserUpdate.getOrgNo());
        if (smOrganization != null) {
            departmentForm.updateSmOrganization(departmentUserUpdate, smOrganization);
        } else {
            throw new GaiaBizException(ErrorType.NO_DATA);
        }
        smOrganization.setChgId(userId);
        int serviceResult = departmentService.updateDepartmentUser(smOrganization);

        boolean isAdminDepartment = "ADMIN".equals(departmentUserUpdate.getPjtNo());
        CnProject cnProject = null;
        String pjtDiv = "";

        if(!isAdminDepartment){
            // pjtDiv 설정
            cnProject = informationService.getProject(departmentUserUpdate.getPjtNo());
            pjtDiv = cnProject.getPjtDiv();
        }

        if(serviceResult == 1){
            if ("Y".equals(commonReqVo.getApiYn()) ) {
                if(PlatformType.PGAIA.getName().equals(platform) && !isAdminDepartment){
                    HashMap<String,Object> apiParams = new HashMap<>();
                    apiParams.put("smOrganization",smOrganization);

                    Map<String,Object> apiResult = invokePgaia2Cairos("GACAM07020105",apiParams);

                    String resultCode = (String)apiResult.get("resultCode");
                    if(!"00".equals(resultCode)){
                        throw new GaiaBizException(ErrorType.INTERFACE,(String)apiResult.get("resultMsg"));
                    }
                }
                else if(isAdminDepartment || "P".equals(pjtDiv)){
                    HashMap<String,Object> apiParams = new HashMap<>();
                    apiParams.put("smOrganization",smOrganization);

                    Map<String,Object> apiResult = invokeCairos2Pgaia("CAGAM07020105",apiParams);

                    String resultCode = (String)apiResult.get("resultCode");
                    if(!"00".equals(resultCode)){
                        throw new GaiaBizException(ErrorType.INTERFACE,(String)apiResult.get("resultMsg"));
                    }
                }
            }
            return "success";
        }
        return "fail";
    }

    @Transactional
    public boolean removeUserOfDepartment(DepartmentForm.DepartmentUserDeleteList departmentUserDeleteList, CommonReqVo commonReqVo){
        String userId = UserAuth.get(true).getUsrId();

        List<Integer> orgNoList = departmentUserDeleteList.getOrgNoList();
        List<SmOrganization> smOrganizationList = departmentService.getOrganizationList(orgNoList);
        for(SmOrganization smOrganization : smOrganizationList){
            smOrganization.setDltId(userId);
        }
        int serviceResult = departmentService.deleteDepartmentUser(smOrganizationList);

        // pjtDiv 설정
        SmDepartment smDepartment = departmentService.getDepartment(smOrganizationList.get(0).getDeptUuid());

        boolean isAdminDepartment = "ADMIN".equals(smDepartment.getPjtNo());
        CnProject cnProject = null;
        String pjtDiv = "";

        if(!isAdminDepartment){
            // pjtDiv 설정
            cnProject = informationService.getProject(smDepartment.getPjtNo());
            pjtDiv = cnProject.getPjtDiv();
        }

        if(serviceResult != -1){
            if ("Y".equals(commonReqVo.getApiYn()) ) {
                if(PlatformType.PGAIA.getName().equals(platform) && !isAdminDepartment){
                    HashMap<String,Object> apiParams = new HashMap<>();

                    apiParams.put("smOrganizationList",smOrganizationList);

                    Map<String,Object> apiResult = invokePgaia2Cairos("GACAM07020106",apiParams);

                    String resultCode = (String)apiResult.get("resultCode");
                    if(!"00".equals(resultCode)){
                        throw new GaiaBizException(ErrorType.INTERFACE,(String)apiResult.get("resultMsg"));
                    }
                }
                else if(isAdminDepartment || "P".equals(pjtDiv)){
                    HashMap<String,Object> apiParams = new HashMap<>();

                    apiParams.put("smOrganizationList",smOrganizationList);

                    Map<String,Object> apiResult = invokeCairos2Pgaia("CAGAM07020106",apiParams);

                    String resultCode = (String)apiResult.get("resultCode");
                    if(!"00".equals(resultCode)){
                        throw new GaiaBizException(ErrorType.INTERFACE,(String)apiResult.get("resultMsg"));
                    }
                }
            }
        }
        return true;
    }

    public List<Map<String, Object>> getAdminFirstTreeData(){
        return departmentService.getAdminFirstDepartmentList();
    }

    public List<Map<String, Object>> getGaiaTreeData(String loginId, String pjtNo, String cntrctNo){
        return departmentService.getGaiaFirstDepartmentList(loginId,pjtNo,cntrctNo);
    }

    public List<Map<String, Object>> getCairosTreeData(String loginId, String pjtNo, String cntrctNo){
        return departmentService.getCmisFirstDepartmentList(loginId,pjtNo,cntrctNo);
    }

    public List<Map<String, Object>> getAdminSecondTreeData(DepartmentForm.DepartmentGet pjtData, String isAdmin,String pjtType){
        String deptId = pjtData.getDeptId();
        log.debug("==============================================");
        log.debug("deptId 	: " + deptId);
        log.debug("==============================================");

        if(!"ADMIN".equals(isAdmin)){
            if("GAIA".equals(pjtType)){ //GAIA 사용자
                deptId = "G" + pjtData.getPjtNo();
            }else{ //CMIS 사용자
                deptId = "C" + pjtData.getCntrctNo();
            }
        }
        return departmentService.getAdminSecondDepartmentList(deptId);
    }

    public Stream<DepartmentDto.EmployeeDetail> getEmployeeOfDepartmentData(String employeeId, String isAdmin, String pjtType, String langInfo, String pjtNo, String cntrctNo){
        return departmentService.getEmployeeDetails(employeeId,isAdmin,pjtType,langInfo,pjtNo,cntrctNo).stream().map(departmentDto::toEmployeeDetail);
    }

    public List<Map<String, Object>> getDepartmentListData(DepartmentForm.DepartmentGet pjtData){
        String deptId = "";
        if(PlatformType.GAIA.getName().equals(platform) || PlatformType.PGAIA.getName().equals(platform)){ //GAIA 사용자
            deptId = "G" + pjtData.getPjtNo();
        }else if(PlatformType.CAIROS.getName().equals(platform)){ //CAIROS 사용자
            deptId = "C" + pjtData.getCntrctNo();
        }

        DepartmentMybatisParam.GetAuthUsersDepartmentInput input = new DepartmentMybatisParam.GetAuthUsersDepartmentInput();
        input.setDeptId(deptId);
        input.setDeptIdList(pjtData.getDeptIdList());

        return departmentService.getAuthGrpUsersDepartmentList(input);
    }

    public Stream<DepartmentDto.Organization> getAuthGrpUsersOrganizationList(String deptId){
        return departmentService.getAuthGrpUsersOrganizationList(deptId).stream().map(departmentDto::toOrganization);
    }

    public Stream<DepartmentDto.Organization> getAuthGrpUsersDownOrganizationList(String deptId){
        return departmentService.getAuthGrpUsersDownOrganizationList(deptId).stream().map(departmentDto::toOrganization);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Map receiveInterfaceService(String transactionId, Map params) {
        Map<String, Object> result = Maps.newHashMap();
        result.put("resultCode", "00");

        try {
            if ("GACAM07020101".equals(transactionId) || "CAGAM07020101".equals(transactionId)) {
                DepartmentForm.DepartmentCreate smDepartment = objectMapper.convertValue(params.get("smDepartment"), DepartmentForm.DepartmentCreate.class);
                String userId = objectMapper.convertValue(params.get("userId"), String.class);

                projectInitializer.addDepartment(smDepartment, userId);

//                if(departmentService.createDepartment(smDepartment) != 1 ){
//                    result.put("resultCode","01");
//                    result.put("resultMsg","API Interface Fail");
//                }

                return result;
            }
            else if ("GACAM07020102".equals(transactionId) || "CAGAM07020102".equals(transactionId)) {
                SmDepartment smDepartment = objectMapper.convertValue(params.get("smDepartment"), SmDepartment.class);
                boolean isChangeSvrType = objectMapper.convertValue(params.get("isChangeSvrType"), Boolean.class);

                int apiResult = departmentService.updateDepartment(smDepartment, isChangeSvrType);
                if(apiResult != 1){
                    result.put("resultCode","01");
                    result.put("resultMsg","API Interface Fail");
                }
                return result;
            }
            else if ("GACAM07020103".equals(transactionId) || "CAGAM07020103".equals(transactionId)) {
                DepartmentForm.DepartmentDelete department = objectMapper.convertValue(params.get("department"), DepartmentForm.DepartmentDelete.class);
                String userId = objectMapper.convertValue(params.get("userId"), String.class);

                List<Integer> deptNoList = departmentService.deleteDepartment(department.getDeptIdList(),userId);
                authorityGroupService.deleteAuthorityGroupUsersByAuthNoAndRghtGrpUsrTy(deptNoList, "D");

                result.put("resultCode","00");
                return result;
            }
            else if ("GACAM07020104".equals(transactionId) ||  "CAGAM07020104".equals(transactionId)) {
                List<SmOrganization> organizationList = objectMapper.convertValue(params.get("organizationList"), new TypeReference<List<SmOrganization>>(){});
                String userId = objectMapper.convertValue(params.get("userId"), String.class);

                List<SmOrganization> serviceResult = departmentService.createDepartmentUser(organizationList,userId);
                if(!serviceResult.isEmpty()){
                    result.put("resultCode","00");
                }
                else{
                    result.put("resultCode","01");
                    result.put("resultMsg","API Interface Fail");
                }
                return result;
            }
            else if ("GACAM07020105".equals(transactionId) ||  "CAGAM07020105".equals(transactionId)) {
                SmOrganization smOrganization = objectMapper.convertValue(params.get("smOrganization"),SmOrganization.class);

                int serviceResult = departmentService.updateDepartmentUser(smOrganization);
                if(serviceResult != -1){
                    result.put("resultCode","00");
                }
                else{
                    result.put("resultCode","01");
                    result.put("resultMsg","API Interface Fail");
                }
                return result;
            }
            else if ("GACAM07020106".equals(transactionId) || "CAGAM07020106".equals(transactionId)) {
                List<SmOrganization> smOrganizationList = objectMapper.convertValue(params.get("smOrganizationList"), new TypeReference<List<SmOrganization>>() {});

                int serviceResult = departmentService.deleteDepartmentUser(smOrganizationList);
                if(serviceResult != -1){
                    result.put("resultCode","00");
                }
                else{
                    result.put("resultCode","01");
                    result.put("resultMsg","API Interface Fail");
                }
                return result;

            }
        } catch (GaiaBizException e) {
            log.error(e.getMessage(), e);
            result.put("resultCode", "01");
            result.put("resultMsg", e.getMessage());
        }

        return result;
    }

}
