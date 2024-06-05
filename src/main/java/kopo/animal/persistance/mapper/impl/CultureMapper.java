package kopo.animal.persistance.mapper.impl;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.geojson.Point;
import com.mongodb.client.model.geojson.Position;
import kopo.animal.dto.CultureDTO;
import kopo.animal.persistance.mapper.AbstractMongoDBComon;
import kopo.animal.persistance.mapper.ICultureMapper;
import kopo.animal.util.CmmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CultureMapper extends AbstractMongoDBComon implements ICultureMapper {

    private final MongoTemplate mongodb;


    @Override
    public List<CultureDTO> getCultureInfo(String colNm, int page, int itemsPerPage, CultureDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".mapper 문화시설 정보 가져오기 시작!");

        // 조회 결과를 전달하기 위한 객체 생성하기
        List<CultureDTO> rList = new LinkedList<>();

        MongoCollection<Document> col = mongodb.getCollection(colNm);

        // 페이지 네이션을 위한 파라미터 설정
        int skip = (page - 1) * itemsPerPage;
        int limit = itemsPerPage;

        Document projection = new Document();

        projection.append("fcltyNm", "$FCLTY_NM");
        projection.append("ctgryThreeNm", "$CTGRY_THREE_NM");
        projection.append("lcLa", "$LC_LA");
        projection.append("lcLo", "$LC_LO");
        projection.append("zipNo", "$ZIP_NO");
        projection.append("rdnmadrNm", "$RDNMADR_NM");
        projection.append("_id", 0);

        // MongoDB의 find 명령어를 통해 조회할 경우 사용함
        // 조회하는 데이터의 양이 적은 경우, find를 사용하고, 데이터 양이 많은 경우 무조건 Aggregate 사용
        Double la = Optional.ofNullable(pDTO.lcLa())
                .map(Double::parseDouble)
                .orElse(null);

        Double lo = Optional.ofNullable(pDTO.lcLo())
                .map(Double::parseDouble)
                .orElse(null);

        // pDTO.range() 값이 null일 경우 기본 값 설정
        Double range = Optional.ofNullable(pDTO.range()).orElse(500.0); // 기본 값을 500 미터로 설정

        log.info("la : " + la);
        log.info("lo : " + lo);
        log.info("range : " + range);

        if (la != null && lo != null) {
            Point refPoint = new Point(new Position(la, lo)); // Note the order: (longitude, latitude)
            Bson geoFilter = Filters.near("location", refPoint, range, 0.0);

            // 페이징을 적용하여 쿼리 실행
            FindIterable<Document> rs = col.find(geoFilter)
                    .projection(projection)
                    .skip(skip)
                    .limit(limit);

            for (Document doc : rs) {
                String fcltyNm = CmmUtil.nvl(doc.getString("fcltyNm"));
                String ctgryThreeNm = CmmUtil.nvl(doc.getString("ctgryThreeNm"));
                String lcLa = CmmUtil.nvl(doc.getString("lcLa"));
                String lcLo = CmmUtil.nvl(doc.getString("lcLo"));
                String zipNo = CmmUtil.nvl(doc.getString("zipNo"));
                String rdnmadrNm = CmmUtil.nvl(doc.getString("rdnmadrNm"));

                log.info("fcltyNm : " + fcltyNm);
                log.info("ctgryThreeNm : " + ctgryThreeNm);
                log.info("lcLa : " + lcLa);
                log.info("lcLo : " + lcLo);
                log.info("zipNo : " + zipNo);
                log.info("rdnmadrNm : " + rdnmadrNm);

                CultureDTO rDTO = CultureDTO.builder()
                        .fcltyNm(fcltyNm)
                        .ctgryThreeNm(ctgryThreeNm)
                        .lcLa(lcLa)
                        .lcLo(lcLo)
                        .zipNo(zipNo)
                        .rdnmadrNm(rdnmadrNm).build();

                // 레코드 결과를 List에 저장하기
                rList.add(rDTO);
            }
        }

        log.info(this.getClass().getName() + ".mapper 문화시설 정보 가져오기 종료!");

        return rList;
    }
}
