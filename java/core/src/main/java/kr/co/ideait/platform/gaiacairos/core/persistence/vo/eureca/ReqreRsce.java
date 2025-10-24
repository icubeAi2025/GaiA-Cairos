package kr.co.ideait.platform.gaiacairos.core.persistence.vo.eureca;

import com.fasterxml.jackson.annotation.JsonProperty;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 소요자원내역
 */
@Data
public class ReqreRsce extends BaseSyncModel {
    @Description(name = "상위 내역순번", description = "Field", type = Description.TYPE.FIELD)
    Long upDtlSn;

    @JsonProperty("dtlsSn")
    @Description(name = "유레카 생성 내역순번", description = "소요자원내역 Field", type = Description.TYPE.FIELD)
    Long dtlsSn;


    @JsonProperty("rsceTpCd")
    @Description(name = "자원유형코드", description = "소요자원내역 Field", type = Description.TYPE.FIELD)
    String rsceTpCd;

    @JsonProperty("rsceCd")
    @Description(name = "자원코드", description = "소요자원내역 Field", type = Description.TYPE.FIELD)
    String rsceCd;

    @JsonProperty("rsceNm")
    @Description(name = "자원명", description = "소요자원내역 Field", type = Description.TYPE.FIELD)
    String rsceNm;

    @JsonProperty("specNm")
    @Description(name = "규격명", description = "소요자원내역 Field", type = Description.TYPE.FIELD)
    String specNm;

    @JsonProperty("unit")
    @Description(name = "단위", description = "소요자원내역 Field", type = Description.TYPE.FIELD)
    String unit;

    @JsonProperty("rsceQty")
    @Description(name = "자원수량", description = "소요자원내역 Field", type = Description.TYPE.FIELD)
    BigDecimal rsceQty;

    // 2025-08-08 관급자재여부 추가
    @JsonProperty("govsplyMtrlYn")
    @Description(name = "관급자재여부", description = "소요자원내역 Field", type = Description.TYPE.FIELD)
    String govsplyMtrlYn;
}
