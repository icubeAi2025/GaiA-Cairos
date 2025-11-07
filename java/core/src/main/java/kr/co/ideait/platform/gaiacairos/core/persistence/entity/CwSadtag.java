package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import jakarta.persistence.*;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class CwSadtag extends AbstractRudIdTime {

    @Description(name = "계약번호", description = "", type = Description.TYPE.FIELD)
    String cntrctNo; // 계약번호

    @Id
    @Column(name = "sadtag_no")
    @Description(name = "안전지적 번호", description = "", type = Description.TYPE.FIELD)
    String sadtagNo; // 안전지적 번호

    @Description(name = "안전지적 문서 번호", description = "", type = Description.TYPE.FIELD)
    String sadtagDocNo; // 안전지적 문서 번호

    @Description(name = "결함타입", description = "", type = Description.TYPE.FIELD)
    String dfccyTyp; // 결함타입

    @Description(name = "제목", description = "", type = Description.TYPE.FIELD)
    String title; // 제목

    @Description(name = "발견자", description = "", type = Description.TYPE.FIELD)
    String findId; // 발견자

    @Description(name = "발견일자", description = "", type = Description.TYPE.FIELD)
    LocalDateTime findDt; // 발견일자

    @Description(name = "결함-부적합내용", description = "", type = Description.TYPE.FIELD)
    String dfccyCntnts; // 결함-부적합내용

    @Description(name = "결함위치", description = "", type = Description.TYPE.FIELD)
    String dfccyLct; // 결함위치

    @Description(name = "조치기한", description = "", type = Description.TYPE.FIELD)
    LocalDateTime actnTmlmt; // 조치기한

    @Description(name = "진행상태", description = "", type = Description.TYPE.FIELD)
    String pstats; // 진행상태

    @Description(name = "조치자", description = "", type = Description.TYPE.FIELD)
    String actnId; // 조치자

    @Description(name = "조치일자", description = "", type = Description.TYPE.FIELD)
    LocalDateTime actnDt; // 조치일자

    @Description(name = "조치결과", description = "", type = Description.TYPE.FIELD)
    String actnRslt; // 조치결과

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
