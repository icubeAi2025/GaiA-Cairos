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
@IdClass(CwDailyReportPhotoId.class)
public class CwDailyReportPhoto extends AbstractRudIdTime {

    @Id
    @Description(name = "계약번호", description = "", type = Description.TYPE.FIELD)
    String cntrctNo;

    @Id
    @Column(columnDefinition = "NUMERIC")
    @Description(name = "작업보고ID", description = "", type = Description.TYPE.FIELD)
    Integer dailyReportId;

    @Id
    @Column(columnDefinition = "NUMERIC")
    @Description(name = "공종사진순번", description = "", type = Description.TYPE.FIELD)
    Integer cnsttyPhtSno;

    @Description(name = "ActivityID", description = "", type = Description.TYPE.FIELD)
    String activityId;

    @Column(columnDefinition = "NUMERIC")
    @Description(name = "첨부파일번호", description = "", type = Description.TYPE.FIELD)
    Integer atchFileNo;

    @Description(name = "제목명", description = "", type = Description.TYPE.FIELD)
    String titlNm;

    @Description(name = "설명", description = "", type = Description.TYPE.FIELD)
    String dscrpt;

    @Description(name = "촬영일자", description = "", type = Description.TYPE.FIELD)
    String shotDate;

    @Description(name = "삭제여부", description = "", type = Description.TYPE.FIELD)
    String dltYn;

}
