package kopo.animal.service.impl;

import jakarta.mail.internet.MimeMessage;
import kopo.animal.dto.MailDTO;
import kopo.animal.service.IMailService;
import kopo.animal.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class MailService implements IMailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromMail;

    @Override
    public int doSendMail(MailDTO pDTO) {

        log.info(this.getClass().getName() + ".doSendMail 서비스 시작!");

        // 메일 발송 성공여부(발송성공 : 1 / 발송실패 : 0)
        int res = 1;

        // 전달받은 DTO로부터 데이터 가져오기
        if (pDTO == null) {
            pDTO = new MailDTO();
        }

        String toMail = CmmUtil.nvl(pDTO.getToMail()); // 받는사람
        String title = CmmUtil.nvl(pDTO.getTitle()); // 메일제목
        String contents = CmmUtil.nvl(pDTO.getContents()); // 메일제목


        log.info("toMail : " + toMail);
        log.info("title : " + title);
        log.info("contents : " + contents);

        // 메일 발송 메시지 구조(파일 첨부 가능)
        MimeMessage message = mailSender.createMimeMessage();

        // 메일 발송 메시지 구조를 쉽게 생성하게 도와주는 객체
        MimeMessageHelper messageHelper = new MimeMessageHelper(message, "UTF-8");

        try {

            messageHelper.setTo(toMail); // 받는 사람
            messageHelper.setFrom(fromMail); // 보내는 사람
            messageHelper.setSubject(title); // 메일 제목
            messageHelper.setText(contents); // 메일 내용

            mailSender.send(message);

        } catch (Exception e) {
            res = 0; // 메일 발송이 실패했기 때문에 0으로 변경
            log.info("[ERROR] " + this.getClass().getName() + ".doSendMail : " + e);
        }

        log.info(this.getClass().getName() + ".doSendMail 서비스 끝!");

        return res;
    }
}
