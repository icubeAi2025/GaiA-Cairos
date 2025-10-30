package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import java.io.Serializable;
import java.util.Objects;

public class CwQualityActivityId implements Serializable {
    private String qltyIspId;
    private String wbsCd;
    private String activityId;

    public CwQualityActivityId() {}

    public CwQualityActivityId(String qltyIspId, String wbsCd, String activityId) {
        this.qltyIspId = qltyIspId;
        this.wbsCd = wbsCd;
        this.activityId = activityId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CwQualityActivityId that = (CwQualityActivityId) o;
        return Objects.equals(qltyIspId, that.qltyIspId) &&
               Objects.equals(wbsCd, that.wbsCd) &&
               Objects.equals(activityId, that.activityId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(qltyIspId, wbsCd, activityId);
    }
}

