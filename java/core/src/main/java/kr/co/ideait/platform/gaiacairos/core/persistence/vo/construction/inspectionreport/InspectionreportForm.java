package kr.co.ideait.platform.gaiacairos.core.persistence.vo.construction.inspectionreport;

import java.math.BigDecimal;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import kr.co.ideait.platform.gaiacairos.core.config.GlobalMapperConfig;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwInspectionReport;
import lombok.Data;

@Mapper(config = GlobalMapperConfig.class)
public interface InspectionreportForm {

    CwInspectionReport toEntity(CreateReport createReport);

    void updateReport(CreateReport report, @MappingTarget CwInspectionReport inspectionreport);

    @Data
    class CreateReport {
        String cntrctNo;
        Long dailyReportId;

        String dailyReportDate;
        String reportNo;
        String title;
        String workCd;

        String amWthr;
        String pmWthr;
        String dlowstTmprtVal;
        String dtopTmprtVal;
        BigDecimal prcptRate;
        BigDecimal snowRate;

        String rmrkCntnts;
        String majorMatter;
        String significantNote;
        String commentResult;

        String startDate;
        String endDate;
        String workType;
        String searchValue;
        String selectValue;
        String year;
        String month;
        String rgstrId;

        String type; // 복사인지 확인용
        String tm;
    }

    @Data
    class Delete {
        List<CreateReport> reportList;
    }
}
