package kr.co.ideait.platform.gaiacairos.comp.project.helper;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import kr.co.ideait.platform.gaiacairos.comp.system.service.CommonCodeService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import kr.co.ideait.platform.gaiacairos.core.exception.ErrorType;
import kr.co.ideait.platform.gaiacairos.core.exception.GaiaBizException;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisInput;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.document.DocumentForm;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.department.DepartmentForm;
import kr.co.ideait.platform.gaiacairos.core.util.restclient.DocumentServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.*;

@Slf4j
@Service
public class ProjectInitializer extends AbstractGaiaCairosService {

//    @Autowired
//    private DocumentComponent documentComponent;

    @Autowired
    private CommonCodeService commonCodeService;

    @Autowired
    private DocumentServiceClient documentServiceClient;

    public static final Map<String, String> BASE_DEPARTMENT = Maps.newHashMap();

    static {
        BASE_DEPARTMENT.put("pstnNm", null);
        BASE_DEPARTMENT.put("mngNm", null);
        BASE_DEPARTMENT.put("useYn", "Y");
        BASE_DEPARTMENT.put("dltYn", "N");
        BASE_DEPARTMENT.put("rgstrId", "SYSTEM");
        BASE_DEPARTMENT.put("chgId", "SYSTEM");
        BASE_DEPARTMENT.put("dsplyYn", "Y");
    }

    //(임시)
    static Map<String, Integer> documentSystemKeyMap = new HashMap<>();

    public static final Integer SYSTEM_NO_PGAIA = 1;
    public static final Integer SYSTEM_NO_GAIA = 2;
    public static final Integer SYSTEM_NO_CAIROS = 3;

    static {
        documentSystemKeyMap.put("pgaia", SYSTEM_NO_PGAIA);
        documentSystemKeyMap.put("gaia", SYSTEM_NO_GAIA);
        documentSystemKeyMap.put("cairos", SYSTEM_NO_CAIROS);
    }

    private List<Map<String, Object>> getDepartmentList(Map<String, Object> params) {
        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.project_initializer.selectDepartmentList", params);
    }

    private Integer getDepartmentCount(Map<String, Object> params) {
        return mybatisSession.selectOne("kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.project_initializer.selectDepartmentCount", params);
    }

    private void addDepartment(Map<String, Object> params) {
        mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.project_initializer.insertDepartment", params);
    }

    private void addDepartmentForSelect(Map<String, Object> params) {
        mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.project_initializer.insertDepartmentForSelect", params);
    }

    private void modifyDepartment(Map<String, Object> params) {
        mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.project_initializer.updateDepartment", params);
    }

    private void removeDepartment(Map<String, Object> params) {
        mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.project_initializer.deleteDepartment", params);
    }

    private void addNavigation(Map<String, Object> params) {
        mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.project_initializer.insertNavigation", params);
    }

    private void modifyNavigation(Map<String, Object> params) {
        mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.project_initializer.updateNavigation", params);
    }

    private void removeNavigation(Map<String, Object> params) {
        mybatisSession.update("kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.project_initializer.deleteNavigation", params);
    }

    private List<Map<String, String>> getAuthorityGroupList(Map<String, Object> params) {
        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.project_initializer.selectAuthorityGroupList", params);
    }

    private void addAuthorityGroup(Map<String, Object> params) {
        mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.project_initializer.insertAuthorityGroup", params);
    }

    private void addAuthority(Map<String, Object> params) {
        mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.project_initializer.insertAuthority", params);
    }

    private void addAuthorityGroupUsers(Map<String, Object> params) {
        mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.project_initializer.insertAuthorityGroupUsers", params);
    }

    private List<Map<String, String>> getMenuList(Map<String, Object> params) {
        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.project_initializer.selectMenuList", params);
    }

	//old 새로운 방식으로 변경~ 추후 자동화 필요!!
//    private void addMenuAuthorityGroup(Map<String, Object> params) {
//        mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.project_initializer.insertMenuAuthorityGroup", params);
//    }
    private void addMenuAuthorityGroup(String pjt_no, String cntrct_no, String pjt_type) {
    	MybatisInput input = MybatisInput.of().add("pjt_no", pjt_no).add("cntrct_no", cntrct_no).add("pjt_type", pjt_type);
        mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.project_initializer.insertMenuAuthorityGroup", input);
    }

    private List<String> getComCodeList(Map<String, Object> params) {
        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.project_initializer.selectComCodeList", params);
    }

    private List<Map<String, Object>> getNaviFolderTypeListFromComCode(Map<String, Object> params) {
        params.put("excludedCmnCd", "0"); // 제외할 코드
        return mybatisSession.selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.project.project_initializer.selectNaviFolderTypeList", params);
    }

    private Map<String, Object> makeGaiaDepartment(String corpNo, String pjtNo, String cntrctNo, String itemName, String itemDesc) {
        Map<String, Object> department = Maps.newHashMap(BASE_DEPARTMENT);
        department.put("corpNo", corpNo);
        department.put("pjtNo", pjtNo);
        department.put("cntrctNo", cntrctNo);
        department.put("pjtType", "GAIA");
        department.put("deptId", String.format("G%s", pjtNo));
        department.put("deptNm", itemName);
        department.put("deptDscrpt", itemDesc);
        department.put("upDeptId", "G");
        department.put("deptLvl", 1);
        department.put("dsplyOrdr", "MAX");
        department.put("svrType", null);
        department.put("deptYn", "N");
        department.put("deptUuid", UUID.randomUUID().toString());

        return department;
    }

