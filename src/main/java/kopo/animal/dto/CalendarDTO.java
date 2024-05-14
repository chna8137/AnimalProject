package kopo.animal.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CalendarDTO {

    private String uid; // 아이디값

    private String dtStart; // 일정 시작 시간

    private String dtEnd; // 일정 종료 시간

    private String summary; // 일정 제목

    private String description; // 일정의 상세내용

    private String location; // 장소 정보

    private String organizer; // 약속일정인 경우 일정의 마스터 정보

    private String attendee; // 약속 일정인 경우 참석자 정보

    private String latModified; // 일정 최종 수정 시각

}
