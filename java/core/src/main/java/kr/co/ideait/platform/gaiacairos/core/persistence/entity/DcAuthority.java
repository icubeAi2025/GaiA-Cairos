package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class DcAuthority extends AbstractRudIdTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Description(name = "권한_No", description = "", type = Description.TYPE.FIELD)
    Integer rghtNo;

    @Description(name = "권한그룹_no", description = "", type = Description.TYPE.FIELD)
    Integer rghtGrpNo;

    @Description(name = "ID", description = "", type = Description.TYPE.FIELD)
    String id;

    @Description(name = "번호", description = "", type = Description.TYPE.FIELD)
    Integer no;

    @Description(name = "권한그룹 코드", description = "", type = Description.TYPE.FIELD)
    String rghtGrpCd;

    @Description(name = "권한 유형", description = "", type = Description.TYPE.FIELD)
    String rghtTy;

    @Description(name = "삭제여부", description = "", type = Description.TYPE.FIELD)
    String dltYn;

}
