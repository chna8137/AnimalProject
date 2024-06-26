package kopo.animal.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kopo.animal.dto.MsgDTO;
import kopo.animal.dto.UserInfoDTO;
import kopo.animal.service.IFileService;
import kopo.animal.service.IS3Service;
import kopo.animal.service.IUserInfoService;
import kopo.animal.util.CmmUtil;
import kopo.animal.util.EncryptUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Optional;

@Slf4j
@RequestMapping(value = "/user")
@RequiredArgsConstructor
@Controller
public class UserInfoController {

    private final IUserInfoService userInfoService;

    /**
     * 회원가입 화면으로 이동
     */
    @GetMapping(value = "userRegForm")
    public String userRegForm() {

        log.info(this.getClass().getName() + ".userRegForm 컨트롤러 시작!");

        return "user/userRegForm";
    }

    /**
     * 회원가입 전 아이디 중복체크하기(Ajax를 통해 입력한 아이디 정보 받음)
     */
    @ResponseBody
    @PostMapping(value = "getUserIdExists")
    public UserInfoDTO getUserIdExists(HttpServletRequest request) throws Exception {

        log.info(this.getClass().getName() + ".getUserIdExists 컨트롤러 시작!");

        String userId = CmmUtil.nvl(request.getParameter("userId")); // 회원 아이디

        log.info("userId : " + userId);

        UserInfoDTO pDTO = new UserInfoDTO();
        pDTO.setUserId(userId);

        // 회원아이디를 통해 중복된 아이디인지 조회
        UserInfoDTO rDTO = Optional.ofNullable(userInfoService.getUserIdExists(pDTO)).orElseGet(UserInfoDTO::new);

        log.info(this.getClass().getName() + ".getUserIdExists 컨트롤러 끝!");

        return rDTO;
    }

    /**
     * 회원가입 전 아이디 중복체크하기(Ajax를 통해 입력한 아이디 정보 받음)
     */
    @ResponseBody
    @PostMapping(value = "getNicknameExists")
    public UserInfoDTO getNicknameExists(HttpServletRequest request) throws Exception {

        log.info(".controller 닉네임 중복체크 실행");

        String nickname = CmmUtil.nvl(request.getParameter("nickname")); // 아이디

        log.info("nickname : " + nickname);

        UserInfoDTO pDTO = new UserInfoDTO();
        pDTO.setNickname(nickname);

        UserInfoDTO rDTO = Optional.ofNullable(userInfoService.getNicknameExists(pDTO)).orElseGet(UserInfoDTO::new);

        log.info(".controller 아이디 중복체크 종료");

        return rDTO;
    }

    /**
     * 회원가입 전 이메일 중복체크하기(Ajax를 통해 입력한 아이디 정보 받음)
     * 유효한 이메일인지 확인하기 위해 입력된 이메일에 인증번호 포함하여 메일 발송
     */
    @ResponseBody
    @PostMapping(value = "getEmailExists")
    public UserInfoDTO getEmailExists(HttpServletRequest request) throws Exception {

        log.info(this.getClass().getName() + ".getEmailExists 컨트롤러 시작!");

        String email = CmmUtil.nvl(request.getParameter("email")); // 회원 아이디

        log.info("email : " + email);

        UserInfoDTO pDTO = new UserInfoDTO();
        pDTO.setEmail(EncryptUtil.encAES128CBC(email));

        // 입력된 이메일이 중복된 이메일인지 조회
        UserInfoDTO rDTO = Optional.ofNullable(userInfoService.getEmailExists(pDTO)).orElseGet(UserInfoDTO::new);

        log.info(this.getClass().getName() + ".getEmailExists 컨트롤러 끝!");

        return rDTO;
    }

