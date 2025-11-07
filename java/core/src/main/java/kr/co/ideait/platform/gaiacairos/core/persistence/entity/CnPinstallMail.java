package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class CnPinstallMail {
    @Id
    @Description(name = "현장 개설 요청 번호", description = "", type = Description.TYPE.FIELD)
    String plcReqNo;

    @Description(name = "관리관 ID", description = "", type = Description.TYPE.FIELD)
    String receiver;

    @Description(name = "슈퍼관리자 ID", description = "", type = Description.TYPE.FIELD)
    String sender;

    @Description(name = "메일내용", description = "", type = Description.TYPE.FIELD)
    String content;

    @Description(name = "발송 일자", description = "", type = Description.TYPE.FIELD)
    LocalDateTime sentDt;

    @PrePersist
    public void prePersist() {
        setSentDt(LocalDateTime.now());
    }
}
