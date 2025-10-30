package kr.co.ideait.platform.gaiacairos.comp.mail;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;

import kr.co.ideait.platform.gaiacairos.comp.mail.service.MailService;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractComponent;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnPinstallMail;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnUseRequest;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.CnProjectInstallRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.security.UserAuth;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.MailForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class MailComponent extends AbstractComponent {

    @Autowired
    MailForm mailForm;

    @Autowired
    MailService mailService;

    @Autowired
    CnProjectInstallRepository projectRepository;

    // 현장개설요청
    public void sendPJTInstall(String email) throws UnsupportedEncodingException {
        // "슈퍼관리자들의 메일"
        List<String> toMails = mailService.adminMail();

        String setFrom = email; // 발신자 이메일
        String title = "GaiA 현장개설요청"; // 이메일 제목
        String content = email + "님이 현장 개설을 요청하였습니다."; // 이메일 내용

        for (String recipient : toMails) {
            mailService.mailSend(recipient, title, content); // 각 이메일 주소로 메일 전송

            List<String> pjtNos = projectRepository.findLatestPlcReqNo();
            String pjtNo = pjtNos.isEmpty() ? null : pjtNos.get(0);

            CnPinstallMail mail = new CnPinstallMail();
            mail.setPlcReqNo(pjtNo);
            mail.setReceiver(setFrom);
            mail.setSender(recipient);
            mail.setContent(content);

            mailService.savePJTInstall(mail); // 메일을 보낸 뒤 디비에 기록 저장
        }
    }

    // 사용요청
    public void sendUseRequest(MailForm.useRequest useRequest, String platform) throws UnsupportedEncodingException {
        String title = "cairos".equals(platform) ? "[CaiROS] 사용 요청" : "[GaiA] 사용 요청";   // 제목
        String recipient = "gaia@ideait.co.kr"; // 수신자 이메일
        String content = buildUseRequestHtml(useRequest, title, platform); // HTML 본문

        // 메일 전송
        if (!"prod".equals(activeProfile)) {
                title = new StringBuilder(title).append(" [Test]").toString();
        }
        mailService.mailSend(recipient, title, content);

        // cn_use_request에 데이터 저장
        CnUseRequest cnUseRequest = mailForm.toUseRequest(useRequest);
        cnUseRequest.setReqNo(UUID.randomUUID());
        cnUseRequest.setDltYn("N");
        mailService.saveUseRequest(cnUseRequest);

        // cn_request_to_use에 데이터 저장
        Map<String, Object> requestToUse = Maps.newHashMap();

        requestToUse.put("reqUsrId", UserAuth.get(true).getUsrId());
        requestToUse.put("reqPjtNo", useRequest.getPjtNo());
        requestToUse.put("reqCntrctNo", useRequest.getCntrctNo());
        requestToUse.put("reqDeptNo", 0); // 임시번호(추후 화면에서 부서 선택하는 부분 설계 완료 시 수정)
        requestToUse.put("reqUuid", UUID.randomUUID().toString());
        String pjtDiv = "pgaia".equals(platform) ? "P" : "G";
        requestToUse.put("reqPjtDiv", pjtDiv);
        requestToUse.put("reqDt", LocalDateTime.now());
        requestToUse.put("prcStatus", 0);
        requestToUse.put("dltYn", "N");

        mailService.saveRequestToUse(requestToUse);
    }

    // 사용 요청 메일 본문 양식
    public String buildUseRequestHtml(MailForm.useRequest dto, String title, String platform) {
        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html>");
        html.append("<html>");
        html.append("<head>");
        html.append("<meta charset='UTF-8'>");
        html.append("</head>");
        html.append("<body style='font-family: 맑은 고딕, sans-serif; font-size: 14px; color: #333;'>");

        html.append(
                "<div style='border: 1px solid #ccc; padding: 20px; border-radius: 6px; width: 600px; margin: auto;'>");
        html.append("<h2 style='margin-top: 0;'>")
                .append(title)
                .append("</h2>");

        html.append("<table style='width: 100%; border-collapse: collapse; margin-top: 20px;'>");

        html.append("<tr>");
        html.append(
                "<th style='text-align: left; padding: 10px; background-color: #f7f7f7; border: 1px solid #ddd; width: 160px;'>계정</th>");
        html.append("<td style='padding: 10px; border: 1px solid #ddd;'>").append(dto.getLoginId()).append("</td>");
        html.append("</tr>");

        html.append("<tr>");
        html.append(
                "<th style='text-align: left; padding: 10px; background-color: #f7f7f7; border: 1px solid #ddd;'>이름</th>");
        html.append("<td style='padding: 10px; border: 1px solid #ddd;'>").append(dto.getUsrNm()).append("</td>");
        html.append("</tr>");

        html.append("<tr>");
        html.append(
                "<th style='text-align: left; padding: 10px; background-color: #f7f7f7; border: 1px solid #ddd;'>직책 / 직급</th>");
        html.append("<td style='padding: 10px; border: 1px solid #ddd;'>").append(dto.getPosition()).append("</td>");
        html.append("</tr>");

        html.append("<tr>");
        html.append(
                "<th style='text-align: left; padding: 10px; background-color: #f7f7f7; border: 1px solid #ddd;'>기타</th>");
        html.append("<td style='padding: 10px; border: 1px solid #ddd; white-space: pre-wrap;'>")
                .append(dto.getContent())
                .append("</td>");
        html.append("</tr>");

        html.append("<tr>");
        html.append(
                "<th style='text-align: left; padding: 10px; background-color: #f7f7f7; border: 1px solid #ddd;'>프로젝트</th>");
        html.append(
                "<td style='padding: 10px; border: 1px solid #ddd; white-space: pre-wrap;'>")
                .append(dto.getPjtNm())
                .append("</td>");
        html.append("</tr>");

        if ("cairos".equalsIgnoreCase(platform)) {
            html.append("<tr>");
            html.append(
                    "<th style='text-align: left; padding: 10px; background-color: #f7f7f7; border: 1px solid #ddd;'>계약</th>");
            html.append(
                    "<td style='padding: 10px; border: 1px solid #ddd; white-space: pre-wrap;'>")
                    .append(dto.getCntrctNm())
                    .append("</td>");
            html.append("</tr>");
        }

        html.append("</table>");
        html.append("</div>");
        html.append("</body>");
        html.append("</html>");

        return html.toString();
    }
    
    /**
	 * GaiA / CaiROS 신규 사용신청
	 */
    public void sendNewUseRequest(String pjtNm, String jobNm, String usrId) throws UnsupportedEncodingException {
    	
        String title = String.format("%s%s", "CAIROS".equals(platform.toUpperCase()) ? "[CaiROS] 신규 사용신청" : "[GaiA] 신규 사용신청", "prod".equals(activeProfile) ? "" : "[TEST]");   // 제목        
        String recipient = "gaia@ideait.co.kr"; // 수신자 이메일
        
        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html>");
        html.append("<html>");
        html.append("<head>");
        html.append("<meta charset='UTF-8'>");
        html.append("</head>");
        html.append("<body style='font-family: 맑은 고딕, sans-serif; font-size: 14px; color: #333;'>");

        html.append(
                "<div style='border: 1px solid #ccc; padding: 20px; border-radius: 6px; width: 600px; margin: auto;'>");
        html.append("<h2 style='margin-top: 0;'>")
                .append(title)
                .append("</h2>");

        html.append("<table style='width: 100%; border-collapse: collapse; margin-top: 20px;'>");

        html.append("<tr>");
        html.append(
                "<th style='text-align: left; padding: 10px; background-color: #f7f7f7; border: 1px solid #ddd; width: 160px;'>신청 아이디</th>");
        html.append("<td style='padding: 10px; border: 1px solid #ddd;'>").append(usrId).append("</td>");
        html.append("</tr>");

        html.append("<tr>");
        html.append(
                "<th style='text-align: left; padding: 10px; background-color: #f7f7f7; border: 1px solid #ddd;'>사업명</th>");
        html.append("<td style='padding: 10px; border: 1px solid #ddd;'>").append(pjtNm).append("</td>");
        html.append("</tr>");

        html.append("<tr>");
        html.append(
                "<th style='text-align: left; padding: 10px; background-color: #f7f7f7; border: 1px solid #ddd;'>담당업무</th>");
        html.append("<td style='padding: 10px; border: 1px solid #ddd;'>").append(jobNm).append("</td>");
        html.append("</tr>");

        html.append("</table>");
        html.append("</div>");
        html.append("</body>");
        html.append("</html>");  

        // 메일 전송
        mailService.mailSend(recipient, title, html.toString());
//		데이터 DB 저장 추후 설정 여부 검토
//        // cn_use_request에 데이터 저장
//        CnUseRequest cnUseRequest = mailForm.toUseRequest(useRequest);
//        cnUseRequest.setReqNo(UUID.randomUUID());
//        cnUseRequest.setDltYn("N");
//        mailService.saveUseRequest(cnUseRequest);
//
//        // cn_request_to_use에 데이터 저장
//        Map<String, Object> requestToUse = Maps.newHashMap();
//
//        requestToUse.put("reqUsrId", UserAuth.get(true).getUsrId());
//        requestToUse.put("reqPjtNo", useRequest.getPjtNo());
//        requestToUse.put("reqCntrctNo", useRequest.getCntrctNo());
//        requestToUse.put("reqDeptNo", 0); // 임시번호(추후 화면에서 부서 선택하는 부분 설계 완료 시 수정)
//        requestToUse.put("reqUuid", UUID.randomUUID().toString());
//        String pjtDiv = "pgaia".equals(platform) ? "P" : "G";
//        requestToUse.put("reqPjtDiv", pjtDiv);
//        requestToUse.put("reqDt", LocalDateTime.now());
//        requestToUse.put("prcStatus", 0);
//        requestToUse.put("dltYn", "N");
//
//        mailService.saveRequestToUse(requestToUse);
    }    
}
