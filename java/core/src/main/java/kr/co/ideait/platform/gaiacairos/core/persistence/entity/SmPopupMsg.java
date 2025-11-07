package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class SmPopupMsg extends AbstractRdIdTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer popMsgSeq;
    String popMsgCd;
    String popDiv;
    String pjtType;
    String pjtNo;
    String cntrctNo;
    String toDept;
    String popTitle;
    String popContent;
    String linkNm;
    String linkUrl;
    LocalDateTime popStartDt;
    LocalDateTime popEndDt;
    String shareYn;
    String useYn;
    String dltYn;
}
