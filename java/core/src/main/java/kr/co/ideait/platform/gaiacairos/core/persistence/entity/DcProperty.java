package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class DcProperty extends AbstractRudIdTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Description(name = "속성 No", description = "", type = Description.TYPE.FIELD)
    Integer attrbtNo;

    @Description(name = "네비게이션 No", description = "", type = Description.TYPE.FIELD)
    Integer naviNo;

    @Description(name = "네비게이션 ID", description = "", type = Description.TYPE.FIELD)
    String naviId;

    @Description(name = "속성 코드", description = "", type = Description.TYPE.FIELD)
    String attrbtCd;

    @Description(name = "속성 코드 종류", description = "", type = Description.TYPE.FIELD)
    String attrbtCdType;

    @Description(name = "속성 종류", description = "", type = Description.TYPE.FIELD)
    String attrbtType;

    @Description(name = "속성 종류 선택", description = "", type = Description.TYPE.FIELD)
    String attrbtTypeSel;

    @Description(name = "속성 이름 영어", description = "", type = Description.TYPE.FIELD)
    String attrbtNmEng;

    @Description(name = "속성 이름 한글", description = "", type = Description.TYPE.FIELD)
    String attrbtNmKrn;

    @Description(name = "속성 순번", description = "", type = Description.TYPE.FIELD)
    Short attrbtDsplyOrder;

    @Description(name = "속정 표시 여부", description = "", type = Description.TYPE.FIELD)
    String attrbtDsplyYn;

    @Description(name = "속성 수정 여부", description = "", type = Description.TYPE.FIELD)
    String attrbtChgYn;

    @Description(name = "삭제여부", description = "", type = Description.TYPE.FIELD)
    String dltYn;
}
