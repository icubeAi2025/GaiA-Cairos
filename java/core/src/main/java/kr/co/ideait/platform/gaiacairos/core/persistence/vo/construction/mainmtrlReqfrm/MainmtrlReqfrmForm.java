package kr.co.ideait.platform.gaiacairos.core.persistence.vo.construction.mainmtrlReqfrm;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import kr.co.ideait.platform.gaiacairos.core.config.GlobalMapperConfig;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwMainmtrl;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwMainmtrlReqfrm;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CwMainmtrlReqfrmPhoto;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.construction.MainmtrlReqfrmMybatisParam;
import lombok.Data;


@Mapper(config = GlobalMapperConfig.class)
public interface MainmtrlReqfrmForm {

    MainmtrlReqfrmMybatisParam.MainmtrlReqfrmInput toMainmtrlReqfrmInput(MainmtrlReqfrmForm.MainmtrlReqfrm mainmtrlReqfrmInput);

    @Mapping(target = "reqDt", source = "reqDt", qualifiedByName = "stringToLocalDateTime")
    CwMainmtrlReqfrm toEntity(MainmtrlReqfrm mainmtrlReqfrm);

    @Mapping(target = "shotDate", source = "shotDate", qualifiedByName = "stringToLocalDateTime")
    CwMainmtrlReqfrmPhoto toEntity(Photo photo);

    CwMainmtrl toEntity(Mainmtrl mainmtrl);


    @Mapping(target = "reqDt", source = "reqDt", qualifiedByName = "stringToLocalDateTime")
    void updateMainmtrlReqfrm(MainmtrlReqfrm mainmtrlReqfrm, @MappingTarget CwMainmtrlReqfrm cwMainmtrlReqfrm);
    // 검수요청서
    @Data
    class MainmtrlReqfrm {
        String reqfrmNo;    // 검수요청서 No
        String cntrctNo;    // 계약번호
        String docNo;       // 문서번호
        String reqDt;       // 검수요청일자
        String cnsttyCd;    // 공종
        String rxcorpNm;    // 수신업체
        String prdnm;       // 품명
        String makrNm;      // 제조회사명 
        String rmrk;        // 비고

        // 검색
        String searchValue;
        String workType;

        // 사진
        List<Photo> photos;

        // 자재
        List<Mainmtrl> mtrlList;

        // 첨부파일
        List<Integer> deleteSno;

        List<Integer> deletePhtSno;

        // 검수결과 등록/수정
        String cmId;      // 검수자 Id
        String rsltYn;      // 검수 여부
        String rsltCd;      // 검수판정결과
        String rsltOpnin; // 자재검사의견
        LocalDateTime cmDt; // 검수일자

        // 자재 목록
        List<MainmtrlReqfrmForm.Mainmtrl> partialFailList;
    }

    // 자재 목록
    @Data
    class Mainmtrl {
        String reqfrmNo;    // 검수요청서 No
        String cntrctNo;    // 계약번호
        String gnrlexpnsCd;
        String rsceNm;
        String specNm;
        String unit;
        Integer totalQty;
        Integer remainQty;
        Integer todayQty;
        Integer passQty;
        Integer failQty;
        String rmrk;
        String passYn;
    }

    // 리스트
    @Data
    class MainmtrlReqfrmList {  //목록
        List<MainmtrlReqfrm> mainmtrlReqfrmList;
        String imgDir;
        String baseUrl;
    }

    // 사진
    @Data
    class Photo {
        int phtSno;
        String cntnts;
        String lct;
        String shotDate;
    }

    // String타입 날짜 형식으로 변환
    @Named("stringToLocalDateTime")
    public static LocalDateTime stringToLocalDateTime(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE)
                .atStartOfDay(ZoneOffset.UTC)
                .toLocalDateTime();
    }
}
