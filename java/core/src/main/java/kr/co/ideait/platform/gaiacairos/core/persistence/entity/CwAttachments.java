package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@IdClass(CwAttachmentsId.class) // 복합 키 클래스 설정
@Data
@EqualsAndHashCode(callSuper = true)
public class CwAttachments extends AbstractRudIdTime {

    @Id
    @Column(columnDefinition = "NUMERIC")
    @Description(name = "파일 번호", description = "", type = Description.TYPE.FIELD)
    Integer fileNo;

    @Id
    @Column(columnDefinition = "NUMERIC")
    @Description(name = "순번", description = "", type = Description.TYPE.FIELD)
    Integer sno;

    @Description(name = "파일 이름", description = "", type = Description.TYPE.FIELD)
    String fileNm;
    @Description(name = "파일 DISK 이름", description = "", type = Description.TYPE.FIELD)
    String fileDiskNm;
    @Description(name = "파일 DISK 경로", description = "", type = Description.TYPE.FIELD)
    String fileDiskPath;
    @Column(columnDefinition = "NUMERIC")
    @Description(name = "파일 사이즈", description = "", type = Description.TYPE.FIELD)
    Integer fileSize;
    @Column(columnDefinition = "NUMERIC")
    @Description(name = "조회수", description = "", type = Description.TYPE.FIELD)
    Integer fileHitNum;
    @Description(name = "삭제여부", description = "", type = Description.TYPE.FIELD)
    String dltYn;
    @Description(name = "파일 구분", description = "", type = Description.TYPE.FIELD)
    String fileDiv;
}
