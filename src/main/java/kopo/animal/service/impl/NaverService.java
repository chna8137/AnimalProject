package kopo.animal.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kopo.animal.dto.CalendarDTO;
import kopo.animal.dto.NaverDTO;
import kopo.animal.dto.TokenDTO;
import kopo.animal.dto.UserInfoDTO;
import kopo.animal.persistance.mapper.IUserInfoMapper;
import kopo.animal.service.INaverService;
import kopo.animal.service.IUserInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Slf4j
@RequiredArgsConstructor
@Service
public class NaverService implements INaverService {

    private final IUserInfoMapper userInfoMapper;

    @Value("${naver.client_id}")
    private String naverClientId;
    @Value("${naver.client_secret}")
    private String naverClientSecret;
    @Value("${naver.redirect_uri}")
    private String naverRedirectUri;

    @Override
    public TokenDTO getAccessToken(String code) throws Exception {


        log.info(".service 네이버 토큰 가져오기 실행");

        /*
         * POST 방식으로 key=value 형식으로 데이터 요청(네이버 쪽으로)
         * http 요청을 편하게 할수 있게하는 Retrofit2, OkHttp, RestTemplate 라이브러리가 있다
         */
        RestTemplate rt = new RestTemplate();

        // HttpHeader 오브젝트 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HttpBody 오브젝트 생성
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", naverClientId);
        params.add("client_secret", naverClientSecret);
        params.add("redirect_uri", naverRedirectUri);
        params.add("code", code);

        log.info("code : " + code);

        // HttpHeader와 HttpBody를 하나의 오브젝트에 담기
        HttpEntity<MultiValueMap<String, String>> naverTokenRequest =
                new HttpEntity<>(params, headers);

        // Http 요청하기 - POST 방식으로 - 그리고 response 변수의 응답 받음.
        ResponseEntity<String> response = rt.exchange(
                "https://nid.naver.com/oauth2.0/token",
                HttpMethod.POST,
                naverTokenRequest,
                String.class
        );

        // 객체에 담아볼 것이다. Gson, Json Simple, ObjectMapper 라이브러리가 있다.
        ObjectMapper objectMapper = new ObjectMapper();

        TokenDTO tokenDTO = null;

        try {
            tokenDTO = objectMapper.readValue(response.getBody(), TokenDTO.class);

        } catch (JsonMappingException e) {
            log.info(e.toString());
            e.printStackTrace();    // Exception 발생 이유와 위치는 어디에서 발생했는지 전체적인 단계 출력

        } catch (JsonProcessingException e) {
            log.info(e.toString());
            e.printStackTrace();    // Exception 발생 이유와 위치는 어디에서 발생했는지 전체적인 단계 출력

        }

        log.info(".service 네이버 토큰 가져오기 종료");

        return tokenDTO;
    }

    /* 네이버에서 정보 가져오기 */
    @Override
    public NaverDTO getNaverUserInfo(TokenDTO pDTO) throws Exception {

        log.info(".service 네이버에서 유저 정보 가져오기 실행");

        log.info("TokenDTO pDTO : " + pDTO);

        // http 요청을 편하게 할 수 있다. Retrofit2, OkHttp, RestTemplate 라이브러리가 있다.
        RestTemplate rt = new RestTemplate();

        // HttpHeader 오브젝트 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + pDTO.getAccess_token());
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        log.info("headers : " + headers);

        // HttpHeader와 HttpBody를 하나의 오브젝트에 담기
        HttpEntity<MultiValueMap<String, String>> naverProfileRequest = new HttpEntity<>(headers);

        log.info("naverProfileRequest : " + naverProfileRequest);

        // Http 요청하기 - POST방식으로 - 그리고 response 변수의 응답 받음.
        ResponseEntity<String> response2 = rt.exchange(
                "https://openapi.naver.com/v1/nid/me",
                HttpMethod.POST,
                naverProfileRequest,
                String.class
        );

        log.info("response2 : " + response2);

        ObjectMapper objectMapper = new ObjectMapper();

        NaverDTO naverDTO = null;

        try {
            naverDTO = objectMapper.readValue(response2.getBody(), NaverDTO.class);

            log.info("naverDTO : " + naverDTO);

            /*  실패 사유 확인용 로그  */
        } catch (JsonMappingException e) {
            log.info(e.toString());
            e.printStackTrace();    // Exception 발생 이유와 위치는 어디에서 발생했는지 전체적인 단계 출력

        } catch (JsonProcessingException e) {
            log.info(e.toString());
            e.printStackTrace();    // Exception 발생 이유와 위치는 어디에서 발생했는지 전체적인 단계 출력

        }

        return naverDTO;
    }

