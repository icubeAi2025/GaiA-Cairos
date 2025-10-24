package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Data
public class CnContractCompanyId implements Serializable {
    private String cntrctNo;
    Long cntrctId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CnContractCompanyId that = (CnContractCompanyId) o;
        return Objects.equals(cntrctNo, that.cntrctNo) &&
               Objects.equals(cntrctId, that.cntrctId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cntrctNo, cntrctId);
    }
}
