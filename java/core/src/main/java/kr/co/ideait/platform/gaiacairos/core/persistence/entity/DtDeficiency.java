package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class DtDeficiency extends AbstractRudIdTime {

    @Id
    @Description(name = "결함번호", description = "", type = Description.TYPE.FIELD)
    String dfccyNo;

    @Description(name = "계약번호", description = "", type = Description.TYPE.FIELD)
    String cntrctNo;

    @Description(name = "결함단계 번호", description = "", type = Description.TYPE.FIELD)
    String dfccyPhaseNo;

    @Description(name = "제목", description = "", type = Description.TYPE.FIELD)
    String title;

    @Description(name = "결함분류", description = "", type = Description.TYPE.FIELD)
    String dfccyCd;

    @Description(name = "결함위치", description = "", type = Description.TYPE.FIELD)
    String dfccyLct;

    @Description(name = "생명/안전/보건 여부", description = "", type = Description.TYPE.FIELD)
    String crtcIsueYn;

    @Description(name = "결함내용", description = "", type = Description.TYPE.FIELD)
    String dfccyCntnts;

    @Description(name = "첨부파일 번호", description = "", type = Description.TYPE.FIELD)
    Integer atchFileNo;

    @Description(name = "답변 완료 여부", description = "", type = Description.TYPE.FIELD)
    String rplyYn;

    @Description(name = "QA 확인", description = "", type = Description.TYPE.FIELD)
    String qaCd;

    @Description(name = "QA 확인 등록자 ID", description = "", type = Description.TYPE.FIELD)
    String qaRgstrId;

    @Description(name = "QA 확인 등록일", description = "", type = Description.TYPE.FIELD)
    LocalDateTime qaRgstDt;

    @Description(name = "관리관 확인", description = "", type = Description.TYPE.FIELD)
    String spvsCd;

    @Description(name = "관리관 확인 등록자 ID", description = "", type = Description.TYPE.FIELD)
    String spvsRgstrId;

    @Description(name = "관리관 확인 등록일", description = "", type = Description.TYPE.FIELD)
    LocalDateTime spvsRgstDt;

    @Description(name = "종결 구분", description = "", type = Description.TYPE.FIELD)
    String edCd;

    @Description(name = "종결 등록자 Id", description = "", type = Description.TYPE.FIELD)
    String edRgstrId;

    @Description(name = "종결 등록일시", description = "", type = Description.TYPE.FIELD)
    LocalDateTime edRgstDt;

    @Description(name = "삭제여부", description = "", type = Description.TYPE.FIELD)
    String dltYn;

    @Description(name = "중요 여부", description = "", type = Description.TYPE.FIELD)
    char priorityCheck;
}