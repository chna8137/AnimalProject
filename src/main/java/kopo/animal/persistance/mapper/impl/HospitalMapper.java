package kopo.animal.persistance.mapper.impl;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.geojson.Point;
import com.mongodb.client.model.geojson.Position;
import kopo.animal.dto.HospitalDTO;
import kopo.animal.persistance.mapper.AbstractMongoDBComon;
import kopo.animal.persistance.mapper.IHospitalMapper;
import kopo.animal.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class HospitalMapper extends AbstractMongoDBComon implements IHospitalMapper {

    private final MongoTemplate mongodb;

    @Override
    public List<HospitalDTO> getHospitalInfo(String colNm, int page, int itemsPerPage, HospitalDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".mapper 동물병원 정보 가져오기 시작!");

        // 조회 결과를 전달하기 위한 객체 생성하기
        List<HospitalDTO> rList = new LinkedList<>();

        MongoCollection<Document> col = mongodb.getCollection(colNm);

        // 페이지네이션을 위한 파라미터 설정
        int skip = (page - 1) * itemsPerPage;
        int limit = itemsPerPage;

        Document projection = new Document();

        projection.append("fcltyNm", "$FCLTY_NM");
        projection.append("rdnmadrNm", "$RDNMADR_NM");
        projection.append("fcltyLa", "$FCLTY_LA");
        projection.append("fcltyLo", "$FCLTY_LO");
        projection.append("_id", 0);

        // MongoDB의 find 명령어를 통해 조회할 경우 사용함
        // 조회하는 데이터의 양이 적은 경우. find를 사용하고, 데이터 양이 많은 경우 무조건 Aggregate 사용한다.
        Double fLa = Optional.ofNullable(pDTO.fcltyLa())
                .map(Double::parseDouble)
                .orElse(null);

        Double fLo = Optional.ofNullable(pDTO.fcltyLo())
                .map(Double::parseDouble)
                .orElse(null);

        log.info("fLa : " + fLa);
        log.info("fLo : " + fLo);

        Point refPoint = new Point(new Position(fLa, fLo));
        Bson geoFilter = Filters.near("location", refPoint, pDTO.range(), 0.0);

        FindIterable<Document> rs = col.find(geoFilter).projection(projection);

        for (Document doc : rs) {
            String fcltyNm = CmmUtil.nvl(doc.getString("fcltyNm"));
            String rdnmadrNm = CmmUtil.nvl(doc.getString("rdnmadrNm"));
            String fcltyLa = CmmUtil.nvl(doc.getString("fcltyLa"));
            String fcltyLo = CmmUtil.nvl(doc.getString("fcltyLo"));

            log.info("fcltyNm : " + fcltyNm);
            log.info("rdnmadrNm : " + rdnmadrNm);
            log.info("fcltyLa : " + fcltyLa);
            log.info("fcltyLo : " + fcltyLo);

           HospitalDTO rDTO = HospitalDTO.builder()
                   .fcltyNm(fcltyNm)
                   .rdnmadrNm(rdnmadrNm)
                   .fcltyLa(fcltyLa)
                   .fcltyLo(fcltyLo)
                   .build();

           // 레코드 결과를 List에 저장하기
            rList.add(rDTO);

        }

        log.info(this.getClass().getName() + "..mapper 동물병원 정보 가져오기 종료!");

        return rList;
    }
}
