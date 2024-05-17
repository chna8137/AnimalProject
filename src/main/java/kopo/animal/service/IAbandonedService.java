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
}
