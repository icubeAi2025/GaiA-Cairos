package kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.overview.information;

import java.time.LocalDateTime;

import org.apache.ibatis.type.Alias;

import lombok.Data;

public interface InformationMybatisParam {

    @Data
    @Alias("informationListInput")
    public class InformationListInput {
        String pjtNo;
        String searchTerm;
        String pjtDiv;
        String startDate;
        String endDate;
        String searchType;
        String searchText;
        String loginId;
        String usrId;
        String userType;
        String systemType;

        String cmnGrpCdMajorCnstty;
        String cmnGrpCdConPstats;
        String cmnGrpCdRgnCd;
        String cmnGrpCdAcrarchlawUsgCd;
        String cmnGrpCdCntrctType;
        String cmnGrpCdDminsttCd;
    }

    @Data
    @Alias("InformationOutput")
    public class InformationOutput {
        String pjtNo; // 프로젝트번호
        String pjtNm; // 공사명
        String cnstwkType; // 공사구분
        String plcLctAdrs; // 현장위치주소내용
        double plcLctX; // 현장위치_X
        double plcLctY; // 현장위치_Y
        String rgnCd;
        String pjtBgnDate; // 공사시작일자
        String pjtEndDate; // 공자종료날짜
        Integer cnstwkDaynum; // 공사일수
        String aprvlDate; // 공사승인일자
        String ntpDate; // 공사착공일자
        String acrarchlawUsgCd; // 건축법상용도코드
        String cntrctType; // 계약구분
        String cnstwkScle; // 공사규모내용
        Integer parkngPsblNum; // 주차가능수
        double totarVal; // 연면적값
        double lndAreaVal; // 대지면적값
        double archtctAreaVal; // 건축면적값
        double landarchtAreaVal; // 조경면적값
        double bdtlRate; // 건폐율
        double measrmtRate; // 용적율
        double bssFloorHgVal; // 기준층높이값
        double topHgVal; // 최고높이값
        String mainFcltyCntnts; // 주요시설내용
        String dminsttDiv; // 수요기관구분
        String dminsttNm; // 수요기관명
        String dminsttCd; // 수요기관코드
        Long cnstwkCst; // 공사금액
        Long chgCntrctAmt; // 변경공사금액
        String conPstats; // 진행상태 1:개설중 2:시공 3:준공 4:완료
        String useYn; // 사용여부
        String airvwAtchFileNo; // 조감도첨부파일번호

        Integer evrfrndScr; // 친환경 점수
        Integer energyScr; // 에너지 점수
        Integer bfScr; // bf 점수

        String greenLevel;
        String greenLevelDocId;
        String energyEffectLevel;
        String energyEffectLevelDocId;
        String zeroEnergyLevel;
        String zeroEnergyLevelDocId;
        String bfLevel;
        String bfLevelDocId;
        Integer evironmentMtrl;
        Integer co2Mtrl;
        Integer ecoMtrl;

        String conPstatsNmKrn;
        String conPstatsNmEng;
        String cntrctTypeNmKrn;
        String cntrctTypeNmEng;
        String etcCntnts; // 기타내용

        String periodDate;
        String rgstrId;
        LocalDateTime rgstrDt;
        String chgId;
        LocalDateTime chgDt;
        String dltId;
        LocalDateTime dltDt;

        String cnstwkTypeNm;
        String rgnCdNm;
        String acrarchlawUsgCdNm;
        String cntrctTypeNm;
        String conPstatsNm;

        String insptrNm;
        String cmNm;
        String spvsCorpNm;
        String cntrctCorpNm;
        String cntrctNo;
        String cntrctNm;
        String pjtDiv;
    }

    @Data
    @Alias("projectProcedure")
    public class ProjectProcedure {
        String pInserttype;
        String pPjttype;
        String pPjtno;
        String pCntrctno;
        String pItemname;
        String pItemdesc;
        String pCorpno;
    }

    @Data
    @Alias("informationDeleteInput")
    public class InformationDeleteInput {
        String pjtNo;
        String usrId;
    }
}
