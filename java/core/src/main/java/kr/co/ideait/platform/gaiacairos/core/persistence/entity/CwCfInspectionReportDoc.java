package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

import java.time.LocalDateTime;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Alias("cwCfInspectionReportDoc")
@IdClass(CwCfInspectionReportDocId.class)
public class CwCfInspectionReportDoc extends AbstractRudIdTime {

    @Id
    @Description(name = "계약번호", description = "", type = Description.TYPE.FIELD)
    String cntrctNo; // 계약번호

    @Id
    @Column(columnDefinition = "NUMERIC")
    @Description(name = "책임감리일지 일련번호", description = "", type = Description.TYPE.FIELD)
    Long dailyReportId; // 책임감리일지 일련번호

    @Id
    @Description(name = "문서 순번", description = "", type = Description.TYPE.FIELD)
    Integer docId; // 문서 순번

    @Description(name = "문서 번호", description = "", type = Description.TYPE.FIELD)
    String docNo; // 문서 번호

    @Description(name = "공종", description = "공종(작업 구분)", type = Description.TYPE.FIELD)
    String workType; // 공종(작업 구분)

    @Description(name = "제목", description = "", type = Description.TYPE.FIELD)
    String title; // 제목

    @Description(name = "내용 요약", description = "", type = Description.TYPE.FIELD)
    String summary; // 내용 요약

    @Description(name = "첨부파일 번호", description = "", type = Description.TYPE.FIELD)
    String atchFileNo; // 첨부파일 번호

    @Description(name = "삭제 여부", description = "", type = Description.TYPE.FIELD)
    String dltYn; // 삭제 여부

    @Description(name = "구분(발송/접수)", description = "구분(발송/접수)", type = Description.TYPE.FIELD)
    String docType; // 구분(발송/접수)

    @Description(name = "날짜(발송일/접수일)", description = "날짜(발송일/접수일)", type = Description.TYPE.FIELD)
    LocalDateTime date; // 날짜(발송일/접수일)

    @Description(name = "수신처/발신처", description = "수신처/발신처", type = Description.TYPE.FIELD)
    String target; // 수신처/발신처

    @Description(name = "비고", description = "비고", type = Description.TYPE.FIELD)
    String rmrkCntnts; // 비고

} 