package kr.co.ideait.platform.gaiacairos.core.persistence.vo.projectcost.c3r;

import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 곻종자원
 */
@Data
public class CtCbsResource {
    @Description(name = "계약변경ID", description = "공종자원 Field", type = Description.TYPE.FIELD)
    String cntrctChgId;
    @Description(name = "단위공사구분코드", description = "공종자원 Field", type = Description.TYPE.FIELD)
    String unitCnstType;
    @Description(name = "공종순번", description = "공종자원 Field", type = Description.TYPE.FIELD)
    Long cnsttySn;
    @Description(name = "세부공종순번", description = "공종자원 Field", type = Description.TYPE.FIELD)
    Long dtlCnsttySn;
    @Description(name = "상세순번", description = "공종자원 Field", type = Description.TYPE.FIELD)
    Long dtlSn;
    @Description(name = "자원유형코드", description = "공종자원 Field", type = Description.TYPE.FIELD)
    String rsceTpCd;
    @Description(name = "자원코드", description = "공종자원 Field", type = Description.TYPE.FIELD)
    String gnrlexpnsCd;
    @Description(name = "자원명", description = "공종자원 Field", type = Description.TYPE.FIELD)
    String rsceNm;
    @Description(name = "규격명", description = "공종자원 Field", type = Description.TYPE.FIELD)
    String specNm;
    @Description(name = "단위", description = "공종자원 Field", type = Description.TYPE.FIELD)
    String unit;
    @Description(name = "단위수량", description = "공종자원 Field", type = Description.TYPE.FIELD)
    BigDecimal unitQty;
    @Description(name = "등록자id", description = "공종자원 Field", type = Description.TYPE.FIELD)
    String rgstrId;
    @Description(name = "수정자id", description = "공종자원 Field", type = Description.TYPE.FIELD)
    String chgId;
    @Description(name = "삭제자id", description = "공종자원 Field", type = Description.TYPE.FIELD)
    String dltId;
    @Description(name = "삭제여부", description = "공종자원 Field", type = Description.TYPE.FIELD)
    String dltYn;

    // 2025-08-08 관급자재여부 추가
    @Description(name = "관급자재여부", description = "공종자원 Field", type = Description.TYPE.FIELD)
    String govsplyMtrlYn;

    // 2025-08-28 총 수량 (자원수량 * 단위수량) 추가
    @Description(name = "총 수량(자원수량 * 단위수량)", description = "공종자원 Field", type = Description.TYPE.FIELD)
    BigDecimal totalQty;
}
