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
public class DmBackcheck extends AbstractRudIdTime {

	@Id
	@Description(name = "백체크 일련번호", description = "", type = Description.TYPE.FIELD)
	String backSeq;

	@Description(name = "설계 리뷰 번호", description = "", type = Description.TYPE.FIELD)
	String dsgnNo;

	@Description(name = "계약번호", description = "", type = Description.TYPE.FIELD)
	String cntrctNo;

	@Description(name = "백체크 의견", description = "", type = Description.TYPE.FIELD)
	String bckchkOpnin;

	@Description(name = "첨부파일 번호", description = "", type = Description.TYPE.FIELD)
	String atchFileNo;

	@Description(name = "등록 순서", description = "", type = Description.TYPE.FIELD)
	Short rgstOrdr;

	@Description(name = "삭제여부", description = "", type = Description.TYPE.FIELD)
	String dltYn;
}
