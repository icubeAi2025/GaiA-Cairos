package kr.co.ideait.platform.gaiacairos.core.persistence.vo.design.tool.backcheck;

import org.apache.ibatis.type.Alias;

import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisPageable;
import lombok.Data;

public interface BackCheckMybatisParam {

	@Data
	@Alias("backCheckListInput")
	public class BackCheckListInput extends MybatisPageable {
		String dsgnPhaseNo;
        String cntrctNo;
        String dsgnPhaseCd;
        String dsgnCd;
        String backchkStatus;
        String keyword;
        String rgstrNm;
        String myRplyYn;
        String startDsgnNo;
        String endDsgnNo;
        String rplyCd;
        String apprerCd;
        String backchkCd;
        String startRgstDt;
        String endRgstDt;
        String isuYn;
        String lesnYn;
        String atachYn;
        String usrId;
        String lang;
	}
}
