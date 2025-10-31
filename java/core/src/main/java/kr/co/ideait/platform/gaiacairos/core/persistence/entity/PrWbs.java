package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import jakarta.persistence.*;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;

@Entity
@Data
@Table(name = "pr_wbs", schema = "gaia_cmis")
@IdClass(PrWbsId.class)
public class PrWbs extends AbstractRdIdTime {

	@Id
	@Description(name = "계약변경ID", description = "", type = Description.TYPE.FIELD)
	String cntrctChgId;
	@Id
	@Description(name = "리비젼ID", description = "", type = Description.TYPE.FIELD)
	String revisionId;
	@Id
	@Description(name = "WBS코드", description = "", type = Description.TYPE.FIELD)
	String wbsCd;
	@Description(name = "WBS경로", description = "", type = Description.TYPE.FIELD)
	String wbsPath;
	@Description(name = "WBS명", description = "", type = Description.TYPE.FIELD)
	String wbsNm;
	@Description(name = "상위WBS코드", description = "", type = Description.TYPE.FIELD)
	String upWbsCd;
	@Column(columnDefinition = "NUMERIC")
	@Description(name = "WBS레벨", description = "", type = Description.TYPE.FIELD)
	Integer wbsLevel;
	@Description(name = "빠른시작일자", description = "", type = Description.TYPE.FIELD)
	String earlyStart;
	@Description(name = "빠른종료일자", description = "", type = Description.TYPE.FIELD)
	String earlyFinish;
	@Description(name = "실제시작일자", description = "", type = Description.TYPE.FIELD)
	String actualStart;
	@Description(name = "실제종료일자", description = "", type = Description.TYPE.FIELD)
	String actualFinish;
	@Description(name = "비고", description = "", type = Description.TYPE.FIELD)
	String rmrk;
	@Description(name = "삭제여부", description = "", type = Description.TYPE.FIELD)
	String dltYn;

	@Column(name = "p6_wbs_obj_id")
	@Description(name = "P6 WBS Object ID", description = "", type = Description.TYPE.FIELD)
	Integer p6WbsObjId;
	@Column(name = "p6_up_wbs_obj_id")
	@Description(name = "P6 WBS 상위 Object ID", description = "", type = Description.TYPE.FIELD)
	Integer p6UpWbsObjId;

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
		if (!(o instanceof PrWbs prWbs)) return false;

        return getCntrctChgId() != null && Objects.equals(getCntrctChgId(), prWbs.getCntrctChgId())
				&& getRevisionId() != null && Objects.equals(getRevisionId(), prWbs.getRevisionId())
				&& getWbsCd() != null && Objects.equals(getWbsCd(), prWbs.getWbsCd());
	}

	@Override
	public final int hashCode() {
		return Objects.hash(cntrctChgId, revisionId, wbsCd);
	}
}
