package kr.co.ideait.platform.gaiacairos.core.persistence.vo.safety.report;

import lombok.Data;

@Data
public class WorkDTO {
    private String workId;
    private String workItem;
    private String workCheck;
    private String workResult;
    private String worker;
}
