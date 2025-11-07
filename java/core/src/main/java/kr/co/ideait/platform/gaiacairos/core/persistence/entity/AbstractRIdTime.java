package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Mappable;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
public abstract class AbstractRIdTime implements Mappable {

    @Description(name = "등록자ID", description = "", type = Description.TYPE.FIELD)
    String rgstrId;
    @Description(name = "등록일시", description = "", type = Description.TYPE.FIELD)
    LocalDateTime rgstDt;

    @PrePersist
    public void prePersist() {
        setRgstrId(UserAuth.get(true).getUsrId());
        setRgstDt(LocalDateTime.now());
    }

}
