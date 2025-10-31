package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import java.io.Serializable;
import java.util.Objects;

public class CwStandardInspectionListId implements Serializable {
    private String cntrctNo;
    private String ispLstId;

    public CwStandardInspectionListId() {}

    public CwStandardInspectionListId(String cntrctNo, String ispLstId) {
        this.cntrctNo = cntrctNo;
        this.ispLstId = ispLstId;
    }

    // equals()와 hashCode() 메서드 구현
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CwStandardInspectionListId that = (CwStandardInspectionListId) o;
        return Objects.equals(cntrctNo, that.cntrctNo) &&
               Objects.equals(ispLstId, that.ispLstId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cntrctNo, ispLstId);
    }
}

