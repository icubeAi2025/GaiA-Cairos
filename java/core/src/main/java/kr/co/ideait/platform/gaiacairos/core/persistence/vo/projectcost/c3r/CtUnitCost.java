package kr.co.ideait.platform.gaiacairos.core.persistence.vo.projectcost.c3r;

import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;

/**
 * 일위대가
 */
@Data
public class CtUnitCost {
    @Description(name = "계약변경ID", description = "일위대가 Field", type = Description.TYPE.FIELD)
    String cntrctChgId;
    @Description(name = "단위공사구분", description = "일위대가 Field", type = Description.TYPE.FIELD)
    String unitCnstType;
    @Description(name = "일위대가코드", description = "일위대가 Field", type = Description.TYPE.FIELD)
    String ucostCd;
    @Description(name = "자원단가적용여부", description = "일위대가 Field", type = Description.TYPE.FIELD)
    String rsceUprcAplyYn;
    
    @Description(name = "적용조건내용", description = "일위대가 Field", type = Description.TYPE.FIELD)
    String aplyCndtnCntnts;
    @Description(name = "중기단가산출식내용", description = "일위대가 Field", type = Description.TYPE.FIELD)
    String hmupCalcfrmlaCntnts;

    @Description(name = "자원유형코드", description = "일위대가 Field", type = Description.TYPE.FIELD)
    String rsceTpCd;
    @Description(name = "자원코드", description = "일위대가 Field", type = Description.TYPE.FIELD)
    String gnrlexpnsCd;
    @Description(name = "자원명", description = "일위대가 Field", type = Description.TYPE.FIELD)
    String rsceNm;
    @Description(name = "규격명", description = "일위대가 Field", type = Description.TYPE.FIELD)
    String specNm;
    @Description(name = "단위", description = "일위대가 Field", type = Description.TYPE.FIELD)
    String unit;
    @Description(name = "재료비단가", description = "일위대가 Field", type = Description.TYPE.FIELD)
    Long mtrlUprc;
    @Description(name = "노무비단가", description = "일위대가 Field", type = Description.TYPE.FIELD)
    Long lbrUprc;
    @Description(name = "경비단가", description = "일위대가 Field", type = Description.TYPE.FIELD)
    Long gnrlexpnsUprc;

    @Description(name = "등록자id", description = "일위대가 Field", type = Description.TYPE.FIELD)
    String rgstrId;
    @Description(name = "수정자id", description = "일위대가 Field", type = Description.TYPE.FIELD)
    String chgId;
    @Description(name = "삭제자id", description = "일위대가 Field", type = Description.TYPE.FIELD)
    String dltId;
    @Description(name = "삭제여부", description = "일위대가 Field", type = Description.TYPE.FIELD)
    String dltYn;
}
