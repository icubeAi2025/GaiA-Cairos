package kr.co.ideait.platform.gaiacairos.core.persistence.vo.eureca;

import com.fasterxml.jackson.annotation.JsonProperty;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;

/**
 * 중기단가산출
 */
@Data
public class HmupcDtl extends BaseSyncModel {
    @JsonProperty("dsplySn")
    @Description(name = "표시순번", description = "중기단가산출", type = Description.TYPE.FIELD)
    Long dsplySn;

    @JsonProperty("hmupCalcfrmlaNm")
    @Description(name = "산출식", description = "중기단가산출", type = Description.TYPE.FIELD)
    String hmupCalcfrmlaNm;

    @JsonProperty("hmupCalcBaseNm")
    @Description(name = "산출결과", description = "중기단가산출", type = Description.TYPE.FIELD)
    String hmupCalcBaseNm;

    @JsonProperty("midCdVal")
    @Description(name = "중간코드값", description = "중기단가산출", type = Description.TYPE.FIELD)
    String midCdVal;
}
