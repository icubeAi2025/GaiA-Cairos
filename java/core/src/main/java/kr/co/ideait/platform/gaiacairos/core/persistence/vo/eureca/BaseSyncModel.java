package kr.co.ideait.platform.gaiacairos.core.persistence.vo.eureca;

import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;

/**
 * Eureca Api DTO 공통 인터페이스
 */
@Data
public abstract class BaseSyncModel {
    @Description(name = "계약번호", description = "Field", type = Description.TYPE.FIELD)
    private String cntrctNo;

    @Description(name = "계약변경ID", description = "Field", type = Description.TYPE.FIELD)
    private String cntrctChgId;
}
