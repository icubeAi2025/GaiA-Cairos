package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import java.io.Serializable;
import java.util.Objects;

public class CwSafetyInspectionListId implements Serializable {
    private String inspectionNo;
    private String cntrctNo;
    private String ispLstId;

    public CwSafetyInspectionListId() {}

    public CwSafetyInspectionListId(String inspectionNo, String cntrctNo, String ispLstId) {
        this.inspectionNo = inspectionNo;
        this.cntrctNo = cntrctNo;
        this.ispLstId = ispLstId;
    }

    // equals()와 hashCode() 메서드 구현
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CwSafetyInspectionListId that = (CwSafetyInspectionListId) o;
        return Objects.equals(inspectionNo, that.inspectionNo) &&
               Objects.equals(cntrctNo, that.cntrctNo) &&
               Objects.equals(ispLstId, that.ispLstId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inspectionNo, cntrctNo, ispLstId);
    }
}

