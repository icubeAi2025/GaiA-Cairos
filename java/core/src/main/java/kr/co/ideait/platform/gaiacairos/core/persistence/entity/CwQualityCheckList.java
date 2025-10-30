package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@IdClass(CwQualityCheckListId.class)
public class CwQualityCheckList extends AbstractRudIdTime {
    @Id
    @Description(name = "품질검측 ID", description = "", type = Description.TYPE.FIELD)
    String qltyIspId; // 품질검측번호

    @Id
    @Column(name = "cntrct_no")
    @Description(name = "계약번호", description = "", type = Description.TYPE.FIELD)
    String cntrctNo; // 계약번호

    @Id
    @Description(name = "체크 리스트 ID", description = "", type = Description.TYPE.FIELD)
    String chklstId; // 체크 리스트 ID

    @Column(name = "chklst_sno", columnDefinition = "int2")
    @Description(name = "검사항목 번호", description = "", type = Description.TYPE.FIELD)
    short chklstSno; // 검사항목 번호

    @Description(name = "검사기준 코드", description = "", type = Description.TYPE.FIELD)
    String chklstBssCd; // 검사기준 코드

    @Description(name = "시공담당자 확인 여부", description = "", type = Description.TYPE.FIELD)
    String cnstrtnYn; // 시공담당자 확인여부

    @Description(name = "검측자 확인 여부", description = "", type = Description.TYPE.FIELD)
    String cqcYn; // 검측자 확인 여부

    @Description(name = "조치사항", description = "", type = Description.TYPE.FIELD)
    String actnDscrpt; // 조치사항
    
    @Description(name = "삭제여부", description = "", type = Description.TYPE.FIELD)
    String dltYn; // 삭제여부
}
