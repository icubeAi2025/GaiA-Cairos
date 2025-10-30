package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@IdClass(DmDesignScheduleId.class)
public class DmDesignSchedule extends AbstractRudIdTime {
    @Id
    @Description(name = "설계단계 번호", description = "", type = Description.TYPE.FIELD)
    String dsgnPhaseNo;

    @Id
    @Description(name = "계약번호", description = "", type = Description.TYPE.FIELD)
    String cntrctNo;

    @Id
    @Description(name = "설계단계 코드", description = "", type = Description.TYPE.FIELD)
    String dsgnPhaseCd;

    @Description(name = "시작일", description = "", type = Description.TYPE.FIELD)
    LocalDateTime bgnDate;

    @Description(name = "종료일", description = "", type = Description.TYPE.FIELD)
    LocalDateTime endDate;

    @Description(name = "삭제여부", description = "", type = Description.TYPE.FIELD)
    String dltYn;
}
