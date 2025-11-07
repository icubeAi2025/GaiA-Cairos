package kr.co.ideait.platform.gaiacairos.comp.scheduled;

import kr.co.ideait.platform.gaiacairos.comp.common.CommonUtilComponent;
import kr.co.ideait.platform.gaiacairos.comp.system.service.DepartmentService;
import kr.co.ideait.platform.gaiacairos.comp.system.service.UserService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Profile({"dev", "staging", "prod"})
@Slf4j
@Component
public class ScheduledUtil {
	
	@Autowired
	CommonUtilComponent commonUtilComponent;
	
    @Autowired
	UserService userService;

	@Autowired
	DepartmentService departmentService;
	
	@Value("${platform}")
    private String platform;
	
	@Value("${machine:node1}")
    String machine;
    
//    @Scheduled(fixedRate = 5000)
//	public void scheduledTest() {
//		log.debug("==============================================");
//		log.debug("=        5초 간격으로 스케쥴링 돌아감        =");
//		log.debug("==============================================");
//	}
	
//    @Scheduled(cron = "0 0 1 * * *")
//	public void syncOracleUsers() {
//		log.debug("=================================");
//		log.debug("=사용자정보 동기화를 시작합니다.=");
//		log.debug("=================================");
//
//		log.debug("사용자 정보 동기화 하러 오라클 조회!!");
//
//    	List<Map<String, Object>> oracleUserList = userService.getOracleUserList();
//
//    	log.debug("조회 카운트 : " + oracleUserList.size() + "건을 조회하였습니다.");
//
//    	if(oracleUserList.size() > 0) {
//
//
//    		int insertResult = userService.setOracleUserList(oracleUserList);
//
//    		if(insertResult == (oracleUserList.size()+500-1)/500) {
//    			log.debug("사용자 정보 입력을 완료하였습니다.");
//    		}
//    	}
//	}

	@Scheduled(cron = "0 0 1 * * ?")
	public void updateOrganiztionFlag(){
		if ("node1".equals(machine)) {
			log.debug("=================================");
			log.debug("=소속 직원 근무 상태를 변경합니다.=");
			log.debug("=================================");
			//조직에서 종료일이 지나면 근무상태 '퇴직'으로 변경
			departmentService.updateOrganiztionFlag();
        }
	}
	
	@Scheduled(cron = "0 0 6,12 * * ?")
	public void insertWeatherInfo() throws IOException{
		if ("node1".equals(machine) && "CAIROS".equals(platform.toUpperCase())) {
			log.debug("########################################################################################");
			log.debug("#                              기상청 날씨를 등록합니다!!                              #");
			log.debug("########################################################################################");
			
			LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));		
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
	        String formatTodDate = today.format(formatter);
	        
			//정해진 시간에 기상청 날씨 및 예보정보를 가지고와서 DB에 저장
			commonUtilComponent.setKmaWeather("AUTO", formatTodDate);
        }
		
	}
}
