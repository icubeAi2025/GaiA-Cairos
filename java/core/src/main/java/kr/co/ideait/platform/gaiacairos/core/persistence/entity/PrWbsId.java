package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class PrWbsId implements Serializable {

    private String cntrctChgId;
    private String revisionId;
    private String wbsCd;

}
