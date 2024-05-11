package kopo.animal.persistance.mapper.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import kopo.animal.dto.AbandonedDTO;
import kopo.animal.persistance.mapper.AbstractMongoDBComon;
import kopo.animal.persistance.mapper.IAbandonedMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AbandonedMapper extends AbstractMongoDBComon implements IAbandonedMapper {

    private final MongoTemplate mongodb;

    @Override
    public int insertAbandoned(List<AbandonedDTO> pList, String colNm) throws Exception {

        log.info(this.getClass().getName() + ".mapper 유기동물 저장 시작!");

        int res = 0;

        if (pList == null) {
            pList = new LinkedList<>();
        }

        // 데이터를 저장할 컬렉션 생성
        super.createCollection(mongodb, colNm, "collectTime");

        // 저장할 컬렉션 객체 생성
        MongoCollection<Document> col = mongodb.getCollection(colNm);

        for (AbandonedDTO pDTO : pList) {
            // 레코드 한 개씩 저장하기
            col.insertOne(new Document(new ObjectMapper().convertValue(pDTO, Map.class)));
        }

        res = 1;

        log.info(this.getClass().getName() + ".mapper 유기동물 저장 종료!");

        return res;
    }

    @Override
    public List<AbandonedDTO> getAbandonedList(String colNm) throws Exception {
        return null;
    }
}
