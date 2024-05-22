package kopo.animal.persistance.mapper;

import kopo.animal.dto.AbandonedDTO;
import kopo.animal.dto.NoticeDTO;

import java.util.List;

public interface IAbandonedMapper {

    /**
     * 유기동물 리스트 저장하기
     *
     * @param pList 저장될 정보
     * @param colNm 저장할 컬렉션 이름
     * @return 저장 결과
     */
    int insertAbandoned(List<AbandonedDTO> pList, String colNm) throws Exception;


    /**
     * 수집된 유기동물 리스트 가져오기
     *
     * @param colNm 조회할 컬렉션 이름
     * @return 노래 리스트
     */
    List<AbandonedDTO> getAbandonedList(String colNm) throws Exception;


    // 유기동물 상세정보
    // 2024.05.21 ( 파라미터 값 변경 )
    AbandonedDTO getAbandoned(String colNm, String idntfyNo) throws Exception;

}
