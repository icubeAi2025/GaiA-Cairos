package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@IdClass(CnContractOrgId.class)
public class CnContractOrg extends AbstractRudIdTime {

    @Id
    @Description(name = "계약번호", description = "", type = Description.TYPE.FIELD)
    String cntrctNo; // 계약번호

    @Id
    @Column(columnDefinition = "NUMERIC")
    @Description(name = "계약조직도ID", description = "", type = Description.TYPE.FIELD)
    Integer cntrctOrgId; // 계약조직도ID

    @Description(name = "공종코드", description = "", type = Description.TYPE.FIELD)
    String cnsttyCd; // 공종코드
    @Description(name = "업체번호", description = "", type = Description.TYPE.FIELD)
    String corpNo; // 업체번호
    @Description(name = "사업자등록번호", description = "", type = Description.TYPE.FIELD)
    String bsnsmnNo; // 사업자등록번호
    @Description(name = "업체명", description = "", type = Description.TYPE.FIELD)
    String corpNm; // 업체명
    @Description(name = "담당구분", description = "", type = Description.TYPE.FIELD)
    String ofclType; // 담당구분
    @Description(name = "담당자ID", description = "", type = Description.TYPE.FIELD)
    String ofclId; // 담당자ID
    @Description(name = "담당자명", description = "", type = Description.TYPE.FIELD)
    String ofclNm; // 담당자명
    @Description(name = "휴대전화번호", description = "", type = Description.TYPE.FIELD)
    String telNo; // 휴대전화번호
    @Description(name = "이메일", description = "", type = Description.TYPE.FIELD)
    String email; // 이메일
    @Description(name = "직책", description = "", type = Description.TYPE.FIELD)
    String pstn; // 직책
    @Description(name = "사용여부", description = "", type = Description.TYPE.FIELD)
    String useYn; // 사용여부
    @Description(name = "삭제여부", description = "", type = Description.TYPE.FIELD)
    String dltYn;
}
