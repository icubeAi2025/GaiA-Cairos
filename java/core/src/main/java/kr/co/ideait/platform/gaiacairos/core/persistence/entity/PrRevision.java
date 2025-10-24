package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import jakarta.persistence.*;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@IdClass(PrRevisionId.class)
public class PrRevision extends AbstractRdIdTime{
	
	@Id
	@Description(name = "계약변경ID", description = "", type = Description.TYPE.FIELD)
	String cntrctChgId;
	
	@Id
	@Description(name = "리비전ID", description = "", type = Description.TYPE.FIELD)
	String revisionId;
	
	@Description(name = "EPS ID", description = "", type = Description.TYPE.FIELD)
	String epsId;
	
	@Description(name = "EPS명", description = "", type = Description.TYPE.FIELD)
	String epsNm;
	
	@Column(name = "p6_project_id")
	@Description(name = "P6프로젝트ID", description = "", type = Description.TYPE.FIELD)
	String p6ProjectId;
	
	@Column(name = "p6_project_nm")
	@Description(name = "P6프로젝트명", description = "", type = Description.TYPE.FIELD)
	String p6ProjectNm;
	
	@Description(name = "최종버전유무", description = "", type = Description.TYPE.FIELD)
	String lastRevisionYn;
	
	@Description(name = "비고", description = "", type = Description.TYPE.FIELD)
	String rmrk;
	
	@Description(name = "삭제여부", description = "", type = Description.TYPE.FIELD)
	String dltYn;
	
	@Description(name = "수정자 ID", description = "", type = Description.TYPE.FIELD)
	String chgId;
	
	@Description(name = "수정일시", description = "", type = Description.TYPE.FIELD)
	LocalDateTime chgDt;

	@Column(name = "p6_eps_obj_id")
	@Description(name = "p6 EPS Object Id", description = "", type = Description.TYPE.FIELD)
	Integer p6EpsObjId;

	@Column(name = "p6_project_obj_id")
	@Description(name = "p6 프로젝트 Object Id", description = "", type = Description.TYPE.FIELD)
	Integer p6ProjectObjId;

	@Transient
	@Description(name = "계약구분", description = "", type = Description.TYPE.FIELD)
	String cntrctPhaseYn;
}
