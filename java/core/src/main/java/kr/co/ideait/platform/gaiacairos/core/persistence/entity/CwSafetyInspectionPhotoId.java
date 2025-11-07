package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import java.io.Serializable;
import java.util.Objects;

public class CwSafetyInspectionPhotoId implements Serializable {
    private String inspectionNo;
    private String cntrctNo;
    private short phtSno;
    private Integer atchFileNo;

    public CwSafetyInspectionPhotoId() {
    }

    public CwSafetyInspectionPhotoId(String inspectionNo, String cntrctNo, short phtSno, Integer atchFileNo) {
        this.inspectionNo = inspectionNo;
        this.cntrctNo = cntrctNo;
        this.phtSno = phtSno;
        this.atchFileNo = atchFileNo;
    }

    // equals()와 hashCode() 메서드 구현
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        CwSafetyInspectionPhotoId that = (CwSafetyInspectionPhotoId) o;
        return Objects.equals(inspectionNo, that.inspectionNo) &&
                Objects.equals(cntrctNo, that.cntrctNo) &&
                Objects.equals(phtSno, that.phtSno) &&
                Objects.equals(atchFileNo, that.atchFileNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inspectionNo, cntrctNo, phtSno, atchFileNo);
    }
}
