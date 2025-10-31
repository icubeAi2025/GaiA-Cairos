package kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.department;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmDepartment;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmOrganization;
import kr.co.ideait.platform.gaiacairos.core.config.GlobalMapperConfig;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.CommonForm;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper(config = GlobalMapperConfig.class)
public interface DepartmentForm {

    SmDepartment toSmDepartment(DepartmentCreate departmentCreate);

    SmDepartment toSmDepartment(DepartmentUpdate departmentCreate);

    void updateSmDepartment(DepartmentUpdate department, @MappingTarget SmDepartment smDepartment);

    List<SmOrganization> toSmOrganizationList(List<DepartmentUserCreate> departmentUserCreateList);

    void updateSmOrganization(DepartmentUserUpdate departmentUserUpdate, @MappingTarget SmOrganization smOrganization);

    @Data
    class DepartmentGet {
        @NotBlank
        String cntrctNo;
        @NotBlank
        String pjtNo;

        String deptId;

        String columnNm;
        String text;

        // 권한그룹관리에서 사용
        String pjtType;
        List<String> deptIdList;
        String cntrctYn;
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    class DepartmentCreate extends CommonForm {
        String corpNo;
        @NotBlank
        String pjtNo;
        @NotBlank
        String cntrctNo;
//        @NotBlank
        String pjtType;
        @NotBlank
        @Size(max = 20)
        String deptId;
        @NotBlank
        String deptNm;
        String deptDscrpt;
        String upDeptId;
        String pstnNm;
        String mngNm;
        @NotNull
        Short deptLvl;
        @NotBlank
        String useYn;
        String svrType;
        String deptUuid;
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    class DepartmentUpdate extends CommonForm {
        @NotNull
        Integer deptNo;
        @NotBlank
        String deptNm;
        @NotBlank
        String useYn;
        String svrType;
        String deptDscrpt;
        String pstnNm;
        String mngNm;
        String deptUuid;
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    class DepartmentDelete extends CommonForm {
        // @NotNull
        // List<Integer> deptNoList;
        @NotNull
        List<String> deptIdList;

        @NotNull
        String pjtNo;
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    class DepartmentUserCreate extends CommonForm {
        @NotNull
        Integer deptNo;
        @NotBlank
        String usrId;
        @NotBlank
        String loginId;
        @NotBlank
        String ratngCd;
        @NotBlank
        String pstnCd;
        @NotBlank
        String flag;
        String corpNo;
        LocalDateTime startDt;
        LocalDateTime endDt;
        String deptUuid;
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    class DepartmentUserCreateList extends CommonForm {
        @NotNull
        List<DepartmentUserCreate> departmentUserCreateList;

        @NotNull
        String pjtNo;
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    class DepartmentUserUpdate extends CommonForm {
        @NotNull
        Integer orgNo;
        String ratngCd;
        String pstnCd;
        String flag;
        LocalDateTime startDt;
        LocalDateTime endDt;

        @NotNull
        String pjtNo;
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    class DepartmentUserDeleteList extends CommonForm {
        @NotNull
        List<Integer> orgNoList;
    }

    // 사용자검색에서 사용
    DepartmentMybatisParam.GetEmployeeListInput toEmployeeList(EmployeeListInput employeeListInput);

    @Data
    class EmployeeListInput extends CommonForm {
        List<Map<String, Integer>> deptNoList;
        String searchText;
    }
}