    private List<Map<String, Object>> makeGaiaSubDeptList(String corpNo, String pjtNo, String cntrctNo, String deptId, String deptNm, String deptDesc) {
        List<Map<String, Object>> subDepartmentList = Lists.newArrayList();

        Map<String, Object> department = Maps.newHashMap(BASE_DEPARTMENT);
        department.put("corpNo", corpNo);
        department.put("pjtNo", pjtNo);
        department.put("cntrctNo", cntrctNo);
        department.put("pjtType", "GAIA");
        department.put("deptId", String.format("%s.M1", deptId));
        department.put("deptNm", "관리관");
        department.put("deptDscrpt", "프로젝트 관리관");
        department.put("upDeptId", deptId);
        department.put("deptLvl", 2);
        department.put("dsplyOrdr", 1);
        department.put("svrType", "07");
        department.put("deptYn", "Y");
        department.put("deptUuid", UUID.randomUUID().toString());

        subDepartmentList.add(department);

        department = Maps.newHashMap(BASE_DEPARTMENT);
        department.put("corpNo", corpNo);
        department.put("pjtNo", pjtNo);
        department.put("cntrctNo", cntrctNo);
        department.put("pjtType", "GAIA");
        department.put("deptId", String.format("%s.M2", deptId));
        department.put("deptNm", "수요기관");
        department.put("deptDscrpt", "프로젝트 수요기관");
        department.put("upDeptId", deptId);
        department.put("deptLvl", 2);
        department.put("dsplyOrdr", 2);
        department.put("svrType", "10");
        department.put("deptYn", "Y");
        department.put("deptUuid", UUID.randomUUID().toString());

        subDepartmentList.add(department);

        department = Maps.newHashMap(BASE_DEPARTMENT);
        department.put("corpNo", corpNo);
        department.put("pjtNo", pjtNo);
        department.put("cntrctNo", cntrctNo);
        department.put("pjtType", "CAIROS");
        department.put("deptId", String.format("C%s", pjtNo));
        department.put("deptNm", deptNm);
        department.put("deptDscrpt", deptDesc);
        department.put("upDeptId", "C");
        department.put("deptLvl", 1);
        department.put("dsplyOrdr", "MAX");
        department.put("svrType", null);
        department.put("deptYn", "N");
        department.put("deptUuid", UUID.randomUUID().toString());

        subDepartmentList.add(department);

        return subDepartmentList;
    }

