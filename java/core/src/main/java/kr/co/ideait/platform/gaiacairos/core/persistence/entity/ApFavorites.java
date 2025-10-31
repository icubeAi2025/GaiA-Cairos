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
public class ApFavorites extends AbstractRIdTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Description(name = "즐겨찾기번호", description = "", type = Description.TYPE.FIELD)
	Integer fvrtsNo;

	@Description(name = "서식 번호", description = "", type = Description.TYPE.FIELD)
	Integer frmNo;

	@Description(name = "즐겨찾기 구분", description = "", type = Description.TYPE.FIELD)
	String fvrtsDiv;

	@Description(name = "사용자 ID", description = "", type = Description.TYPE.FIELD)
	String usrId;

	@Description(name = "로그인 ID", description = "", type = Description.TYPE.FIELD)
	String loginId;

}
