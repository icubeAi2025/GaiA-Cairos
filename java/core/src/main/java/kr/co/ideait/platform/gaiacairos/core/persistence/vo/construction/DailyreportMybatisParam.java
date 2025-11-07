package kr.co.ideait.platform.gaiacairos.core.persistence.vo.construction;

import java.math.BigDecimal;

import kr.co.ideait.iframework.annotation.Description;
import org.apache.ibatis.type.Alias;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;

import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisPageable;
import lombok.Data;

@Mapper(componentModel = ComponentModel.SPRING)
public interface DailyreportMybatisParam {

	@Data
	@Alias("dailyreportformTypeSelectInput")
	public class DailyreportFormTypeSelectInput extends MybatisPageable {
        String cntrctNo;
        Long dailyReportId;

        String dailyReportDate;
        String reportNo;
        String title;
        

        String amWthr;
        String pmWthr;
        String dlowstTmprtVal;
        String dtopTmprtVal;
        BigDecimal prcptRate;
        BigDecimal snowRate;
        
        BigDecimal todayPlanBohalRate;
        BigDecimal todayArsltBohalRate;
        BigDecimal acmltPlanBohalRate;
        BigDecimal acmltArsltBohalRate;

        String majorMatter;
        String workDtType;
        String rsceTpCd;
        
        String planStart;
        String planFinish;
        
        String year;
        String month;
        String status;
        String searchText;
        String actualBgnDateYn;
	}

    @Data
    @Alias("dailyreportAttAchmentsDataOutput")
    public class dailyreportAttAchmentsDataOutput extends MybatisPageable {
        Integer fileNo;
        Integer sno;
        String fileNm;
        String fileDiskNm;
        String fileDiskPath;
        Integer fileSize;
        Integer fileHitNum;
        String dltYn;
        String fileDiv;
        String rgstrId;
        String rgstDt;
    }

    @Data
    @Alias("storageMainOutput")
    public class storageMainOutput extends MybatisPageable {
        Integer docNo;
        String docId;
        Integer naviNo;
        String naviId;
        Integer upDocNo;
        String upDocId;
        String docType;
        String docPath;
        String docNm;
        String docDiskNm;
        String docDiskPath;
        Integer docSize;
        Short docHitNum;
        String docTrashYn;
        String dltYn;
        Integer cbgnKey;
    }
}
