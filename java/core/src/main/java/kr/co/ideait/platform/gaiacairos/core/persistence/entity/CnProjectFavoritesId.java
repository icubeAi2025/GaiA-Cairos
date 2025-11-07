package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class CnProjectFavoritesId implements Serializable {
    private String pjtNo;
    private String cntrctNo;
    private String loginId;
    private String pjtType;
}
