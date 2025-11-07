package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Data
public class DtAttachmentsId implements Serializable {

    private Integer fileNo;
    private Short sno;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        DtAttachmentsId that = (DtAttachmentsId) o;
        return Objects.equals(fileNo, that.fileNo) &&
                Objects.equals(sno, that.sno);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileNo, sno);
    }
}
