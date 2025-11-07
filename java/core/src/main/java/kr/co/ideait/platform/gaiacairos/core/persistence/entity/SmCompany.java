package kr.co.ideait.platform.gaiacairos.core.persistence.entity;



import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Alias("smCompany")
public class SmCompany extends AbstractRudIdTime {

    @Id
    String corpNo;
    String compGrpCd;
    String compNm;
    String bsnsmnNo;
    String corpCeo;
    String compDscrpt;
    String pstnNm;
    String mngNm;
    String compTelno;
    String compFaxno;
    String compAdrs;
    String useYn;
    String dltYn;

    String ociCorpNo;
    String ncpCorpNo;
}
