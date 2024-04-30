package kopo.animal.service;

import kopo.animal.dto.UserInfoDTO;

public interface IUserInfoService {

    // 아이디 중복 체크
    UserInfoDTO getUserIdExists(UserInfoDTO pDTO) throws Exception;

    //닉네임 중복체크 하기 (DB 조회)
    UserInfoDTO getNicknameExists(UserInfoDTO pDTO) throws Exception;

    // 이메일 중복 체크 및 인증 값
    UserInfoDTO getEmailExists(UserInfoDTO pDTO) throws Exception;

    // 회원 가입하기(회원정보 등록하기)
    int insertUserInfo(UserInfoDTO pDTO) throws Exception;

    // 로그인을 위해 아이디와 비밀번호가 일치하는지 확인하기
    UserInfoDTO getLogin(UserInfoDTO pDTO) throws Exception;

    /**
     * 아이디, 비밀번호 찾기에 활용
     * 1. 이름과 이메일이 맞다면, 아이디 알려주기
     * 2. 아이디, 이름과 이메일이 맞다면, 비밀번호 재설정하기
     * */
    UserInfoDTO searchUserIdOrPasswordProc(UserInfoDTO pDTO) throws Exception;

    // 비밀번호 재설정
    int newPasswordProc(UserInfoDTO pDTO) throws Exception;

    /* 이름 가져오기 */
    UserInfoDTO getNickname(UserInfoDTO pDTO) throws Exception;
}
