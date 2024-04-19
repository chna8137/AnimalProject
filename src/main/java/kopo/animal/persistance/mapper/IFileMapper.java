package kopo.animal.persistance.mapper;

import kopo.animal.dto.FileDTO;
import kopo.animal.dto.UserInfoDTO;
import org.apache.catalina.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IFileMapper {

    /* 경로 가져오기 */
    List<FileDTO> getFile(UserInfoDTO pDTO) throws Exception;

    /* 파일 저장 */
    void insertFile(FileDTO pDTO) throws Exception;

    /* 이미지 수정*/
    void updateFile(UserInfoDTO pDTO) throws Exception;

    /* 이미지 삭제 */
    void deleteFile(UserInfoDTO pDTO) throws Exception;

}
