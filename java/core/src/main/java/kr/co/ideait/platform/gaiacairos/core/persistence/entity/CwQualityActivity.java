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
@IdClass(CwQualityActivityId.class)
public class CwQualityActivity extends AbstractRudIdTime {
    @Id
    @Column(name = "qlty_isp_id")
    @Description(name = "품질검측 ID", description = "", type = Description.TYPE.FIELD)
    String qltyIspId; // 품질검측번호

    @Id
    @Column(name = "wbs_cd")
    @Description(name = "WBS코드", description = "", type = Description.TYPE.FIELD)
    String wbsCd; // WBS코드

    @Id
    @Column(name = "activity_id")
    @Description(name = "Activity ID", description = "", type = Description.TYPE.FIELD)
    String activityId; // ActivityID

    @Description(name = "삭제여부", description = "", type = Description.TYPE.FIELD)
    String dltYn; // 삭제여부
}
