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
@IdClass(CwSafetyInspectionListId.class)
public class CwSafetyInspectionList extends AbstractRudIdTime {
    @Id
    @Column(name = "cntrct_no")
    @Description(name = "계약번호", description = "", type = Description.TYPE.FIELD)
    String cntrctNo; // 계약번호

    @Id
    @Description(name = "안전점검 번호", description = "", type = Description.TYPE.FIELD)
    String inspectionNo; // 안전점검 번호

    @Id
    @Description(name = "점검 리스트 ID", description = "", type = Description.TYPE.FIELD)
    String ispLstId; // 점검 리스트 ID

    @Column(name = "isp_lst_no", columnDefinition = "int2")
    @Description(name = "점검항목 번호", description = "", type = Description.TYPE.FIELD)
    short ispLstNo; // 점검항목 번호

    @Column(name = "isp_sno", columnDefinition = "int2")
    @Description(name = "순번", description = "", type = Description.TYPE.FIELD)
    short ispSno; // 순번

    @Description(name = "공종명", description = "", type = Description.TYPE.FIELD)
    String cnsttyNm; // 공종명

    @Description(name = "점검내용", description = "", type = Description.TYPE.FIELD)
    String ispDscrpt; // 점검내용

    @Column(name = "gd_flty_yn", columnDefinition = "int2")
    @Description(name = "양호불량 여부", description = "", type = Description.TYPE.FIELD)
    Short gdFltyYn; // 양호불량 여부

    @Description(name = "개선요망사항", description = "", type = Description.TYPE.FIELD)
    String imprvReq; // 개선요망사항

    @Description(name = "삭제여부", description = "", type = Description.TYPE.FIELD)
    String dltYn; // 삭제여부
}
