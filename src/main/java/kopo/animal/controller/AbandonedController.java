package kopo.animal.controller;

import kopo.animal.controller.response.CommonResponse;
import kopo.animal.dto.AbandonedDTO;
import kopo.animal.dto.MsgDTO;
import kopo.animal.service.IAbandonedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequestMapping(value = "/abandoned")
@RequiredArgsConstructor
@Controller
public class AbandonedController {

    private final IAbandonedService abandonedService;

    /**
     * 유기동물 리스트 저장하기
     * */
    @ResponseBody
    @GetMapping(value = "collectAnimal")
    public ResponseEntity collectAnimal() throws Exception {

        log.info(this.getClass().getName() + ".controller 유기동물 정보 저장 시작!!!");

        // 수집 결과 출력
        String msg = "";

        int res = abandonedService.collectAbandoned();

        if (res == 1) {
            msg = "유기동물 정보 수집 성공!";

        } else {
            msg = "유기동물 정보 수집 실패!";

        }

        MsgDTO dto = new MsgDTO();
        dto.setResult(res);
        dto.setMsg(msg);

        log.info(this.getClass().getName() + ".controller 유기동물 정보 저장 종료!!!");

        return ResponseEntity.ok(
                CommonResponse.of(HttpStatus.OK, HttpStatus.OK.series().name(), dto));
    }

    /**
     * 유기동물 리스트 가져오기
     * */
    @PostMapping(value = "getAbandonedInfo")
    public ResponseEntity getAbandonedInfo() throws Exception {

        log.info(this.getClass().getName() + ".controller 유기동물 정보 가져오기 시작!");

        List<AbandonedDTO> rList = Optional.ofNullable(abandonedService.getAbandonedList())
                .orElseGet(ArrayList::new);


        log.info(this.getClass().getName() + ".controller 유기동물 정보 가져오기 끝!");

        return ResponseEntity.ok(
                CommonResponse.of(HttpStatus.OK, HttpStatus.OK.series().name(), rList));
    }

    @GetMapping(value = "abandonedList")
    public String abandonedList() throws Exception {

        log.info(this.getClass().getName() + ".controller abandonedList 시작!");

        log.info(this.getClass().getName() + ".controller abandonedList 종료!");

        return "/abandoned/abandonedList";
    }
}
