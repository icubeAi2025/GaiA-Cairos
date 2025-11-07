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
@IdClass(CwDailyReportActivityId.class)
@DynamicUpdate
public class CwDailyReportActivity extends AbstractRudIdTime {

    @Id
    @Description(name = "계약번호", description = "", type = Description.TYPE.FIELD)
    String cntrctNo;

    @Id
    @Column(columnDefinition = "NUMERIC")
    @Description(name = "작업보고ID", description = "", type = Description.TYPE.FIELD)
    Long dailyReportId;
    @Id
    @Description(name = "작업ActivityID", description = "", type = Description.TYPE.FIELD)
    Integer dailyActivityId;

    @Description(name = "WBS코드", description = "", type = Description.TYPE.FIELD)
    String wbsCd;

    @Description(name = "ActivityID", description = "", type = Description.TYPE.FIELD)
    String activityId;

    @Description(name = "Activity명", description = "", type = Description.TYPE.FIELD)
    String activityNm;

    @Description(name = "작업일자구분", description = "", type = Description.TYPE.FIELD)
    String workDtType;

    @Description(name = "계획시작일자", description = "", type = Description.TYPE.FIELD)
    String planBgnDate;

    @Description(name = "계획종료일자", description = "", type = Description.TYPE.FIELD)
    String planEndDate;

    @Column(columnDefinition = "NUMERIC")
    @Description(name = "계획소요일수", description = "", type = Description.TYPE.FIELD)
    Long planReqreDaynum;

    @Description(name = "실행시작일자", description = "", type = Description.TYPE.FIELD)
    String actualBgnDate;

    @Description(name = "실행종료일자", description = "", type = Description.TYPE.FIELD)
    String actualEndDate;

    @Column(columnDefinition = "NUMERIC")
    @Description(name = "실행소요일수", description = "", type = Description.TYPE.FIELD)
    Long actualReqreDaynum;

    @Column(columnDefinition = "NUMERIC")
    @Description(name = "당일계획율", description = "", type = Description.TYPE.FIELD)
    Float todayPlanRate;

    @Column(columnDefinition = "NUMERIC")
    @Description(name = "당일실행율", description = "", type = Description.TYPE.FIELD)
    Float todayExeRate;

    @Description(name = "진행상태코드", description = "", type = Description.TYPE.FIELD)
    String pstats;

    @Description(name = "계약변경ID", description = "", type = Description.TYPE.FIELD)
    String cntrctChgId;

    // @Column(name = "revision_id", updatable = false)
    @Description(name = "리비전ID", description = "", type = Description.TYPE.FIELD)
    String revisionId;

    @Description(name = "삭제여부", description = "", type = Description.TYPE.FIELD)
    String dltYn;

}
