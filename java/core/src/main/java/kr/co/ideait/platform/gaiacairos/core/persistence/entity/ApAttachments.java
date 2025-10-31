package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class ApAttachments extends AbstractRudIdTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Description(name = "파일 번호", description = "", type = Description.TYPE.FIELD)
    Integer fileNo;
    
    @Description(name = "결재문서번호", description = "", type = Description.TYPE.FIELD)
    Integer apDocNo;
    
    @Description(name = "결재문서ID", description = "", type = Description.TYPE.FIELD)
    String apDocId;
    
    @Description(name = "파일 이름", description = "", type = Description.TYPE.FIELD)
    String fileNm;
    
    @Description(name = "파일 DISK 이름", description = "", type = Description.TYPE.FIELD)
    String fileDiskNm;
    
    @Description(name = "파일 DISK 경로", description = "", type = Description.TYPE.FIELD)
    String fileDiskPath;
    
    @Description(name = "파일 사이즈", description = "", type = Description.TYPE.FIELD)
    Integer fileSize;
    
    @Description(name = "파일 다운로드 수", description = "", type = Description.TYPE.FIELD)
    Integer fileHitNum;
    
    @Description(name = "삭제여부", description = "", type = Description.TYPE.FIELD)
    String dltYn;

}
