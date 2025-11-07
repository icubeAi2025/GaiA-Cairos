package kr.co.ideait.platform.gaiacairos.core.persistence.vo.safety.report;

import lombok.Data;

@Data
public class SafetyDTO {
    private String checkId;
    private String checkTm;
    private String checkAction;
    private String checkResult;
    private String checker;
}