    @Override
    public UserInfoDTO getUserInfoById(UserInfoDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".getUserInfoById 시작!");

        UserInfoDTO rDTO = userInfoMapper.getUserInfoById(pDTO);

        log.info(this.getClass().getName() + ".getUserInfoById 끝!");

        return rDTO;
    }

    @Override
    public String createNaverCalendarEvent(TokenDTO pDTO, CalendarDTO cDTO) throws Exception {
        log.info(".service 네이버 캘린더 일정 생성 실행");

        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + pDTO.getAccess_token());
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        String calSum = URLEncoder.encode(cDTO.getResponse().getSummary(), StandardCharsets.UTF_8.toString());
        String calDes = URLEncoder.encode(cDTO.getResponse().getDescription(), StandardCharsets.UTF_8.toString());
        String calLoc = URLEncoder.encode(cDTO.getResponse().getLocation(), StandardCharsets.UTF_8.toString());
        String uid = UUID.randomUUID().toString();

        String scheduleIcalString = "BEGIN:VCALENDAR\n" +
                "VERSION:2.0\n" +
                "PRODID:Naver Calendar\n" +
                "CALSCALE:GREGORIAN\n" +
                "BEGIN:VTIMEZONE\n" +
                "TZID:Asia/Seoul\n" +
                "BEGIN:STANDARD\n" +
                "DTSTART:19700101T000000\n" +
                "TZNAME:GMT%2B09:00\n" +
                "TZOFFSETFROM:%2B0900\n" +
                "TZOFFSETTO:%2B0900\n" +
                "END:STANDARD\n" +
                "END:VTIMEZONE\n" +
                "BEGIN:VEVENT\n" +
                "SEQUENCE:0\n" +
                "CLASS:PUBLIC\n" +
                "TRANSP:OPAQUE\n" +
                "UID:" + uid + "\n" +
                "DTSTART;TZID=Asia/Seoul:" + cDTO.getResponse().getDtStart() + "\n" +
                "DTEND;TZID=Asia/Seoul:" + cDTO.getResponse().getDtEnd() + "\n" +
                "SUMMARY:" + calSum + "\n" +
                "DESCRIPTION:" + calDes + "\n" +
                "LOCATION:" + calLoc + "\n" +
                "RRULE:FREQ=YEARLY;BYDAY=FR;INTERVAL=1;UNTIL=20231231\n" +
                "ORGANIZER;CN=관리자:mailto:admin@sample.com\n" +
                "ATTENDEE;ROLE=REQ-PARTICIPANT;PARTSTAT=NEEDS-ACTION;CN=admin:mailto:user1@sample.com\n" +
                "CREATED:20230515T160000Z\n" +
                "LAST-MODIFIED:20230515T160000Z\n" +
                "DTSTAMP:20230515T160000Z\n" +
                "END:VEVENT\n" +
                "END:VCALENDAR";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("calendarId", "defaultCalendarId");
        params.add("scheduleIcalString", scheduleIcalString);

        HttpEntity<MultiValueMap<String, String>> naverCalendarRequest = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = rt.exchange(
                "https://openapi.naver.com/calendar/createSchedule.json",
                HttpMethod.POST,
                naverCalendarRequest,
                String.class
        );

        log.info(".service 네이버 캘린더 일정 추가 종료");

        return response.getBody();
    }

}
