package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class DtDeficiencyConfirmHistory extends AbstractRudIdTime {

	@Id
	@Description(name = "결함변경 일련번호", description = "", type = Description.TYPE.FIELD)
	String historySeq;

	@Description(name = "결함번호", description = "", type = Description.TYPE.FIELD)
	String dfccyNo;

	@Description(name = "계약번호", description = "", type = Description.TYPE.FIELD)
	String cntrctNo;

	@Description(name = "구분", description = "", type = Description.TYPE.FIELD)
	String cnfrmDiv;

	@Description(name = "QA/관리관 확인코드", description = "", type = Description.TYPE.FIELD)
	String cnfrmCd;

	@Description(name = "등록순서", description = "", type = Description.TYPE.FIELD)
	Short rgstOrdr;

	@Description(name = "삭제여부", description = "", type = Description.TYPE.FIELD)
	String dltYn;
}
