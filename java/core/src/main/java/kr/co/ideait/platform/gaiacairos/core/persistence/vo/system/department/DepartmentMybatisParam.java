package kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.department;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.type.Alias;

import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisPageable;
import lombok.Data;
import lombok.EqualsAndHashCode;

public interface DepartmentMybatisParam {

    @Data
    @Alias("createDepartmentPjtContInput")
    @EqualsAndHashCode(callSuper = true)
    public class createDepartmentPjtContInput extends MybatisPageable {
        String pjtNo;
        String cntrctNo;
        String pjtNm;
        String cmnGrpCd;
        String pjtType;
        String insertType;
        String corpNo;
        String pjtDscrpt;
    }

    @Data
    @Alias("createDepartmentInput")
    public class createDepartmentInput {
        String corpNo;
        String pjtNo;
        String cntrctNo;
        String pjtType;
        String deptId;
        String deptNm;
        String deptDscrpt;
        String upDeptId;
        String pstnNm;
        String mngNm;
        Short deptLvl;
        String useYn;
        String svrType;
    }

    @Data
    @Alias("getEmployeeDetailsInput")
    public class GetEmployeeDetailsInput {
        String employeeId;
        String lang;
        String pjtNo;
        String cntrctNo;
        String gaiaType;
    }

    @Data
    @Alias("getDepartmentInput")
    public class GetDepartmentInput {
        String loginId;
        String pjtNo;
        String cntrctNo;
    }

    @Data
    @Alias("getEmployeeListInput")
    public class GetEmployeeListInput extends MybatisPageable {
        Integer departmentNo;
        String lang;

        String columnNm;
        String text;
        List<String> deptIdList;

        List<Map<String, Integer>> deptNoList;
        String searchText;
    }

    @Data
    @Alias("getOranizationEmployeeListInput")
    public class GetOranizationEmployeeListInput {
        String deptId;

        String lang;

        String pjtNo;
        String cntrctNo;

        String columnNm;
        String text;

        String pstnCd; // 직책 공통 코드
        String ratngCd; // 직급 공통 코드

    }

    @Data
    @Alias("getAuthUsersDepartmentInput")
    public class GetAuthUsersDepartmentInput {
        String deptId;
        List<String> deptIdList;
    }

}