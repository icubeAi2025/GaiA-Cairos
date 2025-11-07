package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class SmComment extends AbstractRudIdTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer commentNo;
    Integer receSeq;
    String commentTxt;
    Integer upCommentNo;
    Integer commentLevel;
    String dltYn;

}