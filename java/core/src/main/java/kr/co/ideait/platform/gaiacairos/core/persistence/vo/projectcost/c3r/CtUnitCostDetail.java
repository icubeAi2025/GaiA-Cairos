package kr.co.ideait.platform.gaiacairos.core.persistence.vo.projectcost.c3r;

import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 일위대가상세
 */
@Data
public class CtUnitCostDetail {
    @Description(name = "계약변경ID", description = "일위대가상세 Field", type = Description.TYPE.FIELD)
    String cntrctChgId;
    @Description(name = "단위공사구분코드", description = "일위대가상세 Field", type = Description.TYPE.FIELD)
    String unitCnstType;
    @Description(name = "일위대가코드", description = "일위대가상세 Field", type = Description.TYPE.FIELD)
    String ucostCd;
    @Description(name = "상세순번", description = "일위대가상세 Field", type = Description.TYPE.FIELD)
    Long dtlSn;

    @Description(name = "계산제외여부", description = "일위대가상세 Field", type = Description.TYPE.FIELD)
    String calcExcpYn;
    @Description(name = "관급자재여부", description = "일위대가상세 Field", type = Description.TYPE.FIELD)
    String govsplyMtrlYn;

    @Description(name = "자원유형코드", description = "일위대가상세 Field", type = Description.TYPE.FIELD)
    String rsceTpCd;
    @Description(name = "자원코드", description = "일위대가상세 Field", type = Description.TYPE.FIELD)
    String rsceCd;
    @Description(name = "자원명", description = "일위대가상세 Field", type = Description.TYPE.FIELD)
    String rsceNm;
    @Description(name = "규격명", description = "일위대가상세 Field", type = Description.TYPE.FIELD)
    String specNm;
    @Description(name = "단위명", description = "일위대가상세 Field", type = Description.TYPE.FIELD)
    String unit;
    @Description(name = "자원수량", description = "일위대가상세 Field", type = Description.TYPE.FIELD)
    BigDecimal rsceQty;
    @Description(name = "재료비수량", description = "일위대가상세 Field", type = Description.TYPE.FIELD)
    BigDecimal mtrlQty;
    @Description(name = "노무비수량", description = "일위대가상세 Field", type = Description.TYPE.FIELD)
    BigDecimal lbrQty;
    @Description(name = "경비수량", description = "일위대가상세 Field", type = Description.TYPE.FIELD)
    BigDecimal gnrlexpnsQty;


    @Description(name = "재료비단가", description = "일위대가상세 Field", type = Description.TYPE.FIELD)
    Long mtrlUprc;
    @Description(name = "노무비단가", description = "일위대가상세 Field", type = Description.TYPE.FIELD)
    Long lbrUprc;
    @Description(name = "경비단가", description = "일위대가상세 Field", type = Description.TYPE.FIELD)
    Long gnrlexpnsUprc;

    @Description(name = "등록자id", description = "일위대가상세 Field", type = Description.TYPE.FIELD)
    String rgstrId;
    @Description(name = "수정자id", description = "일위대가상세 Field", type = Description.TYPE.FIELD)
    String chgId;
    @Description(name = "삭제자id", description = "일위대가상세 Field", type = Description.TYPE.FIELD)
    String dltId;
    @Description(name = "삭제여부", description = "일위대가상세 Field", type = Description.TYPE.FIELD)
    String dltYn;
}
