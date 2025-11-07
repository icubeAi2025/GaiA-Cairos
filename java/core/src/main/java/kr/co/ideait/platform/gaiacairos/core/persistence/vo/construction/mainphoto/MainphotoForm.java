package kr.co.ideait.platform.gaiacairos.core.persistence.vo.construction.mainphoto;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwDailyReport;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwDailyReportActivity;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwDailyReportPhoto;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwDailyReportResource;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.CommonForm;
import lombok.Data;
import lombok.ToString;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.MappingTarget;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = ComponentModel.SPRING)
public interface MainphotoForm {
	
	/*
     * @Null null만 허용한다.
     * 
     * @NotNull 빈 문자열(""), 공백(" ")은 허용하되, Null은 허용하지 않음
     * 
     * @NotEmpty 공백(" ")은 허용하되, Null과 빈 문자열("")은 허용하지 않음
     * 
     * @NotBlank null, 빈 문자열(""), 공백(" ") 모두 허용하지 않는다.
     * 
     * @Email 이메일 형식을 검사한다. 단, 빈 문자열("")의 경우엔 통과 시킨다. ( @Pattern을 통한 정규식 검사를 더 많이 사용
     * 
     * @Pattern(regexp = ) 정규식 검사할 때 사용한다.
     * 
     * @Size(min=, max=) 길이를 제한할 때 사용한다.
     * 
     * @Max(value = ) value 이하의 값만 허용한다.
     * 
     * @Min(value = ) value 이상의 값만 허용한다.
     * 
     * @Positive 값을 양수로 제한한다.
     * 
     * @PositiveOrZero 값을 양수와 0만 가능하도록 제한한다.
     * 
     * @Negative 값을 음수로 제한한다.
     * 
     * @NegativeOrZero 값을 음수와 0만 가능하도록 제한한다.
     * 
     * @Future Now 보다 미래의 날짜, 시간이어야 한다.
     * 
     * @FutureOrPresent Now 거나 미래의 날짜, 시간이어야 한다.
     * 
     * @Past Now 보다 과거의 날짜, 시간이어야 한다.
     * 
     * @PastFutureOrPresent Now 거나 과거의 날짜, 시간이어야 한다.
     */

	/**
     * 작업일보 등록
     */
	CwDailyReport DailyReportInsert(DailyReportInsert dailyReportInsert);

	/**
     * 작업일보 액티비티 등록
     */
	CwDailyReportActivity DailyReportActivityInsert(DailyReportInsert dailyReportInsert);
	/**
     * 기성 삭제
     */
    @Data
    class DailyReportList {
        List<CwDailyReport> DailyReportList;
    }

/*
    @Data
    class PaymentUpdate {
		String cntrctNo;
        String payprceSno;
        String apprvlStats;
    }

    @Data
    class PaymentUpdateList {
        List<PaymentUpdate> paymentUpdateList;
    }
    */
	/**
     * 선급금 지급 수정
     */
    void toUpdateCwDailyReport(DailyReportInsert dailyreport, @MappingTarget CwDailyReport cwDailyReport);
    
	/**
     * 내역서 메인 폼
     */
	@Data
    class MainphotoMain {
		String pjtNo;
		String cntrctNo;
		String cntrctChgId;
		String wbsCd;
		String searchText;
    }
	
    /**
     * 코드, 품명, 규격 검색 폼
     */
    @Data
    @ToString
    class ConstructionListGet extends CommonForm{
        String cntrctId;
        String chgId;
		List<Integer> cnsttySnList;
        String searchText;
    }
	
    /**
     * 내역서 상세 폼
     */
    @Data
    class ConstructionDetailGet extends CommonForm{
        String cntrctNo;
        Long payprceSno;
        Long ppaymnySno;
    }

    @Data
    class DailyReportInsert extends CommonForm {
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
        String sftyWorkItem;

        String dltYn;
        
        String apprvlStats;
        String apprvlReqId;
        LocalDateTime apprvlReqDt;
        String apprvlId;
        LocalDateTime apprvlDt;
        String searchText;

        String workDtType;

        List<CwDailyReportActivity> dailyReportActivity;
        List<CwDailyReportActivity> prActivity;
        List<CwDailyReportResource> dailyReportResource;
        List<CwDailyReportPhoto> dailyReportPhoto;


        List<Map<String, String>> dailyReportActivityNewNext;
    }


}
