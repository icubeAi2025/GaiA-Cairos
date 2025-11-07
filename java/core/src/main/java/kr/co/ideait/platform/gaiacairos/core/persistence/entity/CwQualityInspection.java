package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import jakarta.persistence.*;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class CwQualityInspection extends AbstractRudIdTime {
    @Id
    @Description(name = "품질검측 ID", description = "", type = Description.TYPE.FIELD)
    String qltyIspId; // 품질검측 ID

    @Description(name = "계약번호", description = "", type = Description.TYPE.FIELD)
    String cntrctNo; // 계약번호

    @Description(name = "품질검측 문서 번호", description = "", type = Description.TYPE.FIELD)
    String ispDocNo; // 품질검측 문서 번호
    
    @Description(name = "검측 요청자 ID (시공담당자)", description = "", type = Description.TYPE.FIELD)
    String cnstrtnId; // 검측 요청자 ID (시공담당자)

    @Description(name = "검측요청일자", description = "", type = Description.TYPE.FIELD)
    LocalDateTime ispReqDt; // 검측요청일자

    @Description(name = "위치", description = "", type = Description.TYPE.FIELD)
    String ispLct; // 위치

    @Description(name = "상위 공종코드", description = "", type = Description.TYPE.FIELD)
    String cnsttyCd; // 상위 공종코드

    @Column(name = "cnstty_cd_l1")
    @Description(name = "공종코드_1", description = "", type = Description.TYPE.FIELD)
    String cnsttyCdL1; // 공종코드_1

    @Column(name = "cnstty_cd_l2")
    @Description(name = "공종코드_2", description = "", type = Description.TYPE.FIELD)
    String cnsttyCdL2; // 공종코드_2

    @Description(name = "검측부위", description = "", type = Description.TYPE.FIELD)
    String ispPart; // 검측부위

    @Description(name = "검측사항", description = "", type = Description.TYPE.FIELD)
    String ispIssue; // 검측사항

    @Description(name = "첨부파일 번호", description = "", type = Description.TYPE.FIELD)
    Integer atchFileNo; // 첨부파일 번호

    @Description(name = "검측자 ID", description = "", type = Description.TYPE.FIELD)
    String cqcId; // 검측자 ID

    @Description(name = "검측결과 문서번호", description = "", type = Description.TYPE.FIELD)
    String rsltDocNo; // 검측결과 문서번호

    @Description(name = "검측일자", description = "", type = Description.TYPE.FIELD)
    LocalDateTime rsltDt; // 검측일자

    @Description(name = "검측결과 코드", description = "", type = Description.TYPE.FIELD)
    String rsltCd; // 검측결과 코드

    @Description(name = "지시사항", description = "", type = Description.TYPE.FIELD)
    String ordeOpnin; // 지시사항

    @Description(name = "검측요청 전자결재 문서 ID", description = "", type = Description.TYPE.FIELD)
    String ispApDocId; // 검측요청 전자결재 문서ID

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

    @Description(name = "문서 번호", description = "", type = Description.TYPE.FIELD)
    String docId;
}