    private List<Map<String, Object>> makeCairosSubDeptList(String corpNo, String pjtNo, String cntrctNo, String deptId) {
        List<Map<String, Object>> subDepartmentList = Lists.newArrayList();

        Map<String, Object> department = Maps.newHashMap(BASE_DEPARTMENT);
        department.put("corpNo", corpNo);
        department.put("pjtNo", pjtNo);
        department.put("cntrctNo", cntrctNo);
        department.put("pjtType", "CAIROS");
        department.put("deptId", String.format("%s.m1", deptId));
        department.put("deptNm", "관리관");
        department.put("deptDscrpt", "계약별 관리관");
        department.put("upDeptId", deptId);
        department.put("deptLvl", 3);
        department.put("dsplyOrdr", 1);
        department.put("dsply_yn", "N");
        department.put("svrType", "07");
        department.put("deptYn", "Y");
        department.put("deptUuid", UUID.randomUUID().toString());

        subDepartmentList.add(department);

        department = Maps.newHashMap(BASE_DEPARTMENT);
        department.put("corpNo", corpNo);
        department.put("pjtNo", pjtNo);
        department.put("cntrctNo", cntrctNo);
        department.put("pjtType", "CAIROS");
        department.put("deptId", String.format("%s.m3", deptId));
        department.put("deptNm", "건설사업관리단");
        department.put("deptDscrpt", "건설사업관리단");
        department.put("upDeptId", deptId);
        department.put("deptLvl", 3);
        department.put("dsplyOrdr", 2);
        department.put("svrType", "05");
        department.put("deptYn", "Y");
        department.put("deptUuid", UUID.randomUUID().toString());

        subDepartmentList.add(department);

        department = Maps.newHashMap(BASE_DEPARTMENT);
        department.put("corpNo", corpNo);
        department.put("pjtNo", pjtNo);
        department.put("cntrctNo", cntrctNo);
        department.put("pjtType", "CAIROS");
        department.put("deptId", String.format("%s.l1", deptId));
        department.put("deptNm", "품질보증");
        department.put("deptDscrpt", "품질보증담당");
        department.put("upDeptId", String.format("%s.m3", deptId));
        department.put("deptLvl", 4);
        department.put("dsplyOrdr", 1);
        department.put("svrType", "13");
        department.put("deptYn", "Y");
        department.put("deptUuid", UUID.randomUUID().toString());

        subDepartmentList.add(department);

        department = Maps.newHashMap(BASE_DEPARTMENT);
        department.put("corpNo", corpNo);
        department.put("pjtNo", pjtNo);
        department.put("cntrctNo", cntrctNo);
        department.put("pjtType", "CAIROS");
        department.put("deptId", String.format("%s.l2", deptId));
        department.put("deptNm", "책임감리");
        department.put("deptDscrpt", "책임감리담당");
        department.put("upDeptId", String.format("%s.m3", deptId));
        department.put("deptLvl", 4);
        department.put("dsplyOrdr", 2);
        department.put("svrType", "05");
        department.put("deptYn", "Y");
        department.put("deptUuid", UUID.randomUUID().toString());

        subDepartmentList.add(department);

        department = Maps.newHashMap(BASE_DEPARTMENT);
        department.put("corpNo", corpNo);
        department.put("pjtNo", pjtNo);
        department.put("cntrctNo", cntrctNo);
        department.put("pjtType", "CAIROS");
        department.put("deptId", String.format("%s.m2", deptId));
        department.put("deptNm", "현장소장");
        department.put("deptDscrpt", "계약별 현장소장");
        department.put("upDeptId", deptId);
        department.put("deptLvl", 3);
        department.put("dsplyOrdr", 3);
        department.put("svrType", "01");
        department.put("deptYn", "Y");
        department.put("deptUuid", UUID.randomUUID().toString());

        subDepartmentList.add(department);

        department = Maps.newHashMap(BASE_DEPARTMENT);
        department.put("corpNo", corpNo);
        department.put("pjtNo", pjtNo);
        department.put("cntrctNo", cntrctNo);
        department.put("pjtType", "CAIROS");
        department.put("deptId", String.format("%s.s1", deptId));
        department.put("deptNm", "품질");
        department.put("deptDscrpt", "품질담당");
        department.put("upDeptId", String.format("%s.m2", deptId));
        department.put("deptLvl", 4);
        department.put("dsplyOrdr", 1);
        department.put("svrType", "02");
        department.put("deptYn", "Y");
        department.put("deptUuid", UUID.randomUUID().toString());

        subDepartmentList.add(department);

        department = Maps.newHashMap(BASE_DEPARTMENT);
        department.put("corpNo", corpNo);
        department.put("pjtNo", pjtNo);
        department.put("cntrctNo", cntrctNo);
        department.put("pjtType", "CAIROS");
        department.put("deptId", String.format("%s.s2", deptId));
        department.put("deptNm", "안전");
        department.put("deptDscrpt", "안전담당");
        department.put("upDeptId", String.format("%s.m2", deptId));
        department.put("deptLvl", 4);
        department.put("dsplyOrdr", 2);
        department.put("svrType", "03");
        department.put("deptYn", "Y");
        department.put("deptUuid", UUID.randomUUID().toString());

        subDepartmentList.add(department);

        department = Maps.newHashMap(BASE_DEPARTMENT);
        department.put("corpNo", corpNo);
        department.put("pjtNo", pjtNo);
        department.put("cntrctNo", cntrctNo);
        department.put("pjtType", "CAIROS");
        department.put("deptId", String.format("%s.s3", deptId));
        department.put("deptNm", "환경");
        department.put("deptDscrpt", "환경담당");
        department.put("upDeptId", String.format("%s.m2", deptId));
        department.put("deptLvl", 4);
        department.put("dsplyOrdr", 3);
        department.put("svrType", "04");
        department.put("deptYn", "Y");
        department.put("deptUuid", UUID.randomUUID().toString());

        subDepartmentList.add(department);

        department = Maps.newHashMap(BASE_DEPARTMENT);
        department.put("corpNo", corpNo);
        department.put("pjtNo", pjtNo);
        department.put("cntrctNo", cntrctNo);
        department.put("pjtType", "CAIROS");
        department.put("deptId", String.format("%s.s4", deptId));
        department.put("deptNm", "공무");
        department.put("deptDscrpt", "공무담당");
        department.put("upDeptId", String.format("%s.m2", deptId));
        department.put("deptLvl", 4);
        department.put("dsplyOrdr", 4);
        department.put("svrType", "06");
        department.put("deptYn", "Y");
        department.put("deptUuid", UUID.randomUUID().toString());

        subDepartmentList.add(department);

        department = Maps.newHashMap(BASE_DEPARTMENT);
        department.put("corpNo", corpNo);
        department.put("pjtNo", pjtNo);
        department.put("cntrctNo", cntrctNo);
        department.put("pjtType", "CAIROS");
        department.put("deptId", String.format("%s.s5", deptId));
        department.put("deptNm", "설계");
        department.put("deptDscrpt", "설계담당");
        department.put("upDeptId", String.format("%s.m2", deptId));
        department.put("deptLvl", 4);
        department.put("dsplyOrdr", 5);
        department.put("svrType", "11");
        department.put("deptYn", "Y");
        department.put("deptUuid", UUID.randomUUID().toString());

        subDepartmentList.add(department);

        return subDepartmentList;
    }

    private void executeAddDepartment(List<Map<String, Object>> subDepartmentList, final String pjtType, final String pjtNo, final String cntrctNo, final String deptId, final String[] deptIds) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("deptId", deptId);

        for (Map<String, Object> subDepartment : subDepartmentList) {
            this.addDepartment(subDepartment);
        }

