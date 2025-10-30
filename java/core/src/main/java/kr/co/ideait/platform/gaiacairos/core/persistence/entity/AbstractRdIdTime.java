package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Mappable;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
public abstract class AbstractRdIdTime implements Mappable {

    @Description(name = "등록자ID", description = "", type = Description.TYPE.FIELD)
    String rgstrId;
    @Description(name = "등록일시", description = "", type = Description.TYPE.FIELD)
    LocalDateTime rgstDt;
    @Description(name = "삭제자ID", description = "", type = Description.TYPE.FIELD)
    String dltId;
    @Description(name = "삭제일시", description = "", type = Description.TYPE.FIELD)
    LocalDateTime dltDt;

    @PrePersist
    public void prePersist() {
        if (StringUtils.isEmpty(getRgstrId())) {
            setRgstrId(UserAuth.get(true).getUsrId());
        }

        if (getRgstDt() == null) {
            setRgstDt(LocalDateTime.now());
        }
    }

}
