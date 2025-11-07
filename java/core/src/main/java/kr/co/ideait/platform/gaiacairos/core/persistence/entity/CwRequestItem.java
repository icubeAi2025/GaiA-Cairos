package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import jakarta.persistence.*;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class CwRequestItem extends AbstractRudIdTime {
    @Id
    @Description(name = "요청업무아이디", description = "", type = Description.TYPE.FIELD)
    String reqInsId; 

    @Description(name = "프로젝트 번호", description = "", type = Description.TYPE.FIELD)
    String pjtNo; 

    @Description(name = "계약번호", description = "", type = Description.TYPE.FIELD)
    String cntrctNo; 

    @Description(name = "요청업무구분", description = "", type = Description.TYPE.FIELD)
    String reqAppDiv;
    
    @Description(name = "요청대상아이디", description = "", type = Description.TYPE.FIELD)
    String toUsrId;

    @Description(name = "상태구분", description = "", type = Description.TYPE.FIELD)
    String stateDiv;

    @Description(name = "업무처리구분", description = "", type = Description.TYPE.FIELD)
    String endYn;

    @Description(name = "비고", description = "", type = Description.TYPE.FIELD)
    String note;

    @Description(name = "삭제여부", description = "", type = Description.TYPE.FIELD)
    String dltYn;
}
