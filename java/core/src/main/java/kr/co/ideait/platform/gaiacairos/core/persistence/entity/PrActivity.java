package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import jakarta.persistence.*;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "pr_activity", schema = "gaia_cmis")
@IdClass(PrActivityId.class)
public class PrActivity extends AbstractRdIdTime {

    @Id
    @Description(name = "계약변경ID", description = "", type = Description.TYPE.FIELD)
    String cntrctChgId;
    @Id
    @Description(name = "리비젼ID", description = "", type = Description.TYPE.FIELD)
    String revisionId;
    @Id
    @Description(name = "Activity ID", description = "", type = Description.TYPE.FIELD)
    String activityId;
    @Description(name = "WBS코드", description = "", type = Description.TYPE.FIELD)
    String wbsCd;
    @Description(name = "Activity명", description = "", type = Description.TYPE.FIELD)
    String activityNm;
    @Description(name = "Activity종류", description = "", type = Description.TYPE.FIELD)
    String activityKind;
    @Description(name = "빠른시작일자", description = "", type = Description.TYPE.FIELD)
    String earlyStart;
    @Description(name = "빠른종료일자", description = "", type = Description.TYPE.FIELD)
    String earlyFinish;
    @Description(name = "늦은시작일자", description = "", type = Description.TYPE.FIELD)
    String lateStart;
    @Description(name = "늦은종료일자", description = "", type = Description.TYPE.FIELD)
    String lateFinish;
    @Description(name = "계획시작일자", description = "", type = Description.TYPE.FIELD)
    String planStart;
    @Description(name = "계획종료일자", description = "", type = Description.TYPE.FIELD)
    String planFinish;
    @Description(name = "실제시작일자", description = "", type = Description.TYPE.FIELD)
    String actualStart;
    @Description(name = "실제종료일자", description = "", type = Description.TYPE.FIELD)
    String actualFinish;
    @Description(name = "시작일자", description = "", type = Description.TYPE.FIELD)
    String currentStart;
    @Description(name = "완료일자", description = "", type = Description.TYPE.FIELD)
    String currentFinish;
    @Column(columnDefinition = "NUMERIC")
    @Description(name = "최초기간", description = "", type = Description.TYPE.FIELD)
    double intlDuration;
    @Column(columnDefinition = "NUMERIC")
    @Description(name = "잔여기간", description = "", type = Description.TYPE.FIELD)
    double remndrDuration;
    @Column(columnDefinition = "NUMERIC")
    @Description(name = "총여유", description = "", type = Description.TYPE.FIELD)
    double totalFloat;
    @Column(columnDefinition = "NUMERIC")
    @Description(name = "잔여비용", description = "", type = Description.TYPE.FIELD)
    double remndrCost;
    @Column(columnDefinition = "NUMERIC")
    @Description(name = "예상비용", description = "", type = Description.TYPE.FIELD)
    double exptCost;
    @Description(name = "선행관계", description = "", type = Description.TYPE.FIELD)
    String predecessors;
    @Description(name = "후행관계", description = "", type = Description.TYPE.FIELD)
    String successors;
    @Column(columnDefinition = "NUMERIC")
    @Description(name = "완료진행률", description = "", type = Description.TYPE.FIELD)
    double cmpltPercent;
    @Description(name = "비고", description = "", type = Description.TYPE.FIELD)
    String rmrk;
    @Description(name = "삭제여부", description = "", type = Description.TYPE.FIELD)
    String dltYn;

    @Column(name = "p6_wbs_obj_id")
    @Description(name = "p6 WBS Object Id", description = "", type = Description.TYPE.FIELD)
    Integer p6WbsObjId;
    @Column(name = "p6_activity_obj_id")
    @Description(name = "p6 Activity Object Id", description = "", type = Description.TYPE.FIELD)
    Integer p6ActivityObjId;

}
