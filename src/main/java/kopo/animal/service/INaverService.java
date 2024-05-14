package kopo.animal.service;

import kopo.animal.dto.CalendarDTO;
import kopo.animal.dto.NaverDTO;
import kopo.animal.dto.TokenDTO;
import kopo.animal.dto.UserInfoDTO;

public interface INaverService {

    /* 토큰 가져오기 */
    TokenDTO getAccessToken(String code) throws Exception;

    /* 네이버에서 정보 가져오기 */
    NaverDTO getNaverUserInfo(TokenDTO pDTO) throws Exception;

    // DB에서 네이버로 가입한 회원의 정보를 가져오기
    UserInfoDTO getUserInfoById(UserInfoDTO pDTO) throws Exception;

    // 캘린더 일정 상세보기
    CalendarDTO getCalendarInfo(TokenDTO pDTO) throws Exception;

    // 캘린더 일정등록
    void insertCalendar(CalendarDTO pDTO) throws Exception;

    // 캘린더 일정 수정
    void updateCalendar(CalendarDTO pDTO) throws Exception;

    // 캘린더 일정 삭제
    void deleteCalendar(CalendarDTO pDTO) throws Exception;





}
