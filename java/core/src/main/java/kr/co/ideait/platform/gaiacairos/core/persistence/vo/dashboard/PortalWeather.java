package kr.co.ideait.platform.gaiacairos.core.persistence.vo.dashboard;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

public class PortalWeather {

     @Getter
     @Setter
     @JsonProperty("response")
     private Response response;

     @Getter
     @Setter
     public static class Response {
          @JsonProperty("body")
          private Body body;
          @JsonProperty("header")
          private Header header;
     }

     @Getter
     @Setter
     public static class Body {
          @JsonProperty("numOfRows")
          private int numOfRows;        // 한 페이지당 표출 수
          @JsonProperty("pageNo")
          private int pageNo;           // 페이지 수
          @JsonProperty("totalCount")
          private int totalCount;       // 데이터 총 개수
          @JsonProperty("items")
          private Items items;          //
          @JsonProperty("dataType")
          private String dataType;      // 응답 자료 형식
     }

     @Getter
     @Setter
     public static class Items {
          @JsonProperty("item")
          private List<Item> item;
     }


     @Getter
     @Setter
     @ToString
     public static class Item {
          @JsonProperty("baseDate")
          private String baseDate;      // 발표일자
          @JsonProperty("baseTime")
          private String baseTime;      // 발표시각
          @JsonProperty("fcstDate")
          private String fcstDate;      // 예보일자
          @JsonProperty("fcstTime")
          private String fcstTime;      // 예보시각
          @JsonProperty("category")
          private String category;      // 자료구분코드
          @JsonProperty("fcstValue")
          private String fcstValue;     // 예보 값
          @JsonProperty("nx")
          private String nx;            // 예보지점 x 좌표
          @JsonProperty("ny")
          private String ny;            // 예보지점 y 좌표
     }

     // 초단기실황
     @Getter
     @Setter
     @ToString
     public static class Item1 {
          @JsonProperty("baseDate")
          private String baseDate;      // 발표일자
          @JsonProperty("baseTime")
          private String baseTime;      // 발표시각
          @JsonProperty("nx")
          private String nx;            // 예보지점 x 좌표
          @JsonProperty("ny")
          private String ny;            // 예보지점 y 좌표
          @JsonProperty("category")
          private String category;      // 자료구분코드
          @JsonProperty("obsrValue")
          private String obsrValue;     // 실황 값
     }

