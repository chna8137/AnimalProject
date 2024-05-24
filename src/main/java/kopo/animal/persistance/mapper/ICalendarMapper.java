package kopo.animal.persistance.mapper;

import kopo.animal.dto.CalendarDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ICalendarMapper {

    /* 일정 추가 */
    int insertCalendar(CalendarDTO pDTO) throws Exception;

    /* 일정 글 리스트 */
    List<CalendarDTO> getCalendarList(@Param("userId") String userId, @Param("start") String start, @Param("end") String end) throws Exception;


    /* 일정 조회 */
    CalendarDTO calendarInfo(CalendarDTO pDTO) throws Exception;

    /* 오늘 일정 리스트 */
    List<CalendarDTO> getTodayCalendarList(CalendarDTO pDTO) throws Exception;

    /* 일정 수정 */
    void updateCalendar(CalendarDTO pDTO) throws Exception;

    /* 일정 삭제 */
    void deleteCalendar(CalendarDTO pDTO) throws Exception;

    /* 일정 확인 */
    List<CalendarDTO> checkCalendar(CalendarDTO pDTO) throws Exception;
}
