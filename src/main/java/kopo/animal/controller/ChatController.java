package kopo.animal.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kopo.animal.chat.ChatHandler;
import kopo.animal.dto.UserInfoDTO;
import kopo.animal.persistance.mapper.IUserInfoMapper;
import kopo.animal.service.IUserInfoService;
import kopo.animal.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping(value = "/chat")
public class ChatController {

    private final IUserInfoService userInfoService;

    private final IUserInfoMapper userInfoMapper;

    /**
     * 채팅창 입장 전
     * */
    @GetMapping(value = "intro")
    public String intro(HttpSession session, ModelMap model) {

        log.info(this.getClass().getName() + "인트로 시작!");

        String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));

        if (userId.length() < 1) {
            String msg = "로그인 정보가 없습니다. \n 로그인후 이용해 주세요.";
            String url = "/user/login";

            model.addAttribute("msg", msg);
            model.addAttribute("url", url);

            return "redirect";
        }

        log.info(this.getClass().getName() + "인트로 종료!");

        return "chat/intro";
    }

    /**
     * 채팅방 접속
     * */
    @PostMapping(value = "chatroom")
    public String chatroom(HttpServletRequest request, HttpSession session, ModelMap model) throws Exception {

        log.info(this.getClass().getName() + "채팅방 화면 시작!");

        String roomName = CmmUtil.nvl(request.getParameter("roomName")); // 채팅방 명
        String nickname = CmmUtil.nvl(request.getParameter("nickname")); // 채팅방 입장전 입력한 별명
        String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));

        log.info("roomName : " + roomName);
        log.info("nickname : " + nickname);
        log.info("userId : " + userId);

        UserInfoDTO pDTO = new UserInfoDTO();
        pDTO.setNickname(nickname);
        pDTO.setUserId(userId);

        UserInfoDTO rDTO = Optional.ofNullable(userInfoService.getNickname(pDTO)).orElseGet(UserInfoDTO::new);

        model.addAttribute("rDTO", rDTO);
        model.addAttribute("nickname", nickname);
        model.addAttribute("roomName", roomName);

        log.info(this.getClass().getName() + "채팅방 화면 종료!");

        return "chat/chatroom";
    }

    /**
     * 채팅방 목록
     * */
    @RequestMapping(value = "roomList")
    @ResponseBody
    public Set<String> roomList() {

        log.info(this.getClass().getName() + "채팅방 리스트 시작!");

        log.info(this.getClass().getName() + "채팅방 리스트 종료!");

        return ChatHandler.roomInfo.keySet();
    }
}
