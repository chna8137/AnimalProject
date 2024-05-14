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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
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
    public CalendarDTO getCalendarInfo(TokenDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".service 네이버 캘린더 조회하기 시작!");

//        String token = "YOUR_ACCESS_TOKEN";
//        String header = "Bearer " + token; // Bearer 다음에 공백 추가

        // HttpHeader 오브젝트 생성
        HttpHeaders header = new HttpHeaders();
        header.add("Authorization", "Bearer " + pDTO.getAccess_token());

        log.info("headers : " + header);

        try {
            String apiURL = "https://openapi.naver.com/calendar/createSchedule.json";
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", header.getFirst("Authorization"));
            String calSum = URLEncoder.encode("[제목] 캘린더API로 추가한 일정", "UTF-8");
            String calDes = URLEncoder.encode("[상세] 회의 합니다", "UTF-8");
            String calLoc = URLEncoder.encode("[장소] 그린팩토리", "UTF-8");
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
                    "UID:" + uid + "\n" +                          // 일정 고유 아이디
                    "DTSTART;TZID=Asia/Seoul:20161116T170000\n" +  // 시작 일시
                    "DTEND;TZID=Asia/Seoul:20161116T173000\n" +    // 종료 일시
                    "SUMMARY:" + calSum + " \n" +                    // 일정 제목
                    "DESCRIPTION:" + calDes + " \n" +                // 일정 상세 내용
                    "LOCATION:" + calLoc + " \n" +                   // 장소
                    "RRULE:FREQ=YEARLY;BYDAY=FR;INTERVAL=1;UNTIL=20201231\n" +  // 일정 반복시 설정
                    "ORGANIZER;CN=관리자:mailto:admin@sample.com\n" + // 일정 만든 사람
                    "ATTENDEE;ROLE=REQ-PARTICIPANT;PARTSTAT=NEEDS-ACTION;CN=admin:mailto:user1@sample.com\n" + // 참석자
                    "CREATED:20161116T160000Z\n" +         // 일정 생성시각
                    "LAST-MODIFIED:20161116T160000Z\n" +   // 일정 수정시각
                    "DTSTAMP:20161116T160000Z\n" +         // 일정 타임스탬프
                    "END:VEVENT\n" +
                    "END:VCALENDAR";
            String postParams = "calendarId=defaultCalendarId&scheduleIcalString=" + scheduleIcalString;
            System.out.println(postParams);
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(postParams);
            wr.flush();
            wr.close();
            int responseCode = con.getResponseCode();
            BufferedReader br;
            if (responseCode == 200) { // 정상 호출
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {  // 에러 발생
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();
            System.out.println(response.toString());
        } catch (Exception e) {
            System.out.println(e);
        }

        log.info(this.getClass().getName() + ".service 네이버 캘린더 조회하기 종료!");

        return null;
    }

    @Override
    public void insertCalendar(CalendarDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".service 네이버 캘린더 일정 등록하기 시작!");


        log.info(this.getClass().getName() + ".service 네이버 캘린더 일정 등록하기 종료!");

    }

    @Override
    public void updateCalendar(CalendarDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".service 네이버 캘린더 일정 수정하기 시작!");

        log.info(this.getClass().getName() + ".service 네이버 캘린더 일정 수정하기 종료!");

    }

    @Override
    public void deleteCalendar(CalendarDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".service 네이버 캘린더 일정 삭제하기 시작!");

        log.info(this.getClass().getName() + ".service 네이버 캘린더 일정 삭제하기 종료!");

    }
}
