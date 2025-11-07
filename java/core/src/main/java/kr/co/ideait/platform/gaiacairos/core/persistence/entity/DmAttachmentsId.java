package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Data
public class DmAttachmentsId implements Serializable {

    private String fileNo;
    private String fileKey;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        DmAttachmentsId that = (DmAttachmentsId) o;
        return Objects.equals(fileNo, that.fileNo) &&
                Objects.equals(fileKey, that.fileKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileNo, fileKey);
    }
}
