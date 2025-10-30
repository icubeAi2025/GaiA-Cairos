package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import lombok.Data;

import javax.persistence.Column;
import java.io.Serializable;

@Data
public class CwDailyReportQdbId implements Serializable {
    @Column
    private Long dailyReportId;
    @Column
    private String cntrctChgId;
    @Column
    private String revisionId;
    @Column
    private String activityId;
    @Column
    private Long dtlCnsttySn;
}
