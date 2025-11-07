package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@IdClass(CwPayActivityId.class)
@DynamicUpdate
public class CwPayActivity extends AbstractRudIdTime {

    @Id
    @Description(name = "계약번호", description = "", type = Description.TYPE.FIELD)
    String cntrctNo;

    @Id
    @Column(columnDefinition = "NUMERIC")
    @Description(name = "선급금순번", description = "", type = Description.TYPE.FIELD)
    Long payprceSno;

    @Id
    @Description(name = "Activity ID", description = "", type = Description.TYPE.FIELD)
    String activityId;

    @Column(columnDefinition = "NUMERIC")
    @Description(name = "금회진행률", description = "", type = Description.TYPE.FIELD)
    Long thtmProgrsPt;

    @Column(columnDefinition = "NUMERIC")
    @Description(name = "전회진행률", description = "", type = Description.TYPE.FIELD)
    Long prevProgrsPt;

    @Description(name = "삭제여부", description = "", type = Description.TYPE.FIELD)
    String dltYn;

}