        params.put("pjtNo", pjtNo);
        params.put("cntrctNo", cntrctNo);
        params.put("list", deptIds);

        List<Map<String, Object>> departmentList = this.getDepartmentList(params);

        for (Map<String, Object> dept : departmentList) {
            params.put("rghtGrpCd", UUID.randomUUID().toString()); //권한 그룹 UUID 생성
            params.put("pjtNo", pjtNo);
            params.put("cntrctNo", cntrctNo);
            params.put("pjtType", pjtType);
            params.put("deptNm", dept.get("dept_nm"));
            params.put("deptDscrpt", dept.get("dept_dscrpt"));
            params.put("rghtGrpTy", "D");
            params.put("svrType", dept.get("svr_type"));
            this.addAuthorityGroup(params); // 생성된 부서 정보를 기반으로 권한 그룹 추가

            params.put("deptNo", dept.get("dept_no"));
            params.put("rghtGrpUsrTy", "D");
            this.addAuthorityGroupUsers(params); //권한 그룹 사용자 생성
            
          //old 새로운 방식으로 변경~ 추후 자동화 필요!!
//            List<Map<String, String>> menus = this.getMenuList(params); //메뉴 정보 조회 (시스템 관리자 설정, CS 관리의 하위 메뉴 제외)
//
//            for (Map<String, String> menu : menus) {
//                params.put("menuNo", menu.get("menu_no"));
//                params.put("menuCd", menu.get("menu_cd"));
//
//                this.addMenuAuthorityGroup(params); //메뉴 권한 그룹 생성 (최초 권한은 '읽기' 권한으로 설정)
//            }
        }

        String skipMenuAuth = subDepartmentList.getFirst().get("skipMenuAuth") != null
                ? subDepartmentList.getFirst().get("skipMenuAuth").toString()
                : "N"; // null 방지용 기본값

