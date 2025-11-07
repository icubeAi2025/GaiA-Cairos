package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.Mappable;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.time.LocalDateTime;

@Alias("smUserInfo")
@Entity
@Data
public class SmUserInfo implements Mappable {

    @Id
    String usrId;
    String ociUsrId;
    String ncpUsrId;
    String loginId;
    String usrNm;
    String ratngCd;
    String pstnCd;
    String phoneNo;
    String telNo;
    String emailAdrs;
    String mngDiv;
    String useYn;
    String dltYn;
    String rgstrId;
    LocalDateTime rgstDt;
    String chgId;
    LocalDateTime chgDt;
    String dltId;
    LocalDateTime dltDt;
    String corpNo;

//    @PrePersist
//    public void prePersist() {
//        UserAuth userAuth = UserAuth.get();
//        String loginId = userAuth != null ? userAuth.getUsrId() : "not_logined";
//        LocalDateTime now = LocalDateTime.now();
//        setRgstrId(loginId);
//        setRgstDt(now);
//        setChgId(loginId);
//        setChgDt(now);
//    }
//
//    @PreUpdate
//    public void preUpdate() {
//        setChgId(UserAuth.get(true).getUsrId());
//        setChgDt(LocalDateTime.now());
//    }
}
