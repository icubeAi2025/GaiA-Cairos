package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import jakarta.persistence.*;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class CwCntqltyCheckList extends AbstractRudIdTime {

    @Description(name = "체크 리스트 ID", description = "", type = Description.TYPE.FIELD)
    String cntrctNo; // 계약번호

    @Id
    @Description(name = "체크 리스트 ID", description = "", type = Description.TYPE.FIELD)
    String chklstId; // 체크리스트 ID

    @Description(name = "공종 여부", description = "", type = Description.TYPE.FIELD)
    String cnsttyYn; // 공종 여부

    @Description(name = "공종코드", description = "", type = Description.TYPE.FIELD)
    String cnsttyCd; // 공종코드

    @Description(name = "공종명", description = "", type = Description.TYPE.FIELD)
    String cnsttyNm; // 공종명

    @Description(name = "레벨", description = "", type = Description.TYPE.FIELD)
    Short cnsttyLvl; // 레벨

    @Description(name = "상위공종코드", description = "", type = Description.TYPE.FIELD)
    String upCnsttyCd; // 상위공종코드

    @Description(name = "검사항목 번호", description = "", type = Description.TYPE.FIELD)
    Integer chklstSno; // 검사항목 번호

    @Description(name = "검사항목", description = "", type = Description.TYPE.FIELD)
    String chklstDscrpt; // 검사항목

    @Description(name = "검사기준 코드", description = "", type = Description.TYPE.FIELD)
    String chklstBssCd; // 검사기준 코드

    @Description(name = "삭제여부", description = "", type = Description.TYPE.FIELD)
    String dltYn; // 삭제여부
}
