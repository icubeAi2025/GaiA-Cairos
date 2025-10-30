package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@IdClass(CnContractBidId.class)
public class CnContractBid extends AbstractRudIdTime {
    @Id
    @Description(name = "계약번호", description = "", type = Description.TYPE.FIELD)
    String cntrctNo; // 계약번호

    @Id
    @Column(columnDefinition = "NUMERIC")
    @Description(name = "cbs순번", description = "", type = Description.TYPE.FIELD)
    Long cbsSno; // cbs순번

    @Description(name = "제경비여부", description = "", type = Description.TYPE.FIELD)
    String expnssYn; // 제경비여부(기본값 N)

    // 직접공사비
    @Column(columnDefinition = "NUMERIC")
    @Description(name = "계약단위공사순번", description = "", type = Description.TYPE.FIELD)
    double cntrctUnitCnstwkSno; // 계약단위공사순번

    @Column(columnDefinition = "NUMERIC")
    @Description(name = "계약세부공종순번", description = "", type = Description.TYPE.FIELD)
    double cntrctDcnsttySno; // 계약세부공종순번

    @Column(columnDefinition = "NUMERIC")
    @Description(name = "상위세부공종순번", description = "", type = Description.TYPE.FIELD)
    double upCntrctDcnsttySno; // 상위세부공종순번

    @Column(columnDefinition = "NUMERIC")
    @Description(name = "원가공종순번값", description = "", type = Description.TYPE.FIELD)
    double cstCnsttySnoVal; // 원가공종순번값

    // 제경비
    @Column(columnDefinition = "NUMERIC")
    @Description(name = "제경비순번", description = "", type = Description.TYPE.FIELD)
    double expnssSno; // 제경비순번

    @Description(name = "원가계산서위치코드", description = "", type = Description.TYPE.FIELD)
    String cstBillLctCd; // 원가계산서위치코드

    @Description(name = "제경비종류코드", description = "", type = Description.TYPE.FIELD)
    String expnssKindCd; // 제경비종류코드

    @Column(columnDefinition = "NUMERIC")
    @Description(name = "제경비요율백분율", description = "", type = Description.TYPE.FIELD)
    double expnssBscrtPct; // 제경비요율백분율

    @Column(columnDefinition = "NUMERIC")
    @Description(name = "직접공사비백분율", description = "", type = Description.TYPE.FIELD)
    double drctCnstcstPct; // 직접공사비백분율

    @Description(name = "제경비산출식코드", description = "", type = Description.TYPE.FIELD)
    String expnssCalcfrmlaCd; // 제경비산출식코드

    @Description(name = "계약세부공종코드", description = "", type = Description.TYPE.FIELD)
    char cntrctCnstType; // 계약세부공종코드 241122 추가

    @Description(name = "공종내역구분코드", description = "", type = Description.TYPE.FIELD)
    String cnsttyDtlsDivCd; // 공종내역구분코드

    @Column(columnDefinition = "NUMERIC")
    @Description(name = "세부공종레벨수", description = "", type = Description.TYPE.FIELD)
    double dcnsttyLvlNum; // 세부공종레벨수

    @Description(name = "품명", description = "", type = Description.TYPE.FIELD)
    String prdnm; // 품명

    @Description(name = "규격", description = "", type = Description.TYPE.FIELD)
    String spec; // 규격

    @Description(name = "단위", description = "", type = Description.TYPE.FIELD)
    String unit; // 단위

    @Column(columnDefinition = "NUMERIC")
    @Description(name = "수량", description = "", type = Description.TYPE.FIELD)
    BigDecimal qty; // 수량

    @Column(columnDefinition = "NUMERIC")
    @Description(name = "재료비단가", description = "", type = Description.TYPE.FIELD)
    double mtrlcstUprc; // 재료비단가

    @Column(columnDefinition = "NUMERIC")
    @Description(name = "노무비단가", description = "", type = Description.TYPE.FIELD)
    double lbrcstUprc; // 노무비단가

    @Column(columnDefinition = "NUMERIC")
    @Description(name = "경비단가", description = "", type = Description.TYPE.FIELD)
    double gnrlexpnsUprc; // 경비단가

    @Column(columnDefinition = "NUMERIC")
    @Description(name = "합계단가", description = "", type = Description.TYPE.FIELD)
    double sumUprc; // 합계단가

    @Column(columnDefinition = "NUMERIC")
    @Description(name = "재료비금액", description = "", type = Description.TYPE.FIELD)
    double mtrlcstAmt; // 재료비금액

    @Column(columnDefinition = "NUMERIC")
    @Description(name = "노무비금액", description = "", type = Description.TYPE.FIELD)
    double lbrcstAmt; // 노무비금액

    @Column(columnDefinition = "NUMERIC")
    @Description(name = "경비금액", description = "", type = Description.TYPE.FIELD)
    double gnrlexpnsAmt; // 경비금액

    @Column(columnDefinition = "NUMERIC")
    @Description(name = "합계금액", description = "", type = Description.TYPE.FIELD)
    double sumAmt; // 합계금액

    @Description(name = "비고", description = "", type = Description.TYPE.FIELD)
    String rmrk; // 비고

    @Description(name = "세부공종금액유형코드", description = "", type = Description.TYPE.FIELD)
    String dcnsttyAmtTyCd; // 세부공종금액유형코드(기본값 0)

    @Description(name = "표준시장단가코드", description = "", type = Description.TYPE.FIELD)
    String stdMrktUprcCd; // 표준시장단가코드

    @Description(name = "원가자원코드", description = "", type = Description.TYPE.FIELD)
    String cstRsceCd; // 원가자원코드

    @Description(name = "매입세대상여부", description = "", type = Description.TYPE.FIELD)
    String buytaxObjYn; // 매입세대상여부(기본값 N)

    @Description(name = "원가자원유형코드", description = "", type = Description.TYPE.FIELD)
    String cstRsceTyCd; // 원가자원유형코드

    @Description(name = "물량번경허용여부", description = "", type = Description.TYPE.FIELD)
    String oqtyChgPermsnYn; // 물량번경허용여부(기본값 N)

    @Description(name = "원가단위공사번호", description = "", type = Description.TYPE.FIELD)
    String cstUnitCnstwkNo; // 원가단위공사번호

    @Column(columnDefinition = "NUMERIC")
    @Description(name = "원가공종순번", description = "", type = Description.TYPE.FIELD)
    double cstCnsttySno; // 원가공종순번

    @Column(columnDefinition = "NUMERIC")
    @Description(name = "원가세부공종순번", description = "", type = Description.TYPE.FIELD)
    double cstDcnsttySno; // 원가세부공종순번

    @Description(name = "삭제여부", description = "", type = Description.TYPE.FIELD)
    String dltYn; // 삭제여부

}
