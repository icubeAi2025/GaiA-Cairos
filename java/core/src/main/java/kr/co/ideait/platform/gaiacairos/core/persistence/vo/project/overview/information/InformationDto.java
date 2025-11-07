package kr.co.ideait.platform.gaiacairos.core.persistence.vo.project.overview.information;

import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnAttachments;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnProject;
import kr.co.ideait.platform.gaiacairos.core.config.GlobalMapperConfig;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = GlobalMapperConfig.class)
public interface InformationDto {
    Information fromCnProjectOutput(InformationMybatisParam.InformationOutput informationOutput);

    InformationMybatisParam.InformationOutput toInformation(InformationMybatisParam.InformationOutput InformationOutput);

    registerInformation toInformationRegister(CnProject cnProject);

    Information fromCnProject(CnProject cnProject);

    infoAttachMent toInfoAttachments(CnAttachments cnAttachments);

    @Data
    class Information {
        @Description(name = "프로젝트 번호", description = "", type = Description.TYPE.FIELD)
        String pjtNo;
        @Description(name = "공사명", description = "", type = Description.TYPE.FIELD)
        String pjtNm;
        @Description(name = "공사구분", description = "", type = Description.TYPE.FIELD)
        String cnstwkType;
        @Description(name = "현장위치 주소내용", description = "", type = Description.TYPE.FIELD)
        String plcLctAdrs;
        @Description(name = "현장위치_X좌표", description = "", type = Description.TYPE.FIELD)
        double plcLctX;
        @Description(name = "현장위치_Y좌표", description = "", type = Description.TYPE.FIELD)
        double plcLctY;
        @Description(name = "공사시작일자", description = "", type = Description.TYPE.FIELD)
        String pjtBgnDate;
        @Description(name = "공자종료날짜", description = "", type = Description.TYPE.FIELD)
        String pjtEndDate;
        @Description(name = "공사일수", description = "", type = Description.TYPE.FIELD)
        Integer cnstwkDaynum;
        @Description(name = "공사승인일자", description = "", type = Description.TYPE.FIELD)
        String aprvlDate; // 공사승인일자
        @Description(name = "공사착공일자", description = "", type = Description.TYPE.FIELD)
        String ntpDate; // 공사착공일자
        @Description(name = "건축법상용도코드", description = "", type = Description.TYPE.FIELD)
        String acrarchlawUsgCd; // 건축법상용도코드
        @Description(name = "계약구분 코드", description = "", type = Description.TYPE.FIELD)
        String cntrctType; // 계약구분
        @Description(name = "공사규모내용", description = "", type = Description.TYPE.FIELD)
        String cnstwkScle; // 공사규모내용
        @Description(name = "주차가능수", description = "", type = Description.TYPE.FIELD)
        Integer parkngPsblNum; // 주차가능수
        @Description(name = "연면적값", description = "", type = Description.TYPE.FIELD)
        double totarVal; // 연면적값
        @Description(name = "대지면적값", description = "", type = Description.TYPE.FIELD)
        double lndAreaVal; // 대지면적값
        @Description(name = "건축면적값", description = "", type = Description.TYPE.FIELD)
        double archtctAreaVal; // 건축면적값
        @Description(name = "조경면적값", description = "", type = Description.TYPE.FIELD)
        double landarchtAreaVal; // 조경면적값
        @Description(name = "건폐율", description = "", type = Description.TYPE.FIELD)
        double bdtlRate; // 건폐율
        @Description(name = "용적율", description = "", type = Description.TYPE.FIELD)
        double measrmtRate; // 용적율
        @Description(name = "기준층높이값", description = "", type = Description.TYPE.FIELD)
        double bssFloorHgVal; // 기준층높이값
        @Description(name = "최고높이값", description = "", type = Description.TYPE.FIELD)
        double topHgVal; // 최고높이값
        @Description(name = "주요시설내용", description = "", type = Description.TYPE.FIELD)
        String mainFcltyCntnts; // 주요시설내용
        @Description(name = "수요기관코드", description = "", type = Description.TYPE.FIELD)
        String dminsttCd; // 수요기관코드
        @Description(name = "수요기관명", description = "", type = Description.TYPE.FIELD)
        String dminsttNm; // 수요기관명
        @Description(name = "공사금액", description = "", type = Description.TYPE.FIELD)
        Long cnstwkCst; // 공사금액
        @Description(name = "변경공사금액", description = "", type = Description.TYPE.FIELD)
        Long chgCntrctAmt; // 변경공사금액
        @Description(name = "진행상태 코드", description = "", type = Description.TYPE.FIELD)
        String conPstats; // 진행상태 1:개설중 2:시공 3:준공 4:완료
        @Description(name = "사용여부", description = "", type = Description.TYPE.FIELD)
        String useYn; // 사용여부
        @Description(name = "조감도첨부파일번호", description = "", type = Description.TYPE.FIELD)
        String airvwAtchFileNo; // 조감도첨부파일번호
        @Description(name = "기타내용", description = "", type = Description.TYPE.FIELD)
        String etcCntnts; // 기타내용

        @Description(name = "진행상태 이름", description = "", type = Description.TYPE.FIELD)
        String conPstatsNm;
        @Description(name = "진행상태 영문명", description = "", type = Description.TYPE.FIELD)
        String conPstatsNmEng;
        @Description(name = "진행상태 한글명", description = "", type = Description.TYPE.FIELD)
        String conPstatsNmKrn;

