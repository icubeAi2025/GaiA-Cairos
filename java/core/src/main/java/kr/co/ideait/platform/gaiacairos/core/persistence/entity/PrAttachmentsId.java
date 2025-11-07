package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Data
public class PrAttachmentsId implements Serializable {
    private Integer fileNo;
    private Integer sno;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        PrAttachmentsId that = (PrAttachmentsId) o;
        return Objects.equals(fileNo, that.fileNo) &&
                Objects.equals(sno, that.sno);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileNo, sno);
    }
}
