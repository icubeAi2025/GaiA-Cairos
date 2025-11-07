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
public class ApLine extends AbstractRuIdTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Description(name = "결재라인번호", description = "", type = Description.TYPE.FIELD)
	Integer apNo;

	@Description(name = "결재라인ID", description = "", type = Description.TYPE.FIELD)
	String apId;

	@Description(name = "결재문서번호", description = "", type = Description.TYPE.FIELD)
	Integer apDocNo;

	@Description(name = "결재문서ID", description = "", type = Description.TYPE.FIELD)
	String apDocId;

	@Description(name = "결재 순번", description = "", type = Description.TYPE.FIELD)
	Short apOrder;

	@Description(name = "결재 구분", description = "", type = Description.TYPE.FIELD)
	String apDiv;

	@Description(name = "결재 상태", description = "", type = Description.TYPE.FIELD)
	String apStats;

	@Description(name = "결재자 ID", description = "", type = Description.TYPE.FIELD)
	String apUsrId;

	@Description(name = "결재자 LOGIN_ID", description = "", type = Description.TYPE.FIELD)
	String apLoginId;

	@Description(name = "결재자 의견", description = "", type = Description.TYPE.FIELD)
	String apUsrOpnin;

}