    /**
     * 회원가입 로직 처리
     */
    @ResponseBody
    @PostMapping(value = "insertUserInfo")
    public MsgDTO insertUserInfo(HttpServletRequest request) throws Exception {

        log.info(this.getClass().getName() + ".insertUserInfo 컨트롤러 시작");

        String msg = ""; // 회원가입 결과에 대한 메시지를 전달할 변수

        /**
         *  ##################################################################
         *         웹(회원정보 입력화면)에서 받는 정보를 String 변수에 저장 시작!
         *
         *     무조건 웹으로 받은 정보는 DTO에 저장하기 위해 임시로 string 변수에 저장함
         *  ##################################################################
         *  */
        String userId = CmmUtil.nvl(request.getParameter("userId")); // 아이디
        String userName = CmmUtil.nvl(request.getParameter("userName")); // 이름
        String nickname = CmmUtil.nvl(request.getParameter("nickname")); // 이름
        String password = CmmUtil.nvl(request.getParameter("password")); // 비밀번호
        String email = CmmUtil.nvl(request.getParameter("email")); // 이메일
        String addr1 = CmmUtil.nvl(request.getParameter("addr1")); // 주소
        String addr2 = CmmUtil.nvl(request.getParameter("addr2")); // 상세주소

        /**
         *  ##################################################################
         *         웹(회원정보 입력화면)에서 받는 정보를 String 변수에 저장 끝!
         *
         *     무조건 웹으로 받은 정보는 DTO에 저장하기 위해 임시로 string 변수에 저장함
         *  ##################################################################
         *  */

        log.info("userId : " + userId);
        log.info("userName : " + userName);
        log.info("nickname : " + nickname);
        log.info("password : " + password);
        log.info("email : " + email);
        log.info("addr1 : " + addr1);
        log.info("addr2 : " + addr2);

        /**
         *  ##################################################################
         *         웹(회원정보 입력화면)에서 받는 정보를 DTO에 저장하기 시작!
         *  ##################################################################
         *  */

        // 웹(회원정보 입력화면)에서 받는 정보를 저장할 변수를 메모리에 올리기
        UserInfoDTO pDTO = new UserInfoDTO();
        pDTO.setUserId(userId);
        pDTO.setUserName(userName);
        pDTO.setNickname(nickname);
        pDTO.setPassword(EncryptUtil.encHashSHA256(password));
        pDTO.setEmail(EncryptUtil.encAES128CBC(email));
        pDTO.setAddr1(addr1);
        pDTO.setAddr2(addr2);

        // 회원가입 서비스 호출하여 결과 받기
        int res = userInfoService.insertUserInfo(pDTO);

        log.info("회원가입 결과(res) : " + res);

        if (res == 1) {
            msg = "회원가입되었습니다.";

        } else if (res == 2) {
            msg = "이미 가입된 아이디입니다.";

        } else {
            msg = "오류로 인해 회원가입이 실패하였습니다.";

        }

        // 결과 메시지 전달하기
        MsgDTO dto = new MsgDTO();
        dto.setResult(res);
        dto.setMsg(msg);

        log.info(this.getClass().getName() + ".insertUserInfo 컨트롤러 끝!");

        return dto;

    }

    /**
     * 로그인을 위한 입력 화면으로 이동
     */
    @GetMapping(value = "login")
    public String login() {

        log.info(this.getClass().getName() + ".login 시작!");

        log.info(this.getClass().getName() + ".login 끝!");

        return "user/login";
    }

