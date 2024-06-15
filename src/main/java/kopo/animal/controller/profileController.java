package kopo.animal.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kopo.animal.dto.FileDTO;
import kopo.animal.dto.MsgDTO;
import kopo.animal.dto.UserInfoDTO;
import kopo.animal.service.IFileService;
import kopo.animal.service.IProfileService;
import kopo.animal.service.IS3Service;
import kopo.animal.service.IUserInfoService;
import kopo.animal.util.CmmUtil;
import kopo.animal.util.EncryptUtil;
import kopo.animal.util.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequestMapping(value = "/user")
@RequiredArgsConstructor
@Controller
public class profileController {

    private final IProfileService profileService;

    private final IUserInfoService userInfoService;

    private final IFileService fileService;

    private final IS3Service s3Service;

    @GetMapping(value = "profile")
    public String profile(ModelMap model, HttpSession session,
                          @RequestParam(defaultValue = "1") int page) throws Exception {

        log.info(this.getClass().getName() + " 마이페이지 조회 컨트롤러 시작!");

        String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));

        // 네이버 로그인 사용자 접근 차단
        if (userId.startsWith("naver_")) {
            return "redirect:/index";
        }

        log.info("SS_USER_ID : " + userId);

        UserInfoDTO pDTO = new UserInfoDTO();
        pDTO.setUserId(userId);

        UserInfoDTO rDTO = Optional.ofNullable(profileService.getProfile(pDTO)).orElseGet(UserInfoDTO::new);

        log.info("pDTO : " + pDTO);

        log.info("복호화 전 Email : " + rDTO.getEmail());

        rDTO.setEmail(EncryptUtil.encAES128CBC(rDTO.getEmail()));

        log.info("복호화 후 Email : " + rDTO.getEmail());

        model.addAttribute("rDTO", rDTO);

        log.info("회원정보 조회 rDTO.toString() : " + rDTO.toString());

        log.info(this.getClass().getName() + " 마이페이지 조회 컨트롤러 종료!");

        return "user/profile";
    }

    @GetMapping(value = "/profileModify")
    public String profileModify(HttpSession session, ModelMap model) throws Exception {

        log.info(this.getClass().getName() + " 마이페이지 수정페이지 보여주기 시작!");

        String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));

        // 네이버 로그인 사용자 접근 차단
        if (userId.startsWith("naver_")) {
            return "redirect:/index";
        }

        log.info("세션에서 받아온 userId : " + userId);

        UserInfoDTO pDTO = new UserInfoDTO();
        pDTO.setUserId(userId);

        log.info("pDTO : " + pDTO.toString());

        UserInfoDTO rDTO = Optional.ofNullable(profileService.getProfile(pDTO)).orElseGet(UserInfoDTO::new);

        log.info("DB에서 가져와 복호화 하기 전 이메일 : " + rDTO.getEmail());

        rDTO.setEmail(EncryptUtil.decAES128CBC(rDTO.getEmail()));

        log.info("DB에서 가져와 복호화 하고나서 이메일 : " + rDTO.getEmail());

        model.addAttribute("rDTO", rDTO);

        log.info("회원정보 조회 rDTO.toString() : " + rDTO.toString());

        log.info(this.getClass().getName() + " 마이페이지 수정페이지 보여주기 종료!");

        return "user/profileModify";
    }

    @ResponseBody
    @PostMapping(value = "/profileModifyProc")
    public MsgDTO updateProc(HttpSession session, HttpServletRequest request,
                             @RequestParam(value = "file", required = false) List<MultipartFile> files) {

        log.info(this.getClass().getName() + " 마이페이지 수정 시작!");

        String msg = "";
        int result = 0;
        MsgDTO rDTO = null;

        try {

            String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));

            // 네이버 로그인 사용자 접근 차단
            if (userId.startsWith("naver_")) {
                rDTO = new MsgDTO();
                rDTO.setMsg("네이버 로그인 사용자는 마이페이지를 수정할 수 없습니다.");
                rDTO.setResult(0);
                return rDTO;
            }

            String userName = CmmUtil.nvl(request.getParameter("userName"));
            String nickname = CmmUtil.nvl(request.getParameter("nickname"));
            String addr1 = CmmUtil.nvl(request.getParameter("addr1"));
            String addr2 = CmmUtil.nvl(request.getParameter("addr2"));

            log.info("userId : " + userId);
            log.info("userName : " + userName);
            log.info("nickname : " + nickname);
            log.info("addr1 : " + addr1);
            log.info("addr2 : " + addr2);

            UserInfoDTO pDTO = new UserInfoDTO();
            pDTO.setUserId(userId);
            pDTO.setUserName(userName);
            pDTO.setNickname(nickname);
            pDTO.setAddr1(addr1);
            pDTO.setAddr2(addr2);

            profileService.updateProfile(pDTO);

            msg = "수정되었습니다.";
            result = 1;

            if (files != null) {

                // 기존에 있던 파일들 삭제
                fileService.deleteFile(pDTO);

                String saveFilePath = FileUtil.mkdirForData();      // 웹서버에 저장할 파일 경로 생성

                log.info("userId : " + userId);

                for (MultipartFile mf : files) {

                    log.info("mf : " + mf);

                    String orgFileName = mf.getOriginalFilename();      // 파일의 원본 명
                    String fileSize = String.valueOf(mf.getSize());     // 파일 크기
                    String ext = orgFileName.substring(orgFileName.lastIndexOf(".") + 1,    // 확장자
                            orgFileName.length()).toLowerCase();

                    // 이미지 파일만 실행되도록 함
                    if (ext.equals("jpeg") || ext.equals("jpg") || ext.equals("gif") || ext.equals("png")) {


                        log.info("userId : " + userId);
                        log.info("orgFileName : " + orgFileName);
                        log.info("fileSize : " + fileSize);
                        log.info("ext : " + ext);
                        log.info("saveFilePath : " + saveFilePath);

                        FileDTO fileDTO = new FileDTO();
                        fileDTO.setOrgFileName(orgFileName);
                        fileDTO.setFilePath(saveFilePath);
                        fileDTO.setFileSize(fileSize);
                        fileDTO.setUserId(userId);


                        FileDTO fDTO = s3Service.uploadFile(mf, ext);
                        fileDTO.setFileUrl(fDTO.getFileUrl());
                        fileDTO.setFileName(fDTO.getFileName());

                        log.info("sageFileUrl : " + fDTO.getFileUrl());

                        fileService.insertFile(fileDTO);

                        fileDTO = null;

                    }
                }
            }


        } catch (Exception e) {
            msg = "수정에 실패하였습니다. 다시 시도해주세요.";
            log.info(e.toString());
            e.printStackTrace();

        } finally {

            rDTO = new MsgDTO();
            rDTO.setMsg(msg);
            rDTO.setResult(result);

            log.info(this.getClass().getName() + " 마이페이지 수정 종료!");
        }

        return rDTO;
    }

    @ResponseBody
    @PostMapping(value = "/deleteUserInfo")
    public MsgDTO profileDelete(HttpServletRequest request, HttpSession session) throws Exception {

        log.info(this.getClass().getName() + ".deleteUserInfo Start!");

        String msg = ""; // 메시지 내용
        MsgDTO rDTO = null; // 결과 메시지 구조

        try {

            String userId = CmmUtil.nvl((String) session.getAttribute("SS_USER_ID"));

            log.info("SS_USER_ID : " + userId);

            UserInfoDTO pDTO = new UserInfoDTO();
            pDTO.setUserId(userId);

            // 회원정보 삭제하기 메서드 호출
            profileService.deleteUserInfo(pDTO);

            msg = "탈퇴되었습니다.";

        } catch (Exception e) {

            msg = "실패하였습니다. : " + e.getMessage();
            log.info(e.toString());
            e.printStackTrace();

        } finally {

            rDTO = new MsgDTO();
            rDTO.setMsg(msg);

            // 세션에 있는 유저아이디 삭제하기!
            session.removeAttribute("SS_USER_ID");

            log.info("세션 삭제 후 session.getAttribute(\"SS_USER_ID\") : " + session.getAttribute("SS_USER_ID"));

            log.info(this.getClass().getName() + ".deleteUserInfo End!");

        }

        return rDTO;
    }
}