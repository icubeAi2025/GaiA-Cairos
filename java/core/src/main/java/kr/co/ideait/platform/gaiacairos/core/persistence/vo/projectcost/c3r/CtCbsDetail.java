package kr.co.ideait.platform.gaiacairos.core.persistence.vo.projectcost.c3r;

import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 세부공종
 */
@Data
public class CtCbsDetail {
    @Description(name = "계약변경ID", description = "세부공종 Field", type = Description.TYPE.FIELD)
    String cntrctChgId;
    @Description(name = "공종순번", description = "세부공종 Field", type = Description.TYPE.FIELD)
    Long cnsttySn;
    @Description(name = "세부공종순번", description = "세부공종 Field", type = Description.TYPE.FIELD)
    Long dtlCnsttySn;
    @Description(name = "자원유형코드", description = "세부공종 Field", type = Description.TYPE.FIELD)
    String rsceTpCd;
    @Description(name = "자원코드", description = "세부공종 Field", type = Description.TYPE.FIELD)
    String rsceCd;
    @Description(name = "세부공종명", description = "세부공종 Field", type = Description.TYPE.FIELD)
    String dtlCnsttyNm;
    @Description(name = "규격명", description = "세부공종 Field", type = Description.TYPE.FIELD)
    String specNm;
    @Description(name = "단위명", description = "세부공종 Field", type = Description.TYPE.FIELD)
    String unit;
    @Description(name = "단위공사구분코드", description = "세부공종 Field", type = Description.TYPE.FIELD)
    String unitCnstType;
    @Description(name = "자원수량", description = "세부공종 Field", type = Description.TYPE.FIELD)
    BigDecimal rsceQty;
    @Description(name = "금차수량", description = "세부공종 Field", type = Description.TYPE.FIELD)
    BigDecimal thisRsceQty;
    @Description(name = "재료비단가", description = "세부공종 Field", type = Description.TYPE.FIELD)
    Double mtrlUprc;
    @Description(name = "노무비단가", description = "세부공종 Field", type = Description.TYPE.FIELD)
    Double lbrUprc;
    @Description(name = "경비단가", description = "세부공종 Field", type = Description.TYPE.FIELD)
    Double gnrlexpnsUprc;
    @Description(name = "계산제외여부", description = "세부공종 Field", type = Description.TYPE.FIELD)
    String calcExcpYn;
    @Description(name = "관급자재여부", description = "세부공종 Field", type = Description.TYPE.FIELD)
    String govsplyMtrlYn;
    @Description(name = "친환경여부", description = "세부공종 Field", type = Description.TYPE.FIELD)
    String EcoFriendlyYn;
    @Description(name = "면세세부공종여부", description = "세부공종 Field", type = Description.TYPE.FIELD)
    String ftaxDtlCnsttyYn;
    @Description(name = "표준세부공정여부", description = "세부공종 Field", type = Description.TYPE.FIELD)
    String stdDtlCnsttyYn;
    @Description(name = "등록자id", description = "세부공종 Field", type = Description.TYPE.FIELD)
    String rgstrId;
    @Description(name = "수정자id", description = "세부공종 Field", type = Description.TYPE.FIELD)
    String chgId;
    @Description(name = "삭제자id", description = "세부공종 Field", type = Description.TYPE.FIELD)
    String dltId;
    @Description(name = "삭제여부", description = "세부공종 Field", type = Description.TYPE.FIELD)
    String dltYn;
}
