package kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.department;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.processing.Generated;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmDepartment;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmOrganization;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-23T18:31:18+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.5 (Amazon.com Inc.)"
)
@Component
public class DepartmentFormImpl implements DepartmentForm {

    @Override
    public SmDepartment toSmDepartment(DepartmentCreate departmentCreate) {
        if ( departmentCreate == null ) {
            return null;
        }

        SmDepartment smDepartment = new SmDepartment();

        smDepartment.setCorpNo( departmentCreate.getCorpNo() );
        smDepartment.setPjtNo( departmentCreate.getPjtNo() );
        smDepartment.setCntrctNo( departmentCreate.getCntrctNo() );
        smDepartment.setPjtType( departmentCreate.getPjtType() );
        smDepartment.setDeptId( departmentCreate.getDeptId() );
        smDepartment.setDeptNm( departmentCreate.getDeptNm() );
        smDepartment.setDeptDscrpt( departmentCreate.getDeptDscrpt() );
        smDepartment.setUpDeptId( departmentCreate.getUpDeptId() );
        smDepartment.setDeptLvl( departmentCreate.getDeptLvl() );
        smDepartment.setPstnNm( departmentCreate.getPstnNm() );
        smDepartment.setMngNm( departmentCreate.getMngNm() );
        smDepartment.setUseYn( departmentCreate.getUseYn() );
        smDepartment.setSvrType( departmentCreate.getSvrType() );
        smDepartment.setDeptUuid( departmentCreate.getDeptUuid() );

        return smDepartment;
    }

    @Override
    public SmDepartment toSmDepartment(DepartmentUpdate departmentCreate) {
        if ( departmentCreate == null ) {
            return null;
        }

        SmDepartment smDepartment = new SmDepartment();

        smDepartment.setDeptNo( departmentCreate.getDeptNo() );
        smDepartment.setDeptNm( departmentCreate.getDeptNm() );
        smDepartment.setDeptDscrpt( departmentCreate.getDeptDscrpt() );
        smDepartment.setPstnNm( departmentCreate.getPstnNm() );
        smDepartment.setMngNm( departmentCreate.getMngNm() );
        smDepartment.setUseYn( departmentCreate.getUseYn() );
        smDepartment.setSvrType( departmentCreate.getSvrType() );
        smDepartment.setDeptUuid( departmentCreate.getDeptUuid() );

        return smDepartment;
    }

    @Override
    public void updateSmDepartment(DepartmentUpdate department, SmDepartment smDepartment) {
        if ( department == null ) {
            return;
        }

        if ( department.getDeptNo() != null ) {
            smDepartment.setDeptNo( department.getDeptNo() );
        }
        if ( department.getDeptNm() != null ) {
            smDepartment.setDeptNm( department.getDeptNm() );
        }
        if ( department.getDeptDscrpt() != null ) {
            smDepartment.setDeptDscrpt( department.getDeptDscrpt() );
        }
        if ( department.getPstnNm() != null ) {
            smDepartment.setPstnNm( department.getPstnNm() );
        }
        if ( department.getMngNm() != null ) {
            smDepartment.setMngNm( department.getMngNm() );
        }
        if ( department.getUseYn() != null ) {
            smDepartment.setUseYn( department.getUseYn() );
        }
        if ( department.getSvrType() != null ) {
            smDepartment.setSvrType( department.getSvrType() );
        }
        if ( department.getDeptUuid() != null ) {
            smDepartment.setDeptUuid( department.getDeptUuid() );
        }
    }

    @Override
    public List<SmOrganization> toSmOrganizationList(List<DepartmentUserCreate> departmentUserCreateList) {
        if ( departmentUserCreateList == null ) {
            return null;
        }

        List<SmOrganization> list = new ArrayList<SmOrganization>( departmentUserCreateList.size() );
        for ( DepartmentUserCreate departmentUserCreate : departmentUserCreateList ) {
            list.add( departmentUserCreateToSmOrganization( departmentUserCreate ) );
        }

        return list;
    }

    @Override
    public void updateSmOrganization(DepartmentUserUpdate departmentUserUpdate, SmOrganization smOrganization) {
        if ( departmentUserUpdate == null ) {
            return;
        }

        if ( departmentUserUpdate.getOrgNo() != null ) {
            smOrganization.setOrgNo( departmentUserUpdate.getOrgNo() );
        }
        if ( departmentUserUpdate.getRatngCd() != null ) {
            smOrganization.setRatngCd( departmentUserUpdate.getRatngCd() );
        }
        if ( departmentUserUpdate.getPstnCd() != null ) {
            smOrganization.setPstnCd( departmentUserUpdate.getPstnCd() );
        }
        if ( departmentUserUpdate.getFlag() != null ) {
            smOrganization.setFlag( departmentUserUpdate.getFlag() );
        }
        if ( departmentUserUpdate.getStartDt() != null ) {
            smOrganization.setStartDt( departmentUserUpdate.getStartDt() );
        }
        if ( departmentUserUpdate.getEndDt() != null ) {
            smOrganization.setEndDt( departmentUserUpdate.getEndDt() );
        }
    }

    @Override
    public DepartmentMybatisParam.GetEmployeeListInput toEmployeeList(EmployeeListInput employeeListInput) {
        if ( employeeListInput == null ) {
            return null;
        }

        DepartmentMybatisParam.GetEmployeeListInput getEmployeeListInput = new DepartmentMybatisParam.GetEmployeeListInput();

        List<Map<String, Integer>> list = employeeListInput.getDeptNoList();
        if ( list != null ) {
            getEmployeeListInput.setDeptNoList( new ArrayList<Map<String, Integer>>( list ) );
        }
        getEmployeeListInput.setSearchText( employeeListInput.getSearchText() );

        return getEmployeeListInput;
    }

    protected SmOrganization departmentUserCreateToSmOrganization(DepartmentUserCreate departmentUserCreate) {
        if ( departmentUserCreate == null ) {
            return null;
        }

        SmOrganization smOrganization = new SmOrganization();

        smOrganization.setDeptNo( departmentUserCreate.getDeptNo() );
        smOrganization.setUsrId( departmentUserCreate.getUsrId() );
        smOrganization.setLoginId( departmentUserCreate.getLoginId() );
        smOrganization.setRatngCd( departmentUserCreate.getRatngCd() );
        smOrganization.setPstnCd( departmentUserCreate.getPstnCd() );
        smOrganization.setFlag( departmentUserCreate.getFlag() );
        smOrganization.setCorpNo( departmentUserCreate.getCorpNo() );
        smOrganization.setStartDt( departmentUserCreate.getStartDt() );
        smOrganization.setEndDt( departmentUserCreate.getEndDt() );
        smOrganization.setDeptUuid( departmentUserCreate.getDeptUuid() );

        return smOrganization;
    }
}
