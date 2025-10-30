package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import java.io.Serializable;
import java.util.Objects;

public class CwMainmtrlId implements Serializable {
    private String reqfrmNo;
    private String cntrctNo;
    private String gnrlexpnsCd;

    public CwMainmtrlId() {}

    public CwMainmtrlId(String reqfrmNo, String cntrctNo, String gnrlexpnsCd) {
        this.reqfrmNo = reqfrmNo;
        this.cntrctNo = cntrctNo;
        this.gnrlexpnsCd = gnrlexpnsCd;
    }

    // equals()와 hashCode() 메서드 구현
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CwMainmtrlId that = (CwMainmtrlId) o;
        return Objects.equals(reqfrmNo, that.reqfrmNo) &&
               Objects.equals(cntrctNo, that.cntrctNo) &&
               Objects.equals(gnrlexpnsCd, that.gnrlexpnsCd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reqfrmNo, cntrctNo, gnrlexpnsCd);
    }
}