        @Description(name = "계약구분 이름", description = "", type = Description.TYPE.FIELD)
        String cntrctTypeNm;
        @Description(name = "계약구분 한글명", description = "", type = Description.TYPE.FIELD)
        String cntrctTypeNmKrn;
        @Description(name = "계약구분 영문명", description = "", type = Description.TYPE.FIELD)
        String cntrctTypeNmEng;

        @Description(name = "지역 이름", description = "", type = Description.TYPE.FIELD)
        String rgnNm;
        @Description(name = "지역 한글명", description = "", type = Description.TYPE.FIELD)
        String rgnNmKrn;
        @Description(name = "지역 영문명", description = "", type = Description.TYPE.FIELD)
        String rgnNmEng;

        @Description(name = "지역 구분 코드", description = "", type = Description.TYPE.FIELD)
        String rgnCd;
        @Description(name = "관리관명", description = "", type = Description.TYPE.FIELD)
        String insptrNm;
        @Description(name = "CM명", description = "", type = Description.TYPE.FIELD)
        String cmNm;
        @Description(name = "감리회사명", description = "", type = Description.TYPE.FIELD)
        String spvsCorpNm;
        @Description(name = "계약회사명", description = "", type = Description.TYPE.FIELD)
        String cntrctCorpNm;

        @Description(name = "친환경 점수", description = "", type = Description.TYPE.FIELD)
        Integer evrfrndScr; // 친환경 점수
        @Description(name = "에너지 점수", description = "", type = Description.TYPE.FIELD)
        Integer energyScr; // 에너지 점수
        @Description(name = "bf 점수", description = "", type = Description.TYPE.FIELD)
        Integer bfScr; // bf 점수

        @Description(name = "녹색건축", description = "", type = Description.TYPE.FIELD)
        String greenLevel;
        @Description(name = "녹색건축파일문서ID", description = "", type = Description.TYPE.FIELD)
        String greenLevelDocId;
        @Description(name = "건축물에너지효율", description = "", type = Description.TYPE.FIELD)
        String energyEffectLevel;
        @Description(name = "건축물에너지효율파일문서ID", description = "", type = Description.TYPE.FIELD)
        String energyEffectLevelDocId;
        @Description(name = "제로에너지건축물", description = "", type = Description.TYPE.FIELD)
        String zeroEnergyLevel;
        @Description(name = "제로에너지건축물파일문서ID", description = "", type = Description.TYPE.FIELD)
        String zeroEnergyLevelDocId;
        @Description(name = "BF인증", description = "", type = Description.TYPE.FIELD)
        String bfLevel;
        @Description(name = "BF인증파일문서ID", description = "", type = Description.TYPE.FIELD)
        String bfLevelDocId;
        @Description(name = "환경성선언자재", description = "", type = Description.TYPE.FIELD)
        Integer evironmentMtrl;
        @Description(name = "저탄소자재", description = "", type = Description.TYPE.FIELD)
        Integer co2Mtrl;
        @Description(name = "친환경자재", description = "", type = Description.TYPE.FIELD)
        Integer ecoMtrl;
    }

    /**
     * 현장 개설
     */
    @Data
    public class registerInformation {
        @Description(name = "프로젝트명", description = "", type = Description.TYPE.FIELD)
        String pjtNm;
        @Description(name = "현장위치주소내용", description = "", type = Description.TYPE.FIELD)
        String plcLctAdrsCntnts;
        @Description(name = "공사일수", description = "", type = Description.TYPE.FIELD)
        int cnstwkDaynum;
        @Description(name = "주공종코드", description = "", type = Description.TYPE.FIELD)
        String majorCnsttyCd;
        @Description(name = "계약구분", description = "", type = Description.TYPE.FIELD)
        String cntrctType;
        @Description(name = "공사승인일자", description = "", type = Description.TYPE.FIELD)
        String aprvlDate;
        @Description(name = "주요시설내용", description = "", type = Description.TYPE.FIELD)
        String mainFcltyCntnts;
        @Description(name = "수요기관명", description = "", type = Description.TYPE.FIELD)
        String dminsttNm;
        @Description(name = "담당자명", description = "", type = Description.TYPE.FIELD)
        String ofclNm;
        @Description(name = "이메일", description = "", type = Description.TYPE.FIELD)
        String eMail;
        @Description(name = "전화번호", description = "", type = Description.TYPE.FIELD)
        String telNo;
        @Description(name = "비고", description = "", type = Description.TYPE.FIELD)
        String rmk;
    }

    @Data
    public class infoAttachMent {
        @Description(name = "파일 번호", description = "", type = Description.TYPE.FIELD)
        int fileNo;
        @Description(name = "순번", description = "", type = Description.TYPE.FIELD)
        int sno;
        @Description(name = "파일 이름", description = "", type = Description.TYPE.FIELD)
        String fileNm;
        @Description(name = "파일 DISK 이름", description = "", type = Description.TYPE.FIELD)
        String fileDiskNm;
        @Description(name = "파일 DISK 경로", description = "", type = Description.TYPE.FIELD)
        String fileDiskPath;
        @Description(name = "파일 사이즈", description = "", type = Description.TYPE.FIELD)
        int fileSize;
        @Description(name = "조회수", description = "", type = Description.TYPE.FIELD)
        int fileHitNum;
        @Description(name = "삭제여부", description = "", type = Description.TYPE.FIELD)
        String dltYn;
    }
}
