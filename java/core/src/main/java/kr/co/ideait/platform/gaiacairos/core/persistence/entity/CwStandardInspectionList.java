package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import jakarta.persistence.*;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@IdClass(CwStandardInspectionListId.class)
public class CwStandardInspectionList extends AbstractRudIdTime {

    @Id
    @Column(name = "cntrct_no")
    @Description(name = "계약번호", description = "", type = Description.TYPE.FIELD)
    String cntrctNo; // 계약번호

    @Id
    @Column(name = "isp_lst_id", length = 36)
    @Description(name = "점검리스트 ID", description = "", type = Description.TYPE.FIELD)
    String ispLstId; // 점검리스트 ID

    @Description(name = "공종 여부", description = "", type = Description.TYPE.FIELD)
    String cnsttyYn; // 공종 여부

    @Description(name = "공종코드", description = "", type = Description.TYPE.FIELD)
    String cnsttyCd; // 공종코드

    @Description(name = "공종명", description = "", type = Description.TYPE.FIELD)
    String cnsttyNm; // 공종명

    @Description(name = "레벨", description = "", type = Description.TYPE.FIELD)
    short cnsttyLvl; // 레벨

    @Description(name = "상위공종코드", description = "", type = Description.TYPE.FIELD)
    String upCnsttyCd; // 상위공종코드

    @Description(name = "점검항목 번호", description = "", type = Description.TYPE.FIELD)
    Integer ispLstSno; // 점검항목 번호

    @Description(name = "점검항목", description = "", type = Description.TYPE.FIELD)
    String ispLstDscrpt; // 점검항목

    @Description(name = "삭제여부", description = "", type = Description.TYPE.FIELD)
    String dltYn; // 삭제여부
}
