package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Data
public class CnContractBidId implements Serializable {

    private String cntrctNo;
    private Long cbsSno; 

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        CnContractBidId that = (CnContractBidId) o;
        return Objects.equals(cntrctNo, that.cntrctNo) &&
                Objects.equals(cbsSno, that.cbsSno);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cntrctNo, cbsSno);
    }
}
