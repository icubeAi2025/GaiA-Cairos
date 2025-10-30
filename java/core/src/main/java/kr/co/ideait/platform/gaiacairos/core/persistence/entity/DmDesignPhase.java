package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Alias("dmDesignPhase")
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class DmDesignPhase extends AbstractRudIdTime {

    @Id
    @Description(name = "설계단계 번호", description = "", type = Description.TYPE.FIELD)
    String dsgnPhaseNo;

    @Description(name = "계약번호", description = "", type = Description.TYPE.FIELD)
    String cntrctNo;

    @Description(name = "설계단계 이름", description = "", type = Description.TYPE.FIELD)
    String dsgnPhaseNm;

    @Description(name = "설계 단계 순번", description = "", type = Description.TYPE.FIELD)
    Short dsplyOrdr;

    @Description(name = "삭제여부", description = "", type = Description.TYPE.FIELD)
    String dltYn;
}
