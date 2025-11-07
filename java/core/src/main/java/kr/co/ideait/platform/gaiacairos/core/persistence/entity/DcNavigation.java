package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class DcNavigation extends AbstractRudIdTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Description(name = "네비게이션 No", description = "")
    Integer naviNo;

    @Description(name = "네비게이션 ID", description = "")
    String naviId;

    @Description(name = "프로젝트번호", description = "")
    String pjtNo;

    @Description(name = "계약번호", description = "")
    String cntrctNo;

    @Description(name = "네비게이션 구분", description = "")
    String naviDiv;

    @Description(name = "네비게이션 경로", description = "")
    String naviPath;

    @Description(name = "네비게이션 이름", description = "")
    String naviNm;

    @Description(name = "상위네비게이션 No", description = "")
    Integer upNaviNo;

    @Description(name = "상위네비게이션 ID", description = "")
    String upNaviId;

    @Description(name = "레벨", description = "")
    Short naviLevel;

    @Description(name = "네비게이션 종류", description = "FOLDR: 폴더형, ITEM: 아이템형")
    String naviType;

    @Description(name = "폴더 종류 구분", description = "0: 기본, 1: 작업일지, 2: 감리일지, ...")
    String naviFolderType;

    @Description(name = "순번", description = "")
    Short dsplyOrdr;

    @Description(name = "공유여부", description = "")
    String naviSharYn;

    @Description(name = "삭제여부", description = "")
    String dltYn;

    @Description(name = "참조 시스템 키", description = "1: PGAIA, 2: GAIA, 3: CAIROS")
    Integer refSysKey;

    @Description(name = "네비게이션 키", description = "")
    String naviKey;

    @Description(name = "역할 구분", description = "감리, 시공사, ...")
    String svrType;


}
