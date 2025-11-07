package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import java.io.Serializable;
import java.util.Objects;

public class CnSubcontractChangeId implements Serializable {

    private Long cntrctChgId;
    private Long scontrctCorpId;
    private String cntrctNo; // 추가된 필드

    public CnSubcontractChangeId() {
    }

    public CnSubcontractChangeId(Long cntrctChgId, Long scontrctCorpId, String cntrctNo) {
        this.cntrctChgId = cntrctChgId;
        this.scontrctCorpId = scontrctCorpId;
        this.cntrctNo = cntrctNo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        CnSubcontractChangeId that = (CnSubcontractChangeId) o;
        return Objects.equals(cntrctChgId, that.cntrctChgId) &&
                Objects.equals(scontrctCorpId, that.scontrctCorpId) &&
                Objects.equals(cntrctNo, that.cntrctNo); 
    }

    @Override
    public int hashCode() {
        return Objects.hash(cntrctChgId, scontrctCorpId, cntrctNo);
    }
}
