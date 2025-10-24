package kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.department;

import javax.annotation.processing.Generated;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmDepartment;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmOrganization;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisOutput;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:17+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class DepartmentDtoImpl implements DepartmentDto {

    @Override
    public DepartmentTree toDepartmentTree(MybatisOutput mybatisOutput) {
        if ( mybatisOutput == null ) {
            return null;
        }

        DepartmentTree departmentTree = new DepartmentTree();

        for ( java.util.Map.Entry<String, Object> entry : mybatisOutput.entrySet() ) {
            String key = entry.getKey();
            Object value = entry.getValue();
            departmentTree.put( key, value );
        }

        return departmentTree;
    }

    @Override
    public Department toDepartment(SmDepartment smDepartment) {
        if ( smDepartment == null ) {
            return null;
        }

        Department department = new Department();

        department.setDeptNo( smDepartment.getDeptNo() );
        department.setCorpNo( smDepartment.getCorpNo() );
        department.setPjtNo( smDepartment.getPjtNo() );
        department.setCntrctNo( smDepartment.getCntrctNo() );
        department.setPjtType( smDepartment.getPjtType() );
        department.setDeptId( smDepartment.getDeptId() );
        department.setDeptNm( smDepartment.getDeptNm() );
        department.setDeptDscrpt( smDepartment.getDeptDscrpt() );
        department.setUpDeptId( smDepartment.getUpDeptId() );
        department.setDeptLvl( smDepartment.getDeptLvl() );
        department.setDsplyOrdr( smDepartment.getDsplyOrdr() );
        department.setPstnNm( smDepartment.getPstnNm() );
        department.setMngNm( smDepartment.getMngNm() );
        department.setUseYn( smDepartment.getUseYn() );
        department.setDltYn( smDepartment.getDltYn() );
        department.setDsplyYn( smDepartment.getDsplyYn() );
        department.setSvrType( smDepartment.getSvrType() );
        department.setDeptUuid( smDepartment.getDeptUuid() );

        return department;
    }

    @Override
    public DepartmentEmploee toDepartmentEmploee(MybatisOutput mybatisOutput) {
        if ( mybatisOutput == null ) {
            return null;
        }

        DepartmentEmploee departmentEmploee = new DepartmentEmploee();

        for ( java.util.Map.Entry<String, Object> entry : mybatisOutput.entrySet() ) {
            String key = entry.getKey();
            Object value = entry.getValue();
            departmentEmploee.put( key, value );
        }

        return departmentEmploee;
    }

    @Override
    public DepartmentUserCreateResult fromSmOrganization(SmOrganization smOrganization) {
        if ( smOrganization == null ) {
            return null;
        }

        DepartmentUserCreateResult departmentUserCreateResult = new DepartmentUserCreateResult();

        departmentUserCreateResult.setOrgNo( smOrganization.getOrgNo() );
        departmentUserCreateResult.setDeptNo( smOrganization.getDeptNo() );
        departmentUserCreateResult.setUsrId( smOrganization.getUsrId() );
        departmentUserCreateResult.setLoginId( smOrganization.getLoginId() );

        return departmentUserCreateResult;
    }

    @Override
    public EmployeeDetail toEmployeeDetail(MybatisOutput mybatisOutput) {
        if ( mybatisOutput == null ) {
            return null;
        }

        EmployeeDetail employeeDetail = new EmployeeDetail();

        for ( java.util.Map.Entry<String, Object> entry : mybatisOutput.entrySet() ) {
            String key = entry.getKey();
            Object value = entry.getValue();
            employeeDetail.put( key, value );
        }

        return employeeDetail;
    }

    @Override
    public OrganizationEmployee toOrganizationEmployee(MybatisOutput mybatisOutput) {
        if ( mybatisOutput == null ) {
            return null;
        }

        OrganizationEmployee organizationEmployee = new OrganizationEmployee();

        return organizationEmployee;
    }

    @Override
    public Organization toOrganization(MybatisOutput mybatisOutput) {
        if ( mybatisOutput == null ) {
            return null;
        }

        Organization organization = new Organization();

        for ( java.util.Map.Entry<String, Object> entry : mybatisOutput.entrySet() ) {
            String key = entry.getKey();
            Object value = entry.getValue();
            organization.put( key, value );
        }

        return organization;
    }
}
