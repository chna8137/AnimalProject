package kopo.animal.service;

import kopo.animal.dto.MailDTO;

public interface IMailService {

    // 메일 발송
    int doSendMail(MailDTO pDTO);
}
