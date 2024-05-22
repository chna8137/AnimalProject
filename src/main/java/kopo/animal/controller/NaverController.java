package kopo.animal.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kopo.animal.dto.CalendarDTO;
import kopo.animal.dto.NaverDTO;
import kopo.animal.dto.TokenDTO;
import kopo.animal.dto.UserInfoDTO;
import kopo.animal.service.INaverService;
import kopo.animal.service.IUserInfoService;
import kopo.animal.util.EncryptUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Controller
public class NaverController {

    private final INaverService naverService;
    private final IUserInfoService userInfoService;


    /* 네이버 로그인 엑세스 토큰 받기 */

    @GetMapping(value = "/auth/naver/callback")
    public String naverCallback(String code, HttpSession session, ModelMap modelMap) throws Exception {

        log.info(".controller 네이버 회원가입 및 로그인 실행");
        log.info("콜백 컨트롤러 들어와서 매개변수로 받은 code 확인! : " + code);

        String msg = "";
        String url = "";
        int res; // 회원 가입 결과 /// 1 성공, 2 이미 가입

        TokenDTO tokenDTO = naverService.getAccessToken(code);

        log.info("네이버 엑세스 토큰 : " + tokenDTO.getAccess_token());

        NaverDTO naverDTO = naverService.getNaverUserInfo(tokenDTO);

        String userId = "naver_" + naverDTO.getResponse().getId();
        String userName = naverDTO.getResponse().getName();

        log.info("네이버 아이디 : " + naverDTO.getResponse().getId());

        UserInfoDTO pDTO = new UserInfoDTO();
        pDTO.setUserId(userId);


        UserInfoDTO rDTO = naverService.getUserInfoById(pDTO);

        // 첫 로그인시 회원가입 로직 실행
        if (rDTO == null) {

            String email = naverDTO.getResponse().getEmail();
            String gender = naverDTO.getResponse().getGender();
            String addr1 = "a1";
            String addr2 = "a2";

            if (Objects.equals(gender, "F")) {
                gender = "female";

            } else if (Objects.equals(gender, "M")) {
                gender = "male";

            }

            log.info("네이버 아이디(문자) : " + userId);
            log.info("네이버 이름 : " + userName);
            log.info("네이버 이메일 : " + email);
            log.info("네이버 성별 : " + gender);

            pDTO.setUserId(userId);
            pDTO.setUserName(userName);
            pDTO.setPassword(EncryptUtil.encHashSHA256(userId)); // 오 유저아이디를 해시로 암호화 해서 비밀번호로 저장하는구나
            pDTO.setEmail(EncryptUtil.encAES128CBC(email));
            pDTO.setAddr1(addr1);
            pDTO.setAddr2(addr2);

            res = userInfoService.insertUserInfo(pDTO);

            if (res == 1) {
                log.info("회원가입 성공");

                session.setAttribute("SS_USER_ID", userId);

                msg = "회원가입 및 로그인 성공! " + userName + "님 환영합니다.";
                url = "/index";

            } else {
                log.info("회원가입 실패");

            }

        } else {
            log.info("계정 보유로 로그인 실행");

            session.setAttribute("SS_USER_ID", userId);

            msg = "로그인 성공! " + userName + "님 환영합니다.";
            url = "/index";

        }

        modelMap.addAttribute("msg", msg);
        modelMap.addAttribute("url", url);

        log.info(".controller 네이버 회원가입 및 로그인 종료");

        return "/redirect";
    }

}
