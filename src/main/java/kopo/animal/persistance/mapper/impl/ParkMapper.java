package kopo.animal.persistance.mapper.impl;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.geojson.Point;
import com.mongodb.client.model.geojson.Position;
import kopo.animal.dto.ParkDTO;
import kopo.animal.persistance.mapper.AbstractMongoDBComon;
import kopo.animal.persistance.mapper.IParkMapper;
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
public class ParkMapper extends AbstractMongoDBComon implements IParkMapper {

    private final MongoTemplate mongodb;


    @Override
    public List<ParkDTO> getParkInfo(String colNm, int page, int itemsPerPage, ParkDTO pDTO) throws Exception {

        log.info(this.getClass().getName() + ".mapper 공원정보 가져오기 시작!");

        // 조회 결과를 전달하기 위한 객체 생성하기
        List<ParkDTO> rList = new LinkedList<>();

        MongoCollection<Document> col = mongodb.getCollection(colNm);

        // 페이지네이션을 위한 파라미터 설정
        int skip = (page - 1) * itemsPerPage;
        int limit = itemsPerPage;

        Document projection = new Document();

        projection.append("wlkCoursFlagNm", "$WLK_COURS_FLAG_NM");
        projection.append("coursDc", "$COURS_DC");
        projection.append("signguNm", "$SIGNGU_NM");
        projection.append("coursLtCn", "$COURS_LT_CN");
        projection.append("lnmAddr", "$LNM_ADDR");
        projection.append("coursSpotLa", "$COURS_SPOT_LA");
        projection.append("coursSpotLo", "$COURS_SPOT_LO");
        projection.append("_id", 0);


//
//        // MongoDB의 find 명령어를 통해 조회할 경우 사용함
//        // 조회하는 데이터의 양이 적은 경우, find를 사용하고, 데이터 양이 많은 경우 무조건 Aggregate 사용한다.
        Double spotLa = Optional.ofNullable(pDTO.coursSpotLa())
                .map(Double::parseDouble)
                .orElse(null);

        Double spotLo = Optional.ofNullable(pDTO.coursSpotLo())
                .map(Double::parseDouble)
                .orElse(null);

        log.info("spotLa : " + spotLa);
        log.info("spotLo : " + spotLo);

        Point refPoint = new Point(new Position(spotLa, spotLo));
        Bson geoFilter = Filters.near("location", refPoint, pDTO.range(), 0.0);

        FindIterable<Document> rs = col.find(geoFilter).projection(projection);

        for (Document doc : rs) {
            String wlkCoursFlagNm = CmmUtil.nvl(doc.getString("wlkCoursFlagNm"));
            String coursDc = CmmUtil.nvl(doc.getString("coursDc"));
            String signguNm = CmmUtil.nvl(doc.getString("signguNm"));
            String coursLtCn = CmmUtil.nvl(doc.getString("coursLtCn"));
            String lnmAddr = CmmUtil.nvl(doc.getString("lnmAddr"));
            String coursSpotLa = CmmUtil.nvl(doc.getString("coursSpotLa"));
            String coursSpotLo = CmmUtil.nvl(doc.getString("coursSpotLo"));

            log.info("wlkCoursFlagNm : " + wlkCoursFlagNm);
            log.info("coursDc : " + coursDc);
            log.info("signguNm : " + signguNm);
            log.info("coursLtCn : " + coursLtCn);
            log.info("lnmAddr : " + lnmAddr);
            log.info("coursSpotLa : " + coursSpotLa);
            log.info("coursSpotLo : " + coursSpotLo);

            ParkDTO rDTO = ParkDTO.builder()
                    .wlkCoursFlagNm(wlkCoursFlagNm)
                    .coursDc(coursDc)
                    .signguNm(signguNm)
                    .coursLtCn(coursLtCn)
                    .lnmAddr(lnmAddr)
                    .coursSpotLa(coursSpotLa)
                    .coursSpotLo(coursSpotLo).build();

            // 레코드 결과를 List에 저장하기
            rList.add(rDTO);

        }


        log.info(this.getClass().getName() + ".mapper 공원 정보 가져오기 종료!");

        return rList;
    }
}
