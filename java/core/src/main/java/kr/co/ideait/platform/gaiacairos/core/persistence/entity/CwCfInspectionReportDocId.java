package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CwCfInspectionReportDocId implements Serializable {
    private String cntrctNo;
    private Long dailyReportId;
    private Integer docId;
} 