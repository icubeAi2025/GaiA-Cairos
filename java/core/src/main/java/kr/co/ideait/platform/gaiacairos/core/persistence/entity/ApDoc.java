package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class ApDoc extends AbstractRudIdTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Description(name = "결재문서번호", description = "", type = Description.TYPE.FIELD)
	Integer apDocNo;

	@Description(name = "결재문서ID", description = "", type = Description.TYPE.FIELD)
	String apDocId;

	@Description(name = "서식 번호", description = "", type = Description.TYPE.FIELD)
	Integer frmNo;

	@Description(name = "서식 ID", description = "", type = Description.TYPE.FIELD)
	String frmId;

	@Description(name = "프로젝트번호", description = "", type = Description.TYPE.FIELD)
	String pjtNo;

	@Description(name = "계약번호", description = "", type = Description.TYPE.FIELD)
	String cntrctNo;

	@Description(name = "프로젝트 구분", description = "", type = Description.TYPE.FIELD)
	String pjtType;

	@Description(name = "문서구분", description = "", type = Description.TYPE.FIELD)
	String apType;

	@Description(name = "문서 제목", description = "", type = Description.TYPE.FIELD)
	String apDocTitle;

	@Description(name = "문서 내용", description = "", type = Description.TYPE.FIELD)
	String apDocEdtr;

	@Description(name = "문서 내용", description = "", type = Description.TYPE.FIELD)
	String apDocTxt;

	@Description(name = "기안작성자ID", description = "", type = Description.TYPE.FIELD)
	String apUsrId;

	@Description(name = "기안작성자 LOGIN ID", description = "", type = Description.TYPE.FIELD)
	String apLoginId;

	@Description(name = "기안요청일", description = "", type = Description.TYPE.FIELD)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	LocalDateTime apAppDt;

	@Description(name = "결재완료일", description = "", type = Description.TYPE.FIELD)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	LocalDateTime apCmpltDt;

	@Description(name = "결재문서 상태", description = "", type = Description.TYPE.FIELD)
	String apDocStats;

	@Description(name = "삭제여부", description = "", type = Description.TYPE.FIELD)
	String dltYn;

	@Description(name = "수신처", description = "", type = Description.TYPE.FIELD)
	String recipientNm;

	@Description(name = "문서관리 공유 이력 pk", description = "", type = Description.TYPE.FIELD)
	String uuid;

	@Description(name = "문서관리 네비ID", description = "", type = Description.TYPE.FIELD)
	String naviId;

	@Description(name = "발신처", description = "", type = Description.TYPE.FIELD)
	String senderNm;
}
