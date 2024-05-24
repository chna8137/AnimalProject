package kopo.animal.controller;

import jakarta.servlet.http.HttpServletRequest;
import kopo.animal.controller.response.CommonResponse;
import kopo.animal.dto.AbandonedDTO;
import kopo.animal.dto.MsgDTO;
import kopo.animal.service.IAbandonedService;
import kopo.animal.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
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
     */
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
     */
    @GetMapping(value = "abandonedList")
    public String abandonedList(ModelMap model, @RequestParam(defaultValue = "1") int page) throws Exception {

        log.info(this.getClass().getName() + ".controller 유기동물 정보 가져오기 시작!");

        List<AbandonedDTO> rList = Optional.ofNullable(abandonedService.getAbandonedList())
                .orElseGet(ArrayList::new);

        /**페이징 시작 부분*/

        // 페이지당 보여줄 아이템 개수 정의
        // 페이지당 보여줄 아이템 개수 정의
        int itemsPerPage = 9;

// 페이지네이션을 위해 전체 아이템 개수 구하기
        int totalItems = rList.size();

// 전체 페이지 개수 계산
        int totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);

// 현재 페이지에 해당하는 아이템들만 선택하여 rList에 할당
        int fromIndex = (page - 1) * itemsPerPage;
        int toIndex = Math.min(fromIndex + itemsPerPage, totalItems);
        rList = rList.subList(fromIndex, toIndex);

        int pageGroupSize = 10;
        int currentGroup = (int) Math.ceil((double) page / pageGroupSize);
        int startPage = (currentGroup - 1) * pageGroupSize + 1;
        int endPage = Math.min(currentGroup * pageGroupSize, totalPages);

        model.addAttribute("rList", rList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        log.info(this.getClass().getName() + ".페이지 번호 : " + page);

        /**페이징 끝부분*/

        model.addAttribute("rList", rList);

        log.info("rList : " + rList);


        log.info(this.getClass().getName() + ".controller 유기동물 정보 가져오기 끝!");

        return "abandoned/abandonedList";
    }

    // 유기견 상세보기
    // 2024.05.21 ( 로직 변경 { 컬렉션 이름 Controller로 옮기고 idntfyNo를 직접 가져오도록 변경 )
    @GetMapping(value = "abandonedInfo")
    public String abandonedInfo(ModelMap model, @RequestParam("idntfyNo") String idntfyNo) throws Exception {
        log.info(this.getClass().getName() + ".controller 유기동물 상세보기 시작!");

        log.info("idntfyNo : " + idntfyNo);

        // MongoDB에 저장된 컬렉션 이름
        String colNm = "ANIMAL_ABANDONED";

        // 서비스 호출을 통해 rDTO를 가져옴
        AbandonedDTO rDTO = Optional.ofNullable(abandonedService.getAbandoned(colNm, idntfyNo))
                .orElseGet(() -> AbandonedDTO.builder().build());

        model.addAttribute("rDTO", rDTO);

        log.info(this.getClass().getName() + ".controller 유기동물 상세보기 종료");

        return "abandoned/abandonedInfo";
    }


}
