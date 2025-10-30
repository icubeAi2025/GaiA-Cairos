package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class CwDailyReportId implements Serializable {
    private String cntrctNo;
    private Long dailyReportId;
}
