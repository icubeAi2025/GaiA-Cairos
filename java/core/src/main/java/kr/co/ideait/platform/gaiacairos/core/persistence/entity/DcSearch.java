package kr.co.ideait.platform.gaiacairos.core.persistence.entity;

import kr.co.ideait.iframework.annotation.Description;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Data
@EqualsAndHashCode(callSuper = true)
@Alias("dcSearch")
public class DcSearch extends AbstractRudIdTime {

    @Description(name = "문서 No")
    String docNo;

    @Description(name = "문서 ID")
    String docId;

    @Description(name = "네비게이션 No")
    String naviNo;

    @Description(name = "네비게이션 ID")
    String naviId;

    @Description(name = "네비게이션 이름")
    String naviNm;

    @Description(name = "상위문서 No")
    String upDocNo;

    @Description(name = "상위문서 ID")
    String upDocId;

    @Description(name = "문서 종류")
    String docType;

    @Description(name = "문서 경로")
    String docPath;

    @Description(name = "문서 이름")
    String docNm;

    @Description(name = "문서 Disk 이름")
    String docDiskNm;

    @Description(name = "문서 Disk 경로")
    String docDiskPath;

    @Description(name = "문서 URL 경로")
    String docUrlPath;

    @Description(name = "문서 크기")
    String docSize;

    @Description(name = "문서 다운로드 수")
    String docHitNum;

    @Description(name = "문서 휴지통 여부")
    String docTrashYn;

    @Description(name = "프로젝트 번호")
    String pjtNo;

    @Description(name = "계약 번호")
    String cntrctNo;

    @Description(name = "네비게이션 구분")
    String naviDiv;

    @Description(name = "네비게이션 경로")
    String naviPath;

    @Description(name = "상위 네비게이션 No")
    String upNaviNo;

    @Description(name = "상위 네비게이션 ID")
    String upNaviId;

    @Description(name = "레벨")
    String naviLevel;

    @Description(name = "네비게이션 종류")
    String naviType;

    @Description(name = "순번")
    String dsplyOrdr;

    @Description(name = "공유여부")
    String naviSharYn;

    @Description(name = "폴더 종류 구분", description = "0: 기본, 1: 작업일지, 2: 감리일지, ...")
    String naviFolderType;

    @Description(name = "등록자 이름")
    String rgstrNm;

    @Description(name = "수정자 이름")
    String chgNm;

    @Description(name = "착공 문서 번호")
    String cbgnKey;

    @Description(name = "참조 시스템 키", description = "1: PGAIA, 2: GAIA, 3: CAIROS")
    String refSysKey;

    @Description(name = "문서 속성 데이터")
    String propertyData;

    @Description(name = "삭제여부")
    String naviKey;
}