     // 지상관측
     @Getter
     @Setter
     @ToString
     public static class GroundItem {
          @JsonProperty("stnId")
          private String stnId;			// 지점 번호
          @JsonProperty("stnNm")
          private String stnNm;			// 지점명
          @JsonProperty("tm")
          private String tm;				// 시간
          @JsonProperty("avgTa")
          private String avgTa;			// 평균 기온
          @JsonProperty("minTa")
          private String minTa;			// 최저 기온
          @JsonProperty("minTaHrmt")
          private String minTaHrmt;		// 최저 기온 시각
          @JsonProperty("maxTa")
          private String maxTa;			// 최고 기온
          @JsonProperty("maxTaHrmt")
          private String maxTaHrmt;		// 최고 기온 시각
          @JsonProperty("sumRnDur")
          private String sumRnDur;		// 강수 계속시간
          @JsonProperty("mi10MaxRn")
          private String mi10MaxRn;		// 10분 최다강수량
          @JsonProperty("mi10MaxRnHrmt")
          private String mi10MaxRnHrmt;	// 10분 최다강수량 시각
          @JsonProperty("hr1MaxRn")
          private String hr1MaxRn;		// 1시간 최다강수량
          @JsonProperty("hr1MaxRnHrmt")
          private String hr1MaxRnHrmt;	// 1시간 최다 강수량 시각
          @JsonProperty("sumRn")
          private String sumRn;			// 일강수량
          @JsonProperty("maxInsWs")
          private String maxInsWs;		// 최대 순간풍속
          @JsonProperty("maxInsWsWd")
          private String maxInsWsWd;		// 최대 순간 풍속 풍향
          @JsonProperty("maxInsWsHrmt")
          private String maxInsWsHrmt;	// 최대 순간풍속 시각
          @JsonProperty("maxWs")
          private String maxWs;			// 최대 풍속
          @JsonProperty("maxWsWd")
          private String maxWsWd;			// 최대 풍속 풍향
          @JsonProperty("maxWsHrmt")
          private String maxWsHrmt;		// 최대 풍속 시각
          @JsonProperty("avgWs")
          private String avgWs;			// 평균 풍속
          @JsonProperty("hr24SumRws")
          private String hr24SumRws;		// 풍정합
          @JsonProperty("maxWd")
          private String maxWd;			// 최다 풍향
          @JsonProperty("avgTd")
          private String avgTd; 			// 평균 이슬점온도
          @JsonProperty("minRhm")
          private String minRhm;			// 최소 상대습도
          @JsonProperty("minRhmHrmt")
          private String minRhmHrmt;		// 평균 상대습도 시각
          @JsonProperty("avgRhm")
          private String avgRhm;			// 평균 상대습도
          @JsonProperty("avgPv")
          private String avgPv;			// 평균 증기압
          @JsonProperty("avgPa")
          private String avgPa;			// 평균 현지기압
          @JsonProperty("maxPs")
          private String maxPs;			// 최고 해면 기압
          @JsonProperty("maxPsHrmt")
          private String maxPsHrmt;		// 최고 해면기압 시각
          @JsonProperty("minPs")
          private String minPs;			// 최저 해면기압
          @JsonProperty("minPsHrmt")
          private String minPsHrmt;		// 최저 해면기압 시각
          @JsonProperty("avgPs")
          private String avgPs;			// 평균 해면기압
          @JsonProperty("ssDur")
          private String ssDur;			// 가조시간
          @JsonProperty("sumSsHr")
          private String sumSsHr;			// 합계 일조 시간
          @JsonProperty("hr1MaxIcsrHrmt")
          private String hr1MaxIcsrHrmt;	// 1시간 최다 일사 시각
          @JsonProperty("hr1MaxIcsr")
          private String hr1MaxIcsr;		// 1시간 최다 일사량
          @JsonProperty("sumGsr")
          private String sumGsr;			// 합계 일사량
          @JsonProperty("ddMefs")
          private String ddMefs;			// 일 최심신적설
          @JsonProperty("ddMefsHrmt")
          private String ddMefsHrmt;		// 일 최심신적설 시각
          @JsonProperty("ddMes")
          private String ddMes;			// 일 최심적설
          @JsonProperty("ddMesHrmt")
          private String ddMesHrmt;		// 일 최심적설 시각
          @JsonProperty("sumDpthFhsc")
          private String sumDpthFhsc;		// 합계 3시간 신적설
          @JsonProperty("avgTca")
          private String avgTca;			// 평균 전운량
          @JsonProperty("avgLmac")
          private String avgLmac;			// 평균 중하층운량
          @JsonProperty("avgTs")
          private String avgTs;			// 평균 지면온도
          @JsonProperty("minTg")
          private String minTg;			// 최저 초상온도
          @JsonProperty("avgCm5Te")
          private String avgCm5Te;		// 평균 5cm 지중온도
          @JsonProperty("avgCm10Te")
          private String avgCm10Te;		// 평균10cm 지중온도
          @JsonProperty("avgCm20Te")
          private String avgCm20Te;		// 평균 20cm 지중온도
          @JsonProperty("avgCm30Te")
          private String avgCm30Te;		// 평균 30cm 지중온도
          @JsonProperty("avgM05Te")
          private String avgM05Te;		// 0.5m 지중온도
          @JsonProperty("avgM10Te")
          private String avgM10Te;		// 1.0m 지중온도
          @JsonProperty("avgM15Te")
          private String avgM15Te;		// 1.5m 지중온도
          @JsonProperty("avgM30Te")
          private String avgM30Te;		// 3.0m 지중온도
          @JsonProperty("avgM50Te")
          private String avgM50Te;		// 5.0m 지중온도
          @JsonProperty("sumLrgEv")
          private String sumLrgEv;		// 합계 대형증발량
          @JsonProperty("sumSmlEv")
          private String sumSmlEv;		// 합계 소형증발량
          @JsonProperty("n99Rn")
          private String n99Rn;			// 9-9강수
          @JsonProperty("iscs")
          private String iscs;			// 일기현상
          @JsonProperty("sumFogDur")
          private String sumFogDur;		// 안개 계속 시간


     }

     @Getter
     @Setter
     public static class Header {
          @JsonProperty("resultMsg")
          private String resultMsg;
          @JsonProperty("resultCode")
          private String resultCode;

     }
}
