package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import java.io.Serializable;
import java.util.Objects;

public class CnContractOrgId implements Serializable {
    private String cntrctNo;
    private Integer cntrctOrgId;

    // Constructors, equals(), and hashCode()

    public CnContractOrgId() {}

    public CnContractOrgId(String cntrctNo, Integer cntrctOrgId) {
        this.cntrctNo = cntrctNo;
        this.cntrctOrgId = cntrctOrgId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CnContractOrgId that = (CnContractOrgId) o;
        return Objects.equals(cntrctNo, that.cntrctNo) &&
               Objects.equals(cntrctOrgId, that.cntrctOrgId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cntrctNo, cntrctOrgId);
    }
}
