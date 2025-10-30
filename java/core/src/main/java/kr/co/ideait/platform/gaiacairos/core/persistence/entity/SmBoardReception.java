package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class SmBoardReception extends AbstractRudIdTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer receSeq;
    String boardCd;
    String boardDiv;
    String pjtType;
    String pjtNo;
    String cntrctNo;
    String toDept;
    @Column(columnDefinition = "NUMERIC")
    Integer boardView;
    String dltYn;

}
