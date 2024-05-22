package kopo.animal.service;

import kopo.animal.dto.AbandonedDTO;

import java.util.List;

public interface IAbandonedService {

    /**
     * 유기동물 리스트 저장하기
     * */
    int collectAbandoned() throws Exception;

    /**
     * 유기동물 리스트 가져오기
     * */
    List<AbandonedDTO> getAbandonedList() throws Exception;

    // 유기동물 상세정보
    // 2024.05.21 ( 파라미터 값 변경 )
    AbandonedDTO getAbandoned(String colNm, String idntfyNo) throws Exception;


}
