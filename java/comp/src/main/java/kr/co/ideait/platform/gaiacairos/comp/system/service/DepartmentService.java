package kr.co.ideait.platform.gaiacairos.comp.system.service;

import com.fasterxml.jackson.core.type.TypeReference;
import kr.co.ideait.iframework.MaskingUtil;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import kr.co.ideait.platform.gaiacairos.core.constant.CommonCodeConstants;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmDepartment;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmOrganization;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.SmDepartmentRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.SmOrganizationRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.SmUserInfoRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisOutput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.department.DepartmentDto;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.department.DepartmentMybatisParam.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class DepartmentService extends AbstractGaiaCairosService {

    @Autowired
    SmDepartmentRepository smDepartmentRepository;

    @Autowired
    SmOrganizationRepository smOrganizationRepository;

    @Autowired
    SmUserInfoRepository smUserInfoRepository;

    /**
     * 부서
     */

    public List<SmDepartment> getDepartmentList(String contractNo) {
        return smDepartmentRepository.findByCntrctNoAndDltYn(contractNo, "N");
    }

    public SmDepartment getDepartment(int departmentNo) {
        return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.department.selectDepartmentOfDepartmentNo",departmentNo);
    }

    public SmDepartment getDepartment(String departmentUuid) {
        return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.department.selectDepartmentOfDepartmentUuid",departmentUuid);
    }

    public List<MybatisOutput> getDepartmentWithCompanyList(String contractNo) {
        return mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.department.selectDepartmentWithCompanyListByContractNo",
                contractNo);
    }

    /**
     * 선택된 부서의 직원리스트(조직) 조회
     * 
     * @param departmentNo
     * @param langInfo
     * @return
     */
    public List<MybatisOutput> getDepartmentEmploeeListByDeptNo(int departmentNo, String langInfo) {
        // //조직에서 종료일이 지나면 근무상태 '퇴직'으로 변경
        // mybatisSession.update(DaoUtils.getMyBatisId(getClass(),
        // "updateOrganizationFlag"));

        GetEmployeeListInput input = new GetEmployeeListInput();
        input.setDepartmentNo(departmentNo);
        input.setLang(langInfo);

        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.department.selectDepartmentEmploeeListByDeptNo", input);
//        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.department.selectDepartmentEmploeeListByDeptNo", input);
    }

    /**
     * 선택된 부서의 직원리스트(조직) 검색 조회
     */
    public List<MybatisOutput> getDepartmentEmploeeListByDeptNo(GetEmployeeListInput input) {
        List<MybatisOutput> employees = mybatisSession.selectList(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.department.selectDepartmentEmploeeListByDeptNo", input);

        // 마스킹 처리
        for (MybatisOutput employee : employees) {
            Object loginId = employee.get("login_id");
            if (loginId != null) {
                employee.put("login_id", MaskingUtil.maskEmail(loginId.toString()));
            }

            Object usrNm = employee.get("usr_nm");
            if (usrNm != null) {
                employee.put("usr_nm", MaskingUtil.maskName(usrNm.toString()));
            }

            Object emailAdrs = employee.get("email_adrs");
            if (emailAdrs != null) {
                employee.put("email_adrs", MaskingUtil.maskEmail(emailAdrs.toString()));
            }

            Object phoneNo = employee.get("phone_no");
            if (phoneNo != null) {
                employee.put("phone_no", MaskingUtil.maskPhoneNumber(phoneNo.toString()));
            }
        }

        return employees;
    }

    /**
     * 부서 코드 중복 체크
     * 
     * @param
     * @param departmentId
     * @return
     */
    public boolean existDepartment(String departmentId) {
        return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.department.selectDepartmentByDepartmentId", departmentId) != null;
    }

    /**
     * 부서 생성
     * 
     * @param department
     * @return
     */
    public int createDepartment(SmDepartment department) {
        return mybatisSession.insert(
                "kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.department.createDepartment",
                department);
    }

    /**
     * 부서정보 수정
     * 
     * @param department
     * @return
     */
    @Transactional
    public int updateDepartment(SmDepartment department, boolean isChangeSvrType) {
        // 업무 구분이 변경되면 해당 부서에 속한 직원의 직책 변경
        if(isChangeSvrType){
            MybatisInput input = MybatisInput.of().add("deptUuid", department.getDeptUuid())
                                                    .add("pstnCd", department.getSvrType())
                                                    .add("chgId", department.getChgId());
            mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.department.updateOrganizationPstnCd", input);
        }
        return mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.department.updateDepartment",department);
    }

    /**
     * 선택된 부서 삭제 (부서에 속한 조직(소속직원)도 함께 삭제 처리)
     */
    @Transactional
    public List<Integer> deleteDepartment(List<String> departmentIdList, String userId) {
        List<Integer> deptNoList = new ArrayList<Integer>();

        smDepartmentRepository.findAllByDeptIdIn(departmentIdList).forEach(department -> {
            department.setDltId(userId);
            smDepartmentRepository.updateDelete(department, userId);
            deleteOrganization(department.getDeptNo(),userId);

            deptNoList.add(department.getDeptNo());
        });

        return deptNoList;
    }

    /**
     * 조직 삭제
     */
    private void deleteOrganization(Integer deptNo, String userId) {
        List<SmOrganization> organizationList = smOrganizationRepository.findByDeptNo(deptNo);
        organizationList.forEach(organization -> {
            organization.setDltId(userId);
            smOrganizationRepository.updateDelete(organization, userId);
        });
    }

    /**
     * 부서에 속한 직원(조직) 삭제
     * 
     */
    @Transactional
    public int deleteDepartmentUser(List<SmOrganization> smOrganizationList) {
        if(smOrganizationList == null){
            return -1;
        }
        else if(smOrganizationList.size() == 0){
            return 0;
        }
        return mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.department.deleteDepartmentUsers",smOrganizationList);
    }

    /**
     * 조직 구성원 생성
     */
    public List<SmOrganization> createDepartmentUser(List<SmOrganization> organizationList, String userId) {
        organizationList.forEach(organization -> {
            organization.setDltYn("N");
            organization.setRgstrId(userId);
            organization.setChgId(userId);
        });
        return smOrganizationRepository.saveAll(organizationList);
    }

    /**
     * 부서관리 첫번쩨 조직트리 가져오기 (관리자용)
     */
    public List<Map<String, Object>> getAdminFirstDepartmentList() {
        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.department.selectAdminFirstDepartmentList");
    }

    /**
     * 부서관리 첫번쩨 조직트리 가져오기 (Gaia용)
     */
    public List<Map<String, Object>> getGaiaFirstDepartmentList(String loginId, String pjtNo, String cntrctNo) {
        GetDepartmentInput input = new GetDepartmentInput();
        input.setLoginId(loginId);
        input.setCntrctNo(cntrctNo);
        input.setPjtNo(pjtNo);

        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.department.selectGaiaFirstDepartmentList", input);
    }

    /**
     * 부서관리 첫번쩨 조직트리 가져오기 (Cmis용)
     */
    public List<Map<String, Object>> getCmisFirstDepartmentList(String loginId, String pjtNo, String cntrctNo) {
        GetDepartmentInput input = new GetDepartmentInput();
        input.setLoginId(loginId);
        input.setCntrctNo(cntrctNo);
        input.setPjtNo(pjtNo);

        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.department.selectCmisFirstDepartmentList", input);
    }

    /**
     * 부서관리 두번째 조직트리 가져오기
     * 
     */
    public List<Map<String, Object>> getAdminSecondDepartmentList(String deptId) {

        log.debug("==============================================");
        log.debug("deptId 	: " + deptId);
        log.debug("==============================================");

        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.department.selectAdminSecondDepartmentList", deptId);
    }

    /**
     * 부서관리 > 소속 직원 상세 정보 가져오기
     */
    public List<MybatisOutput> getEmployeeDetails(String employeeId, String isAdmin, String userType, String langInfo,
            String pjtNo, String cntrctNo) {
        log.debug("==============================================");
        log.debug("isAdmin 	: " + isAdmin);
        log.debug("userType 	: " + userType);
        log.debug("employeeId 	: " + employeeId);
        log.debug("==============================================");

        GetEmployeeDetailsInput input = new GetEmployeeDetailsInput();
        input.setEmployeeId(employeeId);
        input.setLang(langInfo);

        if ("ADMIN".equals(isAdmin)) {
            return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.department.getEmployeeDetailsAdmin", input);
        } else {
            // 선택한 프로젝트(계약) 정보 받기
            input.setPjtNo(pjtNo);
            input.setCntrctNo(cntrctNo);

            if ("GAIA".equals(userType)) { // GAIA 사용자
                return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.department.getEmployeeDetailsGaia", input);
            } else {// CAIROS 사용자
                return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.department.getEmployeeDetailsCairos", input);
            }
        }
    }

    /**
     * 해당 부서에 추가할 직원 조회
     */
    public List<Map<String, Object>> getOrganizationEmployeeList(String isAdmin,
            GetOranizationEmployeeListInput input) {

        input.setPstnCd(CommonCodeConstants.PSTN_CODE_GROUP_CODE);
        input.setRatngCd(CommonCodeConstants.RANK_CODE_GROUP_CODE);

        if (isAdmin.equals("ADMIN")) {
            return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.department.getOrganizationEmployeeListAdmin", input);
        } else {

            return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.department.getOrganizationEmployeeListGC", input);
        }

    }

    public SmOrganization getOrganizationByOrgNo(Integer orgNo){
        return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.department.getOrganizationByOrgNo", orgNo);
    }

    /**
     * 해당 계약의 해당 직원이 속한 조직 정보 검색
     *
     * @param usrId ({@link String}) 직원의 아이디
     * @param cntrctNo ({@link String}) 계약의 번호
     * @return {@link SmOrganization}
     */
    public List<SmOrganization> getOrganizationByUsrIdAndCntrctNo(String usrId, String cntrctNo){
        HashMap<String, Object> mybatisParams = new HashMap<>();
        mybatisParams.put("usrId", usrId);
        mybatisParams.put("cntrctNo", cntrctNo);
        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.department.selectSmOrganizationByUsrIdAndCntrctNo",mybatisParams);
    }

    /**
     * 유저 아이디와 부서 번호로 조직 정보 검색
     *
     * @param usrId
     * @param deptNo
     * @return
     */
    //사용처
    /*
        InspectionreportApiController.getReport
     */
    public SmOrganization getOrganizationByUsrIdAndDeptNo(String  usrId, Integer deptNo){
        HashMap<String, Object> mybatisParams = new HashMap<>();
        mybatisParams.put("usrId", usrId);
        mybatisParams.put("deptNo", deptNo);
        return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.department.selectSmOrganizationByUsrIdAndDeptNo",mybatisParams);
    }

    /**
     * 유저 아이디와 부서 uuid로 조직 정보 검색
     *
     * @param usrId
     * @param deptUuid
     * @return
     */
    public SmOrganization getOrganizationByUsrIdAndDeptUuid(String  usrId, String deptUuid){
        HashMap<String, Object> mybatisParams = new HashMap<>();
        mybatisParams.put("usrId", usrId);
        mybatisParams.put("deptUuid", deptUuid);
        return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.department.selectSmOrganizationByUsrIdAndDeptUuid",mybatisParams);
    }


    /**
     * 권한그룹 관리 > 권한그룹사용자(부서) 추가 - 부서리스트 조회
     * 
     * @param
     * @return
     */
    public List<Map<String, Object>> getAuthGrpUsersDepartmentList(GetAuthUsersDepartmentInput input) {

        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.department.selectAuthGrpUsersDepartmentList", input);
    }

    /**
     * 권한그룹 관리 > 권한그룹사용자(사용자, 역할) 추가 - 조직리스트 조회
     * 
     * @param deptId
     * @return
     */
    public List<MybatisOutput> getAuthGrpUsersOrganizationList(String deptId) {

        log.debug("==============================================");
        log.debug("deptId 	: " + deptId);
        log.debug("==============================================");

        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.department.selectAuthGrpUsersOrganizationList",
                deptId);
    }

    /**
     * 권한그룹 관리 > 권한그룹사용자(사용자, 역할) 추가 - 하위 조직리스트 조회
     * 
     * @param deptId
     * @return
     */
    public List<MybatisOutput> getAuthGrpUsersDownOrganizationList(String deptId) {

        log.debug("==============================================");
        log.debug("deptId 	: " + deptId);
        log.debug("==============================================");

        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.department.selectAuthGrpUsersDownOrganizationList",
                deptId);
    }

    /**
     * 부서에 속한 소속 직원 정보 수정
     * 
     * @param organization
     * @return
     */
    public int updateDepartmentUser(SmOrganization organization) {
        // 근무 상태가 임시직(T)이 아니면 시작일, 종료일 제거
        if (!organization.getFlag().equals("T")) {
            organization.setStartDt(null);
            organization.setEndDt(null);
        }
        return mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.department.updateUserOfDepartment",organization);
    }

    public SmOrganization getDepartmentUser(Integer organizationId) {
        return smOrganizationRepository.findById(organizationId).orElse(null);
    }

    /**
     * 소속 직원 > 종료일이 지난 경우 근무 상태 값 변경
     */
    public void updateOrganiztionFlag() {
        mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.department.updateOrganizationFlag");
    }

    /**
     * 선택된 부서의 직원리스트(조직) 조회
     * 
     * @param
     * @param langInfo
     * @return
     */
    public Page<MybatisOutput> getUserSearchEmploeeListByDeptNo(GetEmployeeListInput getEmployeeListInput,
            String langInfo) {

        List<MybatisOutput> outputs = mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.department.getUserSearchEmploeeListByDeptNo",getEmployeeListInput);
        Long totalCount = mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.department.getUserSearchEmploeeListByDeptNoCount",getEmployeeListInput);

        return new PageImpl<>(outputs, getEmployeeListInput.getPageable(), totalCount);
    }

    /**
     * 사용자가 속한 부서 목록 조회
     * @param userId
     * @return
     */
    public List<DepartmentDto.Department> getDepartmentListByUserId(String userId) {
        List<SmDepartment> departments = mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.department.selectDepartmentListByUserId", userId);

        return objectMapper.convertValue(departments, new TypeReference<List<DepartmentDto.Department>>() {});
    }

    /**
     * 해당 계약의 사용자가 속한 부서 목록 조회
     *
     * @param userId
     * @param cntrctNo
     * @return
     */
    public List<SmDepartment> getDepartmentListByUserIdAndCntrctNo(String userId, String cntrctNo){
        HashMap<String,Object> mybatisParams = new HashMap<>();
        mybatisParams.put("userId", userId);
        mybatisParams.put("cntrctNo", cntrctNo);
        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.department.selectDepartmentListByUserIdAndCntrctNo", mybatisParams);
    }

    public List<SmOrganization> getOrganizationList(List<Integer> orgNoList) {
        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.department.selectOrganizationListByOrgNos", orgNoList);
    }

    public List<SmDepartment> getDepartmentListOfProject(String pjtNo){
        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.department.selectDepartmentListOfProject", pjtNo);
    }

    public boolean modifyUuidOfDepartmentFindByDeptId(String deptId, String deptUuid){
        HashMap<String, Object> datas = new HashMap<String, Object>();
        datas.put("deptId", deptId);
        datas.put("deptUuid", deptUuid);
        return mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.department.updateDepartmentUuid",datas) == 1;
    }

    /**
     * 해당 부서가 시공사('01') / 감리('05') 여부 확인 후 해당 업무 구분 반환.
     * @param deptId
     * @param cntrctNo
     * @return
     */
    public String containConstructionSvrType(String deptId, String cntrctNo){
        HashMap<String, Object> input = new HashMap<>();
        input.put("deptId", deptId);
        input.put("cntrctNo", cntrctNo);

        return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.system.department.selectContainConstructionSvrType", input);
    }
}
