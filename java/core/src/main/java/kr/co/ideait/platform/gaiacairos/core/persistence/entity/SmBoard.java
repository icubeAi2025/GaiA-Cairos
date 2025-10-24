package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Entity
@Alias("smBoard")
@Data
@EqualsAndHashCode(callSuper = true)
public class SmBoard extends AbstractRudIdTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer boardNo;
    String boardCd;
    String boardType;
    String boardCategory;
    String boardTitle;
    String boardTxt;
    String shareYn;
    String dltYn;

}
