package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

import java.time.LocalDateTime;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Alias("cnContract")
public class CnContract extends AbstractRudIdTime {

    @Description(name = "프로젝트번호", description = "", type = Description.TYPE.FIELD)
    String pjtNo; // 프로젝트번호

    @Id
    @Description(name = "계약번호", description = "", type = Description.TYPE.FIELD)
    String cntrctNo; // 계약번호

    @Description(name = "관리계약번호", description = "", type = Description.TYPE.FIELD)
    String mngCntrctNo; // 관리계약번호

    @Description(name = "공사계약명", description = "", type = Description.TYPE.FIELD)
    String cntrctNm; // 공사계약명

    @Description(name = "계약종류", description = "", type = Description.TYPE.FIELD)
    String cntrctType; // 계약종류

    @Description(name = "계약구분 코드", description = "", type = Description.TYPE.FIELD)
    String cntrctDivCd; // 계약구분 코드

    @Description(name = "주공종코드", description = "", type = Description.TYPE.FIELD)
    String majorCnsttyCd; // 주공종코드

    @Description(name = "계약일자", description = "", type = Description.TYPE.FIELD)
    String cntrctDate; // 계약일자

    @Description(name = "보증일", description = "", type = Description.TYPE.FIELD)
    String grntyDate; // 보증일

    @Description(name = "착공일자", description = "", type = Description.TYPE.FIELD)
    String cbgnDate; // 착공일자

    @Description(name = "준공일자", description = "", type = Description.TYPE.FIELD)
    String ccmpltDate; // 준공일자

    @Column(name = "con_prd", columnDefinition = "NUMERIC")
    @Description(name = "공사기간", description = "", type = Description.TYPE.FIELD)
    double conPrd; // 공사기간

    @Description(name = "금차준공일자", description = "", type = Description.TYPE.FIELD)
    String thisCcmpltDate; // 금차준공일자

    @Column(name = "this_con_prd", columnDefinition = "NUMERIC")
    @Description(name = "금차공사기간", description = "", type = Description.TYPE.FIELD)
    double thisConPrd; // 금차공사기간

    @Column(name = "cntrct_cost", columnDefinition = "NUMERIC")
    @Description(name = "계약금액", description = "", type = Description.TYPE.FIELD)
    double cntrctCost; // 계약금액

    @Column(name = "this_cntrct_cost", columnDefinition = "NUMERIC")
    @Description(name = "금차계약금액", description = "", type = Description.TYPE.FIELD)
    double thisCntrctCost; // 금차계약금액

    @Column(name = "grnty_cost", columnDefinition = "NUMERIC")
    @Description(name = "보증금", description = "", type = Description.TYPE.FIELD)
    double grntyCost; // 보증금

    @Column(name = "vat_rate", columnDefinition = "NUMERIC")
    @Description(name = "부가세율", description = "", type = Description.TYPE.FIELD)
    double vatRate; // 부가세율

    @Column(name = "dfrcmpnst_rate", columnDefinition = "NUMERIC")
    @Description(name = "지체상금율", description = "", type = Description.TYPE.FIELD)
    double dfrcmpnstRate; // 지체상금율

    @Description(name = "업체번호", description = "", type = Description.TYPE.FIELD)
    String corpNo; // 업체번호

    @Description(name = "사업자등록번호", description = "", type = Description.TYPE.FIELD)
    String bsnsmnNo; // 사업자등록번호

    @Description(name = "업체명", description = "", type = Description.TYPE.FIELD)
    String corpNm; // 업체명

    @Description(name = "업체주소", description = "", type = Description.TYPE.FIELD)
    String corpAdrs; // 업체주소

    @Description(name = "전화번호", description = "", type = Description.TYPE.FIELD)
    String telNo; // 전화번호

    @Description(name = "팩스번호", description = "", type = Description.TYPE.FIELD)
    String faxNo; // 팩스번호

    @Description(name = "업체대표자", description = "", type = Description.TYPE.FIELD)
    String corpCeo; // 업체대표자

    @Description(name = "담당자ID", description = "", type = Description.TYPE.FIELD)
    String ofclId; // 담당자ID

    @Description(name = "담당자명", description = "", type = Description.TYPE.FIELD)
    String ofclNm; // 담당자명

    @Description(name = "조직도첨부파일번호", description = "", type = Description.TYPE.FIELD)
    String orgchrtAtchFileNo; // 조직도첨부파일번호

    @Description(name = "삭제여부", description = "", type = Description.TYPE.FIELD)
    String dltYn; // 삭제여부

    @Description(name = "안전사고일", description = "", type = Description.TYPE.FIELD)
    LocalDateTime sftyAcdntDt; // 안전사고일
    // 친환경점수
    // 에너지점수
    // BF점수
}
