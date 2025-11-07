package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import jakarta.persistence.Column;
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
@IdClass(CwQualityPhotoId.class)
public class CwQualityPhoto extends AbstractRudIdTime {
    @Id
    @Description(name = "품질검측 ID", description = "", type = Description.TYPE.FIELD)
    String qltyIspId; // 품질검측 ID

    @Id
    @Description(name = "계약번호", description = "", type = Description.TYPE.FIELD)
    String cntrctNo; // 계약번호

    @Id
    @Column(name = "pht_sno", columnDefinition = "int2")
    @Description(name = "사진순번", description = "", type = Description.TYPE.FIELD)
    int phtSno; // 사진순번

    @Column(name = "atch_file_no", columnDefinition = "serial4")
    @Description(name = "첨부파일번호", description = "", type = Description.TYPE.FIELD)
    Integer atchFileNo; // 첨부파일번호

    @Description(name = "제목명", description = "", type = Description.TYPE.FIELD)
    String titlNm; // 제목명

    @Description(name = "내용", description = "", type = Description.TYPE.FIELD)
    String dscrpt; // 내용

    @Description(name = "촬영일자", description = "", type = Description.TYPE.FIELD)
    LocalDateTime shotDate; // 촬영일자

    @Description(name = "삭제여부", description = "", type = Description.TYPE.FIELD)
    String dltYn; // 삭제여부
}
