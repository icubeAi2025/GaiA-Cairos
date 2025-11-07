package kr.co.ideait.platform.gaiacairos.core.persistence.vo.eureca;

import com.fasterxml.jackson.annotation.JsonProperty;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;

/**
 * 원가계산서
 */
@Data
public class Calculator extends BaseSyncModel {
    @JsonProperty("dsplySn")
    @Description(name = "표시순번", description = "원가계산서 Field", type = Description.TYPE.FIELD)
    Long dsplySn;

    @JsonProperty("cstCalcItCd")
    @Description(name = "원가산출항목코드", description = "원가계산서 Field", type = Description.TYPE.FIELD)
    String cstCalcItCd;

    @JsonProperty("upCstCalcItCd")
    @Description(name = "상위원가산출항목코드", description = "원가계산서 Field", type = Description.TYPE.FIELD)
    String upCstCalcItCd;

    @JsonProperty("itemNm")
    @Description(name = "항목명", description = "원가계산서 Field", type = Description.TYPE.FIELD)
    String itemNm;

    @JsonProperty("costAmt")
    @Description(name = "산출금액", description = "원가계산서 Field", type = Description.TYPE.FIELD)
    Long costAmt;

    /* 06월 추가 요청 항목 */
    @JsonProperty("calcMthdNm")
    @Description(name = "적용산출식명", description = "원가계산서 Field", type = Description.TYPE.FIELD)
    String calcMthdNm;

    @JsonProperty("calcMthdCntnts")
    @Description(name = "적용산출식", description = "원가계산서 Field", type = Description.TYPE.FIELD)
    String calcMthdCntnts;

    @JsonProperty("aplyRatePt")
    @Description(name = "적용요율", description = "원가계산서 Field", type = Description.TYPE.FIELD)
    Double aplyRatePt;
}
