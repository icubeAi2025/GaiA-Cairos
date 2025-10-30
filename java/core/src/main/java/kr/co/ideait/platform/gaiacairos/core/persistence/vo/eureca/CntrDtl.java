package kr.co.ideait.platform.gaiacairos.core.persistence.vo.eureca;

import com.fasterxml.jackson.annotation.JsonProperty;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 계약내역서
 */
@Data
public class CntrDtl extends BaseSyncModel {
    @JsonProperty("dtlsSn")
    @Description(name = "유레카 생성 내역순번", description = "계약내역서 Field", type = Description.TYPE.FIELD)
    private Long dtlsSn;

    @JsonProperty("bidUnitCnstwkSn")
    @Description(name = " BID 단위공사 순번", description = "계약내역서 Field", type = Description.TYPE.FIELD)
    private Long bidUnitCnstwkSn;

    @JsonProperty("bidDtlsSn")
    @Description(name = "BID 내역 순번", description = "계약내역서 Field", type = Description.TYPE.FIELD)
    private Long bidDtlsSn;

    @JsonProperty("dtlsCnsttyDivCd")
    @Description(name = "내역공종구분코드 - G(공종), S(세부공종)", description = "계약내역서 Field", type = Description.TYPE.FIELD)
    private String dtlsCnsttyDivCd;

    @JsonProperty("dtlsNm")
    @Description(name = "내역명", description = "계약내역서 Field", type = Description.TYPE.FIELD)
    private String dtlsNm;

    @JsonProperty("spec")
    @Description(name = "규격", description = "계약내역서 Field", type = Description.TYPE.FIELD)
    private String spec;

    @JsonProperty("unit")
    @Description(name = "단위", description = "계약내역서 Field", type = Description.TYPE.FIELD)
    private String unit;

    @JsonProperty("dtlsQty")
    @Description(name = "내역수량", description = "계약내역서 Field", type = Description.TYPE.FIELD)
    private BigDecimal dtlsQty;

    @JsonProperty("mtrlUprc")
    @Description(name = "재료비단가", description = "계약내역서 Field", type = Description.TYPE.FIELD)
    private Double mtrlUprc;

    @JsonProperty("lbrUprc")
    @Description(name = "노무비단가", description = "계약내역서 Field", type = Description.TYPE.FIELD)
    private Double lbrUprc;

    @JsonProperty("gnrlexpnsUprc")
    @Description(name = "경비단가", description = "계약내역서 Field", type = Description.TYPE.FIELD)
    private Double gnrlexpnsUprc;

    @JsonProperty("mtrlAmt")
    @Description(name = "재료비금액", description = "계약내역서 Field", type = Description.TYPE.FIELD)
    private Long mtrlAmt;

    @JsonProperty("lbrAmt")
    @Description(name = "노무비금액", description = "계약내역서 Field", type = Description.TYPE.FIELD)
    private Long lbrAmt;

    @JsonProperty("gnrlexpnsAmt")
    @Description(name = "경비금액", description = "계약내역서 Field", type = Description.TYPE.FIELD)
    private Long gnrlexpnsAmt;

    @JsonProperty("rsceTpCd")
    @Description(name = "자원유형코드", description = "계약내역서 Field", type = Description.TYPE.FIELD)
    private String rsceTpCd;

    @JsonProperty("rsceCd")
    @Description(name = "자원코드", description = "계약내역서 Field", type = Description.TYPE.FIELD)
    private String rsceCd;

    @JsonProperty("calcExcpYn")
    @Description(name = "계산제외여부", description = "계약내역서 Field", type = Description.TYPE.FIELD)
    private String calcExcpYn;

    @JsonProperty("govsplyMtrlYn")
    @Description(name = "관급자재여부", description = "계약내역서 Field", type = Description.TYPE.FIELD)
    private String govsplyMtrlYn;

    /* 06월 추가 요청 항목 */
    @JsonProperty("lvlNum")
    @Description(name = "계층구조 레벨 가중치", description = "계약내역서 Field", type = Description.TYPE.FIELD)
    private Long lvlNum;

    @JsonProperty("cnsttyCd")
    @Description(name = "공종코드", description = "계약내역서 Field", type = Description.TYPE.FIELD)
    private String cnsttyCd;

    @JsonProperty("upDtlsSn")
    @Description(name = "상위내역순번", description = "계약내역서 Field", type = Description.TYPE.FIELD)
    private Long upDtlsSn;

    @JsonProperty("upCnsttyCd")
    @Description(name = "상위공종코드", description = "계약내역서 Field", type = Description.TYPE.FIELD)
    private String upCnsttyCd;

    @JsonProperty("unitCnstwkDivCd")
    @Description(name = "단위공사구분코드", description = "계약내역서 Field", type = Description.TYPE.FIELD)
    private String unitCnstwkDivCd;

    @JsonProperty("cnsttyCstDivCd")
    @Description(name = "공종구분코드", description = "계약내역서 Field", type = Description.TYPE.FIELD)
    private String cnsttyCstDivCd;

}
