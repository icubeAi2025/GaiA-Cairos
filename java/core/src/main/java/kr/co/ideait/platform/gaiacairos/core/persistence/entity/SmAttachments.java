package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Alias("smAttachments")
public class SmAttachments extends AbstractRudIdTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer fileNo;
    String boardCd;
    String fileOrgNm;
    String fileDiskNm;
    String fileDiskPath;
    @Column(columnDefinition = "NUMERIC")
    Integer fileSize;
    String dltYn;
    String fileUuid;
}
