package kr.co.ideait.platform.gaiacairos.core.persistence.vo.safety.report;

import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 재해일지
 */
@Data
public class DisasterDiary {
    @Description(name = "재해일련번호", description = "재해일지 Field", type = Description.TYPE.FIELD)
    String disasId;
    @Description(name = "계약번호", description = "재해일지 Field", type = Description.TYPE.FIELD)
    String cntrctNo;
    @Description(name = "재해일자", description = "재해일지 Field", type = Description.TYPE.FIELD)
    String disasDt;
    @Description(name = "재해원인", description = "재해일지 Field", type = Description.TYPE.FIELD)
    String disasCause;
    @Description(name = "재해조치", description = "재해일지 Field", type = Description.TYPE.FIELD)
    String disasAction;
    @Description(name = "삭제여부", description = "재해일지 Field", type = Description.TYPE.FIELD)
    String dltYn;
    @Description(name = "등록자ID", description = "재해일지 Field", type = Description.TYPE.FIELD)
    String rgstrId;
    @Description(name = "등록일", description = "재해일지 Field", type = Description.TYPE.FIELD)
    LocalDateTime rgstDt;
    @Description(name = "수정자ID", description = "재해일지 Field", type = Description.TYPE.FIELD)
    String chgId;
    @Description(name = "수정일", description = "재해일지 Field", type = Description.TYPE.FIELD)
    LocalDateTime chgDt;
    @Description(name = "삭제자ID", description = "재해일지 Field", type = Description.TYPE.FIELD)
    String dltId;
    @Description(name = "삭제일", description = "재해일지 Field", type = Description.TYPE.FIELD)
    LocalDateTime dltDt;
}