        if (!"Y".equalsIgnoreCase(skipMenuAuth)) {
            this.addMenuAuthorityGroup(pjtNo, cntrctNo, pjtType.toUpperCase());
        }
    }

    private void executeAddNavigation(Map<String, Object> params) {
        String cntrctNo = (String) params.get("cntrctNo");
        String pjtNo = (String) params.get("pjtNo");
        String itemName = (String) params.get("itemName");

        // 내비게이션 및 네비 권한 생성
        List<String> codes = this.getComCodeList(params);

        for (String code : codes) {
            params.put("naviId", String.format("%s_%s", code, cntrctNo));
            params.put("naviDiv", code);
            params.put("naviPath", itemName);
            params.put("naviNm", itemName);

            // ref_sys_key 설정
            Integer refSysKey = documentSystemKeyMap.get(platform);
            params.put("refSysKey", refSysKey);

            // 메뉴 권한 그룹 생성 (최초 권한은 '읽기' 권한으로 설정)
            this.addNavigation(params);

            // 생성된 권한그룹 조회
            List<Map<String, String>> authorityGroupList = this.getAuthorityGroupList(params);

            for (Map<String, String> authorityGroup : authorityGroupList) {
                params.put("rghtGrpNo", authorityGroup.get("rght_grp_no"));
                params.put("rghtGrpCd", authorityGroup.get("rght_grp_cd"));

                this.addAuthority(params); //문서 네비게이션 권한 생성
            }

//            // 트랜잭션 내에서 변경된 데이터를 DB에 즉시 반영
//            mybatisSession.flushStatements();  // 강제로 DB에 반영

            // 착공계 문서관리 네비게이션인 경우, 하위 네비게이션 생성.
            if("01".equals(code)) {
                Integer upNaviNo = (Integer) params.get("naviNo");
                String upNaviId = params.get("naviId").toString();

                params.put("upNaviNo", upNaviNo);
                params.put("upNaviId", upNaviId);

                this.createInitSubDcNavigationList(cntrctNo, pjtNo, upNaviId, upNaviNo);
            }
        }
    }

    public void addProject(String corpNo, String pjtType, String pjtNo, String cntrctNo, String itemName, String itemDesc) {
        if (StringUtils.isEmpty(pjtNo) && StringUtils.isEmpty(pjtType)) {
            throw new GaiaBizException(ErrorType.ETC, "INVALID PARAMETERS");
        }

        if (!StringUtils.equals(pjtType, "P") && StringUtils.isEmpty(cntrctNo)) {
            throw new GaiaBizException(ErrorType.ETC, "INVALID PARAMETERS");
        }


        Map<String, Object> params = Maps.newHashMap();
        params.put("corpNo", corpNo);
        params.put("pjtNo", pjtNo);
        params.put("itemName", itemName);
        params.put("itemDesc", itemDesc);

        Integer checkCount = 0;

        if (StringUtils.equals(pjtType, "P")) {
            final String gaiaDeptId = String.format("G%s", pjtNo);

            params.put("pjtType", "GAIA");
            params.put("cntrctNo", pjtNo);
            params.put("deptId", gaiaDeptId);

            Map<String, Object> department = this.makeGaiaDepartment(corpNo, pjtNo, cntrctNo, itemName, itemDesc);
            this.addDepartment(department);

            checkCount = getDepartmentCount(params);

            if (checkCount > 0) {
                List<Map<String, Object>> subDepartmentList = this.makeGaiaSubDeptList(corpNo, pjtNo, cntrctNo, gaiaDeptId, itemName, itemDesc);

                this.executeAddDepartment(subDepartmentList, "GAIA", pjtNo, cntrctNo, gaiaDeptId, new String[]{
                        String.format("G%s.M1", pjtNo)
                        , String.format("G%s.M2", pjtNo)
                });
                this.executeAddNavigation(params);
            } else {
                throw new GaiaBizException(ErrorType.ETC, String.format("[code 01] 프로젝트번호 %s의 기본 데이터가 존재하지 않습니다.", pjtNo));
            }
        } else {
            final String deptId = String.format("C%s", cntrctNo);
            final String upDeptId = String.format("C%s", pjtNo);
            params.put("pjtType", "CAIROS");
            params.put("cntrctNo", pjtNo); // 상위 부서(dept_id = 'C+pjt_no')의 cntrctNo = pjtNo
            params.put("deptId", upDeptId);

            checkCount = this.getDepartmentCount(params);

            if (checkCount > 0) {
                Map<String, Object> department = Maps.newHashMap(BASE_DEPARTMENT);
                department.put("corpNo", corpNo);
                department.put("pjtNo", pjtNo);
                department.put("cntrctNo", cntrctNo);
                department.put("pjtType", "CAIROS");
                department.put("deptId", deptId);
                department.put("deptNm", itemName);
                department.put("deptDscrpt", itemDesc);
                department.put("upDeptId", upDeptId);
                department.put("deptLvl", 2);
                department.put("dsplyOrdr", "MAX");
                department.put("svrType", null);
                department.put("deptYn", "N");
                department.put("deptUuid", UUID.randomUUID().toString());

                this.addDepartment(department);

                params.put("cntrctNo", cntrctNo); // 생성된 계약번호로 변경.
                params.put("deptId", deptId);

                checkCount = this.getDepartmentCount(params);

                if (checkCount > 0) {
                    List<Map<String, Object>> subDepartmentList = this.makeCairosSubDeptList(corpNo, pjtNo, cntrctNo, deptId);

                    this.executeAddDepartment(subDepartmentList, "CAIROS", pjtNo, cntrctNo, deptId, new String[]{
                            String.format("C%s.m1", cntrctNo)
                            , String.format("C%s.m2", cntrctNo)
                            , String.format("C%s.m3", cntrctNo)
                            , String.format("C%s.s1", cntrctNo)
                            , String.format("C%s.s2", cntrctNo)
                            , String.format("C%s.s3", cntrctNo)
                            , String.format("C%s.s4", cntrctNo)
                            , String.format("C%s.s5", cntrctNo)
                            , String.format("C%s.l1", cntrctNo)
                            , String.format("C%s.l2", cntrctNo)
                    });
                    this.executeAddNavigation(params);
                } else {
                    throw new GaiaBizException(ErrorType.ETC, String.format("[code 02] 계약번호 %s의 기본 데이터가 존재하지 않습니다.", cntrctNo));
                }
            } else {
                params.put("cntrctNo", pjtNo);
                params.put("deptId", String.format("G%s", pjtNo));

                checkCount = this.getDepartmentCount(params);

                if (checkCount > 0) {
                    Map<String, Object> department = Maps.newHashMap(BASE_DEPARTMENT);
                    department.put("corpNo", corpNo);
                    department.put("pjtNo", pjtNo);
                    department.put("cntrctNo", cntrctNo);
                    department.put("pjtType", "CAIROS");
                    department.put("deptId", deptId);
                    department.put("upDeptId", "C");
                    department.put("whereDeptLvl", 1);
                    department.put("wherePjtType", "GAIA");
                    department.put("whereDeptId", String.format("G%s", pjtNo));

                    this.addDepartmentForSelect(department);

                    department = Maps.newHashMap(BASE_DEPARTMENT);
                    department.put("corpNo", corpNo);
                    department.put("pjtNo", pjtNo);
                    department.put("cntrctNo", cntrctNo);
                    department.put("pjtType", "CAIROS");
                    department.put("deptId", deptId);
                    department.put("deptNm", itemName);
                    department.put("deptDscrpt", itemDesc);
                    department.put("upDeptId", upDeptId);
                    department.put("whereUpDeptId", upDeptId);
                    department.put("deptLvl", 2);
                    department.put("dsplyOrdr", "MAX");
                    department.put("svrType", null);
                    department.put("deptYn", "N");
                    department.put("deptUuid", UUID.randomUUID().toString());

                    this.addDepartment(department);

                    params.put("cntrctNo", cntrctNo);
                    params.put("deptId", String.format("C%s", cntrctNo));

                    checkCount = this.getDepartmentCount(params);

                    if (checkCount > 0) {
                        List<Map<String, Object>> subDepartmentList = this.makeCairosSubDeptList(corpNo, pjtNo, cntrctNo, deptId);

                        this.executeAddDepartment(subDepartmentList, "CAIROS", pjtNo, cntrctNo, deptId, new String[]{
                                String.format("C%s.m1", cntrctNo)
                                , String.format("C%s.m2", cntrctNo)
                                , String.format("C%s.m3", cntrctNo)
                                , String.format("C%s.s1", cntrctNo)
                                , String.format("C%s.s2", cntrctNo)
                                , String.format("C%s.s3", cntrctNo)
                                , String.format("C%s.s4", cntrctNo)
                                , String.format("C%s.s5", cntrctNo)
                                , String.format("C%s.l1", cntrctNo)
                                , String.format("C%s.l2", cntrctNo)
                        });
                        this.executeAddNavigation(params);
                    } else {
                        throw new GaiaBizException(ErrorType.ETC, String.format("[code 03] 계약번호 %s의 기본 데이터가 존재하지 않습니다.", cntrctNo));
                    }
                } else {
                    throw new GaiaBizException(ErrorType.ETC, String.format("[code 04] 프로젝트번호 %s의 기본 데이터가 존재하지 않습니다.", pjtNo));
                }
            }
        }
    }

    public void modifyProject(String corpNo, String pjtType, String pjtNo, String cntrctNo, String itemName, String itemDesc) {
        if (StringUtils.isEmpty(pjtNo) && StringUtils.isEmpty(pjtType)) {
            throw new GaiaBizException(ErrorType.ETC, "INVALID PARAMETERS");
        }

        
        if (!StringUtils.equals(pjtType, "P") && StringUtils.isEmpty(cntrctNo)) {
            throw new GaiaBizException(ErrorType.ETC, "INVALID PARAMETERS");
        }

        Map<String, Object> params = Maps.newHashMap();
        params.put("corpNo", corpNo);
        params.put("pjtNo", pjtNo);
        params.put("itemName", itemName);
        params.put("itemDesc", itemDesc);

        Integer checkCount = 0;

        if (StringUtils.equals(pjtType, "P")) {
            final String gaiaDeptId = String.format("G%s", pjtNo);
            params.put("cntrctNo", cntrctNo);
            params.put("deptId", gaiaDeptId);

            checkCount = this.getDepartmentCount(params);

            if (checkCount > 0) {
                params.put("deptNm", itemName);
                params.put("deptLvl", 1);

                this.modifyDepartment(params);

                params.put("naviPath", itemName);
                params.put("naviNm", itemName);

                this.modifyNavigation(params);
            } else {

                Map<String, Object> department = this.makeGaiaDepartment(corpNo, pjtNo, cntrctNo, itemName, itemDesc);
                this.addDepartment(department);

                params.put("deptId", String.format("G%s", pjtNo));

                checkCount = this.getDepartmentCount(params);

                if (checkCount > 0) {
                    List<Map<String, Object>> subDepartmentList = this.makeGaiaSubDeptList(corpNo, pjtNo, cntrctNo, gaiaDeptId, itemName, itemDesc);

                    this.executeAddDepartment(subDepartmentList, cntrctNo, gaiaDeptId, itemName, itemDesc, new String[]{
                            String.format("G%s.M1", pjtNo)
                            , String.format("G%s.M2", pjtNo)
                    });
                    this.executeAddNavigation(params);
                } else {
                    throw new GaiaBizException(ErrorType.ETC, String.format("[code 05] 프로젝트번호 %s의 기본 데이터가 존재하지 않습니다.", pjtNo));
                }
            }
        } else {
            final String deptId = String.format("C%s", cntrctNo);
            final String upDeptId = String.format("C%s", pjtNo);
            params.put("pjtType", "CAIROS");
            params.put("cntrctNo", cntrctNo);
            params.put("deptId", deptId);

            checkCount = this.getDepartmentCount(params);

            if (checkCount > 0) {
                params.put("deptNm", itemName);
                params.put("deptLvl", 2);

                this.modifyDepartment(params);

                params.put("naviPath", itemName);
                params.put("naviNm", itemName);

                this.modifyNavigation(params);
            } else {
                params.put("deptId", upDeptId);

                checkCount = this.getDepartmentCount(params);

                if (checkCount > 0) {
                    this.addDepartment(params);

                    checkCount = this.getDepartmentCount(params);

                    if (checkCount > 0) {
                        List<Map<String, Object>> subDepartmentList = this.makeCairosSubDeptList(corpNo, pjtNo, cntrctNo, deptId);

                        this.executeAddDepartment(subDepartmentList, cntrctNo, deptId, itemName, itemDesc, new String[]{
                                String.format("C%s.m1", cntrctNo)
                                , String.format("C%s.m2", cntrctNo)
                                , String.format("C%s.m3", cntrctNo)
                                , String.format("C%s.s1", cntrctNo)
                                , String.format("C%s.s2", cntrctNo)
                                , String.format("C%s.s3", cntrctNo)
                                , String.format("C%s.s4", cntrctNo)
                                , String.format("C%s.s5", cntrctNo)
                                , String.format("C%s.l1", cntrctNo)
                                , String.format("C%s.l2", cntrctNo)
                        });
                        this.executeAddNavigation(params);
                    } else {
                        throw new GaiaBizException(ErrorType.ETC, String.format("[code 06] 계약번호 %s의 기본 데이터가 존재하지 않습니다.", cntrctNo));
                    }
                } else {
                    params.put("deptId", upDeptId);

                    checkCount = this.getDepartmentCount(params);

                    if (checkCount > 0) {
                        Map<String, Object> department = Maps.newHashMap(BASE_DEPARTMENT);
                        department.put("corpNo", corpNo);
                        department.put("pjtNo", pjtNo);
                        department.put("cntrctNo", cntrctNo);
                        department.put("pjtType", "CAIROS");
                        department.put("deptId", deptId);
                        department.put("upDeptId", "C");
                        department.put("whereDeptLvl", 1);
                        department.put("wherePjtType", "GAIA");
                        department.put("whereDeptId", String.format("G%s", pjtNo));

                        this.addDepartmentForSelect(params);

                        department = Maps.newHashMap(BASE_DEPARTMENT);
                        department.put("corpNo", corpNo);
                        department.put("pjtNo", pjtNo);
                        department.put("cntrctNo", cntrctNo);
                        department.put("pjtType", "CAIROS");
                        department.put("deptId", deptId);
                        department.put("deptNm", itemName);
                        department.put("deptDscrpt", itemDesc);
                        department.put("upDeptId", upDeptId);
                        department.put("whereUpDeptId", upDeptId);
                        department.put("deptLvl", 2);
                        department.put("dsplyOrdr", "MAX");
                        department.put("svrType", null);
                        department.put("deptYn", "N");
                        department.put("deptUuid", UUID.randomUUID().toString());

                        this.addDepartment(params);

                        params.put("cntrctNo", cntrctNo);
                        params.put("deptId", String.format("C%s", cntrctNo));

                        checkCount = this.getDepartmentCount(params);

                        if (checkCount > 0) {
                            List<Map<String, Object>> subDepartmentList = this.makeCairosSubDeptList(corpNo, pjtNo, cntrctNo, deptId);

                            this.executeAddDepartment(subDepartmentList, "CAIROS", pjtNo, cntrctNo, deptId, new String[]{
                                    String.format("C%s.m1", cntrctNo)
                                    , String.format("C%s.m2", cntrctNo)
                                    , String.format("C%s.m3", cntrctNo)
                                    , String.format("C%s.s1", cntrctNo)
                                    , String.format("C%s.s2", cntrctNo)
                                    , String.format("C%s.s3", cntrctNo)
                                    , String.format("C%s.s4", cntrctNo)
                                    , String.format("C%s.s5", cntrctNo)
                                    , String.format("C%s.l1", cntrctNo)
                                    , String.format("C%s.l2", cntrctNo)
                            });
                            this.executeAddNavigation(params);
                        } else {
                            throw new GaiaBizException(ErrorType.ETC, String.format("[code 07] 계약번호 %s의 기본 데이터가 존재하지 않습니다.", cntrctNo));
                        }
                    } else {
                        throw new GaiaBizException(ErrorType.ETC, String.format("[code 08] 프로젝트번호 %s의 기본 데이터가 존재하지 않습니다.", pjtNo));
                    }
                }
            }
        }
    }

    public void removeProject(String pjtType, String pjtNo, String cntrctNo) {
        if (StringUtils.isEmpty(pjtNo) && StringUtils.isEmpty(pjtType)) {
            throw new GaiaBizException(ErrorType.ETC, "INVALID PARAMETERS");
        }

        if (!StringUtils.equals(pjtType, "P") && StringUtils.isEmpty(cntrctNo)) {
            throw new GaiaBizException(ErrorType.ETC, "INVALID PARAMETERS");
        }

        Map<String, Object> params = Maps.newHashMap();
        params.put("pjtType", pjtType);
        params.put("pjtNo", pjtNo);
        params.put("cntrctNo", cntrctNo);
        params.put("dltYn", "Y");

        this.removeDepartment(params);
        this.removeNavigation(params);
    }

    public void addDepartment(DepartmentForm.DepartmentCreate departmentCreate, String userId){
        List<Map<String, Object>> subDepartmentList = Lists.newArrayList();

        Map<String, Object> department = Maps.newHashMap();

        // 기본 부서 데이터
        department.put("corpNo", departmentCreate.getCorpNo());
        department.put("pjtNo", departmentCreate.getPjtNo());
        department.put("cntrctNo", departmentCreate.getCntrctNo());
        department.put("pjtType", departmentCreate.getPjtType());
        department.put("deptId", departmentCreate.getDeptId());
        department.put("deptNm", departmentCreate.getDeptNm());
        department.put("deptDscrpt", departmentCreate.getDeptDscrpt());
        department.put("upDeptId", departmentCreate.getUpDeptId());
        department.put("deptLvl", departmentCreate.getDeptLvl());
        department.put("pstnNm", departmentCreate.getPstnNm());
        department.put("mngNm", departmentCreate.getMngNm());
        department.put("useYn", departmentCreate.getUseYn());
        department.put("svrType", departmentCreate.getSvrType());
        department.put("deptUuid",departmentCreate.getDeptUuid());

        // 추가 부서 데이터
        department.put("rgstrId", userId);
        department.put("chgId", userId);
        department.put("deptYn", "Y");
        department.put("whereUpDeptId", departmentCreate.getUpDeptId());
        department.put("dsplyOrdr", "MAX");

        department.put("skipMenuAuth", "Y"); // 해당 부서의 권한그룹 메뉴 권한 생성 skip 여부

        subDepartmentList.add(department);

        this.executeAddDepartment(subDepartmentList,
            departmentCreate.getPjtType(), departmentCreate.getPjtNo(), departmentCreate.getCntrctNo(),
            departmentCreate.getDeptId(), new String[]{ departmentCreate.getDeptId() });
    }

    private List<DocumentForm.NavigationCreate> buildSubNavPayload(String cntrctNo, String pjtNo, String upNaviId, Integer upNaviNo) {
        //공문_감리, 공문_시공사 네비게이션 생성.
        List<DocumentForm.NavigationCreate> subNaviList = new ArrayList<>();

        //공문_감리
        DocumentForm.NavigationCreate superNavi = new DocumentForm.NavigationCreate();
        superNavi.setPjtNo(pjtNo);
        superNavi.setCntrctNo(cntrctNo);
        superNavi.setUpNaviId(upNaviId);
        superNavi.setUpNaviNo(upNaviNo);
        superNavi.setNaviDiv("01");
        superNavi.setNaviId(UUID.randomUUID().toString());
        superNavi.setNaviNm("공문_감리");
        superNavi.setNaviPath("공문_감리");
        superNavi.setNaviType("ITEM");
        superNavi.setNaviLevel((short) 1);
        superNavi.setSvrType("05"); //역할 : 감리

        subNaviList.add(superNavi);

        //공문_시공사
        DocumentForm.NavigationCreate constructNavi = new DocumentForm.NavigationCreate();
        constructNavi.setPjtNo(pjtNo);
        constructNavi.setCntrctNo(cntrctNo);
        constructNavi.setUpNaviId(upNaviId);
        constructNavi.setUpNaviNo(upNaviNo);
        constructNavi.setNaviDiv("01");
        constructNavi.setNaviId(UUID.randomUUID().toString());
        constructNavi.setNaviNm("공문_시공사");
        constructNavi.setNaviPath("공문_시공사");
        constructNavi.setNaviType("ITEM");
        constructNavi.setNaviLevel((short) 1);
        constructNavi.setSvrType("01"); //역할 : 현장소장(시공사)

        subNaviList.add(constructNavi);

        return subNaviList;
    }

    private void createInitSubDcNavigationList(String cntrctNo, String pjtNo, String upNaviId, Integer upNaviNo) {
        List<DocumentForm.NavigationCreate> saveNaviList =
                buildSubNavPayload(cntrctNo, pjtNo, upNaviId, upNaviNo);

        // 5-3. 만들어진 navi 객체리스트 documentServiceClient 호출하여 저장 후 반환.
        // 루트 네비 생성
        scheduleSubNavCreateAfterCommit(saveNaviList);

    }

    // 2) 루트 네비 생성만 afterCommit에 등록
    private void scheduleSubNavCreateAfterCommit(List<DocumentForm.NavigationCreate> saveNaviList) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override public void afterCommit() {
                documentServiceClient.createNavigationList(saveNaviList);
            }
        });
    }

