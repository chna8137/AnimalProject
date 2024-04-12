package kopo.animal.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@RequiredArgsConstructor
@Controller
public class MainController {

    @GetMapping(value = "index")
    public String index() throws Exception {

        log.info(this.getClass().getName() + "인덱스 시작");

        log.info(this.getClass().getName() + "인덱스 종료");

        return "index";
    }
}
