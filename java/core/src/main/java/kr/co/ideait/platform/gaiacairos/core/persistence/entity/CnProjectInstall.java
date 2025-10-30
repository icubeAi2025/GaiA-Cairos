package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class CnProjectInstall extends AbstractRudIdTime {

    @Id
    @Description(name = "현장 개설 요청 번호", description = "", type = Description.TYPE.FIELD)
    String plcReqNo;

    @Description(name = "현장명", description = "", type = Description.TYPE.FIELD)
    String plcNm;

    @Description(name = "주공종코드", description = "", type = Description.TYPE.FIELD)
    String majorCnsttyCd;

    @Description(name = "현장위치주소내용", description = "", type = Description.TYPE.FIELD)
    String plcLctAdrsCntnts;

    @Description(name = "공사시작일자", description = "", type = Description.TYPE.FIELD)
    String pjtBgnDate;

    @Description(name = "공사종료일자", description = "", type = Description.TYPE.FIELD)
    String pjtEndDate;

    @Column(columnDefinition = "NUMERIC")
    @Description(name = "공사일수", description = "", type = Description.TYPE.FIELD)
    Integer cnstwkDaynum;

    @Description(name = "계약구분", description = "", type = Description.TYPE.FIELD)
    String cntrctType;

    @Description(name = "공사승인일자", description = "", type = Description.TYPE.FIELD)
    String aprvlDate;

    @Description(name = "주요시설내용", description = "", type = Description.TYPE.FIELD)
    String mainFcltyCntnts;

    @Description(name = "수요기관코드", description = "", type = Description.TYPE.FIELD)
    String dminsttCd;

    @Description(name = "수요기관명", description = "", type = Description.TYPE.FIELD)
    String dminsttNm;

    @Description(name = "비고", description = "", type = Description.TYPE.FIELD)
    String rmk;

    @Description(name = "담당자ID", description = "", type = Description.TYPE.FIELD)
    String ofclId;

    @Description(name = "담당자", description = "", type = Description.TYPE.FIELD)
    String ofclNm;

    @Column(name = "e_mail")
    @Description(name = "E-mail", description = "", type = Description.TYPE.FIELD)
    String email;

    @Description(name = "전화번호", description = "", type = Description.TYPE.FIELD)
    String telNo;

    @Column(columnDefinition = "NUMERIC")
    @Description(name = "첨부파일번호", description = "", type = Description.TYPE.FIELD)
    Integer atchFileNo;

    @Description(name = "개설진행상태", description = "", type = Description.TYPE.FIELD)
    String openPstats;

    @Description(name = "삭제여부", description = "", type = Description.TYPE.FIELD)
    String dltYn;

    @Description(name = "현장 개설 구분", description = "", type = Description.TYPE.FIELD)
    @Column(name = "plt_req_type")
    String pltReqType;
}