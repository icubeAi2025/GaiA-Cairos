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
@IdClass(DtDeficiencyActivityId.class)
public class DtDeficiencyActivity extends AbstractRudIdTime {
    @Id
    @Column(name = "dfccy_no")
    @Description(name = "결함번호", description = "", type = Description.TYPE.FIELD)
    String dfccyNo; // 결함번호

    @Id
    @Column(name = "wbs_cd")
    @Description(name = "WBS코드", description = "", type = Description.TYPE.FIELD)
    String wbsCd; // WBS코드

    @Id
    @Column(name = "cntrct_no")
    @Description(name = "계약번호", description = "", type = Description.TYPE.FIELD)
    String cntrctNo; // ActivityID

    @Id
    @Column(name = "activity_id")
    @Description(name = "ActivityID", description = "", type = Description.TYPE.FIELD)
    String activityId; // ActivityID

    @Description(name = "삭제여부", description = "", type = Description.TYPE.FIELD)
    String dltYn; // 삭제여부
}
