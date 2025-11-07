package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import java.time.LocalDateTime;

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
@IdClass(CwMainmtrlReqfrmPhotoId.class)
public class CwMainmtrlReqfrmPhoto extends AbstractRudIdTime{
    @Id
    @Description(name = "검수요청서 No", description = "", type = Description.TYPE.FIELD)
    String reqfrmNo;        // 검수요청서 No

    @Id
    @Description(name = "계약번호", description = "", type = Description.TYPE.FIELD)
    String cntrctNo;         // 계약번호 (FK)

    @Id
    @Column(name = "pht_sno", columnDefinition = "int2")
    @Description(name = "사진 순번", description = "", type = Description.TYPE.FIELD)
    int phtSno;            // 사진 순번

    @Description(name = "첨부파일 번호", description = "", type = Description.TYPE.FIELD)
    Integer atchFileNo;    // 첨부파일 번호

    @Description(name = "내용", description = "", type = Description.TYPE.FIELD)
    String cntnts;      // 내용

    @Description(name = "위치", description = "", type = Description.TYPE.FIELD)
    String lct;      // 위치

    @Description(name = "촬영일자", description = "", type = Description.TYPE.FIELD)
    LocalDateTime shotDate; // 촬영일자

    @Description(name = "삭제여부", description = "", type = Description.TYPE.FIELD)
    String dltYn;     // 삭제여부 (Y/N)
}
