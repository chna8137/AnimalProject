package kopo.animal.controller;

import jakarta.servlet.http.HttpServletRequest;
import kopo.animal.controller.response.CommonResponse;
import kopo.animal.dto.ParkDTO;
import kopo.animal.service.IParkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequestMapping(value = "/map")
@RequiredArgsConstructor
@Controller
public class MapController {

    private final IParkService parkService;

    @GetMapping(value = "park")
    public String park() throws Exception {

        log.info(this.getClass().getName() + ".controller 공원정보 시작!");

        log.info(this.getClass().getName() + ".controller 공원정보 종료!");

        return "map/park";
    }

    /**
     * 수집된 공원 리스트 가져오기
     */
    @PostMapping(value = "/v1/getParkInfo")
    public ResponseEntity getParkInfo(@RequestParam(defaultValue = "1") int page,
                                      @RequestParam(defaultValue = "20") int itemsPerPage) throws Exception {

        log.info(this.getClass().getName() + ".controller getParkInfo 시작!");

        List<ParkDTO> rList = Optional.ofNullable(parkService.getParkInfo(page, itemsPerPage))
                .orElseGet(ArrayList::new);

        log.info(this.getClass().getName() + ".controller getParkInfo 종료!");

        return ResponseEntity.ok(
                CommonResponse.of(HttpStatus.OK, HttpStatus.OK.series().name(), rList));
    }

    @GetMapping(value = "map")
    public String map() throws Exception {

        log.info(this.getClass().getName() + ".controller map 시작!");

        log.info(this.getClass().getName() + ".controller map 종료!");

        return "map/map";
    }

}
