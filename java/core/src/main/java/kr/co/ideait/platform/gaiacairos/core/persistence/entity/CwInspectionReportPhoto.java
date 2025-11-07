package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@IdClass(CwInspectionReportPhotoId.class)
public class CwInspectionReportPhoto {

    @Id
    @Description(name = "계약번호", description = "", type = Description.TYPE.FIELD)
    String cntrctNo;

    @Id
    @Column(columnDefinition = "NUMERIC")
    @Description(name = "작업보고ID", description = "", type = Description.TYPE.FIELD)
    Long dailyReportId;

    @Id
    @Column(columnDefinition = "NUMERIC")
    @Description(name = "공종사진순번", description = "", type = Description.TYPE.FIELD)
    Long cnsttyPhtSno;

    @Description(name = "ActivityID", description = "", type = Description.TYPE.FIELD)
    String activityId;

    @Description(name = "첨부파일번호", description = "", type = Description.TYPE.FIELD)
    String atchFileNo;

    @Description(name = "제목명", description = "", type = Description.TYPE.FIELD)
    String titlNm;

    @Description(name = "설명", description = "", type = Description.TYPE.FIELD)
    String dscrpt;

    @Description(name = "촬영일자", description = "", type = Description.TYPE.FIELD)
    String shotDate;

    @Description(name = "삭제여부", description = "", type = Description.TYPE.FIELD)
    String dltYn;

    @Description(name = "등록자ID", description = "", type = Description.TYPE.FIELD)
    String rgstrId;

    @Description(name = "등록일시", description = "", type = Description.TYPE.FIELD)
    LocalDateTime rgstDt;

    @Description(name = "수정자ID", description = "", type = Description.TYPE.FIELD)
    String chgId;

    @Description(name = "수정일시", description = "", type = Description.TYPE.FIELD)
    LocalDateTime chgDt;

    @Description(name = "삭제자ID", description = "", type = Description.TYPE.FIELD)
    String dltId;

    @Description(name = "삭제일시", description = "", type = Description.TYPE.FIELD)
    LocalDateTime dltDt;

}

