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
@IdClass(CnContractCompanyId.class)
public class CnContractCompany extends AbstractRudIdTime {

    @Id
    @Description(name = "계약번호", description = "", type = Description.TYPE.FIELD)
    String cntrctNo; // 계약번호

    @Id
    @Column(columnDefinition = "NUMERIC")
    @Description(name = "계약도급 ID", description = "", type = Description.TYPE.FIELD)
    Long cntrctId; // 계약도급 ID

    @Description(name = "공종코드", description = "", type = Description.TYPE.FIELD)
    String cnsttyCd; // 공종코드

    @Description(name = "업체번호", description = "", type = Description.TYPE.FIELD)
    String corpNo; // 업체번호(사업자등록번호로 확인)

    @Description(name = "사업자등록번호", description = "", type = Description.TYPE.FIELD)
    String bsnsmnNo; // 사업자등록번호

    @Description(name = "업체명", description = "", type = Description.TYPE.FIELD)
    String corpNm; // 업체명

    @Description(name = "업체주소", description = "", type = Description.TYPE.FIELD)
    String corpAdrs; // 업체주소

    @Description(name = "사무실번호", description = "", type = Description.TYPE.FIELD)
    String telNo; // 사무실번호

    @Description(name = "팩스번호", description = "", type = Description.TYPE.FIELD)
    String faxNo; // 팩스번호

    @Description(name = "업체대표자", description = "", type = Description.TYPE.FIELD)
    String corpCeo; // 업체대표자

    @Column(columnDefinition = "NUMERIC")
    @Description(name = "지분율", description = "", type = Description.TYPE.FIELD)
    double shreRate; // 지분율

    @Description(name = "대표여부", description = "", type = Description.TYPE.FIELD)
    String rprsYn; // 대표여부

    @Description(name = "담당자명", description = "", type = Description.TYPE.FIELD)
    String ofclNm; // 담당자명

    @Description(name = "담당자ID", description = "", type = Description.TYPE.FIELD)
    String ofclId; // 담당자명 Id

    @Description(name = "삭제여부", description = "", type = Description.TYPE.FIELD)
    String dltYn; // 삭제여부

}
