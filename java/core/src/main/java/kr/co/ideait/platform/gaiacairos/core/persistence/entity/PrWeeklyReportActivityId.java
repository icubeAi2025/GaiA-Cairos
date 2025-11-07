package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class PrWeeklyReportActivityId implements Serializable {
	
	private String cntrctChgId;
    private Long weeklyReportId;
    private Integer weeklyActivityId;
}
