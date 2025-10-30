package kr.co.ideait.platform.gaiacairos.core.persistence.vo.safety;

import org.apache.ibatis.type.Alias;

import kr.co.ideait.platform.gaiacairos.core.constant.CommonCodeConstants;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisPageable;
import lombok.Data;
import lombok.EqualsAndHashCode;

public interface EducationDiaryMybatisParam {
	
	@Data
    @Alias("educationDiaryListInput")
    @EqualsAndHashCode(callSuper = true)
	public class EducationDiaryListInput extends MybatisPageable {
        String cntrctNo;
        String searchYear;
        String searchMonth;
        String searchEduType;
        String cmnGrpCd = CommonCodeConstants.EDUCATION_CODE_GROUP_CODE;
    }
	
	@Data
    @Alias("educationDiaryListOutput")
    @EqualsAndHashCode(callSuper = true)
    public class EducationDiaryListOutput extends MybatisPageable {		
		String eduId;
		String eduDt;
		String eduNm;
		String eduSurv;
		String eduCnt;
		String chgDt;
		Long cnt;

        int atchFileNo;
    }
}
