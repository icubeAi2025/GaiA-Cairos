package kr.co.ideait.platform.gaiacairos.core.persistence.vo.eureca;

import com.fasterxml.jackson.annotation.JsonProperty;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;

/**
 * 일위대가자원
 */
@Data
public class UcostDtl extends BaseSyncModel {
    @JsonProperty("rsceTpCd")
    @Description(name = "자원유형코드", description = "일위대가자원", type = Description.TYPE.FIELD)
    String rsceTpCd;

    @JsonProperty("rsceCd")
    @Description(name = "자원코드", description = "일위대가자원", type = Description.TYPE.FIELD)
    String rsceCd;

    @JsonProperty("rsceNm")
    @Description(name = "자원명", description = "일위대가자원", type = Description.TYPE.FIELD)
    String rsceNm;

    @JsonProperty("specNm")
    @Description(name = "규격명", description = "일위대가자원", type = Description.TYPE.FIELD)
    String specNm;

    @JsonProperty("unit")
    @Description(name = "단위", description = "일위대가자원", type = Description.TYPE.FIELD)
    String unit;

    @JsonProperty("rsceQty")
    @Description(name = "자원수량", description = "일위대가자원", type = Description.TYPE.FIELD)
    Double rsceQty;

    @JsonProperty("calcExcpYn")
    @Description(name = "계산제외여부", description = "일위대가자원", type = Description.TYPE.FIELD)
    String calcExcpYn;

    @JsonProperty("govsplyMtrlYn")
    @Description(name = "관급자재여부", description = "일위대가자원", type = Description.TYPE.FIELD)
    String govsplyMtrlYn;

    /* 06월 추가 요청 항목 */
    @JsonProperty("dtlsSn")
    @Description(name = "세부순번", description = "일위대가자원", type = Description.TYPE.FIELD)
    Long dtlsSn;
}
