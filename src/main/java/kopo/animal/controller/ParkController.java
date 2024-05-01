package kopo.animal.controller;

import kopo.animal.controller.response.CommonResponse;
import kopo.animal.dto.ParkDTO;
import kopo.animal.service.IParkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequestMapping(value = "/map/v1")
@RequiredArgsConstructor
@RestController
public class ParkController {

    private final IParkService parkService;

    /** 수집된 공원 리스트 가져오기 */
    @PostMapping(value = "getParkInfo")
    public ResponseEntity getParkInfo() throws Exception {

        log.info(this.getClass().getName() + ".controller 공원정보 시작!");

        List<ParkDTO> rList = Optional.ofNullable(parkService.getParkInfo())
                .orElseGet(ArrayList::new);

        log.info(this.getClass().getName() + ".controller 공원정보 종료!");

        return ResponseEntity.ok(
                CommonResponse.of(HttpStatus.OK, HttpStatus.OK.series().name(), rList));
    }
}
