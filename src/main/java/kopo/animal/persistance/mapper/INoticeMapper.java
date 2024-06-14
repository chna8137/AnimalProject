package kopo.animal.persistance.mapper;

import kopo.animal.dto.NoticeDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface INoticeMapper {

    // 게시판 리스트
    List<NoticeDTO> getNoticeList() throws Exception;

    List<NoticeDTO> getPagedNoticeList(@Param("startRow") int startRow, @Param("endRow") int endRow);

    int getTotalCount() throws Exception;

    // 게시판 글등록
    void insertNoticeInfo(NoticeDTO pDTO) throws Exception;

    // 게시판 상세보기
    NoticeDTO getNoticeInfo(NoticeDTO pDTO) throws Exception;

    // 게시판 조회수 업데이트
    void updateNoticeReadCnt(NoticeDTO pDTO) throws Exception;

    // 게시판 글 수정
    void updateNoticeInfo(NoticeDTO pDTO) throws Exception;

    // 게시판 글 수정
    void deleteNoticeInfo(NoticeDTO pDTO) throws Exception;
}
