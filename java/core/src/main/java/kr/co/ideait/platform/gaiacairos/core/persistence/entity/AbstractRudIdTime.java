package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
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
public abstract class AbstractRudIdTime implements Mappable {


    @Description(name = "등록자ID", description = "")
    String rgstrId;

    @Description(name = "등록일시", description = "")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime rgstDt;

    @Description(name = "수정자ID", description = "")
    String chgId;

    @Description(name = "수정일시", description = "")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime chgDt;

    @Description(name = "삭제자ID", description = "")
    String dltId;

    @Description(name = "삭제일시", description = "")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime dltDt;

    @PrePersist
    public void prePersist() {
        if (StringUtils.isEmpty(getRgstrId())) {
            setRgstrId(UserAuth.get(true).getUsrId());
        }

        if (getRgstDt() == null) {
            setRgstDt(LocalDateTime.now());
        }
        preUpdate();
    }

    @PreUpdate
    public void preUpdate() {
        if (StringUtils.isEmpty(getChgId())) {
            setChgId(UserAuth.get(true).getUsrId());
        }

        if (getChgDt() == null) {
            setChgDt(LocalDateTime.now());
        }
    }
}
