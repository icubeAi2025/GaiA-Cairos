package kr.co.ideait.platform.gaiacairos.core.persistence.vo.progress.monthlyreport;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.PrMonthlyReport;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.PrMonthlyReportActivity;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.PrMonthlyReportAdmin;
import lombok.Data;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;

import java.util.List;

@Mapper(componentModel = ComponentModel.SPRING)
public interface MonthlyreportAdminForm {

	MonthlyreportAdminMybatisParam.MonthlyreportAdminInput toMonthlyreportAdminInput(MonthlyreportAdmin monthlyreportAdminList);


	@Data
    class MonthlyreportAdmin {
        // 목록 조회용
        String cntrctNo;
        String apprvlStatsCd;
        String searchText;

        // 상세조회용
        String cntrctChgId;
        String monthlyReportAdminId;

        // 추가,수정용
        String reportYm;
        String monthlyReportDate;
        String title;
        String rmrkCntnts;
        String usrId;

        List<PrMonthlyReportAdmin> reportAdminList;
    }

}
