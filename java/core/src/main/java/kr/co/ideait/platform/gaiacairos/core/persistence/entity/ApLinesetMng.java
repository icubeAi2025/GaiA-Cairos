package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class ApLinesetMng extends AbstractRudIdTime{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Description(name = "결재라인 관리 번호", description = "", type = Description.TYPE.FIELD)
	Integer apLineNo;
	
	@Description(name = "결재라인 이름", description = "", type = Description.TYPE.FIELD)
	String apLineNm;
	
	@Description(name = "계약번호", description = "", type = Description.TYPE.FIELD)
	String cntrctNo;
	
	@Description(name = "문서구분", description = "", type = Description.TYPE.FIELD)
	String apType;
	
	@Description(name = "개인 결재 라인 관리 여부", description = "", type = Description.TYPE.FIELD)
	String indvdlYn;
	
	@Description(name = "프로젝트 구분", description = "", type = Description.TYPE.FIELD)
	String pjtType;
	
	@Description(name = "삭제여부", description = "", type = Description.TYPE.FIELD)
	String dltYn;

}
