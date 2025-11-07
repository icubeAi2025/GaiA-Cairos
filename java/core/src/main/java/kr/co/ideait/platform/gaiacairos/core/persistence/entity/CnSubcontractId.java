package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import java.io.Serializable;
import java.util.Objects;

public class CnSubcontractId implements Serializable {
    private String cntrctNo;
    private Long scontrctCorpId;

    public CnSubcontractId() {
    }

    public CnSubcontractId(String cntrctNo, Long scontrctCorpId) {
        this.cntrctNo = cntrctNo;
        this.scontrctCorpId = scontrctCorpId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        CnSubcontractId that = (CnSubcontractId) o;
        return Objects.equals(cntrctNo, that.cntrctNo) &&
                Objects.equals(scontrctCorpId, that.scontrctCorpId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cntrctNo, scontrctCorpId);
    }
}
