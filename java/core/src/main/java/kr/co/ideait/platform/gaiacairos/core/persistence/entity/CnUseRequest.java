package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class CnUseRequest extends AbstractRudIdTime {
    @Id
    @Description(name = "요청 고유번호", description = "", type = Description.TYPE.FIELD)
    @Column(name = "req_no", updatable = false, nullable = false)
    UUID reqNo;

    @Description(name = "프로젝트번호", description = "", type = Description.TYPE.FIELD)
    String pjtNo;
    @Description(name = "프로젝트명", description = "", type = Description.TYPE.FIELD)
    String pjtNm;
    @Description(name = "계약번호", description = "", type = Description.TYPE.FIELD)
    String cntrctNo;
    @Description(name = "계약명", description = "", type = Description.TYPE.FIELD)
    String cntrctNm;
    @Description(name = "로그인 ID", description = "", type = Description.TYPE.FIELD)
    String loginId;
    @Description(name = "사용자명", description = "", type = Description.TYPE.FIELD)
    String usrNm;
    @Description(name = "직급/직책", description = "", type = Description.TYPE.FIELD)
    String position;
    @Description(name = "기타", description = "", type = Description.TYPE.FIELD)
    String content;
    @Description(name = "삭제여부", description = "", type = Description.TYPE.FIELD)
    String dltYn;
}
