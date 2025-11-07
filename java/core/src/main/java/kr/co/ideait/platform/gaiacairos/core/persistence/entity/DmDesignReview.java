package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

import java.time.LocalDateTime;

@Alias("dmDesignReview")
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class DmDesignReview extends AbstractRudIdTime {

    @Id
    @Description(name = "설계 리뷰 번호", description = "", type = Description.TYPE.FIELD)
    String dsgnNo;
    @Description(name = "계약번호", description = "", type = Description.TYPE.FIELD)
    String cntrctNo;
    @Description(name = "설계 단계 번호", description = "", type = Description.TYPE.FIELD)
    String dsgnPhaseNo;
    @Description(name = "제목", description = "", type = Description.TYPE.FIELD)
    String title;
    @Description(name = "설계검토분류", description = "", type = Description.TYPE.FIELD)
    String dsgnCd;
    @Description(name = "문서번호", description = "", type = Description.TYPE.FIELD)
    String docNo;
    @Description(name = "도면번호", description = "", type = Description.TYPE.FIELD)
    String dwgNo;
    @Description(name = "도면명", description = "", type = Description.TYPE.FIELD)
    String dwgNm;
    @Description(name = "검토의견", description = "", type = Description.TYPE.FIELD)
    String rvwOpnin;
    @Description(name = "문제점 YN", description = "", type = Description.TYPE.FIELD)
    String isuYn;
    @Description(name = "교훈 YN", description = "", type = Description.TYPE.FIELD)
    String lesnYn;
    @Description(name = "첨부파일 번호", description = "", type = Description.TYPE.FIELD)
    String atchFileNo;
    @Description(name = "검토도서 번호", description = "", type = Description.TYPE.FIELD)
    String rvwDwgNo;
    @Description(name = "변경요청 도서 번호", description = "", type = Description.TYPE.FIELD)
    String chgDwgNo;
    @Description(name = "평가자 동의", description = "", type = Description.TYPE.FIELD)
    String apprerCd;
    @Description(name = "평가자 동의 등록자 ID", description = "", type = Description.TYPE.FIELD)
    String apprerRgstrId;
    @Description(name = "평가자 동의 등록일", description = "", type = Description.TYPE.FIELD)
    LocalDateTime apprerRgstDt;
    @Description(name = "백체크 확인", description = "", type = Description.TYPE.FIELD)
    String backchkCd;
    @Description(name = "백체크 확인 등록자 ID", description = "", type = Description.TYPE.FIELD)
    String backchkRgstrId;
    @Description(name = "백체크 확인 등록일", description = "", type = Description.TYPE.FIELD)
    LocalDateTime backchkRgstDt;
    @Description(name = "삭제여부", description = "", type = Description.TYPE.FIELD)
    String dltYn;
    @Description(name = "시퀀스", description = "", type = Description.TYPE.FIELD)
    Integer dsgnSeq;
}
