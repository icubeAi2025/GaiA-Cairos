package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DtDeficiencyActivityId implements Serializable {
    private String dfccyNo;
    private String wbsCd;
    private String cntrctNo;
    private String activityId;
}
