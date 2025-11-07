package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@IdClass(CwMainmtrlId.class)
public class CwMainmtrl extends AbstractRudIdTime {

    @Description(name = "검수요청서 No", description = "", type = Description.TYPE.FIELD)
    String reqfrmNo;        // 검수요청서 No

    @Description(name = "계약번호", description = "", type = Description.TYPE.FIELD)
    String cntrctNo;         // 계약번호

    @Id
    @Description(name = "자원코드", description = "", type = Description.TYPE.FIELD)
    String gnrlexpnsCd;   // 자원코드

    @Description(name = "자원명", description = "", type = Description.TYPE.FIELD)
    String rsceNm;         // 자원명

    @Description(name = "규격명", description = "", type = Description.TYPE.FIELD)
    String specNm;         // 규격명

    @Description(name = "단위", description = "", type = Description.TYPE.FIELD)
    String unit;           // 단위

    @Description(name = "금일반입량", description = "", type = Description.TYPE.FIELD)
    BigDecimal todayQty;       // 금일반입량

    @Description(name = "합격수량", description = "", type = Description.TYPE.FIELD)
    BigDecimal passQty;        // 합격수량

    @Description(name = "불합격수량", description = "", type = Description.TYPE.FIELD)
    BigDecimal failQty;        // 불합격수량

    @Description(name = "비고", description = "", type = Description.TYPE.FIELD)
    String rmrk;                // 비고

    @Description(name = "삭제여부", description = "", type = Description.TYPE.FIELD)
    String dltYn;              // 삭제여부
}