    /**
     * 로그인 처리 및 결과 알려주는 화면으로 이동
     */
    @ResponseBody
    @PostMapping(value = "loginProc")
    public MsgDTO loginProc(HttpServletRequest request, HttpSession session) {

        log.info(this.getClass().getName() + ".loginProc Start!");

        int res = 0; //로그인 처리 결과를 저장할 변수 (로그인 성공 : 1, 아이디, 비밀번호 불일치로인한 실패 : 0, 시스템 에러 : 2)
        String msg = ""; //로그인 결과에 대한 메시지를 전달할 변수
        MsgDTO dto = null; // 결과 메시지 구조
        String url = "/index";

        //웹(회원정보 입력화면)에서 받는 정보를 저장할 변수
        UserInfoDTO pDTO = null;

        try {

            String userId = CmmUtil.nvl(request.getParameter("userId")); //아이디
            String password = CmmUtil.nvl(request.getParameter("password")); //비밀번호

            log.info("userId : " + userId);
            log.info("password : " + password);

            //웹(회원정보 입력화면)에서 받는 정보를 저장할 변수를 메모리에 올리기
            pDTO = new UserInfoDTO();

            pDTO.setUserId(userId);

            //비밀번호는 절대로 복호화되지 않도록 해시 알고리즘으로 암호화함
            pDTO.setPassword(EncryptUtil.encHashSHA256(password));

            // 로그인을 위해 아이디와 비밀번호가 일치하는지 확인하기 위한 userInfoService 호출하기
            UserInfoDTO rDTO = userInfoService.getLogin(pDTO);

            if (CmmUtil.nvl(rDTO.getUserId()).length() > 0) { //로그인 성공

                res = 1;

                log.info("SS_USER_ID : " + userId);
                session.setAttribute("SS_USER_ID", userId);
                log.info("세션에 저장 후 session.getAttribute(\"SS_USER_ID\") : " + session.getAttribute("SS_USER_ID"));
                /*
                 * 세션에 회원아이디 저장하기, 추후 로그인여부를 체크하기 위해 세션에 값이 존재하는지 체크한다.
                 * 일반적으로 세션에 저장되는 키는 대문자로 입력하며, 앞에 SS를 붙인다.
                 *
                 * Session 단어에서 SS를 가져온 것이다.
                 */
                msg = "로그인이 성공했습니다." + rDTO.getUserName() + "님 환영합니다.";
                url = "/index";


//                session.setAttribute("SS_USER_ID", userId);
//                session.setAttribute("SS_USER_NAME", CmmUtil.nvl(rDTO.getUserName()));

            } else {
                msg = "아이디와 비밀번호가 올바르지 않습니다.";

            }

        } catch (Exception e) {
            //저장이 실패되면 사용자에게 보여줄 메시지
            msg = "시스템 문제로 로그인이 실패했습니다.";
            res = 2;
            log.info(e.toString());
            e.printStackTrace();

        } finally {
            // 결과 메시지 전달하기
            dto = new MsgDTO();
            dto.setResult(res);
            dto.setMsg(msg);

            log.info(this.getClass().getName() + ".loginProc End!");
        }

        return dto;
    }

    @GetMapping(value = "loginSuccess")
    public String loginSuccess() {

        log.info(this.getClass().getName() + ".loginSuccess 시작!");

        log.info(this.getClass().getName() + ".loginSuccess 끝!");

        return "user/loginSuccess";
    }


    /**
     * 아이디 찾기 화면
     */
    @GetMapping(value = "findId")
    public String findId() {

        log.info(this.getClass().getName() + ".searchUserId 서비스 시작!");

        log.info(this.getClass().getName() + ".searchUserId 서비스 끝!");

        return "user/findId";
    }

