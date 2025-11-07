package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DtDeficientyScheduleId implements Serializable {
    private String dfccyPhaseNo;
    private String dfccyPhaseCd;

     @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DtDeficientyScheduleId that = (DtDeficientyScheduleId) o;
        return Objects.equals(dfccyPhaseNo, that.dfccyPhaseNo) &&
               Objects.equals(dfccyPhaseCd, that.dfccyPhaseCd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dfccyPhaseNo, dfccyPhaseCd);
    }
}