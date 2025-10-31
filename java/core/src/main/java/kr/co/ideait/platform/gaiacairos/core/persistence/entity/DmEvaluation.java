package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Alias("dmEvaluation")
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class DmEvaluation extends AbstractRudIdTime {

	@Id
	@Description(name = "평가 일련번호", description = "", type = Description.TYPE.FIELD)
	String evaSeq;

	@Description(name = "설계 리뷰 번호", description = "", type = Description.TYPE.FIELD)
	String dsgnNo;

	@Description(name = "계약번호", description = "", type = Description.TYPE.FIELD)
	String cntrctNo;

	@Description(name = "평가 의견", description = "", type = Description.TYPE.FIELD)
	String evlOpnin;

	@Description(name = "등록 순서", description = "", type = Description.TYPE.FIELD)
	Short rgstOrdr;

	@Description(name = "첨부파일 번호", description = "", type = Description.TYPE.FIELD)
	String atchFileNo;

	@Description(name = "삭제여부", description = "", type = Description.TYPE.FIELD)
	String dltYn;
}
