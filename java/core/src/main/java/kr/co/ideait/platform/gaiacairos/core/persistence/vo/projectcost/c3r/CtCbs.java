package kr.co.ideait.platform.gaiacairos.core.persistence.vo.projectcost.c3r;

import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;

/**
 * 공종
 */
@Data
public class CtCbs {
    @Description(name = "계약변경ID", description = "공종 Field", type = Description.TYPE.FIELD)
    String cntrctChgId;
    @Description(name = "공종순번", description = "공종 Field", type = Description.TYPE.FIELD)
    Long cnsttySn;
    @Description(name = "공종레벨", description = "공종 Field", type = Description.TYPE.FIELD)
    Long cnsttyLvlNum;
    @Description(name = "단위공사구분코드", description = "공종 Field", type = Description.TYPE.FIELD)
    String unitCnstType;
    @Description(name = "계산제외여부", description = "공종 Field", type = Description.TYPE.FIELD)
    String calcExcpYn;
    @Description(name = "재료비금액", description = "공종 Field", type = Description.TYPE.FIELD)
    Long mtrlAm;
    @Description(name = "노무비금액", description = "공종 Field", type = Description.TYPE.FIELD)
    Long lbrAm;
    @Description(name = "경비금액", description = "공종 Field", type = Description.TYPE.FIELD)
    Long gnrlexpnsAm;
    @Description(name = "공종코드", description = "공종 Field", type = Description.TYPE.FIELD)
    String cnsttyCd;
    @Description(name = "상위공종코드", description = "공종 Field", type = Description.TYPE.FIELD)
    String upCnsttyCd;
    @Description(name = "공종명", description = "공종 Field", type = Description.TYPE.FIELD)
    String cnsttyNm;
    @Description(name = "공종원가구분코드", description = "공종 Field", type = Description.TYPE.FIELD)
    String cnsttyCstDivCd;

    @Description(name = "등록자id", description = "공종 Field", type = Description.TYPE.FIELD)
    String rgstrId;
    @Description(name = "수정자id", description = "공종 Field", type = Description.TYPE.FIELD)
    String chgId;
    @Description(name = "삭제자id", description = "공종 Field", type = Description.TYPE.FIELD)
    String dltId;
    @Description(name = "삭제여부", description = "공종 Field", type = Description.TYPE.FIELD)
    String dltYn;

}
