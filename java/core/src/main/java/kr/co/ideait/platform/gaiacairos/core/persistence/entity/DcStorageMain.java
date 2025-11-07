package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Entity
@Data
@Alias("dcStorageMain")
@EqualsAndHashCode(callSuper = true)
public class DcStorageMain extends AbstractRudIdTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Description(name = "문서 No", description = "")
    Integer docNo;

    @Description(name = "문서 ID", description = "")
    String docId;

    @Description(name = "네비게이션 No", description = "")
    Integer naviNo;

    @Description(name = "네비게이션 ID", description = "")
    String naviId;

    @Description(name = "상위문서 No", description = "")
    Integer upDocNo;

    @Description(name = "상위문서 ID", description = "")
    String upDocId;

    @Description(name = "문서 종류", description = "FOLDR: 폴더형, FILE: 파일, ITEM: 아이템형")
    String docType;

    @Description(name = "문서 경로", description = "")
    String docPath;

    @Description(name = "문서 이름", description = "")
    String docNm;

    @Description(name = "문서 DISK 이름", description = "")
    String docDiskNm;

    @Description(name = "문서 DISK 경로", description = "")
    String docDiskPath;

    @Description(name = "문서 사이즈", description = "")
    Integer docSize;

    @Description(name = "문서 다운로드수", description = "")
    Short docHitNum;

    @Description(name = "문서 휴지통여부", description = "")
    String docTrashYn;

    @Description(name = "삭제여부", description = "")
    String dltYn;

    @Description(name = "착공문서번호", description = "")
    Integer cbgnKey;
}
