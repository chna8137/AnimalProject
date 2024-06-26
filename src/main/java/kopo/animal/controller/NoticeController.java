package kopo.animal.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kopo.animal.dto.MsgDTO;
import kopo.animal.dto.NoticeDTO;
import kopo.animal.service.INoticeService;
import kopo.animal.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequestMapping(value = "/notice")
@RequiredArgsConstructor
@Controller
public class NoticeController {

    private final INoticeService noticeService;

    /**
     * 게시판 리스트 보여주기
     */
    @GetMapping(value = "noticeList")
    public String noticeList(HttpSession session, ModelMap model, HttpServletRequest request,
                             @RequestParam(defaultValue = "1") int page,
                             @RequestParam(defaultValue = "9") int pageSize) throws Exception {

        log.info(this.getClass().getName() + " 공지사항 리스트 컨트롤러 시작!");

        String nSeq = CmmUtil.nvl(request.getParameter("nSeq"));
        String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));

        log.info("nSeq : " + nSeq);
        log.info("userId : " + userId);

        NoticeDTO pDTO = new NoticeDTO();
        pDTO.setNoticeSeq(nSeq);
        pDTO.setUserId(userId);
        pDTO.setPage(page);
        pDTO.setPageSize(pageSize);

        NoticeDTO rDTO = Optional.ofNullable(noticeService.getNoticeInfo(pDTO, true)).orElseGet(NoticeDTO::new);
        model.addAttribute("rDTO", rDTO);

        int totalCount = noticeService.getTotalCount();
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);

        log.info("totalCount : " + totalCount);
        log.info("totalPages : " + totalPages);

        int startRow = (page - 1) * pageSize + 1;
        int endRow = Math.min(page * pageSize, totalCount);
        List<NoticeDTO> pagedNoticeList = noticeService.getPagedNoticeList(startRow, endRow);

        log.info("startRow : " + startRow);
        log.info("endRow : " + endRow);

        model.addAttribute("rList", pagedNoticeList);
        model.addAttribute("page", page);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalPages", totalPages);

        // 페이지 네이션을 10개씩 보여주기 위한 계산
        int pageGroupSize = 10;
        int currentPageGroup = (int) Math.ceil((double) page / pageGroupSize);
        int startPage = (currentPageGroup - 1) * pageGroupSize + 1;
        int endPage = Math.min(startPage + pageGroupSize - 1, totalPages);

        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        log.info("startPage : " + startPage);
        log.info("endPage : " + endPage);
        log.info("pagedNoticeList : " + pagedNoticeList);

        log.info(this.getClass().getName() + ".noticeList 컨트롤러 끝!");

        return "notice/noticeList";
    }


    @GetMapping(value = "noticeReg")
    public String noticeReg(HttpSession session, ModelMap model) {

        log.info(this.getClass().getName() + ".noticeReg 컨트롤러 시작!");

        String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));

        if (userId.length() < 1) {
            String msg = "로그인 정보가 없습니다. \n 로그인 후 이용해 주세요.";
            String url = "/user/login";

            model.addAttribute("msg", msg);
            model.addAttribute("url", url);

            return "redirect";
        }

        log.info(this.getClass().getName() + ".noticeReg 컨트롤러 끝!");

        return "notice/noticeReg";
    }

    /**
     * 게시판 글 등록
     */
    @ResponseBody
    @PostMapping(value = "noticeInsert")
    public MsgDTO noticeInsert(HttpServletRequest request, HttpSession session) throws Exception {

        log.info(this.getClass().getName() + ".noticeInsert 컨트롤러 시작!");

        String msg = ""; // 메시지 내용
        MsgDTO dto = null; // 결과 메시지 구종

        try {
            // 로그인된 사용자 아이디 가져오기
            String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));
            String title = CmmUtil.nvl(request.getParameter("title")); // 제목
            String noticeYn = CmmUtil.nvl(request.getParameter("noticeYn")); // 공지글 여부
            String contents = CmmUtil.nvl(request.getParameter("contents")); // 내용

            log.info("SS_USER_ID : " + userId);
            log.info("title : " + title);
            log.info("noticeYn : " + noticeYn);
            log.info("contents : " + contents);

            // 데이터 저장하기 위해 DTO에 저장
            NoticeDTO pDTO = new NoticeDTO();
            pDTO.setUserId(userId);
            pDTO.setTitle(title);
            pDTO.setNoticeYn(noticeYn);
            pDTO.setContents(contents);

            /**
             * 게시글 등록하기 위한 비즈니스 로직을 호출
             * */
            noticeService.insertNoticeInfo(pDTO);

            // 저장이 완료되면 사용자에게 보여줄 메시지
            msg = "등록되었습니다.";

        } catch (Exception e) {

            // 저장이 실패되면 사용자에게 보여줄 메시지
            msg = "실패하였습니다. : " + e.getMessage();
            log.info(e.toString());
            e.printStackTrace();

        } finally {
            // 결과 메시지 전달하기
            dto = new MsgDTO();
            dto.setMsg(msg);

            log.info(this.getClass().getName() + ".noticeInsert 컨트롤러 끝!");
        }

        return dto;
    }

    /**
     * 게시판 상세보기
     */
    @GetMapping(value = "noticeInfo")
    public String noticeInfo(HttpServletRequest request, ModelMap model, HttpSession session) throws Exception {

        log.info(this.getClass().getName() + ".noticeInfo 컨트롤러 시작!");

        String nSeq = CmmUtil.nvl(request.getParameter("nSeq")); // 공지글 번호(PK)
        String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID")); // 세션 값으로 로그인 된 userId 정보 가져오기

        log.info("nSeq : " + nSeq);
        log.info("userId : " + userId);

        /**
         * 값 전달은 반드시 DTO객체를 이용해서 처리함 전달 받은 값을 DTO 객체에 넣는다.
         * */
        NoticeDTO pDTO = new NoticeDTO();
        pDTO.setNoticeSeq(nSeq);
        pDTO.setUserId(userId);

        // 공지사항 상세정보 가져오기
        NoticeDTO rDTO = Optional.ofNullable(noticeService.getNoticeInfo(pDTO, true)).orElseGet(NoticeDTO::new);

        // 조회된 리스트 결과값 넣어주기
        model.addAttribute("rDTO", rDTO);

        log.info(this.getClass().getName() + ".noticeInfo 컨트롤러 끝!");

        return "notice/noticeInfo";
    }

    /**
     * 게시판 수정 보기
     */
    @GetMapping(value = "noticeEditInfo")
    public String noticeEditInfo(HttpServletRequest request, ModelMap model, HttpSession session) throws Exception {

        log.info(this.getClass().getName() + ".noticeEditInfo 컨트롤러 시작!");

        String nSeq = CmmUtil.nvl(request.getParameter("nSeq"));
        String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));

        if (userId.length() < 1) {
            String msg = "로그인 정보가 없습니다. \n 로그인 후 이용해 주세요.";
            String url = "/user/login";

            model.addAttribute("msg", msg);
            model.addAttribute("url", url);

            return "redirect";
        }

        log.info("nSeq : " + nSeq);
        log.info("userId : " + userId);

        NoticeDTO pDTO = new NoticeDTO();
        pDTO.setNoticeSeq(nSeq);
        pDTO.setUserId(userId);

        NoticeDTO rDTO = Optional.ofNullable(noticeService.getNoticeInfo(pDTO, false)).orElseGet(NoticeDTO::new);

        // 조회되는 리스트 결과값 넣어주기
        model.addAttribute("rDTO", rDTO);

        log.info(this.getClass().getName() + ".noticeEditInfo 컨트롤러 종료");

        return "notice/noticeEditInfo";
    }

    /**
     * 게시판 글 수정
     */
    @ResponseBody
    @PostMapping(value = "noticeUpdate")
    public MsgDTO noticeUpdate(HttpServletRequest request, HttpSession session) throws Exception {

        log.info(this.getClass().getName() + ".noticeUpdate 컨트롤러 시작!");

        String msg = ""; // 메시지 내용
        MsgDTO dto = null; // 결과 메시지 구조

        try {

            String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));
            String nSeq = CmmUtil.nvl(request.getParameter("nSeq"));
            String title = CmmUtil.nvl(request.getParameter("title"));
            String noticeYn = CmmUtil.nvl(request.getParameter("noticeYn"));
            String contents = CmmUtil.nvl(request.getParameter("contents"));

            log.info("userId : " + userId);
            log.info("nSeq : " + nSeq);
            log.info("title : " + title);
            log.info("noticeYn : " + noticeYn);
            log.info("contents : " + contents);

            NoticeDTO pDTO = new NoticeDTO();
            pDTO.setUserId(userId);
            pDTO.setNoticeSeq(nSeq);
            pDTO.setTitle(title);
            pDTO.setNoticeYn(noticeYn);
            pDTO.setContents(contents);

            // 게시글 수정하기 DB
            noticeService.updateNoticeInfo(pDTO);

            msg = "수정되었습니다.";

        } catch (Exception e) {

            msg = "실패하였습니다. : " + e.getMessage();
            log.info(e.toString());
            e.printStackTrace();

        } finally {

            // 결과 메시지 반환하기
            dto = new MsgDTO();
            dto.setMsg(msg);

            log.info(this.getClass().getName() + ".noticeUpdate 컨트롤러 종료");
        }
        return dto;
    }

    /**
     * 게시판 글 삭제
     * */
    @ResponseBody
    @PostMapping(value = "noticeDelete")
    public MsgDTO noticeDelete(HttpServletRequest request, HttpSession session) {

        log.info(this.getClass().getName() + ".noticeDelete 컨트롤러 시작");

        String msg = "";
        MsgDTO dto = null;

        try {

            String nSeq = CmmUtil.nvl(request.getParameter("nSeq"));
            String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));

            log.info("nSeq : " + nSeq);
            log.info("userId : " + userId);

            NoticeDTO pDTO = new NoticeDTO();
            pDTO.setNoticeSeq(nSeq);
            pDTO.setUserId(userId);

            noticeService.deleteNoticeInfo(pDTO);

            msg = "삭제되었습니다.";
        } catch (Exception e) {

            msg = "실패했습니다. : " + e.getMessage();
            log.info(e.toString());
            e.printStackTrace();

        } finally {

            dto = new MsgDTO();
            dto.setMsg(msg);

            log.info(this.getClass().getName() + ".noticeDelete 컨트롤러 종료");
        }

        return dto;
    }
}
