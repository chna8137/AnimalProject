package kopo.animal.service;

import kopo.animal.dto.MongoDTO;

public interface IMongoService {

    /**
     * 간단한 데이터 저장하기
     */
    int mongoTest(MongoDTO pDTO) throws Exception;
}
