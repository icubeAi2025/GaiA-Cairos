package kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.overview.information;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import kr.co.ideait.iframework.annotation.Description;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnProject;
import kr.co.ideait.platform.gaiacairos.core.config.GlobalMapperConfig;
import kr.co.ideait.platform.gaiacairos.core.persistence.model.CommonForm;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(config = GlobalMapperConfig.class)
public interface InformationForm {

    InformationMybatisParam.InformationListInput toInformationListInput(InformationListGet informationListGet);

    CnProject toRegisterInformation(RegisterInformation information); // Dto to Entity

    void updateCnProject(InformationUpdate informationUpdate, @MappingTarget CnProject cnProject);

    /*
     * 프로젝트 검색
     */
    @Data
    @EqualsAndHashCode(callSuper = false)
    public class InformationListGet extends CommonForm {
        String searchTerm;
        String startDate;
        String endDate;
        String loginId;
        String pjtDiv;
        // String searchType;
        // String searchText; CommonForm에 있음
    }

    /**
     * 프로젝트 등록
     */
    @Data
    public class RegisterInformation {
        String pjtNo; // 프로젝트번호
        String plcReqNo; // 현창개설요청번호
        String pjtDiv;
        @NotBlank(message = "공사명은 필수 입력 값입니다.")
        String pjtNm; // 공사명
        @NotBlank(message = "공사구분은 필수 입력 값입니다.")
        String cnstwkType; // 공사구분
        String plcLctAdrs; // 현장위치주소내용
        double plcLctX; // 현장위치_X
        double plcLctY; // 현장위치_Y
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
        String dminsttDiv; // 수요기관 구분
        String dminsttNm; // 수요기관명
        String dminsttCd; // 수요기관코드
        Long cnstwkCst; // 공사금액
        Long chgCntrctAmt; // 변경공사금액
        String conPstats; // 진행상태 1:개설중 2:시공 3:준공 4:완료
        @NotBlank(message = "사용여부는 필수 입력 값입니다.")
        @Size(max = 1)
        String useYn; // 사용여부
        String airvwAtchFileNo; // 조감도첨부파일번호
        String etcCntnts; // 기타내용
        String cmnCdNmKrn;
        @NotBlank(message = "지역구분은 필수 입력 값입니다.")
        String rgnCd;
        String insptrNm;
        String cmNm;
        String spvsCorpNm;
        String cntrctCorpNm;
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
    }

    /*
     * 프로젝트 수정
     */
    @Data
    public class InformationUpdate {
        String pjtNo; // 프로젝트번호
        @NotBlank(message = "공사명은 필수 입력 값입니다.")
        String pjtNm; // 공사명
        @NotBlank(message = "공사구분은 필수 입력 값입니다.")
        String cnstwkType; // 공사구분
        String plcLctAdrs; // 현장위치주소내용
        double plcLctX; // 현장위치_X
        double plcLctY; // 현장위치_Y
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
        String dminsttDiv; // 수요기관 구분
        String dminsttNm; // 수요기관명
        String dminsttCd; // 수요기관명
        Long cnstwkCst; // 공사금액
        Long chgCntrctAmt; // 변경공사금액
        String conPstats; // 진행상태 1:개설중 2:시공 3:준공 4:완료
        @NotBlank(message = "사용여부는 필수 입력 값입니다.")
        @Size(max = 1)
        String useYn; // 사용여부
        String airvwAtchFileNo; // 조감도첨부파일번호
        String etcCntnts; // 기타내용
        String cmnCdNmKrn;
        @NotBlank(message = "지역구분은 필수 입력 값입니다.")
        String rgnCd;
        String insptrNm;
        String cmNm;
        String spvsCorpNm;
        String cntrctCorpNm;
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
    }

    /* 프로젝트 삭제 */
    @Data
    public class InformationList {
        List<String> informationList;
    }

    /**
     * 첨부 파일
     */
    @Data
    public class CnaAttachMent {
        int fileNo;
        int sno;
        String fileNm;
        String fileDiskNm;
        String fileDiskPath;
        String fileSize;
        String fileHitNum;
        String dltYn;
        String rgstrId;
        String rgstrDt;
        String chgId;
        String chgDt;
        String dltId;
        String dltDt;
    }

    // 친환경 첨부파일
    @Data
    public class evrfrndDocument {
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
    }

    /*
     * 프로젝트 추가시 프로시저에 넘길 데이터
     */
    @Data
    @EqualsAndHashCode(callSuper = false)
    public class projectProcedure {
        String pInserttype;
        String pPjttype;
        String pPjtno;
        String pCntrctno;
        String pItemname;
        String pItemdesc;
        String pCorpno;
    }

}