    /**
     * 아이디 찾기 로직 수행
     */
    @ResponseBody
    @PostMapping(value = "searchUserIdProc")
    public MsgDTO searchUserIdProc(HttpServletRequest request, ModelMap model, HttpSession session) throws Exception {

        log.info(this.getClass().getName() + ".searchUserIdProc 서비스 시작!");

        int res = 0; //로그인 처리 결과를 저장할 변수 (로그인 성공 : 1, 아이디, 비밀번호 불일치로인한 실패 : 0, 시스템 에러 : 2)
        String msg = ""; //로그인 결과에 대한 메시지를 전달할 변수
        MsgDTO dto = null; // 결과 메시지 구조
        String url = "/index";

        try {

            String userName = CmmUtil.nvl(request.getParameter("userName"));
            String email = CmmUtil.nvl(request.getParameter("email"));

            log.info("userName : " + userName);
            log.info("email : " + email);

            UserInfoDTO pDTO = new UserInfoDTO();
            pDTO.setUserName(userName);
            pDTO.setEmail(EncryptUtil.encAES128CBC(email));

            UserInfoDTO rDTO = Optional.ofNullable(userInfoService.searchUserIdOrPasswordProc(pDTO))
                    .orElseGet(UserInfoDTO::new);

//            model.addAttribute("rDTO", rDTO);

            if ((rDTO.getUserName() == null || !rDTO.getUserName().equals(userName)) &&
                    (rDTO.getEmail() == null || !rDTO.getEmail().equals(email))) {

                msg = "해당하는 사용자를 찾을 수 없습니다.";

            } else {
                res = 1;

                session.setAttribute("SS_USER_NAME", userName);
                msg = rDTO.getUserName() + "님의 아이디는   " + rDTO.getUserId() + "입니다.";
                url = "/index";
            }
        } catch (Exception e) {
            //저장이 실패되면 사용자에게 보여줄 메시지
            msg = "시스템 문제로 로그인이 실패했습니다.";
            res = 2;
            log.info(e.toString());
            e.printStackTrace();

        } finally {
            // 결과 메시지 전달하기
            dto = new MsgDTO();
            dto.setResult(res);
            dto.setMsg(msg);
        }


        log.info(this.getClass().getName() + ".searchUserIdProc 서비스 끝!");

        return dto;
    }

    /**
     * 비밀번호 찾기 화면
     */
    @GetMapping(value = "findPassword")
    public String findPassword(HttpSession session) {
        log.info(this.getClass().getName() + ".searchPassword 컨트롤러 시작!");

        // 강제 URL 입력 등 오는 경우가 있어 세션 삭제
        // 비밀번호 재생성하는 화면은 보안을 위해 생성한 NEW_PASSWORD 세션 삭제
        session.setAttribute("NEW_PASSWORD", "");
        session.removeAttribute("NEW_PASSWORD");

        log.info(this.getClass().getName() + ".searchPassword 컨트롤러 끝!");

        return "user/findPassword";

    }

    /**
     * 비밀번호 찾기 로직 수행
     * <p>
     * 아이디, 이름, 이메일 일치하면, 비밀번호 재발급 화면 이동
     */
    @ResponseBody
    @PostMapping(value = "searchPasswordProc")
    public MsgDTO searchPasswordProc(HttpServletRequest request, HttpSession session) throws Exception {
        log.info(this.getClass().getName() + ".searchPasswordProc 컨트롤러 시작!");

        int res = 0; // 처리 결과를 저장할 변수 (성공 : 1, 실패 : 0, 시스템 에러 : 2)
        String msg = ""; // 결과 메시지를 전달할 변수
        MsgDTO dto = new MsgDTO(); // 결과 메시지 구조
        String url = "/index";

        try {
            String userId = CmmUtil.nvl(request.getParameter("userId")); // 아이디
            String userName = CmmUtil.nvl(request.getParameter("userName")); // 이름
            String email = CmmUtil.nvl(request.getParameter("email")); // 이메일

            log.info("userId : " + userId);
            log.info("userName : " + userName);
            log.info("email : " + email);

            if (userId.isEmpty() || userName.isEmpty() || email.isEmpty()) {
                msg = "아이디, 이름 및 이메일을 모두 입력해주세요.";
                res = 0;
            } else {
                UserInfoDTO pDTO = new UserInfoDTO();
                pDTO.setUserId(userId);
                pDTO.setUserName(userName);
                pDTO.setEmail(EncryptUtil.encAES128CBC(email));

                // 비밀번호 찾기 가능한지 확인하기
                UserInfoDTO rDTO = Optional.ofNullable(userInfoService.searchUserIdOrPasswordProc(pDTO)).orElseGet(UserInfoDTO::new);

                if ((rDTO.getUserName() == null || !rDTO.getUserName().equals(userName)) &&
                        (rDTO.getEmail() == null || !rDTO.getEmail().equals(email)) &&
                        (rDTO.getUserId() == null || !rDTO.getUserId().equals(userId))) {
                    msg = "해당하는 사용자를 찾을 수 없습니다.";
                    res = 0;
                } else {
                    res = 1;
                    session.setAttribute("NEW_PASSWORD", userId);
                    msg = "비밀번호 재설정 화면으로 이동합니다.";
                }
            }
        } catch (Exception e) {
            msg = "시스템 문제로 비밀번호 찾기가 실패했습니다.";
            res = 2;
            log.error(e.toString(), e); // 로그 레벨을 info에서 error로 변경
        } finally {
            dto.setResult(res);
            dto.setMsg(msg);
        }

        log.info(this.getClass().getName() + ".searchPasswordProc 컨트롤러 끝!");

        return dto;
    }
    @GetMapping(value = "newPassword")
    public String newPassword(HttpSession session) {
        log.info("새로운 비밀번호 설정 페이지 시작");

        // NEW_PASSWORD 세션 확인
        String newPasswordSession = CmmUtil.nvl((String) session.getAttribute("NEW_PASSWORD"));
        if (newPasswordSession.isEmpty()) {
            log.info("비정상 접근 시도");
            return "redirect:/user/findPassword"; // 비정상 접근 시도 시 findPassword 페이지로 리다이렉트
        }

        log.info("새로운 비밀번호 설정 페이지 종료");

        return "user/newPasswordProc";
    }

