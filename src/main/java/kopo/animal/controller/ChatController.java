package kopo.animal.controller;

import kopo.animal.chat.ChatHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Set;

@Slf4j
@Controller
@RequestMapping(value = "/chat")
public class ChatController {

    /**
     * 채팅창 입장 전
     * */
    @GetMapping(value = "intro")
    public String intro() {

        log.info(this.getClass().getName() + "인트로 시작!");

        log.info(this.getClass().getName() + "인트로 종료!");

        return "/chat/intro";
    }

    /**
     * 채팅방 접속
     * */
    @PostMapping(value = "chatroom")
    public String chatroom() {

        log.info(this.getClass().getName() + "채팅방 화면 시작!");

        log.info(this.getClass().getName() + "채팅방 화면 종료!");

        return "/chat/chatroom";
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
