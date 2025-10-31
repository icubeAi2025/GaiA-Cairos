package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class DtDeficiencyPhase extends AbstractRudIdTime {

    @Id
    @Description(name = "결함단계 번호", description = "", type = Description.TYPE.FIELD)
    String dfccyPhaseNo;

    @Description(name = "계약번호", description = "", type = Description.TYPE.FIELD)
    String cntrctNo;

    @Description(name = "결함 단계 이름", description = "", type = Description.TYPE.FIELD)
    String dfccyPhaseNm;

    @Description(name = "결함 단계 순번", description = "", type = Description.TYPE.FIELD)
    Short dsplyOrdr;

    @Description(name = "삭제여부", description = "", type = Description.TYPE.FIELD)
    String dltYn;
}