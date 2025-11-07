package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DtDeficiencyReplyId implements Serializable {

    private Integer replySeq;
    private String dfccyNo;
    private String cntrctNo;
}