    /**
     * 비밀번호 찾기 로직 수행
     * <p>
     * 아이디, 이름, 이메일 일치하면, 비밀번호 재발급 화면 이동
     */
    @ResponseBody
    @PostMapping(value = "newPasswordProc")
    public MsgDTO newPasswordProc(HttpServletRequest request, HttpSession session) throws Exception {

        log.info(this.getClass().getName() + ".controller 비밀번호 변경 시작!");

        int res = 0; // 처리 결과를 저장할 변수 (성공 : 1, 실패 : 0, 시스템 에러 : 2)
        String msg = ""; // 결과 메시지를 전달할 변수
        MsgDTO dto = new MsgDTO(); // 결과 메시지 구조

        try {
            // 정상적인 접근인지 체크
            String newPassword = CmmUtil.nvl((String) session.getAttribute("NEW_PASSWORD"));

            log.info("newPassword : " + newPassword);

            if (newPassword.length() > 0) { // 정상 접근
                String password = CmmUtil.nvl(request.getParameter("password")); // 신규 비밀번호

                log.info("userPassword : " + password);

                UserInfoDTO pDTO = new UserInfoDTO();
                pDTO.setUserId(newPassword);
                pDTO.setPassword(EncryptUtil.encHashSHA256(password));

                // 비밀번호 업데이트
                userInfoService.newPasswordProc(pDTO);

                // 비밀번호 재생성하는 화면은 보안을 위해 생성한 NEW_PASSWORD 세션 삭제
                session.setAttribute("NEW_PASSWORD", "");
                session.removeAttribute("NEW_PASSWORD");

                msg = "비밀번호가 재설정되었습니다.";
                res = 1; // 성공

            } else { // 비정상 접근
                msg = "비정상 접근입니다.";
                res = 0; // 실패
            }

        } catch (Exception e) {
            msg = "시스템 문제로 비밀번호 변경이 실패했습니다.";
            res = 2; // 시스템 에러
            log.error(e.toString(), e);

        } finally {
            dto.setResult(res);
            dto.setMsg(msg);
        }

        log.info(this.getClass().getName() + ".controller 비밀번호 변경 종료!");

        return dto;
    }

    /**
     * 로그아웃 처리
     */
    @ResponseBody
    @PostMapping(value = "logout")
    public MsgDTO logout(HttpSession session) {

        log.info(this.getClass().getName() + "로그아웃 시작!");

        session.setAttribute("SS_USER_ID", ""); // 세션 값 빈값으로 변경
        session.removeAttribute("SS_USER_ID"); // 세션 값 지우기

        // 결과 메시지 전달하기
        MsgDTO dto = new MsgDTO();
        dto.setResult(1);
        dto.setMsg("로그아웃하였습니다.");

        log.info(this.getClass().getName() + "로그아웃 끝!");

        return dto;
    }
}
