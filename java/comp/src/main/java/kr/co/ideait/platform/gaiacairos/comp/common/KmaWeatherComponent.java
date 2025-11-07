package kr.co.ideait.platform.gaiacairos.comp.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.PatternMatchUtils;

import kr.co.ideait.platform.gaiacairos.comp.common.service.CommonUtilService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractComponent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class KmaWeatherComponent extends AbstractComponent {
	
	@Autowired
	CommonUtilService commonUtilService;
	
	/**
     *  기상청 지점 기온정보 저장
     * 
     * @param  String[] 지점코드 배열
     * @param  String	기상청 URL
     * @param  String	조회 날짜
     * @return void
	 * @throws IOException 
     */
	@Async
	public void setWeatherInfo(String[] cityCode, String urlStr, String gatDay) throws IOException {
		
		List<Map<String, Object>> insertDatalist  = new ArrayList<Map<String, Object>>();	// 기상청 데이터를 담아 DB에 입력할 변수
		String kmaDataOutput;

		URL url = new URL(urlStr); // URL 객체 생성
		HttpURLConnection connection = null;
		BufferedReader br  = null;
		InputStreamReader isr = null;
		InputStream inputStream = null;

		try {
			connection = (HttpURLConnection) url.openConnection(); // 연결 객체 생성
	        connection.setRequestMethod("GET"); // GET 요청 설정
	        connection.setRequestProperty("Accept", "application/json"); // Accept 헤더 설정

			inputStream = connection.getInputStream();

			isr = new InputStreamReader(inputStream, "EUC-KR");
	        br = new BufferedReader(isr); // 입력 스트림

	        while ((kmaDataOutput = br.readLine()) != null) {
	            if (kmaDataOutput.indexOf(gatDay) > -1) {
	                String[] arr = kmaDataOutput.split(",");
	                
	                if(PatternMatchUtils.simpleMatch(cityCode, arr[1])) {
	                	Map<String, Object> map = new HashMap<String, Object>();
		        		map.put("weather_cd", arr[1]);	//지점코드
		        		map.put("weather_dt", gatDay);	//관측일자
		        		map.put("ta_avg", arr[10]);		//평균온도
		        		map.put("ta_max", arr[11]); 	//최고온도
		        		map.put("ta_min", arr[13]);		//최저온도
		        		map.put("ws_max", arr[5]);		//최대풍속
		        		
		        		//강수량
		        		if ("-9.0".equals(arr[38])) {
		                    map.put("rn_day", "0.0");
		                }else {
		                	map.put("rn_day", arr[38]);	
		                }
		        		//적설량
		                if ("-9.0".equals(arr[49])) {
		                    map.put("sd_max", "0.0");
		                }else {
		                	map.put("sd_max", arr[49]);
		                }
		                insertDatalist.add(map);	                	
	                } 
	            }
	        }

	        commonUtilService.insertSmWeatherInfo(insertDatalist);	        
	        log.info("1-1.기온 조회 URL : >>>> {}", urlStr);
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
			if (isr != null) {
				isr.close();
			}
			if (br != null) {
				br.close();
			}
			if (connection != null) {
	        	connection.disconnect(); // 연결 종료
			}
		}
	}
	
	/**
     *  기상청 지점 온도정보 저장
     * 
     * @param  String[] 예보지역코드 배열
     * @param  String	기상청 URL
     * @param  String	조회 날짜
     * @return void
	 * @throws IOException 
     */
	@Async
	public void setForecastInfo(String[] cityAreaCode, String urlStr, String gatDay) throws IOException {
		
		List<Map<String, Object>> insertDatalist  = new ArrayList<Map<String, Object>>();	// 기상청 데이터를 담아 DB에 입력할 변수
		String kmaDataOutput;

		URL url = new URL(urlStr); // URL 객체 생성
		HttpURLConnection connection = null;
		BufferedReader br  = null;
		InputStreamReader isr = null;
		InputStream inputStream = null;

		try {
			connection = (HttpURLConnection) url.openConnection(); // 연결 객체 생성
			connection.setRequestMethod("GET"); // GET 요청 설정
			connection.setRequestProperty("Accept", "application/json"); // Accept 헤더 설정

			inputStream = connection.getInputStream();
			isr = new InputStreamReader(inputStream, "EUC-KR");
	        br = new BufferedReader(isr); // 입력 스트림

	        while ((kmaDataOutput = br.readLine()) != null) {

	        	Map<String, Object> map = new HashMap<String, Object>();
	        	String[] arr = kmaDataOutput.split(",");
	        	
	        	if(PatternMatchUtils.simpleMatch(cityAreaCode, arr[0])) {
	        		if (kmaDataOutput.indexOf(gatDay + "0000") > -1) {
		                map.put("forecast_cd", arr[0]); 		// 오전 예보
		                map.put("forecast_dt", gatDay); 	// 오전 예보
		                map.put("forecast_div", "AM"); 			// 오전 예보
		                map.put("forecast", arr[16]); 		// 오전 예보
		                
		                insertDatalist.add(map);
		            }
		            if (kmaDataOutput.indexOf(gatDay + "1200") > -1) {
		                map.put("forecast_cd", arr[0]); 		// 오후 예보
		                map.put("forecast_dt", gatDay); 	// 오후 예보
		                map.put("forecast_div", "PM"); 			// 오후 예보
		                map.put("forecast", arr[16]); 		// 오후 예보
		                
		                insertDatalist.add(map);
		            }
	        		
	        	}	            
	        }
	        
	        commonUtilService.insertSmForecastInfo(insertDatalist);
	        log.info("2-1.날씨 조회 URL : >>>> {}", urlStr);
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
			if (isr != null) {
				isr.close();
			}
			if (br != null) {
				br.close();
			}
			if (connection != null) {
				connection.disconnect(); // 연결 종료
			}
		}
	}

}
