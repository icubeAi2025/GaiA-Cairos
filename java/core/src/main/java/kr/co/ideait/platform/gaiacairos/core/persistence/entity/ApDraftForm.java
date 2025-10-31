package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Alias("apDraftForm")
public class ApDraftForm extends AbstractRudIdTime {

    @Id
    @Description(name = "서식번호", description = "", type = Description.TYPE.FIELD)
    Integer frmNo;

    @Description(name = "서식ID", description = "", type = Description.TYPE.FIELD)
    String frmId;

    @Description(name = "프로젝트번호", description = "", type = Description.TYPE.FIELD)
    String pjtNo;

    @Description(name = "계약번호", description = "", type = Description.TYPE.FIELD)
    String cntrctNo;

    @Description(name = "프로젝트 구분", description = "", type = Description.TYPE.FIELD)
    String pjtType;

    @Description(name = "삭제여부", description = "", type = Description.TYPE.FIELD)
    String dltYn;


}
