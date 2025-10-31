package kr.co.ideait.platform.gaiacairos.core.persistence.vo.projectcost.c3r;


import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 계약내역서 (BID)
 */
@Data
public class CnContractBid {
    @Description(name = "계약번호", description = "계약내역서 Field", type = Description.TYPE.FIELD)
    String cntrctNo;
    @Description(name = "cbs순번", description = "계약내역서 Field", type = Description.TYPE.FIELD)
    Long cbsSno;
    @Description(name = "제경비여부", description = "계약내역서 Field", type = Description.TYPE.FIELD)
    String expnssYn;
    @Description(name = "세부공종레벨수", description = "계약내역서 Field", type = Description.TYPE.FIELD)
    Long dcnsttyLvlNum;
    @Description(name = "수량", description = "계약내역서 Field", type = Description.TYPE.FIELD)
    BigDecimal qty;
    @Description(name = "매입세대상여부", description = "계약내역서 Field", type = Description.TYPE.FIELD)
    String buytaxObjYn;
    @Description(name = "물량변경허용여부", description = "계약내역서 Field", type = Description.TYPE.FIELD)
    String oqtyChgPermsnYn;
    @Description(name = "계약단위공사순번", description = "계약내역서 Field", type = Description.TYPE.FIELD)
    Long cntrctUnitCnstwkSno;
    @Description(name = "계약세부공종순번", description = "계약내역서 Field", type = Description.TYPE.FIELD)
    Long cntrctDcnsttySno;
    @Description(name = "상위계약세부공종순번", description = "계약내역서 Field", type = Description.TYPE.FIELD)
    Long upCntrctDcnsttySno;
    @Description(name = "원가공종순번값", description = "계약내역서 Field", type = Description.TYPE.FIELD)
    Long cstCnsttySnoVal;
    @Description(name = "제경비순번", description = "계약내역서 Field", type = Description.TYPE.FIELD)
    Long expnssSno;
    @Description(name = "원가계산서위치코드", description = "계약내역서 Field", type = Description.TYPE.FIELD)
    String cstBillLctCd;
    @Description(name = "제경비종류코드", description = "계약내역서 Field", type = Description.TYPE.FIELD)
    String expnssKindCd;
    @Description(name = "제경비요율백분율", description = "계약내역서 Field", type = Description.TYPE.FIELD)
    Double expnssBscrtPct;
    @Description(name = "직접공사비백분율", description = "계약내역서 Field", type = Description.TYPE.FIELD)
    Double drctCnstcstPct;
    @Description(name = "제경비산출식코드", description = "계약내역서 Field", type = Description.TYPE.FIELD)
    String expnssCalcfrmlaCd;
    @Description(name = "계약세부공종코드", description = "계약내역서 Field", type = Description.TYPE.FIELD)
    String cntrctCnstType;
    @Description(name = "공종내역구분코드", description = "계약내역서 Field", type = Description.TYPE.FIELD)
    String cnsttyDtlsDivCd;
    @Description(name = "품명", description = "계약내역서 Field", type = Description.TYPE.FIELD)
    String prdnm;
    @Description(name = "규격", description = "계약내역서 Field", type = Description.TYPE.FIELD)
    String spec;
    @Description(name = "단위", description = "계약내역서 Field", type = Description.TYPE.FIELD)
    String unit;
    @Description(name = "재료비단가", description = "계약내역서 Field", type = Description.TYPE.FIELD)
    Double mtrlcstUprc;
    @Description(name = "노무비단가", description = "계약내역서 Field", type = Description.TYPE.FIELD)
    Double lbrcstUprc;
    @Description(name = "경비단가", description = "계약내역서 Field", type = Description.TYPE.FIELD)
    Double gnrlexpnsUprc;
    @Description(name = "합계단가", description = "계약내역서 Field", type = Description.TYPE.FIELD)
    Long sumUprc;
    @Description(name = "재료비금액", description = "계약내역서 Field", type = Description.TYPE.FIELD)
    Long mtrlcstAmt;
    @Description(name = "노무비금액", description = "계약내역서 Field", type = Description.TYPE.FIELD)
    Long lbrcstAmt;
    @Description(name = "경비금액", description = "계약내역서 Field", type = Description.TYPE.FIELD)
    Long gnrlexpnsAmt;
    @Description(name = "합계금액", description = "계약내역서 Field", type = Description.TYPE.FIELD)
    Long sumAmt;
    @Description(name = "비고", description = "계약내역서 Field", type = Description.TYPE.FIELD)
    String rmrk;
    @Description(name = "세부공종금액유형코드", description = "계약내역서 Field", type = Description.TYPE.FIELD)
    String dcnsttyAmtTyCd;
    @Description(name = "표준시장단가코드", description = "계약내역서 Field", type = Description.TYPE.FIELD)
    String stdMrktUprcCd;
    @Description(name = "원가자원코드", description = "계약내역서 Field", type = Description.TYPE.FIELD)
    String cstRsceCd;
    @Description(name = "원가자원유형코드", description = "계약내역서 Field", type = Description.TYPE.FIELD)
    String cstRsceTyCd;
    @Description(name = "원가단위공사번호", description = "계약내역서 Field", type = Description.TYPE.FIELD)
    Long cstUnitCnstwkNo;
    @Description(name = "원가공종순번", description = "계약내역서 Field", type = Description.TYPE.FIELD)
    Long cstCnsttySno;
    @Description(name = "원가세부공종순번", description = "계약내역서 Field", type = Description.TYPE.FIELD)
    Long cstDcnsttySno;
    @Description(name = "등록자id", description = "계약내역서 Field", type = Description.TYPE.FIELD)
    String rgstrId;
    @Description(name = "수정자id", description = "계약내역서 Field", type = Description.TYPE.FIELD)
    String chgId;
    @Description(name = "삭제자id", description = "계약내역서 Field", type = Description.TYPE.FIELD)
    String dltId;
    @Description(name = "삭제여부", description = "계약내역서 Field", type = Description.TYPE.FIELD)
    String dltYn;
}

