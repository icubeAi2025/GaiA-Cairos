package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Alias("smDepartment")
public class SmDepartment extends AbstractRudIdTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer deptNo;
    String corpNo;
    String pjtNo;
    String cntrctNo;
    String pjtType;
    String deptId;
    String deptNm;
    String deptYn;
    String deptDscrpt;
    String upDeptId;
    Short deptLvl;
    Short dsplyOrdr;
    String svrType;
    String pstnNm;
    String mngNm;
    String dsplyYn;
    String useYn;
    String dltYn;
    String deptUuid;
}
