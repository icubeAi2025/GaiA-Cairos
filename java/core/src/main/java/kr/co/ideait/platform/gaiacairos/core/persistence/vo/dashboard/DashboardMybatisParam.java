package kr.co.ideait.platform.gaiacairos.core.persistence.vo.dashboard;

import org.apache.ibatis.type.Alias;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;

import lombok.Data;

@Mapper(componentModel = ComponentModel.SPRING)
public interface DashboardMybatisParam {

     /**
      * 메인대시보드 파라미터
      */
     @Data
     @Alias("mainInput")
     class MainInput {
          String pjtNo;
          String cntrctNo;
          String systemType;
          String loginId;
          String loginType;
          String platform;
     }

     // today
     @Data
     @Alias("todayOutput")
     class TodayOutput {
          String today;
          String dDay;
          String datePercent;
          String cnstwkDaynum;
          String pjtBgnDate;
          String pjtEndDate;
          String sftyAcdntDaynum;
     }

     // 자원 투입현황
     @Data
     @Alias("resourceOutput")
     class ResourceOutput {
          String labor;
          String todayLabor;
          String material;
          String todayMaterial;
          String equipment;
          String todayEquipment;
     }

     // 작업 현황
     @Data
     @Alias("workOutput")
     class WorkOutput {
          String complete;
          String progress;
          String startDelay;
          String endDelay;
          String delay;
     }

     // 물가변동
     @Data
     @Alias("escOutput")
     class EscOutput {
          String escDt;
          Double escRate;
     }

     // 조감도
     @Data
     @Alias("photoOutput")
     class PhotoOutput {
          String fileNm;
          String fileDiskPath;
          String fileDiskNm;
          String titlNm;
          String dscrpt;
     }

     // 주요 자원 activity
     @Data
     @Alias("mainActivityOutput")
     class MainActivityOutput {
          String wbsNm;
          String activityNm;
          String planStart;
          String planFinish;
          String intlDuration;
          String actualStart;
          String actualFinish;
          String remndrDuration;
          String wbsLevel;

          String period;
          String days;
          String state;
     }

     // 결재 현황
     @Data
     @Alias("auditOutput")
     class AuditOutput {
          String dratCount;
          String workCount;
          String etcCount;
     }

     // 친환경 인증
     @Data
     @Alias("ecoScoreOutput")
     class EcoScoreOutput {
          String greenLevel;
          String energyEffectLevel;
          String zeroEnergyLevel;
          String greenLevelDocId;
          String energyEffectLevelDocId;
          String zeroEnergyLevelDocId;
          String evironmentMtrl;
          String co2Mtrl;
          String ecoMtrl;
     }

     // 관급자재 현황
     @Data
     @Alias("govsplyMtrlListOutput")
     class GovsplyMtrlListOutput {
          String dtlCnsttyNm;
          String specNm;
          String totalCount;
     }

     // 공정율
     @Data
     @Alias("processOutput")
     class ProcessOutput {
          String planPer;
          String actualPer;
          String process;
          String planCstPer;
          String actualCstPer;
          String cstProcess;
     }

     /**
      * 종합대시보드01 파라미터
      */
     @Data
     @Alias("regionOutput")
     class RegionOutput {
          String cmnCdNmKrn;
          String cmnCdNmEng;
          String cmnCdNm;
          String cmnCd;
          String count;
          String lv1Count;
          String lv2Count;
          String lv3Count;

          String loginType;
          String usrId;
          String pjtType;
     }

     @Data
     @Alias("regionProjectInput")
     class RegionProjectInput {
          String cmnCd;
          String rgnCd;

          String loginType;
          String usrId;
          String pjtType;

     }

     @Data
     @Alias("regionProjectOutput")
     class RegionProjectOutput {
          String cmnCdNmKrn;
          String cmnCdNmEng;
          String pjtNm;
          String cnstwkCst;
          String insptrNm;
          String dminsttNm;
          String cntrctCorpNm;
          String cmNm;
          String spvsCorpNm;
          String ntpDate;
          String pjtEndDate;
          String totalProjectCount;
          String planPer;
          String actualPer;
          String process;

          String loginType;
          String usrId;
          String pjtType;
     }

     @Data
     @Alias("rankProjectInput")
     class RankProjectInput {
          String rankOrder;

          String loginType;
          String usrId;
          String pjtType;
     }

     @Data
     @Alias("rankProjectOutput")
     class RankProjectOutput {
          String pjtNm;
          String dminsttNm;
          String cntrctCorpNm;
          String cmNm;
          String spvsCorpNm;
          String periodDate;
          String insptrNm;

          String loginType;
          String usrId;
          String pjtType;
     }

     @Data
     @Alias("bigDataOutput")
     class BigDataOutput {
          String laborCount;
          String materialCount;
          String dminsttCount;
          String corpCount;
          String scontrctCorpCount;

          String loginType;
          String usrId;
          String pjtType;
     }

     @Data
     @Alias("bigDataPhotoOutput")
     class BigDataPhotoOutput {
          String titlNm;
          String dscrpt;
          String fileNm;
          String fileDiskPath;
          String fileDiskNm;

          String loginType;
          String usrId;
          String pjtType;
     }
}
