package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Data
public class DmDwgId implements Serializable {

    private String dwgNo;
    private String dwgCd;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        DmDwgId that = (DmDwgId) o;
        return Objects.equals(dwgNo, that.dwgNo) &&
                Objects.equals(dwgCd, that.dwgCd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dwgNo, dwgCd);
    }
}
