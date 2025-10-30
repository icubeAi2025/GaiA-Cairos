package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Entity
@IdClass(DmDwgId.class) // 복합 키 클래스 설정
@Data
@EqualsAndHashCode(callSuper = true)
@Alias("dmDwg")
public class DmDwg extends AbstractRudIdTime {

    @Id
    @Description(name = "도서 No", description = "")
    String dwgNo;

    @Id
    @Description(name = "도서 구분", description = "")
    String dwgCd;

    @Description(name = "설명", description = "")
    String dwgDscrpt;

    @Description(name = "첨부파일번호", description = "")
    String atchFileNo;

    @Description(name = "순번", description = "")
    Short sno;

    @Description(name = "파일 키", description = "")
    String fileKey;

    @Description(name = "삭제여부", description = "")
    String dltYn;
}
