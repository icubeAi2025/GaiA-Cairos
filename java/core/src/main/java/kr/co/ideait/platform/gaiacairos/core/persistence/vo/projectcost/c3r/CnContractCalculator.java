package kr.co.ideait.platform.gaiacairos.core.persistence.vo.projectcost.c3r;

import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;

/**
 * 원가계산서
 */
@Data
public class CnContractCalculator {
    @Description(name = "계약번호", description = "원가계산서 Field", type = Description.TYPE.FIELD)
    String cntrctNo;
    @Description(name = "원가산출항목코드", description = "원가계산서 Field", type = Description.TYPE.FIELD)
    String cstCalcItCd;
    @Description(name = "상위원가산출항목코드", description = "원가계산서 Field", type = Description.TYPE.FIELD)
    String upCstCalcItCd;
    @Description(name = "표시순서", description = "원가계산서 Field", type = Description.TYPE.FIELD)
    Long dsplyOrdr;
    @Description(name = "원가산출항목명", description = "원가계산서 Field", type = Description.TYPE.FIELD)
    String cstCalcItNm;
    @Description(name = "원가산출방법명", description = "원가계산서 Field", type = Description.TYPE.FIELD)
    String cstCalcMthdNm;
    @Description(name = "원가산출방법수식", description = "원가계산서 Field", type = Description.TYPE.FIELD)
    String cstCalcMthdNomfrmCntnts;
    @Description(name = "제경비백분율", description = "원가계산서 Field", type = Description.TYPE.FIELD)
    Double ovrhdcstPt;
    @Description(name = "직공비대비백분율", description = "원가계산서 Field", type = Description.TYPE.FIELD)
    Double drcnstcostCmprPt;
    @Description(name = "원가계산서표시값", description = "원가계산서 Field", type = Description.TYPE.FIELD)
    String cstCalcbllDsplyVal;
    @Description(name = "비용금액", description = "원가계산서 Field", type = Description.TYPE.FIELD)
    Long costAm;
    @Description(name = "등록자id", description = "원가계산서 Field", type = Description.TYPE.FIELD)
    String rgstrId;
    @Description(name = "수정자id", description = "원가계산서 Field", type = Description.TYPE.FIELD)
    String chgId;
    @Description(name = "삭제자id", description = "원가계산서 Field", type = Description.TYPE.FIELD)
    String dltId;
    @Description(name = "삭제여부", description = "원가계산서 Field", type = Description.TYPE.FIELD)
    String dltYn;
}
