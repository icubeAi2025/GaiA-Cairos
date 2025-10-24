package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@IdClass(DtDeficiencyReplyId.class)
public class DtDeficiencyReply extends AbstractRudIdTime {

    @Id
    @Description(name = "결함답변 일련번호", description = "", type = Description.TYPE.FIELD)
    Integer replySeq;

    @Id
    @Description(name = "결함번호", description = "", type = Description.TYPE.FIELD)
    String dfccyNo;

    @Id
    @Description(name = "계약번호", description = "", type = Description.TYPE.FIELD)
    String cntrctNo;

    @Description(name = "답변 분류", description = "", type = Description.TYPE.FIELD)
    String rplyCd;

    @Description(name = "답변 및 조치", description = "", type = Description.TYPE.FIELD)
    String rplyCntnts;

    @Description(name = "답변 첨부 파일 번호", description = "", type = Description.TYPE.FIELD)
    Integer atchFileNo;

    @Description(name = "답변 완료 여부", description = "", type = Description.TYPE.FIELD)
    String rplyYn;

    @Description(name = "답변 완료 여부 등록자 ID", description = "", type = Description.TYPE.FIELD)
    String rplyRgstrId;

    @Description(name = "답변 완료 등록일", description = "", type = Description.TYPE.FIELD)
    LocalDateTime rplyRgstrDt;

    @Description(name = "삭제여부", description = "", type = Description.TYPE.FIELD)
    String dltYn;

    // PostgreSQL 시퀀스를 사용하여 seq 다음 값을 생성
    public void generateReplySeq(EntityManager entityManager) {
        this.replySeq = ((Number) entityManager
                .createNativeQuery("SELECT nextval('gaia_cmis.dt_deficiency_reply_reply_seq_seq')")
                .getSingleResult())
                .intValue();
    }
}