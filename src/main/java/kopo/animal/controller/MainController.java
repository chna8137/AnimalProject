package kopo.animal.controller;

import kopo.animal.dto.AbandonedDTO;
import kopo.animal.service.IAbandonedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
public class MainController {

    private final IAbandonedService abandonedService;

    @GetMapping(value = "/index")
    public String index(ModelMap model) throws Exception {
        log.info("인덱스 페이지 시작");

        List<AbandonedDTO> nearEndDateList = abandonedService.getUrgentAbandonedList("ANIMAL_ABANDONED");
        model.addAttribute("nearEndDateList", nearEndDateList);

        log.info("인덱스 페이지 종료");
        return "index";
    }
}
