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
public class DcPropertyData extends AbstractRuIdTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Description(name = "속성데이타 No", description = "", type = Description.TYPE.FIELD)
    Integer attrbtNo;

    @Description(name = "문서 No", description = "", type = Description.TYPE.FIELD)
    Integer docNo;

    @Description(name = "문서 ID", description = "", type = Description.TYPE.FIELD)
    String docId;

    @Description(name = "속성 코드", description = "", type = Description.TYPE.FIELD)
    String attrbtCd;

    @Description(name = "속성 내용", description = "", type = Description.TYPE.FIELD)
    String attrbtCntnts;
}
