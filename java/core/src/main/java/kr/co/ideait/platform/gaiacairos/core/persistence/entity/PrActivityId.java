package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class PrActivityId implements Serializable {

    String cntrctChgId;
    String revisionId;
    String activityId;

}
