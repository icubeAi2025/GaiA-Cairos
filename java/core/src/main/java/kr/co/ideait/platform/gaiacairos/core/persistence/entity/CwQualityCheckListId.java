package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import java.io.Serializable;
import java.util.Objects;

public class CwQualityCheckListId implements Serializable {
    private String qltyIspId;
    private String cntrctNo;
    private String chklstId;

    public CwQualityCheckListId() {}

    public CwQualityCheckListId(String qltyIspId, String cntrctNo, String chklstId) {
        this.qltyIspId = qltyIspId;
        this.cntrctNo = cntrctNo;
        this.chklstId = chklstId;
    }

    // equals()와 hashCode() 메서드 구현
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CwQualityCheckListId that = (CwQualityCheckListId) o;
        return Objects.equals(qltyIspId, that.qltyIspId) &&
               Objects.equals(cntrctNo, that.cntrctNo) &&
               Objects.equals(chklstId, that.chklstId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(qltyIspId, cntrctNo, chklstId);
    }
}

