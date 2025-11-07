package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@Table(name = "cn_project", schema = "gaia_cmis")
@EqualsAndHashCode(callSuper = true)
public class CnProject extends AbstractRudIdTime {
    @Id
    @Description(name = "프로젝트 번호", description = "", type = Description.TYPE.FIELD)
    String pjtNo;
    @Description(name = "프로젝트 구분", description = "", type = Description.TYPE.FIELD)
    @Column(name = "pjt_div")
    String pjtDiv;
    @Description(name = "공사명", description = "", type = Description.TYPE.FIELD)
    String pjtNm;
    @Description(name = "현장 개설 요청 번호", description = "", type = Description.TYPE.FIELD)
    String plcReqNo;
    @Description(name = "공사구분", description = "", type = Description.TYPE.FIELD)
    String cnstwkType;
    @Description(name = "현장위치 주소내용", description = "", type = Description.TYPE.FIELD)
    String plcLctAdrs;
    @Column(columnDefinition = "NUMERIC", name = "plc_lct_x")
    @Description(name = "현장위치_X좌표", description = "", type = Description.TYPE.FIELD)
    Double plcLctX;
    @Column(columnDefinition = "NUMERIC", name = "plc_lct_y")
    @Description(name = "현장위치_Y좌표", description = "", type = Description.TYPE.FIELD)
    Double plcLctY;
    @Description(name = "공사시작일자", description = "", type = Description.TYPE.FIELD)
    String pjtBgnDate;
    @Description(name = "공자종료날짜", description = "", type = Description.TYPE.FIELD)
    String pjtEndDate;
    @Column(columnDefinition = "NUMERIC")
    @Description(name = "공사일수", description = "", type = Description.TYPE.FIELD)
    Integer cnstwkDaynum;
    @Description(name = "공사승인일자", description = "", type = Description.TYPE.FIELD)
    String aprvlDate;
    @Description(name = "공사착공일자", description = "", type = Description.TYPE.FIELD)
    String ntpDate;
    @Description(name = "건축법상용도코드", description = "", type = Description.TYPE.FIELD)
    String acrarchlawUsgCd;
    @Description(name = "계약구분 코드", description = "", type = Description.TYPE.FIELD)
    String cntrctType;
    @Description(name = "공사규모내용", description = "", type = Description.TYPE.FIELD)
    String cnstwkScle;
    @Column(columnDefinition = "NUMERIC")
    @Description(name = "주차가능수", description = "", type = Description.TYPE.FIELD)
    Integer parkngPsblNum;
    @Column(columnDefinition = "NUMERIC")
    @Description(name = "연면적값", description = "", type = Description.TYPE.FIELD)
    Double totarVal;
    @Column(columnDefinition = "NUMERIC")
    @Description(name = "대지면적값", description = "", type = Description.TYPE.FIELD)
    Double lndAreaVal;
    @Column(columnDefinition = "NUMERIC")
    @Description(name = "건축면적값", description = "", type = Description.TYPE.FIELD)
    Double archtctAreaVal;
    @Column(columnDefinition = "NUMERIC")
    @Description(name = "조경면적값", description = "", type = Description.TYPE.FIELD)
    Double landarchtAreaVal;
    @Column(columnDefinition = "NUMERIC")
    @Description(name = "건폐율", description = "", type = Description.TYPE.FIELD)
    Double bdtlRate;
    @Column(columnDefinition = "NUMERIC")
    @Description(name = "용적율", description = "", type = Description.TYPE.FIELD)
    Double measrmtRate;
    @Column(columnDefinition = "NUMERIC")
    @Description(name = "기준층높이값", description = "", type = Description.TYPE.FIELD)
    Double bssFloorHgVal;
    @Column(columnDefinition = "NUMERIC")
    @Description(name = "최고높이값", description = "", type = Description.TYPE.FIELD)
    Double topHgVal;
    @Description(name = "주요시설내용", description = "", type = Description.TYPE.FIELD)
    String mainFcltyCntnts;
    @Description(name = "수요기관코드", description = "", type = Description.TYPE.FIELD)
    String dminsttCd;
    @Description(name = "수요기관명", description = "", type = Description.TYPE.FIELD)
    String dminsttNm;
    @Description(name = "수요기관궈분", description = "", type = Description.TYPE.FIELD)
    String dminsttDiv;
    @Column(columnDefinition = "NUMERIC")
    @Description(name = "공사금액", description = "", type = Description.TYPE.FIELD)
    Long cnstwkCst;
    @Column(columnDefinition = "NUMERIC")
    @Description(name = "변경공사금액", description = "", type = Description.TYPE.FIELD)
    Long chgCntrctAmt;
    @Description(name = "진행상태 코드", description = "", type = Description.TYPE.FIELD)
    String conPstats; // 1:개설중 2:시공 3:준공 4:완료
    @Column(name = "use_yn")
    @Description(name = "사용여부", description = "", type = Description.TYPE.FIELD)
    String useYn;
    @Description(name = "조감도첨부파일번호", description = "", type = Description.TYPE.FIELD)
    String airvwAtchFileNo;
    @Description(name = "기타내용", description = "", type = Description.TYPE.FIELD)
    String etcCntnts;
    @Description(name = "삭제여부", description = "", type = Description.TYPE.FIELD)
    String dltYn;

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
    @Column(columnDefinition = "NUMERIC")
    @Description(name = "친환경 점수", description = "", type = Description.TYPE.FIELD)
    Integer evrfrndScr;
    @Column(columnDefinition = "NUMERIC")
    @Description(name = "에너지 점수", description = "", type = Description.TYPE.FIELD)
    Integer energyScr;
    @Column(columnDefinition = "NUMERIC")
    @Description(name = "bf 점수", description = "", type = Description.TYPE.FIELD)
    Integer bfScr;

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
    @Column(columnDefinition = "NUMERIC")
    @Description(name = "환경성선언자재", description = "", type = Description.TYPE.FIELD)
    Integer evironmentMtrl;
    @Column(columnDefinition = "NUMERIC", name = "co2_mtrl")
    @Description(name = "저탄소자재", description = "", type = Description.TYPE.FIELD)
    Integer co2Mtrl;
    @Column(columnDefinition = "NUMERIC")
    @Description(name = "친환경자재", description = "", type = Description.TYPE.FIELD)
    Integer ecoMtrl;
}