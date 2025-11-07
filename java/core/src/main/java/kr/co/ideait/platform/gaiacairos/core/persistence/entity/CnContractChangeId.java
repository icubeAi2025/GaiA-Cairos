package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Data
public class CnContractChangeId implements Serializable {
    private String cntrctNo;
    private String cntrctChgId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CnContractChangeId that = (CnContractChangeId) o;
        return Objects.equals(cntrctNo, that.cntrctNo) &&
               Objects.equals(cntrctChgId, that.cntrctChgId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cntrctNo, cntrctChgId);
    }
}
