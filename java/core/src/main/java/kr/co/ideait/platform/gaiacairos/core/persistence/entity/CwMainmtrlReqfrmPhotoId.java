package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import java.io.Serializable;
import java.util.Objects;

public class CwMainmtrlReqfrmPhotoId implements Serializable {
    private String reqfrmNo;
    private String cntrctNo;
    private int phtSno;

    public CwMainmtrlReqfrmPhotoId() {}

    public CwMainmtrlReqfrmPhotoId(String reqfrmNo, String cntrctNo, int phtSno) {
        this.reqfrmNo = reqfrmNo;
        this.cntrctNo = cntrctNo;
        this.phtSno = phtSno;
    }

    // equals()와 hashCode() 메서드 구현
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CwMainmtrlReqfrmPhotoId that = (CwMainmtrlReqfrmPhotoId) o;
        return Objects.equals(reqfrmNo, that.reqfrmNo) &&
               Objects.equals(cntrctNo, that.cntrctNo) &&
               Objects.equals(phtSno, that.phtSno);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reqfrmNo, cntrctNo, phtSno);
    }
}
