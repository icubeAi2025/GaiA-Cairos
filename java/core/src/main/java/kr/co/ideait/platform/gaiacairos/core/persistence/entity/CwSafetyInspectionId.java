package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import java.io.Serializable;
import java.util.Objects;

public class CwSafetyInspectionId implements Serializable {
    private String cntrctNo;
    private String inspectionNo;

    public CwSafetyInspectionId() {}
    
    public CwSafetyInspectionId(String cntrctNo, String inspectionNo) {
        this.cntrctNo = cntrctNo;
        this.inspectionNo = inspectionNo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CwSafetyInspectionId that = (CwSafetyInspectionId) o;
        return Objects.equals(cntrctNo, that.cntrctNo) &&
               Objects.equals(inspectionNo, that.inspectionNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cntrctNo, inspectionNo);
    }
}
