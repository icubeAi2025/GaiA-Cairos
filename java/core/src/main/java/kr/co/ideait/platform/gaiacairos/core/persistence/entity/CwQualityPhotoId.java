package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import java.io.Serializable;
import java.util.Objects;

public class CwQualityPhotoId implements Serializable {
    private String qltyIspId;
    private String cntrctNo;
    private int phtSno;

    public CwQualityPhotoId() {}

    public CwQualityPhotoId(String qltyIspId, String cntrctNo, int phtSno) {
        this.qltyIspId = qltyIspId;
        this.cntrctNo = cntrctNo;
        this.phtSno = phtSno;
    }

    // equals()와 hashCode() 메서드 구현
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CwQualityPhotoId that = (CwQualityPhotoId) o;
        return Objects.equals(qltyIspId, that.qltyIspId) &&
               Objects.equals(cntrctNo, that.cntrctNo) &&
               Objects.equals(phtSno, that.phtSno);
    }

    @Override
    public int hashCode() {
        return Objects.hash(qltyIspId, cntrctNo, phtSno);
    }
}

