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
@IdClass(PrMonthlyReportPhotoId.class) // 복합 키 클래스 설정
@Data
@EqualsAndHashCode(callSuper = true)
@Alias("prMonthlyReportPhoto")
public class PrMonthlyReportPhoto extends AbstractRudIdTime{
    @Id
    @Description(name = "계약변경ID", description = "", type = Description.TYPE.FIELD)
    String cntrctChgId;

    @Id
    @Column(columnDefinition = "NUMERIC")
    @Description(name = "월간보고ID", description = "", type = Description.TYPE.FIELD)
    Long monthlyReportId;

    @Id
    @Column(columnDefinition = "NUMERIC")
    @Description(name = "파일 순번", description = "", type = Description.TYPE.FIELD)
    Short sno;

    @Column(columnDefinition = "NUMERIC")
    @Description(name = "첨부파일번호", description = "", type = Description.TYPE.FIELD)
    Integer fileNo;

    @Description(name = "제목명", description = "", type = Description.TYPE.FIELD)
    String titlNm;

    @Description(name = "설명", description = "", type = Description.TYPE.FIELD)
    String dscrpt;

    @Description(name = "촬영일자", description = "", type = Description.TYPE.FIELD)
    String shotDate;

    @Description(name = "삭제여부", description = "", type = Description.TYPE.FIELD)
    String dltYn;
}
