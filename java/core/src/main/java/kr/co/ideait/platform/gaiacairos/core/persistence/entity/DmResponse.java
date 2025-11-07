package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

import java.util.UUID;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@IdClass(DmResponseId.class)
@Alias("dmResponse")
public class DmResponse extends AbstractRudIdTime {

    @Id
    @Description(name = "설계 리뷰 일련번호", description = "", type = Description.TYPE.FIELD)
    String resSeq;
    @Id
    @Description(name = "설계 리뷰 번호", description = "", type = Description.TYPE.FIELD)
    String dsgnNo;
    @Id
    @Description(name = "계약번호", description = "", type = Description.TYPE.FIELD)
    String cntrctNo;
    @Description(name = "답변 분류", description = "", type = Description.TYPE.FIELD)
    String rplyCd;
    @Description(name = "답변 내용", description = "", type = Description.TYPE.FIELD)
    String rplyCntnts;
    @Description(name = "첨부파일 번호", description = "", type = Description.TYPE.FIELD)
    String atchFileNo;
    @Description(name = "답변 도서 번호", description = "", type = Description.TYPE.FIELD)
    String dwgNo;
    @Description(name = "삭제여부", description = "", type = Description.TYPE.FIELD)
    String dltYn;

}
