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
public class ApShare extends AbstractRdIdTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Description(name = "공유 번호", description = "", type = Description.TYPE.FIELD)
	Integer apCnrsNo;

	@Description(name = "결재문서번호", description = "", type = Description.TYPE.FIELD)
	Integer apDocNo;

	@Description(name = "결재문서ID", description = "", type = Description.TYPE.FIELD)
	String apDocId;

	@Description(name = "공유 구분", description = "", type = Description.TYPE.FIELD)
	String apCnrsDiv;

	@Description(name = "공유 범위", description = "", type = Description.TYPE.FIELD)
	String apCnrsRng;

	@Description(name = "공유자", description = "", type = Description.TYPE.FIELD)
	String apCnrsId;

	@Description(name = "공유자 LOGIN ID", description = "", type = Description.TYPE.FIELD)
	String loginId;

	@Description(name = "삭제여부", description = "", type = Description.TYPE.FIELD)
	String dltYn;

	@Description(name = "하위 부서 여부", description = "", type = Description.TYPE.FIELD)
	String apSubYn;
}
