package kr.co.ideait.platform.gaiacairos.core.persistence.vo.progress.wbs.activityqdb;

import org.apache.ibatis.type.Alias;

import lombok.Data;

public interface ActivityQdbMybatisParam {

	@Data
	@Alias("activityWbsListInput")
	public class ActivityWbsListInput {
		String cntrctChgId;
		String wbsCd;
		String actkindGroupCode;
		String searchText;
	}

	@Data
	@Alias("activityWbsQdbListInput")
	public class ActivityWbsQdbListInput {
		String cntrctChgId;
		String activityId;
		String searchText;
	}

	@Data
	@Alias("activityCbsListInput")
	public class ActivityCbsListInput {
		String cntrctChgId;
		String unitCnstType;
		String cnsttyCd;
		String searchText;
	}
	
	@Data
	@Alias("activityCbsQdbListInput") 
	public class ActivityCbsQdbListInput {
		String cntrctChgId;
        String cnsttySn;
        String dtlCnsttySn;
		String actkindGroupCode;
        String searchText;
	}

	@Data
	@Alias("activityQdbContractListInput")
	public class ActivityQdbContractListInput {
		String pjtNo;
		String MajorCnsttyGroupCode;
	}
	
	@Data
	@Alias("activityTreeListInput")
	public class ActivityTreeListInput {
		String cntrctChgId;		
	}
}
