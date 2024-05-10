package kopo.animal.controller;

import jakarta.servlet.http.HttpServletRequest;
import kopo.animal.controller.response.CommonResponse;
import kopo.animal.dto.CultureDTO;
import kopo.animal.dto.HospitalDTO;
import kopo.animal.dto.ParkDTO;
import kopo.animal.service.ICultureService;
import kopo.animal.service.IHospitalService;
import kopo.animal.service.IParkService;
import kopo.animal.util.CmmUtil;
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
    private final ICultureService cultureService;
    private final IHospitalService hospitalService;


    /**
     * 수집된 공원 리스트 가져오기
     */
    @PostMapping(value = "/v1/getParkInfo")
    public ResponseEntity getParkInfo(HttpServletRequest request, @RequestParam(defaultValue = "1") int page,
                                      @RequestParam(defaultValue = "20") int itemsPerPage) throws Exception {

        log.info(this.getClass().getName() + ".controller getParkInfo 시작!");

        double coursSpotLa = Double.parseDouble(CmmUtil.nvl(request.getParameter("coursSpotLa")));
        double coursSpotLo = Double.parseDouble(CmmUtil.nvl(request.getParameter("coursSpotLo")));

        ParkDTO pDTO = ParkDTO.builder().coursSpotLa(String.valueOf(coursSpotLa)).coursSpotLo(String.valueOf(coursSpotLo)).build();

        List<ParkDTO> rList = Optional.ofNullable(parkService.getParkInfo(page, itemsPerPage, pDTO))
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

    /**
     * 수집된 공원 리스트 가져오기
     */
    @PostMapping(value = "/v1/getCultureInfo")
    public ResponseEntity getCultureInfo(HttpServletRequest request, @RequestParam(defaultValue = "1") int page,
                                      @RequestParam(defaultValue = "20") int itemsPerPage) throws Exception {

        log.info(this.getClass().getName() + ".controller getCultureInfo 시작!");

        double lcLa = Double.parseDouble(CmmUtil.nvl(request.getParameter("lcLa")));
        double lcLo = Double.parseDouble(CmmUtil.nvl(request.getParameter("lcLo")));

        log.info("lcla : " + lcLa);
        log.info("lclo : " + lcLo);

        CultureDTO pDTO = CultureDTO.builder().lcLa(String.valueOf(lcLa)).lcLo(String.valueOf(lcLo)).build();

        List<CultureDTO> rList = Optional.ofNullable(cultureService.getCultureInfo(page, itemsPerPage, pDTO))
                .orElseGet(ArrayList::new);

        log.info(this.getClass().getName() + ".controller getCultureInfo 종료!");

        return ResponseEntity.ok(
                CommonResponse.of(HttpStatus.OK, HttpStatus.OK.series().name(), rList));
    }

    /**
     * 수집된 공원 리스트 가져오기
     */
    @PostMapping(value = "/v1/getHospitalInfo")
    public ResponseEntity getHospitalInfo(HttpServletRequest request, @RequestParam(defaultValue = "1") int page,
                                         @RequestParam(defaultValue = "20") int itemsPerPage) throws Exception {

        log.info(this.getClass().getName() + ".controller getHospitalInfo 시작!");

        double fcltyLa = Double.parseDouble(CmmUtil.nvl(request.getParameter("fcltyLa")));
        double fcltyLo = Double.parseDouble(CmmUtil.nvl(request.getParameter("fcltyLo")));

        log.info("fcltyLa : " + fcltyLa);
        log.info("fcltyLo : " + fcltyLo);

        HospitalDTO pDTO = HospitalDTO.builder().fcltyLa(String.valueOf(fcltyLa)).fcltyLo(String.valueOf(fcltyLo)).build();

        List<HospitalDTO> rList = Optional.ofNullable(hospitalService.getHospitalInfo(page, itemsPerPage, pDTO))
                .orElseGet(ArrayList::new);

        log.info(this.getClass().getName() + ".controller getHospitalInfo 종료!");

        return ResponseEntity.ok(
                CommonResponse.of(HttpStatus.OK, HttpStatus.OK.series().name(), rList));
    }


}
