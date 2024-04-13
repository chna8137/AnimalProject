package kopo.animal.controller;

import jakarta.servlet.http.HttpServletRequest;
import kopo.animal.dto.MailDTO;
import kopo.animal.dto.MsgDTO;
import kopo.animal.service.IMailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@RequestMapping(value = "/mail")
@RequiredArgsConstructor
@Controller
public class MailController {

    private final IMailService mailService; // 메일 발송을 위한 서비스 객체를 사용하기

    /**
     * 메일 발송하기 폼
     */
    @GetMapping(value = "mailForm")
    public String mailForm() throws Exception {

        log.info(this.getClass().getName() + ".mailForm 컨트롤러 시작!");

        return "/mail/mailForm";
    }

    /**
     * 메일 발송하기
     */
}
