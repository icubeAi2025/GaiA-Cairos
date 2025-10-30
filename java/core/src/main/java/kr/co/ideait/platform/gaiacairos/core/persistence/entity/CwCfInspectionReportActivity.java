package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Alias("cwCfInspectionReportActivity")
@IdClass(CwCfInspectionReportActivityId.class)
public class CwCfInspectionReportActivity extends AbstractRudIdTime {

    @Id
    @Description(name = "계약번호", description = "", type = Description.TYPE.FIELD)
    String cntrctNo; // 계약번호

    @Id
    @Column(columnDefinition = "NUMERIC")
    @Description(name = "책임감리일지 일련번호", description = "", type = Description.TYPE.FIELD)
    Long dailyReportId; // 책임감리일지 일련번호

    @Id
    @Description(name = "일지별 활동 순번", description = "", type = Description.TYPE.FIELD)
    Integer dailyActivityId; // 일지별 활동 순번

    @Description(name = "WBS 코드", description = "", type = Description.TYPE.FIELD)
    String wbsCd; // WBS 코드

    @Description(name = "Activity ID", description = "", type = Description.TYPE.FIELD)
    String activityId; // Activity ID

    @Description(name = "작업내용", description = "작업내용 (기존 inspection_item 대신 추가)", type = Description.TYPE.FIELD)
    String taskContent; // 작업내용 (기존 inspection_item 대신 추가)

    @Description(name = "특이사항", description = "특이사항 (기존 inspection_note 대신 추가)", type = Description.TYPE.FIELD)
    String specialNote; // 특이사항 (기존 inspection_note 대신 추가)

    @Description(name = "계약 변경 ID", description = "", type = Description.TYPE.FIELD)
    String cntrctChgId; // 계약 변경 ID

    @Description(name = "Revision ID", description = "", type = Description.TYPE.FIELD)
    String revisionId; // Revision ID

    @Description(name = "삭제 여부", description = "", type = Description.TYPE.FIELD)
    String dltYn; // 삭제 여부

} 