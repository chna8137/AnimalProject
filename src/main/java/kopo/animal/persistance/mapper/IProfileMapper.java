package kopo.animal.persistance.mapper;

import kopo.animal.dto.UserInfoDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IProfileMapper {

    UserInfoDTO getProfile(UserInfoDTO pDTO) throws Exception;

    void updateProfile(UserInfoDTO pDTO) throws Exception;

    void deleteUserInfo(UserInfoDTO pDTO) throws Exception;
}