//    public void addSubNavigation(Map<String, Object> params) {
//        // 기초데이터 관리 > 네비게이션 폴더 종류 데이터 조회
//        List<Map<String, Object>> navifolderTypeList = this.getNaviFolderTypeListFromComCode(params);
//
//        List<DocumentForm.NavigationCreate> saveNaviList = new ArrayList<>();
//
//        if(navifolderTypeList != null) {
//            for (Map<String, Object> folderType : navifolderTypeList) {// 저장할 네비게이션 객체 생성.
//                final String naviId = String.format("%s_%s_%s_%s", "nav", params.get("cntrctNo"), folderType.get("folder_kind"), params.get("naviDiv").toString());
//
//                DocumentForm.NavigationCreate navigation = new DocumentForm.NavigationCreate();
//                navigation.setNaviId(naviId);
//                navigation.setNaviNm(folderType.get("folder_type_nm").toString());
//                navigation.setUpNaviId(params.get("upNaviId").toString());
//                navigation.setUpNaviNo((Integer) params.get("upNaviNo"));
//                navigation.setNaviFolderType(folderType.get("cmn_cd").toString());
//                navigation.setCntrctNo(params.get("cntrctNo").toString());
//                navigation.setPjtNo(params.get("pjtNo").toString());
//                navigation.setNaviDiv(params.get("naviDiv").toString());
//                navigation.setNaviPath(folderType.get("folder_type_nm").toString());
//                navigation.setNaviLevel((short) 1);
//                navigation.setNaviType("FOLDR");
//                navigation.setRgstrId("SYSTEM");
//                navigation.setChgId("SYSTEM");
//
//                navigation.setProperties(commonCodeService.createPropertyListForCommonCode(CommonCodeConstants.DOCUMENT_NAVI_FOLDER_TYPE_GROUP_CODE, folderType.get("cmn_cd").toString(), naviId));
//
//                saveNaviList.add(navigation);
//            }
//        }
//
//        // api 호출
//        Result result = documentServiceClient.createNavigationList(saveNaviList);
//
//        if(!result.isOk()) {
//            throw new GaiaBizException(ErrorType.INTERNAL_SERVER_ERROR);
//        }
//
////        throw new RuntimeException("트랜잭션 롤백 테스트");
//    }
}
