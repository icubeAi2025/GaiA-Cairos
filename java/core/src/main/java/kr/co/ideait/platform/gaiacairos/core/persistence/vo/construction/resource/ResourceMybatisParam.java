package kr.co.ideait.platform.gaiacairos.core.persistence.vo.construction.resource;

import java.util.List;

import org.apache.ibatis.type.Alias;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import kr.co.ideait.platform.gaiacairos.core.persistence.vo.progress.wbs.activity.ActivityMybatisParam;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.MybatisPageable;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ResourceMybatisParam {

    @Data
    @Alias("resourceFormTypeSelectInput")
    public class ResourceFormTypeSelectInput extends MybatisPageable {

        String cntrctNo;
        String currentMonth;
        String rsceCd;
        String rsceTpCd;
    }

    /**
     * 공정관리 - Activity 참고
     * {@link ActivityMybatisParam.ActivityListOutput}
     */
    @Data
    @Alias("resourceActivity")
    public class ResourceActivity {

        String cntrctChgId;
        String revisionId;
        String activityId;
        String wbsCd;
        String activityNm;
        String activityKind;
        String earlyStart;
        String earlyFinish;
        String lateStart;
        String lateFinish;
        String planStart;
        String planFinish;
        String actualStart;
        String actualFinish;
        String currentStart;
        String currentFinish;
        Integer intlDuration;
        Integer remndrDuration;
        Integer totalFloat;
        Integer exptCost;
        Integer remndrCost;
        String predecessors;
        String successors;
        Integer cmpltPercent;
        String rmrk;
        String dltYn;

        String wbsNm;
        String activityKindKrn;
        Integer DEPTH;
        Integer wbsLevel;
    }

    @Data
    @Alias("resourceQdbParam")
    public class ResourceQdbParam {
        String cntrctChgId;
        List<ResourceActivity> activityList;
        String conditionDate;
        String rsceCd;
        String rsceTpCd;
        String returnType = "list";
        String monthlyDetailCurrentDate;

        String planStart;
        String searchText;
    }

    @Data
    @Alias("resourceQdb")
    public class ResourceQdb {
        String cntrctChgId;
        String revisionId;
        String activityId;
        String cnsttyCd;
        String dtlCnsttySn;
        String gnrlexpnsCd;
        String rsceTpCd;
        String rsceNm;
        String specNm;
        String unit;
        double rsceQty; // 자원 수량
        double qty; // 단위 수량
        double totalQty; // 자원 수량 * 단위 수량?
        String dltYn;
        String rgstDt;
        String rgstrId;
        String chgDt;
        String chgId;
        String dltDt;
        String dltId;
    }

    @Getter
    @Alias("dailyResource")
    public class DailyResource {
        String wbsCd;
        String wbsNm;
        String cntrctChgId;
        String activityId;
        String activityNm;
        String planStart;
        String planFinish;
        String actualStart;
        String actualFinish;
        String planDaysCount;
        String actualDaysCount;
        String dailyDate; // returnType = "list"
        String groupMonth; // returnType = "group"

        String rsceTpCd;
        String rsceCd;
        String rsceNm;
        String specNm;
        String unit;
        String dlt_yn;

        double rsceQty; // RSCE_QTY (PR_QDB)
        double multipliedQty; // RSCE_QTY (PR_QDB) * QTY (PR_QDB_RESOURCE) | 자원수량 * 단위수량
        double cbsRsceQty; // RSCE_QTY (CT_CBS_DETAIL) | 공종 자원수량
        double cbsUnitQty; // UNIT_QTY (CT_CBS_RESOURCE) | 공종 단위수량

        double dailyQuantity; //
        double cumulativeQuantity; //
        double currentQty; // 금월
        double remndrQty; // 잔여수량
        double acmtlQty; // 전월누계

        double mtrlUprc; // 재료비단가 (CT_CBS_DETAIL)
        double lbrUprc; // 노무비단가 (CT_CBS_DETAIL)
        double gnrlexpnsUprc; // 경비단가 (CT_CBS_DETAIL)

        double originUprc; // 단가 (CT_CBS_RESOURCE)
        double originMtrlUprc; // 재료비단가 (CT_CBS_RESOURCE)
        double originLbrUprc; // 노무비단가 (CT_CBS_RESOURCE)
        double originGnrlexpnsUprc; // 경비단가 (CT_CBS_RESOURCE)

        double cbsCost; // 단가 (CT_CBS_RESOURCE) * 공종 자원수량 * 공종 단위수량
        String mainRsceDsply; // 주요자재
    }

    // 관급자재 현황
    @Data
    @Alias("rawGovsplyMtrlItem")
    class RawGovsplyMtrlItem {
        String minYear;
        String minMonth;
        String maxYear;
        String maxMonth;

        List<RawGovsplyMtrlList> rawGovsplyMtrlList;
    }

    @Data
    @Alias("rawGovsplyMtrlList")
    class RawGovsplyMtrlList {
        String rsceCd;
        String rsceNm;
        String specNm;
        String unit;
        double rsceQty;
        double monthlyQuantity;
        String year;
        String month;
        String unitCnstType;
        String unitCnstTypeNm;
        String cnsttyCd;
    }

    
    // 노무/장비
    @Data
    @Alias("rawLbrEqList")
    class RawLbrEqList {
        List<RawLbrEqItem> rawLbrEqLists;
        List<RawLbrEqDate> rawLbrEqDates;
    }

    @Data
    @Alias("rawLbrEqItem")
    class RawLbrEqItem {

        String cntrctChgid;
        Double cnsttyLvlNum; 
        String cnsttyCd;
        String upCnsttyCd;
        String unitCnstType;
        String cmnCdNmKrn;
        String cnsttyNm;
        Double cnsttySn; 
        String activityId;
        Double totalEqQty; 
        Double totalLbrQty; 
        String totalEqRsceDetail;
        String planStart;
        String planFinish;
    }

    @Data
    @Alias("rawLbrEqDate")
    class RawLbrEqDate {
        String weekStart;
    }
}
