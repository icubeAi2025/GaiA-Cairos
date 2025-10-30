package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class PrMonthlyReportStatusId implements Serializable {
    private String cntrctChgId;
	private Long monthlyReportId;
	private String cnstrctCd;
}
