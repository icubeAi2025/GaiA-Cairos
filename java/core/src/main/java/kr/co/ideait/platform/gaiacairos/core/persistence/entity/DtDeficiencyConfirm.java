package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class DtDeficiencyConfirm extends AbstractRudIdTime {

	@Id
	@Description(name = "결함확인 일련번호", description = "", type = Description.TYPE.FIELD)
	String dfccySeq;

	@Description(name = "결함번호", description = "", type = Description.TYPE.FIELD)
	String dfccyNo;

	@Description(name = "계약번호", description = "", type = Description.TYPE.FIELD)
	String cntrctNo;

	@Description(name = "확인 의견", description = "", type = Description.TYPE.FIELD)
	String cnfrmOpnin;

	@Description(name = "첨부파일 번호", description = "", type = Description.TYPE.FIELD)
	Integer atchFileNo;

	@Description(name = "등록순서", description = "", type = Description.TYPE.FIELD)
	Short rgstOrdr;

	@Description(name = "삭제여부", description = "", type = Description.TYPE.FIELD)
	String dltYn;

}
