package kr.co.ideait.platform.gaiacairos.core.persistence.vo.util;

import kr.co.ideait.platform.gaiacairos.core.config.GlobalMapperConfig;
import lombok.Data;
import org.mapstruct.Mapper;

@Mapper(config = GlobalMapperConfig.class)
public interface UtilForm { 

    @Data
    class ComCodeSelectBoxGet {
        String cmnGrpCd;			//공통코드 그룹cd
        String selectBoxId;			//만들 셀렉트박스 id        
        String selectBoxNmType;		//셀렉트박스 TEXT 타입 (KOR : 한글, ENG : 영문)
        String ckeckedValue;		//초기 선택할 값
        String orderByCol;			//정렬 컬럼명
        String orderByType;			//정렬 타입 (ACE : 오름차순, DESC : 내림차순)
        String initText;			//셀렉트박스 첫번재 데이터 값 (예 : 선택, 고르시오 등....)
        String initSelect;			//init 선택 가능 여부
        String paramNm;				//view에서 받을 변수명
        String funName;				// 함수명
        String funParam;			// 함수 변수
        String funtype;				// 이벤트 타입
    }
    
    @Data
    class CommonBoxGet {
        String makeId;				//만들어질 item id
        String col1;				//value로 들어갈 값의 컬럼명
        String col2;				//text로 들어갈 값의 컬럼명
        String tableName;			//조회할 테이블 명
        String addSql;				//WHERE절에 추가할 SQL
        String orderByCol;			//정렬 컬럼명
        String orderByType;			//정렬 타입 (ACE : 오름차순, DESC : 내림차순)
        String boxType;				//item 타입 : (셀렉트박스, 라이오버튼)
        String ckeckedValue;		//초기 선택할 값
        String initText;			//셀렉트박스 첫번재 데이터 값 (예 : 선택, 고르시오 등....)
        String paramNm;				//view에서 받을 변수명
        String funName;				// 함수명
        String funParam;			// 함수 변수
        String funtype;				// 이벤트 타입
    }
    
    @Data
    class PdfView {
        String viewType;			//미리보기 할 파일의 위치 타입
        String viewKey;				//미리보기 할 파일 조회 키
    }
    
    @Data
    class KmaWeather {
    	String pjtNo;
    	String tm;
    	String stn;
    }
}
