package kr.co.ideait.platform.gaiacairos.core.persistence.vo.progress.monthlyreport;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.PrMonthlyReportActivity;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.CommonForm;
import lombok.Data;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.type.Alias;

import java.time.LocalDateTime;
import java.util.List;

public interface MonthlyreportAdminMybatisParam {

	@Data
	@Alias("monthlyreportAdminInput")
    public class MonthlyreportAdminInput extends CommonForm {
        String cntrctNo;
		String apprvlStatsCd;

		// 추가, 수정, 조회용
		String cntrctChgId;
		String monthlyReportAdminId;
		String reportYm;
		String monthlyReportDate;
		String title;
		String rmrkCntnts;
		String usrId;
    }

	@Data
	@Alias("monthlyreportAdminOutput")
	public class MonthlyreportAdminOutput {
		String cntrctChgId;
		Long monthlyReportAdminId;
		String monthlyReportDate;
		String reportYm;
		String title;
		String rgstrNm;
		String rmrkCntnts;
	}

}
