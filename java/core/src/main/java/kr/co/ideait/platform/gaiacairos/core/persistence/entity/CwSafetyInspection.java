package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import jakarta.persistence.*;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@IdClass(CwSafetyInspectionId.class)
public class CwSafetyInspection extends AbstractRudIdTime {

    @Id
    @Column(name = "cntrct_no")
    @Description(name = "계약번호", description = "", type = Description.TYPE.FIELD)
    String cntrctNo; // 계약번호

    @Id
    @Column(name = "inspection_no")
    @Description(name = "안전점검 번호", description = "", type = Description.TYPE.FIELD)
    String inspectionNo; // 안전점검번호

    @Description(name = "안전점검 문서 번호", description = "", type = Description.TYPE.FIELD)
    String ispDocNo; // 안전점검 문서번호

    @Description(name = "제목", description = "", type = Description.TYPE.FIELD)
    String title; // 제목

    @Description(name = "요청자 ID", description = "", type = Description.TYPE.FIELD)
    String cnstrtnId; // 요청자 ID(시공담당자)

    @Description(name = "점검요청일자", description = "", type = Description.TYPE.FIELD)
    LocalDateTime ispReqDt; // 점검요청일자

    @Description(name = "공종코드", description = "", type = Description.TYPE.FIELD)
    String cnsttyCd; // 공종코드

    @Column(name = "cnstty_cd_l1")
    @Description(name = "공종코드_1", description = "", type = Description.TYPE.FIELD)
    String cnsttyCdL1; // 공종코드_1

    @Column(name = "cnstty_cd_l2")
    @Description(name = "공종코드_2", description = "", type = Description.TYPE.FIELD)
    String cnsttyCdL2; // 공종코드_2

    @Description(name = "첨부파일 번호", description = "", type = Description.TYPE.FIELD)
    Integer atchFileNo; // 첨부파일 번호

    @Description(name = "결과작성여부", description = "", type = Description.TYPE.FIELD)
    String rsltYn; // 결과작성여부

    @Description(name = "점검자 ID", description = "", type = Description.TYPE.FIELD)
    String ispId; // 점검자 ID

    @Description(name = "점검일자", description = "", type = Description.TYPE.FIELD)
    LocalDateTime ispDt; // 점검일자

    @Description(name = "점검결과작성 요청 문서 ID", description = "", type = Description.TYPE.FIELD)
    String repApDocId; // 점검결과작성 요청 문서 문서 ID
    
    @Description(name = "전자결재 요청자 ID", description = "", type = Description.TYPE.FIELD)
    String apReqId; // 전자결재 요청자 ID

    @Description(name = "전자결재 요청 일자", description = "", type = Description.TYPE.FIELD)
    LocalDateTime apReqDt; // 전자결재 요청 일자

    @Description(name = "전자결재 문서 ID", description = "", type = Description.TYPE.FIELD)
    String apDocId; // 전자결재 문서 ID

    @Description(name = "전자결재 승인자 ID", description = "", type = Description.TYPE.FIELD)
    String apprvlId; // 전자결재 승인자 ID

    @Description(name = "전자결재 승인일", description = "", type = Description.TYPE.FIELD)
    LocalDateTime apprvlDt; // 전자결재 승인일

    @Description(name = "전자결재 승인상태", description = "", type = Description.TYPE.FIELD)
    String apprvlStats; // 전자결재 승인상태

    @Description(name = "전자결재 의견", description = "", type = Description.TYPE.FIELD)
    String apOpnin; // 전자결재 의견

    @Description(name = "삭제여부", description = "", type = Description.TYPE.FIELD)
    String dltYn; // 삭제여부
}
