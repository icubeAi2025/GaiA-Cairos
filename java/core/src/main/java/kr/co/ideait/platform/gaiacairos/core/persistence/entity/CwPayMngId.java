package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class CwPayMngId implements Serializable {
    private String cntrctNo;
    private Long payprceSno;
}
