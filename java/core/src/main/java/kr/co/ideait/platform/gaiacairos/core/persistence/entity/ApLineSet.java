package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@IdClass(ApLineSetId.class)
public class ApLineSet extends AbstractRudIdTime{
	
	@Id
	@Description(name = "결재라인 관리 번호", description = "", type = Description.TYPE.FIELD)
	Integer apLineNo;
	
	@Id
	@Description(name = "결재 순번", description = "", type = Description.TYPE.FIELD)
	Short apOrder;
	
	@Description(name = "결재 구분", description = "", type = Description.TYPE.FIELD)
	String apDiv;
	
	@Description(name = "결재자 ID", description = "", type = Description.TYPE.FIELD)
	String apUsrId;
	
	@Description(name = "결재자 LOGIN ID", description = "", type = Description.TYPE.FIELD)
	String apLoginId;
	
	@Description(name = "삭제여부", description = "", type = Description.TYPE.FIELD)
	String dltYn;
	
}
