package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import lombok.Data;

import javax.persistence.Column;
import java.io.Serializable;

@Data
public class CwDailyReportActivityId implements Serializable {
	@Column
    private String cntrctNo;
	@Column
    private Long dailyReportId;
	@Column(name = "daily_activity_id")
    private Integer dailyActivityId;
}
