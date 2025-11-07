package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DmDesignScheduleId implements Serializable{

    private String dsgnPhaseNo;
    private String dsgnPhaseCd;
    private String cntrctNo;
    
    @Override
    public boolean equals(Object o) {
       if (this == o) return true;
       if (o == null || getClass() != o.getClass()) return false;
       DmDesignScheduleId that = (DmDesignScheduleId) o;
       return Objects.equals(dsgnPhaseNo, that.dsgnPhaseNo) &&
              Objects.equals(dsgnPhaseCd, that.dsgnPhaseCd) &&
              Objects.equals(cntrctNo, that.cntrctNo);
    }

    @Override
    public int hashCode() {
       return Objects.hash(dsgnPhaseNo, dsgnPhaseCd, cntrctNo);
    }
}
