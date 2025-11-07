package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import lombok.Data;

import javax.persistence.Column;
import java.io.Serializable;

@Data
public class CwDailyReportPhotoId implements Serializable {
	@Column
    private String cntrctNo;
	@Column
    private Integer dailyReportId;
	@Column(name = "cnstty_pht_sno")
    private Integer cnsttyPhtSno;
}
