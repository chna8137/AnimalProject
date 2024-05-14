package kopo.animal.persistance.mapper;

import kopo.animal.dto.CalendarDTO;
import kopo.animal.dto.NoticeDTO;
import kopo.animal.dto.UserInfoDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface INaverMapper {

    // DB에서 네이버로 가입한 회원의 정보를 가져오기
    UserInfoDTO getUserInfoById(UserInfoDTO pDTO) throws Exception;

}
