package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class PrMonthlyReportPhotoId implements Serializable {
    private String cntrctChgId;
    private Integer monthlyReportId;
    private Integer sno;
}
