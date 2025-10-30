package kr.co.ideait.platform.gaiacairos.comp.mail.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import kr.co.ideait.platform.gaiacairos.core.base.AbstractGaiaCairosService;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnPinstallMail;
import kr.co.ideait.platform.gaiacairos.core.persistence.entity.CnUseRequest;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.CnPinstallMailRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.jpa.repositories.CnUseRequestRepository;
import kr.co.ideait.platform.gaiacairos.core.persistence.vo.system.MailForm;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MailService extends AbstractGaiaCairosService {

    @Autowired
    MailForm mailForm;

    @Autowired
    JavaMailSender mailSender;

    @Autowired
    CnPinstallMailRepository mailRepository;

    @Autowired
    CnUseRequestRepository useRequestRepository;

    @Value("${spring.mail.username}")
    private String defaultFrom;

    /**
     * 이메일 전송
     */
    public void mailSend(String toMail, String title, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    "UTF-8");

            helper.setFrom(new InternetAddress(defaultFrom, "GaiA 관리자", "UTF-8")); // 포트 설정 메일로 고정(회사 메일 써야함)
            helper.setTo(toMail);
            helper.setSubject(title);
            helper.setText(content, true); // HTML 사용

            mailSender.send(message);
            log.info("메일 전송 성공: {}", toMail);
        } catch (MessagingException | java.io.UnsupportedEncodingException e) {
            log.error("메일 전송 실패: {}", e.getMessage(), e);
        }
    }

    //*************************************현장 개설 요청**************************************/

    // 슈퍼관리자들 메일 찾기
    public List<String> adminMail() {
        List<String> toMails = mybatisSession
                .selectList("kr.co.ideait.platform.gaiacairos.mybatis.mappers.mail.mail.getAdminId");
        return toMails;
    }


    // 현장개설요청 이메일 저장
    public void savePJTInstall(CnPinstallMail mail) {
        mailRepository.save(mail);
    }

    //*************************************사용 요청**************************************/

    // 사용요청 이메일 저장
    public void saveUseRequest(CnUseRequest useRequest) {
        useRequestRepository.save(useRequest);
    }

    // 사용요청 데이터 저장
    public void saveRequestToUse(Map<String, Object> requestToUse){
        mybatisSession.insert("kr.co.ideait.platform.gaiacairos.mybatis.mappers.mail.mail.insertRequest", requestToUse);
    }
}
