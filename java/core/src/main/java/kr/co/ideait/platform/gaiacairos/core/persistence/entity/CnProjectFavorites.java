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
@IdClass(CnProjectFavoritesId.class)
public class CnProjectFavorites extends AbstractRIdTime {

	@Id
	@Description(name = "프로젝트번호", description = "", type = Description.TYPE.FIELD)
	String pjtNo; // 프로젝트번호

	@Id
	@Description(name = "계약번호", description = "", type = Description.TYPE.FIELD)
	String cntrctNo; // 계약번호

	@Id
	@Description(name = "로그인 ID", description = "", type = Description.TYPE.FIELD)
	String loginId; // 로그인 Id

	@Id
	@Description(name = "포탈 타입", description = "", type = Description.TYPE.FIELD)
	String pjtType; // 포탈 타입 (PGAIA, GAIA, CMIS)
}
