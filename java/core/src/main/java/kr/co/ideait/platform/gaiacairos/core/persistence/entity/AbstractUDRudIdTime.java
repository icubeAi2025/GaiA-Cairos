package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Mappable;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;

import java.time.LocalDateTime;

@MappedSuperclass
@Data
// @EntityListeners(AuditingEntityListener.class)
public abstract class AbstractUDRudIdTime implements Mappable {

    @Description(name = "사용여부", description = "", type = Description.TYPE.FIELD)
    String useYn;
    @Description(name = "삭제여부", description = "", type = Description.TYPE.FIELD)
    String dltYn;

    @Description(name = "등록자ID", description = "", type = Description.TYPE.FIELD)
    String rgstrId;
    // @CreatedDate
    @Description(name = "등록일시", description = "", type = Description.TYPE.FIELD)
    LocalDateTime rgstDt;

    @Description(name = "수정자ID", description = "", type = Description.TYPE.FIELD)
    String chgId;
    
    // @CreatedDate
    // @LastModifiedDate
    @Description(name = "수정일지", description = "", type = Description.TYPE.FIELD)
    LocalDateTime chgDt;

    @Description(name = "삭제자ID", description = "", type = Description.TYPE.FIELD)
    String dltId;

    @Description(name = "삭제일시", description = "", type = Description.TYPE.FIELD)
    LocalDateTime dltDt;

    @PrePersist
    public void prePersist() {
        setRgstrId(UserAuth.get(true).getUsrId());
        setRgstDt(LocalDateTime.now());
        preUpdate();
    }

    @PreUpdate
    public void preUpdate() {
        setChgId(UserAuth.get(true).getUsrId());
        setChgDt(LocalDateTime.now());
    }
}
