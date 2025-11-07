package kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.department;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmDepartment;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.SmOrganization;
import kr.co.ideait.platform.gaiacairos.core.config.GlobalMapperConfig;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MapDto;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisOutput;
import lombok.Data;
import org.mapstruct.Mapper;

import java.time.LocalDateTime;

@Mapper(config = GlobalMapperConfig.class)
public interface DepartmentDto {

    DepartmentTree toDepartmentTree(MybatisOutput mybatisOutput);

    Department toDepartment(SmDepartment smDepartment);

    DepartmentEmploee toDepartmentEmploee(MybatisOutput mybatisOutput);

    DepartmentUserCreateResult fromSmOrganization(SmOrganization smOrganization);

    EmployeeDetail toEmployeeDetail(MybatisOutput mybatisOutput);

    OrganizationEmployee toOrganizationEmployee(MybatisOutput mybatisOutput);

    Organization toOrganization(MybatisOutput mybatisOutput);

    class EmployeeDetail extends MapDto {
        String usrId;
        String loginId;
        String usrNm;
        String ratngCd;
        String pstnCd;
        String phoneNo;
        String telNo;
        String emailAdrs;
        String useYn;
        Integer deptNo;
        String pjtType;
        String deptNm;
        String cntrctNm;
        String pstnNm;
        String ratngNm;
        String flagNm;
    }

    class DepartmentTree extends MapDto {

        Integer deptNo;
        // String corpNo;
        // String pjtNo;
        // String cntrctNo;
        // String pjtType;
        String deptId;
        String deptNm;
        // String deptDscrpt;
        String upDeptId;
        Short deptLvl;
        // Short dsplyOrdr;
        // String pstnNm;
        // String mngNm;
        // String useYn;
        // String dltYn;
        String dsplyYn;
        LocalDateTime chgDt;

        // companny
        String compNm;
    }

    class DepartmentEmploee extends MapDto {

        Integer orgNo;
        Integer deptNo;
        String deptId;
        String deptNm;
        String compNm;
        String loginId;
        String usrId;
        String usrNm;
        String ratngNm;
        String pstnNm;
        String ratngCd;
        String pstnCd;
        String flag;
        String deptPstnNm;
        String useYn;
        String telNo;
        String phoneNo;
        String emailAdrs;
        String flagNm;
        LocalDateTime startDt;
        LocalDateTime endDt;
        LocalDateTime chgDt;
    }

    @Data
    class Department {
        Integer deptNo;
        String corpNo;
        String pjtNo;
        String cntrctNo;
        String pjtType;
        String deptId;
        String deptNm;
        String deptDscrpt;
        String upDeptId;
        Short deptLvl;
        Short dsplyOrdr;
        String pstnNm;
        String mngNm;
        String useYn;
        String dltYn;
        String dsplyYn;
        String svrType;
        String deptUuid;
    }

    @Data
    class DepartmentUserCreateResult {
        Integer orgNo;
        Integer deptNo;
        String usrId;
        String loginId;
    }

    @Data
    class OrganizationEmployee {
        String usrId;
        String loginId;
        String usrNm;
        String ratngCd;
        String pstnCd;
        String useYn;
        String dltId;
        String flag;
        String pstnNm;
        String ratngNm;
        String flagNm;
    }

    @Data
    class Organization extends MapDto{
        Integer orgNo;
        String loginId;
        String usrNm;
        String deptNm;
    }
}
